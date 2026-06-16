package com.practice.interfaces;

/**
 * 具体类：继承抽象类 + 实现一个接口
 */
public class Car extends Vehicle implements Flyable {

    public Car(String brand) {
        super(brand);
    }

    // 实现抽象方法
    @Override
    public void start() {
        System.out.println(getBrand() + " 点火启动 vroom vroom!");
    }

    // 实现接口方法
    @Override
    public void fly() {
        System.out.println(getBrand() + " 飞起来了！（未来科技）");
    }

    // 可以重写 default 方法
    @Override
    public void land() {
        System.out.println(getBrand() + " 安全着陆。");
    }
}
