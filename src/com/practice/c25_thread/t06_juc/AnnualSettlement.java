package com.practice.c25_thread.t06_juc;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 【年度结算大管家】—— 魔法交易所年终三件大事
 *
 * 用今天学的三个 JUC 工具，各解决一个真实协作场景：
 *
 *   阶段一 Semaphore     ：结算大厅只有 3 个窗口，8 笔订单排队结算（限流）
 *   阶段二 CountDownLatch：等 8 笔订单全部结算完，主线程出年度总账（一等多）
 *   阶段三 CyclicBarrier ：统计部/核对部/审计部，分 3 轮对账，每轮三部门到齐才进入下一轮（多等多 + 循环复用）
 *
 * 每个 TODO 都对应速查表里的一个核心方法，注释里有提示。
 */
public class AnnualSettlement {

    private static final int ORDER_COUNT = 8;    // 订单总数
    private static final int WINDOW_COUNT = 3;   // 结算窗口数（Semaphore 许可证数）
    private static final int DEPT_COUNT = 3;     // 部门数（CyclicBarrier 参与方数）

    public static void main(String[] args) {
        System.out.println("===== 年度结算开始 =====");
        settleOrders();    // 阶段一 + 二
        reconcile();       // 阶段三
        System.out.println("===== 年度结算完成 =====");
    }

    // ==================== 阶段一&二：Semaphore 限流 + CountDownLatch 汇总 ====================
    static void settleOrders() {
        // 3 张窗口许可证；8 的倒计时门闩；原子累加器（线程安全，下一章展开）
        Semaphore windows = new Semaphore(WINDOW_COUNT);
        CountDownLatch done = new CountDownLatch(ORDER_COUNT);
        AtomicInteger totalAmount = new AtomicInteger(0);

        for (int i = 1; i <= ORDER_COUNT; i++) {
            final int orderId = i;
            final int amount = 100 * orderId;   // 第 i 笔订单金额 100、200、...、800
            new Thread(() -> {
                // TODO ①：用 windows 拿一个结算窗口（拿不到就阻塞）
                //        acquire 会抛 InterruptedException —— 这行放 try 里还是外？

                boolean acquire=false;
                try {
                    // 模拟结算耗时
                    windows.acquire();
                    acquire=true;

                    Thread.sleep(ThreadLocalRandom.current().nextInt(200, 600));
                    totalAmount.addAndGet(amount);
                    System.out.println("订单" +orderId+" 结算完成（金额"+amount+")");

                    // TODO ②：把本订单金额累加进 totalAmount，并打印"订单X 结算完成（金额Y）"
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();   // 恢复中断标记
                } finally {
                    if(acquire){
                        windows.release();
                    }

                    done.countDown();
                    // TODO ③：在 finally 里归还窗口（release）
                    //        为什么必须 finally？—— 如果 sleep 抛异常，许可证会怎样？
                    // TODO ④：在 finally 里调用 done.countDown()
                    //        为什么也必须 finally？—— 如果结算中途异常，主线程会怎样？
                }
            }).start();
        }

        // TODO ⑤：主线程在这里等所有订单结算完（await），然后打印总金额
        try{
            done.await();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("【阶段一&二】" + ORDER_COUNT + " 笔订单全部结算完成，年度总金额 = " + totalAmount.get());
    }

    // ==================== 阶段三：CyclicBarrier 三部门循环对账 ====================
    static void reconcile() {
        // TODO ⑥：创建 CyclicBarrier，等 DEPT_COUNT 个部门到齐；
        //        到齐后执行一个动作：打印"—— 本轮对账完成，各部门进入下一轮 ——"
        //        提示：用带 Runnable 的构造器，那个动作由"最后一个到达"的线程执行
        CyclicBarrier cyclicBarrier=new CyclicBarrier(DEPT_COUNT,()->{
            System.out.println("本轮对账完成，各部门进入下一轮");
        });



        String[] depts = {"统计部", "核对部", "审计部"};
        for (String dept : depts) {
            new Thread(() -> {
                // 循环 3 轮，体现 CyclicBarrier 的"循环复用"——一轮结束自动重置，下一轮继续用
                for (int round = 1; round <= 3; round++) {
                    sleep(dept + " 第" + round + "轮对账");
                    try{
                        cyclicBarrier.await();
                    } catch (BrokenBarrierException e) {
                        Thread.currentThread().interrupt();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    // TODO ⑦：到达栅栏并等待其他部门（await）
                    //        await 返回本线程的到达序号（parties-1 递减到 0）；
                    //        还会抛 InterruptedException 和 BrokenBarrierException，都要接住
                    System.out.println(dept + " 完成第" + round + "轮汇总");
                }
            }, dept).start();
        }
    }

    // 模拟耗时操作：打印 + 随机睡一会儿
    static void sleep(String msg) {
        try {
            System.out.println(msg + "…");
            Thread.sleep(ThreadLocalRandom.current().nextInt(300, 800));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
