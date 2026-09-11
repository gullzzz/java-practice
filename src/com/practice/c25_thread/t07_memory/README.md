# 多线程（七）：内存模型与无锁并发（volatile / ThreadLocal / CAS）

## 零、为什么有锁了，还是不够

前面 t03 你学了 `synchronized` / `Lock`，它解决的是**原子性**——同一时刻只让一个线程进临界区。

但多线程的坑其实有**三个**，锁只填了其中一个：

| 三大特性 | 是什么 | 典型翻车场景 | 谁来救 |
|---------|--------|-------------|--------|
| **原子性** | 一个操作不可再分 | `i++` 其实是"读-改-写"三步，两步之间别人插一脚 | 锁 / **CAS** |
| **可见性** | 一个线程改了，别的线程立刻能看到 | 线程A改了 `flag`，线程B的 `while(!flag)` 永远看不到 | **volatile** / 锁 |
| **有序性** | 代码按写的顺序执行 | CPU/编译器**指令重排**，顺序可能错乱 | **volatile** / 锁 |

> 本关三个主角正好各管一块：**volatile 管可见性+有序性，ThreadLocal 管线程隔离（从根上不共享），CAS 管无锁的原子性。**

### 关联类清单（自查遗漏）

| 类 | 归属 | 说明 |
|----|------|------|
| `volatile` | 关键字 | 可见性 + 禁止重排 |
| `ThreadLocal` | 类 | 线程私有副本 |
| `AtomicInteger` / `AtomicLong` | 原子类 | CAS 的无锁累加/更新 |
| `AtomicReference` | 原子类 | 原子更新引用类型 |
| `LongAdder` | 原子累加器 | 高并发下比 AtomicLong 吞吐更高 |

---

## 一、可见性：为什么线程改了，别人看不见

每个线程有自己的**工作内存**（可以理解为 CPU 缓存/寄存器），共享变量其实存在**主内存**里。线程干活时：

```
主内存  flag = false
   │
   ├── 线程A 读到缓存 flag=false ──► 改成 flag=true（写回自己缓存，还没刷回主内存）
   │
   └── 线程B 读到缓存 flag=false ──► while(!flag) 死循环！
```

线程 B 的 `while(!flag)` 可能**永远停在死循环**——因为 B 读的是自己缓存里那份旧的 `false`，A 改的 `true` 还没刷回主内存、B 也懒得去主内存重新读。

**这就是可见性问题：改了，但别人看不见。**

---

## 二、volatile ⭐（解决可见性 + 有序性）

### 是什么

`volatile` 是一个**字段修饰符**。给字段加上它，就告诉 JVM 两件事：

1. **可见性**：每次读这个字段，都强制从主内存读最新值；每次写，立刻刷回主内存。
2. **有序性**：禁止对这个字段的读写做**指令重排**（JVM 会插"内存屏障"）。

### 一句话记住它管什么、不管什么 ⭐

| | 能管 | 不能管 |
|---|---|---|
| volatile | ✅ 可见性 | ❌ **原子性**（`i++` 照样会错） |
| | ✅ 有序性（禁止重排） | |

> **关键认知：`volatile` 不能替代锁。** 它保证"改的值别人看得见"，但不保证"读-改-写"三步不被插队。`volatile int i = 0;` 然后两个线程 `i++`，结果照样会少——因为 `i++` 不是原子的。

### 经典场景 1：状态标志位（最常用）

```java
// 魔法交易所：一个线程喊停，另一个线程立刻收到信号
public class ShutdownFlag {
    private volatile boolean running = true;   // 不加 volatile，别的线程可能永远看不见 running=false

    public void run() {
        while (running) {                       // 检查标志
            // 干活
        }
        System.out.println("收到停业信号，收工");
    }

    public void stop() {
        running = false;                        // 别的线程一改，run() 立刻可见
    }

    public static void main(String[] args) {
        ShutdownFlag s = new ShutdownFlag();
        new Thread(s::run).start();
        // 主线程停业
        s.stop();
    }
}
```

### 经典场景 2：双重检查锁（DCL）单例里的 volatile

