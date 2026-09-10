# 多线程（八）：异步编排 CompletableFuture

## 零、为什么有了 Future，还是不够

t02 你学线程池时用过 `Future`：`submit(Callable)` 当场返回一个 `Future`，真正的结果要等后台算完才能 `get()`。但 `Future` 有几个硬伤：

| 痛点 | 表现 | 你当时被迫怎么干 |
|------|------|-----------------|
| **get() 阻塞** | `future.get()` 会把当前线程**卡死**，一直等到算完 | 主线程干等，啥也做不了 |
| **不能链式** | 拿到结果后想"再加工一步"，Future 没有下一步 | 手动再 submit 一次，代码越写越乱 |
| **不能组合** | 两个任务的结果想合并（笔数 + 金额），得 `get()` 两次 | 自己 `get` 两次再手动加 |
| **异常难处理** | 任务里抛异常，`get()` 抛 `ExecutionException`，得 try-catch 层层包 | 异常逻辑和正常逻辑缠在一起 |

> 回忆 t02 的代码：`total = total.add(f.get())` —— 这句 `f.get()` 就是阻塞点。任务还没算完，主线程就傻站着等，其他能并行的活全被拖住。

**CompletableFuture 就是来解决"干等"这件事的。** 它把"等结果"升级成"编排流程"——你不再被动等结果，而是主动写下一张"任务流水线图"：算完之后做什么、两个结果怎么合并、出错了怎么办，全都在一条链上声明清楚。

### 差别的本质：轮询 vs 回调

> 有个反直觉的点先说破：**Future 和 CF 都得"等"**——第二个任务要拿第一个任务的结果当输入，`count` 没算出来它就不可能开始。数据依赖上的"等"躲不掉。

差别不在"要不要等"，在"**谁在等、怎么等**"：

| | Future | CompletableFuture |
|---|--------|-------------------|
| 谁在等 | **你**（当前线程）卡死在 `get()` 上 | **没人在等**，你只注册一条规则 |
| 怎么等 | 轮询：反复催"好了没" | 回调：结果到了自动触发下一步 |
| 线程状态 | 被占着干等，别的活干不了 | 算完自动接下一棒，无空闲线程 |

一句话：**Future 是"轮询"（你主动催），CF 是"回调"（结果到了自动通知）。** 就像快递——Future 是你每五分钟打电话问"到了吗"；CF 是你留一句"到了发短信"，然后该干嘛干嘛。

看一个"依赖"场景就懂了（查完笔数，还要拿笔数去查平均客单价）：

```java
// Future：手动"等→拿→再提交→再等"，主线程卡两次，代码是断的
Future<Integer> f1 = pool.submit(() -> queryCount());
int count = f1.get();                                    // 阻塞
Future<Double> f2 = pool.submit(() -> queryAvg(count));
double avg = f2.get();                                   // 再阻塞

// CompletableFuture：一条链声明完，不亲自等
CompletableFuture.supplyAsync(() -> queryCount())
        .thenCompose(count -> supplyAsync(() -> queryAvg(count)))
        .thenAccept(avg -> System.out.println(avg));
```

### 关联类清单（自查遗漏）

| 类 | 归属 | 说明 |
|----|------|------|
| `CompletableFuture<T>` | 类 | 异步编排核心，本章主角 |
| `CompletionStage<T>` | 接口 | CF 实现的接口，定义了所有"编排方法" |
| `Future<T>` / `FutureTask<T>` | 接口 / 类 | 上一代异步（t02 学过），CF 是它的增强 |
| `ForkJoinPool.commonPool()` | 线程池 | CF 默认使用的公共线程池 |
| `Executor` | 接口 | 自定义线程池参数，生产环境必传 |

---

## 前置知识：函数式接口（lambda 的形状说明书）

读本章方法签名（`Function<T,R>`、`Consumer<T>`、`Supplier<T>`……）前，先搞懂它们是什么：**全是接口，而且是"函数式接口"——有且只有一个抽象方法的接口。** lambda 之所以能写，全靠这个"只有一个方法"。

