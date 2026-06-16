package com.practice.polymorphism;

/**
 * 多态Demo的父类
 */
public class Animal {
    private String name;

    public Animal(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    // 实例方法 — 子类重写后可触发多态
    public void makeSound() {
        System.out.println(name + " 发出声音...");
    }
}
