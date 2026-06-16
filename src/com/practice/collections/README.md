  # 阶段 8：集合框架

配套代码：`src/com/practice/collections/CollectionsDemo.java`

---

## 速查索引

| 集合类型 | 常用方法快速跳转 |
|----------|-----------------|
| List | [8.2 下方方法表](#82-list列表) |
| Set | [8.3 下方方法表](#83-set集合) |
| Map | [8.4 下方方法表](#84-map映射) |
| Queue / Deque | [8.5 Queue/Deque](#85-queue--deque队列与双端队列) |
| 遍历方式 | [8.6 遍历集合的四种方式](#86-遍历集合的四种方式) |
| Collections工具类 | [8.7 Collections 工具类](#87-collections-工具类) |
| 集合选型 | [8.9 集合选型速查](#89-集合选型速查) |

---

## 8.1 集合框架概述

**定义**：Java 集合框架（JCF）是一套统一的数据结构体系，包含了接口（Collection、List、Set、Map 等）、具体实现类（ArrayList、HashSet、HashMap 等）和工具类（Collections、Arrays）。核心接口分为两大体系——Collection（存储单值元素）和 Map（存储键值对）。

**解决问题/用途**：数组只能定长存储，增删修改极不方便，没有现成的排序、去重、查找方法。集合框架提供了一站式解决方案——动态扩容的 List、自动去重的 Set、键值存储的 Map、FIFO 模式的 Queue，加上 Collections 工具类的排序/打乱/查找/线程安全包装等能力。你需要什么数据结构，就选什么集合实现，基本不需要手写数据结构。

```
Collection(接口)
├── List(接口) — 有序，可重复
│   ├── ArrayList   — 数组实现，查快
│   └── LinkedList  — 链表实现，增删快
├── Set(接口) — 无序，不可重复
│   ├── HashSet     — 哈希表，最快
│   ├── TreeSet     — 红黑树，自动排序
│   └── LinkedHashSet — 保持插入顺序
└── Queue(接口) — 队列

Map(接口) — 键值对
├── HashMap       — 哈希表，无序
├── TreeMap       — 红黑树，按键排序
└── LinkedHashMap — 保持插入顺序
```

**泛型**：`List<String>` 指定集合中元素的类型，编译器会做类型检查。

---

## 8.2 List（列表）

**定义**：List 是有序、可重复的元素集合，按插入顺序存储，通过从0开始的整数索引访问元素。最常用的三个实现——ArrayList（动态数组，查快）、LinkedList（双向链表，增删快）、Vector（线程安全但已过时）。

**解决问题/用途**：List 是日常开发中最常用的集合类型。当你需要"有顺序的一组东西，允许重复"时就用它——购物车商品列表、搜索结果列表、数据库返回的多行记录。索引访问让随机获取第 N 个元素只需 O(1)，是业务代码的主力数据结构。

#### ArrayList —— 动态数组，查快，首选

**定义**：ArrayList 底层是一个可自动扩容的数组（Object[]）。创建时默认容量为 10（或通过构造器指定），添加元素时如果容量不够，自动扩容为原来的 1.5 倍。通过整数索引直接访问元素——`list.get(5)` 等价于数组的 `arr[5]`，O(1)。

**解决问题/用途**：ArrayList 是日常开发中最常用的 List 实现，90% 的场景用它就够了。当你需要"有顺序的一组数据，经常按索引拿第 N 个、偶尔添加删除"时选它。典型场景——数据库查询结果列表、Web 请求参数列表、批量处理的数据集合。随机访问 O(1) 让它遍历和按索引取值极快。

```java
List<String> list = new ArrayList<>();

list.add("Java");           // 添加
list.add(1, "Python");     // 在位置1插入
list.get(0);               // 按索引访问
list.set(0, "C++");        // 修改
list.remove("Python");     // 删除
list.indexOf("Java");      // 查找索引
list.size();               // 大小
list.contains("Java");     // 是否包含
```

- 底层是**动态数组**，查询 O(1)，插入/删除 O(n)
- 默认容量 10，超出后自动扩容 1.5 倍

#### LinkedList —— 双向链表，增删快，可当队列/栈

**定义**：LinkedList 底层是双向链表，每个节点存有数据、前驱指针和后继指针。查元素从头部或尾部开始遍历（O(n)），但给定节点后插入和删除只需改指针（O(1)）。同时实现了 List 和 Deque 接口，一个类具备三种身份——List（列表）、Queue（队列）、Deque（栈/双端队列）。

**解决问题/用途**：需要频繁在头部/中间插入删除时用 LinkedList——实现一个"最近使用"列表（最新放最前）、不断从头部消费数据的任务队列。但注意随机访问 O(n)，用 `get(5000)` 会从头遍历 5000 次。大多数场景 ArrayList 更快（内存连续、CPU 缓存友好），LinkedList 只在特定场景有优势。

```java
LinkedList<String> linked = new LinkedList<>();
linked.addFirst("最新");   // 头部插入，O(1)
linked.addLast("最旧");    // 尾部插入，O(1)
linked.pollFirst();        // 头部取出，O(1) — 当队列用
linked.push("栈顶");       // 头部压入 — 当栈用
```

- 底层是**双向链表**，查询 O(n)，插入/删除 O(1)
- 同时实现了 `Deque` 接口，可当队列/栈使用

### List 完整方法速查

#### 增（添加元素）

| 方法 | 说明 | 返回值 |
|------|------|--------|
| `add(E e)` | 追加到末尾 | boolean（永远true） |
| `add(int index, E e)` | 插入到指定位置 | void |
| `addAll(Collection c)` | 把另一个集合全部追加到末尾 | boolean |

```java
List<String> list = new ArrayList<>();
list.add("A");              // [A]
list.add(0, "B");           // [B, A] — 插入到位置0
list.addAll(List.of("C", "D")); // [B, A, C, D]
```

#### 删（删除元素）

| 方法 | 说明 | 返回值 |
|------|------|--------|
| `remove(int index)` | 删除指定位置的元素 | 被删除的元素 |
| `remove(Object o)` | 删除第一个匹配的元素 | boolean（是否删到） |
| `clear()` | 清空所有元素 | void |
| `removeAll(Collection c)` | 删除与另一个集合相同的所有元素 | boolean |
| `removeIf(Predicate p)` | 按条件删除（Java 8+） | boolean |

```java
list.remove(0);              // 删除位置0的元素
list.remove("A");            // 删除第一个"A"
list.removeIf(s -> s.length() > 3);  // 删除长度>3的字符串
list.clear();                // 清空
```

#### 改（修改元素）

| 方法 | 说明 | 返回值 |
|------|------|--------|
| `set(int index, E e)` | 替换指定位置的元素 | 被替换的旧元素 |
| `replaceAll(UnaryOperator op)` | 对所有元素做统一变换（Java 8+） | void |

```java
list.set(0, "New");          // 位置0改成"New"
list.replaceAll(String::toUpperCase); // 全部变成大写
```

#### 查（查询元素）

| 方法 | 说明 | 返回值 |
|------|------|--------|
| `get(int index)` | 获取指定位置的元素 | E |
| `size()` | 元素个数 | int |
| `isEmpty()` | 是否为空 | boolean |
| `contains(Object o)` | 是否包含该元素 | boolean |
| `indexOf(Object o)` | 第一个匹配元素的位置 | int（找不到返回-1） |
| `lastIndexOf(Object o)` | 最后一个匹配元素的位置 | int（找不到返回-1） |

```java
String s = list.get(0);      // 取第一个元素
int n = list.size();         // 有多少个
boolean empty = list.isEmpty();  // 是不是空的
boolean has = list.contains("A"); // 里面有没有"A"
int pos = list.indexOf("A");     // "A"在哪个位置
```

#### 遍历相关

| 方法 | 说明 | 返回值 |
|------|------|--------|
| `forEach(Consumer c)` | Lambda遍历（Java 8+） | void |
| `iterator()` | 获取迭代器 | Iterator\<E\> |
| `listIterator()` | 获取双向迭代器（仅List有） | ListIterator\<E\> |
| `toArray()` | 转成数组 | Object[] |

#### 转换

| 方法 | 说明 |
|------|------|
| `Arrays.asList(T... a)` | 数组→List（固定大小，不能add/remove） |
| `List.of(T... a)` | 数组→不可变List（Java 9+，不能修改） |
| `new ArrayList<>(Arrays.asList(...))` | 创建可修改的List |

```java
// 快捷创建（固定写法）
List<String> list = new ArrayList<>(Arrays.asList("A", "B", "C"));
// Java 9+ 更简单
List<String> list2 = new ArrayList<>(List.of("A", "B", "C"));
```

### 选择原则
| 场景 | 选择 |
|------|------|
| 频繁随机访问 | ArrayList |
| 频繁头部/中间插入删除 | LinkedList |
| 大多数情况 | ArrayList（默认选择） |

---

## 8.3 Set（集合）

**定义**：Set 是不允许重复元素的集合，基于数学上的集合概念。元素的相等性通过 equals() 和 hashCode() 方法判定。常用实现——HashSet（哈希表，O(1)，无序）、TreeSet（红黑树，O(log n)，自动排序）、LinkedHashSet（维护插入顺序）。

**解决问题/用途**：当"重复"是问题而非特性时用 Set——存储所有用户的ID（不能重复）、统计一篇文章用了多少个不同的词、判断某个元素是否在集合中。HashSet 的 O(1) 查找速度让"是否包含"操作极快，适合黑名单/白名单类场景。

```java
Set<String> set = new HashSet<>();

set.add("apple");          // 添加
set.add("apple");          // 重复 → 不会加入
set.remove("apple");       // 删除
set.contains("apple");     // 是否包含
set.size();                // 大小
```

### 三种实现对比

| 实现 | 顺序 | 性能 | 适用场景 |
|------|------|------|----------|
| HashSet | 无序 | O(1) | 只需要去重 |
| TreeSet | 自然排序 | O(log n) | 需要排序 |
| LinkedHashSet | 保持插入顺序 | O(1) | 需要记住插入顺序 |

**放入 Set 的对象必须重写 `equals()` 和 `hashCode()`**。

#### HashSet —— 哈希表，只关心"有没有"，不关心"谁先谁后"

**定义**：HashSet 基于 HashMap 实现，底层是哈希表（数组+链表+红黑树）。元素存储位置由 hashCode() 计算得出，不保证迭代顺序与插入顺序一致。增删查平均时间复杂度都是 O(1)，是所有 Set 实现中最快的。

**解决问题/用途**：当你只需要"去重+快速判断是否存在"，完全不关心元素顺序时，HashSet 是首选。比如——用户黑名单（只关心 ID 是不是在黑名单里）、访问去重（同一个 IP 同一天只计一次）、词汇集合（一篇文章用了哪些不同的词）。O(1) 的 contains 操作让"存在性判断"快到几乎零开销。

```java
Set<String> blacklist = new HashSet<>();
blacklist.add("spam_user_123");
blacklist.add("spam_user_456");
boolean isBlocked = blacklist.contains("spam_user_123"); // true, O(1) 极快
```

#### TreeSet —— 红黑树，自动排序

**定义**：TreeSet 基于 TreeMap 实现，底层是红黑树（自平衡二叉搜索树）。元素按自然顺序（Comparable）或自定义比较器（Comparator）排序存储。增删查时间复杂度 O(log n)，比 HashSet 慢但能让元素始终保序。

**解决问题/用途**：当"去重+排序"同时需要时用 TreeSet——学生成绩从低到高排列去重、排行榜 Top N（取前几名）、按字母序输出所有用户名。TreeSet 还提供了额外方法如 `first()`（最小值）、`last()`（最大值）、`headSet(e)`（小于 e 的子集）、`tailSet(e)`（大于等于 e 的子集），让范围操作很便利。

```java
Set<Integer> scores = new TreeSet<>();
scores.add(85);
scores.add(92);
scores.add(78);
scores.add(92);   // 重复，忽略
// 输出: [78, 85, 92]  ← 自动从小到大排序
```

**注意**：放入 TreeSet 的元素必须能比较（要么实现 Comparable，要么构造时提供 Comparator），否则运行时会抛 ClassCastException。

#### LinkedHashSet —— 哈希表+链表，记住插入顺序

**定义**：LinkedHashSet 继承自 HashSet，底层在哈希表基础上额外维护了一条双向链表来记录元素的插入顺序。增删查 O(1)，性能略低于 HashSet（额外链表开销），同时迭代顺序与插入顺序一致。

**解决问题/用途**：去重的同时还需要"按照加入的顺序"遍历元素。比如——用户收藏的文章列表（不能重复收藏，但要按收藏时间顺序展示）、页面访问历史（同一个页面去重，但要保持首次访问顺序）。它提供了"HashSet 的速度 + 可预测的迭代顺序"。

```java
Set<String> favorites = new LinkedHashSet<>();
favorites.add("文章C");  // 最先收藏
favorites.add("文章A");
favorites.add("文章B");
favorites.add("文章C");  // 重复，忽略
// 迭代顺序: 文章C → 文章A → 文章B  ← 保持插入顺序
```

### Set 完整方法速查

| 方法 | 说明 | 返回值 |
|------|------|--------|
| `add(E e)` | 添加元素（重复的不会加入） | boolean（是否真的加了） |
| `remove(Object o)` | 删除元素 | boolean |
| `contains(Object o)` | 是否包含 | boolean |
| `size()` | 元素个数 | int |
| `isEmpty()` | 是否为空 | boolean |
| `clear()` | 清空 | void |
| `addAll(Collection c)` | 批量添加 | boolean |
| `retainAll(Collection c)` | 只保留与另一个集合相同的元素（取交集） | boolean |

```java
Set<String> set = new HashSet<>();
set.add("apple");     // true — 加进去了
set.add("apple");     // false — 重复，没加进去
set.size();           // 1
set.contains("apple"); // true
```

---

## 8.4 Map（映射）

**定义**：Map 是键值对存储结构，每个键（key）映射到一个值（value），键不可重复，值可以重复。通过键快速查找对应的值（O(1)）。常用实现——HashMap（哈希表，无序，最常用）、TreeMap（按键排序）、LinkedHashMap（按插入顺序）。

**解决问题/用途**：当数据需要通过"名字"而非"位置"来访问时用 Map——用户ID查用户信息、配置项key查配置值、统计每个单词出现的次数。Map 的本质是高效字典/查找表，现实世界的大量数据都是键值对形式。

```java
Map<String, Integer> map = new HashMap<>();

map.put("张三", 85);       // 添加/修改
map.get("张三");           // 取值（不存在返回null）
map.getOrDefault("赵六", 0); // 取值，给默认值
map.containsKey("李四");   // 是否包含key
map.containsValue(100);    // 是否包含value
map.remove("王五");        // 删除
map.size();                // 大小
```

### 遍历 Map

```java
// 方式1：entrySet（推荐）
for (Map.Entry<String, Integer> entry : map.entrySet()) {
    System.out.println(entry.getKey() + "=" + entry.getValue());
}

// 方式2：forEach + Lambda
map.forEach((k, v) -> System.out.println(k + "=" + v));

// 方式3：只遍历key或value
for (String key : map.keySet()) { ... }
for (Integer val : map.values()) { ... }
```

### Map 完整方法速查

#### 基础操作

| 方法 | 说明 | 返回值 |
|------|------|--------|
| `put(K key, V value)` | 添加/修改键值对 | V（旧值，首次返回null） |
| `get(Object key)` | 根据key取值 | V（不存在返回null） |
| `getOrDefault(Object key, V default)` | 取值，不存在给默认值 | V |
| `remove(Object key)` | 根据key删除 | V |
| `clear()` | 清空 | void |
| `size()` | 键值对个数 | int |
| `isEmpty()` | 是否为空 | boolean |

```java
Map<String, Integer> map = new HashMap<>();
map.put("A", 1);           // {"A"=1}
map.put("A", 2);           // 覆盖 → {"A"=2}
int v = map.getOrDefault("B", 0); // B不存在，返回0
```

#### 判断

| 方法 | 说明 | 返回值 |
|------|------|--------|
| `containsKey(Object key)` | 是否包含该key | boolean |
| `containsValue(Object value)` | 是否包含该value | boolean |

#### 批量操作

| 方法 | 说明 |
|------|------|
| `putAll(Map m)` | 把另一个Map全部复制进来 |
| `putIfAbsent(K key, V value)` | key不存在才放，已存在就不覆盖 |
| `replace(K key, V value)` | 替换（key必须已存在） |
| `forEach(BiConsumer action)` | Lambda遍历 |

```java
map.putIfAbsent("A", 100);  // A已存在 → 不放，还是原来的值
map.putIfAbsent("C", 100);  // C不存在 → 放进去
```

### 三种Map实现对比

| 实现 | 顺序 | 特点 |
|------|------|------|
| HashMap | 无序 | 最常用，O(1) |
| TreeMap | 按key排序 | key必须能比较 |
| LinkedHashMap | 保持插入顺序 | 额外维护链表记录顺序 |

#### HashMap —— 哈希表，最快最常用

**定义**：HashMap 底层是哈希表（数组+链表+红黑树，JDK 8+）。通过 key 的 hashCode() 计算存储位置，增删查平均 O(1)。不保证 key-value 对的迭代顺序。允许一个 null key 和多个 null value。线程不安全。

**解决问题/用途**：HashMap 是 Java 中最常用的数据结构之一。绝大多数"key→value"映射场景都用它——用户ID→用户信息、缓存、配置项映射、请求参数解析。O(1) 的查找速度让它适合高频率的键值查找。

```java
Map<String, Integer> scores = new HashMap<>();
scores.put("Alice", 95);
scores.put("Bob", 82);
int aliceScore = scores.get("Alice"); // 95, O(1)
```

**放入 key 的对象必须正确重写 equals() 和 hashCode()**，否则相同内容的 key 会被当作不同的 key。

#### TreeMap —— 红黑树，按键排序

**定义**：TreeMap 底层是红黑树，所有 key-value 对按照 key 的自然顺序（Comparable）或自定义比较器（Comparator）排序存储。增删查 O(log n)。不允许 null key，允许 null value。

**解决问题/用途**：需要"按 key 排序"的映射场景用 TreeMap——按字母序输出用户名→积分、按日期排序的事件日志、需要取"最近/最远的某条数据"。TreeMap 额外提供 `firstKey()`（最小key）、`lastKey()`（最大key）、`headMap(k)`（小于 k 的子映射）、`tailMap(k)`（大于等于 k 的子映射）等方法，方便做范围查询。

```java
Map<String, Integer> sortedMap = new TreeMap<>();
sortedMap.put("Charlie", 78);
sortedMap.put("Alice", 95);
sortedMap.put("Bob", 82);
// 迭代顺序: Alice → Bob → Charlie  ← 按键的字母序
```

#### LinkedHashMap —— 哈希表+链表，记住插入顺序或访问顺序

**定义**：LinkedHashMap 继承自 HashMap，额外维护一条双向链表记录 key-value 对的顺序。默认按插入顺序排列。构造器支持指定为访问顺序模式（accessOrder=true），最近被访问的条目会移到链表尾部，可用于实现 LRU 缓存。

**解决问题/用途**：需要"有序 Map"的场景用 LinkedHashMap——记住配置项的添加顺序、按插入时间展示数据。访问顺序模式是 LRU 缓存（最近最少使用淘汰）的标准 Java 实现方式，LinkedHashMap 提供了 `removeEldestEntry()` 方法让你控制何时淘汰最旧的条目。

```java
Map<String, Integer> orderedMap = new LinkedHashMap<>();
orderedMap.put("step3", 3);
orderedMap.put("step1", 1);
orderedMap.put("step2", 2);
// 迭代顺序: step3 → step1 → step2  ← 保持插入顺序

// LRU 缓存（accessOrder=true）
Map<String, String> cache = new LinkedHashMap<>(16, 0.75f, true) {
    @Override
    protected boolean removeEldestEntry(Map.Entry eldest) {
        return size() > 100;  // 超过100条自动删最旧的
    }
};
```

---

## 8.5 Queue / Deque（队列与双端队列）

**定义**：Queue 是先进先出（FIFO）的数据结构——元素从队尾加入，从队头取出。Deque 是双端队列，两端都可以加入和取出，还可以当作栈（LIFO）使用。推荐使用 ArrayDeque 作为实现（比 LinkedList 性能更好）。

**解决问题/用途**：Queue 模拟排队场景——任务调度系统、消息队列消费者、BFS 算法中逐层处理节点。Deque 的栈模式替代了老旧的 Stack 类（Stack 是 Vector 子类，继承了很多不必要的方法，设计有问题）。

### Queue（队列）— 先进先出（FIFO）

```java
Queue<String> queue = new LinkedList<>();

queue.offer("A");   // 入队（推荐，失败返回false）
queue.offer("B");
queue.offer("C");
// 队列: [A, B, C] ←队尾    A ←队头

String head = queue.poll();   // 出队，A被取出（空队列返回null）
String peek = queue.peek();   // 只看队头，不取出（空队列返回null）
```

### Queue 常用方法

| 方法 | 说明 | 空队列行为 |
|------|------|-----------|
| `offer(E e)` | 入队（加到队尾） | 返回false |
| `poll()` | 出队（取走队头） | 返回null |
| `peek()` | 偷看队头（不取走） | 返回null |
| `size()` | 元素个数 | 0 |
| `isEmpty()` | 是否为空 | true |

> add/remove/element 也能用，但失败时抛异常，推荐用 offer/poll/peek

### Deque（双端队列）— 两头都能操作

```java
Deque<String> deque = new LinkedList<>();

// 当队列用（FIFO）
deque.offerLast("A");   // 加到队尾
deque.pollFirst();      // 从队头取

// 当栈用（LIFO）— 推荐替代老旧的Stack类
deque.push("A");        // 压栈（加到头部）
String top = deque.pop(); // 弹栈（从头部取）
String peek = deque.peek(); // 看栈顶
```

### Deque 常用方法速查

| 操作 | 队头方法 | 队尾方法 |
|------|---------|---------|
| 插入 | `offerFirst(e)` / `push(e)` | `offerLast(e)` / `offer(e)` |
| 取出 | `pollFirst()` / `pop()` | `pollLast()` |
| 偷看 | `peekFirst()` / `peek()` | `peekLast()` |
| 删除首次出现 | `removeFirstOccurrence(o)` | `removeLastOccurrence(o)` |

### ArrayDeque vs LinkedList

**定义**：ArrayDeque 底层是循环数组（可自动扩容），LinkedList 底层是双向链表。两者都实现了 Deque 接口，都可以当队列（FIFO）或栈（LIFO）使用。ArrayDeque 因为内存连续、访问更快，是 JDK 官方推荐的 Deque 实现。

**解决问题/用途**：Java 提供了 Stack 类和 Queue 接口，但 Stack 是 Vector 的子类（继承了很多不该有的方法，设计有缺陷），直接用 LinkedList 当队列开销量偏大。ArrayDeque 提供了一个纯粹的、高效的"双端队列"实现——既可以当栈（替代 Stack）又可以当队列（替代 LinkedList 的队列用法），内存更少、速度更快。

| 实现 | 底层 | 性能 |
|------|------|------|
| ArrayDeque | 循环数组 | 更快，推荐 |
| LinkedList | 双向链表 | 额外内存开销 |

```java
Deque<String> deque = new ArrayDeque<>();  // 推荐
```

---

## 8.6 遍历集合的四种方式

**定义**：Java 提供了多种遍历集合的方式——增强 for（最简洁）、Iterator 迭代器（支持安全删除）、forEach+Lambda（函数式风格）、for+索引（仅适用于 List 的随机访问）。每种方式有各自的适用场景。

**解决问题/用途**：遍历是集合操作中最基础的需求。增强 for 是大部分场景的首选，简洁安全。Iterator 的优势是可以在遍历过程中安全删除元素（直接调用 list.remove() 会抛 ConcurrentModificationException）。forEach+Lambda 适合一行表达式处理。

```java
List<String> list = Arrays.asList("A", "B", "C");

// 1. 增强 for（最常用）
for (String s : list) { }

// 2. Iterator（可在遍历中安全删除）
Iterator<String> it = list.iterator();
while (it.hasNext()) {
    String s = it.next();
    if (condition) it.remove();
}

// 3. forEach + Lambda（Java 8+）
list.forEach(s -> System.out.println(s));

// 4. for + 索引（仅List）
for (int i = 0; i < list.size(); i++) {
    String s = list.get(i);
}
```

**不要在 for-each 中直接 remove，用 Iterator 的 remove！**

---

## 8.7 Collections 工具类

**定义**：`java.util.Collections` 是集合框架的静态工具类，提供了一系列操作集合的静态方法——排序/反转/打乱（改变顺序）、max/min/frequency/binarySearch（查找统计）、线程安全包装、空集合与单元素集合等。

**解决问题/用途**：集合实现了增删改查，但排序、打乱、找极值等操作不应该每个集合类型自己实现一遍。Collections 把这些通用算法集中在一起，任何 List/Set 都能调用。就像 Arrays 工具类之于数组。

`java.util.Collections` 是一组静态方法，专门操作集合。

### 排序与顺序

| 方法 | 说明 |
|------|------|
| `sort(list)` | 自然顺序排序（元素必须实现Comparable） |
| `sort(list, comparator)` | 按自定义规则排序 |
| `reverse(list)` | 反转列表顺序 |
| `shuffle(list)` | 随机打乱 |
| `swap(list, i, j)` | 交换两个位置的元素 |
| `rotate(list, distance)` | 循环移动（正数右移，负数左移） |
| `fill(list, obj)` | 用同一个对象填满整个列表 |

```java
List<Integer> nums = new ArrayList<>(Arrays.asList(3, 1, 4, 1, 5));
Collections.sort(nums);                // [1, 1, 3, 4, 5]
Collections.sort(nums, Comparator.reverseOrder()); // [5, 4, 3, 1, 1]
Collections.reverse(nums);             // 反转
Collections.shuffle(nums);             // 随机打乱
Collections.swap(nums, 0, 1);          // 交换位置0和1
Collections.rotate(nums, 2);           // 整体右移2位
```

### 查找统计

| 方法 | 说明 |
|------|------|
| `max(collection)` | 最大值 |
| `min(collection)` | 最小值 |
| `frequency(collection, obj)` | 元素出现次数 |
| `binarySearch(list, key)` | 二分查找（**list必须先排序**） |
| `disjoint(c1, c2)` | 两个集合是否完全没有交集 |
| `indexOfSubList(source, target)` | 子列表在父列表中的首次位置 |

```java
int max = Collections.max(nums);       // 最大值
int count = Collections.frequency(nums, 1); // 1出现了几次
int idx = Collections.binarySearch(nums, 3); // 二分查找3的位置
```

### 线程安全包装

| 方法 | 说明 |
|------|------|
| `synchronizedList(list)` | 包一层，变成线程安全的List |
| `synchronizedSet(set)` | 包一层，变成线程安全的Set |
| `synchronizedMap(map)` | 包一层，变成线程安全的Map |

```java
List<String> syncList = Collections.synchronizedList(new ArrayList<>());
// syncList 现在可以在多线程环境使用了
```

### 空集合与单元素集合

| 方法 | 说明 |
|------|------|
| `emptyList()` | 返回不可变的空List |
| `emptySet()` | 返回不可变的空Set |
| `emptyMap()` | 返回不可变的空Map |
| `singletonList(obj)` | 只有一个元素的不可变List |
| `singleton(obj)` | 只有一个元素的不可变Set |

```java
List<String> empty = Collections.emptyList();
List<String> oneItem = Collections.singletonList("唯一的");
```

---

## 8.8 不可变集合（Java 9+）

**定义**：使用 `List.of()`、`Set.of()`、`Map.of()` 创建的集合是不可变的——创建后不能添加、删除或修改元素。这是 Java 9 引入的便捷工厂方法。

**解决问题/用途**：程序中很多集合是"常量"——星期名称、状态码映射、配置项列表。用可变集合 + final 只是引用不可变，集合内容仍可改。`List.of()` 真正做到了集合内容和引用都不可变，同时语法简洁，一行创建。

```java
List<String> list = List.of("A", "B", "C");
Set<String> set = Set.of("X", "Y", "Z");
Map<String, Integer> map = Map.of("one", 1, "two", 2);
```

简单创建，不可修改。适合常量集合。

---

## 8.9 集合选型速查

- 需要有序、可重复 → `ArrayList`
- 需要去重 → `HashSet`
- 需要键值对 → `HashMap`
- 需要排序 → `TreeSet` / `TreeMap`
- 线程安全 → `ConcurrentHashMap` / `CopyOnWriteArrayList`