**核心认知：方法签名里的这些接口名，就是"lambda 的形状说明书"。** 比如 `thenApply(Function<T,R>)` 读出来就一句话："这里需要一个 lambda：接收一个 T，返回一个 R。"

| 接口 | 唯一抽象方法 | 入参 | 出参 | lambda 形状 | 一句话 |
|------|------------|------|------|------------|--------|
| `Supplier<T>` | `T get()` | 0 | 1 | `() -> 结果` | 只出不进（生产） |
| `Consumer<T>` | `void accept(T)` | 1 | 0 | `x -> { ... }` | 只进不出（消费） |
| `Function<T,R>` | `R apply(T)` | 1 | 1 | `x -> 结果` | 一进一出（转换） |
| `BiFunction<T,U,R>` | `R apply(T,U)` | 2 | 1 | `(x, y) -> 结果` | 两进一出（合并） |
| `Runnable` | `void run()` | 0 | 0 | `() -> { ... }` | 无进无出（干件事） |

**为什么 lambda 能自动对上号？** 函数式接口只有一个抽象方法，编译器看到 `thenApply` 要求 `Function<Integer, String>`，就自动知道：lambda 参数是 Integer、函数体返回 String，实现的就是那个 `apply(T)` 方法。lambda 就是"匿名内部类"的缩略版。

**关于 `CompletionStage<U>`（签名里频繁出现的名字）：** 它是**接口**，CompletableFuture 实现了它。用集合类比：`List` 是接口、`ArrayList` 实现了它，签名爱写 `List` 但你传 `ArrayList`；同理签名写 `CompletionStage`，你实际传的是 `CompletableFuture`。所以在 `thenCombine(CompletionStage<U>, BiFunction<T,U,V>)` 里，第一个参数是"另一个任务"（另一个 CF），第二个参数才是"合并逻辑"（lambda）。

---

## 一、是什么：从"结果的占位符"到"流程的编排器"

一句话记住两者的区别：

| | Future | CompletableFuture |
|---|--------|-------------------|
| 本质 | 一个**结果的占位符**（单号） | 一条**流程的编排器**（流水线） |
| 你只能 | `get()` 干等 | 声明"接下来做什么" |
| 拿结果 | 阻塞等 | 阻塞等 / 不阻塞给默认值 / 回调自动触发 |

> **比喻升级：** Future 是快递单号——你拿到单号后只能反复打电话催（`get()` 阻塞）。CompletableFuture 是快递的**自动化流程**——你设置好"包裹一到就发短信 → 短信发了就归档"，剩下的系统自动跑，你不用盯着。

### 类比 Stream：流水线的两种形态

CompletableFuture 和 Stream 的流水线本质相同：**只声明"要做什么"，不亲自管"怎么一步步执行"。** 方法几乎一一对应（神似，别逐字对）：

| Stream（流的是数据） | CompletableFuture（流的是任务） | 作用 |
|---|---|---|
| `map` | `thenApply` | 转换 A→B |
| `flatMap` | `thenCompose` | 展开嵌套，防套娃 |
| `forEach` | `thenAccept` | 消费，收尾 |
| `reduce` | `thenCombine` | 合并 |
| （Stream 的痛点） | `exceptionally` | 兜底 |

**但关键区别必须分清，否则会混：**

| | Stream | CompletableFuture |
|---|---|---|
| 流水线流的是什么 | **数据元素** | **任务的完成时机** |
| 执行方式 | 同步、单线程 | 异步、跨线程、跨时间 |
| 解决什么问题 | 数据怎么加工写起来清晰 | 任务之间的依赖和并发怎么编排 |

方法长得像，是因为"**转换/合并/兜底**"这套心智模型是通用的；但 Stream 管**数据**，CF 管**任务的时间**。

---

## 二、创建异步任务：runAsync / supplyAsync ⭐

这是流水线的**起点**。两个工厂方法，差别只在"有没有返回值"：

