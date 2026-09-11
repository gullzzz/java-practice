# 正则表达式 —— 一行代码替代十行 if-else

---

## 1. 正则表达式是什么？

正则表达式（Regular Expression，简称 regex）是一种**模式匹配语言**，用一串特殊字符描述"一类字符串的规律"——比如"全是数字"、"像邮箱地址"、"以 `+86` 开头后跟 11 位数字"。

Java 中正则的核心入口是 `java.util.regex` 包下的两个类：

| 类 | 职责 |
|---|---|
| `Pattern` | 编译后的正则表达式（不可变，线程安全，可复用） |
| `Matcher` | 对具体字符串执行匹配操作的引擎 |

### 1.1 Pattern 与 Matcher 的关系 —— 一句话类比

> **Pattern 是"寻宝图"，Matcher 是"拿着寻宝图的探险家"。**

| 概念 | 类比 | 代码 |
|------|------|------|
| `Pattern` | 寻宝图——描述"宝藏长什么样"（规则） | `Pattern.compile("\\d+")` |
| `Matcher` | 探险家——拿着图在具体区域里找宝藏（执行） | `pattern.matcher("订单号: 8848")` |

**为什么分两个类？** 一张寻宝图（Pattern）可以给多个探险家用，在不同文本上反复搜。Pattern 编译一次花了 CPU，之后每次只创建轻量的 Matcher 去干活——这就是性能优化的核心。

**Matcher 的三个核心操作：**

| 方法 | 作用 | 类比 |
|------|------|------|
| `matches()` | 整个区域是不是宝藏？ | "整张地图就是宝藏吗？"——必须整个字符串完全匹配 |
| `find()` | 下一个宝藏在哪？ | "继续走，找到下一个宝藏"——返回 true/false，可循环调用 |
| `group()` | 把当前找到的宝藏取出来 | "挖出来！"——必须在 find() 返回 true 之后调用，否则抛异常 |

> ⚠️ **高频陷阱**：`Pattern.compile()` 是昂贵的，但 Pattern 不可变且线程安全，应声明为 `static final` 复用。每次调 `str.matches(regex)` 或 `str.replaceAll(regex, ...)` 底层都会临时编译 Pattern——循环里这样写是性能反模式。

> **为什么需要正则？** 假设你要校验一个手机号：必须以 1 开头，共 11 位数字。不用正则，你需要 `charAt()` + `Character.isDigit()` + 长度判断，十行代码。用正则：`str.matches("1\\d{10}")`，一行。正则把"对字符串的模式判断"从过程式编码提升为声明式表达——你只管描述"我想要什么"，不用写"怎么逐字符检查"。

---

## 2. 常用语法速查表

### 2.1 字符匹配

| 语法 | 含义 | 示例 |
|------|------|------|
| `.` | 任意单个字符（换行符除外） | `a.c` 匹配 "abc"、"a3c" |
| `\d` | 数字 `[0-9]` | `\d{3}` 匹配 3 位数字 |
| `\D` | 非数字 | `\D+` 匹配 "abc" |
| `\w` | 单词字符 `[a-zA-Z0-9_]` | `\w+` 匹配变量名 |
| `\W` | 非单词字符 | 匹配标点、空格 |
| `\s` | 空白字符（空格、Tab、换行） | `a\sb` 匹配 "a b" |
| `\S` | 非空白字符 | — |
| `[abc]` | a、b、c 中任意一个 | `[aeiou]` 匹配元音 |
| `[^abc]` | 除 a、b、c 之外的任意字符 | `[^0-9]` 匹配非数字 |
| `[a-z]` | 字符范围 | `[A-Za-z]` 匹配任意字母 |

> ⚠️ **Java 字符串转义**：`\d` 在 Java 字符串中必须写 `\\d`，因为 `\` 本身是 Java 转义字符。一个正则的 `\d` → Java 代码里是 `"\\d"`。

### 2.2 数量限定

| 语法 | 含义 | 示例 |
|------|------|------|
| `X?` | X 出现 0 或 1 次 | `colou?r` 匹配 "color"、"colour" |
| `X*` | X 出现 0 或多次 | `a*` 匹配 ""、"a"、"aaa" |
| `X+` | X 出现 1 或多次 | `\d+` 匹配至少一位数字 |
| `X{n}` | X 恰好出现 n 次 | `\d{11}` 匹配 11 位数字 |
| `X{n,}` | X 至少出现 n 次 | `\w{6,}` 匹配 6 位以上密码 |
| `X{n,m}` | X 出现 n 到 m 次 | `\w{6,20}` 匹配 6~20 位用户名 |

> **量词不止用于单字符：** `X` 可以是字符、字符集，也可以是**一个组**。例如 `(\\.\\d+)?` 表示"整个小数部分可选"——`\d+(\\.\\d+)?` 同时匹配 `500` 和 `1.5`，前者小数部分不出现，后者出现。

### 2.3 边界与分组

| 语法 | 含义 | 示例 |
|------|------|------|
| `^` | 行首 | `^hello` 匹配以 hello 开头的行 |
| `$` | 行尾 | `world$` 匹配以 world 结尾的行 |
| `\b` | 单词边界 | `\bcat\b` 匹配 "cat"，不匹配 "catalog" |
| `()` | 捕获组 | `(\d{3})-(\d{4})` 分两组提取区号和号码 |
| `(?: )` | 非捕获组 | 只分组不捕获，提高性能 |
| `|` | 或 | `cat|dog` 匹配 "cat" 或 "dog" |

---

## 3. Java 中正则的三种用法

```java
// 方式一：String.matches() —— 最简单，整体匹配
boolean isPhone = "13812345678".matches("1\\d{10}");

