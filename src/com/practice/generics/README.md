# 第3.7章：泛型（Generics）

配套代码：`src/com/practice/generics/`

---

## 一、定义

**泛型**：JDK 5 引入的"类型参数化"机制。把数据类型当作参数传递，让同一套代码适配多种类型，同时在**编译期**就能发现类型错误。

```java
// 没有泛型：任何东西都能塞进去，取出来要强转
List list = new ArrayList();
list.add("hello");
list.add(123);           // 编译不报错，运行才炸
String s = (String) list.get(0);  // 必须强转

// 有了泛型：编译期锁死类型
List<String> list = new ArrayList<>();
list.add("hello");
// list.add(123);        // ❌ 编译直接报错
String s = list.get(0);  // 无需强转
```

## 二、核心作用

**解决问题/用途**：泛型解决的核心问题是"类型安全 + 消除强制转型"。没有泛型时，集合是"黑箱"——什么都往里扔，取出来才知道是什么，运行时 `ClassCastException` 防不胜防。泛型让编译器帮你盯住类型，把类型错误从**运行时**提前到**编译时**。

1. **类型安全**：编译期检查，杜绝 `ClassCastException`
2. **代码复用**：一个泛型类/方法适配多种类型
3. **消除强转**：取出时自动就是正确类型，代码更简洁

## 三、语法格式

### 3.1 泛型类

```java
// 定义一个泛型类：<T> 是类型参数占位符
class Box<T> {
    private T item;

    public void set(T item) { this.item = item; }
    public T get() { return item; }
}

// 使用
Box<String> strBox = new Box<>();
strBox.set("魔法卷轴");
String item = strBox.get();  // 无需强转
```

### 3.2 泛型接口

```java
interface Comparator<T> {
    int compare(T o1, T o2);
}

// 实现时指定具体类型
class StringLengthComparator implements Comparator<String> {
    public int compare(String o1, String o2) {
        return o1.length() - o2.length();
    }
}
```

### 3.3 泛型方法

```java
// 方法自己的类型参数，在返回值前声明 <T>
public static <T> T getFirst(List<T> list) {
    return list.isEmpty() ? null : list.get(0);
}
```

### 3.4 类型参数命名约定

| 字母 | 含义 | 典型场景 |
|------|------|----------|
| `E` | Element | 集合元素 |
| `K` | Key | Map的键 |
| `V` | Value | Map的值 |
| `T` | Type | 通用类型 |
| `S, U, V` | 第二、第三类型 | 多参数场景 |

### 3.5 通配符（Wildcard）

**定义**：`?` 代表未知类型。当你不关心具体类型、只关心"是什么类型的子类/父类"时使用。三种形态分别解决"只读"、"只写"、"读写"三种场景。

| 通配符 | 名称 | 读 | 写 | 适用场景 |
|--------|------|:--:|:--:|----------|
| `?` | 无界通配符 | ✅(Object) | ❌ | 不关心类型，只读Object |
| `? extends T` | 上界通配符 | ✅(T) | ❌ | **生产者**：只从中读取T类型数据 |
| `? super T` | 下界通配符 | ✅(Object) | ✅(T) | **消费者**：只往里面写入T类型数据 |

**PECS 法则**（Producer Extends, Consumer Super）：
- 要从容器**读取**数据 → `? extends T`（生产者，提供 T 类型数据）
- 要往容器**写入**数据 → `? super T`（消费者，接收 T 类型数据）
- 既要读又要写 → 不用通配符，直接用 `<T>`

**`<T>` 与 `?` 的选择口诀：**

| 场景 | 选哪个 | 原因 |
|------|:------:|------|
| 单个参数，只读或只写 | `?` | 更灵活，不关心具体类型 |
| 多个参数，需要类型联动 | `<T>` | 把它们的类型"绑"在一起 |

```java
// 单个参数只读 → ? extend 足够
void printBooks(List<? extends Book> books) { ... }

// source和targets的类型必须一致 → 用<T>绑定
<T> void copyBooks(List<? super Book> dest, List<T> src) { ... }
```

```java
// ? extends T：可以读，不能写
void printAnimals(List<? extends Animal> list) {
    for (Animal a : list) {     // ✅ 读出来是Animal
        System.out.println(a);
    }
    // list.add(new Dog());     // ❌ 编译错误！不能写
}

// ? super T：可以写，读出来是Object
void addDogs(List<? super Dog> list) {
    list.add(new Dog());        // ✅ 可以写Dog
    // Dog d = list.get(0);     // ❌ 编译错误！读出来是Object
}
```

### 3.6 泛型边界（Bound）

```java
// T 必须 extends Comparable，才能调用 compareTo
class SortedBox<T extends Comparable<T>> {
    // 现在 T 有了"上限"——必须是 Comparable 的子类型
}

// 多重边界（类必须放前面，接口放后面）
class Data<T extends Number & Serializable> {
    // T 必须同时是 Number 的子类和 Serializable 的实现类
}
```

#### 为什么泛型边界里接口也用 extends 而不是 implements？

