package com.practice.thread.pool;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

/**
 * 【魔法交易所·并发结算中心】挑战
 *
 * 背景：10笔订单需要并发结算，每笔处理耗时不同。
 *       用线程池并行处理，最后汇总总金额。
 *
 * 目标：掌握 Executors → ExecutorService → submit(Callable) → Future.get() 全链路。
 */
public class SettlementCenter {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // 模拟10笔待结算订单的金额
        BigDecimal[] amounts = {
            new BigDecimal("1500.00"), new BigDecimal("3200.50"),
            new BigDecimal("800.00"),  new BigDecimal("5600.00"),
            new BigDecimal("2300.00"), new BigDecimal("9100.00"),
            new BigDecimal("450.00"),  new BigDecimal("12000.00"),
            new BigDecimal("6700.00"), new BigDecimal("3900.00")
        };

        // TODO ①：创建一个固定大小为 3 的线程池
        //          提示：用 Executors 工厂方法，变量类型用什么？想想文档里的讨论
        ExecutorService pool=Executors.newFixedThreadPool(3);

        // TODO ②：创建一个 List<Future<BigDecimal>> 用来装每个任务的 Future
        List<Future<BigDecimal>> futures=new ArrayList<Future<BigDecimal>>();



        // TODO ③：遍历 amounts 数组，为每笔订单创建一个 Callable<BigDecimal> 任务
        //          Callable 里：① 调用 settle(金额) ② 返回结算结果
        //          提交到线程池，把返回的 Future 装进 list
        //          提示：Callable 用 Lambda 写，() -> { ... }
        for (int i = 0; i < amounts.length; i++) {
            int a=i;
            Callable<BigDecimal> callable=()->{
                BigDecimal result=settle(amounts[a]);
                return result;
            };
            futures.add(pool.submit(callable));
        }

        // TODO ④：遍历 Future 列表，用 get() 拿每个结果，累加到 total 里
        //          注意：get() 抛什么异常？怎么处理？
        BigDecimal total=BigDecimal.ZERO;
        for (Future<BigDecimal>future:futures){
            BigDecimal bigDecimal = future.get();
            total=total.add(bigDecimal);
        }

        // TODO ⑤：温柔关闭线程池——shutdown() + awaitTermination()
        pool.shutdown();
        pool.awaitTermination(30,TimeUnit.SECONDS);


        System.out.println("\n========== 结算完毕 ==========");
         System.out.println("总金额：" + total);
    }

    /** 模拟一笔订单的结算过程，返回结算金额 */
    static BigDecimal settle(BigDecimal amount) {
        String worker = Thread.currentThread().getName();
        System.out.println(worker + " 开始结算金额：" + amount);

        // 模拟处理耗时（200~800ms随机）
        int ms = 200 + new Random().nextInt(600);
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            System.out.println(worker + " 结算被打断！");
            return BigDecimal.ZERO;
        }

        // 结算金额 = 原金额 * 0.95（扣除5%手续费）
        BigDecimal result = amount.multiply(new BigDecimal("0.95"));
        System.out.println(worker + " 结算完成：" + amount + " → " + result);
        return result;
    }
}
