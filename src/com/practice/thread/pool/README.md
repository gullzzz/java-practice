# 多线程（二）：线程池

## 零、为什么需要线程池

上一章你学会了用 `new Thread(task).start()` 创建线程。但这个做法有两个致命问题：

### 问题1：线程是昂贵的

```
创建线程 ≈ 向 OS 要一块独立的内存栈空间
销毁线程 ≈ 回收这块空间 + 通知 OS 调度器清理

如果你的服务器每秒来 1000 个请求，每个请求都 new Thread → 销毁 →
一秒内创建销毁 1000 个线程 → OS 内存被栈空间吃满 → 系统崩溃
```

### 问题2：线程数量不可控

```java
// 假设这样处理用户请求——来一个请求就开一个线程
while (true) {
    Socket request = server.accept();
    new Thread(() -> handle(request)).start();  // 无上限！
}
// 100 个并发请求 = 100 个线程
// 10000 个并发请求 = 10000 个线程 → OS 直接 OOM
```

### 线程池的思路

**复用线程，控制数量：**

```
传统方式：  任务来 → 创建线程 → 执行 → 销毁线程 → 下个任务 → 又创建...
线程池：    提前创建好 N 个线程 → 任务来 → 找空闲线程执行 → 用完不销毁，归还池中
```

就像一个餐厅——不是每来一个客人就招一个新厨师做完菜就开除，而是**养 5 个固定厨师**，来单就分配，没单就歇着。

---

## 一、核心接口体系

```
    接口                      实现类                      工厂类
    ────                      ─────                      ─────

    Executor              ThreadPoolExecutor           Executors
    (只做)                  (具体怎么做)              (快速创建)
       │                        │                         │
  execute()              corePoolSize=5             newFixedThreadPool(5)
       │                maxPoolSize=10              newCachedThreadPool()
       ↓               workQueue=...               newSingleThreadExecutor()
  ExecutorService           handler=拒绝策略
    (能管理)
       │
  submit()  ← 能拿返回值
  shutdown() ← 能关闭
  awaitTermination() ← 能等关闭

       ↓
  ThreadPoolExecutor
    (能调参)
```

**一句话区分：**

| | 是什么 | 一句话 |
|---|---|---|
| `Executor` | 接口 | "我能执行任务"——就一个 `execute(Runnable)`，扔进去就完 |
| `ExecutorService` | 接口，继承 Executor | "我不仅能执行，还能管理"——多了 `submit`（有返回值）、`shutdown`（能关闭）、`awaitTermination`（等关闭） |
| `ThreadPoolExecutor` | 实现类 | "具体怎么执行、怎么管理，我来做"——构造器配核心线程数、最大线程数、队列、拒绝策略 |
| `Executors` | 工厂工具类 | "你不用亲手 new ThreadPoolExecutor，我帮你调好默认参数"——3行变1行 |

**层级关系：执行 → 管理 → 配置，逐级扩展。**

> ⚠️ **易混淆：** `Executors`（带 s，工厂类）≠ `Executor`（不带 s，接口）。前者是创建后者的工具，就像 `Collections` 是操作 `Collection` 的工具类。

---

## 二、Executors 四种快速创建方式

### 为什么用 ExecutorService 当变量类型？

```java
// 用执行器接口当类型——功能太少
Executor pool = Executors.newFixedThreadPool(5);
pool.execute(task);   // ✅ Executor 有 execute
pool.submit(task);    // ❌ 编译报错——Executor 没有 submit
pool.shutdown();      // ❌ 编译报错——Executor 没有 shutdown

// 用管理接口当类型——常用的都有 ↑ 推荐
ExecutorService pool = Executors.newFixedThreadPool(5);
pool.execute(task);   // ✅ 继承了 Executor 的 execute
pool.submit(task);    // ✅ ExecutorService 自己定义的
pool.shutdown();      // ✅ ExecutorService 自己定义的
```

> **和 List 同理：** `List<String> list = new ArrayList<>()` —— 编译器只看变量声明的类型，不看实际对象。`Executor pool` 手里就算拿着 `ThreadPoolExecutor` 对象，也只能调 `Executor` 定义的那几个方法。

### 2.1 newFixedThreadPool — 固定大小 ⭐ 最常用

```java
ExecutorService pool = Executors.newFixedThreadPool(5);
// 特点：核心线程 = 最大线程 = n，多余任务进无界队列排队
// 场景：长期稳定并发——后台数据处理、批量任务
```

