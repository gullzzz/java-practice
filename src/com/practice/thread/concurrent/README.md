# 多线程（四）：并发集合

## 零、为什么需要并发集合

上一关你用 synchronized 保护了 `goldTotal++`。那集合呢？多个线程同时往一个 ArrayList 里 add，会怎样？

```java
List<Integer> list = new ArrayList<>();
// 10 个线程，每个 add 1000 次
// 结果：要么抛异常（数组越界），要么元素数 < 10000（丢元素）
```

普通集合（`ArrayList`、`HashMap`）不是为并发设计的，多线程下会：**丢数据、抛异常、甚至死循环**（JDK7 的 HashMap 扩容链表成环）。

**解决办法两个方向：**

| 方案 | 做法 | 缺点 |
|------|------|------|
| 自己加锁 | `synchronized(list) { list.add(x); }` | 每处访问都要记得加锁，容易漏 |
| 用并发集合 | `ConcurrentHashMap`、`CopyOnWriteArrayList` | 内置线程安全，直接用 |

> **关键认知：** 并发集合不是"给普通集合套一层锁"，而是**专门为并发重新设计的数据结构**，性能远高于"套锁版"。

---

## 一、ConcurrentHashMap ⭐

线程安全的 HashMap。JDK8 之后用 **CAS + synchronized** 实现，锁的粒度是**单个哈希桶**（不是整个表）——并发读几乎无锁，并发写也只锁冲突的那几个桶。

```java
ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
map.put("gold", 100);
map.get("gold");                        // 读操作不加锁，性能极高
map.putIfAbsent("gold", 200);           // 原子：不存在才 put，存在则返回旧值
map.computeIfAbsent("silver", k -> 0);  // 原子：不存在才计算
```

> **putIfAbsent 的价值：** 普通 HashMap 要"不存在才 put"，得先 `containsKey` 再 `put`，两步不是原子的，并发下会出问题。`putIfAbsent` 把两步合成一步原子操作。

---

## 二、CopyOnWriteArrayList ⭐

**写时复制**：每次写操作（add/set/remove），先复制一份**新数组**，在新数组上改，最后把引用指向新数组。读操作永远读**旧数组**，完全不加锁。

```java
CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>();
list.add("交易记录1");  // 写：复制新数组 + 改 + 换引用
list.get(0);            // 读：直接读当前数组，不加锁
```

| | 优势 | 劣势 |
|---|---|---|
| CopyOnWriteArrayList | 读快（无锁）、读写不互斥 | 写慢（每次复制整个数组）、占内存、读到可能是旧数据 |

> **适用场景：读多写少**——监听器列表、白名单/黑名单、配置项。**不适合**写频繁的场景（每次写都复制整个数组，开销巨大）。

---

### 补充：锁的粒度（粗 vs 细）

**粒度 = 锁的范围大小。**

| | 粗粒度 | 细粒度 |
|---|---|---|
| 类比 | 整个图书馆一把大锁 | 每个书架各自上锁 |
| 典型 | Hashtable 锁整个表 | ConcurrentHashMap 锁单个桶 |
| 并发性能 | 差——无关操作也要抢同一把锁 | 高——操作不同数据各锁各的，互不阻塞 |

> 线程 A 操作桶1、线程 B 操作桶7：Hashtable 下它俩抢同一把全表锁（互相等待）；ConcurrentHashMap 下各锁各的桶（互不干扰）。

---

## 三、对比速查

### HashMap vs Hashtable vs ConcurrentHashMap

| | HashMap | Hashtable | ConcurrentHashMap |
|---|---|---|---|
| 线程安全 | ❌ | ✅（锁整个表） | ✅（锁单个桶） |
| 性能 | 高 | 低（全表锁） | 高（细粒度锁） |
| 允许 null | 允许 | ❌ | ❌ |
| 推荐 | 单线程 | 已过时 | 并发场景 ⭐ |

### ArrayList vs Vector vs CopyOnWriteArrayList

| | ArrayList | Vector | CopyOnWriteArrayList |
|---|---|---|---|
| 线程安全 | ❌ | ✅（每个方法 synchronized） | ✅（写时复制） |
| 性能 | 高 | 低（每方法加锁） | 读快写慢 |
| 推荐 | 单线程 | 已过时 | 读多写少 ⭐ |

> **口诀：** Hashtable 和 Vector 是 JDK1.0 的老古董，全表/每方法加锁，性能差，已被 ConcurrentHashMap 和 CopyOnWriteArrayList 取代。

---

## 四、核心方法速查表

### ConcurrentHashMap

| 方法签名 | 参数 | 返回值 | 说明 | 使用场景 |
|----------|------|--------|------|----------|
| ⭐ `put(K, V)` | `key` + `value` | V（旧值） | 存入键值对 | 常规写入 |
| ⭐ `get(K)` | `key` | V | 读取，**无锁** | 常规读取 |
| ⭐ `putIfAbsent(K, V)` | `key` + `value` | V（旧值） | **原子**：不存在才 put | 幂等写入、防覆盖 |
| `remove(K)` | `key` | V | 删除 | 常规删除 |
| `computeIfAbsent(K, Function)` | `key` + 计算函数 | V | **原子**：不存在才计算 | 懒加载、缓存 |
| `merge(K, V, BiFunction)` | `key` + 值 + 合并函数 | V | **原子**：累加/合并 | 计数器累加 |

### CopyOnWriteArrayList

| 方法签名 | 参数 | 返回值 | 说明 |
|----------|------|--------|------|
| ⭐ `add(E)` | 元素 | `boolean` | 写时复制，线程安全 |
| ⭐ `get(int)` | 下标 | E | 无锁读取，可能读到旧值 |
| `remove(Object)` | 元素 | `boolean` | 写时复制 |
| `iterator()` | 无 | Iterator | 迭代的是**快照**，遍历期间其他线程写不影响本次遍历 |

---

## 五、一个小 Demo

```java
// 魔法交易所：并发统计各商品交易次数（无需手动加锁）
ConcurrentHashMap<String, Integer> counter = new ConcurrentHashMap<>();

for (int i = 0; i < 10; i++) {
    new Thread(() -> {
        for (int j = 0; j < 1000; j++) {
            counter.merge("gold", 1, Integer::sum);  // 原子累加
        }
    }).start();
}
// 最终 counter.get("gold") 稳定 = 10000
```

---

## 面试官视角

> **Q1：ConcurrentHashMap 和 Hashtable 的区别？**  
> Hashtable 锁整个表，性能差；ConcurrentHashMap JDK8 用 CAS + synchronized 锁单个桶，读无锁、写细粒度，并发性能高。

> **Q2：CopyOnWriteArrayList 的原理和适用场景？**  
> 写时复制：写操作复制新数组，读操作无锁读旧数组。适合读多写少，不适合写频繁（每次写复制整个数组）。

> **Q3：HashMap 为什么线程不安全？**  
> 并发 put 会丢数据；JDK7 扩容时可能死循环（链表成环）。并发场景用 ConcurrentHashMap。

> **Q4：putIfAbsent 和先 containsKey 再 put 有什么区别？**  
> 前者是原子操作；后者两步非原子，并发下会出问题。putIfAbsent 一步完成"不存在才写入"。
