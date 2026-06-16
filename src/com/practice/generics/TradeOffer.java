package com.practice.generics;

/**
 * 泛型交易要约：一笔"我的T换你的T"的以物易物提议
 * 两个类型参数：<F, T> —— 我出的物品类型 和 我想要的目标类型
 */
public class TradeOffer<F, T> {
    private final String offerer;   // 发起人
    private final F offering;       // 我出的物品
    private final Class<T> targetType;  // 我想要的目标类型
    private boolean matched;

    public TradeOffer(String offerer, F offering, Class<T> targetType) {
        this.offerer = offerer;
        this.offering = offering;
        this.targetType = targetType;
    }

    public String getOfferer() { return offerer; }
    public F getOffering() { return offering; }
    public Class<T> getTargetType() { return targetType; }
    public boolean isMatched() { return matched; }
    public void markMatched() { this.matched = true; }

    @Override
    public String toString() {
        return offerer + " 出 [" + offering + "] 换 <" + targetType.getSimpleName() + ">";
    }
}
