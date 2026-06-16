package com.practice.java17;

/**
 * Java 17 练习题（共4题）
 *
 * 运行方式：
 *   javac --enable-preview --release 17 -d out src/com/practice/java17/*.java
 *   java --enable-preview -cp out com.practice.java17.Java17Test
 */
public class Java17Test {
    public static void main(String[] args) {
        System.out.println("========== Java 17 练习题 ==========\n");
        test1();
        test2();
        test3();
        test4();
    }

    /*
     * 题1：Record练习
     * 定义一个 record Point(int x, int y)
     * 创建两个Point对象，测试 toString、equals、访问器方法
     * 验证 record 是不可变的
     */
    static void test1() {
        // TODO: 你的代码

    }

    /*
     * 题2：Sealed Class
     * 创建密封接口 Payment，只允许 CreditCard 和 WeChat 两种实现
     * 实现类分别重写 pay() 方法
     * 在 main 中用 instanceof 模式匹配处理不同类型
     */
    static void test2() {
        // TODO: 你的代码

    }

    /*
     * 题3：Switch表达式
     * 用 switch 表达式（→ 箭头语法）把月份数字 1-12 转换为对应季节：
     *   12,1,2→冬季  3,4,5→春季  6,7,8→夏季  9,10,11→秋季
     * 测试月份 3、7、10、12
     */
    static void test3() {
        // TODO: 你的代码

    }

    /*
     * 题4：Text Block
     * 用 Text Block（"""）写一段 JSON 格式的配置信息
     * 包含：name、version、author 三个字段
     * 再用 Text Block 写一段 SQL 查询语句
     */
    static void test4() {
        // TODO: 你的代码

    }
}
