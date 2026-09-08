# 多线程（六）：JUC 同步工具类 CountDownLatch / CyclicBarrier / Semaphore

## 零、为什么需要这三个工具

上一关，你靠 `wait/notify` 手工协调线程——厨师睡、食客喊，代码要自己写 `while + 锁 + 唤醒`，麻烦又容易漏信号。

但很多协作场景是**固定套路**，不需要你每次手搓：

```java
场景A：主线程要等 10 个结算任务全部完成，才出总账          → 一等多
场景B：火箭发射，10 个引擎必须"同时"点火，谁也别抢跑        → 多等多
场景C：厨房只有 3 口锅，10 个厨师抢锅炒菜，最多 3 人同时用   → 一管多
```

这三类场景，Java 已经给你打包好了三个"开箱即用"的同步工具，都在 `java.util.concurrent` 包（简称 **JUC**）里。

**一句话理解 JUC 同步工具类：不用自己写 `while + wait/notify`，直接拿来用的"线程协作积木"。**

---

## 一、三个工具的关系图 ⭐（先建立心智模型）

| 工具 | 生活类比 | 核心机制 | 协作方向 | 能否复用 |
|------|---------|---------|---------|---------|
| **CountDownLatch** | 倒计时门闩 | 计数器归零才开门 | **一个线程等 N 个线程** | ❌ 一次性 |
| **CyclicBarrier** | 循环栅栏 | N 个人到齐才放行 | **N 个线程互相等** | ✅ 可循环 |
| **Semaphore** | 信号灯 / 许可证 | 拿不到证就排队 | **N 个线程抢 M 个资源** | ✅ 可复用 |

> **分辨口诀：**
> - CountDownLatch = "**等人**"——老板等 10 个员工干完活，`countDown` 是员工喊"我干完了"，老板 `await` 等计数归零。
> - CyclicBarrier = "**等人齐**"——10 个运动员等发令枪，人到齐了才能一起起跑，谁也不能抢跑。
> - Semaphore = "**等位子**"——餐厅 3 张桌，10 个顾客，进去一个占一张，出来一个腾一张。

### 关联类清单（自查遗漏）

`java.util.concurrent` 下同步辅助类一共 5 个：

| 类 | 本关覆盖 | 说明 |
|----|---------|------|
| CountDownLatch | ✅ 主角 | 倒计时门闩 |
| CyclicBarrier | ✅ 主角 | 循环栅栏 |
| Semaphore | ✅ 主角 | 信号量 |
| Exchanger | 提一句 | 两个线程交换数据（冷门） |
| Phaser | 提一句 | CyclicBarrier 增强版，支持多阶段 + 动态增减人数 |

> Exchanger / Phaser 面试少考、实际用得少，本关聚焦前三个。

---

## 二、CountDownLatch（倒计时门闩）⭐

### 是什么

一个**只能减、不能加、也不能重置**的计数器。构造时给定 `count`，每调一次 `countDown()` 减 1，减到 0 时，所有 `await()` 的线程被放行。

> 就像火箭发射的倒计时：`3、2、1、点火`——数到 0 之前，点火线程一直 `await()` 等；每个子任务完成就 `countDown()` 喊一声。

### 方法速查表

| 方法签名 | 参数 | 返回值 | 说明 | 注意事项 |
|----------|------|--------|------|----------|
| `new CountDownLatch(int count)` | `count` 初始计数 | 对象 | 构造，设定要等几个任务 | count 必须 ≥ 0 |
| ⭐ `countDown()` | 无 | `void` | 计数减 1 | 到 0 后继续调不报错，但没意义 |
| ⭐ `await()` | 无 | `void` | 阻塞，直到计数归 0 | 可被中断，抛 InterruptedException |
| `await(long, TimeUnit)` | `timeout` 时限 + `unit` 单位 | `boolean` | 限时等待，超时返回 `false` | 没等到就返回，别无限等 |
| `getCount()` | 无 | `long` | 返回当前剩余计数 | 调试用 |

### Demo：主线程等 3 个结算任务完成

```java
// 魔法交易所：老板等 3 个员工分别结算完，才出总账
public class SettlementGate {
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(3);   // 等 3 个任务

        for (int i = 1; i <= 3; i++) {
            final int id = i;
            new Thread(() -> {
                try {
                    Thread.sleep(1000);                  // 模拟结算耗时
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                System.out.println("员工" + id + "结算完成");
                latch.countDown();                       // 员工喊："我干完了"
            }).start();
        }

        latch.await();                                   // 老板等：计数归零才继续
        System.out.println("3 个任务全部完成，出总账！");
    }
}
```

> **关键：`countDown()` 和 `await()` 可以在不同线程、不同对象里各调各的**——员工调 `countDown`，老板调 `await`，它们共享同一个 `latch` 对象。

---

