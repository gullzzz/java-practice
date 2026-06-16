package com.practice.mathrandom;

/**
 * 炼金术士——程序入口
 *
 * 运行后你大概会看到一屏幕报错（因为 TODO 还没填），
 * 把三个 TODO 方法实现完，输出就会变成药剂合成统计表。
 */
public class AlchemistApp {
    public static void main(String[] args) {
        // 注册三种配方
        PotionRecipe healingPotion = new PotionRecipe(
                "治疗药水", 70,   // 70% 成功率
                70, 90            // 70分=优秀, 90分=完美
        );
        PotionRecipe manaPotion = new PotionRecipe(
                "魔力药水", 50,   // 50% 成功率——更难合
                75, 92
        );
        PotionRecipe luckPotion = new PotionRecipe(
                "幸运药水", 30,   // 30% 成功率——极其不稳定
                80, 95
        );

        AlchemyLab lab = new AlchemyLab();

        // 每种药剂批量合成 1000 瓶，看看运气怎么样
        lab.batchBrew(healingPotion, 1000);
        System.out.println();
        lab.batchBrew(manaPotion, 1000);
        System.out.println();
        lab.batchBrew(luckPotion, 1000);
    }
}
