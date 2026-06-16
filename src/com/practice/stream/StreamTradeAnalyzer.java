package com.practice.stream;

import com.practice.lambda.TradeAnalyzer;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import static com.practice.lambda.TradeAnalyzer.*;

/**
 * 魔法交易所 · Stream 流水线版
 *
 * 你的任务：把 TradeAnalyzer 里手写的 for 循环，
 * 全部替换成 Stream 链式调用。
 *
 * 同样的数据、同样的 Lambda，但控制流程交给 Stream。
 */
public class StreamTradeAnalyzer {

    // ========== 目标①：filter → 用 Stream 重写 ==========

    /**
     * 用 Stream.filter() 替代 for + if。
     *
     * TODO 1: 把下面的 for 循环改成一行 stream().filter().collect()
     *
     * 对照你的旧代码：
     *   for (String t : trades) {
     *       if (condition.test(t)) {     ← 这就是 filter
     *           result.add(t);           ← 这就是 collect
     *       }
     *   }
     *
     * 提示：
     *   - trades.stream() 打开流水线
     *   - .filter(condition) 保留满足条件的（注意：condition 已经是 Predicate，直接传！）
     *   - .collect(Collectors.toList()) 收进 List
     */
    public static List<String> filterByStream(List<String> trades, Predicate<String> condition) {
        // TODO: 用一行 Stream 链式调用替换下面的 for 循环
        // ===== 旧代码（删掉，换成 Stream）=====
          List<String> result = trades.stream().filter(condition).collect(Collectors.toList());

//        for (String t : trades) {
//            if (condition.test(t)) {
//                result.add(t);
//            }
//        }
        return result;
        // ===== 你的 Stream 代码写在这里 =====
    }

    // ========== 目标②：map → 用 Stream 重写 ==========

    /**
     * 用 Stream.map() 替代 for + apply。
     *
     * TODO 2: 把 for + mapper.apply() 改成 stream().map().collect()
     *
     * 对照：
     *   for (String t : trades) {
     *       result.add(mapper.apply(t));  ← 这就是 map
     *   }
     */
    public static List<String> mapByStream(List<String> trades, Function<String, String> mapper) {
        // TODO: 用 Stream 重写
        return trades.stream().map(mapper).collect(Collectors.toList());
    }

    // ========== 目标③：sorted → Stream 新增能力 ==========

    /**
     * 用 Stream.sorted() 对交易按价格排序。
     *
     * TODO 3: stream().sorted(比较器).collect()
     *
     * 提示：
     *   - sorted() 需要 Comparator，你得告诉它怎么比两条交易的价格
     *   - 可以用 Comparator.comparingInt(TradeAnalyzer::getPrice)
     *   - 想从高到低？在 comparingInt 后面加 .reversed()
     */
    public static List<String> sortByPrice(List<String> trades) {
        // TODO: 用 Stream + sorted 实现按价格排序（从高到低）

        return trades.stream().sorted(Comparator.comparingInt(TradeAnalyzer::getPrice).reversed()).collect(Collectors.toList());

    }

    // ========== 目标④：综合流水线 ==========

    /**
     * 一条流水线完成：过滤高价 → 提取物品名 → 去重 → 排序 → 收集。
     *
     * TODO 4: 把 filter → map → distinct → sorted → collect 串成一条链
     *
     * 提示：中间操作可以无限 . 下去：
     *   stream()
     *     .filter(...)
     *     .map(...)
     *     .distinct()
     *     .sorted()
     *     .collect(...)
     */
    public static List<String> highValueItems(List<String> trades, int minPrice) {
        // TODO: 一条流水线完成所有操作

        return trades.stream().filter(t -> getPrice(t) > minPrice).map(TradeAnalyzer::getItemName).distinct().sorted().collect(Collectors.toList());
    }

    // ========== 目标⑤：limit → 只看前 N 名 ==========

    /**
     * 返回价格最高的前 n 条交易。
     *
     * TODO 5: sorted(降序) → limit(n) → collect
     *
     * 提示：
     *   - 先排好序（高→低），再 limit(n) 截断
     *   - 排序用 sorted(Comparator.comparingInt(...).reversed())
     */
    public static List<String> topNTrades(List<String> trades, int n) {
        // TODO: 用 Stream 实现

        return trades.stream().sorted(Comparator.comparingInt( TradeAnalyzer::getPrice ).reversed()).limit(n).collect(Collectors.toList());
    }

