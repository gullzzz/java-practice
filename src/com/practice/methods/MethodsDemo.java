package com.practice.methods;

/**
 * 阶段3：方法（函数）
 * 涵盖：方法定义、参数传递、返回值、方法重载、可变参数、递归
 */
public class MethodsDemo {
    public static void main(String[] args) {
        System.out.println("========== Java 方法（函数） ==========\n");

        methodBasics();
        parametersAndReturn();
        methodOverloading();
        varargs();
        recursion();
    }

    // ---------- 工具方法 ----------
    static void printDivider() {
        System.out.println("------------------");
    }

    static void methodBasics() {
        System.out.println("--- 1. 方法定义与调用 ---");

        // 调用无参无返回值方法
        greet();

        // 调用有参方法
        greetTo("小明");
        greetTo("小红");

        System.out.println();
    }

    // 无参数，无返回值
    static void greet() {
        System.out.println("Hello, 你好！");
    }

    // 有参数，无返回值
    static void greetTo(String name) {
        System.out.println("你好, " + name + "!");
    }

    // ---------- 2. 参数与返回值 ----------
    static void parametersAndReturn() {
        System.out.println("--- 2. 参数传递与返回值 ---");

        // Java 方法参数传递：基本类型是值传递，引用类型传递的是引用的副本
        int a = 10;
        changeValue(a);
        System.out.println("changeValue后 a = " + a + " (值传递，原值不变)");

        int[] arr = {1, 2, 3};
        changeArray(arr);
        System.out.println("changeArray后 arr[0] = " + arr[0] + " (引用传递，内容被修改)");

        // 有返回值的方法
        int sum = add(15, 27);
        System.out.println("add(15, 27) = " + sum);

        // 链式调用
        int result = multiply(add(2, 3), 4);
        System.out.println("multiply(add(2,3), 4) = " + result);

        System.out.println();
    }

    static void changeValue(int x) {
        x = 999;  // 不影响调用处的变量
    }

    static void changeArray(int[] arr) {
        arr[0] = 999;  // 会影响调用处的数组内容
    }

    static int add(int a, int b) {
        return a + b;
    }

    static int multiply(int a, int b) {
        return a * b;
    }

    // ---------- 3. 方法重载 ----------
    static void methodOverloading() {
        System.out.println("--- 3. 方法重载（Overload） ---");

        // 同名方法，参数列表不同 → 重载
        System.out.println("max(3, 8) = " + max(3, 8));
        System.out.println("max(3.5, 2.7) = " + max(3.5, 2.7));
        System.out.println("max(1, 5, 9) = " + max(1, 5, 9));

        System.out.println();
    }

    static int max(int a, int b) {
        return a > b ? a : b;
    }

    static double max(double a, double b) {
        return a > b ? a : b;
    }

    static int max(int a, int b, int c) {
        return max(max(a, b), c);
    }

    // ---------- 4. 可变参数 ----------
    static void varargs() {
        System.out.println("--- 4. 可变参数（varargs） ---");

        System.out.println("sumAll(1, 2, 3) = " + sumAll(1, 2, 3));
        System.out.println("sumAll(1, 2, 3, 4, 5) = " + sumAll(1, 2, 3, 4, 5));
        System.out.println("sumAll() = " + sumAll());  // 可以传0个参数

        // 可以传数组
        int[] nums = {10, 20, 30};
        System.out.println("sumAll(数组...) = " + sumAll(nums));

        System.out.println();
    }

    // int... 本质是 int[]，但调用更灵活
    static int sumAll(int... numbers) {
        int total = 0;
        for (int n : numbers) {
            total += n;
        }
        return total;
    }

    // ---------- 5. 递归 ----------
    static void recursion() {
        System.out.println("--- 5. 递归 ---");

        // 阶乘 n! = n × (n-1)!
        int n = 5;
        System.out.println("factorial(" + n + ") = " + factorial(n));

        // 斐波那契数列
        System.out.println("fibonacci(10) = " + fibonacci(10));

        // 递归打印
        System.out.print("countdown(" + n + "): ");
        countdown(n);
        System.out.println();
        System.out.println();
    }

    static int factorial(int n) {
        if (n <= 1) return 1;        // 递归终止条件
        return n * factorial(n - 1); // 递归调用
    }

    static int fibonacci(int n) {
        if (n <= 1) return n;
        return fibonacci(n - 1) + fibonacci(n - 2);
    }

    static void countdown(int n) {
        if (n <= 0) {
            System.out.print("Go!");
            return;
        }
        System.out.print(n + " ");
        countdown(n - 1);
    }
}
