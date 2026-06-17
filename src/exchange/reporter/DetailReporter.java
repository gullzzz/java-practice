package exchange.reporter;

// TODO: 继承 TradeReporter
// TODO: 实现 buildContent(trades)：生成明细报表，包含：
//         - 每笔交易的完整信息（名称、金额、状态、日期）
//         - Top N 最贵交易（委托给 TradeAnalyzer）
//         - 按商品分组展示

import exchange.analyzer.TradeAnalyzer;
import exchange.model.Trade;

import java.time.LocalDateTime;
import java.util.List;

public class DetailReporter extends TradeReporter {
    @Override
    protected String builtContent(List<Trade> trades) {
        TradeAnalyzer analyzer = new TradeAnalyzer();
        StringBuilder sb = new StringBuilder();

        sb.append("======== 魔法交易所·明细报表 ========\n");
        sb.append("生成时间：").append(LocalDateTime.now()).append("\n\n");

        sb.append("--- 全部交易明细 ---\n");
        // TODO: for 循环遍历 trades → sb.append(t).append("\n")
        for(Trade trade:trades){
            sb.append("名称：").append(trade.getName())
                    .append(" | 总金额：¥").append(trade.getAmount())
                    .append(" | 状态：").append(trade.getStatus())
                    .append(" | 日期：").append(trade.getDate())
                    .append("\n");
        }

        sb.append("\n--- Top 3 最贵交易 ---\n");
        // TODO: analyzer.topN(trades, 3) → 遍历结果 → sb.append(...)
        analyzer.topN(trades,3).forEach(trade -> sb.append("名称：").append(trade.getName())
                .append(" | 总金额：¥").append(trade.getAmount())
                .append(" | 状态：").append(trade.getStatus())
                .append(" | 日期：").append(trade.getDate())
                .append("\n"));


        return sb.toString();
    }
}