| 方法签名 | 参数 | 返回值 | 说明 | 注意事项 |
|----------|------|--------|------|----------|
| ⭐ `static runAsync(Runnable)` | `runnable` — 无返回值任务 | `CompletableFuture<Void>` | 开一个异步任务，**没有返回值** | 就像 `execute()`，只管跑 |
| ⭐ `static supplyAsync(Supplier<T>)` | `supplier` — 提供结果的函数 | `CompletableFuture<T>` | 开一个异步任务，**有返回值** | 就像 `submit(Callable)` |
| `static runAsync(Runnable, Executor)` | 同上 + `executor` 自定义线程池 | 同上 | 指定线程池跑 | 生产环境推荐，见 §八 |
| `static supplyAsync(Supplier<T>, Executor)` | 同上 + `executor` | 同上 | 指定线程池跑 | 同上 |

> **不带 Executor 参数时**，默认跑在 `ForkJoinPool.commonPool()`（公共线程池）里。它的线程数是 CPU 核数 - 1，**不适合 IO 密集型任务**——这就是为什么生产环境要传自定义线程池（详见 §八）。

### Demo：盘点起点

```java
import java.util.concurrent.CompletableFuture;

// 魔法交易所周年庆收工，数据部开了两个异步统计任务
public class SettleStart {
    public static void main(String[] args) {
        // 任务1：异步统计成交笔数（有返回值）
        CompletableFuture<Integer> countFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println("统计成交笔数中...");
            return 1280;                        // Supplier 的返回值
        });

        // 任务2：异步写一条盘点日志（无返回值）
        CompletableFuture<Void> logFuture = CompletableFuture.runAsync(() -> {
            System.out.println("已记录盘点日志");
        });
    }
}
```

> 注意：`supplyAsync` 传的是 `Supplier`（`() -> 返回值`），`runAsync` 传的是 `Runnable`（`() -> {}`）。区分点就在"有没有 return"。

---

## 三、then 系列：链式编排（结果怎么往下流）⭐

流水线的**中段**。任务算完后，你想对结果"再加工一步"，用 then 系列。三个方法的区别只在于"**要不要结果、要不要返回新结果**"：

| 方法签名 | 参数 | 返回值 | 说明 | 注意事项 |
|----------|------|--------|------|----------|
| ⭐ `thenApply(Function<T,R>)` | `fn` — 接收结果、返回新值 | `CompletableFuture<R>` | **转换**结果（进一个、出一个） | 有返回值，结果变了 |
| ⭐ `thenAccept(Consumer<T>)` | `consumer` — 接收结果、不返回 | `CompletableFuture<Void>` | **消费**结果（进一个、啥也不出） | 收尾动作，打印/入库 |
| `thenRun(Runnable)` | `runnable` — 不接收结果 | `CompletableFuture<Void>` | 结果不关心，只**跑个动作** | 完全无视结果 |

> **一句话记：apply 是"改"（A→B），accept 是"用"（吃掉 A），run 是"顺手干件事"（不碰 A）。**

### 普通版 vs Async 版：要不要换线程 ⭐

先记住一个关键事实：**起点 `supplyAsync` / `runAsync` 天生就是异步的**（开新任务本身就是异步，所以它们没有"非 Async 版"）。**只有 `then` 系列才有两个版本**，区别只在"下一步跑在哪条线程上"：

| 写法 | 下一步跑在哪 | 形象说法 |
|------|------------|---------|
| `thenApply(fn)` | 上一步完成的那条线程上**接着跑** | 同一个选手跑下一棒（就地接力） |
| `thenApplyAsync(fn)` | **强制丢到线程池里另一条线程**跑 | 换一个选手跑下一棒 |

那"换不换线程"影响什么？就两件事：

1. **换线程有开销**——线程切换要花时间。快速的小步骤（拼字符串、算个数）用**默认版**，省掉无谓的切换。
2. **下一步如果会阻塞**（查数据库、调接口），用**默认版**就会把上一步那条线程也拖住；此时用 **Async** 版把它甩到线程池去，原线程立刻解脱。

### Demo：把笔数加工成报表文案

