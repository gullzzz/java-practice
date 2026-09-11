# 反射机制（Reflection）：Class / Method / Field / Constructor

## 零、为什么需要反射（是什么 + 为什么学）

### 正常写代码 vs 反射

正常写代码，一切在**编译期**就定死了：

```java
Hero hero = new Hero("法师", 5);   // 编译期就知道 Hero 这个类、这个构造器
hero.name = "大法师";              // 编译期就知道 name 这个字段，拼错会直接报红
```

**反射则相反：编译期只知道一个"字符串"，运行时才去 JVM 里把类翻出来操作。** 你手上只有 `"Hero"` 三个字（比如它来自配置文件、注解、方法参数），要到程序跑起来的那一刻，才知道这背后是一个类，才能去拿它的构造器、字段、方法。

```java
String className = "Hero";              // 编译期：这只是一个字符串，没有任何类型信息
Class<?> clazz = Class.forName(className);   // 运行时：去 JVM 里查"有没有这个类"
```

### 为什么框架离不开它

| 框架 | 它编译期不知道什么 | 靠反射在运行时补上 |
|------|-------------------|-------------------|
| **Spring IoC** | 配置文件里写的 `com.xxx.UserService` | `forName` 加载类 → 构造器 `newInstance` 创建 → 字段 `set` 注入依赖 |
| **MyBatis** | 你只写了一个 Mapper 接口，没有实现类 | 运行时生成代理实现，反射调方法 |
| **Jackson / Gson** | 拿到的是任意对象 | 反射遍历字段，拼成 JSON |

> **一句话：反射把"编译期才知道的事"推迟到"运行时才知道"。** 框架要足够通用，就必须"不认识你的类也能操作你的类"——反射就是这副"不认识也能操作"的手。

---

## 关联类清单（自查遗漏）

| 类 | 归属 | 说明 |
|----|------|------|
| `Class<T>` | 类 | 反射的**入口**，代表一个类的"说明书" |
| `Field` | 类 | 字段对象（成员变量） |
| `Method` | 类 | 方法对象 |
| `Constructor<T>` | 类 | 构造器对象 |
| `Modifier` | 类 | 判断修饰符（public/private/static/final） |
| （进阶）`Proxy` + `InvocationHandler` | 类 / 接口 | 动态代理，Spring AOP 的基石，后续章节展开 |

> 记忆锚点：`Class` 是"整本书的说明书"，`Field`/`Method`/`Constructor` 是书里"某一页的条目"，`Modifier` 是"条目上的标签"。

---

## 怎么拿到这些对象（获取格式总览）⭐

> 拿取的顺序永远一条：**先拿 `Class`（入口）→ 再从 `Class` 拿 Field / Method / Constructor（零件）。**
> 拿零件时靠两个东西"指认"：**字符串名字**（字段名 / 方法名），或**参数类型的 `.class`**（构造器）。

下面用一个贯穿例子，把每一类对象"怎么拿"的格式一次看全。假设有个类：

```java
class Student {
    private String name;                 // 私有字段
    private int age;                     // 私有字段
    public String school = "魔法学院";    // public 字段

    public Student() {}                               // 无参构造
    private Student(String name, int age) { }         // 私有有参构造

    public void show() { }                            // public 方法
    private void study() { }                          // 私有方法
}
```

### ① 拿 Class 对象（入口，三种写法）

```java
Class<?> c1 = Student.class;                          // 类名.class
Class<?> c2 = new Student().getClass();               // 对象.getClass()
Class<?> c3 = Class.forName("com.practice.xxx.Student");  // 字符串（必须写全限定名）
```

### ② 拿 Field 对象 —— 括号里传「字段名的字符串」

```java
Field nameField = c3.getDeclaredField("name");   // 拿"name"这个字段
Field schoolField = c3.getField("school");       // 拿 public 字段（用 getField）
Field[] all = c3.getDeclaredFields();            // 拿本类所有字段
```

> 格式：`clazz.getDeclaredField("字段名")` → 返回一个 `Field` 对象。括号里是**字段名的字符串**，不是字段本身。

### ③ 拿 Method 对象 —— 括号里传「方法名的字符串」（带参再加参数类型.class）

```java
Method m1 = c3.getDeclaredMethod("study");                  // 无参方法：只传方法名
Method m2 = c3.getDeclaredMethod("setName", String.class);  // 带参方法：方法名 + 参数类型.class
Method[] ms = c3.getDeclaredMethods();                      // 所有方法
```

> 格式：`clazz.getDeclaredMethod("方法名", 参数类型.class, ...)` → 返回 `Method` 对象。
> 无参方法括号里只有 `"方法名"`；带参方法在 `"方法名"` 后面**按顺序**跟每个参数的 `.class`。

