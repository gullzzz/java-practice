# 3.9 IO流

## 一、为什么需要 IO 流？

程序的数据存在内存里——关掉 JVM，一切归零。IO 流让程序拥有"永久记忆"：把数据写到硬盘文件，下次启动再读回来。

用一句话概括：**IO 流 = 程序与外部世界（文件、网络、控制台）之间的数据传输管道。**

---

## 二、类之间的关系 — 一张图看懂"套娃"

先搞清楚"谁是谁的谁"，后面写代码就不会晕了。

### 2.1 核心设计：分层包装，各司其职

```
┌─────────────────────────────────────────────────────────────────┐
│                      你的业务代码                                │
│            "把交易记录追加写入文件"                              │
│            "把日志逐行读出来"                                    │
└────────────┬────────────────────────────┬───────────────────────┘
             │                            │
             ▼ 写                         ▼ 读
┌────────────────────────┐   ┌────────────────────────┐
│  ⭐ BufferedWriter     │   │  ⭐ BufferedReader     │  ← 第2层：缓冲 + 便利方法
│  - write(String)       │   │  - readLine()          │    一次读一大块到内存
│  - newLine()           │   │  - lines()             │    按行读、自动缓冲
│  职责：攒一批再写磁盘   │   │  职责：提前读一批到内存  │
└────────┬───────────────┘   └────────┬───────────────┘
         │ 包装                        │ 包装
         ▼                             ▼
┌────────────────────────┐   ┌────────────────────────┐
│  FileWriter            │   │  FileReader            │  ← 第1层：连接磁盘
│  new FileWriter(path)  │   │  new FileReader(path)  │    知道文件在哪
│  write(int) 逐字写     │   │  read() 逐字读         │    但一次只能读/写一个字符
│  职责：打开文件，写字符  │   │  职责：打开文件，读字符  │
└────────┬───────────────┘   └────────┬───────────────┘
         │ 需要                       │ 需要
         ▼                             ▼
┌─────────────────────────────────────────────────────────────────┐
│  File file = new File("trades.log")                             │  ← 第0层：路径抽象
│  职责：描述"哪个文件"——检查存在、创建目录、获取路径              │
│  不负责读写内容！                                                │
└─────────────────────────────────────────────────────────────────┘
```

### 2.2 为什么要分层？—— 一个生活类比

想象你要从图书馆借一本书：

| 角色 | 对应 IO 类 | 职责 |
|------|-----------|------|
| 图书馆地址 | `File` | "北京市朝阳区xxx号" — 知道在哪，但不会帮你拿书 |
| 图书管理员 | `FileReader` / `FileWriter` | 帮你跑进书库一本一本拿 — 但一次只拿一本，来回跑很慢 |
| 推着小车的管理员 | `BufferedReader` / `BufferedWriter` | 一次推一车书出来 — 你要哪本直接从车上拿，不用每次跑书库 |

**结论：** 不是 Java 故意搞复杂，而是每一层解决一个独立问题。你只需要记住"推小车的管理员"(`BufferedReader`/`BufferedWriter`) 是日常最常用的。

### 2.3 套娃写法是怎么来的

```java
// 写法拆解 —— 从内往外看：

// 第1步：FileReader 打开文件，但它只会逐字读，没有 readLine()
FileReader fr = new FileReader("trades.log");

// 第2步：把 FileReader 包进 BufferedReader，就有了缓冲 + readLine()
BufferedReader br = new BufferedReader(fr);

// 合并成一行 ← 日常写法
BufferedReader br = new BufferedReader(new FileReader("trades.log"));
//                   ↑ 缓冲+按行读        ↑ 打开文件+逐字读
```

> **记法口诀：** `new BufferedReader(new FileReader(路径))` — 外层管"怎么读方便"，内层管"从哪读"。

### 2.4 快速对照：每个类给文件加了什么能力

