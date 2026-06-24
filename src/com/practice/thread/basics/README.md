# 多线程（一）：线程创建与生命周期

## 零、为什么要创建线程

一句话：**不是为了"更快"，而是为了"不浪费等待时间"。**

程序的耗时操作分两种：

| 类型 | CPU 在干嘛 | 例子 |
|------|-----------|------|
| CPU 密集型 | 满负荷跑 | 加密运算、图像处理、复杂计算 |
| IO 密集型 | **闲等**（等磁盘、等网络、等数据库） | 读文件、调 HTTP 接口、查数据库 |

单线程下，`fakeFileRead()` 那 1.5 秒里 CPU 几乎在发呆——等磁盘把数据吐过来。如果这 1.5 秒里 CPU 能去干别的事（渲染界面、响应用户操作），整个程序就"感觉更快"了，虽然单个操作的时间没变。

```
单线程餐厅：  接单 → 炒菜(3min) → 上菜 → 接下一单 → 炒菜(3min)...
多线程餐厅：  主厨接单 → 帮厨炒菜 → 主厨继续接下一单...
```

> **关键认知：** 单核 CPU 上，多线程只是交替执行，总时间不会减少；IO 密集场景下，一个线程等 IO 时另一个线程可以占用 CPU，**吞吐量**提升。多核 CPU 上，可以真正**并行**执行。

---

## 一、什么是线程

**进程** = 操作系统分配资源的最小单位（一个 Java 程序 = 一个进程）  
**线程** = CPU 调度的最小单位（一个进程内可以有多个线程同时跑）

Java 程序启动时，JVM 自动创建一个 **main 线程** 执行 `main()` 方法。你手动创建的线程，可以和 main 线程交替/并行执行。

## 二、创建线程的两种方式

### 方式1：继承 Thread 类

```java
class MyThread extends Thread {
    @Override
    public void run() {
        System.out.println("子线程运行：" + Thread.currentThread().getName());
    }
}
// 启动：
MyThread t = new MyThread();
t.start();  // 启动新线程，JVM 会调用 run()
```

### 方式2：实现 Runnable 接口 ⭐ 推荐

```java
@FunctionalInterface
public interface Runnable {
    void run();  // 唯一抽象方法：无参数，无返回值
}
```

`Runnable` 是一个**函数式接口**——只有一个抽象方法 `run()`。它的语义是：**"我是一段可以被线程执行的任务"**。

为什么要实现它？因为 `Thread` 构造器需要知道"新线程该干什么活"。你把任务逻辑写在 `run()` 里，`Thread.start()` 之后 JVM 会在新线程里自动调用它。

用 Lambda 实现：
```java
Runnable task = () -> {                    // () 对应 run() 的空参数列表
    System.out.println("子线程运行：" +
        Thread.currentThread().getName()); // { ... } 对应 run() 的方法体
};
Thread t = new Thread(task);              // Thread 接受一个 Runnable
t.start();                                // 新线程启动，JVM 调用 task.run()
```

> **本质理解：三段式模式**  
> `Runnable` 封装任务逻辑 → Lambda 当场匿名实现 → `Thread` 只依赖接口调用 `run()`。  
> 这和你在 Lambda 章反复用的模式一模一样：  
> `Predicate` 封装判断逻辑 → Lambda 当场实现 → `filter` 负责调用。  
> 只是把"调用方"从 Stream 换成了 Thread。同一个设计模式，换个壳而已。

> ⚠️ **Runnable 的双重缺陷**  
> 
> ```java
> void run();  // 看这行签名，两个问题已经注定：
> ```
> 
> **① 无返回值** — 子线程算完结果，没法交回给主线程。主线程想拿后台算出来的数据，`run()` 是 `void`，什么都传不回来。  
> **② 异常传不回** — 子线程里抛的异常只存在于那个线程内部，`join()` 解除阻塞后主线程完全不知道它正常结束还是炸了。  
> 
> 这两个缺陷正是下一章 **Callable + Future + 线程池** 要解决的核心问题。