// 方式二：Pattern.compile + matcher().matches() —— 可复用 Pattern
Pattern phonePattern = Pattern.compile("1\\d{10}");
Matcher m1 = phonePattern.matcher("13812345678");
boolean result = m1.matches();  // true

// 方式三：Matcher.find() —— 查找子串（不是整体匹配）
Pattern digitPattern = Pattern.compile("\\d+");
Matcher m2 = digitPattern.matcher("订单号: 8848, 金额: 199");
while (m2.find()) {
    System.out.println(m2.group());  // 8848  →  199
}
```

### String 类内置的正则方法

| 方法 | 作用 |
|------|------|
| `str.matches(regex)` | 整个字符串是否匹配 |
| `str.replaceAll(regex, replacement)` | 替换所有匹配部分 |
| `str.replaceFirst(regex, replacement)` | 替换第一个匹配部分 |
| `str.split(regex)` | 按正则切分字符串 |
| `str.split(regex, limit)` | 按正则切分，限制份数 |

> ⚠️ **高频陷阱**：`replaceAll()` 的替换字符串中 `$1`、`$2` 表示捕获组引用，`\\` 表示一个字面反斜杠。如果替换内容来自用户输入，用 `Matcher.replaceAll()` + `Matcher.quoteReplacement()` 防止注入。

---

## 4. Demo：格式校验器

```java
import java.util.regex.Pattern;

public class FormatValidator {
    // 编译一次，到处复用 —— Pattern 是线程安全的
    private static final Pattern EMAIL = Pattern.compile(
        "^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,}$"
    );

    public static boolean isValidEmail(String email) {
        return EMAIL.matcher(email).matches();
    }

    public static void main(String[] args) {
        System.out.println(isValidEmail("duke@java.dev"));   // true
        System.out.println(isValidEmail("not-an-email"));    // false
    }
}
```

**关键细节：** `Pattern.compile()` 是昂贵的——它把正则字符串编译成内部的有穷自动机。把 Pattern 声明为 `static final`，只编译一次，之后每次匹配只需创建轻量的 Matcher。

---

## 5. 面试官视角

| 常见问法 | 考察点 | 参考答案 |
|----------|--------|----------|
| "matches() 和 find() 有什么区别？" | 整体匹配 vs 部分查找 | `matches()` 要求整个字符串完全匹配正则（隐含 `^...$`）；`find()` 在字符串中扫描下一个匹配的子串，可多次调用遍历所有匹配项 |
| "Pattern 为什么要预编译？" | 编译成本、线程安全 | `Pattern.compile()` 内部会构建有穷自动机，CPU 开销不小。每次调 `String.matches()` 底层都会重新编译 Pattern。预编译成 static final 复用，是正则性能优化的第一原则。Pattern 不可变且线程安全，天然适合单例复用 |
| "贪婪、勉强、独占量词有什么区别？" | 回溯机制、性能 | 贪婪（`*`）尽可能多吃字符，不匹配时逐个回退（回溯）；勉强（`*?`）尽可能少吃，逐步扩展；独占（`*+`）尽可能多吃但绝不回退——匹配快但可能匹配不到。回溯次数失控会导致**正则灾难性回溯（ReDoS）** |
| "用什么方法替换字符串中的匹配内容？" | replaceAll vs replaceFirst | `replaceAll(regex, replacement)` 替换所有匹配；`replaceFirst(regex, replacement)` 只替换第一个。`replacement` 中 `$1` 引用第一个捕获组。注意 `replaceAll` 的替换参数也会解析 `\` 和 `$`，含用户输入时用 `Matcher.quoteReplacement()` 防注入 |
| "如何让 `.` 也匹配换行符？" | DOTALL 标志 | 默认 `.` 不匹配 `\n`。用 `Pattern.compile(regex, Pattern.DOTALL)` 或在正则开头加 `(?s)` 开启 DOTALL 模式 |
| "split 和 StringTokenizer 有什么区别？" | 现代替代 | `String.split(regex)` 返回数组，支持正则分隔——更灵活。`StringTokenizer` 是遗留类，只支持单字符分隔，基本不推荐使用 |
| "正则表达式会导致安全问题吗？" | ReDoS | 会。含有嵌套量词的正则（如 `(a+)+b`）遇到特定输入（如 "aaaa...X"），回溯次数呈指数爆炸，CPU 被长时间占满。防范：① 避免嵌套量词 ② 用独占量词 `*+`/`++` 消除回溯 ③ 对用户输入的正则加超时（Java 本身不支持正则超时，需外部处理） |

---

这些知识足够你开始挑战了，动手吧！
