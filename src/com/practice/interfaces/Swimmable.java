package com.practice.interfaces;

/**
 * 另一个接口：定义"能游泳"的行为
 */
public interface Swimmable {
    void swim();

    default void dive() {
        System.out.println("潜入水中...");
    }
}
