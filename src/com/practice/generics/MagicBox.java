package com.practice.generics;

/**
 * 泛型魔法物品箱
 * <T> 表示这个箱子只能存放同一种类型的物品
 */
public class MagicBox<T> {
    private T item;
    private String label;

    public MagicBox(String label) {
        this.label = label;
    }

    public void put(T item) {
        this.item = item;
    }

    public T get() {
        return item;
    }

    public boolean isEmpty() {
        return item == null;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return "[" + label + "] " + (item != null ? item.toString() : "空");
    }
}