```
                         读                         写
                     ─────────                 ─────────
第2层（便利）   BufferedReader              BufferedWriter
                ├─ readLine()  按行读        ├─ write(str)  写字符串  
                ├─ 内部缓冲区，减少磁盘IO     ├─ newLine()   跨平台换行
                └─ 必须包装一个 Reader        └─ 必须包装一个 Writer
                     │                            │
                     ▼                            ▼
第1层（连接）   FileReader                  FileWriter
                ├─ new Xxx(path) 打开文件    ├─ new Xxx(path) 打开文件
                ├─ read() 逐字符读           ├─ write(str) 写字符串
                └─ 没有 readLine()！         └─ new Xxx(path, true) 追加模式
                     │                            │
                     ▼                            ▼
第0层（路径）   File                        File
                ├─ exists()  文件存在吗？    ├─ getParentFile().mkdirs()
                ├─ length()  文件多大？      └─ 不负责读写内容！
                └─ 不负责读写内容！
```

> ⭐ **选型决策（一秒判断法）：** 读文本按行 → `BufferedReader`。写文本按行 → `BufferedWriter` / `PrintWriter`。读写二进制 → `BufferedInputStream` / `BufferedOutputStream`。控制编码 → `InputStreamReader` / `OutputStreamWriter`。

---

## 三、File 类 — 操作路径，不操作内容

**核心理念：** `File` 对象 = 一张写着路径的"便签纸"，它不是磁盘上真实的文件。就像你写下一个地址，不代表那里有房子。但有了这张便签，你可以问"这里有文件吗？"，或者在这里建一个。

**一句话记 `File` 的能力边界：**

| File 能做的（路径层面） | File 不能做的（内容层面） |
|------------------------|--------------------------|
| 检查文件是否存在 (`exists`) | 读取文件内容 → 交给流 |
| 创建/删除文件或目录 | 写入数据 → 交给流 |
| 列出目录下的文件 (`listFiles`) | 复制/移动文件内容 |
| 获取文件名、大小 | 任何跟文件**内容**有关的事 |

> `new File(path)` 不要求路径真实存在——指向不存在的路径也不会报错，调 `exists()` 返回 `false` 而已。

### 常用方法

| 方法 | 参数 | 返回值 | 说明 | 注意事项 |
|------|------|--------|------|----------|
| ⭐ `new File(path)` | `String path` — 文件或目录的路径 | File 对象 | 创建 File 对象（便签纸） | **不会在磁盘上创建实际文件！** 只是内存中的一个路径抽象 |
| ⭐ `exists()` | 无 | `boolean` | 判断路径上是否有文件/目录 | 读写文件前先调它判断，避免 FileNotFoundException |
| ⭐ `getParentFile().mkdirs()` | 无 | `boolean` | 创建所有不存在的父目录 | **写文件前必调！** 否则父目录不存在会抛 IOException |
| `createNewFile()` | 无 | `boolean` | 在磁盘上真正创建文件 | 父目录必须先存在，否则抛 IOException；已存在则返回 false |
| `isFile()` | 无 | `boolean` | 判断是否为文件（非目录） | 路径不存在也返回 false |
| `isDirectory()` | 无 | `boolean` | 判断是否为目录 | 路径不存在也返回 false |
| `delete()` | 无 | `boolean` | 删除文件或**空**目录 | 目录非空时删除失败返回 false |
| ⭐ `listFiles()` | 无 | `File[]` | 列出目录下所有文件 | 不是目录或不存在时返回 null，遍历前要判空 |
| `getName()` | 无 | `String` | 返回文件名（不含路径） | 纯字符串操作，不管文件是否存在 |
| `length()` | 无 | `long` | 返回文件字节数 | 文件不存在返回 0 |

### Demo

```java
File f = new File("data/trades.log");

// 写文件前的标准前置操作
if (!f.exists()) {
    f.getParentFile().mkdirs();   // 确保 data/ 目录存在
    f.createNewFile();            // 创建实际文件
}

System.out.println(f.getName());  // "trades.log"
System.out.println(f.length());   // 0（刚创建，空的）
```

---

## 四、字节流 — 万物皆可读，但读文本很痛苦

### FileInputStream（读字节）

