package com.practice.java17;

import java.util.random.RandomGenerator;

/**
 * 阶段10：Java 17 新特性
 * 涵盖：Record、Sealed Classes、Text Blocks、Switch表达式增强、Pattern Matching
 */
public class Java17Demo {
    public static void main(String[] args) {
        System.out.println("========== Java 17 新特性 ==========\n");

        recordDemo();
        sealedClassDemo();
        textBlocks();
        patternMatching();
        otherFeatures();
    }

    static void recordDemo() {
        System.out.println("--- 1. Record（记录类） ---");

        var circle = new Circle(5.0);
        var rect = new Rectangle(4, 6);

        // 自动生成的访问器方法（没有 get 前缀，直接是字段名）
        System.out.println("circle.radius: " + circle.radius());
        System.out.println("rect.width: " + rect.width() + ", rect.height: " + rect.height());

        // 自动生成 toString
        System.out.println("circle: " + circle);
        System.out.println("rect: " + rect);

        // 自动生成 equals（按值比较）
        var anotherCircle = new Circle(5.0);
        System.out.println("circle.equals(anotherCircle): " + circle.equals(anotherCircle));

        // Record 不可变，没有 setter
        // circle.radius = 10; // ❌ 编译错误

        System.out.println();
    }

    static void sealedClassDemo() {
        System.out.println("--- 2. Sealed Class（密封类/接口） ---");

        Shape[] shapes = {
            new Circle(3),
            new Rectangle(5, 7),
            new Triangle(4, 6)
        };

        for (Shape s : shapes) {
            // sealed 类配合 instanceof 模式匹配
            String desc;
            if (s instanceof Circle c) {
                desc = String.format("圆形(r=%.1f)", c.radius());
            } else if (s instanceof Rectangle r) {
                desc = String.format("矩形(w=%.1f, h=%.1f)", r.width(), r.height());
            } else if (s instanceof Triangle t) {
                desc = String.format("三角形(b=%.1f, h=%.1f)", t.base(), t.height());
            } else {
                desc = "未知形状";  // sealed 保证这行不会执行
            }
            System.out.println(desc + " → 面积=" + String.format("%.2f", s.area()));
        }

        System.out.println();
    }

    static void textBlocks() {
        System.out.println("--- 3. Text Blocks（文本块，Java 15+） ---");

        // 传统方式
        String oldJson = "{\n  \"name\": \"张三\",\n  \"age\": 25\n}";
        System.out.println("传统写法:\n" + oldJson);

        // Text Block（用 """ 包围）
        String json = """
            {
              "name": "张三",
              "age": 25,
              "skills": ["Java", "Python"]
            }
            """;
        System.out.println("\nText Block:\n" + json);

        // SQL 示例
        String sql = """
            SELECT id, name, age
            FROM users
            WHERE age >= 18
            ORDER BY name ASC
            """;
        System.out.println("SQL:\n" + sql);

        System.out.println();
    }

    static void patternMatching() {
        System.out.println("--- 4. Pattern Matching（模式匹配） ---");

        Object obj = "Hello, Java 17!";

        // 传统 instanceof
        if (obj instanceof String) {
            String s = (String) obj;  // 需要强制转换
            System.out.println("传统写法: " + s.toUpperCase());
        }

        // Java 16+ 模式匹配：一步完成判断+转换
        if (obj instanceof String s) {
            System.out.println("模式匹配: " + s.toUpperCase());
        }

        // 更复杂的例子
        Object[] items = {42, "Hello", 3.14, "World"};
        for (Object item : items) {
            if (item instanceof String s && s.length() > 3) {
                System.out.println("长字符串: " + s);
            } else if (item instanceof Integer i) {
                System.out.println("整数: " + (i * 2));
            }
        }

        System.out.println();
    }

    static void otherFeatures() {
        System.out.println("--- 5. 其他实用特性 ---");

        // var 局部变量类型推断（Java 10）
        var list = java.util.List.of("A", "B", "C");
        var map = java.util.Map.of("key", 123);
        System.out.println("var推断: list=" + list + ", map=" + map);

        // List.of / Set.of / Map.of 不可变集合（Java 9）
        // 简洁创建小集合

        // 增强的 Random（Java 17）
        var random = RandomGenerator.getDefault();
        System.out.println("随机int: " + random.nextInt(100));
        System.out.println("随机double: " + String.format("%.3f", random.nextDouble()));

        // NPE 增强信息（Java 14）
        // 当链式调用中出现 NPE，JVM 会指出具体哪个变量是 null
        // 例如: obj.getA().getB().getC() 会精确提示哪一步返回了 null

        System.out.println();
    }
}
