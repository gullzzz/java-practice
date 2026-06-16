package com.practice.oop;

/**
 * OOP 练习题（共4题）
 *
 * 运行方式：
 *   javac -encoding UTF-8 -d out src/com/practice/oop/*.java
 *   java -cp out com.practice.oop.OopTest
 */
public class OopTest {
    public static void main(String[] args) {
        System.out.println("========== OOP 练习题 ==========\n");
        test1();
        test2();
        test3();
        test4();
    }

    /*
     * 题1：学生类
     * 创建一个 Student 类，包含：
     *   私有属性：name(String)、age(int)、score(double)
     *   构造方法：有参和无参各一个
     *   getter/setter（setScore不能超过100不能低于0）
     * 在 main 中创建两个学生对象并打印信息
     */
    static void test1() {
        // TODO: 你的代码（Student类写在同一个文件里，以内部类或独立类方式）

    }

    /*
     * 题2：银行账户
     * 创建一个 BankAccount 类，包含：
     *   私有属性：accountNumber(String)、balance(double)
     *   方法：deposit(存钱)、withdraw(取钱，余额不足提示)、getBalance
     * 模拟存取款操作
     */
    static void test2() {
        // TODO: 你的代码

    }

    /*
     * 题3：计算器
     * 创建一个 Calculator 类，包含：
     *   方法：add、subtract、multiply、divide（divide除数为0时抛异常）
     * 测试加减乘除
     */
    static void test3() {
        // TODO: 你的代码

    }

    /*
     * 题4：对象数组
     * 用第1题的 Student 类，创建一个 Student[] 数组（3个学生）
     * 遍历找出分数最高的学生并打印其信息
     */
    static void test4() {
        // TODO: 你的代码

    }
}