    // ========== 目标⑥：mapToInt + sum → 统计总流水 ==========

    /**
     * 计算所有交易的总金额。
     *
     * TODO 6: stream → mapToInt(getPrice) → sum()
     *
     * 提示：
     *   - mapToInt 和 map 的区别：mapToInt 返回 IntStream（不是 Stream<Integer>）
     *   - IntStream 上有 .sum()、.average()、.max() 直接用
     *   - mapToInt 的参数类型是 ToIntFunction<String>，你猜怎么传？
     */
    public static int totalRevenue(List<String> trades) {
        // TODO: 用 Stream 实现

        return trades.stream().mapToInt(t->getPrice(t)).sum();
    }

    // ========== 目标⑦：anyMatch → 风控巡检 ==========

    /**
     * 检查是否存在超过阈值的交易（风控用）。
     *
     * TODO 7: stream → anyMatch(条件)
     *
     * 提示：
     *   - anyMatch 返回 boolean，不是 List，不需要 collect
     *   - anyMatch 是短路操作：找到一个 true 就停，不继续遍历
     */
    public static boolean hasSuspiciousTrade(List<String> trades, int threshold) {
        // TODO: 用 Stream 实现

        return trades.stream().anyMatch(t->getPrice(t)>threshold);
    }

    // ========== main ==========

    public static void main(String[] args) {
        System.out.println("===== 魔法交易所 · Stream 流水线 =====\n");

        // 复用 Lambda 章的数据
        List<String> trades = Arrays.asList(
            "冰霜法杖 → 张三 | 成交价: 500g",
            "龙鳞盾 → 李四 | 成交价: 1200g",
            "治疗药水 → 王五 | 成交价: 50g",
            "凤凰羽毛 → 赵六 | 成交价: 3000g",
            "魔法卷轴 → 孙七 | 成交价: 150g",
            "暗影斗篷 → 周八 | 成交价: 800g",
            "圣光之剑 → 吴九 | 成交价: 2500g",
            "解毒草 → 郑十 | 成交价: 30g"
        );

        // TODO 1 测试：过滤高价交易 (> 1000g)
        System.out.println("【TODO 1: 高价交易 > 1000g】");
        List<String> expensive = filterByStream(trades, t -> getPrice(t) > 1000);
        System.out.println("  结果: " + expensive);
        System.out.println("  预期: [龙鳞盾, 凤凰羽毛, 圣光之剑] 相关记录共 3 条\n");

        // TODO 2 测试：提取所有物品名
         System.out.println("【TODO 2: 提取物品名】");
         List<String> items1 = mapByStream(trades, TradeAnalyzer::getItemName);
         System.out.println("  结果: " + items1 + "\n");

        // TODO 3 测试：按价格从高到低排序
        System.out.println("【TODO 3: 按价格排序（高→低）】");
        List<String> sorted = sortByPrice(trades);
        sorted.forEach(t -> System.out.println("  " + getPrice(t) + "g — " + getItemName(t)));
        System.out.println();

        // TODO 4 测试：综合流水线
         System.out.println("【TODO 4: 高价物品清单（去重排序）】");
         List<String> items2 = highValueItems(trades, 500);
         System.out.println("  结果: " + items2 + "\n");

        // TODO 5 测试：价格最高的前 3 名
        System.out.println("【TODO 5: 成交价 Top 3】");
        List<String> top3 = topNTrades(trades, 3);
        top3.forEach(t -> System.out.println("  " + getPrice(t) + "g — " + getItemName(t)));
        System.out.println();

        // TODO 6 测试：总流水
        System.out.println("【TODO 6: 今日总流水】");
        int total = totalRevenue(trades);
        System.out.println("  总金额: " + total + "g\n");

        // TODO 7 测试：风控巡检
        System.out.println("【TODO 7: 风控巡检（>2000g 警戒线）】");
        boolean suspicious = hasSuspiciousTrade(trades, 2000);
        System.out.println("  存在可疑交易？ " + suspicious + "");
    }
}
