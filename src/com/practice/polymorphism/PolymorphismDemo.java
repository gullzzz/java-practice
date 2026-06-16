package com.practice.polymorphism;

/**
 * 第2.4章：多态（Polymorphism）
 * 涵盖：向上转型、向下转型、instanceof、静态方法无多态、字段无多态
 */
public class PolymorphismDemo {
    public static void main(String[] args) {
        System.out.println("========== 多态（Polymorphism） ==========\n");

        upcasting();
        downcasting();
        instanceofDemo();
        staticMethodNoPoly();
        fieldNoPoly();
        methodParamPoly();
        collectionPoly();
    }

    // 1. 向上转型（Upcasting）—— 自动，绝对安全
    static void upcasting() {
        System.out.println("--- 1. 向上转型（Upcasting） ---");

        // 子类对象赋值给父类引用，自动完成
        Animal a1 = new Dog("旺财");
        Animal a2 = new Cat("咪咪");

        // 调用重写方法 → 执行实际对象类型的方法（动态绑定）
        a1.makeSound();  // Dog的makeSound
        a2.makeSound();  // Cat的makeSound

        // a1.wagTail(); // ❌ 编译错误 — Animal类型没有wagTail方法
        // 规则：编译看左边（引用类型），运行看右边（实际对象类型）

        System.out.println("a1的实际类型: " + a1.getClass().getSimpleName());
        System.out.println("a2的实际类型: " + a2.getClass().getSimpleName());
        System.out.println();
    }

    // 2. 向下转型（Downcasting）—— 强制，可能抛异常
    static void downcasting() {
        System.out.println("--- 2. 向下转型（Downcasting） ---");

        Animal a = new Dog("大黄");

        // 向下转型：父类引用 → 子类类型，必须强转
        Dog d = (Dog) a;
        d.wagTail();  // 现在可以调用Dog特有方法了

        // 错误示范：实际对象不是目标类型
        Animal a2 = new Cat("小花");
        try {
            Dog d2 = (Dog) a2;  // 编译通过，但运行时会炸
        } catch (ClassCastException e) {
            System.out.println("转型失败: " + e.getMessage());
        }

        System.out.println();
    }

    // 3. instanceof —— 安全转型的前提
    static void instanceofDemo() {
        System.out.println("--- 3. instanceof 类型检查 ---");

        Animal[] animals = {new Dog("小黑"), new Cat("雪球"), new Dog("来福")};

        for (Animal a : animals) {
            // 传统写法：先判断，再强转
            if (a instanceof Dog) {
                Dog dog = (Dog) a;
                System.out.print("发现一只狗 → ");
                dog.wagTail();
            }

            // Java 16+ 模式匹配：判断 + 强转一步完成
            if (a instanceof Cat cat) {
                System.out.print("发现一只猫 → ");
                cat.climbTree();
            }
        }

        // instanceof 对所有父类都返回 true
        Animal a = new Dog("测试");
        System.out.println("a instanceof Dog: " + (a instanceof Dog));       // true
        System.out.println("a instanceof Animal: " + (a instanceof Animal)); // true
        System.out.println("a instanceof Object: " + (a instanceof Object)); // true
        System.out.println("a instanceof Cat: " + (a instanceof Cat));       // false

        System.out.println();
    }

    // 4. 静态方法没有多态 —— 与类绑定，不是对象
    static void staticMethodNoPoly() {
        System.out.println("--- 4. 静态方法：没有多态 ---");

        Parent p = new Child();

        p.instanceMethod();  // 多态生效 → 调用Child的instanceMethod
        p.staticMethod();    // 没有多态 → 调用Parent的staticMethod（看引用类型）

        // 正确做法：静态方法直接用类名调用
        Parent.staticMethod();  // Parent的
        Child.staticMethod();   // Child的

        System.out.println();
    }

    // 5. 成员变量没有多态 —— 看引用类型
    static void fieldNoPoly() {
        System.out.println("--- 5. 成员变量：没有多态 ---");

        Parent p = new Child();

        System.out.println("p.name = " + p.name);  // "Parent" — 看引用类型
        Child c = (Child) p;
        System.out.println("c.name = " + c.name);  // "Child"  — 看引用类型

        // 这被称为"字段隐藏"（Field Hiding），不是重写
        System.out.println();
    }

    // 6. 方法参数多态 —— 最常用的多态场景
    static void methodParamPoly() {
        System.out.println("--- 6. 方法参数多态 ---");

        // 同一个方法，传入不同子类对象，表现不同行为
        feedAnimal(new Dog("阿福"));
        feedAnimal(new Cat("球球"));

        System.out.println();
    }

    // 参数声明为父类类型，可接收任意子类
    static void feedAnimal(Animal a) {
        System.out.print("喂养 " + a.getName() + " → ");
        a.makeSound();
    }

    // 7. 集合/数组多态
    static void collectionPoly() {
        System.out.println("--- 7. 集合/数组多态 ---");

        // 用父类类型声明数组/集合，存储不同子类对象
        Animal[] zoo = {
            new Dog("大黄"),
            new Cat("咪咪"),
            new Dog("来福"),
            new Cat("雪球")
        };

        System.out.println("动物园集体发声：");
        for (Animal a : zoo) {
            a.makeSound();  // 多态：无需判断类型，自动调用正确方法
        }

        System.out.println();
    }
}
