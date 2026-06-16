package com.practice.collections;

import java.util.*;

/**
 * 阶段8：集合框架
 * 涵盖：List(ArrayList/LinkedList)、Set(HashSet/TreeSet)、Map(HashMap/TreeMap)、遍历方式
 */
public class CollectionsDemo {
    public static void main(String[] args) {
        System.out.println("========== Java 集合框架 ==========\n");

        listDemo();
        setDemo();
        mapDemo();
        iteratorsDemo();
        collectionsUtil();
    }

    static void listDemo() {
        System.out.println("--- 1. List（有序，可重复） ---");

        // ArrayList：底层数组，查快改快
        List<String> list = new ArrayList<>();
        list.add("Java");
        list.add("Python");
        list.add("JavaScript");
        list.add("Java");  // 可以重复

        System.out.println("ArrayList: " + list);
        System.out.println("size: " + list.size());
        System.out.println("get(1): " + list.get(1));
        System.out.println("indexOf('Java'): " + list.indexOf("Java"));
        System.out.println("lastIndexOf('Java'): " + list.lastIndexOf("Java"));

        list.remove("Python");
        System.out.println("remove后: " + list);

        // LinkedList：底层链表，增删快
        List<Integer> linkedList = new LinkedList<>();
        linkedList.add(10);
        linkedList.add(20);
        linkedList.add(1, 15);  // 在位置1插入
        System.out.println("LinkedList: " + linkedList);

        // 排序
        List<Integer> numbers = new ArrayList<>(Arrays.asList(5, 2, 8, 1, 9));
        Collections.sort(numbers);
        System.out.println("排序后: " + numbers);

        System.out.println();
    }

    static void setDemo() {
        System.out.println("--- 2. Set（无序，不可重复） ---");

        // HashSet：基于哈希表，无序，不允许重复
        Set<String> hashSet = new HashSet<>();
        hashSet.add("apple");
        hashSet.add("banana");
        hashSet.add("apple");  // 重复，不会被加入
        hashSet.add("cherry");

        System.out.println("HashSet: " + hashSet);
        System.out.println("包含'apple': " + hashSet.contains("apple"));

        // TreeSet：基于红黑树，自动排序
        Set<Integer> treeSet = new TreeSet<>(Arrays.asList(5, 2, 8, 1, 9, 2));
        System.out.println("TreeSet（自动排序）: " + treeSet);

        // LinkedHashSet：保持插入顺序
        Set<String> linkedSet = new LinkedHashSet<>();
        linkedSet.add("first");
        linkedSet.add("second");
        linkedSet.add("third");
        System.out.println("LinkedHashSet（保持插入顺序）: " + linkedSet);

        System.out.println();
    }

    static void mapDemo() {
        System.out.println("--- 3. Map（键值对） ---");

        // HashMap：基于哈希表，无序
        Map<String, Integer> map = new HashMap<>();
        map.put("张三", 85);
        map.put("李四", 92);
        map.put("王五", 78);
        map.put("张三", 90);  // 覆盖

        System.out.println("HashMap: " + map);
        System.out.println("张三的成绩: " + map.get("张三"));
        System.out.println("包含key'李四': " + map.containsKey("李四"));
        System.out.println("包含value 100: " + map.containsValue(100));

        // 获取不存在的key
        System.out.println("赵六的成绩: " + map.get("赵六"));  // null

        // getOrDefault
        System.out.println("赵六的成绩(默认0): " + map.getOrDefault("赵六", 0));

        // 遍历 Map
        System.out.print("遍历: ");
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            System.out.print(entry.getKey() + "=" + entry.getValue() + " ");
        }
        System.out.println();

        // TreeMap：按键排序
        Map<String, Integer> treeMap = new TreeMap<>();
        treeMap.putAll(map);
        System.out.println("TreeMap（按键排序）: " + treeMap);

        System.out.println();
    }

    static void iteratorsDemo() {
        System.out.println("--- 4. 遍历集合的方式 ---");

        List<String> list = Arrays.asList("A", "B", "C", "D");

        // 方式1：增强 for
        System.out.print("for-each: ");
        for (String s : list) {
            System.out.print(s + " ");
        }
        System.out.println();

        // 方式2：Iterator（可以在遍历中安全删除）
        List<String> mutable = new ArrayList<>(list);
        System.out.print("Iterator: ");
        Iterator<String> it = mutable.iterator();
        while (it.hasNext()) {
            String s = it.next();
            System.out.print(s + " ");
            if (s.equals("B")) {
                it.remove();  // 安全删除
            }
        }
        System.out.println();
        System.out.println("删除后: " + mutable);

        // 方式3：forEach + Lambda（Java 8+）
        System.out.print("forEach Lambda: ");
        list.forEach(s -> System.out.print(s + " "));
        System.out.println();

        // 方式4：for + 索引（仅 List）
        System.out.print("for索引: ");
        for (int i = 0; i < list.size(); i++) {
            System.out.print(list.get(i) + " ");
        }
        System.out.println("\n");
    }

    static void collectionsUtil() {
        System.out.println("--- 5. Collections 工具类 ---");

        List<Integer> list = new ArrayList<>(Arrays.asList(3, 1, 4, 1, 5, 9, 2, 6));

        System.out.println("原始: " + list);

        // 排序
        Collections.sort(list);
        System.out.println("排序: " + list);

        // 反转
        Collections.reverse(list);
        System.out.println("反转: " + list);

        // 打乱
        Collections.shuffle(list);
        System.out.println("打乱: " + list);

        // 最大值/最小值
        System.out.println("max: " + Collections.max(list));
        System.out.println("min: " + Collections.min(list));

        // 频率
        System.out.println("1的出现次数: " + Collections.frequency(list, 1));

        // 不可变集合（Java 9+）
        List<String> immutable = List.of("A", "B", "C");
        Set<String> immutableSet = Set.of("X", "Y", "Z");
        Map<String, Integer> immutableMap = Map.of("one", 1, "two", 2);
        System.out.println("不可变List: " + immutable);
        // immutable.add("D"); // ❌ UnsupportedOperationException

        System.out.println();
    }
}
