package com.practice.thread.sync;

import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 【魔法金库守卫·初阶】
 * 5 个收银员同时往金库存金币，每人存 10000 次，期望总数 = 50000。
 * 但金库的 deposit() 有线程安全问题——并发时会"丢金币"。
 *
 * 你的任务：
 *   1. 先直接运行，观察金币是怎么丢的（❌ 会丢多少）
 *   2. TODO ① 用 synchronized 修复 deposit()
 *   3. TODO ② 用 ReentrantLock 实现 depositByLock()
 *   4. TODO ③ 取消测试2的注释，验证两种方式都能守住金库
 */
public class VaultRaid {

    static class Vault {
        private int goldTotal = 0;
        private final Lock lock = new ReentrantLock();
        Object lock1=new Object();

        // TODO ①：这个方法有线程安全问题——多个收银员同时 deposit 会丢金币。
        // 请用 synchronized 修复它（同步方法 或 同步代码块 任选一种）。
        public void deposit() {
            synchronized (lock1) {
                goldTotal++;
            }
        }

        // TODO ②：再用 ReentrantLock 实现一个等价版本。
        // 铁律：unlock() 必须放 finally 里！
        public void depositByLock() {
            lock.lock();
            try {
                goldTotal++;
            }finally {
                lock.unlock();
            }
            // TODO: 你的实现
        }

        public int getGoldTotal() {
            return goldTotal;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        int tellerCount = 5;
        int perTeller = 10000;
        int expected = tellerCount * perTeller;

        // 测试1：synchronized 版（先跑，你会看到丢金币）
        test("synchronized 版", tellerCount, perTeller, expected, false);

        // 测试2：ReentrantLock 版 —— TODO ③ 实现 depositByLock 后，取消下面这行注释再跑
         test("ReentrantLock 版", tellerCount, perTeller, expected, true);
    }

    private static void test(String name, int tellerCount, int perTeller,
                             int expected, boolean useLock) throws InterruptedException {
        Vault vault = new Vault();
        Thread[] tellers = new Thread[tellerCount];
        for (int i = 0; i < tellerCount; i++) {
            tellers[i] = new Thread(() -> {
                for (int j = 0; j < perTeller; j++) {
                    if (useLock) {
                        vault.depositByLock();
                    } else {
                        vault.deposit();
                    }
                }
            });
        }
        for (Thread t : tellers) t.start();
        for (Thread t : tellers) t.join();

        int actual = vault.getGoldTotal();
        System.out.println("[" + name + "] 期望 " + expected + "，实际 " + actual +
                (actual == expected ? " ✅ 金库无损失" : " ❌ 丢了 " + (expected - actual) + " 枚金币"));
    }
}
