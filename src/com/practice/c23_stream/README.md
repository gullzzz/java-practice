# Stream API —— 数据流水线

## 一、是什么？

Stream 不是数据结构，不存数据。它是一条**流水线**——数据从集合/数组/文件流进来，经过若干中间操作（过滤、转换、排序），最终被终端操作收集走。

```
数据源  →  [中间操作1] → [中间操作2] → [中间操作3] →  终端操作 → 结果
List        .filter()      .map()        .sorted()     .collect()
```

**一句话：** Stream = 集合的"流水线版" for 循环，用链式调用把"做什么"串起来，而不是写嵌套 for/if。

## 二、核心特征

| 特征 | 说明 |
|------|------|
| **不存数据** | Stream 只是数据的"视图/管道"，不修改数据源 |
| **惰性求值** | 中间操作只搭管道，终端操作触发时才真正执行 |
| **只能消费一次** | 流被终端操作消费后就关闭了，不能复用 |
| **不修改数据源** | 中间操作返回新 Stream，原集合不变 |

## 三、常用方法速查表

### 3.1 创建 Stream

| 方法 | 说明 | 示例 |
|------|------|------|
| `collection.stream()` | 从集合创建（最常用） | `list.stream()` |
| `Arrays.stream(arr)` | 从数组创建 | `Arrays.stream(new int[]{1,2,3})` |
| `Stream.of(...)` | 从可变参数创建 | `Stream.of("a", "b", "c")` |
| `Stream.iterate(seed, f)` | 无限流（按规则生成下一个） | `Stream.iterate(0, n -> n+1)` |
| `Stream.generate(supplier)` | 无限流（Supplier 生成） | `Stream.generate(Math::random)` |

### 3.2 中间操作（搭管道，惰性）

| 方法 | 参数类型 | 说明 |
|------|----------|------|
| ⭐ `filter(Predicate<T>)` | `T → boolean` | 保留满足条件的元素 |
| ⭐ `map(Function<T,R>)` | `T → R` | 把每个元素转换成另一个值 |
| `flatMap(Function<T,Stream<R>>)` | `T → Stream<R>` | 把每个元素展开成流，再合并 |
| ⭐ `sorted()` / `sorted(Comparator)` | `(T,T) → int` | 排序（自然顺序 / 自定义） |
| `distinct()` | — | 去重（靠 equals） |
| `limit(long n)` | — | 只保留前 n 个 |
| `skip(long n)` | — | 跳过前 n 个 |
| `peek(Consumer<T>)` | `T → void` | 看一眼每个元素（调试用） |

### 3.3 终端操作（触发执行，只能一次）

| 方法 | 返回类型 | 说明 |
|------|----------|------|
| ⭐ `collect(Collectors.toList())` | `List<T>` | 收集到 List |
| `collect(Collectors.toSet())` | `Set<T>` | 收集到 Set（自动去重） |
| `collect(Collectors.toMap(k,v))` | `Map<K,V>` | 收集到 Map |
| ⭐ `forEach(Consumer<T>)` | `void` | 对每个元素执行操作 |
| `count()` | `long` | 计数 |
| `anyMatch(Predicate)` / `allMatch` / `noneMatch` | `boolean` | 短路判断 |
| `findFirst()` / `findAny()` | `Optional<T>` | 找第一个 / 找任意一个 |
| `reduce(identity, BinaryOperator)` | `T` | 归约（累加/求积/拼接） |

## 四、Demo：从 for 循环到 Stream

```java
// 数据
List<String> names = List.of("Alice", "Bob", "Charlie", "Diana");

// ❌ 旧式 for 循环：5 行，意图淹没在控制流程里
List<String> result1 = new ArrayList<>();
for (String name : names) {
    if (name.length() > 3) {
        result1.add(name.toUpperCase());
    }
}

// ✅ Stream 流水线：3 个操作，一眼看出"过滤→转换→收集"
List<String> result2 = names.stream()
    .filter(name -> name.length() > 3)   // 保留长度>3的
    .map(String::toUpperCase)             // 转大写
    .collect(Collectors.toList());        // 收集到List
// result2 = ["ALICE", "CHARLIE", "DIANA"]
```

## 五、Collectors 常用方法

| 方法 | 说明 |
|------|------|
| ⭐ `toList()` | 收集到 List |
| `toSet()` | 收集到 Set |
| `toMap(Function keyMapper, Function valueMapper)` | 收集到 Map |
| ⭐ `joining(", ")` | 字符串拼接 |
| ⭐ `groupingBy(Function classifier)` | 分组，返回 `Map<K, List<T>>` |
| `partitioningBy(Predicate)` | 二分，返回 `Map<Boolean, List<T>>` |
| `counting()` | 计数 |
| `summarizingInt(ToIntFunction)` | 一次性统计 count/sum/min/max/avg |

## 六、面试官视角

> **Q1: Stream 的惰性求值是什么意思？**
> 中间操作（filter/map/sorted）只是声明"我要干嘛"，并不真的遍历数据。终端操作（collect/forEach/count）触发时，所有中间操作才一次性执行。这样可以做短路优化（比如 `filter().findFirst()` 找到第一个就停）。

> **Q2: 中间操作和终端操作怎么区分？**
> 看返回值：返回 Stream 的是中间操作（可以继续 `.`），返回其他类型或 void 的是终端操作。

> **Q3: 一个 Stream 能消费两次吗？**
> 不能。终端操作执行后流就关闭了，再操作抛 `IllegalStateException: stream has already been operated upon or closed`。

> **Q4: parallelStream 什么时候用？**
> 数据量大（10万+）、每个元素的操作是 CPU 密集且独立的。数据量小或操作有 I/O 阻塞时，并行反而更慢。
