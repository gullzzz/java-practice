package com.practice.inheritance;

/**
 * 继承 练习题（共4题）
 *
 * 运行方式：
 *   javac -encoding UTF-8 -d out src/com/practice/inheritance/*.java
 *   java -cp out com.practice.inheritance.InheritanceTest
 */
public class InheritanceTest {
    public static void main(String[] args) {
        System.out.println("========== 继承 练习题 ==========\n");
        test1();
        test2();
        test3();
        test4();
    }

    /*
     * 题1：Shape继承体系
     * 创建父类 Shape（有方法 area() 返回0.0）
     * 子类 Circle（半径）和 Rectangle（长宽），分别重写 area()
     * 创建对象测试面积计算
     */
    static void test1() {
        // TODO: 你的代码（类可以写在同一个文件里）

    }

    /*
     * 题2：super使用
     * 创建父类 Person（name, age），子类 Student extends Person（增加 school 属性）
     * Student构造器用 super(name, age) 调用父类构造器
     * Student中重写一个方法，其中先调用 super.xxx() 再写自己的逻辑
     */
    static void test2() {
        // TODO: 你的代码

    }

    /*
     * 题3：构造顺序
     * 创建三层继承：Grandparent → Parent → Child
     * 每个类的构造器打印自己的类名
     * 验证：new Child() 时的构造器执行顺序
     */
    static void test3() {
        // TODO: 你的代码

    }

    /*
     * 题4：final关键字
     * 创建一个 final class 和一个带有 final 方法的普通类
     * 尝试继承 final class（注释掉，表明会编译报错）
     * 验证 final 变量不可修改
     */
    static void test4() {
        // TODO: 你的代码

    }
}
