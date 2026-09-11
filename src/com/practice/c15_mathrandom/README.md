# Math & Random & BigDecimal —— 从掷骰子到精确金额

---

## 1. 这俩是干嘛的？

### Math 类
`java.lang.Math` 是一个**工具类**——全是静态方法，不能 new。它把游戏开发、物理引擎、数据统计中最常用的数学运算封装好，让你不用自己手写平方根和三角函数。

> **为什么需要 Math？** JDK 1.0 就存在。早期 Java 被寄予厚望做客户端应用（Applet、桌面程序），图形渲染和游戏逻辑需要大量数学运算。如果每个开发者都自己写 `pow()`、`sqrt()`，不仅重复造轮子，精度和性能也参差不齐。Math 类用 JNI 调用底层 C 数学库，兼顾了**正确性**和**速度**。

### Random 类
`java.util.Random` 是 Java 的**伪随机数生成器（PRNG）**。给定一个"种子"（seed），它用线性同余算法算出一串看起来随机的数列。

> **为什么需要 Random？** 真随机数需要硬件熵源（热噪声、放射性衰变），普通计算机拿不到。伪随机数用数学公式模拟——种子相同，序列就相同。这个"可复现"特性反而是**调试**和**测试**的救命稻草：只要固定种子，每次运行结果一样。

### ThreadLocalRandom（Java 7+）
高并发下多个线程抢同一个 Random 实例会自旋竞争。`ThreadLocalRandom` 每个线程独立持有自己的生成器，无锁，性能碾压。

---

## 2. 常用方法速查表

### Math 类（java.lang，自动导入）

| 方法 | 作用 | 示例 |
|------|------|------|
| `Math.abs(x)` | 绝对值 | `Math.abs(-5)` → 5 |
| `Math.max(a, b)` | 取较大值 | `Math.max(3, 7)` → 7 |
| `Math.min(a, b)` | 取较小值 | `Math.min(3, 7)` → 3 |
| `Math.pow(a, b)` | a 的 b 次方 | `Math.pow(2, 3)` → 8.0 |
| `Math.sqrt(x)` | 平方根 | `Math.sqrt(16)` → 4.0 |
| `Math.random()` | [0.0, 1.0) 随机小数 | `Math.random() * 100` → 0~99.999... |
| `Math.round(x)` | 四舍五入 | `Math.round(3.6)` → 4 |
| `Math.ceil(x)` | 向上取整 | `Math.ceil(3.1)` → 4.0 |
| `Math.floor(x)` | 向下取整 | `Math.floor(3.9)` → 3.0 |
| `Math.PI` | 圆周率常量 | 3.141592653589793 |
| `Math.E` | 自然常数 | 2.718281828459045 |

### Random 类（java.util，需导入）

| 方法 | 作用 | 示例 |
|------|------|------|
| `new Random()` | 默认构造器（种子=纳秒时间戳） | — |
| `new Random(seed)` | 固定种子构造器 | `new Random(42)` |
| `nextInt()` | 随机 int（全范围） | — |
| `nextInt(bound)` | [0, bound) 随机 int | `nextInt(100)` → 0~99 |
| `nextDouble()` | [0.0, 1.0) 随机 double | — |
| `nextBoolean()` | 随机 true/false | — |
| `nextLong()` | 随机 long | — |

### ThreadLocalRandom（java.util.concurrent，需导入）

| 方法 | 作用 |
|------|------|
| `ThreadLocalRandom.current()` | 获取当前线程的实例 |
| `nextInt(bound)` | 同 Random，但无锁 |

---

## 3. Demo：掷骰子

```java
import java.util.Random;

public class DiceRoller {
    public static void main(String[] args) {
        // 方式1：Math.random() —— 简单一次性的随机
        int quickRoll = (int) (Math.random() * 6) + 1;  // 1~6

        // 方式2：Random 实例 —— 需要多次掷骰时更高效
        Random rand = new Random();
        int betterRoll = rand.nextInt(6) + 1;  // 也生成 1~6

        System.out.println("快掷: " + quickRoll + "，标准掷: " + betterRoll);
    }
}
```

