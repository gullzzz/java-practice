package com.practice.mathrandom;

/**
 * 药剂定价器 —— 用 Math 工具方法计算售价
 *
 * 定价公式：
 *   基础价 = 原料成本 × 难度系数^稀有度等级
 *   最终价 = 基础价 + sqrt(基础价) × 品质加成 → 向上取整到整数金币
 *
 * 你的任务：把三个 TODO 方法填完，每个只需一行代码
 */
public class PotionPricing {

    /** 稀有度等级对应的乘数 */
    public enum Rarity {
        COMMON(1),    // 普通
        RARE(2),      // 稀有
        EPIC(3);      // 史诗

        final int level;
        Rarity(int level) { this.level = level; }
    }

    /**
     * 计算基础价 = 原料成本 × 难度系数^稀有度等级
     *
     * 提示：Math.pow(底数, 指数) —— 2^3 = Math.pow(2, 3) = 8.0
     */
    public static double basePrice(double materialCost, double difficultyFactor, Rarity rarity) {
        // TODO: 一行搞定 —— materialCost × difficultyFactor 的 rarity.level 次方
       return materialCost*Math.pow(difficultyFactor,rarity.level);
//        throw new UnsupportedOperationException("TODO");
    }

    /**
     * 计算浮动溢价 = sqrt(基础价) × 品质加成
     *
     * 提示：Math.sqrt(x) 返回 x 的平方根
     */
    public static double premium(double basePrice, double qualityBonus) {
        // TODO: sqrt(basePrice) × qualityBonus
        return Math.sqrt(basePrice) * qualityBonus;
//        throw new UnsupportedOperationException("TODO");
    }

    /**
     * 最终售价 = 向上取整(基础价 + 溢价)
     *
     * 提示：Math.ceil(3.1) = 4.0，强转 (long) 得到整数
     * 注意：Math.round 是四舍五入，Math.ceil 是向上取整——这里用哪个？
     */
    public static long finalPrice(double basePrice, double premium) {
        // TODO: 向上取整 → 转 long
        return (long) Math.ceil(basePrice+premium);
//        throw new UnsupportedOperationException("TODO");
    }
}
