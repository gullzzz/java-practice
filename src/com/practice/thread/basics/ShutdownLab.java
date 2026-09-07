package com.practice.thread.basics;

import java.util.concurrent.ExecutorService;

import java.util.concurrent.*;
import java.time.LocalTime;
public class ShutdownLab {
    public static void main(String[] args) throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(2);
        for (int i = 1; i <= 2; i++) {
            final int id = i;
            pool.submit(() -> {
                System.out.println(LocalTime.now() + " 任务" + id + " 开始");
                try { Thread.sleep(5000); } catch (InterruptedException ignored) {}
                System.out.println(LocalTime.now() + " 任务" + id + " 结束");
            });
        }
        System.out.println(LocalTime.now() + " A. 调用 shutdown 之前");
        pool.shutdown();
        System.out.println(LocalTime.now() + " B. shutdown 返回了");
        pool.awaitTermination(10, TimeUnit.SECONDS);
        System.out.println(LocalTime.now() + " C. awaitTermination 返回了");
    }
}
