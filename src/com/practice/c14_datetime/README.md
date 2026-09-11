# 日期时间 (java.time)

`java.time` 包（Java 8+）是现代 Java 日期时间 API，线程安全、不可变、设计清晰。彻底取代了 `java.util.Date` 和 `java.util.Calendar`。

---

## 1. 为什么需要 java.time？（旧 API 的血泪史）

Java 1.0 就有了 `java.util.Date`，但它身上有三个"原罪"，JDK 设计者公开承认这是 Java 早期最糟糕的设计之一：

### 问题一：反直觉的 API 设计

```java
Date d = new Date(2026, 5, 20);     // 你以为是 2026-05-20？
System.out.println(d.getYear());    // 输出 126 —— 年份要减 1900！
System.out.println(d.getMonth());   // 输出 5 —— 月份从 0 开始，实际是 6 月！
```

`getYear()` 返回的是"当前年份 - 1900"，`getMonth()` 从 0 开始。这不是 bug，是设计失误——写代码像在做数学题。到了 `Calendar` 时代也没好到哪去：

```java
Calendar c = Calendar.getInstance();
c.set(Calendar.MONTH, 5);  // 你以为设的是5月？不，这是6月（January=0）
```

### 问题二：可变对象（Mutable）→ 线程不安全

```java
Date d = new Date();
someMethod(d);           // 这个方法内部可能改了 d！
System.out.println(d);   // 你根本不知道 d 是什么时候了
```

`Date` 和 `Calendar` 都是可变的——任何拿到引用的代码都能偷偷改它。在多线程环境下，这就是一个不定时炸弹。**SimpleDateFormat 更是一个著名的线程安全陷阱**：多个线程共用一个实例会导致格式化结果错乱。

### 问题三：职责混乱——一个类干太多事

`Date` 这个名字暗示它只管日期，但它实际存储了一个精确到毫秒的时间点。而 `Calendar` 既处理日期运算又处理时区又处理格式化。类和类的边界模糊不清，新手根本不知道该用谁。

### java.time 的三条设计哲学（核心价值）

| 原则 | 含义 |
|------|------|
| **不可变** | 所有操作返回新对象，线程安全，不怕被偷改 |
| **职责分离** | 日期就是 `LocalDate`，时间就是 `LocalTime`，合起来就是 `LocalDateTime`——各管各的 |
| **命名直觉** | `now()` 就是现在，`plusDays(7)` 就是加 7 天——读代码像读英语 |

> **一句话**：java.time 不是 Date 的升级版，而是推翻了重来。学它不是因为 Date 不够用，而是因为 Date 设计错了。

---

## 2. 三大核心类

| 类 | 含义 | 格式示例 | 类比 |
|----|------|---------|------|
| `LocalDate` | 纯日期（年月日） | `2026-05-20` | 日历上的某一页 |
| `LocalTime` | 纯时间（时分秒纳秒） | `14:30:00` | 手表上的指针 |
| `LocalDateTime` | 日期+时间 | `2026-05-20T14:30:00` | 日历 + 手表 |

所有 `java.time` 对象都是**不可变的**（immutable）——任何"修改"操作都返回新对象，原对象不变。

---

## 3. 创建实例

```java
// 方式一：now() — 获取当前
LocalDate today = LocalDate.now();
LocalTime now = LocalTime.now();
LocalDateTime dateTime = LocalDateTime.now();

// 方式二：of() — 指定值
LocalDate d = LocalDate.of(2026, 5, 20);
LocalTime t = LocalTime.of(14, 30, 0);
LocalDateTime dt = LocalDateTime.of(2026, 5, 20, 14, 30);

// 方式三：组合
LocalDateTime dt2 = LocalDateTime.of(today, now);  // 日期 + 时间
LocalDate fromDT = dt.toLocalDate();                // 从 LocalDateTime 提取日期
LocalTime fromDT2 = dt.toLocalTime();               // 从 LocalDateTime 提取时间

// 方式四：parse() — 从字符串解析
LocalDate parsed = LocalDate.parse("2026-05-20");
LocalTime parsedT = LocalTime.parse("14:30:00");
```

