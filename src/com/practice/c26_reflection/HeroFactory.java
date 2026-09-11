package com.practice.c26_reflection;

import java.io.File;
import java.lang.reflect.*;

/**
 * 迷你对象工厂 —— 只靠一个字符串类名，召唤出对象并摆弄它。
 * 这就是 Spring IoC 干活的缩影。
 */
public class HeroFactory {
    public static void main(String[] args) throws Exception {
        String className = "com.practice.c26_reflection.Hero";   // 手上只有这个字符串

        // TODO ① 靠 className 拿到 Hero 的"说明书"（Class 对象）
        Class<?>c1=Class.forName(className);

        // TODO ② 从说明书里找到构造器，造出一个 Hero 实例
        //      ⚠️ 它的构造器上了锁（private）
        Constructor<?> ctor= c1.getDeclaredConstructor(String.class,int.class);
        ctor.setAccessible(true);
        Hero h1=(Hero) ctor.newInstance("射手",6);


        // TODO ③ 把它的 name 改成 "暗影法师"、level 改成 10
        //      ⚠️ 这两个字段也上了锁
        Field f1=c1.getDeclaredField("name");
        Field f2=c1.getDeclaredField("level");
        f1.setAccessible(true);
        f2.setAccessible(true);
        f1.set(h1,"暗影法师");
        f2.set(h1,10);

        // TODO ④ 让它释放大招（castUltimate），控制台要打印出大招台词
        //      ⚠️ 这个方法同样上了锁
        Method m= c1.getDeclaredMethod("castUltimate");
        m.setAccessible(true);
        m.invoke(h1);
    }
}