| 方法 | 参数 | 返回值 | 说明 | 注意事项 |
|------|------|--------|------|----------|
| ⭐ `new FileInputStream(path)` | `String path` | FileInputStream | 打开文件用于读取 | 文件不存在抛 FileNotFoundException |
| ⭐ `read()` | 无 | `int`（0~255 的字节值） | 读一个字节 | **返回 -1 表示读完（EOF）**，循环条件就是 `!= -1` |
| ⭐ `read(byte[])` | `byte[] buffer` — 缓冲区 | `int` — 实际读取的字节数 | 批量读，填到 buffer | **返回 -1 表示读完**；返回值可能小于 buffer.length |

### FileOutputStream（写字节）

| 方法 | 参数 | 返回值 | 说明 | 注意事项 |
|------|------|--------|------|----------|
| ⭐ `new FileOutputStream(path)` | `String path` | FileOutputStream | 打开文件用于写入 | **默认覆盖模式！** 文件已有内容会被清空 |
| ⭐ `new FileOutputStream(path, true)` | `String path, boolean append` | FileOutputStream | 打开文件用于**追加**写入 | 第二个参数 `true` = 追加，`false` = 覆盖（默认） |
| `write(int)` | `int b` — 0~255 | `void` | 写一个字节 | 实际只写低8位 |
| `write(byte[])` | `byte[] data` | `void` | 批量写字节 | **最后一次可能写入垃圾数据**（见下方陷阱） |
| ⭐ `write(byte[], int off, int len)` | `byte[] data, int offset, int length` | `void` | 写数组中从 `off` 开始的 `len` 个字节 | **搭配 `read(byte[])` 返回值使用的唯一正确方式** |

> ⚠️ **三种 write 的区别 —— 新手最容易踩的坑：**
> 
> ```java
> byte[] buff = new byte[1024];
> int bytesRead = fis.read(buff);  // 假设返回 300（最后一批只读到 300 字节）
> 
> fos.write(bytesRead);            // ❌ 调的是 write(int)！把 300 当成一个字节写进去（实际写了 0x2C）
> fos.write(buff);                 // ❌ 整个数组 1024 字节全写，后 724 个是上次残留的垃圾数据
> fos.write(buff, 0, bytesRead);   // ✅ 只写 buff[0]~buff[299]，精确 300 字节
> ```
> 
> **记忆口诀：** `read(byte[])` 返回多少，`write(byte[], 0, 返回值)` 就写多少。三参数 write 是批量 IO 的标准搭配，两个参数的 `write(byte[])` 只有在你**确定数组恰好装满**时才安全。

### Demo

```java
// ⚠️ 读写 IO 都需要处理 IOException：要么方法签名加 throws，要么 try-catch
public void demoByteWrite() throws IOException {
    // 追加写入 — FileOutputStream(path, true)
    try (FileOutputStream fos = new FileOutputStream("output.txt", true)) {
        fos.write("Hello\n".getBytes());  // String → byte[]
    }
}

public void demoByteRead() throws IOException {
    // 批量读取 — read(byte[]) 减少磁盘访问
    try (FileInputStream fis = new FileInputStream("input.txt")) {
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = fis.read(buffer)) != -1) {
            // buffer[0] 到 buffer[bytesRead-1] 是本次读到的有效数据
        }
    }
}
```

---

## 四B. 缓冲字节流 — 让字节流不再"每次读都访问磁盘"

`FileInputStream` 的 `read()` 每次调用都触发一次磁盘访问——读 1MB 文件就是 100 万次磁盘 IO。`BufferedInputStream` 内部维护一个 `byte[8192]` 缓冲区：一次从磁盘读一大块到内存，后续 `read()` 直接从内存取，磁盘访问次数降至 1/8192。

> **装饰器模式：** `BufferedInputStream` 包装了 `FileInputStream`，给它加了缓冲能力，但对外暴露的仍然是 `InputStream` 接口——调用方无感知。

### BufferedInputStream（读字节 + 自动缓冲）

| 方法 | 参数 | 返回值 | 说明 | 注意事项 |
|------|------|--------|------|----------|
| ⭐ `new BufferedInputStream(new FileInputStream(path))` | `InputStream in` | BufferedInputStream | 默认 8KB 缓冲区 | 包装模式，不直接接触文件路径 |
| `new BufferedInputStream(fis, 16384)` | `InputStream in, int size` | BufferedInputStream | 自定义缓冲区大小 | size 一般取 2 的幂，`8192` 是经验最优值 |
| `read()` | 无 | `int` (0~255, -1=EOF) | 从缓冲区读一个字节 | 缓冲区空了才触发一次磁盘 IO |
| `read(byte[])` | `byte[] b` | `int` — 实际读取字节数 | 批量读到数组 | 返回 -1 表示读完 |

