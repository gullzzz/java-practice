package exchange.analyzer;

import java.math.BigDecimal;

//总笔数、总金额、平均金额、完成率
public class AnalyzerData {
    private int tradeCount;
    private BigDecimal totalAmount;
    private BigDecimal averageAmount;
    private BigDecimal completionRate;

    public AnalyzerData(int tradeCount, BigDecimal totalAmount, BigDecimal averageAmount, BigDecimal completionRate) {
        this.tradeCount = tradeCount;
        this.totalAmount = totalAmount;
        this.averageAmount = averageAmount;
        this.completionRate = completionRate;
    }



    public int getTradeCount() {
        return tradeCount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public BigDecimal getAverageAmount() {
        return averageAmount;
    }

    public BigDecimal getCompletionRate() {
        return completionRate;
    }

  }


