# 多线程（三）：同步锁 synchronized / Lock

## 零、为什么需要同步锁

上一关你在 `SchedulerLab` 里让两个线程分别统计 `vipDone++` 和 `internDone++`，没出事——因为各加各的变量。但现在换个场景：

```java
int count = 0;
Thread t1 = new Thread(() -> { for (int i = 0; i < 10000; i++) count++; });
Thread t2 = new Thread(() -> { for (int i = 0; i < 10000; i++) count++; });
t1.start(); t2.start();
t1.join(); t2.join();
System.out.println(count);  // 期望 20000，实际可能 15000、17000...每次都不一样
```

为什么 `count++` 会出错？因为 `++` 不是一步完成的，底层分三步：

```
① 读：从主内存把 count 的值读进 CPU 寄存器
② 改：寄存器里的值 + 1
③ 写：把新值写回主内存
```

两个线程可能这样交错：

```
线程A：读 count = 0
线程B：读 count = 0     ← B 也读到 0，A 的 +1 还没写回
线程A：0+1=1，写回
线程B：0+1=1，写回      ← B 也写 1，把 A 的结果覆盖了
结果：加了两次，count 却只从 0 变成 1（丢了一次更新）
```

这种"结果取决于线程交错顺序"的现象，叫**竞态条件（Race Condition）**。解决它的唯一办法，就是让"读-改-写"三步变成一个**不可分割的原子操作**——这正是同步锁要干的活。

> **关键认知：** 单线程下 `count++` 永远不会错，因为没人跟你抢。多线程下，凡是"先读再改再写"的操作（`++`、`list.add`、`map.put`、`BigDecimal sum = sum.add(x)`），都是线程不安全的，都可能丢更新。

---

## 一、synchronized 关键字

`synchronized` 是 Java 内置的同步机制：**同一时刻，只允许一个线程进入被锁保护的代码块**。被挡在门外的线程进入 BLOCKED 状态排队。

### 三种用法

**用法1：同步代码块** ⭐ 最灵活

```java
synchronized (锁对象) {
    // 同一时刻只有一个线程能进来
}
```

**用法2：实例同步方法**

```java
public synchronized void method() {
    // 锁对象是 this
}
```

**用法3：静态同步方法**

```java
public static synchronized void method() {
    // 锁对象是 类的 Class 对象
}
```

### 锁对象到底是什么

| 用法 | 锁对象 | 谁和谁互斥 |
|------|--------|-----------|
| `synchronized (obj) { }` | `obj` 这个对象 | 抢同一把 `obj` 的线程 |
| `public synchronized void m()` | `this`（当前实例） | 抢同一个实例的线程 |
| `public static synchronized void m()` | `Xxx.class`（Class 对象） | 抢这个类的所有线程 |

> **核心理解：synchronized 锁的是"对象"，不是"代码"。** 两个线程只有抢**同一把锁**才会互斥；各拿各的锁，等于没锁。

### 用 synchronized 修复 count++

```java
int count = 0;
Object lock = new Object();  // 专门的锁对象，更清晰

Thread t1 = new Thread(() -> {
    for (int i = 0; i < 10000; i++) {
        synchronized (lock) {
            count++;  // 读-改-写变成原子操作
        }
    }
});
// t2 同样用 synchronized (lock) 包裹 count++
```

---

## 二、对象锁 vs 类锁

| | 对象锁 | 类锁 |
|---|---|---|
| 锁的是什么 | 某个实例对象 `this` / `obj` | 类的 `Class` 对象（全局唯一） |
| 写法 | `synchronized` 实例方法 / `synchronized(obj)` | `static synchronized` / `synchronized(Xxx.class)` |
| 互斥范围 | 同一个实例内互斥，不同实例各锁各的 | 所有实例共享一把锁，全局互斥 |

```java
class Counter {
    public synchronized void add() { ... }      // 对象锁：锁 this
    public static synchronized void reset() { ... }  // 类锁：锁 Counter.class
}
// add() 和 reset() 锁的不是同一把锁 → 它们之间不互斥！
```

> ⚠️ **易混淆点：** 一个线程持对象锁，另一个线程持类锁，两者**不互斥**——因为锁的是不同的对象。

---

## 三、Lock 接口 + ReentrantLock

`synchronized` 是隐式锁：加锁、解锁由 JVM 自动完成。`Lock` 是显式锁：加锁、解锁**必须手动写**。

```java
Lock lock = new ReentrantLock();
lock.lock();       // 手动加锁
try {
    // 临界区代码
} finally {
    lock.unlock(); // 手动解锁 —— 必须放 finally！
}
```

> ⚠️ **铁律：`unlock()` 必须放 `finally`。** 如果临界区抛异常，没进 finally 的 unlock，锁就永远不释放——其他线程全部饿死。

**ReentrantLock 四个 synchronized 没有的能力：**

| 能力 | 说明 |
|------|------|
| 可中断 | `lockInterruptibly()`——等锁时能被中断，不死等 |
| 可超时 | `tryLock(3, TimeUnit.SECONDS)`——等 3 秒拿不到就放弃 |
| 可尝试 | `tryLock()`——拿不到立刻返回 false，不阻塞 |
| 公平锁 | `new ReentrantLock(true)`——先到先得，防止线程饥饿 |