### BufferedOutputStream（写字节 + 自动缓冲）

| 方法 | 参数 | 返回值 | 说明 | 注意事项 |
|------|------|--------|------|----------|
| ⭐ `new BufferedOutputStream(new FileOutputStream(path))` | `OutputStream out` | BufferedOutputStream | 默认 8KB 缓冲区 | 数据先写到缓冲区，缓冲区满了才一次写入磁盘 |
| `write(int)` | `int b` — 0~255 | `void` | 写一个字节到缓冲区 | 缓冲区满了自动 flush 到磁盘 |
| `write(byte[])` | `byte[] b` | `void` | 批量写字节到缓冲区 | 同上；注意最后一次可能写入垃圾（同上节陷阱） |
| ⭐ `write(byte[], int off, int len)` | `byte[] b, int offset, int length` | `void` | 写指定范围的字节到缓冲区 | **搭配 `read(byte[])` 返回值使用** |
| ⭐ `flush()` | 无 | `void` | 强制把缓冲区数据写入磁盘 | **写完后必须 flush()，否则缓冲区里剩的数据会丢失！** |
| `close()` | 无 | `void` | 关流（内部自动调 flush()） | try-with-resources 自动调用 |

### Demo

```java
// 缓冲复制 — 代码跟你用 byte[] 手动缓冲几乎一样，但缓冲逻辑被封装了
public void bufferedCopy(String src, String dest) throws IOException {
    try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(src));
         BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(dest))) {
        int b;
        while ((b = bis.read()) != -1) {  // read() 实际从内存缓冲区取，不是从磁盘！
            bos.write(b);
        }
        // bos.close() 会自动 flush()，不必手动调
    }
}
```

> **你的 copyWithBuffer vs BufferedStream：本质区别是什么？**
> 
> 你的 `copyWithBuffer`：缓冲逻辑**暴露在方法代码里**——声明 `byte[]`、传 `bytesRead`、管理偏移量，每次调用都要自己写。
> 
> `BufferedStream`：缓冲逻辑**封装在类内部**——调用方的代码跟逐字节读写一模一样，但底层自动缓冲。这是装饰器模式的核心价值：**增强能力，不改变接口**。

---

## 四C. 字节↔字符桥梁流 — 解决乱码的钥匙

字节流读的是 `byte`，字符流读的是 `char`。中间需要"翻译"——这就是 **InputStreamReader / OutputStreamWriter**。它们是字节流和字符流之间的适配器。

> **昨天你写的 `new BufferedReader(new FileReader(path))` 用的是系统默认编码（Windows 上通常是 GBK，Linux 上是 UTF-8）——同一个文件在不同 OS 上读出来可能乱码。** 要指定编码，必须用桥梁流。

### InputStreamReader（字节→字符，指定编码）

| 方法 | 参数 | 返回值 | 说明 | 注意事项 |
|------|------|--------|------|----------|
| ⭐ `new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8)` | `InputStream in, Charset charset` | InputStreamReader | 用指定编码把字节流转成字符流 | 这是唯一能**显式指定编码**的方式！FileReader 不行 |
| `read()` | 无 | `int` (字符值, -1=EOF) | 读一个字符 | 内部完成字节→字符的编解码 |

### OutputStreamWriter（字符→字节，指定编码）

| 方法 | 参数 | 返回值 | 说明 | 注意事项 |
|------|------|--------|------|----------|
| ⭐ `new OutputStreamWriter(new FileOutputStream(path), StandardCharsets.UTF_8)` | `OutputStream out, Charset charset` | OutputStreamWriter | 用指定编码把字符流转成字节流 | 保证写出的文件是目标编码 |

### 完整 Demo：带编码控制的文本读写