### ④ 拿 Constructor 对象 —— 括号里传「参数类型的 .class」（没有名字）

```java
Constructor<?> c1 = c3.getConstructor();                            // 无参构造：空括号
Constructor<?> c2 = c3.getDeclaredConstructor(String.class, int.class);  // 有参构造：参数类型列表
```

> 格式：`clazz.getDeclaredConstructor(参数类型.class, ...)` → 返回 `Constructor` 对象。
> 括号里**只有参数类型的 `.class`**，没有名字——因为构造器天生没名字，只能靠参数类型认。

### ⑤ 一条龙串起来（拿 → 用）

```java
Class<?> clazz = Class.forName("com.practice.xxx.Student");   // ① 拿 Class
Constructor<?> ctor = clazz.getDeclaredConstructor(String.class, int.class);  // ② 拿构造器
ctor.setAccessible(true);                                      // ③ 开锁（private）
Student stu = (Student) ctor.newInstance("小明", 18);          // ④ 造对象

Field f = clazz.getDeclaredField("name");                      // ⑤ 拿字段
f.setAccessible(true);
f.set(stu, "小红");                                             // ⑥ 改字段

Method m = clazz.getDeclaredMethod("study");                   // ⑦ 拿方法
m.setAccessible(true);
m.invoke(stu);                                                  // ⑧ 调方法
```

> **核心口诀：** `getDeclaredField` / `getDeclaredMethod` 括号里填**字符串名字**；`getDeclaredConstructor` 括号里填**参数类型的 `.class`**（构造器没名字）。凡是要碰 private 的，拿到后先 `setAccessible(true)` 开锁。

---

## 一、Class：反射的入口 ⭐

### 是什么

每个类被 JVM 加载后，内存里都会生成**唯一一个** `Class` 对象，装着这个类的完整说明书（有哪些字段、方法、构造器、注解）。反射的第一步永远是拿到这个 `Class` 对象。

> **关键认知：`Class` 对象是一个类的"元信息"，不是那个类的实例。** `Hero.class` 是"Hero 的说明书"，`new Hero()` 才是"一个 Hero 实例"。就像"菜谱"和"菜"的区别——菜谱（Class）只有一份，照着菜谱能炒出无数盘菜（实例）。

### 拿 Class 对象的三种方式 ⭐

| 方式 | 写法 | 前提 | 使用场景 |
|------|------|------|---------|
| 类名 `.class` | `Hero.class` | 编译期就知道类名 | 写死的代码里用 |
| 对象 `.getClass()` | `hero.getClass()` | 已经有一个对象 | 拿已知对象的运行时类型 |
| ⭐ `Class.forName("全限定名")` | `Class.forName("com.xxx.Hero")` | 只有一个字符串 | **框架最常用**，类名来自配置文件/注解 |

> **`forName` 为什么是框架最爱？** 因为框架编译时根本不知道你会写什么类，它手里只有一个字符串（配置文件里读出来的、注解里标着的）。它只能拿字符串去 JVM 里"要人"。

### 方法速查表

| 方法签名 | 参数 | 返回值 | 说明 | 注意事项 |
|----------|------|--------|------|----------|
| ⭐ `static Class<?> forName(String className)` | `className` — 全限定类名 | `Class<?>` | 靠字符串加载类 | 找不到抛 `ClassNotFoundException`（Checked） |
| ⭐ `Method getMethod(String name, Class<?>... paramTypes)` | `name` — 方法名；`paramTypes` — 参数类型 | `Method` | 拿 **public** 方法（含继承的） | 找不到抛 `NoSuchMethodException` |
| ⭐ `Method getDeclaredMethod(String name, Class<?>... paramTypes)` | 同上 | `Method` | 拿**本类声明**的方法（含 private） | **不含继承来的** |
| `Method[] getMethods()` | 无 | `Method[]` | 拿所有 public 方法（含继承） | |
| `Method[] getDeclaredMethods()` | 无 | `Method[]` | 拿本类声明的所有方法（含 private） | |
| `Field getField(String name)` | `name` — 字段名 | `Field` | 拿 **public** 字段 | 不含 private |
| `Field getDeclaredField(String name)` | `name` — 字段名 | `Field` | 拿**本类声明**的字段（含 private） | 不含继承来的 |
| `Constructor<?> getConstructor(Class<?>... paramTypes)` | `paramTypes` — 参数类型 | `Constructor` | 拿 **public** 构造器 | |
| `Constructor<?> getDeclaredConstructor(Class<?>... paramTypes)` | 同上 | `Constructor` | 拿本类声明的构造器（含 private） | |
| `Class<? super T> getSuperclass()` | 无 | `Class` | 拿父类的 Class | 没有父类（Object）返回 null |
| `Class<?>[] getInterfaces()` | 无 | `Class[]` | 拿实现的接口 | |
| `String getName()` / `getSimpleName()` | 无 | `String` | 全限定名 / 简单类名 | |
| `boolean isAnnotationPresent(Class<? extends Annotation>)` | 注解的 Class | `boolean` | 判断有没有某个注解 | 框架扫描注解全靠它 |

