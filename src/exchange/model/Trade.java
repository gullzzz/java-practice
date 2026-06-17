package exchange.model;

import java.math.BigDecimal;
import java.time.LocalDate;

// TODO: 添加四个 private final 字段：name(String)、amount(BigDecimal)、status(TradeStatus)、date(LocalDate)
// TODO: 添加全参构造器
// TODO: 添加四个 getter 方法
// TODO: 添加 toString()，格式："商品名 | 金额 | 状态 | 日期"

public class Trade {
    private final String name;
    private final BigDecimal amount;
    private final TradeStatus status;
    private final LocalDate date;

    public Trade(String name, BigDecimal amount, TradeStatus status, LocalDate date) {
        this.name = name;
        this.amount = amount;
        this.status = status;
        this.date = date;
    }

    @Override
    public String toString() {
        return getName() +"|"+getAmount()+"|"+getStatus().getLabel()+"|"+getDate();
    }

    public String getName() {
        return name;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public TradeStatus getStatus() {
        return status;
    }

    public LocalDate getDate() {
        return date;
    }
}