```java
import java.util.concurrent.CompletableFuture;

public class ThenChain {
    public static void main(String[] args) {
        CompletableFuture.supplyAsync(() -> 1280)                     // ① 统计出笔数
                .thenApply(count -> "今日成交 " + count + " 笔")       // ② 转换成文案（A→B）
                .thenAccept(msg -> System.out.println(msg))            // ③ 打印文案（吃掉结果）
                .thenRun(() -> System.out.println("报表已归档"));       // ④ 顺手归档（不看结果）

        // 主线程等一等，让异步流水线跑完（演示用，真实项目不会这样干等）
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
    }
}
// 输出：今日成交 1280 笔
//       报表已归档
```

---

## 四、组合：thenCompose / thenCombine / allOf / anyOf ⭐

链式只能"一路往下"，但真实业务经常要"**岔路汇合**"——两个任务的结果合并，或者一个任务的结果决定下一个异步任务。

### thenCompose（扁平化，串行依赖）⭐

**场景：** 第一个任务的结果，是**启动第二个异步任务**的输入。

| 方法签名 | 参数 | 返回值 | 说明 |
|----------|------|--------|------|
| ⭐ `thenCompose(Function<T, CompletionStage<U>>)` | `fn` — 接收结果、返回**一个新的异步任务** | `CompletableFuture<U>` | 把两个有依赖的异步任务**串成一条** |

> **为什么要"扁平化"？** 如果你用 `thenApply` 去接"返回另一个 CF 的任务"，会得到一个 `CompletableFuture<CompletableFuture<U>>` —— 套娃。`thenCompose` 会自动把内层展开，直接得到 `CompletableFuture<U>`。

```java
// 先按"结算员ID"查到他的结算记录条数，再用条数去开一个"生成明细"的异步任务
CompletableFuture<Integer> records = CompletableFuture.supplyAsync(() -> 45);   // 查出45条记录

CompletableFuture<String> detail = records.thenCompose(n ->                    // n = 45
        CompletableFuture.supplyAsync(() -> "共 " + n + " 条明细，正在生成...")); // 用45启动新异步任务
```

### thenCombine（并行合并）⭐

**场景：** 两个**互不依赖**的异步任务都算完后，把结果合并。

| 方法签名 | 参数 | 返回值 | 说明 |
|----------|------|--------|------|
| ⭐ `thenCombine(CompletionStage<U>, BiFunction<T,U,V>)` | `other` — 另一个任务；`fn` — 接收两个结果、合并出新值 | `CompletableFuture<V>` | 两个任务都完成，合并结果 |

```java
// 笔数和金额两个统计并行跑，都完成后再合成一句汇总
CompletableFuture<Integer> count = CompletableFuture.supplyAsync(() -> 1280);
CompletableFuture<Double> amount = CompletableFuture.supplyAsync(() -> 175049.5);

CompletableFuture<String> summary = count.thenCombine(amount,
        (c, a) -> "成交 " + c + " 笔，合计 ¥" + a);    // c=1280, a=175049.5
```

### allOf / anyOf（等全部 / 等任一）

| 方法签名 | 参数 | 返回值 | 说明 |
|----------|------|--------|------|
| ⭐ `static allOf(CompletableFuture<?>...)` | `cfs` — 多个任务 | `CompletableFuture<Void>` | **全部**完成才继续 |
| ⭐ `static anyOf(CompletableFuture<?>...)` | `cfs` — 多个任务 | `CompletableFuture<Object>` | **任意一个**完成就继续 |

```java
// 等"笔数统计"和"金额统计"都完成，才打印"盘点结束"
CompletableFuture<Void> all = CompletableFuture.allOf(count, amount);
all.thenRun(() -> System.out.println("所有统计完成，盘点结束"));
```

> `allOf` 返回的是 `Void`，拿不到具体结果——所以通常是 `allOf(...).thenRun(...)` 这种"全做完干件事"的用法；要拿具体结果还是得 `thenCombine` 或分别 `join()`。

---

## 五、异常处理：exceptionally / handle / whenComplete ⭐