> **为什么推荐 Runnable 而非继承 Thread？**  
> ① 不占用继承名额（Java 单继承，继承了 Thread 就不能继承别的了）  
> ② 任务与线程解耦——同一个 Runnable 可以交给多个线程执行，也可以交给线程池  
> ③ 符合"组合优于继承"——"线程"是执行者，"任务"是被执行的东西，两者本就不该绑死

## 三、核心方法速查

### 第一组：创建与启动

| 方法签名 | 参数 | 返回值 | 说明 | 谁调 / 注意事项 |
|----------|------|--------|------|----------------|
| ⭐ `start()` | 无 | `void` | 真正创建新线程，JVM 随后调用 `run()` | 外部线程调；**永远别手动调 `run()`** |
| `run()` | 无 | `void` | 线程要执行的业务逻辑 | **JVM 自动调用**，你只负责覆写 |

### 第二组：暂停与等待

| 方法签名 | 参数 | 返回值 | 说明 | 使用场景 | 是否释放锁 |
|----------|------|--------|------|----------|:--:|
| ⭐ `Thread.sleep(long ms)` | `ms` — 毫秒数 | `void` | 让**当前线程**暂停指定毫秒 | 模拟耗时操作、定时轮询、限流 | ❌ 不释放 |
| ⭐ `t.join()` | 无 | `void` | 让**调用线程**等 `t` 执行完 | 主线程等所有子线程干完再汇总 | ❌ 不释放 |
| `t.join(long ms)` | `ms` — 最多等多久 | `void` | 限时等待，超时就继续走 | 等结果但不想无限死等 | ❌ 不释放 |

> **`sleep()` vs `join()` 本质区别：** `sleep(ms)` 是自己睡，睡够自己醒；`join()` 是等**别人**结束，别人结束你就醒。

### 为什么需要 sleep()？

**核心问题：** 轮询等待时，如果不用 sleep，线程会以每秒百万次的速度空转检查，CPU 烧到 100%，实际什么都没干。

```java
// 没有 sleep：CPU 空转地狱
while (!taskDone) {
    checkStatus();  // 一秒执行几百万次，CPU 全吃满
}

// 有 sleep：礼貌让出 CPU
while (!taskDone) {
    checkStatus();
    Thread.sleep(5000);  // 5 秒后再查，CPU 这 5 秒可以服务其他线程
}
```

> **本质：** `sleep()` 是**告诉操作系统调度器"这段时间别调度我"**。OS 会把 CPU 时间片分给其他线程。注意它不是精确计时器——实际暂停时间可能略长于参数值，取决于 OS 调度。

### 为什么需要 join()？

**核心问题：** 线程各自跑各自的，谁也不等谁。但如果主线程需要子线程的**计算结果**才能往下走，你就必须等。

```java
// 没有 join：子线程还在跑，主线程就结束了
Thread worker = new Thread(() -> {
    Thread.sleep(3000);
    System.out.println("终于算完了");
});
worker.start();
// 主线程直接结束 → JVM 退出 → worker 根本没机会跑完

// 有 join：主线程在汇合点等
worker.start();
worker.join();  // "我等 worker，它跑完我再走"
System.out.println("确认 worker 已完成，我的后续逻辑才能执行");
```

> **本质：** `join()` 是**线程间的依赖声明**——"B 线程的结果是 A 线程的输入，A 怎么知道 B 什么时候结束？"——join 就是这个问题的答案。

### 主线程 vs 子线程：一眼分清

```java
public static void main(String[] args) {
    System.out.println("A");           // ← main线程在执行

    Thread worker = new Thread(() -> {
        System.out.println("B");       // ← worker线程在执行
    });
    worker.start();

    System.out.println("C");           // ← main线程在执行
}
```

- **main 线程**：JVM 自动创建，执行 `main()` 方法。它也是普通线程，不特殊。
- **worker 线程**：你 `new Thread()` + `start()` 创建的。`run()` 里的代码谁调谁就是子线程。

输出可能是 `A → C → B`（worker 慢一步），也可能是 `A → B → C`（worker 抢到了 CPU）。但 `A` 一定在最前面。

### 怎么判断一个线程"跑完"了？

