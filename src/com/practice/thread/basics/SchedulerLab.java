package com.practice.thread.basics;

/**
 * 【魔法交易所·VIP调度实验室】挑战
 *
 * 背景：VIP交易员和实习交易员同时抢100笔订单。
 *       VIP线程设置高优先级，实习线程设置低优先级。
 *       每处理5笔订单，线程主动 yield() 让出CPU。
 *
 * 目标：观察 yield + 优先级的组合行为，回答核心问题：
 *       "高优先级 + yield() 就一定先跑完吗？"
 */
public class SchedulerLab {

    /** 两人共享的订单池——谁抢到处理谁的计数器 */
    static int vipDone = 0;
    static int internDone = 0;
    static final int TOTAL = 1;

    public static void main(String[] args) throws InterruptedException {

        // TODO ①：创建 Runnable 任务——VIP交易员
        //          每次循环：① 抢一单（vipDone++）② 打印进度 ③ 每5单 yield() 让出CPU
        //          提示：判断"每5单"用 vipDone % 5 == 0
        Runnable vipTask = () -> {
            // TODO: 在这里实现VIP的处理循环
            while (internDone+vipDone!=TOTAL){
                vipDone++;
                if(vipDone%5==0){
                    Thread.yield();
                }


            }


        };

        // TODO ②：创建 Runnable 任务——实习交易员（逻辑同上，计数器用 internDone）
        Runnable internTask = () -> {
            // TODO: 在这里实现实习交易员的处理循环
            while (internDone+vipDone!=TOTAL){
                internDone++;
                if(internDone%5==0){
                    Thread.yield();

                }


            }


        };

        // TODO ③：创建两个Thread，分别包装 vipTask 和 internTask
        Thread vip = new Thread(vipTask);
        Thread intern = new Thread(internTask);

        // TODO ④：设置优先级——VIP 用 Thread.MAX_PRIORITY，实习用 Thread.MIN_PRIORITY
        //          提示：设优先级必须在 start() 之前调用
        vip.setPriority(10);
        intern.setPriority(1);

        // 同时开跑

        vip.start();
        intern.start();


        // 等两人都跑完
        vip.join();
        intern.join();

        System.out.println("\n========== 最终结果 ==========");
        System.out.println("VIP交易员处理：" + vipDone + " 单");
        System.out.println("实习交易员处理：" + internDone + " 单");
        System.out.println(internDone+vipDone);

        // TODO ⑤：多跑几次，观察结果是否稳定。
        //          如果VIP不是每次都赢，想想 README 里那句话——
        //          "优先级不保证，不同OS行为不一致"
    }
}
