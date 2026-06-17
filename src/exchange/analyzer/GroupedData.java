package exchange.analyzer;

import java.math.BigDecimal;

//笔数、合计金额
public class GroupedData {
    private int count;
    private BigDecimal totalAmount;

    public GroupedData(int count, BigDecimal totalAmount) {
        this.count = count;
        this.totalAmount = totalAmount;
    }

    public int getCount() {
        return count;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
}
