package com.practice.interfaces;

/**
 * 鸭子：实现多个接口（Java 支持多实现）
 */
public class Duck implements Flyable, Swimmable {

    private String name;

    public Duck(String name) {
        this.name = name;
    }

    @Override
    public void fly() {
        System.out.println(name + " 在天空中飞翔~");
    }

    @Override
    public void swim() {
        System.out.println(name + " 在湖里游泳~");
    }

    // 两个接口都有 default 方法，没有冲突
    // 如果冲突，必须手动重写并指定用哪个接口的实现
}