---

## 4. 格式化（DateTimeFormatter）

```java
// 输出到字符串
DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
String str = LocalDateTime.now().format(fmt);  // "2026/05/20 14:30:00"

// 从自定义格式解析
LocalDate d2 = LocalDate.parse("20-05-2026", DateTimeFormatter.ofPattern("dd-MM-yyyy"));
```

常用模式符号：

| 符号 | 含义 | 示例 |
|------|------|------|
| `yyyy` | 四位年份 | 2026 |
| `MM` | 两位月份 | 05 |
| `dd` | 两位日期 | 20 |
| `HH` | 24小时制小时 | 14 |
| `mm` | 分钟 | 30 |
| `ss` | 秒 | 00 |
| `E` | 星期缩写 | 周三 |

---

## 5. 日期时间运算（核心能力）

所有运算都**返回新对象**，不影响原对象：

```java
LocalDate d = LocalDate.of(2026, 5, 20);

// 加减日期
d.plusDays(7);          // 7天后：2026-05-27
d.minusMonths(1);       // 1月前：2026-04-20
d.plusYears(1);         // 1年后：2027-05-20

// 调整到特定边界
d.withDayOfMonth(1);    // 当月第一天：2026-05-01
d.withMonth(12);        // 当年12月：2026-12-20

// 获取字段
d.getDayOfWeek();       // DayOfWeek.WEDNESDAY
d.getDayOfMonth();      // 20
d.getMonth();           // Month.MAY
d.lengthOfMonth();      // 31（当月天数）
```

---

## 6. 日期比较

```java
LocalDate d1 = LocalDate.of(2026, 5, 20);
LocalDate d2 = LocalDate.of(2026, 6, 1);

d1.isBefore(d2);    // true
d1.isAfter(d2);     // false
d1.isEqual(d2);     // false
d1.compareTo(d2);   // 负数（d1 在 d2 之前）
```

---

## 7. Period 与 Duration

| 类 | 用于 | 精度 |
|----|------|------|
| `Period` | 日期差（年月日） | 天级别 |
| `Duration` | 时间差 | 纳秒级别 |

```java
Period p = Period.between(d1, d2);     // P11D（11天）
p.getDays();                            // 11

Duration dur = Duration.between(t1, t2);  // PT2H30M
dur.toMinutes();                          // 150
```

---

## 8. 关键规则

- **不可变**：`d.plusDays(1)` 不改变 `d`，必须接收返回值
- **线程安全**：不需要 synchronized，放心在多线程环境使用
- **null 安全**：避免用 null 表示"没有日期"——用 `Optional<LocalDate>` 或定义一个特殊常量
- **不要用旧的 Date/Calendar**：`java.util.Date` 里的 `getYear()` 返回的是"年份-1900"，这是 Java 早期最臭名昭著的设计 Bug 之一

---

## 9. 常用方法速查表

### 创建

| 方法 | 说明 | 示例 |
|------|------|------|
| `LocalDate.now()` | 当前日期 | `LocalDate.now()` → 2026-05-20 |
| `LocalTime.now()` | 当前时间 | `LocalTime.now()` → 14:30:00.123 |
| `LocalDateTime.now()` | 当前日期+时间 | `LocalDateTime.now()` |
| `LocalDate.of(y, m, d)` | 指定日期 | `LocalDate.of(2026, 5, 20)` |
| `LocalTime.of(h, m)` | 指定时间 | `LocalTime.of(14, 30)` |
| `LocalTime.of(h, m, s)` | 指定时间（精确到秒） | `LocalTime.of(14, 30, 0)` |
| `LocalDateTime.of(date, time)` | 日期+时间组合 | `LocalDateTime.of(date, time)` |

### 解析与格式化