---

## 四、核心方法速查表

### Lock 接口

| 方法签名 | 参数 | 返回值 | 说明 | 使用场景 |
|----------|------|--------|------|----------|
| ⭐ `lock()` | 无 | `void` | 获取锁，拿不到就**阻塞**等待 | 常规加锁 |
| ⭐ `unlock()` | 无 | `void` | 释放锁 | **必须放 finally** |
| `tryLock()` | 无 | `boolean` | 尝试拿锁，拿到返回 true，拿不到**立即**返回 false | 不想阻塞的场景 |
| `tryLock(long, TimeUnit)` | `timeout` + `unit` | `boolean` | 限时尝试拿锁，超时返回 false | 等一会儿拿不到就放弃 |
| `lockInterruptibly()` | 无 | `void` | 可中断地获取锁 | 等待锁时能响应中断 |
| `newCondition()` | 无 | `Condition` | 创建条件变量 | 配合 await/signal（下一关线程通信） |

### 可重入性（reentrant）

"可重入"指：**同一个线程已经持有锁，还能再次进入被同一把锁保护的代码**，不会被自己锁死。

```java
public synchronized void methodA() {
    methodB();  // methodB 也是 synchronized，但同一线程可重入，不会死锁
}
public synchronized void methodB() { ... }
```

> 如果不支持可重入，methodA 调 methodB 时，线程要等自己手里的锁释放——永远等不到，死锁。

---

## 五、synchronized vs ReentrantLock 对比

| 维度 | synchronized | ReentrantLock |
|------|--------------|---------------|
| 加锁方式 | 隐式（JVM 自动加解锁） | 显式（手动 lock/unlock） |
| 释放时机 | 代码块结束 / 异常自动释放 | 必须手动 `unlock()` |
| 可中断 | ❌ | ✅ `lockInterruptibly()` |
| 可超时 | ❌ | ✅ `tryLock(timeout)` |
| 公平锁 | ❌ 只能非公平 | ✅ `new ReentrantLock(true)` |
| 性能 | JDK6 优化后接近 | 接近 |
| 上手难度 | 简单，不易出错 | 复杂，忘记 unlock 就出事 |

> **选型口诀：** 简单场景无脑用 `synchronized`（安全、省心）；需要"可中断 / 可超时 / 公平"这些高级能力时，才上 `ReentrantLock`。

---

## 六、死锁（Deadlock）

两个线程各自持有一把锁，又都在等对方手里的锁——谁都动不了，程序卡死。

```java
Object lockA = new Object();
Object lockB = new Object();

Thread t1 = new Thread(() -> {
    synchronized (lockA) {          // t1 先拿 lockA
        Thread.sleep(100);
        synchronized (lockB) {      // 再等 lockB
            // ...
        }
    }
});

Thread t2 = new Thread(() -> {
    synchronized (lockB) {          // t2 先拿 lockB
        Thread.sleep(100);
        synchronized (lockA) {      // 再等 lockA
            // ...
        }
    }
});
// t1 持 lockA 等 lockB，t2 持 lockB 等 lockA → 互相等待 → 死锁
```

**死锁四个必要条件：** 互斥、持有并等待、不可剥夺、循环等待。破坏任意一个即可避免。

**避免死锁的简单办法：** 让所有线程**按相同顺序**获取锁（都先拿 lockA 再拿 lockB），就不会成环。

---

## 七、一个小 Demo

```java
// 魔法交易所：并发统计交易笔数，用 synchronized 保证不丢更新
public class TradeCounter {
    private int count = 0;
    private final Object lock = new Object();

    public void increment() {
        synchronized (lock) {   // 读-改-写原子化
            count++;
        }
    }

    public int getCount() {
        synchronized (lock) {
            return count;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        TradeCounter counter = new TradeCounter();
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) counter.increment();
        });
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) counter.increment();
        });
        t1.start(); t2.start();
        t1.join(); t2.join();
        System.out.println("总笔数：" + counter.getCount());  // 稳定 20000
    }
}
```

---

## 面试官视角

> **Q1：synchronized 锁的是什么？**  
> 锁的是**对象**，不是代码。同步代码块锁括号里的对象，实例方法锁 `this`，静态方法锁 `Xxx.class`。抢同一把锁才互斥。

> **Q2：`i++` 是线程安全的吗？为什么？**  
> 不安全。`i++` 底层是"读-改-写"三步非原子操作，多线程会丢更新。要 `synchronized` 或 `AtomicInteger`。

> **Q3：synchronized 和 ReentrantLock 有什么区别？**  
> synchronized 隐式加解锁、不可中断、不可超时、非公平；ReentrantLock 显式加解锁（unlock 放 finally）、可中断、可超时、可公平。简单场景优先 synchronized。

> **Q4：什么是死锁？怎么避免？**  
> 两个线程互相持有对方需要的锁、又都在等待对方释放。避免方法：所有线程按相同顺序获取锁，破坏"循环等待"。

> **Q5：synchronized 是可重入的吗？**  
> 是。同一线程已持有锁，还能再次进入被同一把锁保护的代码，不会自己锁死自己。
