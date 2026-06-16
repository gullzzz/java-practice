# 阶段 7：接口与抽象类

配套代码：`src/com/practice/interfaces/`

---

## 7.1 抽象类（Abstract Class）

**定义**：使用 `abstract` 关键字修饰的类，不能被实例化（不能 new），只能被继承。抽象类可以有字段、构造器、普通方法（有方法体）和抽象方法（无方法体，子类必须实现或子类也是抽象类）。它的本质是"不完整的类"，等待子类完成具体实现。

**解决问题/用途**：当一组类有共同属性和部分共同行为，但部分行为无法在父类中给出通用实现时，用抽象类。例如 Vehicle 有 brand 属性和启动/停止行为——但"怎么启动"因车而异（燃油车点火、电动车通电）。把 start() 声明为抽象方法，强制每种车提供自己的启动方式，同时把公共逻辑（stop、getBrand）写在抽象类中复用。抽象类 = 部分复用 + 部分强制实现。

```java
public abstract class Vehicle {
    private String brand;

    public Vehicle(String brand) {
        this.brand = brand;
    }

    // 抽象方法：子类必须实现
    public abstract void start();

    // 普通方法：子类可以直接继承
    public void stop() {
        System.out.println(brand + " 熄火了。");
    }
}
```

特点：
- 可以有**构造器**（供子类调用）
- 可以有**字段**和**普通方法**
- 抽象方法必须在子类中实现（或子类也是抽象类）

---

## 7.2 接口（Interface）

**定义**：接口是用 `interface` 关键字定义的一组行为规范——声明了实现类必须提供哪些方法。Java 8+ 接口可以包含抽象方法（无方法体）、default 方法（有默认实现）、static 方法（属于接口本身）和常量（隐式 public static final）。

**解决问题/用途**：接口定义了"能做什么"的契约，但不关心"怎么做"和"你是什么"。比如"会飞的"这个能力——飞机、鸟、超人都会飞，但它们没有继承关系。接口让不同类层次的对象共享同一套行为规范，这是 Java 单继承的最大补充。接口也是面向接口编程的基础——调用方依赖接口而非实现类，降低耦合。

```java
public interface Flyable {
    // 常量（默认 public static final）
    String TYPE = "飞行物";

    // 抽象方法（默认 public abstract）
    void fly();

    // Java 8+ default 方法（有默认实现）
    default void land() {
        System.out.println("正在降落...");
    }

    // Java 8+ static 方法
    static String getWingInfo() {
        return "翅膀是实现飞行的关键";
    }
}
```

---

## 7.3 实现接口

**定义**：类使用 `implements` 关键字来遵守接口契约，必须提供接口中所有抽象方法的实现（除非该类是抽象类）。一个类可以实现多个接口，用逗号分隔。同时可以 extends 一个父类 + implements 多个接口。

**解决问题/用途**：这是 Java 单继承限制的关键解决方案——一个类只能继承一个父类，但可以实现任意多个接口。例如 Duck 可以同时实现 Flyable 和 Swimmable，既会飞又会游泳，而不用纠结 Duck 应该继承 Bird 还是 Fish。

```java
public class Car extends Vehicle implements Flyable {
    @Override
    public void start() { ... }  // 实现抽象方法

    @Override
    public void fly() { ... }    // 实现接口方法
}

// 实现多个接口
public class Duck implements Flyable, Swimmable {
    @Override
    public void fly() { ... }
    @Override
    public void swim() { ... }
}
```

- 一个类可以实现**多个接口**（弥补单继承的限制）
- 必须实现所有抽象方法（或声明为 abstract）

---

## 7.4 抽象类 vs 接口

**定义**：抽象类和接口都是无法直接实例化的类型，但设计目的不同——抽象类表达"是什么"（is-a），共享属性和部分实现；接口表达"能做什么"（can-do），只定义行为契约。

