# 第3.2章：包装类（Wrapper Classes）

配套代码：`src/com/practice/wrappers/`

---

## 一、定义

Java 为 8 种基本类型各提供了一个对应的**引用类型**，称为包装类：

| 基本类型 | 包装类 | 默认值（字段） |
|----------|--------|---------------|
| `byte` | `Byte` | `null` |
| `short` | `Short` | `null` |
| `int` | `Integer` | `null` |
| `long` | `Long` | `null` |
| `float` | `Float` | `null` |
| `double` | `Double` | `null` |
| `char` | `Character` | `null` |
| `boolean` | `Boolean` | `null` |

核心区别：基本类型存**值**，包装类是**对象**（可以为 `null`）。

## 二、场景/用途

**为什么需要包装类？**

1. **泛型只接受引用类型**：`List<int>` ❌ → `List<Integer>` ✅
2. **需要表示"无值"**：`int score = 0` 分不清是"没考试"还是"考了0分"，`Integer score = null` 明确表示缺考
3. **工具方法**：`Integer.parseInt("42")`、`Double.isNaN(0.0/0)` 等
4. **集合框架**：`HashMap`、`ArrayList` 等只能存对象，不能存基本类型

## 三、自动装箱与自动拆箱（核心机制）

```java
Integer x = 100;        // 自动装箱：编译器生成 Integer.valueOf(100)
int y = x;              // 自动拆箱：编译器生成 x.intValue()

Integer a = 100;
Integer b = 100;
System.out.println(a == b);   // true  —— 缓存池 [-128, 127]

Integer c = 200;
Integer d = 200;
System.out.println(c == d);   // false —— 超出缓存范围，new 了两个对象
```

**缓存机制**：`Integer.valueOf()` 对 -128~127 的值使用缓存数组，返回同一个对象。`Long`、`Short`、`Byte` 也有同范围缓存。`Character` 缓存 0~127。

```java
// 装箱到底调用的是什么？
Integer x = 100;   // → Integer.valueOf(100)  ← 自动装箱
Integer y = new Integer(100);  // ← 手动装箱（已废弃，Java 9+）

// 拆箱
int z = x;         // → x.intValue()  ← 自动拆箱
```

## 四、常用方法速查表

| 方法 | 说明 | 示例 |
|------|------|------|
| `Integer.parseInt(String)` | 字符串→int | `Integer.parseInt("42")` → 42 |
| `Integer.valueOf(String)` | 字符串→Integer | `Integer.valueOf("42")` → Integer(42) |
| `Integer.valueOf(int)` | int→Integer（带缓存） | `Integer.valueOf(100)` |
| `intValue()` | Integer→int | `num.intValue()` |
| `Integer.MAX_VALUE` | 常量：2147483647 | — |
| `Integer.MIN_VALUE` | 常量：-2147483648 | — |
| `Double.parseDouble(String)` | 字符串→double | `Double.parseDouble("3.14")` |
| `Boolean.parseBoolean(String)` | 字符串→boolean | `Boolean.parseBoolean("true")` |
| `Character.isDigit(char)` | 判断是否数字 | `Character.isDigit('5')` → true |
| `Integer.compare(int, int)` | 比较两个int | `Integer.compare(3, 5)` → -1 |

### parseInt vs valueOf 区别

- `parseInt("42")` → 返回 `int` 基本类型
- `valueOf("42")` → 返回 `Integer` 对象（有缓存）

## 五、Demo 代码示例

```java
// 自动装箱拆箱
List<Integer> scores = new ArrayList<>();
scores.add(95);          // int → Integer（自动装箱）
int first = scores.get(0);  // Integer → int（自动拆箱）

// 字符串转换
int num = Integer.parseInt("  42  ".trim());  // → 42
String hex = Integer.toHexString(255);         // → "ff"

// 比较陷阱
Integer i1 = 127, i2 = 127;
System.out.println(i1 == i2);       // true（缓存）
Integer i3 = 200, i4 = 200;
System.out.println(i3 == i4);       // false（超出缓存）
System.out.println(i3.equals(i4));  // true（值比较，正确做法）
```

## 六、面试官视角

| 常见问法 | 考察点 | 参考答案 |
|----------|--------|----------|
| "`Integer` 和 `int` 有什么区别？" | 对象 vs 基本类型、null 处理、内存占用 | `int` 是基本类型，存值，默认 0，占 4 字节；`Integer` 是引用类型，存对象地址，默认 null，占 16 字节（对象头+值）。核心区别：① `Integer` 可为 null，适合表示"无值"；② `Integer` 才能放进泛型集合 |
| "`==` 比较两个 `Integer` 结果是什么？" | 缓存机制 (-128~127)、equals vs == | `==` 比较内存地址，不可靠：-128~127 范围内走 `Integer.valueOf()` 缓存池返回同一对象，`==` 为 true；超出范围则各自 new，`==` 为 false。**正确做法永远用 `equals()`** |
| "装箱拆箱在字节码层面做了什么？" | `Integer.valueOf()` / `intValue()` 调用 | 装箱 `Integer i = 100` → 字节码调用 `Integer.valueOf(100)`；拆箱 `int x = i` → 字节码调用 `i.intValue()`。可以用 `javap -c` 反编译验证 |
| "`parseInt` 和 `valueOf` 返回值类型有什么区别？" | int vs Integer，缓存差异 | `parseInt("42")` 返回 `int` 基本类型；`valueOf("42")` 返回 `Integer` 对象，且在 -128~127 内走缓存池。如果只需要 int 做计算，用 `parseInt` 更直接（少一次拆箱） |
| "为什么泛型不能用基本类型？" | 类型擦除 → 擦到 Object，基本类型不是 Object 子类 | Java 泛型在编译后会擦除类型信息，`List<T>` → `List<Object>`。基本类型不是 `Object` 子类，无法放入。所以有了包装类作为桥梁。这被称为"类型擦除的代价" |
| "`Integer` 缓存池能调吗？" | `-XX:AutoBoxCacheMax=2000` JVM参数可调上限 | 缓存上限默认 127，可通过 `-XX:AutoBoxCacheMax=2000` 调大，但**下限 -128 不可调整**。注意：这只是调 `Integer`，其他包装类（Byte/Short/Long/Character）各有限制，不受此参数影响 |
