package com.practice.lambda;

import java.util.*;
import java.util.function.*;

/**
 * 魔法交易所 — 数据分析中心
 *
 * 挑战：用 Lambda 表达式给交易数据加上分析能力。
 * 你手里有一个交易记录列表，每条格式： "物品 → 买家 | 成交价: 数字g"
 *
 * 四个 TODO 对应四个核心函数式接口。
 */
public class TradeAnalyzer {

    // 脚手架给的交易数据
    private static final List<String> TRADES = List.of(
            "冰霜法杖 → 张三 | 成交价: 500g",
            "龙鳞盾 → 李四 | 成交价: 1200g",
            "治疗药水 → 王五 | 成交价: 50g",
            "凤凰羽毛 → 赵六 | 成交价: 3000g",
            "魔法卷轴 → 孙七 | 成交价: 150g",
            "暗影斗篷 → 周八 | 成交价: 800g",
            "圣光之剑 → 吴九 | 成交价: 2500g",
            "解毒草 → 郑十 | 成交价: 30g"
    );

    // ========== 目标①：Predicate — 过滤 ==========

    /**
     * 用一个 Predicate 条件过滤交易列表。
     *
     * TODO 1: 用 Lambda 写出 Predicate 传给 filter，只保留满足条件的交易。
     *
     * 提示：
     *   - Predicate<String> 的抽象方法是 boolean test(String s)
     *   - 你需要从交易字符串中提取价格数字，判读是否 > 阈值
     *   - 写完后在 main 里测试：filter(trades, t -> ???)
     */
    public static List<String> filter(List<String> trades, Predicate<String> condition) {
        // TODO: 你的代码
        if(trades==null||condition==null){
            return Collections.emptyList();
        }
        List<String>result=new ArrayList<String>(trades.size());
            for(String t: trades){
                if(condition.test(t)){
                    result.add(t);
                }
            }
        return result;
    }

    // ========== 目标②：Function — 提取/转换 ==========

    /**
     * 从每条交易记录中提取一个字段（如物品名、买家名、价格）。
     *
     * TODO 2: 用 Lambda 写出 Function 传给 extract，返回提取后的列表。
     *
     * 提示：
     *   - Function<String, String> 的抽象方法是 String apply(String s)
     *   - 从 "物品 → 买家 | 成交价: 500g" 中拆出物品名 → 找到 " →" 的位置
     *   - 写完后在 main 里测试：extract(trades, t -> ???)
     */
    public static List<String> extract(List<String> trades, Function<String, String> mapper) {
        // TODO: 你的代码
        if(trades==null||mapper==null){
            return Collections.emptyList();
        }
        List<String>result=new ArrayList<String>(trades.size());

            for( String t:trades){
                String rt=mapper.apply(t);
                result.add(rt);
            }



        return result;
    }

    // ========== 目标③：Consumer — 消费/打印 ==========

    /**
     * 对每条交易执行一个操作（打印、存库、发通知等），不返回数据。
     *
     * TODO 3: 用 Lambda 写出 Consumer，帮老板生成报表格式。
     *
     * 提示：
     *   - Consumer<String> 的抽象方法是 void accept(String s)
     *   - 打印格式："[报表] 物品:{物品名} | 买家:{买家} | 价格:{价格}g"
     */
    public static void forEach(List<String> trades, Consumer<String> action) {
        // TODO: 你的代码
        if(trades==null||action==null){
            System.out.println("请传入合法参数");
            return;
        }

            for (String t:trades){
                action.accept(t);
            }

    }

    // ========== 目标④：Supplier — 生成数据 ==========

    /**
     * 用 Supplier 生成一条模拟交易记录。
     *
     * TODO 4: 用 Lambda 写出 Supplier，每次调用返回一条随机的新交易。
     *
     * 提示：
     *   - Supplier<String> 的抽象方法是 String get()
     *   - 随机从物品池选物品、从买家池选买家、随机价格 10~5000g
     *   - 格式要和 TRADES 一致："{物品} → {买家} | 成交价: {价格}g"
     *   - 下面已经给了 ITEMS 和 BUYERS 数组，Random 也声明好了
     */
    private static final String[] ITEMS = {"冰霜法杖", "龙鳞盾", "治疗药水", "凤凰羽毛", "魔法卷轴",
                                           "暗影斗篷", "圣光之剑", "解毒草", "火焰宝石", "雷霆之锤"};
    private static final String[] BUYERS = {"张三", "李四", "王五", "赵六", "孙七",
                                            "周八", "吴九", "郑十", "冒险者A", "冒险者B"};
    private static final Random RANDOM = new Random();

    public static List<String> generate(int count, Supplier<String> recordSupplier) {
        // TODO: 你的代码
        if(count<=0||recordSupplier==null){
            return Collections.emptyList();
        }
        List<String>result=new ArrayList<String>(count);

            while(count>0){
                result.add(recordSupplier.get());
                count--;
            }


        return result;
    }

    // ========== 脚手架工具方法 ==========

    /**
     * 从交易字符串中提取价格数字。
     * "冰霜法杖 → 张三 | 成交价: 500g" → 500
     *
     * 不用改，直接调。
     */
    public static int getPrice(String trade) {
        // "成交价: 500g"  →  "500"
        int start = trade.lastIndexOf(":") + 1;
        int end = trade.lastIndexOf("g");
        return Integer.parseInt(trade.substring(start, end).trim());
    }

    /**
     * 从交易字符串中提取物品名称。
     * "冰霜法杖 → 张三 | 成交价: 500g" → "冰霜法杖"
     */
    public static String getItemName(String trade) {
        return trade.substring(0, trade.indexOf(" →")).trim();
    }

    public static String getBuyerName(String trade) {
        int start = trade.indexOf("→") + 1;
        int end = trade.indexOf("|");
        return trade.substring(start, end).trim();
    }
    public static void main(String[] args) {
        System.out.println("===== 魔法交易所 · 数据分析中心 =====\n");

        // TODO 1: 过滤出高价交易（> 1000g），打印看看
        System.out.println("【高价交易 > 1000g】");
         List<String> expensive = filter(TRADES,t -> getPrice(t) > 1000 );
         forEach(expensive, t -> System.out.println("  " + t));

        System.out.println();

        // TODO 2: 提取所有物品名称，去重排序后打印
        System.out.println("【在售物品清单】");

         List<String> itemNames = extract(TRADES, t->getItemName(t));
         Set<String> in=new TreeSet<>(itemNames);

        System.out.println(in);
        // ...

        System.out.println();

        // TODO 3: 用 Consumer 给所有交易生成报表
        System.out.println("【老板日报表】");
       forEach(TRADES, t -> System.out.println("[报表] 物品:" + getItemName(t) + " | 买家:" + getBuyerName(t) + " | 价格:" + getPrice(t) + "g"));

        System.out.println();

        // TODO 4: 用 Supplier 生成 5 条模拟交易记录并打印
        System.out.println("【模拟新交易】");
         List<String> generated = generate(5, () ->{
             String item = ITEMS[RANDOM.nextInt(ITEMS.length)];   // 从数组随机选
             String buyer = BUYERS[RANDOM.nextInt(BUYERS.length)]; // 从数组随机选
             int price = RANDOM.nextInt(10, 5001);                 // 随机价格
             return item + " → " + buyer + " | 成交价: " + price + "g";
         }                                              );
         forEach(generated, t -> System.out.println("  " + t));
    }
}