流水线中途某个环节**炸了**，得有个兜底。三个方法的区别在于"**只在出错时走，还是成败都走**"：

| 方法签名 | 参数 | 返回值 | 说明 | 注意事项 |
|----------|------|--------|------|----------|
| ⭐ `exceptionally(Function<Throwable,T>)` | `fn` — 接收异常、返回兜底值 | `CompletableFuture<T>` | **只有异常时**才走，给个默认值 | 就像 try-catch 的 catch 分支 |
| ⭐ `handle(BiFunction<T,Throwable,R>)` | `fn` — 同时接收结果和异常（一个为 null） | `CompletableFuture<R>` | **成败都走**，能拿到结果或异常 | 相当于 finally + catch 合体 |
| `whenComplete(BiConsumer<T,Throwable>)` | `action` — 观察结果/异常，**不改结果** | `CompletableFuture<T>` | 成败都走，但**只观察不改变** | 结果还是原来的结果 |

> **一句话记：exceptionally 是"出错了补个默认值"，handle 是"成也处理败也处理（能改结果）"，whenComplete 是"冷眼旁观记录一下（不改结果）"。**

### Demo：统计失败给兜底

```java
CompletableFuture.supplyAsync(() -> {
            if (Math.random() < 0.5) throw new RuntimeException("数据源超时");
            return 1280;
        })
        .exceptionally(ex -> {                                  // 出错了走这里
            System.out.println("统计失败：" + ex.getMessage());
            return 0;                                           // 兜底返回 0
        })
        .thenAccept(n -> System.out.println("最终笔数：" + n));
// 一半概率输出：统计失败：数据源超时
//               最终笔数：0
// 一半概率输出：最终笔数：1280
```

> **⚠️ 关键认知：`exceptionally` 只兜住它**之前**的异常。** 流水线里 `exceptionally` 之后又接了 `thenApply`，那个 `thenApply` 再抛异常就兜不住了。所以要么把 `exceptionally` 放在链尾，要么每段都兜。

---

## 六、获取结果：get / join / getNow ⭐

流水线搭完了，最终还是要拿到结果。三个方法：

| 方法签名 | 参数 | 返回值 | 说明 | 注意事项 |
|----------|------|--------|------|----------|
| ⭐ `get()` | 无 | `T` | 阻塞等结果 | 抛 **Checked** 异常，必须 try-catch |
| ⭐ `join()` | 无 | `T` | 阻塞等结果 | 抛 **Unchecked** 异常，不用 try-catch，Lambda 里更顺 |
| ⭐ `getNow(T defaultValue)` | `defaultValue` — 没算完时的默认值 | `T` | **不阻塞**，没算完就返回默认值 | 想"有就用、没有拉倒"时用 |

```java
CompletableFuture<Integer> f = CompletableFuture.supplyAsync(() -> 1280);

Integer r = f.join();          // 阻塞等，最常用（不用 try-catch）
Integer now = f.getNow(-1);    // 不阻塞：算完了给1280，没算完给-1
```

> **生产环境经验：** 业务代码里用 `join()` 远多于 `get()`——因为 `join()` 抛的是 `CompletionException`（Unchecked），不用被迫写一堆 try-catch。

---

## 七、核心速查表（全章汇总）

| 需求 | 方法 | 一句话 |
|------|------|--------|
| 开一个有返回值的任务 | `supplyAsync(Supplier)` | 任务起点，返回 `CF<T>` |
| 开一个无返回值的任务 | `runAsync(Runnable)` | 任务起点，返回 `CF<Void>` |
| 转换结果 | `thenApply(Function)` | A 进 B 出 |
| 消费结果 | `thenAccept(Consumer)` | 吃掉结果，无返回 |
| 顺手干件事 | `thenRun(Runnable)` | 不看结果 |
| 依赖前一个任务 | `thenCompose(Function)` | 串行，扁平化防套娃 |
| 合并两个任务 | `thenCombine(CF, BiFunction)` | 并行，都完成再合并 |
| 等全部完成 | `allOf(...)` | 全齐了再继续 |
| 等任一完成 | `anyOf(...)` | 一个完成就继续 |
| 异常兜底 | `exceptionally(Function)` | 出错给默认值 |
| 成败都处理 | `handle(BiFunction)` | 相当于 catch+finally |
| 阻塞拿结果 | `join()` | 最常用，Unchecked 异常 |
| 不阻塞拿结果 | `getNow(defaultValue)` | 有就用没有拉倒 |

