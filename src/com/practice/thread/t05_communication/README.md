# 多线程（五）：线程通信 wait/notify / Condition

## 零、为什么需要线程通信

前面几关，线程各干各的——收银员存钱、线程池结算，互不搭理。但真实世界里线程常常要**协作**：一个线程干到一半，要等另一个线程给它信号才能继续。

最经典的例子——**生产者-消费者**：

```java
// 魔法交易所：厨师（生产者）做包子，食客（消费者）吃包子
// 厨师做完一个，食客才能吃；食客吃完了，厨师才能做下一个
// 但蒸笼只有一个位置：满了厨师要停，空了食客要停
```

如果没有"通信"机制，厨师和食客只能靠 `while(true)` 死循环轮询"蒸笼空了吗？"——浪费 CPU 空转。

**线程通信 = 线程之间互相发信号**："我做完了，你可以继续了" / "我等你，你做完了叫我"。

Java 提供两套机制：

```java
① wait / notify / notifyAll    —— 是 Object 类的方法，配合 synchronized 使用（老牌）
② Condition.await / signal     —— 是 JUC 里的接口，配合 Lock 使用（升级版）
```

| | wait/notify | Condition |
|---|---|---|
| 配合谁 | `synchronized` | `Lock` |
| 定义在 | `Object`（所有对象都有） | `Lock.newCondition()` 生成 |
| 唤醒粒度 | 只能随机唤醒一个 / 全唤醒 | 可以**多个条件队列**，精准唤醒 |

> **关键认知：** 线程通信的本质是"**等待**（我没法继续，先睡）+ **唤醒**（条件满足了，起来干活）"。它和上一关的"锁"是两码事——锁管的是"同一时刻谁进临界区"，通信管的是"线程之间怎么配合传递信号"。

---

## 一、wait / notify 机制（Object 的方法）

### 三个方法

```java
Object lock = new Object();   // 任何对象都能当"通信的信物"，因为它有这三个方法

synchronized (lock) {
    lock.wait();        // ① 当前线程释放锁，进入 WAITING，睡到有人唤醒
    lock.notify();      // ② 随机唤醒一个正在 wait(lock) 的线程
    lock.notifyAll();   // ③ 唤醒所有正在 wait(lock) 的线程
}
```

> **一睡一喊，永远是一对：** `wait()` 睡的是**自己**，`notify()` 喊的是**别人**——所以睡下去的线程**自己醒不过来**，必须靠**另一个线程**来喊。而且喊和睡必须作用在**同一把锁对象**上（同一个对讲机频道），否则喊声听不见，线程永远睡死。

### 铁律三条 ⭐

**规则1：必须在 `synchronized` 块内调用。** 否则抛 `IllegalMonitorStateException`。

> 为什么？因为 `wait()` 要"释放锁"，你得**先持有锁**才有锁可释放——`synchronized` 就是先拿到这把锁的监视器。

**规则2：`wait()` 会释放锁，`sleep()` 不会。** 这是它俩最本质的区别（详见第六节）。

**规则3：被唤醒后，要重新抢锁才能继续。** `wait()` 的线程醒来不是马上执行，而是先回到锁的竞争队列，抢到锁了才从 `wait()` 后面继续走。

### 监视器的两个队列（wait 之后到底发生了什么）⭐

`wait()` 释放锁，和"唤醒线程"，是**两件独立的事**——`wait()` 只让自己睡，从不叫醒别人（叫醒是 `notify` 的活）。一个锁对象内部有两个队列：

| 队列 | 线程状态 | 怎么进来 | 怎么出去 |
|------|---------|---------|---------|
| **入口队列** Entry Set | `BLOCKED` | 在 `synchronized(lock)` 门口排队抢锁 | 抢到锁就进临界区 |
| **等待队列** Wait Set | `WAITING` | 调了 `lock.wait()` | 被 `notify()` 挪回入口队列 |

- 调 `wait()` 的线程：释放锁 → 进**等待队列**（WAITING），锁变空闲。
- 锁空闲后，入口队列里 BLOCKED 的线程会**自动**抢到锁（这不需要 notify，是锁竞争机制）。
- `notify()` 不是"把锁交给线程"，而是把线程从等待队列**挪回入口队列**重新抢锁。
- 一直没人 `notify()` → 线程永远停在 WAITING。**注意：这不是死锁**——锁仍空闲、别人照常用，只是这个线程没人叫它，睡死了。

