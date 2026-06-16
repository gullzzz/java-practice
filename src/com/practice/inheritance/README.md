# 阶段 6：继承与多态

配套代码：`src/com/practice/inheritance/`

---

## 6.1 继承的概念

**定义**：继承是面向对象的三大特性之一，使用 `extends` 关键字让子类自动获得父类的非 private 属性和方法。子类与父类建立 **is-a** 关系（狗是动物）。Java 是单继承——一个类只能直接继承一个父类，但父类还可以有自己的父类形成继承链，同时可以实现多个接口弥补单继承的限制。

**解决问题/用途**：代码复用是继承最直接的好处——所有动物共享的吃、睡、移动逻辑写在 Animal 里，Dog、Cat 不用重复写。更深层的价值是建立类型层次结构，让"以统一方式处理不同子类"成为可能（这是多态的前提）。

```java
public class Animal {          // 父类（基类）
    private String name;

    public Animal(String name) {
        this.name = name;
    }

    public void makeSound() {
        System.out.println(name + " 发出声音...");
    }
}

public class Dog extends Animal {  // 子类（派生类）
    public Dog(String name) {
        super(name);  // 调用父类构造器
    }

    @Override
    public void makeSound() {  // 重写
        System.out.println(getName() + " 汪汪！");
    }
}
```

Java **单继承**：一个类只能继承一个父类（但可以实现多个接口）。

---

## 6.2 super 关键字

**定义**：`super` 是子类中引用父类部分的特殊关键字。主要有三个用途——调用父类构造器（`super(args)`，必须在子类构造器第一行）、调用父类被重写的方法（`super.method()`）、访问父类的字段（当子类有同名字段时）。

**解决问题/用途**：子类构造时必须先初始化父类部分（因为子类是建立在父类基础上的），super() 确保父类构造器被调用。在方法重写场景中，子类有时想在父类原有逻辑的基础上"增加"而非完全"替换"——`super.makeSound()` 先执行父类发声逻辑，再追加子类特有行为。

`super` 指向**父类**，是 `this` 的对应物：

```java
public Dog(String name) {
    super(name);  // 1. 调用父类构造器（必须在第一行）
}

@Override
public void makeSound() {
    super.makeSound();  // 2. 调用父类被重写的方法
    System.out.println("汪汪！");
}
```

---

## 6.3 方法重写（Override）

**定义**：子类提供与父类方法签名完全相同（方法名、参数列表、返回类型一致或返回子类型）的新实现来覆盖父类方法。使用 `@Override` 注解让编译器验证是否真的在重写（防止拼写错误导致意外重载）。final 方法和 static 方法不能被重写。

**解决问题/用途**：父类定义了"动物叫"这个通用行为，但 Dog 叫"汪汪"、Cat 叫"喵喵"——每个子类需要不同的发声方式。重写让子类可以在不改变父类接口的前提下定制自己的行为，这是多态的必备条件。

子类重新定义父类的方法：

```java
@Override  // 注解：编译器检查是否真的在重写
public void makeSound() {
    // 新实现
}
```

规则：
- 方法名、参数列表、返回类型必须相同（或返回子类型）
- 访问权限不能更严格（public → public 可以，public → private 不行）
- **final 方法不能被重写**
- **static 方法不能被重写**（可以隐藏，但不推荐）

---

## 6.4 多态（Polymorphism）

**定义**：多态指同一个方法调用在不同对象上表现出不同行为。在 Java 中体现为——父类引用指向子类对象时，调用重写方法执行的是实际子类对象的方法，而不是引用类型的方法，这叫动态绑定。

**解决问题/用途**：多态是 OOP 最强大的特性。它能让你写出"面向抽象"的代码——一个 `feed(Animal a)` 方法可以给任何动物喂食，不需要为 Dog、Cat 各写一个方法，新增 Tiger 时也不用改这个方法的代码。这就是开闭原则（对扩展开放，对修改关闭）。

**核心**：父类引用指向子类对象，调用方法时执行的是**实际对象**的方法。

```java
Animal a1 = new Dog("旺财");  // 向上转型（自动）
Animal a2 = new Cat("咪咪");

a1.makeSound();  // "旺财 汪汪！" ← 调用的是 Dog 的方法
a2.makeSound();  // "咪咪 喵喵！" ← 调用的是 Cat 的方法
```

这就是**动态绑定**：运行时确定调用哪个方法。

### 多态的价值

```java
// 不需要知道具体类型，统一操作
Animal[] zoo = {new Dog("阿福"), new Cat("球球")};
for (Animal a : zoo) {
    a.makeSound();  // 每个动物以自己的方式发声
}
```

不修改循环代码就能处理不同类型的动物 —— **开闭原则**。

---

## 6.5 向上转型 vs 向下转型

**定义**：向上转型是子类对象赋值给父类引用，自动安全。向下转型是父类引用强制转回子类类型，有风险——如果实际对象不是目标类型，运行时会抛出 ClassCastException。安全做法是先用 `instanceof` 检查实际类型。

