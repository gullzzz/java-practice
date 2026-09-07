package com.practice.thread.communication;

import java.util.LinkedList;

/**
 * 【魔法厨房·初阶】
 * 2 个厨师（生产者）各做 3 个包子，2 个食客（消费者）各吃 3 个包子。
 * 蒸笼容量 3：满了厨师得停，空了食客得停。
 *
 * 直接运行会发生什么？蒸笼可能突破容量堆到 6 个；食客可能抢空蒸笼抛异常。
 * 你的任务——用 wait/notify 让厨房恢复秩序：
 *   1. TODO ①：produce() 里"蒸笼满了"要 wait（提示：while + buns.size() == capacity）
 *   2. TODO ②：consume() 里"蒸笼空了"要 wait（提示：while + buns.isEmpty()）
 *   3. TODO ③：叫醒用 notify 还是 notifyAll？跑通后试试换成 notify，多跑几次观察会不会卡死，想清为什么
 */
public class BunShop {

    static class Steamer {
        private final LinkedList<String> buns = new LinkedList<>();
        private final int capacity = 1;
        private final Object lock = new Object();

        // 生产者：放入一个包子
        public void produce(String bun) throws InterruptedException {
            synchronized (lock) {
                while(buns.size()==capacity){
                    lock.wait();
                }

                // TODO ①：蒸笼满了要等。wait 必须包在 while 里！
                // while (________) { lock.wait(); }

                buns.add(bun);
                System.out.println(Thread.currentThread().getName() + " 做好：" + bun
                        + "（蒸笼剩 " + buns.size() + " 个）");
                // TODO ③：叫醒等待的线程——notify 还是 notifyAll？
                lock.notifyAll();
            }
        }

        // 消费者：取出一个包子
        public String consume() throws InterruptedException {
            synchronized (lock) {
                // TODO ②：蒸笼空了要等。同样用 while！
                // while (________) { lock.wait(); }
                while(buns.size()==0){
                    lock.wait();
                }

                String bun = buns.removeFirst();
                System.out.println(Thread.currentThread().getName() + " 吃掉：" + bun
                        + "（蒸笼剩 " + buns.size() + " 个）");
                // TODO ③：叫醒等待的线程——notify 还是 notifyAll？
                lock.notifyAll();
                return bun;
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Steamer steamer = new Steamer();

        // 2 个厨师：各做 3 个包子
        Thread chefA = new Thread(() -> {
            String[] names = {"肉包", "菜包", "豆沙包"};
            try { for (String name : names) steamer.produce(name); }
            catch (InterruptedException e) { e.printStackTrace(); }
        }, "厨师A");

        Thread chefB = new Thread(() -> {
            String[] names = {"奶黄包", "叉烧包", "灌汤包"};
            try { for (String name : names) steamer.produce(name); }
            catch (InterruptedException e) { e.printStackTrace(); }
        }, "厨师B");

        // 2 个食客：各吃 3 个包子
        Runnable eat = () -> {
            try { for (int i = 0; i < 3; i++) steamer.consume(); }
            catch (InterruptedException e) { e.printStackTrace(); }
        };
        Thread eaterA = new Thread(eat, "食客A");
        Thread eaterB = new Thread(eat, "食客B");

        chefA.start();
        chefB.start();
        eaterA.start();
        eaterB.start();

        chefA.join();
        chefB.join();
        eaterA.join();
        eaterB.join();
        System.out.println("=== 全部吃完，打烊 ===");
    }
}
