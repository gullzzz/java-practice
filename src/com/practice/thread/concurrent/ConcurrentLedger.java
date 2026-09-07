package com.practice.thread.concurrent;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 【交易所并发账本·初阶】
 * 10 个收银员同时处理交易，每个收银员给 "gold" 记 1000 笔，期望总数 = 10000。
 *
 * 你的任务：
 *   1. TODO ① 用 ConcurrentHashMap 的原子方法实现 recordTrade（别自己加 synchronized！）
 *   2. TODO ② 实现 getCount
 *   3. 跑测试1，应该看到 ✅ 账目正确
 *   4. TODO ③ 写一个用普通 HashMap 的对照版，观察并发丢数据
 */
public class ConcurrentLedger {

    private final ConcurrentHashMap<String, Integer> tradeCount = new ConcurrentHashMap<>();

    // TODO ①：并发安全的计数——某商品每交易一次，次数 +1。
    // 提示：普通写法 tradeCount.put(key, tradeCount.get(key) + 1) 是"读-改-写"三步，
    //       非原子，会丢更新。去 README「四、核心方法速查表」找一个能原子累加的方法。
    public void recordTrade(String product) {

        // TODO: 你的实现
        tradeCount.merge(product,1,Integer::sum);
    }

    // TODO ②：查询某商品的交易次数，不存在则返回 0
    public int getCount(String product) {
        return tradeCount.getOrDefault(product,0);
        // TODO: 你的实现
    }

    public static void main(String[] args) throws InterruptedException {
        ConcurrentLedger ledger = new ConcurrentLedger();
        runTest("ConcurrentHashMap 版", ledger, 10, 1000, 10000);

        // TODO ③：实现 compareWithHashMap 后，取消下面这行注释，看普通 HashMap 怎么丢数据
         compareWithHashMap();
    }

    private static void runTest(String name, ConcurrentLedger ledger, int threads,
                                int perThread, int expected) throws InterruptedException {
        Thread[] ts = new Thread[threads];
        for (int i = 0; i < threads; i++) {
            ts[i] = new Thread(() -> {
                for (int j = 0; j < perThread; j++) ledger.recordTrade("gold");
            });
        }
        for (Thread t : ts) t.start();
        for (Thread t : ts) t.join();
        int actual = ledger.getCount("gold");
        System.out.println("[" + name + "] 期望 " + expected + "，实际 " + actual +
                (actual == expected ? " ✅ 账目正确" : " ❌ 丢了 " + (expected - actual) + " 笔"));
    }

    // TODO ③：用普通 HashMap 做同样的并发累加（故意用"读-改-写"三步），
    // 跑几次观察：结果是不是经常 < 10000？
    private static void compareWithHashMap() throws InterruptedException {
        Map<String, Integer> map = new HashMap<>();
        map.put("gold",0);
        int threads=10;
        int perThread=1000;
        Thread[] ts = new Thread[threads];
        for (int i = 0; i < threads; i++) {
            ts[i] = new Thread(() -> {
                for (int j = 0; j < perThread; j++) {
                    // 第二步：读-改-写 三步（故意非原子，制造丢数据）
                    int old = map.get("gold");        // 读：拿到旧值
                    map.put("gold", old + 1);         // 写：把旧值+1 放回去
                }
            });
        }
        for (Thread t : ts) t.start();
        for (Thread t : ts) t.join();
        // 第三步：打印 期望 vs 实际
        int expected = threads * perThread;
        int actual = map.get("gold");                 // 读：拿最终值
        System.out.println("[普通 HashMap 版] 期望 " + expected + "，实际 " + actual +
                (actual == expected ? " ✅ 侥幸没丢" : " ❌ 丢了 " + (expected - actual) + " 笔"));


        // TODO: 你的实现
    }
}
