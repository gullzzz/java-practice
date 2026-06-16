package com.practice.interfaces;

/**
 * 接口 练习题（共4题）
 *
 * 运行方式：
 *   javac -encoding UTF-8 -d out src/com/practice/interfaces/*.java
 *   java -cp out com.practice.interfaces.InterfaceTest
 */
public class InterfaceTest {
    public static void main(String[] args) {
        System.out.println("========== 接口 练习题 ==========\n");
        test1();
        test2();
        test3();
        test4();
    }

    /*
     * 题1：USB设备
     * 创建一个 USB 接口（有 connect() 和 disconnect() 方法）
     * 创建 Mouse 和 Keyboard 类实现 USB 接口
     * 在 main 中创建两个设备并调用它们的方法
     */
    static void test1() {
        // TODO: 你的代码

    }

    /*
     * 题2：default方法
     * 创建一个 Printer 接口，包含：
     *   抽象方法：print(String content)
     *   default方法：printLine() 打印一条分隔线"-----"
     * 实现类只实现 print，自动获得 printLine 功能
     */
    static void test2() {
        // TODO: 你的代码

    }

    /*
     * 题3：多接口实现
     * 创建两个接口：Flyable（fly方法）、Swimmable（swim方法）
     * 创建 Duck 类同时实现这两个接口
     * 用接口类型变量接收 Duck 对象，验证多态
     */
    static void test3() {
        // TODO: 你的代码

    }

    /*
     * 题4：接口回调
     * 创建一个 Button 类，内部有 OnClickListener 接口
     * Button 有 click() 方法，点击时调用监听器的 onClick()
     * 演示设置监听器并触发点击
     */
    static void test4() {
        // TODO: 你的代码

    }
}
