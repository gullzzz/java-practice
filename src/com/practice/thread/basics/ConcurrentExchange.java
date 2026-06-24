package com.practice.thread.basics;

/**
 * 【魔法交易所·并发升级】挑战
 *
 * 目标：让交易文件的后台加载和分析计算同时进行，
 *       主线程不等结果，先输出"系统就绪，正在后台处理…"
 *
 * 当前问题：所有操作都在 main 线程串行执行，
 *          主线程干等着加载和分析，什么也做不了。
 */
public class ConcurrentExchange {

    public static void main(String[] args) throws InterruptedException {
        // TODO ①：创建任务对象（Runnable），任务里依次调用 loadAndAnalyze()
        //          提示：Runnable 是函数式接口，可以用 Lambda
        Runnable task= ()->{
            loadAndAnalyze();
        };
        // TODO ②：用 Thread 包装这个任务，并启动它
        //          注意：调用的是 start() 还是 run()？想想 README 里的区别
        Thread thread=new Thread(task);
        thread.start();

        // 这是主线程要做的事——不应该被阻塞
        System.out.println("系统就绪，正在后台处理…");
        thread.join();

        // TODO ③：主线程打印完就没事了？试试看程序会不会在后台线程
        //          结束前就退出。如果会，怎么让主线程等一等？
        //          提示：查一下 join() 的用法
    }

    /** 模拟：从文件加载交易数据 → 分析 → 输出统计结果 */
    static void loadAndAnalyze() {
        System.out.println("[后台] 开始加载交易日志...");
        fakeFileRead();                // 模拟耗时 IO
        System.out.println("[后台] 加载完成，开始分析...");
        fakeCompute();                 // 模拟耗时计算
        System.out.println("[后台] 分析完毕！总交易笔数：12854，总金额：¥3,827,600");
    }

    private static void fakeFileRead() {
        sleep(1500);
    }

    private static void fakeCompute() {
        sleep(1000);
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            System.out.println("线程被打断了！");
        }
    }
}
