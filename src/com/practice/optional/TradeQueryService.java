package com.practice.optional;

import com.practice.lambda.TradeAnalyzer;

import java.util.*;
import java.util.stream.*;

import static com.practice.lambda.TradeAnalyzer.*;

/**
 * 魔法交易所 · 查询安全官
 *
 * 挑战：让所有"可能查不到"的查询都返回 Optional，
 * 调用方被迫优雅处理空结果，而不是裸调 .get() 炸穿 JVM。
 */
public class TradeQueryService {

    private final List<String> trades;

    public TradeQueryService(List<String> trades) {
        this.trades = trades != null ? trades : List.of();
    }

    // ========== 目标①：findMostExpensive ==========

    /**
     * 找到价格最高的那笔交易。
     *
     * TODO 1: 用 stream().max() 实现。
     *
     * 提示：
     *   - max() 需要 Comparator，用 Comparator.comparingInt(TradeAnalyzer::getPrice)
     *   - max() 的返回值类型是什么？去 IDE 里看一下——你会发现它已经是 Optional！
     *   - 如果 trades 是空的，max() 返回什么？
     *   - 代码只需要一行
     */
    public Optional<String> findMostExpensive() {
        // TODO: 你的代码
        return trades.stream().max(Comparator.comparingInt(TradeAnalyzer::getPrice));
    }

    // ========== 目标②：findCheapest ==========

    /**
     * 找到价格最低的那笔交易。
     *
     * TODO 2: 和上面一样，但用 min()。
     */
    public Optional<String> findCheapest() {
        // TODO: 你的代码
        return trades.stream().min(Comparator.comparingInt(TradeAnalyzer::getPrice));
    }

    // ========== 目标③：findByBuyer ==========

    /**
     * 根据买家名查找交易。
     *
     * TODO 3: 用 stream().filter().findFirst() 实现。
     *
     * 提示：
     *   - 过滤条件：getBuyerName(t).equals(name)
     *   - findFirst() 的返回值类型是什么？
     *   - 如果没人叫这个名字，findFirst() 返回什么？
     */
    public Optional<String> findByBuyer(String name) {
        // TODO: 你的代码
        return trades.stream().filter(t->getBuyerName(t).equals(name)).findFirst();

    }

    // ========== 目标④：findByItemName ==========

    /**
     * 根据物品名查找交易。
     *
     * TODO 4: 和上面一样，但按物品名查。
     */
    public Optional<String> findByItemName(String itemName) {
        // TODO: 你的代码
        return trades.stream().filter(t->getItemName(t).equals(itemName)).findFirst();
    }

    // ========== main：测试你的实现 ==========

    public static void main(String[] args) {
        System.out.println("===== 魔法交易所 · 查询安全官 =====\n");

        // 复用 Lambda 章的数据
        List<String> tradeList = Arrays.asList(
            "冰霜法杖 → 张三 | 成交价: 500g",
            "龙鳞盾 → 李四 | 成交价: 1200g",
            "治疗药水 → 王五 | 成交价: 50g",
            "凤凰羽毛 → 赵六 | 成交价: 3000g",
            "魔法卷轴 → 孙七 | 成交价: 150g",
            "暗影斗篷 → 周八 | 成交价: 800g",
            "圣光之剑 → 吴九 | 成交价: 2500g",
            "解毒草 → 郑十 | 成交价: 30g"
        );

        TradeQueryService service = new TradeQueryService(tradeList);

        // ===== 测试区：等你实现上面的方法后跑 =====

        // 场景A：找到最贵交易，打印物品名和价格

        System.out.println("【场景A：最贵交易】");

        // TODO 5: 用 findMostExpensive() + map + ifPresent 链式调用
        // 目标输出格式："最贵交易：凤凰羽毛，成交价 3000g"
        //
        // 提示链：
        //   service.findMostExpensive()           → Optional<String>
        //     .map(t -> ???)                      → Optional<String> (格式化成你要的文字)
        //     .ifPresent(System.out::println);    → 有值就打印
        //
        // 你的代码写在这里 ↓
        service.findMostExpensive().map(t->"最贵交易:"+getItemName(t)+",成交价"+getPrice(t)+"g").ifPresent(System.out::println);

        // ===== 场景B：查找一个存在的买家 =====
        System.out.println("\n【场景B：查找买家赵六】");
        // TODO 6: 用 findByBuyer("赵六") 查找，链式打印物品名和价格
        // 目标输出："赵六买了凤凰羽毛，成交价 3000g"
        //
        // 你的代码写在这里 ↓
        String name="赵六";
        service.findByBuyer(name).map(t->name+"买了"+getItemName(t)+",成交价"+getPrice(t)+"g").ifPresent(System.out::println);


        // ===== 场景C：查找一个不存在的买家 =====
        System.out.println("\n【场景C：查找买家神秘人（不存在）】");
        // TODO 7: 用 findByBuyer("神秘人") 查找，如果不存在就打印"未找到此买家"
        //
        // 提示：有值用 ifPresent，没值...还有什么方法可以在空的时候执行？
        // 你的代码写在这里 ↓
        String name1="神秘人";
        service.findByBuyer(name1).map(t->name1+"买了"+getItemName(t)+",成交价"+getPrice(t)+"g").ifPresentOrElse(System.out::println,()-> System.out.println("未找到买家"));

        // ===== 场景D：链式查找 + 价格格式化 =====
        System.out.println("\n【场景D：物品价格查询】");
        // TODO 8: 查"解毒草"，拿到价格后格式化成 "解毒草 的价格是 30g"
        // 如果查不到（比如查"灭世魔杖"），输出 "物品不存在"
        //
        // 你的代码写在这里 ↓
        String itemName="解毒草";
        service.findByItemName(itemName).map(t->itemName+"的价格是"+getPrice(t)).ifPresentOrElse(System.out::println,()-> System.out.println("物品不存在"));


        // ===== 场景E：风控检查——最贵交易是否超过阈值 =====
        System.out.println("\n【场景E：风控检查（阈值 2000g）】");
        // TODO 9: 用 findMostExpensive() + filter + isPresent
        // 如果最贵交易 > 2000g，打印 "⚠️ 警告：存在超过2000g的高额交易！"
        // 否则打印 "✅ 今日交易均在安全范围内"
        //
        // 提示：filter 里提取价格判读，然后判断 isPresent()
        // 你的代码写在这里 ↓
        service.findMostExpensive().filter(t->getPrice(t)>2000).ifPresentOrElse(t-> System.out.println("⚠️ 警告：存在超过2000g的高额交易！"),()-> System.out.println("✅ 今日交易均在安全范围内"));

        // ===== 场景F：对比空列表的行为 =====
        System.out.println("\n【场景F：空列表查询】");
        TradeQueryService emptyService = new TradeQueryService(List.of());
        // TODO 10: 在空列表上调用 findMostExpensive()，用 orElse 给出兜底文案
        // 目标输出："暂无交易记录"
        //
        // 你的代码写在这里 ↓
        System.out.println(emptyService.findMostExpensive().orElse("暂无交易记录"));


    }
}
