package com.practice.inheritance;

/**
 * 子类：继承 Animal，重写 makeSound
 */
public class Dog extends Animal {

    public Dog(String name) {
        super(name);  // 调用父类构造器
    }

    @Override  // 注解：告诉编译器这是重写方法
    public void makeSound() {
        System.out.println(getName() + "：汪汪！🐶");
    }

    // 子类特有的方法
    public void wagTail() {
        System.out.println(getName() + " 在摇尾巴~");
    }
}
