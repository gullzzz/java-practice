package exchange;

import exchange.analyzer.AnalyzerData;
import exchange.analyzer.GroupedData;
import exchange.analyzer.TradeAnalyzer;
import exchange.analyzer.TradeQuery;
import exchange.exception.TradeParseException;
import exchange.loader.FileTradeLoader;
import exchange.model.Trade;
import exchange.model.TradeStatus;
import exchange.reporter.DetailReporter;
import exchange.reporter.SummaryReporter;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class ExchangeApp {
    public static void main(String[] args) {
        try {
            FileTradeLoader loader = new FileTradeLoader();
            List<Trade> trades = loader.load("trades.log");
            TradeAnalyzer analyzer = new TradeAnalyzer();

            // 1. 总览
            AnalyzerData stats = analyzer.computeStats(trades);
            System.out.println("=== 总览 ===");
            System.out.println("总笔数：" + stats.getTradeCount());
            System.out.println("总金额：¥" + stats.getTotalAmount());
            System.out.println("平均金额：¥" + stats.getAverageAmount());
            System.out.println("完成率：" + stats.getCompletionRate() + "%");

            // 2. 分组
            System.out.println("\n=== 按状态分组 ===");
            Map<TradeStatus, GroupedData> groups = analyzer.groupByStatus(trades);
            for (Map.Entry<TradeStatus, GroupedData> entry : groups.entrySet()) {
                System.out.println(entry.getKey().getLabel() + "：" + entry.getValue().getCount()
                        + "笔 | 合计 ¥" + entry.getValue().getTotalAmount());
            }

            // 3. Top3
            System.out.println("\n=== Top 3 最贵 ===");
            for (Trade t : analyzer.topN(trades, 3)) {
                System.out.println(t.getName() + " | ¥" + t.getAmount()
                        + " | " + t.getStatus().getLabel());
            }

            // 4. 查询
            System.out.println("\n=== 查询：冰霜法杖 ===");
            TradeQuery.findByProductName(trades, "冰霜法杖")
                    .ifPresentOrElse(
                            list -> list.forEach(System.out::println),
                            () -> System.out.println("未找到该商品")
                    );

            // 5. 生成报表
            new SummaryReporter().generate(trades, "report.txt");
            new DetailReporter().generate(trades, "detail_report.txt");
            System.out.println("\n报表已生成 → report.txt / detail_report.txt");

        } catch (IOException | TradeParseException e) {
            System.out.println("运行失败：" + e.getMessage());
        }
    }
}
