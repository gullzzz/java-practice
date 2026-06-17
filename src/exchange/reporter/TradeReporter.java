package exchange.reporter;

// TODO: 改成抽象类
// TODO: 添加模板方法：public final void generate(List<Trade> trades, String outputPath)
//         1. 调 buildContent(trades) 生成内容
//         2. 调 writeToFile(content, outputPath) 写入文件
// TODO: 添加抽象方法：protected abstract String buildContent(List<Trade> trades)
// TODO: 添加私有方法：private void writeToFile(String content, String path)
//         用 PrintWriter 写入文件

import exchange.model.Trade;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public abstract class TradeReporter {
    public final void generate(List<Trade> trades, String outputPath) throws IOException {
        String content=builtContent(trades);
        writeToFile(content,outputPath);
    }
    protected abstract String builtContent(List<Trade>trades);
    private void writeToFile(String content,String path) throws IOException {
        File f=new File(path);
        if (!f.exists()) {
            if (f.getParentFile() != null) {
                f.getParentFile().mkdirs();
            }
            f.createNewFile();
        }

        try(PrintWriter pw= new PrintWriter (new OutputStreamWriter(new FileOutputStream(f,false), StandardCharsets.UTF_8 ))) {
            pw.println(content);
        }


    }



}
