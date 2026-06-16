package com.practice.mathrandom;

/**
 * 炼金结果枚举
 */
public enum BrewingResult {
    FAIL(" 炸锅了！药剂变得漆黑一团，散发着焦臭味..."),
    NORMAL(" 普通品质——能用，但也就勉强及格的水平"),
    EXCELLENT(" 优秀品质——色泽通透，效果稳定，冒险者会喜欢的"),
    PERFECT("  完美品质！药剂泛着金色微光，这是教科书级别的杰作");

    private final String description;

    BrewingResult(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