### 2.2 newCachedThreadPool — 弹性扩容

```java
ExecutorService pool = Executors.newCachedThreadPool();
// 特点：核心=0，最大=Integer.MAX_VALUE，空闲60s回收
// 场景：短期大量突发任务——短平快的请求处理
// ⚠️ 危险：无界创建线程，请求暴增时可能撑爆内存
```

### 2.3 newSingleThreadExecutor — 单线程

```java
ExecutorService pool = Executors.newSingleThreadExecutor();
// 特点：就一个线程，保证任务按提交顺序串行执行
// 场景：要求顺序执行的场景——日志写入、消息队列消费
```

### 2.4 newScheduledThreadPool — 定时调度

```java
ScheduledExecutorService pool = Executors.newScheduledThreadPool(3);
// 特点：支持延迟执行 + 定时执行
// 场景：定时任务——心跳检测、定时拉取数据
```

**四种池子对比：**

| 工厂方法 | 核心线程 | 最大线程 | 队列 | 适用场景 |
|----------|:--:|:--:|------|----------|
| `newFixedThreadPool(n)` | n | n | 无界 LinkedBlockingQueue | 稳定并发量 |
| `newCachedThreadPool()` | 0 | Integer.MAX | 同步移交 SynchronousQueue | 突发短任务 |
| `newSingleThreadExecutor()` | 1 | 1 | 无界 LinkedBlockingQueue | 顺序执行 |
| `newScheduledThreadPool(n)` | n | Integer.MAX | 延迟工作队列 | 定时/周期任务 |

> ⚠️ **阿里巴巴开发手册禁止直接用 Executors 创建线程池**——原因：无界队列可能 OOM，最大线程数不设限也有 OOM 风险。生产环境应直接 `new ThreadPoolExecutor(...)` 显式指定所有参数。但学习阶段先用 Executors 理解行为，下一节再深入 ThreadPoolExecutor。

---

## 三、核心方法速查

### 第一组：提交任务

| 方法签名 | 参数 | 返回值 | 说明 |
|----------|------|--------|------|
| ⭐ `execute(Runnable)` | `command` — 任务 | `void` | 执行任务，无返回值 |
| ⭐ `submit(Callable<T>)` | `task` — 有返回值任务 | `Future<T>` | 提交任务，可通过 Future 获取结果 |
| `submit(Runnable)` | `task` — 任务 | `Future<?>` | 提交无返回值任务，返回的 Future.get() 为 null |
| `submit(Runnable, T result)` | `task` + `result` — 预设返回值 | `Future<T>` | 任务完成后 Future.get() 返回预设值 |

> **execute vs submit：** execute 是 Executor 接口定义的，只接收 Runnable，无返回值。submit 是 ExecutorService 扩展的，可接收 Callable，返回 Future。

### 第二组：关闭线程池

| 方法签名 | 参数 | 返回值 | 说明 |
|----------|------|--------|------|
| ⭐ `shutdown()` | 无 | `void` | **温柔关闭**——已提交的任务继续执行完，不接受新任务 |
| `shutdownNow()` | 无 | `List<Runnable>` | **暴力关闭**——尝试中断所有线程，返回未执行的任务列表 |
| `isShutdown()` | 无 | `boolean` | 是否已调用 shutdown/shutdownNow |
| `isTerminated()` | 无 | `boolean` | 所有任务是否已执行完毕 |
| ⭐ `awaitTermination(long, TimeUnit)` | `timeout` + `unit` | `boolean` | 阻塞等待线程池终止，超时返回 false |

> **标准关闭模式：** `shutdown()` → `awaitTermination(30, SECONDS)` → 超时则 `shutdownNow()`

### 第三组：Future 接口 — 获取异步结果 ⭐

| 方法签名 | 参数 | 返回值 | 说明 |
|----------|------|--------|------|
| ⭐ `get()` | 无 | `T` | **阻塞**等待任务完成并返回结果 |
| `get(long, TimeUnit)` | `timeout` + `unit` | `T` | 限时等待，超时抛 TimeoutException |
| `isDone()` | 无 | `boolean` | 任务是否完成（正常结束/异常/取消都算） |
| `cancel(boolean)` | `mayInterruptIfRunning` | `boolean` | 尝试取消任务 |

> **Future 的本质：** 一个"未来结果的占位符"——submit 当场返回 Future，但真正的结果要在后台线程算完之后才能 `get()` 到。就像快递单号：你下单当场拿到单号，但包裹要等仓库发货、运输、派送后才能到你手上。

