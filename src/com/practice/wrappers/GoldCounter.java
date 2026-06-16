package com.practice.wrappers;

import java.util.ArrayList;
import java.util.List;

/**
 * 魔法钱袋 —— 冒险者公会金币统计工具
 *
 * 挑战：金币数据来自各种来源（字符串、int、null），统一收纳并计算总和。
 * 代码能编译，但有至少 3 个隐患——找到它们并修复。
 */
public class GoldCounter {

    public static void main(String[] args) {
        // ---------- 数据来源 ----------
        // 来源1：从文件/网络读到的原始字符串
        String[] rawData = {"150", "200", "not_a_number", "350", null, "127","9999999999999"};

        // 来源2：基本类型运算结果
        int baseGold = 100;
        int bonusGold = 50;
        int totalFromCalc = baseGold + bonusGold;
        String regex = "-?\\d+";

        // ---------- 目标：把所有金币装进袋子 ----------
        List<Integer> goldBag = new ArrayList<>();

        // ---------- 步骤1：解析字符串数组，装入袋子 ----------
        for (String raw : rawData) {
            // BUG #1 潜伏于此：这行代码在遇到 "not_a_number" 或 null 时会怎样？
            if (raw == null || !raw.matches(regex)) {
                continue;
            }

            try {
                int gold = Integer.parseInt(raw);
                goldBag.add(gold); // 自动装箱：int → Integer
            } catch (NumberFormatException e) {
                System.out.println("跳过非法数据: " + raw);

            }
        }

        // ---------- 步骤2：把计算结果也装进去 ----------
        goldBag.add(totalFromCalc); // 这行没问题——但你确定？

        // ---------- 步骤3：计算总金币 ----------
        int total = 0;
        for (Integer g : goldBag) {
            // BUG #2 潜伏于此：如果袋子里出现 null，这行会发生什么？
            total += g; // 自动拆箱：Integer → int
        }

        System.out.println("冒险者公会总金币: " + total);

        // ---------- 步骤4：排行榜比较 ----------
        // TODO: 定义两个 Integer 变量 rankA 和 rankB，都赋值为 127，
        // 用 == 和 equals 分别比较，看看结果有什么不同。
        // 然后再定义 rankC 和 rankD，都赋值为 200，重复比较。
        // 把你的发现写在注释里！
        Integer rankA=127;
        Integer rankB=127;
        Integer rankC=200;
        Integer rankD=200;
        System.out.println(rankA==rankB);
        System.out.println(rankC==rankD);
        System.out.println(rankA.equals(rankB));
        System.out.println(rankC.equals(rankD));
        //我发现rankA和rankB是同一个对象,而rankCD则不是,因为Integer的缓存机制只支持-128到127之间



        // ---------- 步骤5：缓存边界探测 ----------
        // TODO: 写一个循环，从 -129 循环到 129，
        // 用 Integer.valueOf(i) 和 == 比较，找出缓存生效的边界。
        // 提示：Integer.valueOf(-129) == Integer.valueOf(-129) 的结果是？
        System.out.println(Integer.valueOf(-129) == Integer.valueOf(-129));
        System.out.println(Integer.valueOf(-128) == Integer.valueOf(-128));
        System.out.println(Integer.valueOf(127) == Integer.valueOf(127));
        System.out.println(Integer.valueOf(128) == Integer.valueOf(128));

//        System.out.println("____________________________");
//        String[] testCases = {"42", "not_a_number", "9999999999999"};
//        for (String s : testCases) {
//            // TODO: 用 try-catch 包住 parseInt，打印成功或失败
//            try {
//                int i=Integer.parseInt(s);
//            }catch (NumberFormatException n){
//                System.out.println("跳过非法数据: " + s);
//            }
//        }




    }


}