> **这段代码在干什么？——"单例模式"：一个类全程序只允许存在一个实例。**
> 典型场景：① 全局配置类（启动读一次配置文件，所有模块共用）② 数据库连接池（全程序一个池，大家借连接）③ 日志器 Logger（所有代码写进同一个日志目的地）④ 线程池（统一管理线程资源）。
> 共性一句话：**全局只需要一份、创建成本高、状态必须一致**。所以构造器设成 `private` 堵死 `new`，只留 `getInstance()` 一个入口。

下面按"从零到一"的顺序，一步步推出这段代码——每一步都说清"为什么要下一步"。

#### 第 0 步：单例对象的一生（先建立直觉）

`new Config()` 这件事，全程序**只发生一次**。看时间线：

```
程序启动             instance = null
第 1 次调用          是 null → new 出对象 → instance 指向它（从此不再 null）
第 2 次调用          不是 null → 不 new，直接返回同一个
第 3 次…第 100 万次   都不是 null → 都返回同一个
```

> **关键认知：`if (instance == null)` 这个判断，第一次是 true，之后永远是 false。** 对象建好之后，后面的调用都是"拿现成的"——这就是"早就建好了"的意思。

#### 第 1 步：最笨的写法——每次进来都上锁

```java
public static Config getInstance() {
    synchronized (Config.class) {          // 每次调用都要排队进锁
        if (instance == null) {
            instance = new Config();
        }
    }
    return instance;
}
```

这么写**能保证单例（正确）**，但慢：对象早就建好了，每个线程还是得排队进门，进去才发现"哦，早建好了"，又空手出来。

> **比喻**：图书馆只有一本《Java 圣经》。这个写法 = 每个人借书都得先去柜台排队登记，排到了才被告知"书就在书架"。书一直躺着，人天天排长队。

#### 第 2 步：加外层 if——让"有书的人"别排队

```java
public static Config getInstance() {
    if (instance == null) {                // "书还没上架"才需要去办上架
        synchronized (Config.class) {
            instance = new Config();
        }
    }
    return instance;                       // 书已上架，直接拿
}
```

外层 if 的方向（别搞反）：

| instance 的状态 | `if (instance == null)` 结果 | 动作 |
|----------------|------------------------------|------|
| 还是 null（书没上架） | true | **进去**排队上架 |
| 已建好（书已上架） | false | **跳过**排队，直接 return 拿 |

> 所以是"**没书的人进去上架，有书的人直接拿**"——门口挂块"书已上架"的牌子，大多数人瞄一眼直接走，不排队。

#### 第 3 步：加内层 if——防止排队进去的人重复 new

第 2 步有个漏洞：可能**好几个人同时发现"书没上架"**（都通过了外层 if），一起排队进锁。第一个进去的人 new 好了；后面的人进门后如果不再看一眼，会又 new 一个，把前面的覆盖掉——单例破了。

```java
public static Config getInstance() {
    if (instance == null) {                // 第 1 次检查：要不要去排队
        synchronized (Config.class) {
            if (instance == null) {        // 第 2 次检查：进去后再确认"有人已经建好了吗"
                instance = new Config();
            }
        }
    }
    return instance;
}
```

内层 if 的作用：**拦住那些"进门之前判过 null、进门时发现已经有人建好了"的线程。**

> **一句话分工：外层 if 提速（建好后不进锁），内层 if 保命（防重复 new）。**

#### 第 4 步：为什么还要 volatile

```java
private static volatile Config instance;   // 少了 volatile，可能拿到"半成品"
```

> `new Config()` 分三步：①分配内存 ②初始化对象 ③把引用赋给 instance。JVM 可能把 ②③ 重排成 ③②——另一个线程在 ③ 之后、② 之前拿到 `instance`，此时对象还没初始化完，用了就炸。`volatile` 禁止这个重排。

#### 最终完整代码

```java
public class Config {
    private static volatile Config instance;    // ⚠️ 必须 volatile，防指令重排

    private Config() {}

    public static Config getInstance() {
        if (instance == null) {                 // 第 1 次检查：性能（建好后不进锁）
            synchronized (Config.class) {
                if (instance == null) {         // 第 2 次检查：正确性（防重复 new）
                    instance = new Config();
                }
            }
        }
        return instance;
    }
}
```

