package com.practice.basics;

/**
 * 阶段1：Java 基础语法
 * 涵盖：变量、数据类型、类型转换、运算符、字符串
 */
public class BasicsDemo {
    public static void main(String[] args) {
        System.out.println("========== Java 基础语法 ==========\n");

        // 1. 基本数据类型（8种）
        primitiveTypes();

        // 2. 引用类型 - String
        stringBasics();

        // 3. 类型转换
        typeConversion();

        // 4. 运算符
        operators();

        // 5. 常量和 var（Java 10+）
        constantsAndVar();
    }

    static void primitiveTypes() {
        System.out.println("--- 1. 基本数据类型 ---");

        // 整数类型
        byte b = 127;                    // 1字节, -128 ~ 127
        short s = 32767;                 // 2字节
        int i = 2_147_483_647;           // 4字节（默认）, 可用下划线分隔
        long l = 9_223_372_036_854_775_807L; // 8字节, 后缀 L

        // 浮点类型
        float f = 3.14f;                 // 4字节, 后缀 f
        double d = 3.141592653589793;    // 8字节（默认）

        // 字符和布尔
        char c = 'A';                    // 2字节, Unicode
        boolean flag = true;

        System.out.println("byte: " + b);
        System.out.println("short: " + s);
        System.out.println("int: " + i);
        System.out.println("long: " + l);
        System.out.println("float: " + f);
        System.out.println("double: " + d);
        System.out.println("char: " + c + " (Unicode: " + (int) c + ")");
        System.out.println("boolean: " + flag);
        System.out.println();
    }

    static void stringBasics() {
        System.out.println("--- 2. 字符串 ---");

        String greeting = "Hello";
        String name = "Java";

        // 字符串拼接
        System.out.println(greeting + ", " + name + "!");

        // 常用方法
        System.out.println("长度: " + name.length());
        System.out.println("大写: " + name.toUpperCase());
        System.out.println("小写: " + name.toLowerCase());
        System.out.println("字符索引1: " + name.charAt(1));
        System.out.println("包含'va': " + name.contains("va"));
        System.out.println("替换: " + name.replace('a', 'A'));

        // 字符串比较（用 equals，不用 ==）
        String a = "hello";
        String b = new String("hello");
        System.out.println("equals比较: " + a.equals(b));    // true
        System.out.println("==比较(地址): " + (a == b));     // false
        System.out.println();
    }

    static void typeConversion() {
        System.out.println("--- 3. 类型转换 ---");

        // 自动类型转换（小 → 大）
        int small = 100;
        long big = small;          // int 自动转 long
        double bigger = big;       // long 自动转 double
        System.out.println("自动转换: int(" + small + ") → long(" + big + ") → double(" + bigger + ")");

        // 强制类型转换（大 → 小，可能丢失数据）
        double pi = 3.14159;
        int piInt = (int) pi;      // 小数部分被截断
        System.out.println("强制转换: double(" + pi + ") → int(" + piInt + ")");

        // 字符串 ↔ 数字
        String numStr = "123";
        int parsed = Integer.parseInt(numStr);
        String backToStr = String.valueOf(parsed);
        System.out.println("字符串转数字: \"" + numStr + "\" → " + parsed);
        System.out.println("数字转字符串: " + parsed + " → \"" + backToStr + "\"");
        System.out.println();
    }

    static void operators() {
        System.out.println("--- 4. 运算符 ---");

        // 算术运算符
        int a = 10, b = 3;
        System.out.println("算术: " + a + " + " + b + " = " + (a + b));
        System.out.println("算术: " + a + " - " + b + " = " + (a - b));
        System.out.println("算术: " + a + " * " + b + " = " + (a * b));
        System.out.println("算术: " + a + " / " + b + " = " + (a / b) + " (整数除法)");
        System.out.println("算术: " + a + " % " + b + " = " + (a % b) + " (取余)");
        // 真正除法
        System.out.println("真正除法: 10.0 / 3.0 = " + (10.0 / 3.0));

        // 自增/自减
        int x = 5;
        System.out.println("x++ (先用后加): " + (x++) + " → x现在=" + x);
        System.out.println("++x (先加后用): " + (++x) + " → x现在=" + x);

        // 比较运算符
        System.out.println("5 > 3: " + (5 > 3));
        System.out.println("5 == 3: " + (5 == 3));
        System.out.println("5 != 3: " + (5 != 3));

        // 逻辑运算符 && 短路与, || 短路或
        System.out.println("true && false: " + (true && false));
        System.out.println("true || false: " + (true || false));
        System.out.println("!true: " + (!true));

        // 三元运算符
        int score = 85;
        String result = score >= 60 ? "及格" : "不及格";
        System.out.println("三元: score=" + score + " → " + result);
        System.out.println();
    }

    static void constantsAndVar() {
        System.out.println("--- 5. 常量与 var ---");

        // final 常量（命名规范：全大写+下划线）
        final double PI = 3.14159;
        final int MAX_SIZE = 100;
        System.out.println("常量 PI = " + PI + ", MAX_SIZE = " + MAX_SIZE);

        // Java 10+ 局部变量类型推断 var
        var message = "自动推断为 String";
        var number = 42;           // 推断为 int
        var decimal = 3.14;        // 推断为 double
        System.out.println("var推断: message=" + message + ", number=" + number + ", decimal=" + decimal);
        System.out.println();
    }
}
