package com.practice.methods;

/**
 * 方法 练习题（共4题）
 *
 * 运行方式：
 *   javac -encoding UTF-8 -d out src/com/practice/methods/*.java
 *   java -cp out com.practice.methods.MethodsTest
 */
public class MethodsTest {
    public static void main(String[] args) {
        System.out.println("========== 方法 练习题 ==========\n");
        test1();
        test2();
        test3();
        test4();
    }

    /*
     * 题1：判断素数
     * 写一个方法 isPrime(int n)，判断一个数是不是素数（质数）
     * 返回 boolean，用 17、24、97 测试
     */
    static void test1() {
        // TODO: 你的代码

    }

    /*
     * 题2：方法重载
     * 写三个同名的 max 方法：
     *   max(int a, int b)      — 返回两个整数中较大的
     *   max(double a, double b) — 返回两个小数中较大的
     *   max(int a, int b, int c) — 返回三个整数中最大的
     * 分别测试
     */
    static void test2() {
        // TODO: 你的代码

    }

    /*
     * 题3：递归求阶乘
     * 写一个递归方法 factorial(int n)，返回 n!
     * 测试 0!、5!、10!
     * 注意：0! = 1
     */
    static void test3() {
        // TODO: 你的代码

    }

    /*
     * 题4：参数传递理解
     * 写两个方法分别演示：
     *   1. 基本类型参数 — 交换方法内两个int，但方法外不变
     *   2. 引用类型参数 — 修改数组元素，方法外也变了
     * 打印方法内外的值来验证
     */
    static void test4() {
        // TODO: 你的代码

    }
}
