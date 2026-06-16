package com.practice.polymorphism;

/**
 * 用于演示：静态方法无多态、字段无多态
 */
public class Child extends Parent {
    public String name = "Child";  // 字段隐藏，不是重写

    @Override
    public void instanceMethod() {
        System.out.println("Child 的实例方法");
    }

    // 这不是重写（@Override不能用于静态方法），这是方法隐藏
    public static void staticMethod() {
        System.out.println("Child 的静态方法");
    }
}