```java
// ✅ 推荐写法：明确指定 UTF-8，跨平台不乱码
try (BufferedReader br = new BufferedReader(
        new InputStreamReader(new FileInputStream("data.txt"), StandardCharsets.UTF_8))) {
    String line;
    while ((line = br.readLine()) != null) {
        System.out.println(line);
    }
}

// ✅ 写入同理
try (BufferedWriter bw = new BufferedWriter(
        new OutputStreamWriter(new FileOutputStream("data.txt"), StandardCharsets.UTF_8))) {
    bw.write("你好，世界");
    bw.newLine();
}
```

> **对照：** `FileReader` = `new InputStreamReader(new FileInputStream(path), Charset.defaultCharset())` — 它只是桥梁流使用**系统默认编码**的快捷方式。跨平台项目**永远用桥梁流指定 UTF-8**。

---

## 四D. PrintWriter — 写文本最省事的类

比 `BufferedWriter` 更方便：自带 `println()`、`printf()`、自动 flush 选项。

| 方法 | 参数 | 返回值 | 说明 | 注意事项 |
|------|------|--------|------|----------|
| ⭐ `new PrintWriter(File file, Charset charset)` | `File, Charset` | PrintWriter | 直接指定文件和编码 | Java 10+，无需 FileWriter |
| ⭐ `new PrintWriter(new OutputStreamWriter(new FileOutputStream(path), StandardCharsets.UTF_8))` | `Writer` | PrintWriter | 桥梁流链，跨平台不乱码 | 所有 Java 版本通用 |
| `new PrintWriter(new FileWriter(path))` | `String` → `Writer` | PrintWriter | 通过 FileWriter 打开 | 编码坑：FileWriter 用系统默认编码 |
| ⭐ `println(String)` | `String` | `void` | 写一行 + 换行 | 相当于 BufferedWriter 的 write + newLine |
| `printf(String format, Object... args)` | 格式化字符串 | `void` | 格式化写入 | 跟 `System.out.printf` 语法一样 |

```java
// ❌ 简单但危险：FileWriter 用系统默认编码
try (PrintWriter pw = new PrintWriter(new FileWriter("log.txt"))) {
    pw.println("中文内容");   // Windows GBK，Mac 打开乱码
}

// ✅ 推荐：PrintWriter + File + Charset（Java 10+）
try (PrintWriter pw = new PrintWriter(new File("log.txt"), StandardCharsets.UTF_8)) {
    pw.println("交易完成");
    pw.printf("金额: %.2f%n", 199.99);
}

// ✅ 等价写法：桥梁流链（所有 Java 版本通用）
try (PrintWriter pw = new PrintWriter(
        new OutputStreamWriter(
            new FileOutputStream("log.txt"),   // false = 覆盖
            StandardCharsets.UTF_8))) {
    pw.println("交易完成");
}
```

---

## 五、字符流 — 专门处理文本

### FileReader / FileWriter（底层流，不带缓冲）

| 方法 | 参数 | 返回值 | 说明 | 注意事项 |
|------|------|--------|------|----------|
| `new FileReader(path)` | `String path` | FileReader | 打开文本文件用于读取 | 文件不存在抛 FileNotFoundException |
| ⭐ `new FileWriter(path, true)` | `String path, boolean append` | FileWriter | 打开文本文件用于追加写入 | `true` = 追加，缺省 = 覆盖 |
| `read()` | 无 | `int`（字符的 Unicode 值） | 读一个字符 | 返回 -1 表示读完 |
| `write(String)` | `String str` | `void` | 写一个字符串 | **不会自动加换行**，需要自己加 `\n` 或调 `newLine()` |

### ⭐ BufferedReader / BufferedWriter（带缓冲 + 按行读写）← 最高频

**缓冲流是 IO 性能优化的第一课。** FileReader/FileWriter 每次读写都访问磁盘；BufferedReader/BufferedWriter 内部维护一个内存缓冲区，一次读一大块到内存，后续从内存取，磁盘访问次数大幅减少。

> **装饰器模式：** `BufferedReader` 包装了 `FileReader` — 给它加了缓冲能力和 `readLine()`，但不改变 Reader 接口。

