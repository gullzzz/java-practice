# 内部类 (Inner Class)

---

## 1. 内部类的概念

**定义**：定义在另一个类内部的类，称为内部类。Java 中有四种内部类：成员内部类、局部内部类、匿名内部类、静态嵌套类。内部类可以访问外部类的私有成员（因为编译后编译器会生成访问桥接方法）。

**解决问题/用途**：当一个类只服务于另一个类、不会在别处使用时，定义为内部类可以：
- 逻辑分组——让辅助类紧贴宿主
- 增强封装——外部类 private 成员对内部类可见
- 更清晰的代码结构——如 GUI 的事件监听器、集合的迭代器

---

## 2. 四种内部类速览

### 2.1 成员内部类（最基本）

```java
public class Outer {
    private String name = "Outer";

    // 成员内部类：不加 static
    class Inner {
        public void print() {
            System.out.println(name);  // 直接访问外部类 private 字段
        }
    }
}

// 使用方式
Outer outer = new Outer();
Outer.Inner inner = outer.new Inner();  // 必须先有外部类对象
```

### 2.2 静态嵌套类（static nested class）

```java
public class Outer {
    private static String name = "Outer";

    // 加了 static：独立于外部类实例
    static class StaticNested {
        public void print() {
            System.out.println(name);  // 只能访问 static 成员
        }
    }
}

// 使用方式：不需要外部类对象
Outer.StaticNested nested = new Outer.StaticNested();
```

### 2.3 局部内部类（方法内定义）

```java
public void someMethod() {
    class LocalInner {  // 定义在方法里，作用域只在方法内
        void say() { System.out.println("Hello"); }
    }
    LocalInner li = new LocalInner();  // 只能在方法内使用
    li.say();
}
```

### 2.4 匿名内部类（最常用）

```java
// 不用显式定义类名，直接 new 接口/抽象类 + 实现体
Runnable task = new Runnable() {
    @Override
    public void run() {
        System.out.println("匿名内部类干活！");
    }
};

// 常见于集合排序
Collections.sort(list, new Comparator<String>() {
    @Override
    public int compare(String a, String b) {
        return a.compareTo(b);
    }
});
```

---

## 3. 四种内部类对比速查表

| 类型 | 关键字 | 位置 | 能访问外部类成员 | 创建方式 |
|------|--------|------|:--:|------|
| 成员内部类 | 无 static | 类体内 | 全部 | `outer.new Inner()` |
| 静态嵌套类 | static | 类体内 | 仅 static | `new Outer.Inner()` |
| 局部内部类 | 无 static | 方法内 | 全部（要求变量 final/effectively final） | 直接在方法内 new |
| 匿名内部类 | 无 | 表达式位置 | 同上 | `new 接口/父类() { ... }` |

---

## 4. 关键规则

- **成员内部类持有外部类引用**（`Outer.this`），这意味着如果内部类对象存活，外部类对象无法被 GC 回收——可能导致内存泄漏。
- **局部/匿名内部类访问的局部变量必须是 final 或 effectively final**（Java 8+ 放宽了 final 修饰符要求，但变量在初始化后不能被修改）。
- **匿名内部类不能有显式构造器**（因为它没有名字）。
- **静态嵌套类不持有外部引用**——如果你不需要访问外部类实例成员，优先用它，避免内存泄漏。

```java
// effectively final：变量初始化后没有被重新赋值
String name = "Duke";
Runnable r = () -> System.out.println(name);  // OK，name 没被改过
// name = "Java";  // 如果加上这行，上面的 Lambda/匿名类就编译错误
```

---

## 5. 为什么需要内部类？（设计价值）

不被内部类的语法表象迷惑，它的核心价值在三个字：**更紧密的耦合**。

- 需要一个回调/事件处理对象（如 GUI 按钮点击） → 匿名内部类
- 需要一个只在某个类内部用的辅助数据结构 → 静态嵌套类
- 需要一个外部类实例的"扩展"对象 → 成员内部类
- 需要一个只在某个方法里临时用一次的类型 → 局部内部类

---

## 6. 面试官视角

| 考察点 | 参考答案 |
|--------|---------|
| Java 有几种内部类？分别怎么用？ | 四种：成员内部类（依赖外部实例）、静态嵌套类（独立于外部实例）、局部内部类（方法内定义）、匿名内部类（一次性 new 接口/抽象类）。 |
| 成员内部类和静态嵌套类的区别？ | 成员内部类持有外部类引用，能访问所有外部成员，创建需外部对象；静态嵌套类不持有外部引用，只能访问静态成员，可独立创建。优先用静态嵌套类。 |
| 匿名内部类能访问的局部变量有什么限制？ | 必须是 final 或 effectively final（初始化后不再修改）。因为 JDK 在编译时会将变量值拷贝到匿名类中，如果变量可变，两者会不一致。 |
| 内部类可能导致什么问题？ | 内存泄漏——成员内部类持有外部引用，如果内部类对象比外部类活得更久（如被缓存/线程持有），外部类无法 GC。 |
| 内部类编译后的 class 文件名格式？ | `OuterClass$InnerClass.class`。匿名内部类用数字编号：`OuterClass$1.class`。 |
| Lambda 和匿名内部类的本质区别？ | Lambda 不生成新的 class 文件（invokedynamic 指令），匿名内部类会生成 `$1.class`；Lambda 的 this 指向外部类，匿名内部类的 this 指向自身。 |
