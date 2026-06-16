package com.practice.inheritance;

/**
 * 阶段6：继承与多态
 * 涵盖：extends、方法重写(@Override)、super、多态、instanceof、final
 */
public class InheritanceDemo {
    public static void main(String[] args) {
        System.out.println("========== Java 继承与多态 ==========\n");

        basicInheritance();
        polymorphism();
        instanceofDemo();
        superDemo();
    }

    static void basicInheritance() {
        System.out.println("--- 1. 基本继承 ---");

        Dog dog = new Dog("旺财");
        Cat cat = new Cat("咪咪");

        // 继承自父类的方法
        dog.eat();       // protected，子类可见
        dog.sleep();     // final，不能被重写
        cat.sleep();

        // 重写的方法
        dog.makeSound();
        cat.makeSound();

        // 子类特有方法
        dog.wagTail();
        cat.eatFish();

        System.out.println();
    }

    static void polymorphism() {
        System.out.println("--- 2. 多态（Polymorphism） ---");

        // 父类引用 → 子类对象（向上转型，自动）
        Animal a1 = new Dog("大黄");
        Animal a2 = new Cat("小花");

        // 调用的是实际对象的方法（动态绑定）
        a1.makeSound();  // 汪汪！
        a2.makeSound();  // 喵喵！

        // a1.wagTail(); // ❌ 编译错误 — Animal 类型没有 wagTail

        // 数组多态
        Animal[] animals = {
            new Dog("阿福"),
            new Cat("球球"),
            new Dog("来福"),
            new Cat("雪球")
        };

        System.out.println("\n动物园集体发声:");
        for (Animal a : animals) {
            a.makeSound();  // 多态：不用判断类型，自动调用正确的方法
        }

        System.out.println();
    }

    static void instanceofDemo() {
        System.out.println("--- 3. instanceof 类型判断 + 向下转型 ---");

        Animal a = new Dog("小黑");

        // instanceof 判断对象的真实类型
        if (a instanceof Dog) {
            Dog dog = (Dog) a;  // 向下转型（需要强转）
            dog.wagTail();
        }

        // Java 16+ 简化写法：instanceof + 模式匹配
        if (a instanceof Dog dog) {
            dog.wagTail();  // 不用再手动强转
        }

        System.out.println("a 是 Animal? " + (a instanceof Animal));  // true
        System.out.println("a 是 Dog? " + (a instanceof Dog));        // true
        System.out.println("a 是 Cat? " + (a instanceof Cat));        // false

        System.out.println();
    }

    static void superDemo() {
        System.out.println("--- 4. super 关键字 ---");

        // super 用于：
        // 1. super() — 调用父类构造器（必须在子类构造器第一行）
        // 2. super.method() — 调用父类方法
        // 3. super.field — 访问父类字段

        Dog dog = new Dog("豆豆");
        dog.makeSound();  // 调用重写后的方法

        // super 细节见 Dog.java 和 Cat.java 的构造器
        System.out.println("子类构造器通过 super(name) 传递参数给父类构造器");
        System.out.println();
    }
}
