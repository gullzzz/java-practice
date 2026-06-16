package exchange.model;

/**
 * 交易状态枚举。
 *
 * TODO 1: 定义三个常量 —— COMPLETED / FAILED / PENDING，每个带中文标签
 *         语法：COMPLETED("完成"), FAILED("失败"), PENDING("处理中");
 *         注意：常量列表结束后的分号不能丢！
 *
 * TODO 2: 声明一个 private final String label 字段，存中文标签
 *
 * TODO 3: 写一个私有构造器，接收 label 并赋值给 this.label
 *         枚举构造器默认就是 private，不需要写 private 关键字
 *
 * TODO 4: 写一个 public String getLabel() 方法，返回 label
 */
public enum TradeStatus {
    COMPLETED("完成"),FAILED("失败"),PENDING("处理中");

    private final String label;

    TradeStatus(String label) {
        this.label = label;
    }
    public String getLabel(){
        return label;
    }
    public static TradeStatus fromLabel(String label){
        for (TradeStatus ts:TradeStatus.values() ){
            if (ts.getLabel().equals(label)) {
                return ts;
            }
        }
        throw new IllegalArgumentException("不存在的标签");
    }

}