### 等待/通知的标准配合套路

```java
// 等待方（消费者）
synchronized (lock) {
    while (条件不满足) {   // ⚠️ 用 while，不是 if！
        lock.wait();
    }
    // 条件满足了，干活
}

// 通知方（生产者）
synchronized (lock) {
    // 改变条件
    lock.notifyAll();   // 唤醒等待方
}
```

---

## 二、虚假唤醒（spurious wakeup）⭐ 面试必问

**虚假唤醒 = 线程没收到 `notify`，却自己醒过来了。** JVM 允许这种"幽灵唤醒"存在（底层 OS 调度导致）。

但比"虚假唤醒"更实在、每天都会遇到的是：**多个线程同时被 `notifyAll` 叫醒，资源却只有一个**。两种情况都逼着同一件事——**醒来必须重新检查条件，所以用 `while` 不用 `if`**。

用「1 个包子、2 个食客」讲清楚：

```java
// 蒸笼里刚放 1 个包子，厨师 notifyAll() 把食客 A、B 都叫醒

// 食客A 抢到锁：有包子！拿走（hasBun = false）→ 释放锁
// 食客B 抢到锁：从 wait() 后面醒过来，继续往下走……

// ❌ 食客B 写的是 if：
if (!hasBun) {
    lock.wait();   // 这行早就执行过了，醒来不会回头再检查
}
// B 直接去拿包子 → 包子早被 A 拿走 → 拿到 null → 崩！

// ✅ 食客B 写的是 while：
while (!hasBun) {
    lock.wait();   // 醒来回头再问一次"还有包子吗？"
}
// → 发现包子没了 → 重新 wait() 睡回去 → 安全！
```

> **关键：`wait()` 醒来后，条件可能已经变了。** 从"被叫醒"到"抢到锁"之间，别的线程可能已经把资源抢走。所以醒来第一件事是**重新核实条件**，而不是想当然往下走。
>
> **口诀：wait 永远包在 while 里，醒来先问"条件真的满足了吗"。** `if` 只检查一次，`while` 醒来重新核实。

---

## 三、生产者-消费者模式（经典应用）

用一个**容量为 1 的"蒸笼"**串起厨师和食客：

```java
public class Steamer {
    private String food = null;        // null = 蒸笼空，非 null = 有包子
    private final Object lock = new Object();

    // 生产者：蒸笼空了才放包子，放完叫醒食客
    public void put(String bun) throws InterruptedException {   // bun = 厨师传进来的那个包子
        synchronized (lock) {
            while (food != null) {     // 蒸笼还满着，厨师等
                lock.wait();
            }
            food = bun;                // 把传进来的包子放进蒸笼
            lock.notifyAll();          // 叫醒食客："有包子了"
        }
    }

    // 消费者：蒸笼有货才拿，拿完叫醒厨师
    public String take() throws InterruptedException {
        synchronized (lock) {
            while (food == null) {     // 蒸笼空着，食客等
                lock.wait();
            }
            String taken = food;       // 先把包子接到自己手里（记住地址）
            food = null;               // 再清空蒸笼（taken 手里的地址不受影响）
            lock.notifyAll();          // 叫醒厨师："蒸笼空了，可以做了"
            return taken;              // 返回手里的包子
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Steamer steamer = new Steamer();

        // 厨师线程：连续放 3 个包子
        Thread chef = new Thread(() -> {
            try {
                steamer.put("肉包");
                steamer.put("菜包");
                steamer.put("豆沙包");
            } catch (InterruptedException e) { e.printStackTrace(); }
        });

        // 食客线程：连续吃 3 个包子
        Thread eater = new Thread(() -> {
            try {
                System.out.println("食客吃到了：" + steamer.take());
                System.out.println("食客吃到了：" + steamer.take());
                System.out.println("食客吃到了：" + steamer.take());
            } catch (InterruptedException e) { e.printStackTrace(); }
        });

        chef.start();
        eater.start();
        chef.join();
        eater.join();
        // 输出：食客吃到了：肉包 / 菜包 / 豆沙包
    }
}
```

