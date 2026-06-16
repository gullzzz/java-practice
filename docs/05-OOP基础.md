# 阶段 5：OOP 基础

配套代码：`src/com/practice/oop/Person.java`、`OopDemo.java`

---

## 5.1 面向对象核心概念

| 概念 | 含义 |
|------|------|
| **类（Class）** | 对象的蓝图/模板 |
| **对象（Object）** | 类的具体实例 |
| **属性（Field）** | 对象的状态/数据 |
| **方法（Method）** | 对象的行为/功能 |

类比：`Person` 类是"人类"这个概念，`new Person("张三", 25)` 是具体的一个"人"。

---

## 5.2 类的定义

```java
public class Person {
    // 1. 字段（属性）
    private String name;
    private int age;

    // 2. 构造器
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    // 3. 方法
    public void introduce() {
        System.out.println("我叫" + name);
    }
}
```

---

## 5.3 构造器（Constructor）

```java
// 无参构造器
public Person() {
    this("未命名", 0);  // 调用另一个构造器
}

// 全参构造器
public Person(String name, int age) {
    this.name = name;
    this.age = age;
}
```

要点：
- 构造器名称**必须与类名相同**
- **没有返回类型**（连 void 都没有）
- 用 `new` 调用：`Person p = new Person("张三", 25);`
- 如果没有写任何构造器，编译器会**自动生成一个无参构造器**
- 如果写了任意构造器，编译器**不再自动生成**无参构造器
- `this(...)` 调用另一个构造器，**必须放在第一行**

---

## 5.4 封装（Encapsulation）

**核心思想**：隐藏内部实现，只暴露安全的接口。

```java
public class Person {
    private String name;     // 字段私有

    public String getName() {      // Getter（访问器）
        return name;
    }

    public void setName(String name) {  // Setter（修改器）
        if (name != null && !name.isBlank()) {  // 校验
            this.name = name;
        }
    }
}
```

访问修饰符：

| 修饰符 | 同类 | 同包 | 子类 | 任何地方 |
|--------|------|------|------|----------|
| `private` | ✓ | | | |
| `default`（不加） | ✓ | ✓ | | |
| `protected` | ✓ | ✓ | ✓ | |
| `public` | ✓ | ✓ | ✓ | ✓ |

最严格的 `private` 到最开放的 `public`。封装原则：**字段用 private，方法根据需要开放**。

---

## 5.5 this 关键字

`this` 指向**当前对象**本身：

```java
public Person(String name, int age) {
    this.name = name;  // this.name 是字段，name 是参数
    this.age = age;
}
```

用途：
1. **区分字段和参数**（同名时）
2. **this() 调用本类其他构造器**
3. **返回当前对象**（链式调用）：`return this;`

---

## 5.6 static（静态）vs 实例

| | 实例成员 | 静态成员 |
|------|----------|----------|
| 归属 | 属于**对象** | 属于**类** |
| 访问方式 | `obj.method()` | `ClassName.method()` |
| 内存 | 每个对象一份 | 整个类一份（共享） |
| 有 `this` | 有 | 没有 |
| 访问范围 | 可访问静态和实例 | 只能直接访问静态 |

```java
public class Person {
    private static int totalCount = 0;  // 类变量（所有实例共享）

    public Person() {
        totalCount++;  // 每创建一个对象+1
    }

    public static int getTotalCount() {  // 类方法
        return totalCount;
    }
}

// 调用
int count = Person.getTotalCount();  // 通过类名调用
```

---

## 5.7 toString() 与 equals()

这两个方法定义在 `Object` 类（所有类的祖先）中，通常需要**重写**：

```java
@Override
public String toString() {
    return "Person{name='" + name + "', age=" + age + "}";
}

@Override
public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    Person other = (Person) obj;
    return age == other.age && name.equals(other.name);
}
```

- `toString()`：`System.out.println(obj)` 时自动调用
- `equals()`：默认 `==` 比较地址，需要按内容比较时必须重写
- `hashCode()`：如果重写 `equals`，必须同时重写 `hashCode`（集合中会用到）

---

## 5.8 对象创建过程

```
Person p = new Person("张三", 25);

1. 堆中分配内存
2. 字段初始化为默认值（name=null, age=0）
3. 执行构造器中的赋值（name="张三", age=25）
4. 将对象的地址赋给引用变量 p
```

---

## 5.9 内存分析

```
栈                    堆
┌─────────┐     ┌──────────────────┐
│ p ──────┼──→  │ Person对象        │
│ (四个字  │     │ name = "张三"    │
│  节的引  │     │ age = 25        │
│  用)     │     └──────────────────┘
└─────────┘
```
