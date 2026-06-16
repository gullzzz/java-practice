package com.practice.enums;

import java.lang.reflect.Constructor;

/**
 * 游戏全局配置——枚举单例
 */
public enum GameConfig {
    INSTANCE("JavaQuest", 100, 500);

    private final String appName;
    private final int maxLevel;
    private final int initialGold;

    GameConfig(String appName, int maxLevel, int initialGold) {
        this.appName = appName;
        this.maxLevel = maxLevel;
        this.initialGold = initialGold;
    }


    public String getAppName() {
        return appName;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public int getInitialGold() {
        return initialGold;
    }


    public static void main(String[] args) {
        GameConfig c1 = GameConfig.INSTANCE;
        GameConfig c2 = GameConfig.INSTANCE;
        System.out.println(c1 == c2);
        try {
            Constructor<GameConfig> c = GameConfig.class.getDeclaredConstructor(String.class, int.class, int.class);
            c.setAccessible(true);
            GameConfig hacked = c.newInstance("Hacked", 999, 999);
            System.out.println("⚠️ 第二个实例被创建了！INSTANCE == hacked? " + (INSTANCE == hacked));
        } catch (Exception e) {
            System.out.println("🛡️ Java 挡住了反射攻击：" + e.getClass().getSimpleName());
        }
    }
}
