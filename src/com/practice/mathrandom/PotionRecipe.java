package com.practice.mathrandom;

/**
 * 药剂配方——定义一种药剂的属性和合成规则
 */
public class PotionRecipe {

    private final String name;
    private final int baseSuccessRate;        // 基础成功率 (0~100)
    private final int excellentThreshold;     // 达到"优秀"的最低随机值
    private final int perfectThreshold;       // 达到"完美"的最低随机值

    /**
     * @param name              药剂名称
     * @param baseSuccessRate   基础成功率 (0~100)，随机值 < 此值则成功
     * @param excellentThreshold 成功前提下，品质骰 >= 此值则为"优秀"
     * @param perfectThreshold   成功前提下，品质骰 >= 此值则为"完美"
     */
    public PotionRecipe(String name, int baseSuccessRate,
                        int excellentThreshold, int perfectThreshold) {
        this.name = name;
        this.baseSuccessRate = baseSuccessRate;
        this.excellentThreshold = excellentThreshold;
        this.perfectThreshold = perfectThreshold;
    }

    public String getName() { return name; }

    /**
     * 判定合成是否成功：掷一个 [0, 100) 的随机数，
     * 如果小于 baseSuccessRate，则合成成功。
     *
     * 提示：Random.nextInt(100) 生成的是 [0, 100) 即 0~99
     * 那如果 baseSuccessRate=70，代表 70% 成功率，
     * 随机值 0~69 命中 → 成功，70~99 不命中 → 失败
     */
    public boolean isSuccess(int roll) {
        return roll < baseSuccessRate;
    }

    /**
     * 合成成功后，掷品质骰（另一个随机数，范围 [0, 100)），
     * 根据阈值决定品质等级。
     */
    public BrewingResult determineQuality(int qualityRoll) {
        if (qualityRoll >= perfectThreshold) {
            return BrewingResult.PERFECT;
        } else if (qualityRoll >= excellentThreshold) {
            return BrewingResult.EXCELLENT;
        }
        return BrewingResult.NORMAL;
    }
}