| 方法 | 参数 | 返回值 | 说明 | 注意事项 |
|------|------|--------|------|----------|
| ⭐ `new BufferedReader(new FileReader(path))` | `Reader reader` | BufferedReader | 给字符流加缓冲区 | 包装模式，不直接接触文件路径 |
| ⭐ `readLine()` | 无 | `String` | 读一行文本（不含换行符） | **返回 null 表示读完！** 这是最常用的文本读取方式 |
| `new BufferedWriter(new FileWriter(path, true))` | `Writer writer` | BufferedWriter | 给字符写流加缓冲区 | 同上，包装模式 |
| ⭐ `write(String)` | `String str` | `void` | 写一个字符串 | 不会自动换行 |
| `newLine()` | 无 | `void` | 写入一个**跨平台**换行符 | 比手动 `\n` 好——Windows 上自动写 `\r\n`，Linux 上写 `\n` |

### 操作步骤拆解

#### 写入文件（逐行追加）

```
要写的数据: "冰霜法杖 → 张三 | 500g"
目标: 追加到 trades.log 末尾，占一行，自动换行
```

| 步骤 | 代码 | 为什么要这步 |
|------|------|-------------|
| ① 打开文件 | `new FileWriter("trades.log", true)` | 连接到磁盘上的文件；`true` = 追加模式，不清空旧内容 |
| ② 包装缓冲 | `new BufferedWriter(①)` | 给第①步加两块能力：内存缓冲（攒一批再写磁盘）+ `newLine()` |
| ③ 自动关闭 | `try (②) { ... }` | 离开 try 块时自动关流，不用写 finally |
| ④ 写入内容 | `bw.write(③)` | 把字符串写进去（只写内容，不换行） |
| ⑤ 补换行 | `bw.newLine()` | 写完一行补一个换行符，下次写入从新行开始 |

```java
// 对应上面5步，完整写法：
public void appendLog(String text) throws IOException {
    try (BufferedWriter bw = new BufferedWriter(           // ②③
            new FileWriter("trades.log", true))) {         // ①
        bw.write(text);                                    // ④
        bw.newLine();                                      // ⑤
    }
}
```

#### 读取文件（逐行读出）

```
目标: 把 trades.log 的每一行读出来，打印到控制台
```

| 步骤 | 代码 | 为什么要这步 |
|------|------|-------------|
| ① 打开文件 | `new FileReader("trades.log")` | 连接到磁盘上的文件 |
| ② 包装缓冲 | `new BufferedReader(①)` | 给第①步加 `readLine()` 能力和内存缓冲 |
| ③ 自动关闭 | `try (②) { ... }` | 离开 try 块自动关流 |
| ④ 循环读行 | `while ((line = br.readLine()) != null)` | 每次调用读一行；读完返回 null，循环退出 |
| ⑤ 处理每行 | `System.out.println(line)` | 对每一行做你想做的事 |

> **`readLine()` 循环套路（记住这个模式就行）：**
> ```java
> String line;
> while ((line = br.readLine()) != null) {
>     // 处理 line
> }
> ```
> 两件事在一条语句里完成：① 读一行赋值给 `line` ② 判断是否为 null。这是 Java IO 里最固定的写法模式。

```java
// 对应上面5步，完整写法：
public void readLog() throws IOException {
    try (BufferedReader br = new BufferedReader(           // ②③
            new FileReader("trades.log"))) {               // ①
        String line;
        while ((line = br.readLine()) != null) {           // ④
            System.out.println(line);                      // ⑤
        }
    }
}
```

---

## 六、try-with-resources — 自动关流（Java 7+）

Java 的 `try` 有两种形态：

### 形态一：普通 try-catch-finally（关流靠自己，不推荐）

```java
FileWriter fw = null;              // 1. 先声明在外面
try {
    fw = new FileWriter("test.txt");
    fw.write("hello");
} catch (IOException e) {
    e.printStackTrace();
} finally {
    if (fw != null) {
        fw.close();                // 3. 手动关流，容易忘
    }
}
```

### ⭐ 形态二：try-with-resources（关流靠 JVM）← 推荐

```java
try (FileWriter fw = new FileWriter("test.txt")) {  // 资源声明在小括号里
    fw.write("hello");
}  // 离开这里时，JVM 自动调用 fw.close()
```

