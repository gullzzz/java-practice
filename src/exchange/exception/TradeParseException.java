package exchange.exception;

// TODO: 继承 Exception（Checked 异常）
// TODO: 添加构造器 TradeParseException(String message)
// TODO: 添加构造器 TradeParseException(String message, Throwable cause)

public class TradeParseException extends Exception {
    public TradeParseException(String message) {
        super(message);
    }

    public TradeParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