---

## 三、ThreadLocal ⭐（线程隔离，从根上不共享）

### 是什么

`ThreadLocal` 给**每个线程**一份**独立的变量副本**。同一个 `ThreadLocal` 对象，线程 A 往里面存的值，线程 B 看不到——各存各的，互不干扰。

> 既然会"共享出问题"，那干脆**不共享**——这是和锁、volatile 完全相反的思路：后者是"共享但管好"，ThreadLocal 是"压根不共享"。

> **关键认知：ThreadLocal 不是装数据的容器，是"钥匙"。** ⭐
> 它自己肚子里不装值——值存在**每个线程对象内部的 Map**（`ThreadLocalMap`）里：key 是你的 `ThreadLocal` 对象，value 才是你存的数据。所以 `stamp.set("财务章")` 是"用 stamp 这把钥匙，打开**当前线程**自己的柜子，把值放进去"；`get()` 是从**当前线程**的柜子里取。钥匙（ThreadLocal 对象）只有一把，但每个线程的柜子各不相同——这才是"隔离"的机制来源。

### 方法速查表

| 方法签名 | 参数 | 返回值 | 说明 | 注意事项 |
|----------|------|--------|------|----------|
| ⭐ `get()` | 无 | `T` | 取当前线程的副本值 | 没 set 过则返回初始值（默认 null） |
| ⭐ `set(T value)` | `value` 要存的值 | `void` | 给当前线程存副本 | 只有当前线程看得见 |
| ⭐ `remove()` | 无 | `void` | 删掉当前线程的副本 | **用完必须 remove，防内存泄漏** |
| `withInitial(Supplier)` | `Supplier` 初始值工厂 | `ThreadLocal` | 静态工厂，设默认值 | 懒加载，第一次 get 才生成 |

### Demo：每个员工自己的印章

```java
// 魔法交易所：每个结算员有自己的印章，不共享
public class StampBox {
    private static final ThreadLocal<String> stamp = ThreadLocal.withInitial(() -> "普通章");

    public static void main(String[] args) {
        new Thread(() -> {
            stamp.set("财务专用章");
            System.out.println("结算员A 的章：" + stamp.get());   // 财务专用章
        }, "结算员A").start();

        new Thread(() -> {
            System.out.println("结算员B 的章：" + stamp.get());   // 普通章（B 没改过，拿默认值）
        }, "结算员B").start();
    }
}
```

> **典型应用：** 数据库连接（每个线程一个 Connection）、`SimpleDateFormat`（它线程不安全，用 ThreadLocal 给每线程一个）、Web 里的用户会话。

### ⚠️ 陷阱：内存泄漏（用完必须 remove）⭐ 面试必问

ThreadLocal 的值存在 `Thread` 对象里的一个 `ThreadLocalMap` 中，这个 Map 的 **key 是 ThreadLocal 的弱引用，value 是你的对象（强引用）**。

线程池里的线程是**长期存活**的——如果线程往 ThreadLocal 存了对象又不 `remove()`，这个 value 就跟着线程一直活着，GC 回收不掉 → **内存泄漏**。

> **铁律：用完 `finally { threadLocal.remove(); }`。** 尤其是线程池场景，忘了 remove 就是慢性内存泄漏。

---

## 四、CAS 与原子类 ⭐（无锁的原子性）

### 是什么

CAS = **Compare And Swap（比较并交换）**。它是一种**不加锁**也能保证原子性的机制：

```
CAS(期望值, 新值)：
    如果内存里的值 == 期望值  →  把值改成新值，返回 true
    否则                     →  什么都不做，返回 false
```

这一"比较+交换"是 **CPU 的一条原子指令**（`cmpxchg`）完成的，中途不会被插队——所以不用 `synchronized` 上锁，也能安全地做 `i++`。

> **乐观锁思想：** 上锁是"悲观"的（先假设有人抢，锁住再说）；CAS 是"乐观"的（先试着改，失败了再重试）。

### AtomicInteger 方法速查表（CAS 的现成封装）

