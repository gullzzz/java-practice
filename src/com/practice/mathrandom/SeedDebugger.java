package com.practice.mathrandom;

import java.util.Random;

/**
 * 种子调试器 —— 证明"随机"是可以复现的
 *
 * 场景：测试员报告"幸运药水合成率只有 25%，不是宣称的 30%！"
 * 你想复现他的测试环境，但 new Random() 每次结果不同怎么办？
 *
 * 答案：固定种子。同一个种子 → 同一个随机序列 → 能复现 Bug
 *
 * 你的任务：完成两个方法，然后运行 main 对比效果
 */
public class SeedDebugger {




    /**
     * 用默认 Random（种子=纳秒时间戳）模拟 20 次合成，
     * 返回成功次数。
     *
     * 每次运行结果都不同——这就是"不可复现"的问题
     */
    // TODO: 实现方法签名和逻辑
    //       提示：new Random() 无参 → 种子来自系统时间，每次运行不同
    //       循环 20 次，用 nextInt(100) < successRate 判定成功

    public static int brewWithDefaultRandom(int successRate) {
        Random r=new Random();

        int count=0;
        for (int i = 0; i <20 ; i++) {
            int successRoll = r.nextInt(100);
            if(successRoll<successRate){
                count++;
            }
        }

        return count ;
    }


    /**
     * 用固定种子的 Random 模拟 20 次合成，
     * 返回成功次数。
     *
     * 每次运行结果完全相同——这才是调试环境！
     */
    // TODO: 实现方法签名和逻辑
    //       提示：new Random(42)  → 种子固定，每次运行序列一致
    public static int brewWithFixedSeed(int successRate,long seed) {
        Random r=new Random(seed);

        int count=0;
        for (int i = 0; i <20 ; i++) {
            int successRoll = r.nextInt(100);
            if(successRoll<successRate){
                count++;
            }
        }
        return count ;
    }

    public static void main(String[] args) {
        int successRate = 30;  // 30% 成功率

        // 运行 3 次默认模式，看结果漂移
        System.out.println("=== 默认 Random（每次结果不同）===");
        for (int i = 0; i < 3; i++) {
            // TODO: 调用你实现的默认方法，打印结果

            System.out.println("第" + (i+1) + "次: " +brewWithDefaultRandom(successRate) + "/20 成功");
        }

        // 运行 3 次固定种子模式，看结果完全一致
        System.out.println("\n=== 固定种子 Random(42)（每次结果相同）===");
        for (int i = 0; i < 3; i++) {
            // TODO: 调用你实现的固定种子方法，打印结果
             System.out.println("第" + (i+1) + "次: " + brewWithFixedSeed(successRate,42) + "/20 成功");
        }

        System.out.println("\n思考：如果三次固定种子输出完全一样，\"随机\"去哪了？");
    }
}
