package com.practice.exceptions;

/**
 * 阶段9：异常处理
 * 涵盖：try-catch-finally、throws、自定义异常、try-with-resources
 */
public class ExceptionsDemo {
    public static void main(String[] args) {
        System.out.println("========== Java 异常处理 ==========\n");

        tryCatchDemo();
        finallyDemo();
        throwsDemo();
        customException();
        tryWithResources();
    }

    static void tryCatchDemo() {
        System.out.println("--- 1. try-catch 基础 ---");

        // 捕获数组越界
        int[] arr = {1, 2, 3};
        try {
            System.out.println(arr[5]);  // 越界
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("捕获异常: " + e.getClass().getSimpleName());
            System.out.println("  消息: " + e.getMessage());
        }

        // 多catch（具体异常在前，通用异常在后）
        try {
            String s = null;
            System.out.println(s.length());  // NPE
        } catch (NullPointerException e) {
            System.out.println("空指针异常: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("其他异常: " + e.getMessage());
        }

        // 多异常合并（Java 7+）
        try {
            int x = 10 / 0;  // 除零
        } catch (ArithmeticException | IndexOutOfBoundsException e) {
            System.out.println("算术或索引异常: " + e.getClass().getSimpleName());
        }

        System.out.println();
    }

    static void finallyDemo() {
        System.out.println("--- 2. finally（始终执行） ---");

        try {
            System.out.println("执行 try...");
            int x = 10 / 0;
        } catch (ArithmeticException e) {
            System.out.println("执行 catch: " + e.getMessage());
        } finally {
            System.out.println("执行 finally（无论是否异常，都会执行）");
            // 通常用于释放资源（文件流、数据库连接等）
        }

        System.out.println();
    }

    static void throwsDemo() {
        System.out.println("--- 3. throws 声明异常 ---");

        // checked exception：必须处理或声明
        try {
            riskyMethod();
        } catch (InterruptedException e) {
            System.out.println("捕获到 InterruptedException");
        }

        // 对比：运行时异常不强制处理（但最好也捕获）
        try {
            throwRuntimeIfNeeded(0);
        } catch (IllegalArgumentException e) {
            System.out.println("即使未声明 throws，运行时异常也能被捕获: " + e.getMessage());
        }

        System.out.println();
    }

    // throws 声明这个方法可能抛出 checked exception
    static void riskyMethod() throws InterruptedException {
        // 模拟：可能被中断的操作
        System.out.println("正在执行可能有风险的操作...");
        // throw new InterruptedException("模拟中断");
    }

    static void throwRuntimeIfNeeded(int x) {
        if (x == 0) {
            throw new IllegalArgumentException("参数不能为0");  // 运行时异常，不需要 throws
        }
    }

    static void customException() {
        System.out.println("--- 4. 自定义异常 ---");

        try {
            register("");  // 空名字
        } catch (InvalidNameException e) {
            System.out.println("自定义异常: " + e.getMessage());
        }

        try {
            register("正常用户");
        } catch (InvalidNameException e) {
            System.out.println("不应该到这里");
        }

        System.out.println();
    }

    static void register(String name) throws InvalidNameException {
        if (name == null || name.isBlank()) {
            throw new InvalidNameException("用户名不能为空");
        }
        System.out.println("注册成功: " + name);
    }

    static void tryWithResources() {
        System.out.println("--- 5. try-with-resources（自动关闭资源, Java 7+） ---");

        // 传统写法（需要 finally 手动关闭）
        // 新写法：资源实现 AutoCloseable 接口，自动关闭
        try (var resource = new MyResource("数据库连接")) {
            resource.work();
        }  // 自动调用 resource.close()
        // catch 可选
        catch (Exception e) {
            System.out.println("异常: " + e.getMessage());
        }

        System.out.println();
    }
}

/**
 * 自定义异常（继承 Exception = checked exception）
 */
class InvalidNameException extends Exception {
    public InvalidNameException(String message) {
        super(message);
    }
}

/**
 * 实现 AutoCloseable，可被 try-with-resources 自动管理
 */
class MyResource implements AutoCloseable {
    private String name;

    public MyResource(String name) {
        this.name = name;
        System.out.println(name + " 已打开");
    }

    public void work() {
        System.out.println(name + " 工作中...");
    }

    @Override
    public void close() {
        System.out.println(name + " 已自动关闭");
    }
}