| 方法 | 说明 | 示例 |
|------|------|------|
| `LocalDate.parse("yyyy-MM-dd")` | 字符串→日期 | `LocalDate.parse("2026-05-20")` |
| `LocalTime.parse("HH:mm")` | 字符串→时间 | `LocalTime.parse("14:30")` |
| `date.format(formatter)` | 日期→格式化字符串 | `date.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日"))` |
| `DateTimeFormatter.ofPattern("...")` | 定义格式模板 | 见模式符号表 |

### 运算（全部返回新对象）

| 方法 | 说明 | 示例 |
|------|------|------|
| `plusDays(n)` | +n 天 | `date.plusDays(7)` |
| `plusMonths(n)` | +n 月 | `date.plusMonths(1)` |
| `plusYears(n)` | +n 年 | `date.plusYears(1)` |
| `plusMinutes(n)` | +n 分钟 | `time.plusMinutes(90)` |
| `minusDays(n)` | -n 天 | `date.minusDays(30)` |
| `minusMonths(n)` | -n 月 | `date.minusMonths(1)` |
| `withDayOfMonth(n)` | 设置为本月第 n 天 | `date.withDayOfMonth(1)` |

### 比较与计算

| 方法 | 说明 | 示例 |
|------|------|------|
| `isBefore(other)` | 是否在 other 之前 | `d1.isBefore(d2)` → true |
| `isAfter(other)` | 是否在 other 之后 | `d1.isAfter(d2)` → false |
| `isEqual(other)` | 是否同一天 | `d1.isEqual(d2)` → false |
| `compareTo(other)` | 比较，返回负数/0/正数 | `d1.compareTo(d2)` → -1 |
| `Period.between(d1, d2)` | 日期间隔（年月日） | `Period.between(d1, d2)` → P11D |
| `Duration.between(t1, t2)` | 时间间隔（时分秒） | `Duration.between(t1, t2).toMinutes()` |
| `ChronoUnit.DAYS.between(d1, d2)` | 两日期相差天数 | `ChronoUnit.DAYS.between(d1, d2)` → 11 |

### 提取字段

| 方法 | 说明 | 示例 |
|------|------|------|
| `getDayOfWeek()` | 星期几（DayOfWeek 枚举） | `date.getDayOfWeek()` → WEDNESDAY |
| `getDayOfMonth()` | 本月第几天 | `date.getDayOfMonth()` → 20 |
| `getMonth()` | 月份（Month 枚举） | `date.getMonth()` → MAY |
| `getYear()` | 年份 | `date.getYear()` → 2026 |
| `lengthOfMonth()` | 当月天数 | `date.lengthOfMonth()` → 31 |
| `toLocalDate()` | LocalDateTime → LocalDate | `dt.toLocalDate()` |
| `toLocalTime()` | LocalDateTime → LocalTime | `dt.toLocalTime()` |

---

## 10. 排序与 Comparator 速查

### Collections.sort() vs List.sort()

```java
List<String> list = new ArrayList<>(List.of("C", "A", "B"));

// 方式一：Collections.sort(list) —— 自然顺序（元素必须实现 Comparable）
Collections.sort(list);                              // [A, B, C]

// 方式二：Collections.sort(list, comparator) —— 自定义比较器
Collections.sort(list, Comparator.reverseOrder());   // [C, B, A]

// 方式三：list.sort(comparator) —— 直接调 List 的方法（推荐，更简洁）
list.sort(Comparator.naturalOrder());                // [A, B, C]
```

### Comparator 链式构建（最常用）

```java
// 单字段排序
list.sort(Comparator.comparing(w -> w.date));

// 多字段排序：先按日期，日期相同按时间
list.sort(Comparator.comparing(w -> w.date)
                    .thenComparing(w -> w.startTime));

// 降序
list.sort(Comparator.comparing(w -> w.date).reversed());
```

### 类型推断问题的两种解法

