package com.practice.polymorphism;

/**
 * 多态 练习题（共4题）
 *
 * 运行方式：
 *   javac -encoding UTF-8 -d out src/com/practice/polymorphism/*.java
 *   java -cp out com.practice.polymorphism.PolymorphismTest
 */
public class PolymorphismTest {
    public static void main(String[] args) {
        System.out.println("========== 多态 练习题 ==========\n");
        test1();
        test2();
        test3();
        test4();
    }

    /*
     * 题1：多态数组
     * 用本包的 Animal/Dog/Cat 类：
     *   创建 Animal[] 数组，放入 Dog 和 Cat 各两个
     *   遍历数组调用 makeSound()，验证多态行为
     */
    static void test1() {
        // TODO: 你的代码

    }

    /*
     * 题2：向下转型
     * 创建 Animal a = new Dog("小黑")
     * 用 instanceof 检查后向下转型为 Dog，调用 wagTail()
     * 再尝试把 Cat 强转为 Dog，捕获 ClassCastException
     */
    static void test2() {
        // TODO: 你的代码

    }

    /*
     * 题3：字段无多态验证
     * 用本包的 Parent/Child 类：
     *   Parent p = new Child();
     *   打印 p.name，观察输出的是 Parent 还是 Child 的字段
     *   解释原因（写在注释里）
     */
    static void test3() {
        // TODO: 你的代码

    }

    /*
     * 题4：方法参数多态
     * 写一个静态方法 processAnimal(Animal a)，接收Animal参数
     *   方法内：调用 a.makeSound()
     *   如果是 Dog，额外让它摇尾巴
     *   如果是 Cat，额外让它爬树
     * 分别传入 Dog 和 Cat 测试
     */
    static void test4() {
        // TODO: 你的代码

    }
}
