package com.practice.collections;

import java.util.*;

/**
 * 【词汇大师·初阶】脚手架代码
 *
 * 你的词库核心类。这不是成品——有几处关键逻辑需要你补全。
 * 运行方式：
 *   javac -encoding UTF-8 -d out src/com/practice/collections/WordLibrary.java
 *   java -cp out com.practice.collections.WordLibrary
 */
public class WordLibrary {

    // TODO ①：这里选哪种 Set 实现？为什么？
    private final Set<String> words = new HashSet<>();

    /**
     * 添加单词
     * @return true=添加成功，false=单词已存在
     */
    public boolean addWord(String word) {
        // TODO ②：空字符串或 null 应该怎么处理？直接返回 false 还是抛异常？
        if (word == null || word.trim().isEmpty()) {
            System.out.println("添加失败");
            return false;
            // 你的处理逻辑
        }
        word =word.toLowerCase();


        // TODO ③：统一转小写再存储，避免 "Apple" 和 "apple" 被当成两个词
        // 提示：这行代码有一个陷阱——add 返回的是 boolean，你直接用了吗？
        return words.add(word);
    }

    /**
     * 查询单词是否存在
     */
    public boolean containsWord(String word) {
        if(word==null){
            return false;
        }

        word =word.toLowerCase();

        // TODO ④：这里也需要统一大小写处理吗？
        return words.contains(word);
    }

    /**
     * 获取词库中的单词总数
     */
    public int wordCount() {
        return words.size();
    }

    /**
     * 列出所有单词（按字母顺序）
     * TODO ⑤：words 是 HashSet，天生无序。怎么返回排序后的列表？
     * 提示：不一定要换 TreeSet，Collections 里有个现成的方法…
     */
    public List<String> listAllWords() {
        List<String> wordlist=new ArrayList<>(words);
        Collections.sort(wordlist);

        // 你的代码
        return wordlist;
    }

    /**
     * 批量添加单词，返回成功添加的数量
     * TODO ⑥：遍历数组，调用 addWord，统计成功数
     */
    public int addAllWords(String[] newWords) {
        int successCount = 0;
        // 你的代码
        for (int i = 0; i < newWords.length; i++) {
            if(addWord(newWords[i])){
                successCount++;
            }
        }
        return successCount;
    }

    // ========== 简易命令行测试入口 ==========
    public static void main(String[] args) {
        WordLibrary lib = new WordLibrary();

        // 测试添加
        System.out.println("添加 'apple': " + lib.addWord("apple"));    // 期望 true
        System.out.println("添加 'Apple': " + lib.addWord("Apple"));    // 期望 false（大小写统一后重复）
        System.out.println("添加 'banana': " + lib.addWord("banana"));  // 期望 true
        System.out.println("添加 'apple': " + lib.addWord("apple"));    // 期望 false
        System.out.println("添加 '': " + lib.addWord(""));              // 你决定的行为

        System.out.println("词库总数: " + lib.wordCount());              // 期望 2

        System.out.println("包含 'apple': " + lib.containsWord("apple"));
        System.out.println("包含 'APPLE': " + lib.containsWord("APPLE")); // 期望 true

        // 批量添加
        String[] batch = {"cherry", "Banana", "date", "cherry"};
        System.out.println("批量成功添加: " + lib.addAllWords(batch));    // 期望 2（cherry, date）

        System.out.println("全部单词: " + lib.listAllWords());            // 期望按字母顺序
    }
}
