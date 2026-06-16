package com.practice.polymorphism;

/**
 * 用于演示：静态方法无多态、字段无多态
 */
public class Parent {
    public String name = "Parent";

    public void instanceMethod() {
        System.out.println("Parent 的实例方法");
    }

    public static void staticMethod() {
        System.out.println("Parent 的静态方法");
    }
}
