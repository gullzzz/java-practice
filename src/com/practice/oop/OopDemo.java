package com.practice.oop;

/**
 * 阶段5：面向对象编程（OOP）基础
 * 涵盖：类与对象、构造器、封装、this、static、toString/equals
 */
public class OopDemo {
    public static void main(String[] args) {
        System.out.println("========== Java OOP 基础 ==========\n");

        createObjects();
        encapsulation();
        thisKeyword();
        staticDemo();
        toStringAndEquals();
    }

    static void createObjects() {
        System.out.println("--- 1. 创建对象与构造器 ---");

        // 使用构造器创建对象
        Person p1 = new Person("张三", 25);
        Person p2 = new Person("李四", 30);
        Person p3 = new Person();  // 无参构造器

        p1.introduce();
        p2.introduce();
        p3.introduce();

        System.out.println("已创建人数: " + Person.getTotalCount());

        // 对象的使用
        p1.birthday();  // 调用方法
        System.out.println(p1.getName() + " 的年龄: " + p1.getAge());

        System.out.println();
    }

    static void encapsulation() {
        System.out.println("--- 2. 封装（Encapsulation） ---");

        Person p = new Person("王五", 20);

        // 通过 getter 读取
        System.out.println("姓名: " + p.getName() + ", 年龄: " + p.getAge());

        // 通过 setter 修改（有校验）
        p.setAge(-5);   // 非法值，被拒绝
        System.out.println("设置-5后: " + p.getAge());  // 还是20

        p.setAge(25);   // 合法值
        System.out.println("设置25后: " + p.getAge());

        // 不能直接访问 private 字段
        // p.name = "hack";  // ❌ 编译错误

        System.out.println();
    }

    static void thisKeyword() {
        System.out.println("--- 3. this 关键字 ---");

        // this 指向当前对象
        // - this.name 区分成员变量和参数
        // - this() 调用本类的另一个构造器（见 Person 无参构造器）
        System.out.println("详见 Person.java 中的构造器链调用: this(\"未命名\", 0)");
        System.out.println();
    }

    static void staticDemo() {
        System.out.println("--- 4. static 静态 vs 实例 ---");

        Person p1 = new Person("A", 10);
        Person p2 = new Person("B", 20);

        // 静态方法：通过类名调用
        System.out.println("总人数（静态）: " + Person.getTotalCount());

        // 实例方法：通过对象调用
        p1.introduce();
        p2.introduce();

        // 静态变量在所有实例间共享
        System.out.println("p1和p2共享同一个totalCount");

        System.out.println();
    }

    static void toStringAndEquals() {
        System.out.println("--- 5. toString() 与 equals() ---");

        Person p1 = new Person("小明", 18);
        Person p2 = new Person("小明", 18);
        Person p3 = p1;

        // toString：println 自动调用
        System.out.println("p1: " + p1.toString());
        System.out.println("p2: " + p2);

        // == 比较引用地址
        System.out.println("p1 == p2: " + (p1 == p2));        // false（不同对象）
        System.out.println("p1 == p3: " + (p1 == p3));        // true（同一对象）

        // equals 比较内容（我们重写了 equals）
        System.out.println("p1.equals(p2): " + p1.equals(p2)); // true（内容相同）

        System.out.println();
    }
}
