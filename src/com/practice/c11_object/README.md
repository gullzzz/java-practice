# Object 类 —— 所有 Java 类的"祖宗"

## ① 是什么？

`java.lang.Object` 是 Java 中**所有类的终极父类**。你写的每一个类，即使没有 `extends`，也隐式继承了 `Object`。

```java
// 你写的：
public class Card { }

// 编译器实际看到的：
public class Card extends Object { }
```

这意味着你的 `Card` 对象天生就拥有 Object 定义的 11 个方法。

## ② 为什么要重写？

Object 的默认实现是"基于内存地址"的，这在业务中通常是错的：

| 方法 | 默认行为 | 问题 |
|------|----------|------|
| `toString()` | 返回 `类名@十六进制hashCode` | debug 时看到的全是乱码 |
| `equals(Object)` | `==` 比较内存地址 | 两张同名同属性的牌被当成"不同"的牌 |
| `hashCode()` | 返回内存地址相关的 int | 导致 `HashSet`/`HashMap` 无法正常工作 |

**核心契约（面试必考）：**
- 如果 `a.equals(b)` 为 `true`，那么 `a.hashCode()` **必须**等于 `b.hashCode()`
- 如果 `a.equals(b)` 为 `false`，那么 `a.hashCode()` **最好**不相等（但不强制）

## ③ 常用方法速查表

| 方法签名 | 作用 | 何时重写 |
|----------|------|----------|
| `String toString()` | 对象转字符串 | 需要可读的调试输出时 |
| `boolean equals(Object obj)` | 判断内容是否相等 | 需要按字段值比较两个对象时 |
| `int hashCode()` | 生成哈希值 | 重写 equals 时**必须**重写（契约要求） |
| `Class<?> getClass()` | 获取运行时类 | 通常不重写（final） |
| `protected Object clone()` | 浅拷贝对象 | 需实现 `Cloneable` 接口 |
| `protected void finalize()` | GC 回收前调用 | Java 9 已废弃，不推荐使用 |

## ④ 快速 Demo

```java
public class Card {
    private String name;
    private int cost;

    public Card(String name, int cost) {
        this.name = name;
        this.cost = cost;
    }

    @Override
    public String toString() {
        return "Card{name='" + name + "', cost=" + cost + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;          // 同一个引用
        if (!(obj instanceof Card)) return false;  // 类型检查
        Card other = (Card) obj;
        return this.cost == other.cost
            && Objects.equals(this.name, other.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, cost);  // JDK 7+ 一行搞定
    }
}
```

> **提示：** `java.util.Objects` 工具类能安全处理 null —— `Objects.equals(a, b)` 不会抛 NPE。

## ⑤ equals-hashCode 契约的底层原理

### 为什么 equals 为 true 时，hashCode 必须相等？

因为 **HashMap / HashSet 的查找逻辑是分两步走的**：

```
HashMap 查找流程：
  ┌──────────────────────────────────────────────────┐
  │  1. 用 key.hashCode() 算出该去哪个"桶" (bucket)  │
  │  2. 到达目标桶后，用 key.equals() 在桶内精确定位  │
  └──────────────────────────────────────────────────┘
```

**举个例子：** 假设你往 HashMap 里 put 了一个 key `fireball`，它的 hashCode 是 `42`：

| 步骤 | 操作 | 说明 |
|------|------|------|
| put | `map.put(fireball, 2)` | HashMap 把它放进 **42 号桶** |
| get | `map.get(anotherFireball)` | anotherFireball.equals(fireball) = true |

现在你拿着 `anotherFireball` 去 map 里找它：

- scenario **如果 hashCode 也相等（=42）**：HashMap 直奔 42 号桶，用 equals 一比对——找到了！返回 2。✔
- scenario **如果 hashCode 不相等（=99）**：HashMap 去 99 号桶找，里面空空如也——返回 null。但实际上应该找到的！✘

**一句话总结：hashCode 决定去哪个房间找人，equals 决定进了房间后认不认得这个人。如果你连房间都去错了，人就永远找不到了。**

### 为什么 equals 为 false 时，hashCode 可以不相等？

这恰恰是 **哈希冲突（Hash Collision）**——不同的对象被分到了同一个桶。

- HashMap 的桶结构是**链表 + 红黑树**，同一个桶里可以挂多个对象
- 桶内再用 `equals()` 逐个比对，区分它们
- 所有 key 的 hashCode 都返回 `1` 也能工作——只是所有 key 挤在同一个桶里，查找退化成 O(n)

**所以 "hashCode 最好不相等" 是为了性能**：哈希值越分散，每个桶里的元素越少，查找越快（趋近 O(1)）。

---

## ⑥ 面试官视角（含完整答案）

**Q1: "为什么重写 equals 必须重写 hashCode？"**

