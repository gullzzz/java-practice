# 4.1 Lambda 表达式

## 一、为什么需要 Lambda？

Java 一直以来的痛点：想传一段"行为"给方法，得先写一个匿名内部类，5 行代码里只有 1 行是真正干活的：

```java
// 想对一个字符串列表排序——但 Collections.sort 需要传一个 Comparator
Collections.sort(list, new Comparator<String>() {   // 4 行样板代码
    @Override
    public int compare(String a, String b) {
        return a.length() - b.length();              // 只有这行是真正的逻辑
    }
});
```

Lambda 把上面的 5 行压缩为 1 行：

```java
Collections.sort(list, (a, b) -> a.length() - b.length());
```

> **一句话定义：** Lambda 是一个**可以当参数传递的匿名函数**——它不是对象，但 JVM 把它当函数式接口的实例使用。核心价值：**把行为当数据传递，消灭样板代码。**

---

## 二、Lambda 语法——3 种形态

```
(参数列表) -> { 方法体 }
```

| 形态 | 示例 | 适用场景 |
|------|------|----------|
| **无参** | `() -> System.out.println("hi")` | 不需要输入，只执行动作 |
| **单参** | `name -> name.length()` | 只有一个参数时可省略括号 |
| **多参** | `(a, b) -> a - b` | 多个参数必须括号 |

**方法体的两种写法：**

| 写法 | 示例 | 规则 |
|------|------|------|
| **表达式体** | `(a, b) -> a - b` | 方法体只有一条语句时，省略 `{}` 和 `return`，直接写表达式 |
| **语句块体** | `(a, b) -> { int r = a - b; return r; }` | 多条语句或多行逻辑时，必须用 `{}`，`return` 不能省 |

---

### 方法引用（`::`）— Lambda 的进一步简写

**定义：** 当 Lambda 体里只是**原封不动地调用一个已存在的方法**（参数直接传进去、结果直接返回），就可以用 `::` 省略掉参数和箭头。

**等价关系（三者完全一样）：**

```java
(a, b) -> a + b                 // 1. 直接写表达式
(a, b) -> Integer.sum(a, b)     // 2. Lambda 调用静态方法 sum
Integer::sum                    // 3. 方法引用：把"参数原样传给 sum"缩写掉
```

> 因为 `Integer.sum(int a, int b)` 是静态方法，作用就是返回 `a + b`。`Integer::sum` 在 `ConcurrentHashMap.merge` 里常用来做原子累加。

**四种形式速查表：**

| 形式 | 写法 | 等价 Lambda | 典型例子 |
|------|------|-------------|----------|
| 类::静态方法 | `Integer::sum` | `(a,b) -> Integer.sum(a,b)` | `Math::max`、`Integer::sum` |
| 对象::实例方法 | `obj::method` | `(x) -> obj.method(x)` | `System.out::println` |
| 类::实例方法 | `String::length` | `(s) -> s.length()` | `String::length` |
| 类::new（构造引用） | `ArrayList::new` | `() -> new ArrayList()` | 配合 Stream 收集 |

**一句话判断能不能用 `::`：** 看 Lambda 的入参，是不是**正好被原样传给了那个方法**。是，就能缩写；中间还夹了别的运算（如 `(a,b) -> a*b + 1`），就不能用。

---

## 三、函数式接口 — Lambda 的"容器"

Lambda 本身没有类型——`var x = (a, b) -> a - b;` **编译报错**。它必须赋值给一个**函数式接口**。

**函数式接口 = 只定义一个抽象方法的接口。** 标了 `@FunctionalInterface` 注解（可选但推荐）。

```java
@FunctionalInterface
interface Comparator<T> {
    int compare(T o1, T o2);     // 唯一的抽象方法
}
// Lambda 的 (a,b) -> a-b 自动匹配到这个抽象方法
```

> **Lambda 和函数式接口的关系：** Lambda 表达式 = 函数式接口的匿名实现实例。编译器通过**目标类型推断**决定 Lambda 匹配哪个接口——赋值给 `Comparator` 就是 compare 的实现，赋值给 `Runnable` 就是 run 的实现。

