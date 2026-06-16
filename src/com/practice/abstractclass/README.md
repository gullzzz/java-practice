# 抽象类 (abstract class)

---

## 1. 抽象类的概念

**定义**：用 `abstract` 关键字修饰的类，不能被实例化（不能 new），只能被继承。抽象类通常包含被 `abstract` 修饰的方法——抽象方法没有方法体，只有一个分号。子类必须实现所有抽象方法，除非子类本身也是抽象类。

**解决问题/用途**：当你发现"父类的方法根本没法写通用实现，因为每个子类的行为完全不同"时，就需要抽象类。例如 Animal 的 makeSound()——狗汪汪叫、猫喵喵叫，Animal 本身叫什么？没法定义。把 makeSound() 声明为 abstract，等于对子类说："这个行为你们各自去实现，我不定义默认版本。"

```java
// 抽象类：不能被 new
public abstract class Animal {
    protected String name;

    public Animal(String name) {
        this.name = name;
    }

    // 普通方法：子类直接继承
    public void sleep() {
        System.out.println(name + " 呼呼大睡...");
    }

    // 抽象方法：没有方法体，子类必须实现
    public abstract void makeSound();
}
```

```java
public class Dog extends Animal {
    public Dog(String name) { super(name); }

    @Override
    public void makeSound() {  // 必须重写！
        System.out.println(name + " 汪汪！");
    }
}
```

---

## 2. 抽象类 vs 接口

| 维度 | 抽象类 | 接口 (Java 17) |
|------|--------|---------------|
| 关键字 | `abstract class` | `interface` |
| 实例化 | 不能 | 不能 |
| 构造器 | 可以有 | 不能有 |
| 字段 | 可以有实例变量 | 只能有常量 (`public static final`) |
| 方法 | 普通方法 + 抽象方法 | 抽象方法 + default/static 方法 |
| 继承 | 单继承 (`extends`) | 多实现 (`implements`) |
| 访问修饰符 | 任意 | 方法默认 public |
| **关系** | is-a（是一种） | can-do（能做某事） |

---

## 3. 何时用抽象类？何时用接口？

- **抽象类**：共享状态 + 共同行为。子类们有共同字段（name/age），且部分方法实现一致。
  - 问题：Java 单继承，用一次就"用掉"了唯一的机会。
- **接口**：定义能力契约。不同类的对象能做同一件事，但实现完全无关。
  - 例子：`Comparable`、`Runnable`——完全不相关的类都可以实现。

**经验法则**：需要共享字段 → 抽象类。只需要共享行为 → 接口。

---

## 4. 关键规则速查

| 规则 | 说明 |
|------|------|
| 抽象类不能被 new | `new Animal()` → 编译错误 |
| 抽象类可以有构造器 | 给子类通过 `super()` 调用 |
| 抽象方法不能有方法体 | 连 `{}` 都不能写 |
| 子类必须实现全部抽象方法 | 否则子类也必须是 abstract |
| `abstract` 和 `final` 互斥 | final 类不能被继承，final 方法不能被重写 |
| `abstract` 和 `private` 互斥 | private 方法子类无法访问，更无法重写 |
| `abstract` 和 `static` 互斥 | static 方法属于类，不存在重写 |

```java
// 编译错误示例
public abstract final class X {}    // ✗ abstract + final 冲突
public abstract static void f();    // ✗ abstract + static 冲突
public abstract private void f();   // ✗ abstract + private 冲突
```

---

## 5. 模板方法模式（关键应用场景）

抽象类最常见的实战模式——父类定义算法骨架，把可变步骤留给子类实现：

```java
public abstract class DataProcessor {
    // 模板方法：定义算法骨架（final 防止子类篡改流程）
    public final void process() {
        loadData();
        doTransform();   // 交给子类
        saveResult();
    }

    private void loadData() { System.out.println("1. 加载数据..."); }
    private void saveResult() { System.out.println("3. 保存结果..."); }

    // 抽象方法：子类各自实现
    protected abstract void doTransform();
}
```

---

## 6. 面试官视角

| 考察点 | 参考答案 |
|--------|---------|
| 抽象类和接口的区别？什么时候用哪个？ | 抽象类有构造器、可带实例字段、单继承，表达"is-a"；接口只能有常量、多实现，表达"can-do"。需要共享状态和默认行为用抽象类，只需定义契约用接口。 |
| `abstract` 能和 `final`/`private`/`static` 一起用吗？ | 都不能。final 阻止重写与 abstract 必须重写矛盾；private 子类不可见无法重写；static 属于类不存在重写语义。 |
| 抽象类可以有构造器吗？既然不能 new，要构造器干嘛？ | 可以，给子类的 `super()` 调用链用。子类构造器必须先初始化父类部分。 |
| 模板方法模式是什么？ | 抽象类定义算法骨架（final 方法），把可变步骤声明为 abstract 让子类实现。如 Servlet 的 service() 方法。 |
| 抽象类可以不包含任何抽象方法吗？ | 可以。只想强制使用父类、阻止直接实例化时这样做，但更常用的是 private 构造器 + 静态工厂。 |
