package com.practice.collections;

import java.util.*;

/**
 * 【词典大师·初阶】脚手架代码
 *
 * 把 WordLibrary 从"单词列表"升级为"单词→释义"的词典。
 * 关键数据结构从 Set 换成 Map，有几个核心方法需要你补全。
 *
 * 运行方式：
 *   javac -encoding UTF-8 -d out src/com/practice/collections/*.java
 *   java -cp out com.practice.collections.DictDemo
 */
public class DictDemo {

    //  ①：选哪种 Map 实现？不需要排序，只追求最快的查询速度
    private final Map<String, String> dict = new HashMap<>();

    /**
     * 添加单词+释义
     * @return true=添加成功，false=单词已存在（拒绝重复）
     */
    public boolean addWord(String word, String meaning) {
        //  ②：null检查 + 空字符串检查（未通过返回 false）
        // 提示：word 和 meaning 都要检查
        if(word==null||word.trim().isEmpty()||meaning==null||meaning.trim().isEmpty()){
            System.out.println("单词或释义不为空");
            return false;
        }

        // TODO ③：统一转小写（和 WordLibrary 一样，不区分大小写）
        word=word.toLowerCase();

        //  ④：检查单词是否已存在，存在则拒绝并返回 false
        // 提示：用哪个方法检查 key 是否存在？
        boolean flag=dict.containsKey(word);
        if(!flag){
            dict.put(word,meaning);
            return true;
        }


        return false;
    }

    /**
     * 查询单词释义
     * @return 释义文本，如果单词不存在返回 null
     */
    public String queryWord(String word) {
        //  ⑥：null检查
        if(word==null||word.trim().isEmpty()){return null;}

        word=word.toLowerCase();
        String result=dict.get(word);
        return result;



        //  ⑦：统一转小写后查询，用哪个方法？

    }

    /**
     * 词典中的单词数量
     */
    public int wordCount() {
        //  ⑧：一行搞定
        return dict.size();
    }

    /**
     * 列出词典中所有条目（格式："单词 → 释义"）
     * 要求按单词字母顺序排列
     */
    public List<String> listAllEntries() {
        // TODO ⑨：取出所有 key，排序，组装成 "key → value" 格式的列表

        List<String> al1=new ArrayList<>();
        for (Map.Entry<String,String> m1: dict.entrySet()){
            String m=m1.getKey()+"->"+m1.getValue();
            al1.add(m);
        }
        Collections.sort(al1);
        return al1;
    }

    // ========== 交互式命令行入口 ==========
    public static void main(String[] args) {
        DictDemo app = new DictDemo();
        Scanner scanner = new Scanner(System.in);

        System.out.println("📖 词典大师·初阶 — 命令行词典");
        System.out.println("命令：添加 <单词> <释义> | 查询 <单词> | 列出 | 退出\n");

        // TODO ⑩：用 while(true) + 读取每行输入，解析命令
        // 提示：
        //   1. 用 scanner.nextLine() 读一行
        //   2. 用 line.trim() 去首尾空格
        //   3. 用 line.startsWith("添加") 判断命令
        //   4. 用 line.substring(3).trim() 取命令后面的参数部分
        //   5. 对于"添加"，参数是 "单词 释义"，找第一个空格切分：
        //      int spaceIdx = args.indexOf(" ");
        //      word = args.substring(0, spaceIdx)
        //      meaning = args.substring(spaceIdx + 1)
        //   6. 根据 app.addWord / app.queryWord 返回结果打印对应信息

        while (true) {
            System.out.print("> ");
            String line = scanner.nextLine().trim();

            // 你的代码

        }
    }
}