> **`getXxx` vs `getDeclaredXxx` 的唯一区别：** 带 `Declared` 的只认"本类自己声明的"（含 private），不带 `Declared` 的只认"public 的"（含从父类继承的）。记一句：**Declared = 私房货全给你，public 版 = 只给公开的。**

---

## 二、Field：操作字段 ⭐

### 是什么

`Field` 代表类里的一个成员变量。拿到它之后，你可以**读它的值、改它的值**——包括 private 的。

### 方法速查表

| 方法签名 | 参数 | 返回值 | 说明 | 注意事项 |
|----------|------|--------|------|----------|
| ⭐ `Object get(Object obj)` | `obj` — 从哪个对象身上读 | `Object` | 读字段的值 | private 字段需先 `setAccessible(true)` |
| ⭐ `void set(Object obj, Object value)` | `obj` — 往哪个对象写；`value` — 新值 | `void` | 写字段的值 | 同上 |
| `String getName()` | 无 | `String` | 字段名 | |
| `Class<?> getType()` | 无 | `Class<?>` | 字段的类型 | |
| `int getModifiers()` | 无 | `int` | 修饰符的编码 | 配合 `Modifier.isPrivate(mod)` 等判断 |
| `void setAccessible(boolean flag)` | `flag` — `true` 表示放开检查 | `void` | 突破 private 检查 | 反射的"万能钥匙"，见 §五 |

---

## 三、Method：调用方法 ⭐

### 是什么

`Method` 代表类里的一个方法。拿到它之后，你可以**在任何对象身上调用这个方法**。

### 方法速查表

| 方法签名 | 参数 | 返回值 | 说明 | 注意事项 |
|----------|------|--------|------|----------|
| ⭐ `Object invoke(Object obj, Object... args)` | `obj` — 在哪个对象上调；`args` — 传给方法的实参 | `Object` | 真正执行那个方法 | private 方法需 `setAccessible(true)`；无返回值时为 `null` |
| `String getName()` | 无 | `String` | 方法名 | |
| `Class<?> getReturnType()` | 无 | `Class<?>` | 方法的返回类型 | |
| `Class<?>[] getParameterTypes()` | 无 | `Class<?>[]` | 参数类型数组 | |
| `void setAccessible(boolean flag)` | `flag` | `void` | 突破 private 检查 | |

---

## 四、Constructor：创建对象 ⭐

### 是什么

`Constructor` 代表类里的一个构造器。拿到它之后，你可以**用它 `new` 出实例**——在只知道字符串类名的前提下。

### 方法速查表

| 方法签名 | 参数 | 返回值 | 说明 | 注意事项 |
|----------|------|--------|------|----------|
| ⭐ `T newInstance(Object... args)` | `args` — 传给构造器的实参 | `T` | 创建实例 | 替代过时的 `Class.newInstance()`（它只能调无参构造） |
| `Class<?>[] getParameterTypes()` | 无 | `Class<?>[]` | 构造器的参数类型 | |

> **⚠️ 过时 API 提醒：** 老的 `Class.newInstance()` 已过时（只能调无参构造、异常处理糟糕）。创建实例一律用 `Constructor.newInstance(args)`。

---

## 五、setAccessible：打破 private 的万能钥匙 ⭐

前面反复出现的 `setAccessible(true)`，是反射最"越界"的一招：

- **默认情况下**，JVM 的访问检查会拦住你对 private 字段/方法/构造器的访问，直接抛 `IllegalAccessException`。
- 调了 `field.setAccessible(true)` 之后，**这道检查就被关掉了**——private 形同虚设。

```java
Field nameField = clazz.getDeclaredField("name");   // private 字段
nameField.setAccessible(true);                       // ⭐ 关掉访问检查
nameField.set(hero, "大法师");                       // 现在能改了
```

> **为什么要有这道检查？** 封装（把字段藏起来、只留方法入口）是 OOP 的护城河。反射用 `setAccessible` 把护城河填平了——所以它是"破坏封装"的元凶，也是框架"无所不能"的原因。这是一体两面。