> **为什么用 `notifyAll` 而不是 `notify`？** `notify` 随机唤醒一个——万一把另一个"条件不满足"的线程唤醒了，它醒来发现条件还是不满足，又睡回去，而真正该被唤醒的线程却一直没被叫到 → **信号丢失**。`notifyAll` 把所有等待线程都叫醒，让它们自己检查条件，安全。

---

## 四、Condition（Lock 的搭档）

`wait/notify` 有个缺陷：**所有等待线程挤在一个队列里**，`notifyAll` 一锅端全叫醒，做不到"只叫醒我要的那种线程"。

`Condition` 解决了这个问题——它是 `Lock` 的**条件队列**，一个 Lock 可以拆出**多个 Condition**：

```java
Lock lock = new ReentrantLock();
Condition notFull  = lock.newCondition();   // "蒸笼没满"这个条件
Condition notEmpty = lock.newCondition();   // "蒸笼没空"这个条件

// 生产者：满了就睡，不满了才放
lock.lock();
try {
    while (food != null) notFull.await();   // 对应 wait()
    food = bun;
    notEmpty.signal();                       // 对应 notify()，精准叫醒"等着吃"的线程
} finally {
    lock.unlock();
}

// 消费者：空了就睡，不空了才拿
lock.lock();
try {
    while (food == null) notEmpty.await();
    Object taken = food; food = null;        // 先把包子接到手里，再清空蒸笼
    notFull.signal();                        // 精准叫醒"等着放"的线程
    return taken;
} finally {
    lock.unlock();
}
```

### wait/notify vs Condition 对照

| Object | Condition | 说明 |
|--------|-----------|------|
| `wait()` | `await()` | 释放锁，进入等待 |
| `wait(timeout)` | `await(timeout, unit)` | 限时等待 |
| `notify()` | `signal()` | 唤醒一个 |
| `notifyAll()` | `signalAll()` | 唤醒全部 |
| 一个锁一个队列 | 一个锁多个队列 | Condition 能**按条件分组** |

> **Condition 的核心优势：精准唤醒。** 生产者只叫醒消费者（`notEmpty.signal()`），不叫醒其他生产者——减少无谓的线程切换。

> **顺带说 `notifyAll()` 的代价——惊群效应（Thundering Herd）：** 把所有人全叫醒，被叫错的人"醒来→抢锁→检查→又睡回去"，白忙一轮；100 个等待线程就是 100 次无用功。Condition 靠"一个锁拆多个条件队列"，`signal()` 只叫醒**对应队列**里的人，从根上省掉这些无用功。

---

## 五、核心方法速查表

### Object 类（配合 synchronized）

| 方法签名 | 参数 | 返回值 | 说明 | 注意事项 |
|----------|------|--------|------|----------|
| ⭐ `wait()` | 无 | `void` | 释放锁，进入 WAITING，无限等 | **必须持有锁**（synchronized 内）才可调用 |
| `wait(long)` | `timeout` 毫秒 | `void` | 限时等待，超时自动醒 | 醒来也要重新抢锁 |
| `wait(long, int)` | `timeout` 毫秒 + `nanos` 纳秒 | `void` | 更精确的限时等待 | 少用，通常 `wait(long)` 够了 |
| ⭐ `notify()` | 无 | `void` | 随机唤醒**一个**等待线程 | 可能唤醒"条件不满足"的线程 → 信号丢失 |
| ⭐ `notifyAll()` | 无 | `void` | 唤醒**所有**等待线程 | 配合 while 用，最安全 |

### Condition 接口（配合 Lock）

| 方法签名 | 参数 | 返回值 | 说明 | 注意事项 |
|----------|------|--------|------|----------|
| ⭐ `await()` | 无 | `void` | 释放锁，进入等待 | 对应 `wait()` |
| `await(long, TimeUnit)` | `timeout` + `unit` | `boolean` | 限时等待，超时返回 false | 对应 `wait(timeout)` |
| ⭐ `signal()` | 无 | `void` | 唤醒**这个条件队列**里的一个 | 对应 `notify()` |
| `signalAll()` | 无 | `void` | 唤醒这个条件队列里的全部 | 对应 `notifyAll()` |

> `Lock.newCondition()` 在上一关同步锁 README 的速查表里已经出现过一次，本关是它的主场。