---

## 四、java.util.function 四大核心接口

不需要每次自己定义接口——JDK 内置了 43 个函数式接口。但 90% 的场景只用这 4 个：

| 接口 | 抽象方法 | 参数 | 返回值 | 用途 | Lambda 形状 |
|------|----------|------|--------|------|------------|
| ⭐ `Predicate<T>` | `boolean test(T t)` | T | boolean | 条件判断、过滤 | `t -> t > 0` |
| ⭐ `Function<T, R>` | `R apply(T t)` | T | R | 转换/映射 | `s -> s.length()` |
| ⭐ `Consumer<T>` | `void accept(T t)` | T | void | 消费/打印/存储 | `s -> System.out.println(s)` |
| ⭐ `Supplier<T>` | `T get()` | 无 | T | 工厂/懒加载/提供值 | `() -> new User()` |

### Demo

```java
// Predicate: 判读
Predicate<String> isLong = s -> s.length() > 5;
isLong.test("hello");   // false
isLong.test("hello world");  // true

// Function: 转换
Function<String, Integer> lengthFn = s -> s.length();
lengthFn.apply("hello");  // 5

// Consumer: 消费
Consumer<String> printer = s -> System.out.println(s);
printer.accept("冰霜法杖");  // 打印: 冰霜法杖

// Supplier: 提供
Supplier<Double> randomSupplier = () -> Math.random();
randomSupplier.get();  // 0.7342...
```

---

## 五、变量捕获 — Lambda 能"看见"什么

Lambda 体里可以使用外部的变量，但有规则：

| 变量类型 | 能否访问 | 能否修改 |
|----------|----------|----------|
| 局部变量 | ✅ | ❌ 必须是 effectively final（赋值一次不变） |
| 实例字段 | ✅ | ✅ 无限制 |
| 静态字段 | ✅ | ✅ 无限制 |

```java
String prefix = "物品: ";  // effectively final
Consumer<String> printer = s -> System.out.println(prefix + s);
printer.accept("冰霜法杖");  // 物品: 冰霜法杖

// prefix = "商品: ";  // 如果加了这行，上面的 Lambda 编译报错——prefix 不再是 effectively final
```

---

## 六、Lambda vs 匿名内部类 — 两个关键区别

| | Lambda | 匿名内部类 |
|------|--------|------------|
| **this 指向** | 外层类的 this | 匿名类自身的 this |
| **生成 .class 文件** | 不生成（invokedynamic） | 生成（Outer$1.class） |
| **适用范围** | 只能用于函数式接口 | 可用于任何接口/抽象类 |

> **记法：** 一个接口只有一个抽象方法 → 用 Lambda。接口有多个方法或有字段 → 匿名内部类。Lambda 不是语法糖——底层用 `invokedynamic` 指令，不生成额外的 class 文件，性能更好。

---

## 七、面试官视角

| 常见问法 | 回答要点 |
|---------|---------|
| **Lambda 是什么？底层原理？** | 匿名函数，`invokedynamic` 指令 + `LambdaMetafactory`，运行时生成实现类，不产生 .class 文件 |
| **函数式接口是什么？** | 只有一个抽象方法的接口，`@FunctionalInterface` 注解，是 Lambda 的类型载体 |
| **java.util.function 有哪几个核心接口？** | Predicate（判）、Function（转）、Consumer（消）、Supplier（供）——从输入输出就能区分 |
| **为什么 Lambda 里不能修改局部变量？** | 局部变量在栈上、Lambda 可能在不同线程执行，捕获的是值副本——强制 effectively final 防止不一致 |
| **方法引用是什么？** | Lambda 的简写：`String::length` = `s -> s.length()`，`System.out::println` = `s -> System.out.println(s)` |
| **Lambda 和匿名内部类的区别？** | this 指向不同、生成机制不同（invokedynamic vs class 文件）、Lambda 只能用于函数式接口 |

---

> 这些知识足够你开始挑战了，动手吧！
