package com.practice.mathrandom;

import java.util.Random;

/**
 * 炼金实验室——核心合成逻辑
 */
public class AlchemyLab {
    private final Random random = new Random();

    /**
     * 合成一瓶药剂
     */
    public BrewingResult brew(PotionRecipe recipe) {
        int successRoll = random.nextInt(100);
        if (recipe.isSuccess(successRoll)) {
            int qualityRoll = random.nextInt(100);
            return recipe.determineQuality(qualityRoll);
        }
        return BrewingResult.FAIL;
    }

    /**
     * 批量合成 N 瓶药剂，打印统计结果
     */
    public void batchBrew(PotionRecipe recipe, int count) {
        int successCount = 0;
        int failCount = 0;
        int normalCount = 0;
        int excellentCount = 0;
        int perfectCount = 0;

        for (int i = 0; i < count; i++) {
            switch (this.brew(recipe)) {
                case FAIL -> failCount++;
                case NORMAL -> normalCount++;
                case EXCELLENT -> excellentCount++;
                case PERFECT -> perfectCount++;
            }
        }
        successCount = count - failCount;
        double successRate = successCount * 100.0 / count;

        System.out.println("========== 批量合成：" + recipe.getName() + " × " + count + " ==========");
        System.out.println("  成功：" + successCount + " 瓶 (" + String.format("%.1f", successRate) + "%)");
        System.out.println("  失败：" + failCount + " 瓶");
        System.out.println("  ---- 品质分布 ----");
        System.out.println("  普通：" + normalCount + " 瓶  优秀：" + excellentCount + " 瓶  完美：" + perfectCount + " 瓶");
        System.out.println("================================================");
    }
}
