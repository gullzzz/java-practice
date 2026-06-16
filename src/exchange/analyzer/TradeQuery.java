package exchange.analyzer;

// TODO: findByProductName(trades, name) —— 按商品名精确匹配，返回 Optional<List<Trade>>
//         提示：可能查不到，返回 Optional.empty()

import exchange.model.Trade;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TradeQuery {
    public  static Optional<List<Trade>> findByProductName(List<Trade> trade,String name){

      List<Trade> result= trade.stream().filter(t -> t.getName().equals(name)).collect(Collectors.toList());
      return result.isEmpty()?Optional.empty():Optional.of(result);


    }
}
