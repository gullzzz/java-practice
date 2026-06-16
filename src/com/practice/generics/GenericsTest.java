package com.practice.generics;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 泛型 练习题（共4题）
 *
 * 运行方式：
 *   javac -encoding UTF-8 -d out src/com/practice/generics/*.java
 *   java -cp out com.practice.generics.GenericsTest
 */
public class GenericsTest {
    public static void main(String[] args) {
        System.out.println("========== 泛型 练习题 ==========\n");
        test1();
        test2();
        test3();
        test4();
    }

    /*
     * ==================================================================
     * 题1：泛型类基础 —— MagicBox<T> 的类型安全
     *
     * 任务：
     *   ① 创建 MagicBox<String> 和 MagicBox<Integer> 各一个
     *   ② 往 String 箱子里放 "治愈药水"，往 Integer 箱子里放 42
     *   ③ 取出并打印两个箱子
     *   ④ 尝试把 Integer 放进 String 箱子 —— 观察编译器的反应（注释掉即可）
     *
     * 思考：如果 MagicBox 不用泛型，里面存 Object，还能编译期发现放错类型吗？
     * ==================================================================
     */
    static void test1() {
        MagicBox <String> potionBoc= new MagicBox<String>("String");
        MagicBox <Integer> coinBox=new MagicBox<>("Integer");
        // TODO ①：创建两个不同类型的 MagicBox
        // MagicBox<String> potionBox = ...
        // MagicBox<Integer> coinBox = ...

        // TODO ②：放入物品
        potionBoc.put("药水1");
        potionBoc.put("药水2");
        potionBoc.put("药水3");
        System.out.println(potionBoc.get());
        coinBox.put(1);
        coinBox.put(2);
        coinBox.put(3);
        coinBox.put(4);
        System.out.println(coinBox.get());

        // TODO ③：取出并打印


        System.out.println("题1 通过 ✅\n");
    }

    /*
     * ==================================================================
     * 题2：泛型方法 —— 交易所匹配引擎
     *
     * 任务：
     *   ① 实现下面的 swapContent 方法：交换两个同类型 MagicBox 的内容
     *   ② 实现下面的 matchTwoOffers 方法：
     *      检查两笔 TradeOffer 是否恰好匹配——A出的类型等于B要的类型，且B出的类型等于A要的类型
     *      提示：TradeOffer 的 getTargetType() 返回 Class<T>，Class 有 isInstance() 方法
     *   ③ 在 main 逻辑里创建两笔匹配的交易（比如药水换武器 vs 武器换药水），验证匹配逻辑
     *
     * 提示：matchTwoOffers 需要什么类型参数？两个要约的出/求类型都不一定相同
     * ==================================================================
     */

    // TODO ②-1：实现交换方法
    static <T> void swapContent(MagicBox<T> a, MagicBox<T> b) {
        // TODO: 交换两个箱子的内容
        T n= a.get();
        a.put(b.get());
        b.put(n);
    }

    // TODO ②-2：实现匹配检查
    // F1=第一个要约出的类型, T1=第一个要约要的类型
    // F2=第二个要约出的类型, T2=第二个要约要的类型
    // 两者匹配的条件：F1 == T2 且 F2 == T1（即：你出的恰好是我要的，我出的恰好是你要的）
    static <F1, T1, F2, T2> boolean matchTwoOffers(TradeOffer<F1, T1> a, TradeOffer<F2, T2> b) {
        // TODO: 实现匹配逻辑

         return a.getTargetType().equals(b.getOffering().getClass())&&b.getTargetType().equals(a.getOffering().getClass());


        // 提示：用 getTargetType() 的 isInstance() 方法判断类型是否兼容
        // 或者直接比较 Class 对象：a.getTargetType().equals(b.getOffering().getClass())

    }

    static void test2() {
        // TODO ②-3：创建两笔交易要约并测试
        TradeOffer<String,String> offer1=new TradeOffer<>("offrer1","冰霜法杖",String.class);
        TradeOffer<String,String> offer2=new TradeOffer<>("offrer2","火球术卷轴",String.class);
        boolean b = matchTwoOffers(offer1, offer2);
        System.out.println(b);



        // 示例思路：
        // - offer1: 法师用 "火球术卷轴"(String) 换 "冰霜法杖"(String)
        // - offer2: 术士用 "冰霜法杖"(String) 换 "火球术卷轴"(String)
        // - 调用 matchTwoOffers 验证它们是否匹配


        System.out.println("题2 通过 ✅\n");
    }