---

## 六、wait vs sleep ⭐ 面试最高频

| 维度 | wait() | sleep() |
|------|--------|---------|
| 定义在 | `Object` 类 | `Thread` 类 |
| 释放锁 | ✅ **释放** | ❌ **不释放** |
| 唤醒方式 | 必须被 `notify/notifyAll` 或超时唤醒 | 时间到自动醒 |
| 调用前提 | 必须在 `synchronized` 内 | 任何地方都能调 |
| 用途 | 线程间通信（等信号） | 单纯让线程暂停 |

```java
synchronized (lock) {
    Thread.sleep(1000);   // 睡了，但锁没放！别人进不来
    lock.wait();          // 睡了，锁也放了！别人能进来干活
}
```

> **一句话记死：** `sleep` 是"我困了睡会儿，手里的钥匙（锁）还攥着"；`wait` 是"我等别人叫我，钥匙先交出去"。

---

## 七、一个小 Demo

```java
// 魔法交易所：厨师做 5 个包子，食客吃 5 个包子，交替进行
public class BunShop {
    private static final Object lock = new Object();
    private static boolean hasBun = false;   // 蒸笼状态

    static class Chef extends Thread {
        public void run() {
            for (int i = 1; i <= 5; i++) {
                synchronized (lock) {
                    while (hasBun) { lock.wait(); }   // 蒸笼有包子，厨师等
                    System.out.println("厨师：做好第 " + i + " 个包子");
                    hasBun = true;
                    lock.notifyAll();                 // 叫食客来吃
                }
            }
        }
    }

    static class Eater extends Thread {
        public void run() {
            for (int i = 1; i <= 5; i++) {
                synchronized (lock) {
                    while (!hasBun) { lock.wait(); }  // 蒸笼空着，食客等
                    System.out.println("食客：吃掉第 " + i + " 个包子");
                    hasBun = false;
                    lock.notifyAll();                 // 叫厨师继续做
                }
            }
        }
    }
    // main: new Chef().start(); new Eater().start();
    // 输出严格交替：厨师做1 → 食客吃1 → 厨师做2 → 食客吃2 ...
}
```

---

## 面试官视角

> **Q1：wait() 和 sleep() 有什么区别？**  
> wait 是 Object 的方法、会释放锁、必须被 notify/超时唤醒、必须在 synchronized 内调用；sleep 是 Thread 的方法、不释放锁、时间到自动醒。一句话：sleep 攥着锁睡，wait 放下锁睡。

> **Q2：为什么 wait/notify 必须在 synchronized 块内调用？**  
> 因为 wait 要释放锁，必须先持有锁（先进入 synchronized 拿到监视器）才有锁可释放。否则抛 IllegalMonitorStateException。

> **Q3：什么是虚假唤醒？怎么避免？**  
> 线程没收到 notify 却自己醒了（OS 调度导致）。避免方法：wait 必须包在 `while` 循环里，醒来重新检查条件，不满足继续睡。用 `if` 只检查一次会踩坑。

> **Q4：为什么推荐用 notifyAll 而不是 notify？**  
> notify 随机唤醒一个，可能唤醒的是"条件不满足"的线程，而真正该醒的没被叫到 → 信号丢失。notifyAll 全部唤醒，各自检查条件，最安全。

> **Q5：Condition 和 wait/notify 有什么区别？**  
> wait/notify 配合 synchronized，所有等待线程挤一个队列；Condition 配合 Lock，一个锁可拆出多个 Condition 条件队列，能精准唤醒特定线程（signal 只叫醒对应队列）。

> **Q6：生产者-消费者模式为什么容量满了生产者要停？**  
> 为了不浪费：蒸笼满了厨师继续做会堆不下（无界队列会 OOM，前面线程池关卡踩过），所以满了就 wait 让出锁，等食客吃掉一个 notifyAll 再继续做。

> **Q7：线程调了 wait()，但没人 notify() 它，这是死锁吗？**  
> 不是。死锁是"持有并等待 + 循环等待"——两个线程互相攥着对方的锁不放；而 wait() 已经把锁交出去了，锁空着、其他线程照常干活。这是**永久等待 / 信号丢失（Lost Signal）**，属于活性问题但不是死锁。
