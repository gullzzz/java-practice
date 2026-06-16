# 阶段 9：异常处理

配套代码：`src/com/practice/exceptions/ExceptionsDemo.java`

---

## 9.1 异常体系

**定义**：Java 异常体系以 `Throwable` 为根，分为两大分支——`Error`（JVM 级别的严重错误，程序不应捕获，如 OutOfMemoryError）和 `Exception`（程序可能处理的异常）。Exception 又分为 RuntimeException（运行时异常，编译器不强制处理）和 Checked Exception（编译时异常，编译器强制要求 try-catch 或 throws 声明）。

**解决问题/用途**：没有异常机制时，错误处理靠返回错误码——-1 表示失败、null 表示没找到——容易被忽略，且错误码与正常返回值混在一起难区分。异常机制将"正常流程"和"错误处理"分离：正常代码写在 try 块里，异常处理写在 catch 块里，代码意图清晰，且编译器能强制要求处理可预见的异常（Checked Exception）。

```
Throwable
├── Error（严重错误，不应捕获，如 OutOfMemoryError）
└── Exception
    ├── RuntimeException（运行时异常，不强制处理）
    │   ├── NullPointerException
    │   ├── ArrayIndexOutOfBoundsException
    │   ├── ArithmeticException
    │   └── IllegalArgumentException
    └── 其他（Checked Exception，必须处理或声明）
        ├── IOException
        ├── SQLException
        └── InterruptedException
```

关键区分：
- **Checked Exception**：编译器强制处理（try-catch 或 throws）
- **Unchecked Exception**（RuntimeException）：编译器不强制，但建议处理

### 常见异常类型详解

#### RuntimeException（运行时异常）— 程序bug，编译器不强制处理

**NullPointerException（空指针异常）**：

**定义**：当程序试图在 null 引用上调用方法或访问属性时抛出。这是 Java 中最常见的异常，几乎每个开发者都遇到过。

**解决问题/用途**：NPE 本质上是帮你发现"以为对象存在但实际是 null"的逻辑漏洞。Java 14+ 增强了 NPE 信息，会明确指出具体哪个变量为 null，大幅提升排查效率。

```java
String name = null;
int len = name.length();  // 💥 NullPointerException: Cannot invoke "String.length()" because "name" is null
```

**ArrayIndexOutOfBoundsException（数组索引越界异常）**：

**定义**：用小于 0 或大于等于数组长度的索引访问数组时抛出。

**解决问题/用途**：这是一种安全机制——数组在内存中是固定区域，越界访问会读到其他数据甚至 C++ 时代能导致安全漏洞。Java 强制运行时检查，用异常而非让程序读了脏数据继续执行。

```java
int[] arr = {1, 2, 3};
int x = arr[3];  // 💥 ArrayIndexOutOfBoundsException: Index 3 out of bounds for length 3
```

**ArithmeticException（算术异常）**：

**定义**：数学运算中出现非法操作时抛出，最常见的触发方式是整数除以零。

**解决问题/用途**：整数除以零在数学上无定义，Java 用异常阻止程序继续使用一个"无意义的结果"。注意浮点数除以零不会抛异常（返回 Infinity 或 NaN，符合 IEEE 754 标准）。

```java
int result = 10 / 0;  // 💥 ArithmeticException: / by zero
double d = 10.0 / 0;  // 不抛异常，返回 Infinity
```

**IllegalArgumentException（非法参数异常）**：

**定义**：方法接收到不合法参数值时抛出（如传入了负数而方法要求正数）。这是开发者主动用 `throw` 抛出的代表性异常，用于 fail-fast。

**解决问题/用途**：在方法入口处校验参数合法性，把问题拦截在最外层，防止非法值深入系统内部造成更难排查的 bug。

```java
public void setAge(int age) {
    if (age < 0) throw new IllegalArgumentException("年龄不能为负数: " + age);
    this.age = age;
}
```

#### Checked Exception（编译时异常）— 可预见的异常，编译器强制处理

**IOException（IO异常）**：

**定义**：输入输出操作失败时抛出的异常，是大多数 IO 相关异常的父类。常见子类包括 FileNotFoundException（文件不存在）、EOFException（意外到达文件末尾）、SocketException（网络连接异常）。

**解决问题/用途**：IO 操作依赖外部资源——磁盘可能满了、文件可能被删了、网络可能断了——这些都不是程序能控制的。IOException 强制调用方处理这些不可控因素，防止程序在 IO 失败时无声崩溃。

**SQLException（SQL异常）**：

**定义**：数据库操作失败时抛出的异常——包括连接失败、SQL 语法错误、约束冲突（主键重复、外键不存在）等。

**解决问题/用途**：数据库操作也是外部依赖，出问题的可能性很多——数据库宕机、网络超时、SQL 写错了。JDBC 用 SQLException 统一报告这些错误，强制调用方考虑失败场景。

**InterruptedException（中断异常）**：

**定义**：当线程在睡眠（sleep）、等待（wait）或阻塞时，被其他线程调用 interrupt() 方法中断时抛出。

