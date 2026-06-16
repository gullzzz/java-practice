package com.practice.interfaces;

/**
 * 抽象类：有共同属性但不能直接实例化
 */
public abstract class Vehicle {
    private String brand;

    public Vehicle(String brand) {
        this.brand = brand;
    }

    public String getBrand() {
        return brand;
    }

    // 抽象方法（子类必须实现）
    public abstract void start();

    // 普通方法（子类可以继承或重写）
    public void stop() {
        System.out.println(brand + " 熄火了。");
    }
}