> HashMap/HashSet 依赖 hashCode 定位存储桶（bucket）。如果两个 equals 为 true 的对象 hashCode 不同，HashMap 会去不同的桶里查找，导致找不到已有 key，出现"逻辑重复"——同一张牌在 Set 里出现两次，put 相同的 key 不会覆盖而是新增。这直接违反了 HashMap/HashSet 的"键唯一"约定。

**Q2: "equals 和 == 有什么区别？"**

> `==` 比较**栈内存中的值**——基本类型比较数值，引用类型比较内存地址（两个引用是否指向同一个对象）。`equals()` 是 Object 定义的方法，默认实现就是 `==`，但可以被重写为按**内容/字段值**判断相等。String、Integer 等 JDK 类已经重写了 equals，所以 `"abc".equals("abc")` 为 true，而 `new String("abc") == new String("abc")` 为 false。

**Q3: "两个对象 equals 不相等，hashCode 一定不相等吗？"**

> 不一定。hashCode 返回 int，int 只有 2^32 种取值，而对象的可能性远大于此，哈希冲突在数学上不可避免。equals 不相等但 hashCode 相等是允许的，只是查找时需要在同一个桶里多比对几次，影响性能但不影响正确性。

---

# Objects 工具类 —— 防御式编程的瑞士军刀

## ① 是什么？

`java.util.Objects`（JDK 7+）是 Object 的**静态工具伴侣**。Object 定义"每个对象有什么能力"，Objects 提供"操作对象时的安全防护"——全是静态方法，专治 NPE。

> **为什么需要？** `obj.equals(other)` 如果 obj 为 null 直接炸。`Objects.equals(a, b)` 内部先判 null 再比，永远不抛 NPE。

## ② 常用方法速查表

| 方法 | 参数 | 返回值 | 作用 | 注意事项 |
|------|------|--------|------|----------|
| ⭐ `equals(a, b)` | `Object, Object` | `boolean` | null 安全的 equals | 两者都为 null 返回 true |
| ⭐ `requireNonNull(obj)` | `T` | `T` | obj 为 null 时抛 NPE | fail-fast 神器 |
| ⭐ `requireNonNull(obj, msg)` | `T, String` | `T` | 同上，带自定义错误信息 | 调 bug 时一眼定位 |
| ⭐ `hash(values...)` | `Object...` | `int` | 计算组合哈希值 | equals+hashCode 重写标配 |
| `toString(obj)` | `Object` | `String` | null 安全的 toString | obj 为 null 返回 `"null"` |
| `toString(obj, nullDefault)` | `Object, String` | `String` | null 时返回默认字符串 | 比三目运算符更简洁 |
| `requireNonNullElse(obj, default)` | `T, T` | `T` | null 时返回默认值 | Java 9+ |
| `isNull(obj) / nonNull(obj)` | `Object` | `boolean` | null 判断 | Stream 里当 Predicate 用 |

## ③ Demo

```java
import java.util.Objects;

// 1. 参数校验——入口处拦 null
public void transfer(String from, String to, BigDecimal amount) {
    Objects.requireNonNull(from, "转出账户不能为空");
    Objects.requireNonNull(to, "转入账户不能为空");
    Objects.requireNonNull(amount, "金额不能为空");
    // 业务逻辑...
}

// 2. null 安全的 equals
String a = null, b = "hello";
Objects.equals(a, b);  // false，不抛 NPE
// a.equals(b)          // ← 这行会炸！

// 3. 一行写 hashCode（配合 equals 重写）
@Override
public int hashCode() {
    return Objects.hash(name, cost, status);  // 自动处理 null
}
```

## ④ 面试官视角

| 考察点 | 常见问法 | 要答出的关键点 |
|--------|----------|----------------|
| 设计意图 | "Objects 和 Object 什么关系？" | Object 是所有类的父类（定义实例能力），Objects 是静态工具类（提供 null 安全的操作）。前者是"对象的模板"，后者是"操作对象的安全工具箱" |
| requireNonNull | "参数校验用 if 还是 requireNonNull？" | `requireNonNull` 语义更明确（一眼看出这是前置条件），代码更短。IDEA 还能基于它做静态分析——标注了 `@NotNull` 的地方如果传了没校验的值会标黄 |
| hash 原理 | "Objects.hash() 和 Objects.hashCode() 区别？" | `hash(...)` 接收多个值，调 `Arrays.hashCode` 组合哈希；`hashCode(o)` 只接收一个对象，等价于 `o != null ? o.hashCode() : 0` |

---

**Q4: "instanceof vs getClass() 在 equals 中怎么选？"**

> - `instanceof`：若是子类对象，equals 可以返回 true，遵循**里氏替换原则**（子类应当能替代父类）
> - `getClass()`：严格要求类型完全一致，即使是子类也返回 false
>
> 《Effective Java》的建议：**除非有明确的跨类型相等需求，否则优先用 `getClass()`**，因为 `instanceof` 可能破坏 equals 的**对称性**——父类 instanceof 子类为 false，子类 instanceof 父类为 true，反过来就不相等了。