**关键细节：** `Math.random()` 底层也是调 `Random.nextDouble()`，但每次调都要创建和销毁一个匿名 Random 对象。循环里用 `Math.random()` 是性能陷阱——应该用一个 Random 实例复用。

---

## 4. 面试官视角

| 考察点 | 常见问法 | 你要答出的关键点 |
|--------|----------|-----------------|
| 伪随机概念 | "Random 是真随机吗？" | 不是，是伪随机——用种子+算法生成的确定性序列。相同种子 → 相同序列，这个特性对测试/调试反而是优势 |
| 种子作用 | "设固定种子有什么用？" | 让随机序列可复现。调试时固定种子能复现 Bug；游戏用种子生成世界（Minecraft 地图种子） |
| Math.random vs Random | "两者有什么区别？" | `Math.random()` 底层调 `new Random().nextDouble()`，每次创建新对象。循环里用是性能反模式；Random 实例可复用，还能指定种子 |
| 高并发随机 | "多线程下 Random 有什么问题？用什么替代？" | `Random.next()` 用 CAS 自旋更新种子，高并发下多个线程竞争同一个原子变量 → 性能退化。用 `ThreadLocalRandom.current().nextInt()` 替代，每个线程独立生成器，无锁 |
| 范围公式 | "怎么生成 [1, 100] 的随机整数？" | `random.nextInt(100) + 1`（nextInt(bound) 范围是 [0, bound)） |
| 安全性 | "需要密码学安全的随机数用什么？" | `SecureRandom`（java.security），基于熵源，但性能差；普通游戏/模拟用 Random 即可 |

---

---

# BigDecimal & BigInteger —— 精确计算

## 1. 这俩是干嘛的？

### 为什么 `double` 不够用？

在 Java 里执行 `System.out.println(0.1 + 0.2);`，输出不是 `0.3`，而是 `0.30000000000000004`。

**原因：** `double` 用 IEEE 754 二进制浮点数表示十进制小数。`0.1` 在二进制里是一个无限循环小数（和十进制的 `1/3 = 0.333...` 一样），64 位截断后产生舍入误差。科学计算可以容忍，但**金额不行**——差一分钱就是 Bug。

### BigDecimal
`java.math.BigDecimal` —— 任意精度的**不可变**十进制数。用字符串构造，用 `compareTo` 比较，运算必须接收返回值。

> **核心场景：** 金额计算、合同数值、利率、税费——任何要求精确十进制运算的地方。

### BigInteger
`java.math.BigInteger` —— 任意精度的**不可变**整数。`long` 最大只能到 `9.2 × 10¹⁸`（约 922 亿亿），`BigInteger` 无上限。

> **核心场景：** 密码学（RSA 大素数）、天文/物理计算、超出 long 范围的计数。

---

## 2. 常用方法速查表

### BigDecimal 构造（java.math，需导入）

| 方法 | 作用 | 注意 |
|------|------|------|
| ⭐ `new BigDecimal("1000.00")` | **字符串构造（唯一正解）** | 精确，必须用字符串 |
| `new BigDecimal(0.1)` | double 构造（**禁止**） | → `0.10000000000000000555...`，污染源 |
| `BigDecimal.valueOf(100)` | 静态工厂，整数转 BigDecimal | 等价于 `new BigDecimal("100")` |

### BigDecimal 运算（不可变！必须接收返回值）

| 方法 | 作用 | 示例 |
|------|------|------|
| ⭐ `add(BigDecimal)` | 加 | `a.add(b)` |
| ⭐ `subtract(BigDecimal)` | 减 | `a.subtract(b)` |
| ⭐ `multiply(BigDecimal)` | 乘 | `a.multiply(b)` |
| `divide(BigDecimal, int, RoundingMode)` | 除（必须指定精度和舍入） | `a.divide(b, 2, RoundingMode.HALF_UP)` |

### BigDecimal 常量（无需 new）

| 常量 | 值 |
|------|-----|
| `BigDecimal.ZERO` | `0` |
| `BigDecimal.ONE` | `1` |
| `BigDecimal.TEN` | `10` |

