package exchange;

// TODO: main 方法 —— 串联整个流程：
//         1. 创建 FileTradeLoader，调 load("trades.log") 得到交易列表
//         2. 创建 TradeAnalyzer，调 computeStats / groupByStatus / topN / filterByDate
//         3. 创建 TradeQuery，调 findByProductName 查某个商品
//         4. 创建 SummaryReporter，调 generate() 生成汇总报表 → report.txt
//         5. 创建 DetailReporter，调 generate() 生成明细报表 → detail_report.txt
//         6. 把分析结果也打印到控制台
//         7. 所有 IO 操作用 try-catch 包裹

import exchange.exception.TradeParseException;
import exchange.loader.FileTradeLoader;
import exchange.model.Trade;

import java.io.IOException;
import java.util.List;

public class ExchangeApp {
    public static void main(String[] args) throws IOException, TradeParseException {
        FileTradeLoader fileTradeLoader=new FileTradeLoader();

        List<Trade> list= fileTradeLoader.load("D:/java/project/java-practice/trades.log");
        for (Trade t:list){
            System.out.println(t);
        }


    }
}
