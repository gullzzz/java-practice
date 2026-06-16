package com.practice.interfaces;

/**
 * 接口：定义"能飞"的行为契约
 */
public interface Flyable {
    // 常量（默认 public static final）
    String TYPE = "飞行物";

    // 抽象方法（默认 public abstract）
    void fly();

    // Java 8+ default 方法（有默认实现）
    default void land() {
        System.out.println("正在降落...");
    }

    // Java 8+ static 方法
    static String getWingInfo() {
        return "翅膀是实现飞行的关键";
    }
}