---

## 八、线程池陷阱（面试高频）⭐

> 前面 §二 提过：**不带 `Executor` 参数的 CF，默认跑在 `ForkJoinPool.commonPool()`**。这个公共池的线程数 = CPU 核数 - 1，是所有 CF 共享的。

**翻车场景：** 你的 `supplyAsync` 里是一个**阻塞的 IO 操作**（查数据库、调接口）。公共池就那几条线程，全被 IO 占着傻等，别的异步任务全排队饿死。

**解法：** 传自定义线程池。

```java
ExecutorService ioPool = Executors.newFixedThreadPool(10);     // 专门的 IO 线程池

CompletableFuture<String> f = CompletableFuture.supplyAsync(
        () -> queryFromDb(),      // 阻塞的数据库查询
        ioPool                    // ⭐ 指定用它跑，别用公共池
);
```

> **一句话铁律：CPU 密集任务可以不管（默认池），IO 密集任务必须传自定义线程池。**

---

## 面试官视角

> **Q1：CompletableFuture 和 Future 有什么区别？**  
> Future 只能 `get()` 阻塞拿结果，无法链式、无法组合、异常难处理。CompletableFuture 实现了 CompletionStage，支持 `thenApply/thenCompose/thenCombine` 等链式编排、`allOf/anyOf` 组合、`exceptionally/handle` 优雅处理异常。

> **Q2：thenApply 和 thenCompose 的区别？**  
> `thenApply` 的入参返回**普通值**（`T -> R`）；`thenCompose` 的入参返回**另一个 CompletableFuture**（`T -> CF<U>`），并自动**扁平化**，避免出现 `CF<CF<U>>` 套娃。前者用于"同步转换"，后者用于"串行启动下一个异步任务"。

> **Q3：thenCombine 和 allOf 的区别？**  
> `thenCombine` 合并**两个**任务的结果，能拿到合并值（`BiFunction` 处理）。`allOf` 等待**任意多个**任务全部完成，但只返回 `Void` 拿不到结果。要"两两合并结果"用 thenCombine，要"等一堆都完事"用 allOf。

> **Q4：exceptionally、handle、whenComplete 的区别？**  
> `exceptionally` 只在异常时走，返回兜底值；`handle` 成败都走，能拿到结果或异常，且能改返回值；`whenComplete` 成败都走，但只观察不改结果（返回原结果）。

> **Q5：join 和 get 的区别？**  
> 都阻塞拿结果。`get()` 抛 Checked 异常（`InterruptedException`/`ExecutionException`），必须 try-catch；`join()` 抛 Unchecked 异常（`CompletionException`），Lambda 里更简洁。业务代码常用 `join()`。

> **Q6：为什么 CompletableFuture 要传自定义线程池？**  
> 不带 Executor 时默认用 `ForkJoinPool.commonPool()`，线程数 = 核数 - 1，是全局共享的。若任务里有阻塞 IO，公共池线程被占满会导致所有异步任务饥饿。所以 IO 密集型任务必须传自定义线程池。

> **Q7：supplyAsync 和 runAsync 的区别？**  
> `supplyAsync` 传 `Supplier`，有返回值（`CF<T>`）；`runAsync` 传 `Runnable`，无返回值（`CF<Void>`）。要不要结果，选哪个。

> **Q8：thenApply 和 thenApplyAsync 的区别？**  
> `thenApply` 在上一步完成的那条线程上"就地接力"跑下一步，不换线程、省开销；`thenApplyAsync` 强制把下一步丢到线程池另一条线程跑。快速小步骤用默认版，阻塞/耗时的步骤用 Async 版，避免拖住原线程。