---

## 六、反射的代价（三个坑，面试必问）

| 坑 | 表现 | 说明 |
|----|------|------|
| **性能差** | 反射调用比直接调用慢一个数量级 | 每次都要做类型检查、方法解析，还无法被 JIT 优化 |
| **破坏封装** | `setAccessible(true)` 越过 private | OOP 的护城河被填平 |
| **编译期检查失效** | 字符串写错类名，编译不报错 | 直接调 `new` 写错类名编译就报红；`forName("Her0")` 编译通过、运行才炸 |

> **所以框架怎么平衡？** 框架只在"启动时、低频"的地方用反射（加载配置、生成 Bean），**热点路径**（每次请求都走的路）要么缓存反射结果、要么干脆生成字节码/直接调用，避免反复反射。

---

## 七、完整 Demo：反射操作一个私有英雄

```java
import java.lang.reflect.*;

class Hero {
    private String name;
    private int level;

    public Hero() {}
    public Hero(String name, int level) { this.name = name; this.level = level; }

    private void levelUp() {
        level++;
        System.out.println(name + " 升级到 " + level);
    }
}

public class ReflectionDemo {
    public static void main(String[] args) throws Exception {
        // ① 拿 Class —— 三种方式
        Class<?> c1 = Hero.class;                       // 类名.class
        Class<?> c2 = new Hero("战士", 1).getClass();   // 对象.getClass()
        Class<?> c3 = Class.forName("Hero");            // forName（框架最常用）

        // ② 拿构造器 → 创建实例
        Constructor<?> ctor = c3.getConstructor(String.class, int.class);
        Hero hero = (Hero) ctor.newInstance("法师", 5);

        // ③ 拿私有字段 → 改值
        Field nameField = c3.getDeclaredField("name");
        nameField.setAccessible(true);
        nameField.set(hero, "大法师");

        // ④ 拿私有方法 → 调用
        Method levelUp = c3.getDeclaredMethod("levelUp");
        levelUp.setAccessible(true);
        levelUp.invoke(hero);                            // 输出：大法师 升级到 6
    }
}
```

> **这条链就是 Spring 干活的缩影：** `forName` 拿到类 → `getConstructor` + `newInstance` 创建对象 → `getDeclaredField` + `set` 注入依赖 → `invoke` 调用方法。

---

## 面试官视角

> **Q1：什么是反射？**  
> 反射是 Java 在**运行时**动态获取类的信息（字段、方法、构造器、注解）并操作对象的一种机制。核心入口是 `Class` 对象，配合 `Field`/`Method`/`Constructor` 完成"读字段、调方法、建对象"。

> **Q2：获取 Class 对象的三种方式？**  
> ① `类名.class`（编译期已知）② `对象.getClass()`（已有对象）③ `Class.forName("全限定名")`（只有字符串，框架最常用）。三种拿到的是**同一个** Class 对象（JVM 里一个类只有一份元信息）。

> **Q3：getMethod 和 getDeclaredMethod 的区别？**  
> `getMethod` 只拿 **public** 方法（含从父类继承的）；`getDeclaredMethod` 拿**本类声明的所有**方法（含 private），但不含继承来的。字段、构造器同理（`getField`/`getDeclaredField` 等）。

> **Q4：反射能做什么？举框架例子？**  
> ① 运行时创建对象（`Constructor.newInstance`）② 读写字段（`Field.get/set`）③ 调用方法（`Method.invoke`）④ 读注解（`isAnnotationPresent`）。Spring IoC 靠反射加载 Bean 并注入依赖，MyBatis 靠反射给 Mapper 接口生成实现，Jackson 靠反射把对象序列化成 JSON。

> **Q5：反射的缺点？**  
> ① 性能差（每次要做检查解析，无法 JIT 优化）② 破坏封装（`setAccessible(true)` 越过 private）③ 编译期检查失效（字符串写错类名编译不报错、运行才炸）。

> **Q6：setAccessible 有什么用？**  
> 关掉 JVM 的访问检查，让反射能访问 private 字段/方法/构造器。默认访问 private 会抛 `IllegalAccessException`，`setAccessible(true)` 之后 private 形同虚设——这是框架"无所不能"的原因，也是它"破坏封装"的代价。

> **Q7：Class.forName 和 new 有什么区别？**  
> `new` 是编译期就知道类型、直接创建对象；`Class.forName` 是运行时靠字符串加载类（返回 Class 对象），再通过构造器 `newInstance` 创建对象。`forName` 是反射的入口，牺牲性能换来了"不认识也能操作"的通用性。