**核心区别：** 把需要"用完就关"的东西声明在 `try()` 的小括号里，JVM 保证离开 try 块时自动关闭。你再也不需要写 `finally { close(); }` 了。

多个资源用分号隔开，关闭顺序是**先创建的后关**（逆序）：

```java
try (FileReader fr = new FileReader("in.txt");    // 先创建
     BufferedReader br = new BufferedReader(fr)) { // 后创建
    String line = br.readLine();
}  // 自动关闭：先关 br，再关 fr
```

> **前提条件：** 只有实现了 `AutoCloseable` 接口的类才能放进 `try()` 里——所有 IO 流类都实现了这个接口。

---

## 七、快速选型决策树

拿到一个文件 IO 需求，按下面流程一秒选对类：

```
需要读写什么？
    │
    ├─ 文本文件（.txt .log .csv .json ...）
    │   │
    │   ├─ 按行读取  → new BufferedReader(new FileReader(path))
    │   ├─ 按行写入  → new BufferedWriter(new FileWriter(path, true))
    │   │              new PrintWriter(new FileWriter(path, true))
    │   ├─ 需要指定编码 → 把 FileReader/FileWriter 换成桥梁流：
    │   │               new BufferedReader(new InputStreamReader(
    │   │                   new FileInputStream(path), StandardCharsets.UTF_8))
    │   └─ 一次性读全文件 → Files.readString(Path.of(path))  // Java 11+
    │
    ├─ 二进制文件（图片 视频 音频 ...）
    │   │
    │   ├─ 复制/读写 → new BufferedInputStream(new FileInputStream(path))
    │   │              new BufferedOutputStream(new FileOutputStream(path))
    │   └─ 小文件一次性 → Files.readAllBytes(Path.of(path))
    │
    ├─ Java 对象 → ObjectInputStream / ObjectOutputStream（序列化章节）
    │
    └─ 基本数据类型 → DataInputStream / DataOutputStream
```

> **记住一个原则：** 永远在底层流外面包一层缓冲流。`FileInputStream` 裸用是新手常见性能杀手。

---

## 八、面试官视角

| 常见问法 | 回答要点 |
|---------|---------|
| **字节流和字符流的区别？** | 字节流处理二进制（图片/视频），最小单位 byte；字符流处理文本，最小单位 char，内置编解码 |
| **FileInputStream 和 BufferedInputStream 的区别？** | 前者每次 read() 都访问磁盘；后者内部维护 `byte[8192]` 缓冲区，磁盘访问次数降为 1/8192。装饰器模式 |
| **BufferedInputStream 默认缓冲区多大？** | 8192 字节（8KB），构造器可自定义 |
| **read(byte[]) 返回值有什么用？** | 返回实际读取的字节数。最后一次读取可能填不满 buffer，直接 write(buffer) 会写入垃圾数据 |
| **read() 为什么返回 int 而不是 byte？** | byte 范围 -128~127，而字节数据 0~255（无符号），且 -1 被用作 EOF 信号。用 int 可区分 255（数据）和 -1（EOF） |
| **readLine() 返回 null 是什么意思？** | 文件读完了（EOF）。注意不是空行——空行返回 `""`，EOF 返回 `null` |
| **字符流读取时为什么会出现乱码？** | 文件的编码和读取指定的编码不一致。FileReader 用系统默认编码，跨平台必须用 `InputStreamReader` + 显式指定 `StandardCharsets.UTF_8` |
| **try-with-resources 的原理？** | 实现了 `AutoCloseable` 的对象在 try 块结束后自动调用 `close()`，**逆序关闭** |
| **如何高效复制一个大文件？** | 字节流 + 8KB+ 缓冲区，或 `Files.copy(Path, Path)`（NIO.2 内部自动优化） |
| **flush() 和 close() 的区别？** | flush() 只刷缓冲区数据到磁盘，流仍可用；close() 先 flush() 再释放系统资源，流不可再用 |

---

> 文档已补齐——缓冲字节流、桥梁流、PrintWriter、选型决策树，全都在了。现在回去写 FileBackup 吧！
