package com.practice.io;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 魔法交易所的"账本"——负责把交易记录持久化到文件。
 *
 * 你的任务：
 *   1. 补全 appendTrade() —— 把一条交易记录追加写入文件末尾
 *   2. 补全 readAllTrades() —— 读取文件中所有交易记录并打印
 *   3. 找出代码中隐藏的性能问题
 */
public class TradeLogger {

    // TODO: 这个文件路径有什么潜在问题？不同操作系统上能正常工作吗？
    private static final String LOG_FILE = "trades.log";
    File file = new File(LOG_FILE);

    /**
     * 将一条交易记录追加写入日志文件。
     * 格式: "yyyy-MM-dd HH:mm | {物品名} → {买家} | 成交价: {价格}"
     *
     * TODO 1: 选择合适的 IO 类实现"追加 + 按行写入"
     * TODO 2: 确保每次写入后换行
     * TODO 3: 确保流一定会被关闭（即使写的过程中出错了）
     *
     * 提示：想追加写入，FileWriter 构造器的第二个参数填什么？
     */
    public void appendTrade(String itemName, String buyer, String price) throws IOException {
        // 你的代码写在这里

        if(!file.exists()){
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        try( BufferedWriter br=new BufferedWriter(new FileWriter(LOG_FILE,true))){
                br.write(formatLine(itemName,buyer,price));
                br.newLine();
        }
    }

    /**
     * 读取日志文件中的所有交易记录，逐行打印到控制台。
     *
     * TODO 4: 选择合适的 IO 类实现"按行读取"
     * TODO 5: 文件不存在时应该怎么处理？
     *
     * 提示：哪种 Reader 能一行一行读？
     */
    public void readAllTrades() throws IOException{
        if(!file.exists()){
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        // 你的代码写在这里
        try(BufferedReader br=new BufferedReader(new FileReader(LOG_FILE))){
            String line;
            while((line= br.readLine())!=null){
                System.out.println(line);
            }
        }
    }

    // ========== 脚手架已写好的部分，无需修改 ==========

    private String formatLine(String itemName, String buyer, String price) {
        String time = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        return String.format("%s | %s → %s | 成交价: %s", time, itemName, buyer, price);
    }

    /**
     * 脚手架测试入口：写完一条记录后，立即读出来验证。
     * 你应该看到写入的那条记录出现在控制台中。
     */
    public static void main(String[] args) throws IOException {
        TradeLogger logger = new TradeLogger();

        // 写几笔交易
        logger.appendTrade("冰霜法杖", "冒险者张三", "500g");
        logger.appendTrade("龙鳞盾", "骑士李四", "1200g");

        // 读出来看看
        System.out.println("===== 交易日志 =====");
        logger.readAllTrades();
    }
}
