package com.practice.regex;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 炼金配方清洗师 —— 用正则表达式清洗脏数据。
 *
 * 运行方式：javac src/com/practice/regex/PotionCleaner.java && java -cp src com.practice.regex.PotionCleaner
 */
public class PotionCleaner {

    public static void main(String[] args) {
        // ==================== 挑战1：清洗配方名称 ====================
        System.out.println("=== 挑战1：清洗配方名称 ===");

        List<String> dirtyNames = List.of(
            "治疗药水!!!",
            "魔  力  药  水",
            "火焰-抗性-药剂",
            "  隐身药水...  ",
            "力量+++药水###"
        );

        for (String dirty : dirtyNames) {
            String clean = cleanPotionName(dirty);
            System.out.println("脏数据: \"" + dirty + "\" → 清洗后: \"" + clean + "\"");
        }

        // ==================== 挑战2：校验原料格式 ====================
        System.out.println("\n=== 挑战2：校验原料格式 ===");

        List<String> ingredients = List.of(
            "3x龙鳞",       // 合法
            "10x月光草",    // 合法
            "龙鳞x3",       // 不合法——数字不在前面
            "3-龙鳞",       // 不合法——分隔符不是 x
            "5x",           // 不合法——缺少原料名
            "x龙鳞",        // 不合法——缺少数量
            "abcx龙鳞"      // 不合法——数量不是数字
        );

        Pattern ingredientPattern = compileIngredientPattern();
        for (String ing : ingredients) {
            boolean valid = validateIngredient(ingredientPattern, ing);
            System.out.printf("  %-12s → %s%n", ing, valid ? "✅ 合法" : "❌ 不合法");
        }

        // ==================== 挑战3：提取配方中的数字 ====================
        System.out.println("\n=== 挑战3：提取配方中的数字 ===");

        List<String> descriptions = List.of(
            "将3份龙鳞研磨成粉，加入500ml清水，用80度温火熬煮2小时",
            "取10g凤凰羽毛，混合25ml月光精华，在100度下搅拌30分钟",
            "准备1.5升独角兽眼泪，倒入200克金粉，煮沸45秒"
        );

        for (String desc : descriptions) {
            List<Double> numbers = extractNumbers(desc);
            System.out.println("文本: " + desc);
            System.out.println("  提取到的数字: " + numbers);
        }

        System.out.println("\n🎉 三个挑战全部通过后，你就是【炼金配方清洗师】！");
    }

    // ================================================================
    // 挑战1：清洗配方名称
    // ================================================================

    /**
     * 清洗配方名称，使其符合标准格式。
     *
     * 要求：
     *   1. 去掉首尾空白字符
     *   2. 去掉所有连续标点（如 "!!!"、"###"、"---"、"..."）
     *   3. 将连续多个空格合并为一个空格
     *   4. 去掉所有非中文字符且非字母数字的字符（保留中文、字母、数字、空格）
     *
     * 示例：
     *   "治疗药水!!!"        → "治疗药水"
     *   "魔  力  药  水"     → "魔力药水"（或"魔 力 药 水"均可，看你多激进）
     *   "火焰-抗性-药剂"     → "火焰抗性药剂"
     *   "  隐身药水...  "    → "隐身药水"
     *
     * 提示：
     *   - String.trim() 只能去首尾，去不掉的交给正则
     *   - replaceAll 是你最好的朋友
     *   - 中文范围：\\u4e00-\\u9fa5
     */
    public static String cleanPotionName(String name) {
        return name.trim()
            .replaceAll("[!！.。~～+#-]+", "")
            .replaceAll("\\s+", "");
    }

    // ================================================================
    // 挑战2：校验原料格式
    // ================================================================

    /**
     * 编译原料格式的正则 Pattern。
     *
     * 合法格式：「数字 + x + 原料名」
     *   - 数字：至少一位正整数（1, 10, 999 都行）
     *   - 分隔符：必须是英文小写字母 x
     *   - 原料名：至少一个 Unicode 字母/中文/下划线字符
     *
     * 合法示例：3x龙鳞、10x月光草、1x凤凰羽毛、999x魔法粉尘
     * 不合法示例：龙鳞x3（数字在右）、3-龙鳞（分隔符不对）、5x（缺原料名）
     *
     * 提示：
     *   - \\d+ 匹配一个或多个数字
     *   - \\p{L} 匹配任意 Unicode 字母（含中文）
     *   - 考虑原料名中可能出现下划线（如 dragon_scale）
     */
    public static Pattern compileIngredientPattern() {
        // TODO: 返回编译后的 Pattern，匹配合法原料格式
        final Pattern EMAIL =  Pattern.compile("\\d+x[\\p{L}_]+");

        // 提示：使用 Pattern.compile("正则表达式")
        // 因为 matches() 隐含 ^...$，所以正则本身不需要加 ^ 和 $
        return EMAIL;
    }

    /**
     * 用预编译的 Pattern 校验原料字符串是否合法。
     */
    public static boolean validateIngredient(Pattern pattern, String ingredient) {
        // TODO: 用 pattern.matcher(ingredient).matches() 校验
        return pattern.matcher(ingredient).matches();

    }

    // ================================================================
    // 挑战3：提取配方中的数字
    // ================================================================

    /**
     * 从描述文本中提取所有整数。
     *
     * 示例：
     *   "将3份龙鳞研磨成粉，加入500ml清水" → [3, 500]
     *   "取10g凤凰羽毛，混合25ml月光精华"  → [10, 25]
     *
     * 提示：
     *   - 用 Pattern.compile 编译一个匹配"连续数字"的正则
     *   - 用 matcher.find() 循环提取，每次 matcher.group() 拿到匹配文本
     *   - Integer.parseInt() 转成 int
     */
    public static List<Double> extractNumbers(String text) {
        List<Double> list = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\d+(\\.\\d+)?");
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            list.add(Double.parseDouble(matcher.group()));
        }
        return list;
    }
}