## 三、CyclicBarrier（循环栅栏）⭐

### 是什么

**一组线程互相等待**，等所有线程都到齐了（到达"栅栏点"），才一起继续往下走。

> 就像 10 个运动员各就各位，等最后一个人也蹲下，发令枪才响，大家一起起跑。跑完这一圈，栅栏自动重置，还能再等下一圈——所以叫"循环"（Cyclic）。

### 方法速查表

| 方法签名 | 参数 | 返回值 | 说明 | 注意事项 |
|----------|------|--------|------|----------|
| `new CyclicBarrier(int parties)` | `parties` 参与线程数 | 对象 | 构造，等几个线程到齐 | 线程数必须 ≥ 1 |
| `new CyclicBarrier(int parties, Runnable action)` | `parties` + `action` 到齐后要执行的动作 | 对象 | 到齐后**先执行 action** 再放行 | action 由最后一个到达的线程执行 |
| ⭐ `await()` | 无 | `int` | 到达栅栏并等待，返回自己的"到达序号" | 序号从 parties-1 递减到 0 |
| `await(long, TimeUnit)` | `timeout` + `unit` | `int` | 限时等待 | 超时抛 TimeoutException |
| `getParties()` | 无 | `int` | 需要多少个线程到齐 | 调试用 |
| `reset()` | 无 | `void` | 重置栅栏到初始状态 | 让还在等的线程抛 BrokenBarrierException |

### Demo：3 个引擎同时点火

```java
// 火箭发射：3 个引擎必须先各自"预热"完，再同时点火
public class RocketLaunch {
    public static void main(String[] args) {
        CyclicBarrier barrier = new CyclicBarrier(3, () ->
            System.out.println("所有引擎就绪，点火！🚀")
        );

        for (int i = 1; i <= 3; i++) {
            final int id = i;
            new Thread(() -> {
                try {
                    Thread.sleep(1000);                  // 模拟引擎预热耗时
                    System.out.println("引擎" + id + "预热完成，等待点火");
                    barrier.await();                     // 到达栅栏，等另外两个引擎
                    System.out.println("引擎" + id + "点火！");
                } catch (InterruptedException | BrokenBarrierException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }
    }
}
```

> **注意 CyclicBarrier 和 CountDownLatch 的最大区别：**
> - Latch 是**老板等员工**（一个人等一群人，单向）。
> - Barrier 是**员工互等**（一群人互相等，凑齐了才一起走，双向）。

---

## 四、Semaphore（信号量）⭐

### 是什么

一个**许可证池**。构造时给 `permits` 张许可证，线程干活前要先 `acquire()` 拿到一张，没有空余许可证就阻塞排队；干完 `release()` 还回去。

> 就像厨房只有 3 口锅：10 个厨师想炒菜，最多 3 个同时开火，第 4 个必须等有人腾出锅。

### 方法速查表

| 方法签名 | 参数 | 返回值 | 说明 | 注意事项 |
|----------|------|--------|------|----------|
| `new Semaphore(int permits)` | `permits` 许可证数 | 对象 | 构造，非公平模式（默认） | 允许"插队" |
| `new Semaphore(int permits, boolean fair)` | `permits` + `fair` 是否公平 | 对象 | 构造，公平模式按先来后到 | `fair=true` 性能略差 |
| ⭐ `acquire()` | 无 | `void` | 拿一张许可证，没有就阻塞 | 可被中断 |
| `acquire(int n)` | `n` 要拿几张 | `void` | 一次拿 n 张，不够就阻塞 | 用完记得对应还 n 张 |
| ⭐ `release()` | 无 | `void` | 还一张许可证 | 归还后唤醒一个等待者 |
| `release(int n)` | `n` 还几张 | `void` | 一次还 n 张 | 和 acquire(n) 配对 |
| `tryAcquire()` | 无 | `boolean` | 尝试拿一张，拿到返回 `true` | 不阻塞，拿不到立刻返回 false |
| `tryAcquire(long, TimeUnit)` | `timeout` + `unit` | `boolean` | 限时尝试，超时返回 `false` | 比 acquire 灵活 |
| `availablePermits()` | 无 | `int` | 还剩几张许可证 | 调试用 |

### Demo：3 口锅，10 个厨师抢着炒菜

```java
// 魔法厨房：只有 3 口锅，10 个厨师炒菜，最多 3 人同时开火
public class Kitchen {
    public static void main(String[] args) {
        Semaphore stoves = new Semaphore(3);    // 3 张"锅的许可证"

        for (int i = 1; i <= 10; i++) {
            final int id = i;
            new Thread(() -> {
                try {
                    stoves.acquire();                    // ① 拿一口锅（没有就排队等）
                    System.out.println("厨师" + id + "抢到锅，开始炒菜");
                    Thread.sleep(1000);                  // ② 炒菜耗时
                    System.out.println("厨师" + id + "炒完了");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    stoves.release();                    // ③ 还锅（必须在 finally，防许可证泄漏）
                }
            }).start();
        }
    }
}
```