| 方法签名 | 参数 | 返回值 | 说明 |
|----------|------|--------|------|
| `get()` | 无 | `int` | 取当前值 |
| `set(int)` | `newValue` | `void` | 直接设值（非原子组合） |
| ⭐ `incrementAndGet()` | 无 | `int` | `++i` 的原子版，先加后返回 |
| ⭐ `addAndGet(int)` | `delta` 增量 | `int` | 加一个数，原子 |
| ⭐ `compareAndSet(int expect, int update)` | `expect` 期望值 + `update` 新值 | `boolean` | **CAS 本体**：相等才改，返回是否成功 |
| `getAndIncrement()` | 无 | `int` | `i++` 的原子版，先返回后加 |

### Demo：不用锁的计数器

```java
// 魔法交易所：10 个线程各累加 1000 次，最终 = 10000
public class Counter {
    private static final AtomicInteger count = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {
        Thread[] threads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    count.incrementAndGet();     // 原子 ++，不用 synchronized
                }
            });
            threads[i].start();
        }
        for (Thread t : threads) t.join();
        System.out.println("最终计数：" + count.get());   // 10000
    }
}
```

> **还记得 t06 里 `AnnualSettlement` 的 `totalAmount.addAndGet(amount)` 吗？** 那正是 AtomicInteger——你当时"无意识地"用了 CAS，现在知道它的底层了。

### ⚠️ CAS 的 ABA 问题

CAS 只比较"值相等"，但"值相等"不代表"没被人动过"：

```
初始 A
线程1：读到 A
线程2：A → B → 又改回 A
线程1：CAS(期望 A, 新值) → 成功！但它不知道中间发生过 B
```

值从 A 变 B 又变回 A，CAS 以为没变。**单靠 CAS 更新 int 计数通常没事，但更新引用类型（栈、链表）时 ABA 可能出问题**——解决靠 `AtomicStampedReference`（带版本号）。

---

## 五、核心速查表（三合一汇总）

| 工具 | 管什么 | 一句话 | 注意 |
|------|--------|--------|------|
| `volatile` | 可见性 + 有序性 | 改了立刻看得见 | 不管原子性，`i++` 照样错 |
| `ThreadLocal` | 线程隔离 | 每线程一份私有副本 | 用完 `remove()` 防泄漏 |
| `AtomicInteger`（CAS） | 无锁原子性 | 不加锁也能安全 `i++` | 高并发累加可用 `LongAdder` |

---

## 面试官视角

> **Q1：volatile 能保证原子性吗？**  
> 不能。volatile 只保证可见性（改了看得见）和有序性（禁止重排）。`i++` 是"读-改-写"三步，volatile 管不住中间被插队，所以两个线程 `volatile int i; i++` 依然会丢。

> **Q2：synchronized 和 volatile 的区别？**  
> ① synchronized 保证原子性 + 可见性 + 有序性，但重（要上锁）；volatile 只保证可见性 + 有序性，不保证原子性，但轻。② volatile 只能修饰字段，synchronized 能修饰方法/代码块。③ 一句话：要互斥用锁，只要"状态标志"用 volatile。

> **Q3：什么是 CAS？它有什么问题？**  
> CAS = 比较并交换，CPU 原子指令，不加锁实现原子更新。问题：① ABA 问题（值变了又变回来，CAS 察觉不到）② 循环 CAS 在竞争激烈时自旋开销大 ③ 只能保证单个变量的原子性。

> **Q4：ThreadLocal 的原理和内存泄漏？**  
> 每个 Thread 内部有个 ThreadLocalMap，key 是 ThreadLocal 的弱引用、value 是强引用。线程池线程长期存活，若不 `remove()`，value 会一直占着内存 → 泄漏。解决：用完 `finally { threadLocal.remove(); }`。

> **Q5：AtomicInteger 和 synchronized 谁快？**  
> 竞争不激烈时 CAS 更快（无锁、无上下文切换）；竞争激烈时 CAS 会自旋空转，反而可能不如锁。高并发累加场景可用 `LongAdder`（分段累加，吞吐更高）。

> **Q6：为什么 DCL 单例里 instance 要加 volatile？**  
> 防止指令重排。`new Config()` 三步（分配内存、初始化、赋引用）可能被重排成"先赋引用、后初始化"，另一个线程拿到还没初始化完的对象就用了。volatile 禁止这个重排。
