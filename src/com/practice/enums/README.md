# 阶段 12：枚举（Enum）

配套代码：`src/com/practice/enums/TrafficLightDemo.java`

---

## 12.1 枚举概述

**定义**：`enum` 是 Java 中一种特殊的类，**实例数量在编译时就固定死了**。每个常量本质上是一个 `public static final` 的单例对象，JVM 保证类加载时只创建一次，线程安全。

**解决问题/用途**：传统的 `int` 常量或 `String` 常量表示固定状态有两个致命问题——类型不安全（可以传入非法值，编译期不报错）和缺乏命名空间（所有常量混在一起）。enum 在编译期就锁死了所有合法实例，编辑器能自动补全，switch 原生支持，且自带 `values()` 遍历和 `valueOf()` 反查能力。适合表示一组固定的常量：季节、方向、订单状态、错误码等。

```java
// ❌ int 常量的反模式
public static final int RED = 0;
public static final int GREEN = 1;
int light = 999;  // 编译通过！但不是合法颜色

// ✅ enum —— 编译期就限制了合法值
public enum TrafficLight {
    RED, GREEN, YELLOW
}
TrafficLight light = TrafficLight.RED;  // 只有这三种，不可能出现 999
```

---

## 12.2 最简单的枚举

```java
public enum TrafficLight {
    RED, GREEN, YELLOW   // 三个常量，仅此而已
}
```

要点：
- `enum` 关键字声明，不是 `class`
- 常量用逗号分隔，最后一个可加逗号（Java 推荐不加）
- 每个常量本质上是 `public static final` 的单例对象
- **enum 不能被 new**，实例数量在编译时就固定了

---

## 12.3 带字段和构造器的枚举

**定义**：枚举常量可以携带数据——给每个常量绑定字段，通过私有构造器在类加载时初始化。常量列表 `RED(60), GREEN(45), YELLOW(3);` 本质上是调用构造器的语法糖，每个常量对应一个构造器调用。

**解决问题/用途**：纯常量名只能表示"是什么"，无法表示"携带什么属性"。交通灯只有三种颜色，但每种颜色对应的秒数不同。enum 的字段+构造器机制让你给每个常量绑定任意类型的数据——RED 挂 60 秒、GREEN 挂 45 秒——数据跟着常量走，不会出现"RED + 45秒"这种错误组合。

```java
public enum TrafficLight {
    RED(60), GREEN(45), YELLOW(3);  // ← 调用构造器，常量列表以分号结束

    private final int duration;     // 每个常量持有的数据

    TrafficLight(int duration) {    // 构造器（只能是 private，不用写修饰符）
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }
}
```

注意点：
- 常量列表必须在**第一行**
- 如果有字段/方法/构造器，常量列表末尾用 **分号** 分隔
- 构造器**默认且强制 private**——你不能在外面 `new TrafficLight(100)`
- 每个常量在类加载时调用一次构造器（线程安全，JVM 保证）

---

## 12.4 常用方法速查

| 方法 | 说明 | 示例 | 返回值 |
|------|------|------|--------|
| `values()` | 返回所有常量的数组，按声明顺序 | `TrafficLight.values()` | `TrafficLight[]` |
| `valueOf(String name)` | 按名字查找常量，找不到抛 IllegalArgumentException | `TrafficLight.valueOf("RED")` | `TrafficLight` |
| `name()` | 返回常量在代码中的名字 | `TrafficLight.RED.name()` | `"RED"` |
| `ordinal()` | 返回声明位置的序号（从 0 开始） | `TrafficLight.RED.ordinal()` | `0` |

```java
// values() — 遍历所有常量
for (TrafficLight light : TrafficLight.values()) {
    System.out.println(light.name() + " → " + light.getDuration() + "秒");
}
// 输出:
// RED → 60秒
// GREEN → 45秒
// YELLOW → 3秒

// valueOf() — 字符串转枚举
TrafficLight r = TrafficLight.valueOf("RED");  // → TrafficLight.RED
TrafficLight bad = TrafficLight.valueOf("BLUE"); // 💥 IllegalArgumentException
```

> ⚠️ `ordinal()` 依赖声明顺序，维护时容易出错，大多数场景用自定义字段代替它。

---

## 12.5 枚举中定义方法

**定义**：枚举和普通类一样，可以有成员变量和自定义方法。更进一步，每个常量可以拥有自己的方法实现——这叫**常量特定的类主体**（constant-specific class body），通过在常量名后加 `{ }` 书写覆写方法。

