# 阶段 10：Java 17 新特性

配套代码：`src/com/practice/java17/Shape.java`、`Java17Demo.java`

---

## 10.1 Record（记录类）— Java 16 final

**定义**：Record 是一种特殊的类，专用于**不可变数据的声明**。用一行代码声明：`public record Circle(double radius) {}`，编译器自动生成全参构造器、字段访问器方法（`radius()` 而非 `getRadius()`）、equals()、hashCode() 和 toString()。Record 是 final 的，不能被继承，字段也是 final 不可修改。

**解决问题/用途**：Java 中定义一个"纯粹的数据载体"极啰嗦——需要写构造器、getter、equals()、hashCode()、toString()，几十行代码。而 DTO、值对象、配置项、返回结果等场景极其常见。Record 把数据载体的定义浓缩到一行声明，消除了大量样板代码，同时保证了不可变性（线程安全、可放心作为 Map key）。

**不可变数据的简洁声明**：

```java
// 一行搞定：构造器 + 字段 + getter + equals + hashCode + toString
public record Circle(double radius) implements Shape {
    @Override
    public double area() {
        return Math.PI * radius * radius;
    }
}

// 使用
var c = new Circle(5.0);
System.out.println(c.radius());    // 访问器（没有 get 前缀）
System.out.println(c);             // Circle[radius=5.0]
// c.radius = 10;                  // ❌ 不可修改
```

自动生成：
- 全参构造器
- 每个字段的访问器方法（字段名本身，如 `radius()`）
- `equals()`、`hashCode()`、`toString()`

适用场景：DTO、值对象、配置数据、方法的返回多值。

---

## 10.2 Sealed Class（密封类）— Java 17 final

**定义**：Sealed Class（密封类/接口）通过 `sealed` 关键字和 `permits` 子句，明确声明哪些类可以继承或实现它。被密封的类型就像"封闭俱乐部"——只有白名单上的成员能加入。实现类必须声明为 `final`、`sealed` 或 `non-sealed`。

**解决问题/用途**：继承的开放性是一把双刃剑——任何类都能 extends 你的类，导致难以预测的非法子类。比如 Shape 接口只应该被 Circle、Rectangle、Triangle 实现，但普通接口允许任何人实现。Sealed Class 限制了继承范围，让编译器知道"所有可能的子类"，从而在 switch 中实现**穷尽检查**——漏掉一个子类分支编译器就报错，彻底消除"忘了处理某个类型"的 bug。

**限制谁可以继承/实现**：

```java
// 只允许 Circle、Rectangle、Triangle 实现
public sealed interface Shape
    permits Circle, Rectangle, Triangle { }

// 实现类必须是 final、sealed 或 non-sealed
final record Circle(double radius) implements Shape { }
final record Rectangle(double w, double h) implements Shape { }
```

价值：编译器知道所有子类，可以在 switch 中进行**穷尽检查**，防止漏掉分支。

---

## 10.3 Text Blocks（文本块）— Java 15 final

**定义**：Text Blocks 使用 `"""` 三个双引号括起多行字符串，内容保留原始格式的换行和缩进（自动去除公共前导空格）。内层双引号不需要转义（除非三个连续），让嵌入的 JSON、SQL、HTML 等文本保持和代码中一样的格式化呈现。

**解决问题/用途**：Java 传统字符串写多行文本极痛苦——每行要加 `\n`、双引号要 `\"` 转义、拼接符 `+` 不断。一个简单的 JSON 字符串变得面目全非、可读性极差。Text Blocks 让多行文本在代码中"所见即所得"，大大提升嵌入 SQL、JSON、HTML 的可读性和可维护性。

**多行字符串**，用 `"""` 包围：

```java
// 传统
String json = "{\n  \"name\": \"张三\",\n  \"age\": 25\n}";

// Text Block（清晰直观）
String json = """
    {
      "name": "张三",
      "age": 25
    }
    """;

// SQL 也实用
String sql = """
    SELECT id, name, age
    FROM users
    WHERE age >= 18
    ORDER BY name
    """;
```

- 自动去除公共前导空格
- 不用转义 `"`（除非三个连续的 `"""`）
- 内部换行就是真实换行

---

## 10.4 Pattern Matching for instanceof（实例模式匹配）— Java 16 final

