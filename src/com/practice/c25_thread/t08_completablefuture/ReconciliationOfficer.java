package com.practice.c25_thread.t08_completablefuture;

import java.util.concurrent.*;

/**
 * 【结算中心·异步对账官】
 *
 * 对账日到了，结算中心要从 3 个"数据源"并行拉取数据，最后合并成一份对账单。
 * 你接手时，旧代码是串行 get() 干等：先查笔数、再查金额、再查异常数，一个等一个，
 * 三个查询各 2 秒，对账总共要跑 6 秒。
 *
 * 你的任务：用 CompletableFuture 把这段串行逻辑改造成"并行编排"。
 *
 * ── 旧代码长这样（已被删除，只留回忆）──
 *   int count = queryCount();          // 阻塞 2s
 *   double amount = queryAmount();     // 阻塞 2s
 *   int exception = queryException();  // 阻塞 2s，还可能直接抛异常
 *   System.out.println("对账完成：成交" + count + "笔，合计¥" + amount + "，异常" + exception + "单");
 * ── 总共 6 秒，且 queryException 一抛异常，整段对账直接崩 ──
 */
public class ReconciliationOfficer {

    public static void main(String[] args) {
        // TODO ①：这三个查询都是"查数据库"的 IO 密集任务，默认公共池会饿死。
        //         自己准备一个线程池，存到变量 ioPool。
        ExecutorService pool = Executors.newFixedThreadPool(3);

        // TODO ②：让三个查询（queryCount / queryAmount / queryException）并行跑起来，
        //         别串行等。它们互不依赖。
        try {
            CompletableFuture<Integer> queryCount=CompletableFuture.supplyAsync(ReconciliationOfficer:: queryCount,pool) ;
            CompletableFuture<Double> queryAmount=CompletableFuture.supplyAsync(ReconciliationOfficer::queryAmount,pool) ;
            CompletableFuture<Integer> queryException=CompletableFuture.supplyAsync(ReconciliationOfficer::queryException,pool).exceptionally(ex ->
            {
                System.out.println("统计失败：" + ex.getMessage());
                return 0;
            }) ;


            // TODO ③：把"笔数"和"金额"拼成一句汇总，比如"成交 1280 笔，合计 ¥175049.5"。
            CompletableFuture<String> summary=queryCount.thenCombine(queryAmount,(c, a)->"成交 " + c + " 笔，合计 ¥" + a);


            // TODO ④：queryException 可能抛异常（见方法实现）。对账不能因此崩掉，
            //         失败时异常订单数按 0 处理。


            // TODO ⑤：等"汇总文案"和"兜底后的异常数"都出来后，打印最终对账单，
            //         形如"对账完成：成交 1280 笔，合计 ¥175049.5，异常 7 单"
            CompletableFuture<Void> finalbill=summary.thenCombine(queryException,(s,q)->"对账完成：" + s + "，异常 " + q + " 单" ).handle((bill, ex) -> ex == null ? bill : "对账失败：" + ex.getMessage()).thenAccept(System.out::println);


            // TODO ⑥：主线程不能比异步任务先退场——把最终结果拿回来，并优雅关闭线程池。
            finalbill.join();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            pool.shutdown();
        }


    }

    // ── 三个"数据源"查询方法（已实现，不用动）────────────────────────

    /** 数据源1：查成交笔数（模拟 2 秒的数据库查询） */
    private static int queryCount() {
        sleep(2000);
        return 1280;
    }

    /** 数据源2：查成交总金额（模拟 2 秒的数据库查询） */
    private static double queryAmount() {
        sleep(2000);
        return 175049.5;
    }

    /** 数据源3：查异常订单数（模拟 2 秒，且约一半概率"查询超时"抛异常） */
    private static int queryException() {
        sleep(2000);
        if (Math.random() < 0.5) {
            throw new RuntimeException("风控库查询超时");
        }
        return 7;
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
