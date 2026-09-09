package com.practice.thread.t07_memory;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 【秒杀日结算终端】—— 魔法交易所周年庆
 *
 * 周年庆当天冒出 3 个并发事故，每个都对应本章一个知识点。
 * 这次不告诉你该用哪个工具——只描述"事故现场"，工具自己去 README 速查表里挑。
 *
 *   事故一：总裁喊"停业"，结算员却像没听见，继续埋头干活
 *   事故二：10 个结算员同时记账，最后账本数字对不上
 *   事故三：财务员的"专用章"被别的结算员盖走了
 *
 * 下面 3 个字段都是"普通版"，正是这三起事故的元凶。你要做的：
 *   ① 诊断每个字段该升级成什么（去 README 速查表找工具）
 *   ② 填完方法体里的 TODO
 *
 * 跑通后，场景二会打印"正确值应是…"让你自己对账。
 */
public class FlashSaleSettlement {

    // ============ 事故一：停业信号 ============
    // TODO ①：这个字段是"停业喊不停"的元凶，升级它。
    private static volatile boolean  closed = false;

    // ============ 事故二：总账 ============
    // TODO ②：这俩字段是"账本对不上"的元凶，升级它们（其中一个要防 21 亿溢出）。
    private static AtomicInteger totalOrders=new AtomicInteger(0);
    private static AtomicLong totalAmount =new AtomicLong(0);

    // ============ 事故三：印章 ============
    // TODO ③：这个字段是"章被盖走"的元凶，升级它，没设置过的默认"普通章"。
    private static ThreadLocal<String>  stamp = ThreadLocal.withInitial(()->"普通章");

    public static void main(String[] args) throws InterruptedException {
        System.out.println("===== 周年庆秒杀日结算 =====");
        scenario1_shutdown();
        scenario2_counter();
        scenario3_stamp();
        System.out.println("===== 结算完成 =====");
    }

    // ============ 事故一：3 个结算员盯"停业"信号 ============
    static void scenario1_shutdown() throws InterruptedException {
        // 【执行流】谁执行什么？分水岭是 new Thread(() -> {...}) 这道墙：
        //   - lambda {} 里面 → 每个"结算员"子线程执行
        //   - lambda 外面（start / sleep / closed=true）→ main 线程执行
        for (int i = 1; i <= 3; i++) {
            final int id = i;
            new Thread(() -> {
                // ↓ 从这里开始是子线程（结算员）自己的代码
                while (!closed) {                  // 每次循环都重新读 closed（volatile 保证可见）
                    System.out.println("干活");
                }
                // 跳出循环 = 看到了停业信号
                System.out.println("结算员" + id + " 看到停业信号，收工！");
            }, "结算员" + id).start();             // start() 后，子线程和 main 并发跑
        }

        // ↓ 以下是 main 线程自己的代码（子线程在旁边并发干活）
        Thread.sleep(2000);                        // main 睡 2 秒，给子线程"启动+干活"的时间窗口
        closed = true;                             // main 喊停（volatile 写，立刻刷回主内存）
        System.out.println("【总裁】停业！所有结算员立刻收工！");
    }

    // 【复盘① · 忙循环】while 里若只剩纯空转（删掉 println），线程会一直霸占 CPU 烧时间片，别的线程还可能被饿着。
    // 真实场景"只等信号不干活"时，循环里该加 sleep 主动让出 CPU。

    // ============ 事故二：10 个结算员各结算 1000 单 ============
    static void scenario2_counter() throws InterruptedException {
        int workers = 10;
        int perWorker = 1000;
        Thread[] ts = new Thread[workers];
        for (int i = 0; i < workers; i++) {
            ts[i] = new Thread(() -> {
                for (int j = 0; j < perWorker; j++) {
                    totalOrders.incrementAndGet();
                    totalAmount.addAndGet(10);
                    // TODO ⑥：原子地累加总笔数（+1）和总金额（每单 10 块）。
                }
            });
            ts[i].start();
        }
        for (Thread t : ts) t.join();
        System.out.println("【事故二】总笔数 = " + totalOrders.get() + "，总金额 = " + totalAmount.get());
        System.out.println("  （正确值应是 " + (workers * perWorker) + " 笔 / " + (workers * perWorker * 10) + " 元）");
    }

    // 【复盘② · LongAdder】AtomicLong 是"一本总账，大家抢一支笔"——线程多了 CAS 失败就自旋，竞争越激烈越空转。
    // LongAdder 靠"分段累加"：内部拆多个 Cell，各累加各的，最后 sum() 汇总。取舍：写多读少用 LongAdder，读写都频繁且要精确值用 AtomicLong。

    // ============ 事故三：财务员盖财务章，普通员用默认章 ============
    static void scenario3_stamp() throws InterruptedException {
        Runnable worker = () -> {
            String name = Thread.currentThread().getName();
            // TODO ⑦：财务结算员换"财务专用章"；各自打印自己的章；用完清理。
            try {
                if(name.equals("财务结算员")){
                stamp.set("财务专用章");
                }
                System.out.println(name + " 的印章 = " + stamp.get());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }finally {
                stamp.remove();
            }
        };


        Thread a = new Thread(worker, "财务结算员");
        Thread b = new Thread(worker, "普通结算员");
        a.start();
        b.start();
        a.join();
        b.join();
    }

    // 【复盘③ · ThreadLocal 泄漏】ThreadLocalMap 里 key（ThreadLocal）是弱引用、value（数据）是强引用。
    // key 可能被 GC 收走，但 value 赖在线程的 Map 里，线程不死它不走 → 线程池线程长期存活就内存泄漏。
    // finally remove() 就是手动把 value 赶走。
}