```java
// 问题：链式 Lambda 有时编译器推断不出类型
list.sort(Comparator.comparing(w -> w.date).thenComparing(w -> w.startTime));
// → 编译错误：找不到符号 date

// 解法一：第一个 Lambda 显式标类型
list.sort(Comparator.comparing((Workshop w) -> w.date).thenComparing(w -> w.startTime));

// 解法二：抽出来分两步写（最稳）
Comparator<Workshop> byDate = Comparator.comparing(w -> w.date);
list.sort(byDate.thenComparing(w -> w.startTime));

// 解法三：方法引用（需要 getter）
list.sort(Comparator.comparing(Workshop::getDate).thenComparing(Workshop::getStartTime));
```

### 常用 Comparator 静态方法

| 方法 | 说明 | 示例 |
|------|------|------|
| `Comparator.naturalOrder()` | 自然顺序（升序） | `list.sort(Comparator.naturalOrder())` |
| `Comparator.reverseOrder()` | 自然顺序的逆序（降序） | `list.sort(Comparator.reverseOrder())` |
| `Comparator.comparing(Function)` | 按指定字段提取器排序 | `Comparator.comparing(w -> w.date)` |
| `.thenComparing(Function)` | 多字段链式排序 | `.thenComparing(w -> w.time)` |
| `.reversed()` | 反转当前比较器 | `Comparator.comparing(w -> w.date).reversed()` |

---

## 11. Lambda 表达式速查

### 基本格式

```
(参数) -> { 方法体; return 返回值; }
```

### 按参数数量分

```java
// 无参数：必须写空括号
() -> System.out.println("Hello")

// 单参数：括号可省略
s -> s.length()
(String s) -> s.length()      // 也可以显式写类型加括号

// 多参数：必须写括号
(x, y) -> x + y
```

### 按方法体分

```java
// 单行表达式（不用 return，不用分号）
s -> s.length()

// 代码块（必须写 return 和分号）
s -> {
    String upper = s.toUpperCase();
    return upper.length();
}
```

### 方法引用（Lambda 的简写）

```java
// Lambda：从对象提取字段
w -> w.date                     // 等价于 ↓
Workshop::getDate               // 实例方法引用（需要 getter）

// Lambda：调用方法
s -> s.length()                 // 等价于 ↓
String::length                  // 任意对象的实例方法

// Lambda：调用静态方法
s -> Integer.parseInt(s)        // 等价于 ↓
Integer::parseInt               // 静态方法引用

// Lambda：构造对象
() -> new ArrayList<>()         // 等价于 ↓
ArrayList::new                  // 构造器引用
```

### 常见使用位置

```java
// 排序
list.sort((a, b) -> a.compareTo(b));

// 遍历
list.forEach(item -> System.out.println(item));

// 条件删除
list.removeIf(s -> s.isEmpty());

// 替换
list.replaceAll(s -> s.toUpperCase());
```

> **本质**：Lambda 是一个**匿名函数**——一段可以像值一样传递的代码。`w -> w.date` 等价于"给一个 w，返回它的 date"，编译器自动把它变成 `Comparator` 需要的 `compare` 方法实现。

---

## 12. 面试官视角

| 考察点 | 参考答案 |
|--------|---------|
| Java 8 的日期时间 API 为什么比 Date/Calendar 好？ | ① 不可变 + 线程安全 ② API 清晰——LocalDate 管日期，LocalTime 管时间，各司其职 ③ 命名直觉——of()/plusDays() vs old Calendar.set() ④ Date 的 getYear() 返回年份-1900，极不直觉 |
| LocalDateTime 和 ZonedDateTime 的区别？ | LocalDateTime 不带时区信息，只是"一个日期+时间"的组合；ZonedDateTime 绑定时区，能正确处理夏令时和跨时区计算。不涉及全球业务用 LocalDateTime。 |
| `d.plusDays(7)` 会改变原对象吗？ | 不会。java.time 所有类都是不可变的，会返回新对象。如果忽略返回值就会丢失计算结果——这是新手最容易犯的 Bug。 |
| Period 和 Duration 的区别？ | Period 用于日期级（年月日），Duration 用于时间级（时/分/秒/纳秒）。Period.between(LocalDate, LocalDate)，Duration.between(LocalTime, LocalTime)。 |
