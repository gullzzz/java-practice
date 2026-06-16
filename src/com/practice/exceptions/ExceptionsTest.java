package com.practice.exceptions;

/**
 * 异常处理 练习题（共4题）
 *
 * 运行方式：
 *   javac -encoding UTF-8 -d out src/com/practice/exceptions/*.java
 *   java -cp out com.practice.exceptions.ExceptionsTest
 */
public class ExceptionsTest {
    public static void main(String[] args) {
        System.out.println("========== 异常处理 练习题 ==========\n");
        test1();
        test2();
        test3();
        test4();
    }

    /*
     * 题1：除零异常
     * 写一个 divide(int a, int b) 方法
     * 当 b=0 时捕获 ArithmeticException，打印"除数不能为零"
     * 测试 10/2 和 10/0
     */
    static void test1() {
        // TODO: 你的代码

    }

    /*
     * 题2：空指针防护
     * 声明 String str = null
     * 调用 str.length() 并捕获 NullPointerException
     * 再用 if 判断提前防护，避免异常
     * 对比两种方式
     */
    static void test2() {
        // TODO: 你的代码

    }

    /*
     * 题3：自定义异常
     * 创建自定义异常 AgeException（继承 Exception）
     * 写一个 setAge(int age) 方法，当 age<0 或 age>150 时抛 AgeException
     * 在 main 中 try-catch 测试合法和非法年龄
     */
    static void test3() {
        // TODO: 你的代码

    }

    /*
     * 题4：try-catch-finally
     * 写一个方法演示 finally 的执行时机：
     *   情况1：正常执行
     *   情况2：catch 中有 return
     *   情况3：try 中 return，但 finally 仍然执行
     * 观察 finally 是否总是执行
     */
    static void test4() {
        // TODO: 你的代码

    }
}
