package com.practice.basics;

/**
 * 基础语法 练习题（共4题）
 *
 * 运行方式：
 *   javac -encoding UTF-8 -d out src/com/practice/basics/*.java
 *   java -cp out com.practice.basics.BasicsTest
 */
public class BasicsTest {
    public static void main(String[] args) {
        System.out.println("========== 基础语法 练习题 ==========\n");
        test1();
        test2();
        test3();
        test4();
    }

    /*
     * 题1：变量交换
     * 声明两个 int 变量 a=10, b=20，在不使用第三个变量的前提下交换它们的值
     * 打印交换前后的结果
     */
    static void test1() {
        // TODO: 你的代码

    }

    /*
     * 题2：类型转换
     * 将 double 类型的 3.14159 强制转换为 int，观察精度丢失
     * 再将这个 int 自动转换为 double，观察结果变化
     * 打印每一步的结果
     */
    static void test2() {
        // TODO: 你的代码

    }

    /*
     * 题3：字符串拼接性能
     * 用 String 做 10000 次字符串拼接（+），记录耗时
     * 再用 StringBuilder 做 10000 次拼接，记录耗时
     * 比较两者的时间差
     * （提示：System.currentTimeMillis() 获取时间戳）
     */
    static void test3() {
        // TODO: 你的代码

    }

    /*
     * 题4：运算符练习
     * 给出一个三位整数 153，分别取出它的百位、十位、个位数字
     * 判断它是不是水仙花数（每位数字的立方和等于原数）
     * 打印每一步的中间结果
     */
    static void test4() {
        // TODO: 你的代码

    }
}
