package com.practice.regex;

/**
 * 【药剂配方清洗师·初阶挑战】
 *
 * 你的魔法工坊收到了各地采集来的配方数据，格式一塌糊涂。
 * 今天的任务是实现配方名称的清洗引擎。
 */
public class PotionRecipeCleaner {

    /**
     * 清洗配方名称，三步流水线：
     * ① 去除首尾空白字符
     * ② 把连续多个空白字符压缩成一个空格
     * ③ 删除名称末尾多余的标点装饰符（!！.。~～ 等）
     *
     * @param rawName 原始采集到的名称（可能含多余空格、标点）
     * @return 清洗后的干净名称
     */
    public static String cleanName(String rawName) {
        // TODO Step 1: 去除首尾空白字符（空格、Tab等）
        // 提示：String 有个现成方法，一行搞定，你之前处理用户输入时用过

        // TODO Step 2: 把连续多个空白字符压缩成一个空格
        // 提示：replaceAll + 正则 "\s+" → 替换成 " "
        // 注意：Java 字符串里 \ 需要转义，所以你要写 "\\s+"

        // TODO Step 3: 删除名称末尾多余的标点符号
        // 提示：replaceAll + 字符集 "[!！.。~～]+" + 末尾锚点 "$"
        // 末尾标点可能重复（如 "药水!!!"），所以需要量词 +
        // 如果末尾还有其他装饰符（比如【】），试着也加到字符集里

        return ""; // TODO: 返回清洗后的字符串
    }

    public static void main(String[] args) {
        String[] dirtyNames = {
            "  治疗药水!!!  ",
            "魔力  药水",
            "生命药水【高级】",
            "火焰药剂~~~",
            "  冰霜  药剂!!  ",
            "解毒 剂...",
        };

        System.out.println("=== 配方名称清洗引擎 ===\n");

        for (String name : dirtyNames) {
            String cleaned = cleanName(name);
            System.out.println("清洗前: [" + name + "]");
            System.out.println("清洗后: [" + cleaned + "]");
            System.out.println("---");
        }

        // TODO Bonus: 你还能想到什么边界情况？
        // 比如：空字符串、null、全是标点的名称……
    }
}