**定义**：在 `instanceof` 判断后，如果结果为 true，可以直接声明一个变量并完成类型转换，不需要额外的强转语句。变量作用域仅限于 `if` 块内。

**解决问题/用途**：传统写法 `if (obj instanceof String) { String s = (String) obj; ... }` 是 Java 中最常见的重复代码模式——先判断类型，再强转，再用。Pattern Matching 将三步合并为一步：`if (obj instanceof String s)`，消除冗余的强转和多余变量声明，写起来快读起来也快。

**一步完成判断 + 类型转换**：

```java
// 传统写法
if (obj instanceof String) {
    String s = (String) obj;
    System.out.println(s.toUpperCase());
}

// 新模式匹配（一步到位）
if (obj instanceof String s) {
    System.out.println(s.toUpperCase());
}

// 加上条件
if (obj instanceof String s && s.length() > 3) {
    System.out.println("长字符串: " + s);
}
```

变量 `s` 的作用域在 if 块内。

---

## 10.5 Switch 表达式（Java 14 final）

**定义**：增强的 switch 使用箭头 `->` 语法（无需 break，无穿透），并可以作为表达式直接返回一个值。在代码块中使用 `yield` 关键字来返回值。支持多个值合并到一个 case：`case 1, 2, 3 ->`。

**解决问题/用途**：传统 switch 的穿透机制虽是特性但更是 bug 来源——95% 的 case 后面都写 break，忘了就出事。箭头语法从根本上消灭了穿透问题。switch 作为表达式让"根据值决定结果"的赋值更简洁：以前需要先声明变量，再在 switch 中赋值，现在一行 switch 直接赋值给变量。

```java
// switch 作为表达式直接赋值
String type = switch (day) {
    case 1, 2, 3, 4, 5 -> "工作日";
    case 6, 7 -> "周末";
    default -> "无效";
};

// 代码块 + yield 返回值
String desc = switch (day) {
    case 5 -> {
        System.out.println("周五了！");
        yield "TGIF";
    }
    default -> "普通的一天";
};
```

- 箭头 `->` 语法，**没有穿透**（不需要 break）
- 用 `yield` 从代码块返回值

---

## 10.6 其他实用特性

### var 局部变量类型推断（Java 10）

**定义**：`var` 关键字让编译器根据右侧赋值自动推断局部变量的类型，编译后类型就确定了（不是动态类型）。

**解决问题/用途**：减少冗长的类型声明——`HashMap<String, List<Integer>> map = new HashMap<>()` 类型信息重复两遍，用 `var` 只需写一遍。

```java
var list = new ArrayList<String>();  // 编译器推断类型
var map = Map.of("key", 123);
```

### 不可变集合工厂（Java 9）

**定义**：`List.of()`、`Set.of()`、`Map.of()` 一行创建不可修改的集合实例。

**解决问题/用途**：替代以前需要 `Arrays.asList()` + `Collections.unmodifiableList()` 两行的创建方式，代码更紧凑且自动保证不可变。

```java
List.of("A", "B", "C");
Set.of("X", "Y");
Map.of("k1", 1, "k2", 2);
```

### 增强的 NPE 信息（Java 14）

**定义**：当发生 NullPointerException 时，JVM 会明确指出"哪个变量是 null"，而非以前只给行号。

**解决问题/用途**：传统的 NPE 只有行号和异常名，需要看代码和调试才能确定哪个变量为 null。增强版直接告诉你 `"Cannot invoke ... because 'b' is null"`，定位问题速度大幅提升。

```java
// a.b.c.d() 如果 b 是 null，JVM 会明确提示：
// "Cannot invoke ... because 'b' is null"
```

### 增强的 Random（Java 17）

```java
var random = RandomGenerator.getDefault();
random.nextInt(100);
```

---

## 10.7 Java 版本演进速览（8→17）

| 版本 | 关键特性 |
|------|---------|
| Java 8 | Lambda、Stream、Optional |
| Java 9 | 模块系统、`List.of()` |
| Java 10 | `var` 类型推断 |
| Java 11 | 字符串增强方法 |
| Java 14 | Switch 表达式、Records 预览 |
| Java 15 | Text Blocks |
| Java 16 | Records 正式、Pattern Matching 正式 |
| **Java 17** | **LTS**，Sealed Classes、增强 PRNG |

Java 17 是继 11 之后的**长期支持版本（LTS）**，推荐用于生产环境。