**解决问题/用途**：明确选择标准——需要共享字段和构造器→抽象类；只定义行为规范→接口；需要多继承行为→只能是接口。在 Java 8+ 时代，接口通过 default 方法也能提供默认实现，抽象类和接口的界限在模糊化，但构造器和字段仍然是抽象类的独有优势。

| | 抽象类 | 接口 |
|------|--------|------|
| 继承/实现 | 单继承 `extends` | 多实现 `implements` |
| 构造器 | 可以有 | 不能有 |
| 字段 | 可以有 | 只能有常量 |
| 方法实现 | 可以有 | Java 8+ 有 default/static |
| 访问修饰符 | 各种 | 默认 public |
| 语义 | **是什么**（is-a） | **能做什么**（can-do） |

**选择原则**：
- 需要共享属性和构造器 → 抽象类
- 只定义行为契约，不限制层次 → 接口
- 需要多继承行为 → 接口（只能是接口）

---

## 7.5 接口的 default 方法（Java 8+）

**定义**：`default` 方法是在接口中提供默认实现的方法，实现类可以覆盖它，也可以直接继承默认实现。这是 Java 8 的重大创新——在不破坏已有实现类的前提下，给接口增加新方法。

**解决问题/用途**：从历史上看，接口加新方法会炸掉所有实现类（编译错误，因为缺少实现）。比如要在 List 接口加一个 `sort()` 方法，JDK 所有 List 实现类都会炸。default 方法彻底解决了这个问题——JDK 开发者可以在接口层面提供通用的默认排序实现，各实现类不用修改代码就能用上。

```java
public interface Flyable {
    void fly();

    // 新增 default 方法，已有实现类不用修改
    default void land() {
        System.out.println("正在降落...");
    }
}
```

如果两个接口有同名 default 方法，实现类必须手动指定：

```java
public class Duck implements Flyable, Swimmable {
    // 如果 Flyable 和 Swimmable 都有 land()
    @Override
    public void land() {
        Flyable.super.land();  // 指定用哪个接口的实现
    }
}
```

---

## 7.6 接口的 static 方法

**定义**：Java 8+ 接口中可以定义 static 方法，有自己的方法体，通过 `接口名.方法名()` 调用，不依附于任何实现类实例。

**解决问题/用途**：接口相关的工具函数以前没有好去处——要么放在抽象类（但接口不需要抽象类），要么放单独的 XXXUtil 类。现在可以直接放在接口里，保持内聚：Flyable 相关的工具方法就放在 Flyable 接口中，使用方只需记住一个名字。

```java
public interface Flyable {
    static String getWingInfo() {
        return "翅膀是飞行的关键";
    }
}

// 调用
String info = Flyable.getWingInfo();  // 通过接口名调用
```

属于接口本身，不属于实现类。

---

## 7.7 接口的多态

**定义**：与继承多态同理——接口引用指向实现类对象，调用重写的方法时执行实现类的方法。同一个对象可以通过不同的接口引用表现出不同的"身份"（一个 Duck 既是 Flyable 又是 Swimmable）。

**解决问题/用途**：接口多态让代码极致解耦——调用方只依赖接口，不依赖任何具体实现。你可以自由替换实现类（从 MySQL 切到 PostgreSQL、从 List 切到 Set）而不影响调用方代码。这是DI（依赖注入）和单元测试 mocking 的底层基础。

```java
Flyable f1 = new Car("BMW");
Flyable f2 = new Duck("小黄鸭");

f1.fly();   // Car 的 fly
f2.fly();   // Duck 的 fly

// 同一对象可以表现为不同接口类型
Duck duck = new Duck("唐老鸭");
Flyable flyingDuck = duck;        // 向上转型
Swimmable swimmingDuck = duck;    // 向上转型
```

---

## 7.8 设计模式启示

- **面向接口编程**：依赖接口而非具体实现
```java
List<String> list = new ArrayList<>();  // 用 List 接口声明
```

- **接口隔离**：接口应小而专，不要一个接口包含所有方法
- **组合 > 继承**：用接口定义行为，用组合复用代码
