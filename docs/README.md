# Java 17 学习指南

基于 Java 17 LTS，从零开始系统学习 Java 编程。

## 学习路线

| 阶段 | 主题 | 代码包 | 理论文档 |
|------|------|--------|----------|
| 1 | 基础语法 | `com.practice.basics` | [01-基础语法.md](01-基础语法.md) |
| 2 | 控制流程 | `com.practice.controlflow` | [02-控制流程.md](02-控制流程.md) |
| 3 | 方法 | `com.practice.methods` | [03-方法.md](03-方法.md) |
| 4 | 数组 | `com.practice.arrays` | [04-数组.md](04-数组.md) |
| 5 | OOP 基础 | `com.practice.oop` | [05-OOP基础.md](05-OOP基础.md) |
| 6 | 继承与多态 | `com.practice.inheritance` | [06-继承与多态.md](06-继承与多态.md) |
| 7 | 接口与抽象类 | `com.practice.interfaces` | [07-接口与抽象类.md](07-接口与抽象类.md) |
| 8 | 集合框架 | `com.practice.collections` | [08-集合框架.md](08-集合框架.md) |
| 9 | 异常处理 | `com.practice.exceptions` | [09-异常处理.md](09-异常处理.md) |
| 10 | Java 17 新特性 | `com.practice.java17` | [10-Java17新特性.md](10-Java17新特性.md) |

## 环境要求

- **JDK 17+**（LTS 长期支持版本）
- 任意文本编辑器或 IDE（推荐 IntelliJ IDEA Community）

## 使用方式

```bash
# 编译所有源码
compile.bat

# 运行指定阶段示例
java -cp out com.practice.basics.BasicsDemo
java -cp out com.practice.oop.OopDemo
```

## 学习建议

1. **按顺序学习**：每个阶段依赖前一个阶段的知识
2. **先读理论，再看代码**：每个阶段先阅读对应的 `.md` 文档，再运行代码
3. **动手修改**：在示例代码基础上修改、实验
4. **遇到问题先自己调试**：打印变量、查阅文档、理解报错信息