**解决问题/用途**：这是 Java 多线程协作机制的核心——不是所有的"等待"都该无限等下去，线程中断提供了一种优雅的"请停止当前操作"的信号。收到 InterruptedException 后应该终止当前任务并恢复中断状态，而不是吞掉不管。

#### Error（严重错误）— 程序不该捕获

**OutOfMemoryError（内存溢出错误）**：

**定义**：JVM 堆内存耗尽，无法分配新对象时抛出。不是 Exception 而是 Error——意味着 JVM 处于危险状态，程序不应尝试捕获恢复，而应让进程退出。

**解决问题/用途**：这是一个"你该优化程序或加大堆内存"的信号，不是运行时异常。常见原因包括内存泄漏（持有无用对象的引用）、一次性加载过多数据到内存、死循环中不停创建对象。

---

## 9.2 try-catch

**定义**：`try-catch` 是 Java 捕获异常的基本语法。把可能出异常的代码放在 try 块中，如果抛出异常，程序立即跳转到匹配该异常类型的 catch 块执行处理代码。多个 catch 块按顺序匹配，匹配到第一个就停止，所以具体异常放前面、通用异常放后面。

**解决问题/用途**：程序执行中会遇到各种意外——文件不存在、网络断开、数据库宕机。try-catch 让你优雅地处理这些意外而不让程序崩溃，可以记录日志、给用户友好提示、尝试降级方案。关键设计是"异常的代码自行跳转到处理代码"，正常流程和处理逻辑不混杂。

```java
try {
    int result = 10 / 0;  // 可能抛出异常
} catch (ArithmeticException e) {
    System.out.println("除零异常: " + e.getMessage());
} catch (Exception e) {
    System.out.println("其他异常");
}
```

规则：
- **具体异常在前，通用异常在后**（否则编译警告）
- 匹配到第一个能处理该异常的 catch 后，不再继续
- 可以用 `|` 合并多个异常类型（Java 7+）

```java
catch (ArithmeticException | IndexOutOfBoundsException e) { ... }
```

---

## 9.3 finally

**定义**：`finally` 块中的代码无论是否发生异常、异常是否被捕获，都会执行（唯一的例外是调用了 `System.exit(0)` 或 JVM 崩溃）。finally 通常用于释放资源——关闭文件流、数据库连接、网络套接字等。注意 finally 中不建议放 return，会覆盖 try/catch 中的返回值。

**解决问题/用途**：资源管理需要保证"不论成功还是失败都必须释放资源"——打开了一个文件，处理过程出错，文件不能一直被锁着不关闭。finally 的"无论如何都执行"语义完美满足这个需求，避免了资源泄漏导致系统慢慢耗尽文件句柄或数据库连接。

```java
try {
    // 可能异常的代码
} catch (Exception e) {
    // 处理异常
} finally {
    // 无论是否异常，都会执行
    // 通常用于释放资源：关闭文件、数据库连接等
}
```

- `finally` **始终执行**（即使有 return，也有例外：`System.exit(0)` 会跳过）
- finally 中通常不放 return，会覆盖 try/catch 中的返回值

---

## 9.4 throws（声明异常）

**定义**：`throws` 关键字用在方法声明上，表示该方法可能抛出指定的异常类型，但不自行处理，而是将异常向上抛给调用方。Checked Exception 必须要么 try-catch 捕获，要么通过 throws 声明告知调用方。

**解决问题/用途**：不是所有异常都适合在发生位置处理——底层 IO 操作不知道当前是 Web 请求还是批处理任务，无法决定给用户返回什么。throws 将处理责任上移，让"知道全局上下文"的高层代码来做决定。它也是一种文档——看方法签名就知道这个方法可能出什么异常。

方法可能抛出异常但不自行处理时，用 `throws` 声明：

```java
public void readFile(String path) throws IOException {
    // 可能抛出 IOException，交给调用方处理
}
```

- Checked Exception 必须**要么捕获，要么声明 throws**
- RuntimeException 可以声明，但不是强制的

---

## 9.5 throw（抛出异常）

**定义**：`throw` 关键字用于在代码中主动抛出一个异常对象。常用于参数校验、业务规则校验——当方法接收到的参数不合法或业务状态不符合预期时，主动抛出异常中断当前流程。

**解决问题/用途**：被动等待 JVM 抛异常不够——你要在"问题刚出现迹象时"就主动报告。比如用户注册时用户名为空，等传到数据库再报错早就晚了。在方法入口处 throw 校验异常，实现 fail-fast（尽早失败），让问题暴露在最近的位置，方便定位和修复。

用 `throw` 主动抛出异常：

```java
public void register(String name) {
    if (name == null || name.isBlank()) {
        throw new IllegalArgumentException("用户名不能为空");
    }
    // 正常逻辑...
}
```

---

## 9.6 自定义异常

**定义**：通过继承 `Exception`（Checked）或 `RuntimeException`（Unchecked）来创建业务专用的异常类。命名规范为 `XxxException`，通常只需要传入错误信息并调用父类 `super(message)` 构造器即可。

