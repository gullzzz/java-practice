# Optional —— 消灭 null 的容器

## 核心理念

> Optional 是一个**最多装一个元素**的"盒子"——要么装着某个值，要么是空的。它用类型系统强制你处理"值可能不存在"的情况。

**生活类比：** 快递柜——柜子里可能有一个包裹（有值），也可能是空的（没到货）。Optional 逼着你**先检查有没有**，再打开拿东西。

```java
// ❌ 没有 Optional 的世界：到处 null 检查
Trade trade = findTrade("TX-999");
if (trade != null) {
    String buyer = trade.getBuyerName();
    if (buyer != null) {
        System.out.println(buyer.toUpperCase());
    }
}

// ✅ Optional 的世界：链式处理，空分支自动短路
findTrade("TX-999")
    .map(Trade::getBuyerName)
    .map(String::toUpperCase)
    .ifPresent(System.out::println);
```

核心思想：**把"可能为空"塞进类型系统，编译器帮你记住要处理它。**

---

## Optional 不管什么？——一个关键的边界

Optional 解决的是**"查了一圈没找到"这种业务空值**，而不是**"调用方传了 null 进来"这种程序 Bug**：

```java
// 场景 1：参数 null 检查 —— Optional 管不了
public List<String> filter(List<String> trades, Predicate<String> cond) {
    if (trades == null || cond == null) {  // 防御"调用方瞎传"，是 Bug 排查
        return Collections.emptyList();
    }
    // ...
}

// 场景 2：Stream 返回 Optional —— Optional 的用武之地
trades.stream()
    .filter(t -> getPrice(t) > 10000)
    .findFirst();  // 查无此人——是正常业务，不是 Bug
```

| | 参数 null 检查 | Stream 返回 Optional |
|---|---|---|
| **null 从哪来** | 调用方瞎传的——是代码 Bug | 业务上"查无此人"——是正常情况 |
| **处理策略** | fail-fast，尽早暴露 Bug | Optional 链式兜底，业务容错 |
| **Optional 能管吗** | ❌ 管不了——null 根本没进流水线 | ✅ max()/findFirst() 内部自动创建 |

**记住：Optional 管的是"出参"的缺失，不管"入参"的 null。**

---

## 一、创建 Optional

| 方法签名 | 参数 | 返回值 | 说明 | 注意事项 |
|----------|------|--------|------|----------|
| ⭐ `Optional.of(T value)` | `value` — 非 null 的值 | `Optional<T>` | 创建装有 value 的 Optional | **value 为 null 立刻抛 NPE**——fail-fast 设计 |
| ⭐ `Optional.ofNullable(T value)` | `value` — 可为 null 的值 | `Optional<T>` | value 为 null 返回空 Optional，否则装值 | 最常用，对接"可能是 null"的旧代码 |
| `Optional.empty()` | 无 | `Optional<T>` | 创建一个空的 Optional | 等同于 `Optional.ofNullable(null)`，但语义更明确 |

```java
// Demo：三种创建方式
Optional<String> opt1 = Optional.of("hello");        // 确定非 null
Optional<String> opt2 = Optional.ofNullable(maybe);  // 不确定，可能是 null
Optional<String> opt3 = Optional.empty();            // 显式空
```

---

## 二、取出值——返回 T

| 方法签名 | 参数 | 返回值 | 说明 | 注意事项 |
|----------|------|--------|------|----------|
| ⭐ `orElse(T other)` | `other` — 兜底默认值 | `T` | 有值返回值，空则返回 other | **other 无论是否需要都会被执行**（急性质） |
| ⭐ `orElseGet(Supplier<? extends T> supplier)` | `supplier` — 兜底值工厂 | `T` | 有值返回值，空则调用 supplier | **惰性求值**，兜底值计算昂贵时用它 |
| ⭐ `orElseThrow()` | 无 | `T` | 有值返回值，空抛 NoSuchElementException | Java 10，比 `get()` 语义更清晰 |
| `orElseThrow(Supplier<X> exceptionSupplier)` | `exceptionSupplier` — 异常工厂 | `T` | 空时抛出自定义异常 | 异常信息可以更具体 |
| `get()` | 无 | `T` | 直接取值 | **不推荐**——空时抛 NoSuchElementException，和 NPE 一样糟 |
| `isPresent()` | 无 | `boolean` | 有值返回 true | **不推荐**——退化回 `if (x != null)` 模式 |
| `isEmpty()` | 无 | `boolean` | 为空返回 true | Java 11，语义比 `!isPresent()` 清晰 |

```java
// Demo：orElse vs orElseGet —— 急性质 vs 惰性求值
String a = opt.orElse(expensiveFallback());           // expensiveFallback() 一定会执行！
String b = opt.orElseGet(() -> expensiveFallback());  // 只在 Optional 为空时才执行

// Demo：orElseThrow 自定义异常
Trade t = findTrade(id).orElseThrow(() -> new TradeNotFoundException(id));
```

---

## 三、条件执行——返回 void

