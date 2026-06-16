package exchange.loader;

// TODO: 实现 TradeLoader 接口
// TODO: 实现 load(String filePath) 方法：
//         1. 用 BufferedReader + FileReader 逐行读取文件
//         2. 跳过空行和以 # 开头的注释行
//         3. 每行按逗号分割，解析为 Trade 对象（商品名,金额,状态,日期）
//         4. 金额用 new BigDecimal(str)，日期用 LocalDate.parse(str)
//         5. 如果格式不对（字段数不对、金额格式错、状态不匹配、日期格式错），
//            用 throw new TradeParseException("行" + lineNumber + "格式错误", 原始异常)
//         6. 返回 List<Trade>

import exchange.exception.TradeParseException;
import exchange.model.Trade;
import exchange.model.TradeStatus;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FileTradeLoader implements TradeLoader {


    @Override
    public List<Trade> load(String filePath) throws TradeParseException, IOException {
        File file=new File(filePath);
        List<Trade> tradeList=new ArrayList<>();

        if (!file.exists()){
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        try (BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))){
            String line;
            int lineNum=1;
            while((line=bufferedReader.readLine())!=null){
                if(line.trim().isEmpty()||line.startsWith("#"))continue;
                try {
                    String[] parts=line.split(",");
                    if(parts.length!=4){
                        throw new TradeParseException("第" + lineNum + "行格式错误");
                    }
                    Trade trade=new Trade(parts[0], new BigDecimal(parts[1]), TradeStatus.fromLabel(parts[2]), LocalDate.parse(parts[3]));
                    tradeList.add(trade);

                } catch ( DateTimeException |IllegalArgumentException e) {
                    System.out.println("第" + lineNum + "行格式错误");
                }catch (TradeParseException e ){
                    System.out.println(e);
                }
                lineNum++;
            }
        }
        return tradeList;
    }
}
