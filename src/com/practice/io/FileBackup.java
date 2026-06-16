package com.practice.io;

import java.io.*;
import java.time.Duration;
import java.time.Instant;

/**
 * 魔法交易所 — 文件备份系统
 */
public class FileBackup {

    private static final String SOURCE = "trades.log";
    private static final String BACKUP_DIR = "backup";
    private static final String BIG_FILE = BACKUP_DIR + "/big_test.dat";

    // ========== 目标①：字节流逐字节复制 ==========

    public void copyWithByteStream(String srcPath, String destPath) throws IOException {
        try (FileInputStream fis = new FileInputStream(srcPath);
             FileOutputStream fos = new FileOutputStream(destPath)) {
            int b;
            while ((b = fis.read()) != -1) {
                fos.write(b);
            }
        }
    }

    // ========== 目标②：自定义缓冲区批量复制 ==========

    public void copyWithBuffer(String srcPath, String destPath, int bufferSize) throws IOException {
        try (FileInputStream fis = new FileInputStream(srcPath);
             FileOutputStream fos = new FileOutputStream(destPath)) {
            byte[] buff = new byte[bufferSize];
            int bytesRead;
            while ((bytesRead = fis.read(buff)) != -1) {
                fos.write(buff, 0, bytesRead);
            }
        }
    }

    // ========== 目标②扩展：BufferedInputStream/OutputStream ==========

    public void copyWithBufferedStream(String srcPath, String destPath,int bufferSize) throws IOException {
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(srcPath));
             BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(destPath))) {
            int b;
            byte[] buff = new byte[bufferSize];


            while ((b = bis.read(buff)) != -1) {
                bos.write(buff,0,b);
            }
        }
    }

    // ========== 目标③：文件完整性校验 ==========

    public boolean verifyFiles(String path1, String path2) throws IOException {
        File f1 = new File(path1);
        File f2 = new File(path2);
        if (f1.length() != f2.length()) {
            System.out.println("  长度不同: " + f1.length() + " vs " + f2.length());
            return false;
        }
        try (BufferedInputStream bis1 = new BufferedInputStream(new FileInputStream(path1));
             BufferedInputStream bis2 = new BufferedInputStream(new FileInputStream(path2))) {
            int pos = 0, a, b;
            while ((a = bis1.read()) != -1) {
                b = bis2.read();
                if (a != b) {
                    System.out.println("  字节 " + pos + " 不一致: " + a + " vs " + b);
                    return false;
                }
                pos++;
            }
        }
        return true;
    }

    // ========== 辅助：生成大文件用于压力测试 ==========

    /**
     * 生成一个指定大小的测试文件，内容为重复的字母序列。
     */
    private void generateBigFile(String path, int sizeInMB) throws IOException {
        new File(BACKUP_DIR).mkdirs();
        byte[] chunk = new byte[8192];
        // 用字母填满 chunk，方便肉眼确认内容
        for (int i = 0; i < chunk.length; i++) {
            chunk[i] = (byte) ('A' + (i % 26));
        }
        long total = sizeInMB * 1024L * 1024L;
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(path))) {
            long written = 0;
            while (written < total) {
                int toWrite = (int) Math.min(chunk.length, total - written);
                bos.write(chunk, 0, toWrite);
                written += toWrite;
            }
        }
        System.out.println("  生成了 " + sizeInMB + " MB 测试文件: " + path);
    }

    // ========== 性能基准测试 ==========

    public void benchmark() throws IOException {
        new File(BACKUP_DIR).mkdirs();

        // 先用小文件热身
        File src = new File(SOURCE);
        File  des=new File(BACKUP_DIR);
        if (!src.exists()) {
            System.out.println("⚠️ " + SOURCE + " 不存在，先运行 TradeLogger 写几条交易记录吧！");
            return;
        }else if(!des.exists()){
            System.out.println(  "⚠️ " + SOURCE + " 不存在");
        }

        System.out.println("===== 小文件测试 (" + src.length() + " 字节) =====");
        timeIt("逐字节复制         ", () -> copyWithByteStream(SOURCE, BACKUP_DIR + "/backup_byte.tmp"));
        timeIt("1KB 缓冲区         ", () -> copyWithBuffer(SOURCE, BACKUP_DIR + "/backup_1k.tmp", 1024));
        timeIt("8KB 缓冲区         ", () -> copyWithBuffer(SOURCE, BACKUP_DIR + "/backup_8k.tmp", 8192));
        timeIt("BufferedStream     ", () -> copyWithBufferedStream(SOURCE, BACKUP_DIR + "/backup_buf.tmp",8192));

        // 验证小文件
        boolean ok = verifyFiles(SOURCE, BACKUP_DIR + "/backup_8k.tmp");
        System.out.println("  小文件校验: " + (ok ? "✅ 一致" : "❌ 不一致"));

        // 生成大文件（10MB）做真正的压力测试
        System.out.println();
        System.out.println("===== 大文件测试 (10 MB) =====");
        generateBigFile(BIG_FILE, 10);
        System.out.println();

        timeIt("逐字节复制         ", () -> copyWithByteStream(BIG_FILE, BACKUP_DIR + "/big_byte.tmp"));
        timeIt("1KB 缓冲区         ", () -> copyWithBuffer(BIG_FILE, BACKUP_DIR + "/big_1k.tmp", 1024));
        timeIt("8KB 缓冲区         ", () -> copyWithBuffer(BIG_FILE, BACKUP_DIR + "/big_8k.tmp", 8192));
        timeIt("BufferedStream     ", () -> copyWithBufferedStream(BIG_FILE, BACKUP_DIR + "/big_buf.tmp",8192));

        // 验证大文件
        ok = verifyFiles(BIG_FILE, BACKUP_DIR + "/big_8k.tmp");
        System.out.println("  大文件校验: " + (ok ? "✅ 一致" : "❌ 不一致"));

        System.out.println();
        System.out.println("💡 对比逐字节 vs 8KB 缓冲的耗时差——你亲手证明了缓冲的意义。");
    }

    // ========== 脚手架 ==========

    private void timeIt(String label, IOTask task) {
        Instant start = Instant.now();
        try {
            task.run();
            Instant end = Instant.now();
            long ms = Duration.between(start, end).toMillis();
            System.out.println("  " + label + ": " + ms + " ms");
        } catch (IOException e) {
            System.out.println("  " + label + ": 失败 — " + e.getMessage());
        }
    }

    @FunctionalInterface
    interface IOTask {
        void run() throws IOException;
    }

    public static void main(String[] args) throws IOException {
        new FileBackup().benchmark();
    }
}