---

## 四、Runnable 的缺陷被 Callable 弥补

回顾第一章 README 里标注的 Runnable 双重缺陷：

```java
void run();  // ① 无返回值  ② 异常传不回
```

**Callable<T> 直接解决：**

```java
@FunctionalInterface
public interface Callable<V> {
    V call() throws Exception;  // 有返回值 + 可以抛异常
}
```

| | Runnable | Callable |
|---|---|---|
| 方法签名 | `void run()` | `V call() throws Exception` |
| 返回值 | 无 | 有，泛型指定 |
| 异常 | 不能抛 Checked 异常 | 可以抛 |
| 提交方式 | `execute()` / `submit()` | 只能 `submit()` |
| 获取结果 | 永远拿不到 | `Future.get()` |

```java
// 对比：Runnable 算完拿不到结果
Runnable task = () -> {
    int sum = 1 + 2;  // 算了也没用，传不回去
};
Future<?> f = pool.submit(task);
f.get();  // null——没有返回值

// Callable：算完通过 Future 传回来
Callable<Integer> task = () -> {
    int sum = 1 + 2;
    return sum;            // ← 有返回值！
};
Future<Integer> f = pool.submit(task);
Integer result = f.get();  // 3——拿到了！
```

---

## 五、线程池工作原理

```
任务提交 → ① 核心线程未满？→ 创建核心线程执行
              ↓ 核心线程都忙
           ② 队列未满？→ 放入工作队列等待
              ↓ 队列也满了
           ③ 最大线程未满？→ 创建临时线程执行
              ↓ 最大线程也满了
           ④ 执行拒绝策略 RejectedExecutionHandler
```

**关键参数（ThreadPoolExecutor 构造器）：**

| 参数 | 含义 |
|------|------|
| `corePoolSize` | 核心线程数——常驻线程，即使空闲也不销毁（除非 allowCoreThreadTimeOut） |
| `maximumPoolSize` | 最大线程数——核心线程 + 临时线程的上限 |
| `keepAliveTime` | 临时线程空闲多久后被回收 |
| `workQueue` | 工作队列——核心线程忙时任务在这里排队 |
| `threadFactory` | 线程工厂——自定义创建线程（命名、优先级、守护） |
| `handler` | 拒绝策略——线程池满 + 队列满时怎么处理新任务 |

---

## 六、一个小 Demo

```java
// 魔法交易所：用5个线程并行分析100笔交易
ExecutorService pool = Executors.newFixedThreadPool(5);
List<Future<BigDecimal>> futures = new ArrayList<>();

for (int i = 0; i < 100; i++) {
    final int tradeId = i;
    Callable<BigDecimal> task = () -> {
        // 模拟分析一笔交易
        Thread.sleep(new Random().nextInt(500));
        return BigDecimal.valueOf(tradeId * 100);  // 返回计算结果
    };
    Future<BigDecimal> future = pool.submit(task);
    futures.add(future);
}

// 汇总所有结果
BigDecimal total = BigDecimal.ZERO;
for (Future<BigDecimal> f : futures) {
    total = total.add(f.get());  // get() 阻塞等待每个任务完成
}
System.out.println("总计: " + total);

pool.shutdown();
```

---

## 面试官视角

> **Q1：submit() 和 execute() 有什么区别？**  
> `execute(Runnable)` 无返回值，是 Executor 接口定义的。`submit()` 可接收 Callable/Runnable，返回 Future，是 ExecutorService 定义的。

> **Q2：Executors 创建线程池有什么问题？**  
> `newFixedThreadPool` 和 `newSingleThreadExecutor` 用无界队列，可能堆积请求导致 OOM。`newCachedThreadPool` 最大线程数不设限，也可能 OOM。生产环境应直接使用 `ThreadPoolExecutor` 构造器显式指定参数。

> **Q3：线程池的工作流程是怎样的？**  
> 核心线程 → 工作队列 → 临时线程 → 拒绝策略，四步逐级兜底。

> **Q4：Callable 和 Runnable 有什么区别？**  
> Runnable 的 `run()` 无返回值、不能抛 Checked 异常；Callable 的 `call()` 有返回值、可以抛异常。Runnable 用 `execute`/`submit`，Callable 只能用 `submit`，结果通过 `Future.get()` 获取。

> **Q5：怎么合理设置线程池大小？**  
> CPU 密集型：`N+1`（N = CPU 核心数）。IO 密集型：`2*N`（经验公式，实际需压测）。一切以压测为准。
