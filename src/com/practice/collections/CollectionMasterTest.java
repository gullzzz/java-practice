package com.practice.collections;

import java.util.*;

/**
 * 【集合终极试炼】— 遍历 & Collections 工具类
 *
 * 用今天学的遍历方式和Collections工具方法，改写你 ListTest 中的旧实现。
 *
 * 运行方式：
 *   javac -encoding UTF-8 -d out src/com/practice/collections/*.java
 *   java -cp out com.practice.collections.CollectionMasterTest
 */
public class CollectionMasterTest {

    // 复用 ListTest 的数据
    static List<Integer> numbers = new ArrayList<>(Arrays.asList(5, 3, 8, 1, 9));
    static List<String> languages = new ArrayList<>(Arrays.asList("Java", "Python", "C++", "Java"));

    public static void main(String[] args) {
        System.out.println("========== 集合终极试炼 ==========\n");
        test1();
        test2();
        test3();
        test4();
    }

    /*
     * 题1：用 Collections 工具类找最大最小值
     * 之前你用手写循环找 max/min。现在用 Collections 一行搞定。
     * 数据：numbers = [5, 3, 8, 1, 9]
     */
    static void test1() {
        // TODO：用 Collections.max 和 Collections.min
        int max=Collections.max(numbers);
        int min=Collections.min(numbers);
        System.out.println("max :"+max+" min:"+min);

    }

    /*
     * 题2：用 Iterator 安全删除
     * 遍历 languages，删除所有包含字母'a'的语言，打印删除后的列表
     * 提示：String.contains() 判断是否含某字符
     * 数据：["Java", "Python", "C++", "Java"]
     * 期望结果：[C++]
     */
    static void test2() {
        // TODO：创建 Iterator，遍历中 it.remove()
        Iterator<String> iterator=languages.iterator();
        while(iterator.hasNext()){
            if(iterator.next().contains("a")){
                iterator.remove();
            }
        }
        System.out.println(languages);
    }


    /*
     * 题3：用 forEach + Lambda 统计偶数
     * 生成20个 0~100 的随机数，用 list.forEach() 统计偶数个数
     * 提示：Lambda 里只能访问 effectively final 的变量，用 int[] count = {0} 当计数器
     */
    static boolean isEven(int n){

        return (n % 2 == 0);
    }
    static void test3() {
        // TODO
        Random r1=new Random();
        List<Integer>list=new ArrayList<>();
        for (int i = 0; i <20 ; i++) {
            int n= r1.nextInt(0,101);
            list.add(n);
        }
        int [] count={0};


        list.forEach(n -> {
            if (isEven(n)) count[0]++;
        });

    }

    /*
     * 题4：用 frequency 统计频率
     * 统计数据 ["A","B","A","C","A","B"] 中 "A" 出现了几次
     */
    static void test4() {
        // TODO：用 Collections.frequency
        String str="A";
        List<String>list=Arrays.asList("A","B","A","C","A","B");
        int fre=Collections.frequency(list,"A");


    }
}
