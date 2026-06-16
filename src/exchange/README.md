# 魔法交易所 3.0 (Magic Exchange 3.0)

> 命令行交易数据分析系统 — 整合项目 · 终章

---

## 一、系统架构图

```
trades.log (磁盘文件)
    │
    ▼  [loader: IO流 + 正则解析]
List<Trade> (内存数据)
    │
    ├─► [analyzer: Stream + Lambda] 统计/分组/排序
    │       │
    │       ▼  [Optional] 安全查询
    │
    ├─► [reporter: 模板方法模式] 格式化内容
    │       │
    │       ▼  [IO流] 写入磁盘
    report.txt (输出文件)
```

**数据流向：** 文件 → 对象列表 → 分析计算 → 报表文件

---

## 二、构建顺序（依赖图）

数字越小越先写，上层依赖下层。

```
⑤ ExchangeApp (主入口)  ←── 组装所有层
│
├── ④ reporter (报表输出)  ←── 依赖 model
│
├── ③ loader (数据加载)    ←── 依赖 model
├── ③ analyzer (分析引擎)  ←── 依赖 model
│
├── ② TradeParseException  ←── 依赖 model（解析失败时包装错误行）
│
└── ① model (数据模型)     ←── 不依赖任何人
     ├── TradeStatus  ← 最先：Trade 要用它
     └── Trade        ← 随后：所有层都用它
```

**原则：谁不依赖别人，谁就先写。**

> model 层不知道 loader 的存在，loader 不知道 reporter 的存在——每一层只知道自己和下一层的接口。

---

## 三、包结构 & 类职责

```
exchange/
│
├── model/                    数据模型层
│   ├── TradeStatus.java      枚举：COMPLETED / FAILED / PENDING
│   └── Trade.java            交易实体：商品名、金额(BigDecimal)、状态、日期(LocalDate)
│
├── exception/                异常定义层
│   └── TradeParseException.java   Checked 异常：日志行解析失败时抛出
│
├── loader/                   数据接入层（策略模式）
│   ├── TradeLoader.java          接口：定义"加载交易列表"的能力
│   └── FileTradeLoader.java      实现：从 .log 文件 BufferedReader 逐行读取
│
├── analyzer/                 分析引擎层
│   ├── TradeAnalyzer.java        Stream 管道：统计总览/分组/排序/TopN
│   └── TradeQuery.java           Optional 查询：按商品名查交易
│
├── reporter/                 报表输出层（模板方法模式）
│   ├── TradeReporter.java        抽象类：骨架方法 final + 子类扩展点 abstract
│   ├── SummaryReporter.java      汇总报表
│   └── DetailReporter.java       明细报表
│
└── ExchangeApp.java          主入口：命令行交互，组合各模块
```

---

## 四、分层设计决策速查

| 决策 | 选型 | 一句话理由 |
|------|------|-----------|
| Loader 用接口还是类？ | 接口 `TradeLoader` | 将来换数据源（MySQL/API）只新增实现，不动调用方 |
| Reporter 用接口还是抽象类？ | 抽象类 `TradeReporter` | 写文件流程固定（骨架），内容生成不同（留给子类）→ 模板方法模式 |
| 金额用什么类型？ | `BigDecimal` | `0.1 + 0.2` 在 `double` 下不等 `0.3`，涉及钱必须精确 |
| 状态用什么表示？ | 枚举 `TradeStatus` | 编译器防拼错，`switch` 全覆盖，比 `String` 安全 |
| 解析失败怎么处理？ | 自定义 Checked 异常 | 调用方**必须**处理格式错误，不能假装没看见 |

---

## 五、涉及的 Java 技能清单

| 技能 | 用在哪 | 对应章节 |
|------|--------|---------|
| 枚举（带字段+构造器） | `TradeStatus` | 第二阶段 |
| 封装 + BigDecimal + LocalDate | `Trade` | OOP + 包装类 + 日期时间 |
| 自定义 Checked 异常 | `TradeParseException` | 异常处理 |
| 接口 + 面向接口编程 | `TradeLoader` | 接口 |
| BufferedReader + 正则 | `FileTradeLoader` | IO流 + 正则 |
| Stream + Lambda 管道 | `TradeAnalyzer` | Stream + Lambda |
| Optional 链式查询 | `TradeQuery` | Optional |
| 抽象类 + 模板方法模式 | `TradeReporter` | 抽象类 |
| 组合优于继承 | `ExchangeApp` | OOP 设计原则 |
| 泛型（List\<T\> 类型安全） | 全部 | 泛型 |

---

## 六、五日推进计划

| 天 | 模块 | 产出 |
|----|------|------|
| Day 1 | model + exception | `TradeStatus`、`Trade`、`TradeParseException` |
| Day 2 | loader | `TradeLoader` 接口 + `FileTradeLoader` 实现 |
| Day 3 | analyzer | `TradeAnalyzer` + `TradeQuery` |
| Day 4 | reporter | `TradeReporter` 抽象类 + 子类 |
| Day 5 | 整合 + 复盘 | `ExchangeApp` 主入口 + 健壮性/性能审查 |

---

## 七、面试官视角

> **Q: 你这个项目怎么分层设计的？为什么？**
>
> A: 四层——model（数据）、loader（接入）、analyzer（分析）、reporter（输出）。核心原则是**依赖抽象不依赖具体**：`ExchangeApp` 依赖 `TradeLoader` 接口而不是 `FileTradeLoader` 类，将来换数据源只需新增实现，上层代码零改动。

> **Q: Reporter 为什么用抽象类而不是接口？**
>
> A: 报表生成流程固定——"读数据→算统计→格式化→写文件"。抽象类的 `final` 方法锁死流程骨架（模板方法模式），子类只实现内容格式化部分。如果用接口，每个子类都要自己写写文件的代码，重复且容易不一致。

> **Q: 金额为什么用 BigDecimal？**
>
> A: `double` 二进制浮点数无法精确表示十进制小数，`0.1 + 0.2 = 0.30000000000000004`。涉及金额必须用 `BigDecimal`，这是金融 Java 的铁律。内部存储还可以用 `long` 表示"分"，运算最快。
