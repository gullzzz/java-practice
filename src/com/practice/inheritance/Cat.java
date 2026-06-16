package com.practice.inheritance;

/**
 * 另一个子类：继承 Animal
 */
public class Cat extends Animal {

    public Cat(String name) {
        super(name);
    }

    @Override
    public void makeSound() {
        System.out.println(getName() + "：喵喵！🐱");
    }

    // 访问父类的 protected 方法
    public void eatFish() {
        System.out.print(getName() + " 抓到了一条鱼，");
        eat();  // 可以调用父类 protected 方法
    }
}
