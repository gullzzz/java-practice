package com.practice.c26_reflection;

/**
 * 被召唤的英雄 —— 三道锁全上：
 *   ① 构造器 private
 *   ② 字段 private
 *   ③ 方法 private
 * 反射就是那把能开所有锁的钥匙。
 */
class Hero {
    private String name;
    private int level;

    private Hero(String name, int level) {
        this.name = name;
        this.level = level;
    }

    private void castUltimate() {
        System.out.println(name + "（" + level + " 级）释放了大招！");
    }
}
