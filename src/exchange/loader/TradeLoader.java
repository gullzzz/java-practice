package exchange.loader;

import exchange.exception.TradeParseException;
import exchange.model.Trade;
import exchange.model.TradeStatus;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// TODO: 添加方法 List<Trade> load(String filePath)
//       需要抛出的异常：TradeParseException, java.io.IOException

public interface TradeLoader {
    public  List<Trade> load(String filePath) throws TradeParseException,IOException;
}