Java 类声明中 `extends` 表继承、`implements` 表实现。但泛型边界里统一只用 `extends`——不管后面是类还是接口：

```java
// 正常类声明：extends 类，implements 接口
class Dog extends Animal implements Runnable { }

// 泛型边界：全部用 extends！
<T extends Number>         // Number 是类    → extends
<T extends Comparable>     // Comparable 是接口 → 还是 extends！
<T extends Number & Serializable>  // 类 + 接口 → 全部 extends
```

**原因：** 泛型边界只表达"T 是 XX 的子类型"这一层单一语义。`extends` 在此表示"继承自某个类型体系"，而非字面上的"继承类"。同时，统一用 `extends` 避免了为通配符引入 `? implements X` 这套冗余语法。

#### 多重边界为什么类必须排第一？

```java
✅ class Box<T extends Number & Comparable<T>>   // 类在前
❌ class Box<T extends Comparable<T> & Number>   // 接口在前（假设语法允许）
```

根源在**类型擦除**——编译后 T 被替换为第一个边界类型：

```java
// T extends Number & Comparable → 擦除后 T → Number
class Box { Number val; }   // ✅ val 是 Number，自带 intValue()/doubleValue()

// T extends Comparable & Number → 擦除后 T → Comparable
class Box { Comparable val; }  // ❌ val 是 Comparable，没有 Number 的方法
```

类有具体实现代码，接口只有方法签名。类排第一，擦除后的默认类型自带可用方法，不必处处强转。

#### 非第一边界的方法怎么办？编译器自动强转

```java
class SortedBox<T extends Number & Comparable<T>> {
    T max(T a, T b) {
        return a.compareTo(b) > 0 ? a : b;  // compareTo 来自第二边界 Comparable
    }
}
```

`Number` 本身没有 `compareTo` 方法，但编译器知道 T 也实现了 `Comparable`——编译后自动插入强转：

```java
// 编译后等价于：
return ((Comparable) a).compareTo(b) > 0 ? a : b;
```

**结论：第一个边界决定擦除后"T 的身份"，后续边界的方法不会丢失——编译器靠强转兜底。代价是字节码里多一条 `checkcast` 指令，性能影响可忽略。**

## 四、类型擦除（Type Erasure）

Java 泛型通过**编译期擦除**实现——编译后泛型类型信息会被移除，替换为原始类型（上界或 Object）。

```java
// 你写的
List<String> list1 = new ArrayList<>();
List<Integer> list2 = new ArrayList<>();

// 编译后（字节码层面）
List list1 = new ArrayList();  // String 被擦除
List list2 = new ArrayList();  // Integer 被擦除

// 所以：
System.out.println(list1.getClass() == list2.getClass());  // true！
```

**四个因擦除导致的限制：**
1. 不能 `new T()` ——运行时不知道 T 是啥
2. 不能 `instanceof T` ——运行时类型信息已擦除
3. 不能 `new T[]` ——数组需要具体类型
4. 不能定义泛型异常类 ——异常在运行时被检查

## 五、易错注意点

1. **基本类型不能作泛型参数**：不能写 `List<int>`，必须 `List<Integer>`
2. **List\<Dog\> 不是 List\<Animal\> 的子类**：泛型没有继承关系
3. **静态成员不能使用类的类型参数**：类加载时 T 还是未知占位符，JVM 无法确定内存大小；类型参数在 `new Box<String>()` 时才确定具体类型。而且 static 成员全类只有一份，T 在不同实例间却可以不同——两者逻辑矛盾：

```java
class Box<T> {
    T instanceField;        // ✅ 每个实例各有一份，各自的 T 明确
    // static T staticField;  // ❌ 只有一份，strBox 的 T=String vs intBox 的 T=Integer，该听谁的？
}
```
4. **不能重载泛型方法（擦除后签名相同）**：编译后 `void method(List<String>)` 和 `void method(List<Integer>)` 相同

```java
// 易错点2：泛型没有协变
List<Dog> dogs = new ArrayList<>();
// List<Animal> animals = dogs;  // ❌ 编译错误！
// 正确做法：用上界通配符
List<? extends Animal> animals = dogs;  // ✅
```

---

## 面试官视角

| 常见问法 | 考察点 |
|----------|--------|
| "泛型的作用是什么？" | 类型安全 + 消除强转 + 编译期检查 |
| "Java泛型和C++模板的区别？" | Java是类型擦除，C++是真泛型（代码膨胀） |
| "什么是类型擦除？有什么影响？" | 编译后擦除为上界类型，4个限制 |
| "`? extends` 和 `? super` 的区别？" | PECS法则：生产者extends，消费者super |
| "List\<Object\> 和 List\<?\> 的区别？" | List\<Object\> 可插任意对象，List\<?\> 只能插null（用了无界通配符后具体类型未知） |
| "为什么不能写 `new T()`？" | 泛型在运行时被擦除，JVM不知道T的具体类型 |
| "桥接方法是什么？" | 编译器为保持多态自动生成的桥接方法（擦除导致的产物） |