| 方式 | 用法 | 说明 |
|------|------|------|
| 看日志 | `run()` 最后一行打 `"XXX 结束"` | 最直观，控制台看到就是跑完了 |
| `t.isAlive()` | 返回 `boolean` | `true` = 还在跑，`false` = 已终止 |
| `t.join()` | 阻塞当前线程直到 `t` 结束 | 走到 `join()` 下一行时，`t` 一定已终止 |

**sleep() 典型用法：**
```java
// 定时轮询——每 5 秒检查一次，不让 CPU 空转
while (!taskDone) {
    checkStatus();
    Thread.sleep(5000);
}
```

`join()` 典型用法：
```java
// 等三个数据源都加载完，再开始计算
Thread loader1 = new Thread(() -> loadFromDB());
Thread loader2 = new Thread(() -> loadFromAPI());
loader1.start();
loader2.start();
loader1.join();  // main 在这里等 loader1
loader2.join();  // main 在这里等 loader2
// 两个都结束了，可以放心汇总
computeResult();
```

### 第三组：线程信息

| 方法签名 | 参数 | 返回值 | 说明 | 使用场景 |
|----------|------|--------|------|----------|
| ⭐ `Thread.currentThread()` | 无 | `Thread` | 获取**当前正在执行这段代码**的线程对象 | 日志里标注是谁干的活 |
| `t.getName()` | 无 | `String` | 获取线程名称 | 调试时区分线程 |
| `t.setName(String)` | `name` — 线程名 | `void` | 设置线程名称 | 给线程起有意义的名字 |

> **注意：** `currentThread()` 是 `static` 方法——它返回的是调用它的那个线程，不是随便哪个线程。

```java
// 多个线程干活，不打名字分不清谁是谁
Runnable task = () -> {
    String name = Thread.currentThread().getName();
    System.out.println(name + " 开始处理...");
    System.out.println(name + " 处理完毕");
};
```

### 第四组：守护线程

| 方法签名 | 参数 | 返回值 | 说明 | 使用场景 |
|----------|------|--------|------|----------|
| `t.setDaemon(true)` | `on` — true=守护 | `void` | 标记为守护线程 | 后台自动保存、心跳检测、GC |

> ⚠️ **关键规则：** ① 必须 `start()` 之前调用 ② JVM 在所有**非守护线程**结束后直接退出，不管守护线程跑完没有。

```java
Thread autoSaver = new Thread(() -> {
    while (true) {
        saveToDisk();
        Thread.sleep(60000);  // 每分钟自动存
    }
});
autoSaver.setDaemon(true);  // 必须在 start() 之前！
autoSaver.start();
// 所有非守护线程结束 → JVM 退出，autoSaver 自动跟着死
```

### 为什么要用守护线程？

**JVM 的退出规则：** 所有非守护线程结束时，JVM 直接退出，不管守护线程跑没跑完。

| 场景 | 为什么是守护 |
|------|-------------|
| GC 垃圾回收 | 你的代码跑完，回收就没意义了 |
| 后台自动保存 | 主程序关了，还存什么 |
| 心跳检测 | 没人在线，检测给谁看 |

```java
// 对比：非守护线程（默认）—— 死循环会让 JVM 永不退出
Thread worker = new Thread(() -> {
    while (true) {
        System.out.println("干活...");
        Thread.sleep(1000);
    }
});
worker.start();
// JVM 永远不会退出——worker 是默认的非守护，它不死 JVM 就不死
```

```java
// 对比：守护线程 —— JVM 不等你
Thread worker = new Thread(() -> {
    while (true) {
        System.out.println("干活...");
        Thread.sleep(1000);
    }
});
worker.setDaemon(true);     // 标记守护
worker.start();
Thread.sleep(5000);         // main 睡 5 秒
// main 醒来 → 唯一的非守护结束 → JVM 退出 → worker 被强制终止
```

### 两条铁律

1. **`setDaemon(true)` 必须在 `start()` 之前**——启动后再设直接抛 `IllegalThreadStateException`
2. **守护线程别做"必须完成"的事**——比如写文件写到一半 JVM 退出，文件就坏了
3. **守护线程创建的子线程默认也是守护**——传染规则

