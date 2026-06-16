# 第2.4章：多态（Polymorphism）

配套代码：`src/com/practice/polymorphism/`

---

## 一、定义

**多态**：同一个行为具有多个不同表现形式。Java中指父类引用指向子类对象，调用方法时执行的是**实际对象类型**的方法，而非引用类型的方法。

```java
Animal a = new Dog();  // 父类引用 → 子类对象
a.makeSound();         // 执行的是 Dog 的 makeSound()
```

## 二、核心作用

**解决问题/用途**：多态解决的核心问题是"如何写出不依赖具体类型的通用代码"。没有多态，每种动物都得写一个专用方法——`feedDog(Dog d)`、`feedCat(Cat c)`，新增动物就得加新方法。有了多态，只需写一个 `feed(Animal a)`，任何 Animal 子类都能传入。这是开闭原则（对扩展开放、对修改关闭）的技术基础。

1. **统一接口**：用父类类型统一操作不同子类对象，无需关心具体类型
2. **可扩展性**：新增子类无需修改已有代码（开闭原则）
3. **解耦**：调用方只依赖父类/接口，不依赖具体实现

```java
// 不修改此方法，新增任何Animal子类都能正常工作
void feed(Animal a) {
    a.eat();
}
```

## 三、语法格式

### 3.1 前提条件

- 存在继承关系（extends）或接口实现（implements）
- 子类重写父类方法
- 父类引用指向子类对象

### 3.2 向上转型（Upcasting）—— 自动

**定义**：将子类对象赋值给父类类型的引用变量，由编译器自动完成，没有任何风险。转型后只能调用父类中定义的方法，子类特有方法暂时不可见。

**解决问题/用途**：向上转型让你可以用父类类型去接收和处理不同子类对象。这是多态的基本前提——只有通过父类引用指向子类对象，才能实现"同一调用、不同行为"的效果。

```java
父类类型 变量名 = new 子类构造器();

// 示例
Animal a = new Dog("旺财");   // Dog → Animal，自动
Object o = "hello";           // String → Object，自动
List list = new ArrayList();  // ArrayList → List，自动
```

特点：通过父类引用**只能调用父类中定义的方法**，无法调用子类特有方法。

```java
Animal a = new Dog("旺财");
a.makeSound();   // ✅ 可以（Animal中定义过）
a.wagTail();     // ❌ 编译错误（Animal中没有wagTail）
```

### 3.3 向下转型（Downcasting）—— 强制

**定义**：将父类引用强制转换回子类类型，需要显式写 `(子类类型)` 进行强转，有运行时风险——如果实际对象不是目标类型，会抛出 ClassCastException。

**解决问题/用途**：向上转型后子类特有方法暂时不可见，当你需要调用子类特有方法（如 Dog 独有的 wagTail）时，必须向下转型。向下转型本身是安全操作的前置步骤——先向上转型获得通用处理能力，必要时向下转型访问子类特有功能。

```java
子类类型 变量名 = (子类类型) 父类引用;

// 示例
Animal a = new Dog("旺财");
Dog d = (Dog) a;    // 强制转换
d.wagTail();        // 现在可以调用Dog特有方法了
```

风险：如果实际对象不是目标类型，运行时会抛 `ClassCastException`。

```java
Animal a = new Cat("咪咪");
Dog d = (Dog) a;  // 💥 ClassCastException: Cat cannot be cast to Dog
```

### 3.4 instanceof —— 类型检查

**定义**：`instanceof` 是 Java 的二元运算符，用于在运行时检查对象是否是指定类型（或其子类型）的实例，返回 boolean。Java 16+ 的模式匹配语法让判断和转换可以一步完成。

**解决问题/用途**：向下转型需要确保安全——盲目强转可能 ClassCastException。instanceof 在转型前验证实际类型，提供"先检查、再转换"的安全路径。模式匹配（Java 16+）进一步简化了语法，消除了"判断后还要单独声明变量并强转"的冗余。

转型前先用 `instanceof` 判断实际类型，确保安全。

```java
if (对象 instanceof 目标类型) {
    目标类型 变量 = (目标类型) 对象;
    // 安全操作
}

// 示例
if (a instanceof Dog) {
    Dog d = (Dog) a;
    d.wagTail();
}
```

**Java 16+ 模式匹配**（简化写法，一步完成判断+转换）：

```java
if (a instanceof Dog d) {   // 判断 + 声明变量 + 强转，一步到位
    d.wagTail();
}
```

## 四、使用场景

| 场景 | 说明 |
|------|------|
| 方法参数 | 参数声明为父类类型，可传入任意子类对象 |
| 返回值 | 返回父类类型，实际返回子类对象 |
| 集合/数组 | 用父类类型声明集合，存储不同子类对象 |
| 工厂模式 | 根据条件返回不同子类，调用方只依赖父类 |

```java
// 场景1：方法参数多态
void makeItSound(Animal a) {
    a.makeSound();
}
makeItSound(new Dog("旺财"));
makeItSound(new Cat("咪咪"));

// 场景2：返回值多态
Animal createAnimal(String type) {
    if ("dog".equals(type)) return new Dog("旺财");
    else return new Cat("咪咪");
}

// 场景3：集合多态
List<Animal> zoo = new ArrayList<>();
zoo.add(new Dog("旺财"));
zoo.add(new Cat("咪咪"));
```

## 五、易错注意点

1. **向上转型后无法调用子类特有方法**：编译看左边（引用类型），运行看右边（实际类型）
2. **向下转型必须先用 instanceof 判断**，否则可能 ClassCastException
3. **static 方法没有多态**：静态方法与类绑定，不是对象，重写对静态方法无效
4. **字段没有多态**：成员变量看引用类型，不看实际对象类型
5. **private 方法没有多态**：私有方法不能被重写，不存在多态
6. **构造方法没有多态**：构造方法不能被重写

```java
// 易错点4：字段没有多态
class Parent {
    String name = "Parent";
}
class Child extends Parent {
    String name = "Child";
}
Parent p = new Child();
System.out.println(p.name);  // "Parent" ← 看引用类型，不是"Child"！
```
