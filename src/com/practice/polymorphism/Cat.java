package com.practice.polymorphism;

/**
 * 子类Cat
 */
public class Cat extends Animal {

    public Cat(String name) {
        super(name);
    }

    @Override
    public void makeSound() {
        System.out.println(getName() + "：喵喵！");
    }

    // Cat特有方法
    public void climbTree() {
        System.out.println(getName() + " 爬上了树！");
    }
}
