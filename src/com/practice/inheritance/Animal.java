package com.practice.inheritance;

/**
 * 父类（基类）：展示 protected、方法重写的基础
 */
public class Animal {
    private String name;

    public Animal(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    // 子类可以重写这个方法
    public void makeSound() {
        System.out.println(name + " 发出声音...");
    }

    // 子类可以访问 protected 方法
    protected void eat() {
        System.out.println(name + " 在吃东西。");
    }

    // final 方法不能被重写
    public final void sleep() {
        System.out.println(name + " 在睡觉。zzz...");
    }
}