| 方法签名 | 参数 | 返回值 | 说明 | 注意事项 |
|----------|------|--------|------|----------|
| ⭐ `ifPresent(Consumer<? super T> action)` | `action` — 有值时执行的操作 | `void` | 有值就执行 action，空则什么都不做 | Consumer 无返回值——只做副作用（打印、存库） |
| ⭐ `ifPresentOrElse(Consumer<? super T> action, Runnable emptyAction)` | `action` — 有值时执行 / `emptyAction` — 空时执行 | `void` | 双分支：有值走 Consumer，空走 Runnable | Java 9，Runnable 无参无返回值 |

```java
// Demo：ifPresent —— 只关心"有值"的情况
findTrade(id).ifPresent(t -> System.out.println("找到: " + t));

// Demo：ifPresentOrElse —— 两个分支都要处理
findTrade(id).ifPresentOrElse(
    t  -> System.out.println("找到: " + t),  // Consumer：有值
    () -> System.out.println("未找到")       // Runnable：空
);
```

---

## 四、中间操作——返回 Optional

| 方法签名 | 参数 | 返回值 | 说明 | 注意事项 |
|----------|------|--------|------|----------|
| ⭐ `map(Function<? super T, ? extends U> mapper)` | `mapper` — 转换函数 | `Optional<U>` | 有值时转换，空则返回空 Optional | 和 Stream.map 一样语义，但面对的是 0 或 1 个元素 |
| ⭐ `flatMap(Function<? super T, Optional<U>> mapper)` | `mapper` — 返回 Optional 的函数 | `Optional<U>` | 有值时转换并摊平 | 当 mapper 本身返回 Optional 时用它，防嵌套 |
| ⭐ `filter(Predicate<? super T> predicate)` | `predicate` — 条件 | `Optional<T>` | 有值且满足条件则保留，否则变空 | 条件不满足 → 变空，管道短路 |

```java
// Demo：map —— 提取/转换
Optional<String> buyer = findTrade(id).map(Trade::getBuyerName);

// Demo：flatMap —— mapper 本身返回 Optional，防 Optional<Optional<T>> 嵌套
Optional<String> cleaned = findTrade(id).flatMap(TradeService::validateName);

// Demo：filter —— 条件不满足就变空
Optional<Trade> vip = findTrade(id).filter(t -> t.getPrice() > 10_000);
```

---

## 五、级联回退 & 转 Stream

| 方法签名 | 参数 | 返回值 | 说明 | 注意事项 |
|----------|------|--------|------|----------|
| ⭐ `or(Supplier<? extends Optional<? extends T>> supplier)` | `supplier` — 备选 Optional 工厂 | `Optional<T>` | 有值返回自己，空则返回 supplier 提供的 Optional | Java 9，和 `orElseGet` 的区别：它返回 Optional 而非 T |
| `stream()` | 无 | `Stream<T>` | 有值返回单元素流，空返回空流 | Java 9，Optional → Stream 的桥梁 |

```java
// Demo：or —— 级联回退查找，不拆盒子
findInCache(id)
    .or(() -> findInDb(id))       // 缓存没命中 → 查数据库
    .or(() -> findInRemote(id))   // 数据库没有 → 查远程
    .ifPresent(System.out::println);

// Demo：stream —— 把 Optional 接回 Stream 流水线
List<String> names = trades.stream()
    .map(TradeService::findBuyerName)   // → Stream<Optional<String>>
    .flatMap(Optional::stream)          // → Stream<String>，空 Optional 自动丢弃
    .collect(Collectors.toList());
```

---

## 常用组合模式

```java
// 模式 1：查找 → 转换 → 兜底
String display = findTrade(id).map(Trade::getBuyerName).orElse("未知交易");

// 模式 2：查找 → 过滤 → 惰性兜底
Trade vip = findTrade(id).filter(t -> t.getPrice() > 10_000)
    .orElseGet(() -> loadFromBackup(id));

// 模式 3：级联回退
findInCache(id).or(() -> findInDb(id)).orElse(defaultTrade);
```

---

## 反模式：千万别这么写

```java
// ❌ 反模式 1：isPresent + get —— 退化回 null 检查
if (opt.isPresent()) { String val = opt.get(); }

// ❌ 反模式 2：orElse(null) —— 把 null 又放回去了
String val = opt.orElse(null);

// ❌ 反模式 3：方法参数用 Optional
public void process(Optional<Trade> tradeOpt) { }  // Optional 是返回值类型，不是参数类型
```

---

## 面试官视角

| 常见问法 | 考察点 |
|----------|--------|
| "Optional 能解决什么问题？" | 显式表达"值可能不存在"，强迫调用方处理空值分支 |
| "`orElse` 和 `orElseGet` 有什么区别？" | 急性质 vs 惰性求值——`orElse` 的参数一定会执行 |
| "`orElseGet` 和 `or()` 有什么区别？" | orElseGet 返回 T（拆盒子），or 返回 Optional（不拆），用于级联回退 |
| "`map` 和 `flatMap` 什么时候用哪个？" | 转换函数返回普通值用 map，返回 Optional 用 flatMap 防嵌套 |
| "为什么不该用 `isPresent()` + `get()`？" | 和 `if (x != null)` 没区别，违背 Optional 的设计初衷 |
| "Optional 能完全消灭 NPE 吗？" | 不能——`of(null)` 抛 NPE，`get()` 空时抛异常。Optional 是"提醒你处理"而非"消灭空值" |
| "Optional 适合做字段类型吗？" | 不推荐——序列化问题、额外内存、违背返回值类型的初衷 |
