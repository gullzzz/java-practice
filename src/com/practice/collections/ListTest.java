package com.practice.collections;

import java.util.*;

/**
 * List 练习题（共5题）
 *
 * 运行方式：
 *   javac -encoding UTF-8 -d out src/com/practice/collections/*.java
 *   java -cp out com.practice.collections.ListTest
 */
public class ListTest {
    static List<Integer> number =Arrays.asList(5,3,8,1,9);
    static List<String> languages =new LinkedList<>();
    public static void main(String[] args) {
        System.out.println("========== List 练习题 ==========\n");
        test1();
        test2();
        test3();
        test4();
        test5();
    }

    /*
     * 题1：排序与反转
     * 创建 List<Integer> 存入 5、3、8、1、9
     * ① 排序
     * ② 反转
     * ③ 打印最终结果
     */
    static void test1() {

        Collections.sort(number);
        Collections.reverse(number);
        System.out.println(number);


    }

    /*
     * 题2：插入与删除
     * 创建 List<String> 存储你喜欢的三种编程语言
     * ① 在第二位插入一门新语言
     * ② 删除最后一门语言
     * ③ 打印最终列表
     */
    static void test2() {
        // TODO: 你的代码

        languages.add("Java");
        languages.add("Python");
        languages.add("C++");
        languages.add(1,"go");
        languages.remove(languages.size()-1);
        System.out.println(languages);

    }

    /*
     * 题3：找最大最小值
     * 用循环（不能用 Collections.max/min）找出 List<Integer> 中的最大值和最小值
     * 数据：5, 3, 8, 1, 9
     */
    static void test3() {
        int max= number.get(0);
        int min= number.get(0);
        for (int i = 1; i < number.size(); i++) {
            int n= number.get(i);
            if(max<n){
                max=n;
            }
            if(min>n){
                min=n;
            }

        }
        System.out.println("max:"+max+" min"+min);


    }

    /*
     * 题4：去重（保持原有顺序）
     * 写一个方法，接收 List<String>，去掉里面所有重复的元素，返回去重后的新列表
     * 要求：保持原有出现顺序
     * 示例：["A","B","A","C","B"] → ["A","B","C"]
     */
    static void test4() {


        System.out.println(deduplicate(languages));

    }
    static Set<String> deduplicate(List<String> l1){
        Set<String> s1=new LinkedHashSet<>(l1);
        return s1;

    }

    /*
     * 题5：统计偶数
     * 创建一个包含100个随机数（0~1000）的 ArrayList
     * 统计其中偶数的个数并打印
     */
    static void test5() {

        List<Integer>l1=new ArrayList<>();
        Random r1=new Random();
        int n=0;
        for (int i = 0; i <100 ; i++) {
            int i1 = r1.nextInt(0, 1001);
            l1.add(i1);
            if(i1%2==0){
                n++;
                System.out.println(n);
            }

        }
        System.out.println(n);
    }
}
