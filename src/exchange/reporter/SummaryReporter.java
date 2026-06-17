package exchange.reporter;

// TODO: 继承 TradeReporter
// TODO: 实现 buildContent(trades)：生成总览汇总报表，包含：
//         - 生成时间
//         - 总笔数、总金额、平均金额、完成率（委托给 TradeAnalyzer）
//         - 按状态分组统计（委托给 TradeAnalyzer）

import exchange.analyzer.AnalyzerData;
import exchange.analyzer.GroupedData;
import exchange.analyzer.TradeAnalyzer;
import exchange.model.Trade;
import exchange.model.TradeStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class SummaryReporter extends TradeReporter{
    @Override
    protected String builtContent(List<Trade> trades) {
        TradeAnalyzer analyzer = new TradeAnalyzer();
        AnalyzerData stats = analyzer.computeStats(trades);
        Map<TradeStatus, GroupedData> groups = analyzer.groupByStatus(trades);

        StringBuilder sb = new StringBuilder();
        sb.append("======== 魔法交易所·汇总报表 ========\n");
        sb.append("生成时间：").append(LocalDateTime.now()).append("\n\n");

        sb.append("--- 总览 ---\n");
        // TODO: 拼 stats 四个指标，参考上面我给你的那行 append
        sb.append("总笔数：").append(stats.getTradeCount())
                .append(" | 总金额：¥").append(stats.getTotalAmount())
                .append(" | 平均金额：¥").append(stats.getAverageAmount())
                .append(" | 完成率：").append(stats.getCompletionRate()).append("%")
                .append("\n");

        sb.append("\n--- 按状态分组 ---\n");
        for (Map.Entry<TradeStatus, GroupedData> entry : groups.entrySet()) {
            // TODO: 拼每组的状态名 + 笔数 + 金额
            sb.append(entry.getKey().getLabel()).append(entry.getValue().getCount()).append(entry.getValue().getTotalAmount());
        }

        return sb.toString();

    }
}
