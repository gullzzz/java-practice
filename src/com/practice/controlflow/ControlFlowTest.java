package com.practice.controlflow;

/**
 * 控制流程 练习题（共4题）
 *
 * 运行方式：
 *   javac -encoding UTF-8 -d out src/com/practice/controlflow/*.java
 *   java -cp out com.practice.controlflow.ControlFlowTest
 */
public class ControlFlowTest {
    public static void main(String[] args) {
        System.out.println("========== 控制流程 练习题 ==========\n");
        test1();
        test2();
        test3();
        test4();
    }

    /*
     * 题1：成绩等级
     * 输入一个0~100的分数，用if-else判断等级：
     *   90-100 → A   80-89 → B   70-79 → C   60-69 → D   <60 → E
     * 打印结果
     */
    static void test1() {
        // TODO: 你的代码（设置 score 变量并判断）

    }

    /*
     * 题2：星期转换
     * 用 switch 把数字 1-7 转换为对应的星期名称（中文）
     * 数字不在 1-7 时输出"无效"
     */
    static void test2() {
        // TODO: 你的代码

    }

    /*
     * 题3：九九乘法表
     * 用嵌套 for 循环打印 9x9 乘法表，格式：
     *   1x1=1  1x2=2  ...  1x9=9
     *   2x1=2  2x2=4  ...  2x9=18
     *   ...
     */
    static void test3() {
        // TODO: 你的代码

    }

    /*
     * 题4：猜数字游戏
     * 用 while 循环模拟猜数字：预设一个1-100的目标数
     * 循环让程序"猜"（每次猜范围的中间值），直到猜对
     * 打印每次猜测和调整范围的过程
     */
    static void test4() {
        // TODO: 你的代码

    }
}