**解决问题/用途**：向上转型让你可以用父类类型统一处理不同子类。但有时你需要调用子类特有方法（Dog 独有的 wagTail() 在 Animal 中不存在），就需要向下转型。instanceof 和模式匹配（Java 16+）提供了安全的转型方式。

```java
// 向上转型（自动）：子类 → 父类
Animal a = new Dog("小黑");  // 安全，Dog is-a Animal

// 向下转型（强制）：父类 → 子类
Dog d = (Dog) a;  // 危险！如果a不是Dog会抛ClassCastException
d.wagTail();       // 可以调用Dog特有方法
```

**安全做法 — instanceof 检查**：

```java
if (a instanceof Dog) {
    Dog d = (Dog) a;
    d.wagTail();
}

// Java 16+ 简化写法（模式匹配）
if (a instanceof Dog dog) {
    dog.wagTail();  // 直接用变量，无需强转
}
```

---

## 6.6 访问修饰符与继承

**定义**：四个访问级别从严格到宽松为 private（仅同类）、default（同包可见）、protected（同包+子类可见）、public（全局可见）。`protected` 专为继承设计——让子类能访问父类成员，外部不可见。

**解决问题/用途**：封装原则要求字段 private，但子类可能需要直接访问父类的一些内部数据。protected 提供了"对子类开放，对外部隐藏"的中间选择，既维持封装又支持继承。

| 修饰符 | 子类能访问父类成员？ |
|--------|---------------------|
| `private` | **不能** |
| `default`（同包） | 同包子类可以 |
| `protected` | **可以**（同包或不同包都行） |
| `public` | 可以 |

`protected` 就是为继承设计的 —— 对子类可见，对外部不可见。

---

## 6.7 final 关键字

**定义**：`final` 可以修饰类（不能被继承）、方法（不能被子类重写）、变量（赋值后不可修改）。String、Integer 等核心类都是 final 的，JDK 设计者不希望它们的语义被子类破坏。

**解决问题/用途**：final 在三个层面提供"不可变"保障——防止意外继承破坏类的完整性（如 String 一旦被继承就可能改变不可变约定）、防止关键方法被篡改（如安全相关方法）、确保常量不被修改。

| 用法 | 含义 |
|------|------|
| `final class` | 不能被继承 |
| `final` 方法 | 不能被重写 |
| `final` 变量 | 不可修改（常量） |

```java
public final class String { ... }     // String 不能被继承
public final void sleep() { ... }    // 子类不能重写 sleep
final double PI = 3.14159;           // 常量
```

---

## 6.8 继承层次中的构造顺序

**定义**：创建子类对象时，构造器按继承链从顶向下执行——先执行 Object 构造器，再逐层向下到实际子类的构造器。子类构造器的第一条语句必须是调用父类构造器（隐式 `super()` 或显式 `super(args)`）。

**解决问题/用途**：子类建立在父类基础上，父类部分必须先初始化完整才能构建子类部分。理解这个顺序可以避免在构造器中调用可被子类重写的方法（父类构造时子类字段还是默认值，会导致诡异的 bug）。

```
new Dog("旺财") 的执行顺序：
1. Animal 构造器执行（super调用）
2. Dog 构造器执行
```

子类构造器**必须先调用父类构造器**（隐式 `super()` 或显式 `super(args)`）。

---

## 6.9 Object 类 —— 所有类的终极父类

**定义**：`java.lang.Object` 是所有 Java 类的终极祖先——每个类都直接或间接继承自 Object。Object 提供了所有对象都具备的基础方法——toString()、equals()、hashCode()、getClass() 等。

**解决问题/用途**：Object 的存在让 Java 有了统一的类型根——任何对象都可以赋值给 Object 类型的变量，这让通用容器（如早期的 ArrayList 存 Object）、通用工具方法（如 Collections.sort 接受 Object）成为可能。你写任何类时都自动获得这些基础方法。

Java 中所有类都直接或间接继承自 `Object`：

```java
// Object 提供的核心方法
toString()    // 返回字符串表示
equals(obj)   // 判断是否相等
hashCode()    // 返回哈希码
getClass()    // 返回运行时类
```

---

## 6.10 组合 vs 继承

**定义**：继承表达 **is-a** 关系（狗是动物），通过 extends 复用代码。组合表达 **has-a** 关系（人有地址），通过持有另一个类的引用来复用功能。

**解决问题/用途**：继承是一把双刃剑——用好了省代码，滥用则导致类层次僵硬难改（父类一改，所有子类受影响）。组合更灵活：一个 Person 可以随时换 Address 的实现，不受"你是什么"的层次约束。Effective Java 的建议：优先使用组合，只在明确的 is-a 关系时才用继承。

继承 = **is-a**（狗是动物）
组合 = **has-a**（人有地址）

```java
// 组合
public class Person {
    private Address address;  // Person has-a Address
}
```

推荐优先使用**组合**，只在明确的 is-a 关系时才用继承。