### 第五组：线程调度

| 方法签名 | 参数 | 返回值 | 说明 | 使用场景 | 是否释放锁 |
|----------|------|--------|------|----------|:--:|
| ⭐ `Thread.yield()` | 无 | `void` | 当前线程**主动让出** CPU，从 Running → Ready | 调试、优化轮询时的 CPU 占用 | ❌ 不释放 |
| `t.setPriority(int)` | `1`~`10`，默认`5` | `void` | 设置线程优先级，值越高越容易被调度 | 区分后台任务和前台任务的调度权重 | — |
| `t.getPriority()` | 无 | `int` | 获取优先级 | 调试 | — |

> ⚠️ **yield() 只是"建议"**——OS 调度器可以不理。它不等于阻塞，线程让出后立刻回到就绪队列，还是可能被再次选中。

> ⚠️ **优先级不保证**——Java 线程优先级映射到 OS 原生优先级，不同 OS 行为不一致。**不要用优先级做业务逻辑**——它只能作为调度的"倾向提示"，不能靠它保证执行顺序。

```java
// yield() 的典型场景：自旋等待时降低 CPU 占用
while (!dataReady) {
    Thread.yield();  // 让出 CPU，避免空转烧满核心
}

// 优先级：后台清理线程不该和前台业务抢 CPU
Thread cleanup = new Thread(() -> { /* 清理缓存 */ });
cleanup.setPriority(Thread.MIN_PRIORITY);  // = 1
cleanup.start();
```

> **总结：yield() 和优先级都是"建议性"的**——它们向调度器表达意图，但不强制执行。实际开发中 yield() 极少用，优先级主要用于区分后台任务（低优先级）和前台交互任务（高优先级）。

## 四、start() vs run() 的致命区别

```java
Thread t = new Thread(() -> System.out.println("A"));
t.run();   // 错误！run() 只是在当前线程执行方法，不会启动新线程
t.start(); // 正确！start() 才会真正创建新线程
```

## 五、一个小 Demo

```java
public class ThreadDemo {
    public static void main(String[] args) {
        System.out.println("main 线程启动");

        Thread worker = new Thread(() -> {
            System.out.println("worker 开始干活...");
            try {
                Thread.sleep(2000);  // 模拟耗时操作
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("worker 干完了！");
        });
        worker.start();

        System.out.println("main 不等人，先走一步");
    }
}
```

观察输出顺序，你会看到 main 确实没有等 worker。

## 六、线程的生命周期

```
NEW ──start()──> RUNNABLE ──run()结束──> TERMINATED
                   │    ↑
                   │    │ 锁可用/notify
                   ▼    │
                BLOCKED/WAITING/TIMED_WAITING
```

- **NEW**：`new Thread()` 之后，`start()` 之前
- **RUNNABLE**：`start()` 之后，等待 CPU 调度或正在运行
- **BLOCKED**：等待获取锁
- **WAITING**：`wait()` / `join()` 无限等待
- **TIMED_WAITING**：`sleep(ms)` / `join(ms)` 限时等待
- **TERMINATED**：`run()` 执行完毕

## 面试官视角

> **Q1：start() 和 run() 有什么区别？**  
> `start()` 创建新线程并调用 `run()`；直接调 `run()` 只是在当前线程执行，不启动新线程。

> **Q2：Thread 和 Runnable 怎么选？**  
> 优先 `Runnable`——不浪费单继承、任务可复用。能用 Lambda 写的都该用 Runnable。

> **Q3：sleep() 和 wait() 的区别？**  
> `sleep()` 是 Thread 的静态方法，不释放锁；`wait()` 是 Object 的方法，释放锁。必须持有锁才能调用 `wait()`。（wait 后续章节展开）

> **Q4：sleep() 和 join() 的区别？**  
> `sleep(ms)` 是当前线程自己睡，睡够自己醒；`join()` 是当前线程等另一个线程结束。两个都不释放锁。

> **Q5：守护线程和非守护线程的区别？**  
> JVM 在所有非守护线程结束后直接退出，不管守护线程是否跑完。`setDaemon(true)` 必须在 `start()` 之前调用。
