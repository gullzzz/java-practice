package com.practice.polymorphism;

/**
 * 子类Dog
 */
public class Dog extends Animal {

    public Dog(String name) {
        super(name);
    }

    @Override
    public void makeSound() {
        System.out.println(getName() + "：汪汪！");
    }

    // Dog特有方法 — 向上转型后无法通过父类引用调用
    public void wagTail() {
        System.out.println(getName() + " 在摇尾巴~");
    }
}