    /*
     * ==================================================================
     * 题3：通配符 —— PECS 法则实战
     *
     * 编程界有一条铁律：Producer Extends, Consumer Super
     *
     * 任务：
     *   ① 实现 printAllBoxes —— 接受任意 MagicBox 的 List，打印它们的内容
     *      （你是数据消费者还是生产者？该用 extends 还是 super？）
     *
     *   ② 实现 fillBoxesFrom —— 接受一个 List<MagicBox<T>> 和一个 MagicBox<T> 源，
     *      把源箱子的内容复制到 List 中所有空箱子里
     *      （= 号在泛型中是赋值动作，你该用哪种通配符？writ）
     *
     *   ③ 在 main 中创建几个箱子测试
     * ==================================================================
     */

    // TODO ③-1：只能读 → 该用哪种通配符？
    static void printAllBoxes(List<? extends MagicBox<?> > boxes) {
        // TODO: 遍历并打印每个箱子
        for(MagicBox box :boxes){
            System.out.println(box.getLabel());
            System.out.println(box.toString());
        }

    }

    // TODO ③-2：只能写 → 该用哪种通配符？
    // 提示：T 和通配符是不同的概念。这个方法该不该声明 <T>？
    static <T> void fillBoxesFrom(List<?super MagicBox<T> > targets, MagicBox<T> source) {
        // TODO: 遍历 targets，对每个空箱子放进 source 的内容
        for(Object n:targets){

            MagicBox<T> box=(MagicBox<T>)n;
            if(box.isEmpty()) box.put(source.get());

        }
    }

    static void test3() {
        // TODO ③-3：创建测试数据
        MagicBox<Integer> box1 = new MagicBox<>("整数箱");
        MagicBox<Integer> box2 = new MagicBox<>("备用整数箱");
        MagicBox<Integer> source = new MagicBox<>("源头箱");
        source.put(41);
        List<MagicBox<Integer>> boxes = List.of(box1, box2);
        fillBoxesFrom(boxes, source);   // 把42复制到box1和box2
        printAllBoxes(boxes);


        System.out.println("题3 通过 ✅\n");
    }

    /*
     * ==================================================================
     * 题4：泛型边界 —— <T extends Comparable<T>>
     *
     * 魔法物品有的更稀有（价值更高）。你需要一个能比较物品的 SmartBox。
     *
     * 任务：
     *   ① 创建一个 SmartBox<T extends Comparable<T>> 类（可以写在同文件或新建文件）
     *      它继承 MagicBox<T>，新增方法 findMax(T... items)：从多个同类物品中找出最大值
     *      提示：用 compareTo 逐个比较
     *
     *   ② 用 String 类型测试（String 实现了 Comparable）：
     *      SmartBox<String> box = new SmartBox<>("卷轴箱");
     *      调用 box.findMax("火球术", "冰霜新星", "奥术飞弹") —— 按字母序最大的是哪个？
     *
     *   ③ （思考题）如果尝试 SmartBox<Object>，编译器会说什么？为什么？
     * ==================================================================
     */
    static void test4() {
        // TODO ④-1：创建 SmartBox 类（在下方或新文件）
        SmartBox<String> box = new SmartBox<String>("卷轴箱");
        String max = box.findMax("火球术", "冰霜新星", "奥术飞弹",null);
        System.out.println(max);

        // TODO ④-2：用 String 类型测试 findMax
        // TODO ④-3：尝试 new SmartBox<Object>() —— 观察编译错误，注释掉



        System.out.println("题4 通过 ✅\n");
    }
}
class SmartBox<T extends Comparable<T>> extends MagicBox<T> {
    public SmartBox(String label) {
        super(label);
    }
    @SafeVarargs
    public final T findMax(T... items){
        T max=items[0];
        for (int i = 1; i < items.length; i++) {

            try {
                if(items[i].compareTo(max)>0){
                    max=items[i];
                }
            } catch (NullPointerException e) {
                System.out.println("数据不能为空");;
            }
        }


      return max;
    }
    // TODO: 构造方法（调用 super）

    // TODO: findMax 方法
}
/*
 * ================================================================
 * 脚手架提示：题4需要的 SmartBox 类，写在这里即可。
 *
 * 几个关键点：
 * - extends MagicBox<T> 继承箱子功能
 * - <T extends Comparable<T>> 约束 T 必须可比较
 * - findMax 用第一个参数初始化 max，然后逐个 compareTo 比较
 * - 注意 @SafeVarargs 注解（压制泛型可变参数警告）
 * ================================================================
*/

