# Demo 学习指南

> 更新时间：2026-05-11

---

## 学习顺序

按下列顺序依次阅读理论文档，再运行对应Demo代码：

| 序号 | 章节 | 理论文档 | Demo入口 |
|------|------|----------|----------|
| 1 | HelloWorld & 程序结构 | docs/01-基础语法.md | src/com/practice/HelloWorld.java |
| 2 | 变量与数据类型 | docs/01-基础语法.md | src/com/practice/basics/BasicsDemo.java |
| 3 | 控制流程 | docs/02-控制流程.md | src/com/practice/controlflow/ControlFlowDemo.java |
| 4 | 方法定义与调用 | docs/03-方法.md | src/com/practice/methods/MethodsDemo.java |
| 5 | 数组 | docs/04-数组.md | src/com/practice/arrays/ArraysDemo.java |
| 6 | 类与对象、构造方法、封装 | docs/05-OOP基础.md | src/com/practice/oop/OopDemo.java |
| 7 | 继承 | docs/06-继承与多态.md | src/com/practice/inheritance/InheritanceDemo.java |
| 8 | 接口 | docs/07-接口与抽象类.md | src/com/practice/interfaces/InterfaceDemo.java |
| 9 | 多态（Polymorphism） | docs/11-多态详解.md | src/com/practice/polymorphism/PolymorphismDemo.java |
| 10 | 集合框架 | docs/08-集合框架.md | src/com/practice/collections/CollectionsDemo.java |
| 11 | 异常处理 | docs/09-异常处理.md | src/com/practice/exceptions/ExceptionsDemo.java |
| 12 | Java 17 新特性 | docs/10-Java17新特性.md | src/com/practice/java17/Java17Demo.java |

---

## 各Demo功能说明

### 09-多态（PolymorphismDemo.java）

| 方法 | 演示内容 |
|------|----------|
| `upcasting()` | 向上转型：子类→父类（自动），调用重写方法执行实际对象类型的方法 |
| `downcasting()` | 向下转型：父类→子类（强制），错误转型抛出ClassCastException |
| `instanceofDemo()` | instanceof类型检查 + Java 16+模式匹配写法 |
| `staticMethodNoPoly()` | 静态方法没有多态——看引用类型，不看实际对象类型 |
| `fieldNoPoly()` | 成员变量没有多态——字段隐藏，非重写 |
| `methodParamPoly()` | 方法参数多态——参数声明为父类，传入不同子类 |
| `collectionPoly()` | 集合/数组多态——父类类型存储不同子类对象 |

---

## 运行方式

### 编译所有Demo
```bash
javac -encoding UTF-8 -d out src/com/practice/polymorphism/*.java
```

### 运行
```bash
java -cp out com.practice.polymorphism.PolymorphismDemo
```

### 编译+运行一步
```bash
javac -encoding UTF-8 -d out src/com/practice/polymorphism/*.java && java -cp out com.practice.polymorphism.PolymorphismDemo
```