> **关键：Semaphore 的许可证数量，就是"同时能有多少线程进入临界区"。** 这和上一关的 `Lock`（同时只能 1 个）是一脉相承的——Lock 相当于 `permits=1` 的特例。

---

### ⚠️ 陷阱：release 必须"对账"（拿过才还）⭐

`acquire()` 是可能**失败**的——线程排队等许可证时被 interrupt，会抛 `InterruptedException`，此时线程**根本没拿到许可证**。

如果把 `release()` 放进 `finally` 无条件执行，就会"没借却还"——许可证计数器凭空 +1，限流失效：

```java
// ❌ 错误：acquire 失败时，finally 仍执行 release
try {
    windows.acquire();      // 被打断，没拿到
    // 干活
} finally {
    windows.release();      // 没拿到却还了 → 3 个窗口变 4 个
}

// ✅ 正确：用 boolean 标志记"到底拿没拿到"
boolean acquired = false;
try {
    windows.acquire();
    acquired = true;        // 拿到后立刻标记
    // 干活
} finally {
    if (acquired) {
        windows.release();  // 只有真拿到才还
    }
}
```

> **为什么不能靠 Semaphore 的方法判断"我持没持有"？** Semaphore 没有这样的 API——`availablePermits()` 查的是"还剩几张"，不是"我拿没拿"。所以最直接的办法就是自己记一个 boolean。

### 对比：为什么 CountDownLatch 的 countDown 却可以无条件执行？

同样是 `finally`，`countDown()` 无条件执行反而是**必须的**：

| 工具 | 管的是什么 | 正确的账 |
|------|-----------|---------|
| **Semaphore** | **资源**（窗口/锅） | 拿了才还，**对账** |
| **CountDownLatch** | **进度**（任务结束没） | 结束了就报，**报信** |

`countDown()` 的含义不是"我成功了"，而是"我这单到此为止了，无论成败，把我从等待名单上划掉"。被打断的线程如果不 `countDown()`，计数永远归不了零，main 线程就永久 `await()`（这正是上一关"永久等待"的病）。

> **一句话：** 资源要"借还相抵"，进度要"有始有终"。

---

## 五、核心方法速查表（三合一汇总）

| 工具 | 核心方法 | 返回值 | 一句话作用 |
|------|---------|--------|-----------|
| CountDownLatch | `countDown()` | `void` | 计数减 1 |
| | `await()` | `void` | 阻塞直到计数归 0 |
| CyclicBarrier | `await()` | `int` | 到达栅栏，等齐了才放行 |
| Semaphore | `acquire()` | `void` | 拿许可证（阻塞） |
| | `release()` | `void` | 还许可证 |
| | `tryAcquire()` | `boolean` | 尝试拿，不阻塞 |

---

## 面试官视角

> **Q1：CountDownLatch 和 CyclicBarrier 有什么区别？**  
> ① 协作方向：Latch 是一个线程等 N 个线程（一等多），Barrier 是 N 个线程互相等（多等多）。② 能否复用：Latch 计数归零后不能重置，一次性；Barrier 到齐后自动重置，可循环用。③ 侧重点：Latch 强调"等任务做完"，Barrier 强调"等大家到齐同时出发"。

> **Q2：CountDownLatch 的计数能加回去吗？**  
> 不能。它只能 `countDown()` 递减，没有"加"的方法，归零后不能重置。要复用就用 CyclicBarrier。

> **Q3：Semaphore 和 synchronized/Lock 什么关系？**  
> synchronized/Lock 是"同一时刻只能 1 个线程进"；Semaphore 是"同一时刻最多 N 个线程进"。所以 `new Semaphore(1)` 就等价于一把锁——Semaphore 是锁的"限量版"推广，用来做**限流**。

> **Q4：Semaphore 的公平模式（fair=true）是什么？**  
> 公平模式下，等待拿许可证的线程按"先来后到"排队，先等的先拿；非公平（默认）允许新来的线程插队。公平保证顺序但性能略差，非公平吞吐更高。和 `ReentrantLock(true)` 一个道理。

> **Q5：CyclicBarrier 的 await() 返回 int 是什么？**  
> 返回这个线程的"到达序号"（从 parties-1 递减到 0）。最后一个到达的线程返回 0，你可以用这个序号让"最后到的线程"负责执行一些收尾动作（比如打印"到齐了"）。

> **Q6：如果 CyclicBarrier 等待时某个线程中断或超时了怎么办？**  
> 会抛 `BrokenBarrierException`，栅栏进入"损坏"状态，其他还在等的线程也会被唤醒并抛同样的异常。可以用 `reset()` 恢复，但要保证没有线程还在等。
