package com.practice.java17;

/**
 * 密封接口：只允许特定的类/记录实现（Java 17 final）
 */
public sealed interface Shape
    permits Circle, Rectangle, Triangle {
    double area();
}

/**
 * 记录类（Record）：不可变数据载体（Java 14+，Java 16 final）
 * 自动生成：构造器、getter、equals、hashCode、toString
 */
record Circle(double radius) implements Shape {
    @Override
    public double area() {
        return Math.PI * radius * radius;
    }
}

record Rectangle(double width, double height) implements Shape {
    @Override
    public double area() {
        return width * height;
    }
}

record Triangle(double base, double height) implements Shape {
    @Override
    public double area() {
        return 0.5 * base * height;
    }
}
