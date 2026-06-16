package com.practice.interfaces;

/**
 * 阶段7：接口与抽象类
 * 涵盖：interface、abstract、implements、default方法、多接口实现
 */
public class InterfaceDemo {
    public static void main(String[] args) {
        System.out.println("========== Java 接口与抽象类 ==========\n");

        abstractClassDemo();
        interfaceDemo();
        multiImplement();
        defaultMethodDemo();
    }

    static void abstractClassDemo() {
        System.out.println("--- 1. 抽象类 ---");

        // Vehicle v = new Vehicle("x"); // ❌ 抽象类不能实例化

        Vehicle car = new Car("Tesla");
        car.start();     // 抽象方法 → 多态调用子类实现
        car.stop();      // 普通方法 → 来自抽象类

        // car.fly();    // ❌ Vehicle 类型看不到 Flyable 的方法

        System.out.println();
    }

    static void interfaceDemo() {
        System.out.println("--- 2. 接口 ---");

        Flyable f = new Car("BMW");
        f.fly();         // 多态调用
        f.land();        // default 方法

        // 接口静态方法
        System.out.println("类型: " + Flyable.TYPE);
        System.out.println("信息: " + Flyable.getWingInfo());

        System.out.println();
    }

    static void multiImplement() {
        System.out.println("--- 3. 多接口实现 ---");

        Duck duck = new Duck("唐老鸭");
        duck.fly();      // Flyable
        duck.swim();     // Swimmable
        duck.land();     // Flyable 的 default
        duck.dive();     // Swimmable 的 default

        // 同一对象可以赋值给不同接口类型
        Flyable flyingDuck = duck;
        Swimmable swimmingDuck = duck;

        flyingDuck.fly();
        swimmingDuck.swim();
        // flyingDuck.swim();  // ❌ Flyable 看不到 Swimable 的方法

        System.out.println();
    }

    static void defaultMethodDemo() {
        System.out.println("--- 4. default 方法（Java 8+） ---");

        Duck duck = new Duck("小黄鸭");
        duck.dive();     // 直接用接口的默认实现
        duck.land();     // 直接用接口的默认实现

        // 抽象类 vs 接口 对比:
        // 抽象类：单继承，可以有字段和构造器
        // 接口：多实现，只能有常量和抽象/default/static方法（Java 8+）
        System.out.println("\n关键区别:");
        System.out.println("- 类只能继承一个抽象类（单继承）");
        System.out.println("- 类可以实现多个接口（多实现）");
        System.out.println("- 抽象类可以有构造器和实例字段");
        System.out.println("- 接口主要定义行为契约");
        System.out.println();
    }
}
