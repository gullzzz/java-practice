package exchange.reporter;

// TODO: 改成抽象类
// TODO: 添加模板方法：public final void generate(List<Trade> trades, String outputPath)
//         1. 调 buildContent(trades) 生成内容
//         2. 调 writeToFile(content, outputPath) 写入文件
// TODO: 添加抽象方法：protected abstract String buildContent(List<Trade> trades)
// TODO: 添加私有方法：private void writeToFile(String content, String path)
//         用 PrintWriter 写入文件

public class TradeReporter {
}