> 常用场景：`BigDecimal sum = BigDecimal.ZERO;` 初始化累加器，杜绝 `null` 引发的 NPE。

### BigDecimal 比较 & 转换

| 方法 | 作用 | 示例 |
|------|------|------|
| ⭐ `compareTo(BigDecimal)` | 比较大小 | `-1`(小于) / `0`(等于) / `1`(大于) |
| `equals(Object)` | 判断相等（**慎用**：连精度也参与比较） | `"2.0".equals("2.00")` → `false`！ |
| `doubleValue()` | 转 double | 可能丢精度 |
| `intValue()` | 转 int | 向下截断 |
| `toString()` | 转字符串 | 可用于输出 |

### BigInteger 常用方法

| 方法 | 作用 |
|------|------|
| `new BigInteger("12345678901234567890")` | 字符串构造 |
| `add / subtract / multiply / divide(BigInteger)` | 四则运算 |
| `compareTo / equals` | 比较 |
| `mod(BigInteger)` | 取模 |
| `isProbablePrime(int certainty)` | 素性检测（密码学常用） |

---

## 3. Demo：金额计算

```java
import java.math.BigDecimal;
import java.math.RoundingMode;

public class MoneyCalc {
    public static void main(String[] args) {
        BigDecimal price = new BigDecimal("199.99");
        BigDecimal qty = new BigDecimal("3");
        BigDecimal taxRate = new BigDecimal("0.06");

        // 乘法：单价 × 数量
        BigDecimal subtotal = price.multiply(qty);
        System.out.println("小计: " + subtotal);  // 599.97

        // 乘法 + 舍入：税费保留两位小数
        BigDecimal tax = subtotal.multiply(taxRate)
                .setScale(2, RoundingMode.HALF_UP);
        System.out.println("税费: " + tax);  // 36.00

        // 加法：总价
        BigDecimal total = subtotal.add(tax);
        System.out.println("总计: " + total);  // 635.97

        // 比较：用 compareTo，别用 equals
        BigDecimal a = new BigDecimal("2.0");
        BigDecimal b = new BigDecimal("2.00");
        System.out.println(a.equals(b));      // false！精度不同
        System.out.println(a.compareTo(b));   // 0（数值相等）
    }
}
```

**关键细节：**
- `setScale(2, RoundingMode.HALF_UP)` —— 保留两位小数，四舍五入
- `divide()` 除不尽时**必须**指定精度，否则抛 `ArithmeticException`
- `equals` 比较数值 + 精度，`compareTo` 只比较数值——比较钱用 `compareTo`

---

## 4. 面试官视角

| 考察点 | 常见问法 | 你要答出的关键点 |
|--------|----------|-----------------|
| 为什么不用 double | "金额字段为什么用 BigDecimal？" | double 二进制浮点数无法精确表示十进制小数，`0.1+0.2≠0.3`。金额必须用 BigDecimal（字符串构造） |
| 构造器陷阱 | "new BigDecimal(0.1) 有什么问题？" | 0.1 这个 double 字面量本身就是近似值，构造器拿到的是被污染的二进制近似。必须用字符串 `new BigDecimal("0.1")` |
| equals vs compareTo | "比较两个 BigDecimal 用哪个？" | `equals` 比较数值+精度（"2.0"≠"2.00"），`compareTo` 只比较数值。金额比较用 `compareTo`，比较结果 -1/0/1 |
| 不可变性 | "add() 之后原对象会变吗？" | 不会。BigDecimal 不可变，`a.add(b)` 返回新对象，必须接收返回值。忘记接收是静默丢弃——编译器不报错 |
| 除不尽 | "divide 除不尽怎么办？" | 抛 ArithmeticException。必须用 `divide(divisor, scale, RoundingMode)` 指定精度和舍入模式 |
| 内部存储方案 | "除了 BigDecimal 还有什么方式存金额？" | 用 `long` 存"分"——支付宝/微信内部都用这个方案：`199.99元 = 19999分`。整数运算最快，只在前端展示时 `÷100` 格式化 |

---

这些知识足够你开始挑战了，动手吧！
