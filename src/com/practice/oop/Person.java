package com.practice.oop;

/**
 * Person 类 —— 展示封装、构造器、this、实例方法 vs 静态方法
 */
public class Person {
    // 1. 字段（私有 → 封装）
    private String name;
    private int age;

    // 静态变量（属于类，所有实例共享）
    private static int totalCount = 0;

    // 2. 无参构造器
    public Person() {
        this("未命名", 0);  // 调用另一个构造器
    }

    // 3. 全参构造器
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
        totalCount++;  // 每创建一个对象，计数+1
    }

    // 4. Getter / Setter（控制访问）
    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name != null && !name.isBlank()) {
            this.name = name;
        }
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        if (age >= 0 && age <= 150) {
            this.age = age;
        }
    }

    // 5. 实例方法
    public void introduce() {
        System.out.println("你好，我叫" + name + "，今年" + age + "岁。");
    }

    public void birthday() {
        age++;
        System.out.println(name + " 过生日了！现在 " + age + " 岁。");
    }

    // 6. 静态方法（属于类，不依赖实例）
    public static int getTotalCount() {
        return totalCount;
    }

    // toString：返回对象的字符串表示
    @Override
    public String toString() {
        return "Person{name='" + name + "', age=" + age + "}";
    }

    // equals：判断两个对象内容是否相等
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Person other = (Person) obj;
        return age == other.age && name.equals(other.name);
    }
}
