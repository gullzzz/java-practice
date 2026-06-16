package exchange.analyzer;

// TODO: 添加四个分析方法，每个接收 List<Trade>：
// TODO: computeStats(trades) —— 返回总笔数、总金额、平均金额、完成率
//         提示：完成率 = COMPLETED笔数 / 总笔数
// TODO: groupByStatus(trades) —— 按 TradeStatus 分组，
//         每组包含：笔数、合计金额
// TODO: topN(trades, n) —— 按金额降序，返回前 N 笔
// TODO: filterByDate(trades, startDate, endDate) —— 返回日期在 [start, end] 范围内的交易

import exchange.model.Trade;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class TradeAnalyzer {
    public AnalyzerData computeStats(List<Trade> trades){
        int size = trades.size();
        BigDecimal sum= BigDecimal.ZERO;
        BigDecimal avg= BigDecimal.ZERO;
        BigDecimal comp= BigDecimal.ZERO;
        int completed=0;
        sum= trades.stream().map(Trade::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        for(Trade trade:trades){
            if(trade.getStatus().getLabel().equals("完成")){
                completed++;
            }
        }

        avg=sum.divide(BigDecimal.valueOf(size), 2,RoundingMode.HALF_UP);
        comp=BigDecimal.valueOf(completed).divide(BigDecimal.valueOf(size),1,RoundingMode.HALF_UP);
        return new AnalyzerData(size,sum,avg,comp);
    }



}