**解决问题/用途**：JDK 内置异常太过通用——`IllegalArgumentException` 可以表示"用户名不合法"也可以表示"密码不合法"，调用方无法精确区分。自定义异常如 `InvalidNameException` 和 `InvalidPasswordException` 让异常类型本身就是有意义的业务信息，调用方可以针对不同异常做不同处理，也让日志和错误报告更精确。

```java
// 继承 Exception → Checked Exception
public class InvalidNameException extends Exception {
    public InvalidNameException(String message) {
        super(message);
    }
}

// 使用
public void register(String name) throws InvalidNameException {
    if (name == null || name.isBlank()) {
        throw new InvalidNameException("用户名不能为空");
    }
    System.out.println("注册成功: " + name);
}
```

命名规范：`XxxException`，从名字即可看出异常含义。

---

## 9.7 try-with-resources（Java 7+）

**定义**：`try-with-resources` 是一种自动资源管理语法——在 try 后面的括号中声明实现了 `AutoCloseable` 接口的资源（如文件流、数据库连接），无论 try 块如何退出（正常、异常、return），JVM 都会自动调用资源的 `close()` 方法。

**解决问题/用途**：传统方式必须在 finally 中手动关闭资源——代码冗长、容易忘记、嵌套资源时层层 if-null 判断痛苦不堪。try-with-resources 直接把资源声明和生命周期绑定在一起，自动关闭，代码量减半以上，且永远不会发生资源泄漏。

自动关闭实现了 `AutoCloseable` 接口的资源：

```java
// 传统写法（需要手动关闭）
Connection conn = null;
try {
    conn = getConnection();
    // 使用 conn...
} catch (SQLException e) {
    // ...
} finally {
    if (conn != null) conn.close();  // 繁琐
}

// 新写法（自动关闭）
try (Connection conn = getConnection()) {
    // 使用 conn...
}  // 自动调用 conn.close()，即使出错也会关闭
```

- 资源必须实现 `AutoCloseable` 接口
- 可以声明多个资源，用 `;` 分隔

---

## 9.8 异常处理最佳实践

1. **不要吞掉异常**（空 catch 块）
2. **catch 具体异常**，不要盲目 `catch (Exception e)`
3. **尽早抛出，延迟捕获**：底层抛出，高层处理
4. **异常信息要具体**："文件不存在: /path/to/file" 比 "错误" 有用
5. **优先用 try-with-resources** 管理资源
6. **不要用异常做流程控制**：异常是"异常"情况，不应替代 if-else

---

## 面试官视角

| 常见问法 | 考察点 | 参考答案 |
|----------|--------|----------|
| "Checked 和 Unchecked 异常有什么区别？" | 编译器强制处理 vs 不强制、设计意图 | Checked Exception（Exception 子类，非 RuntimeException）编译器强制要求 try-catch 或 throws，代表"可预见且调用方应处理"的情况（如文件不存在）；Unchecked（RuntimeException 及其子类）不强制处理，代表"程序 Bug"（如空指针），应在开发阶段修复而非运行时捕获 |
| "finally 一定会执行吗？" | 唯一例外、return 覆盖陷阱 | 唯一不执行的情况：① `System.exit(0)` ② JVM 崩溃（如 kill -9）。注意：finally 中写 return 会覆盖 try 里的返回值，这是经典的隐蔽 bug |
| "throw 和 throws 有什么区别？" | 一个是动作、一个是声明 | `throw` 是实际抛出异常对象（`throw new XxxException()`）；`throws` 是方法签名上的声明，告知调用方此方法可能抛出哪些异常（`void foo() throws IOException`） |
| "自定义异常怎么做？继承 Exception 还是 RuntimeException？" | 业务语义选择 | 需要调用方强制处理 → 继承 `Exception`（Checked）；不需要强制处理（通常是参数校验、业务规则）→ 继承 `RuntimeException`（Unchecked）。命名以 `Exception` 结尾，通常只需定义构造器调用 `super(message)` |
| "try-with-resources 的原理是什么？" | AutoCloseable 接口、自动 close 顺序 | 资源必须实现 `AutoCloseable` 接口。try 块退出时（正常/异常/return），JVM 自动调用 `close()`，多个资源按**声明相反顺序**关闭。本质是编译器生成 finally + close() 的字节码，但不用手写了 |
| "catch 多个异常时顺序有要求吗？" | 父类子类匹配顺序 | 子类异常必须在前，父类在后。因为 catch 按书写顺序匹配，如果父类在前则子类永远不会被匹配到，编译器会报错。Java 7+ 可用 `catch (A \| B e)` 合并不相干的异常类型 |
| "什么时候用异常，什么时候用 if-else？" | 异常的定位是"异常情况" | 可预见的、正常的业务流程用 if-else（如用户输入格式校验）；不可预见的、调用方无法控制的外部和运行时错误用异常（如网络断开、文件被删）。核心判断：这个情况是"意料之中"还是"意外" |