**解决问题/用途**：当每个枚举常量的行为不同时，if-else 判断枚举类型是一种坏味道。比如四则运算——PLUS 做加法、MINUS 做减法——在枚举类中定义抽象方法，让每个常量实现自己的版本。这样新增一种运算只需加一个常量值，不用去翻遍所有 if-else。

```java
public enum Operation {
    PLUS {
        public double apply(double a, double b) { return a + b; }
    },
    MINUS {
        public double apply(double a, double b) { return a - b; }
    },
    MULTIPLY {
        public double apply(double a, double b) { return a * b; }
    };

    public abstract double apply(double a, double b);
}

// 使用
double result = Operation.PLUS.apply(3.0, 5.0);  // → 8.0
```

---

## 12.6 枚举实现接口

枚举可以 implements 接口（但不能 extends 类，因为已继承 `Enum`）：

```java
public interface Describable {
    String describe();
}

public enum TrafficLight implements Describable {
    RED(60) {
        @Override
        public String describe() {
            return "停车！剩余 " + getDuration() + " 秒";
        }
    },
    GREEN(45) {
        @Override
        public String describe() {
            return "通行！剩余 " + getDuration() + " 秒";
        }
    },
    YELLOW(3) {
        @Override
        public String describe() {
            return "注意！剩余 " + getDuration() + " 秒";
        }
    };

    private final int duration;
    TrafficLight(int duration) { this.duration = duration; }
    public int getDuration() { return duration; }
}
```

---

## 12.7 枚举实现单例

**定义**：用单元素枚举实现单例——直接声明一个 `INSTANCE` 常量，JVM 保证类加载时创建唯一的实例。这是 `Effective Java` 作者 Josh Bloch 推荐的最佳单例实现方式。

**解决问题/用途**：传统的单例实现——懒汉式/饿汉式/双重检查锁/DCL+volatile——有线程安全问题、反射攻击问题、反序列化问题。枚举单例由 JVM 保证：构造器防反射攻击（反射调用 newInstance 会抛异常）、反序列化不会创建新实例（Enum 的序列化是名字序列化而非对象序列化）、写法只有 3 行。

```java
public enum Config {
    INSTANCE;  // JVM 保证只有一个实例，防反射、防序列化破坏

    private String appName = "MyApp";
    public String getAppName() { return appName; }
}

// 使用
String name = Config.INSTANCE.getAppName();
```

---

## 12.8 enum vs class 对比

| | enum | class |
|------|------|-------|
| 实例数量 | 固定，编译期确定 | 不限，运行时 new |
| 继承 | 默认继承 `java.lang.Enum`，不能再继承其他类 | 可以继承任意类 |
| 实现接口 | ✅ 可以 | ✅ 可以 |
| 构造器 | 强制 private | 任意访问修饰符 |
| 序列化 | JVM 内置支持，不会创建新实例 | 普通序列化机制 |
| switch 支持 | ✅ 原生支持，编译器检查覆盖 | ❌ 不支持 |

---

## 12.9 枚举选型速查

| 场景 | 选择 |
|------|------|
| 固定的一组常量（状态、类型、选项） | ✅ 用 enum |
| 常量需要携带数据 | ✅ enum + 字段 + 构造器 |
| 每个常量行为不同 | ✅ 常量特定类主体 + 抽象方法 |
| 需要单例 | ✅ 单元素 enum（最佳方案） |
| 运行时动态增加 | ❌ enum 不行，用 class |
| 需要继承某个类 | ❌ enum 不能继承 |

---

## 面试官视角

| 常见问法 | 得分点 |
|----------|--------|
| "enum 的构造器为什么是 private？" | 实例数量固定，外部 new 会破坏这个保证。JLS 规定 enum 构造器不能有 public/protected 修饰符 |
| "enum 和普通 class 有什么区别？" | 继承自 `Enum` 类（不能再继承）、实例固定、构造器私有、values()/valueOf() 由编译器自动生成 |
| "enum 能继承其他类吗？能实现接口吗？" | 不能继承（已隐式继承 `java.lang.Enum`），但能实现接口 |
| "为什么用 enum 实现单例最好？" | 防反射攻击（newInstance 对 enum 抛异常）、防序列化破坏、写法极简、线程安全由 JVM 保证 |
| "values() 方法哪来的？" | 编译器自动生成的静态方法，`Enum` 源码里看不到它 |
| "enum 在 switch 中怎么用？" | switch 原生支持 enum，case 直接写常量名（不带类名前缀），编译器会检查是否覆盖所有分支 |
| "枚举能继承枚举吗？" | 不能。所有 enum 默认 final，无法被继承 |
