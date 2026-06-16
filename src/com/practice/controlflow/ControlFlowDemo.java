package com.practice.controlflow;

/**
 * 阶段2：控制流程
 * 涵盖：if/else、switch、for、while、do-while、break/continue
 */
public class ControlFlowDemo {
    public static void main(String[] args) {
        System.out.println("========== Java 控制流程 ==========\n");

        ifElseDemo();
        switchDemo();
        forLoopDemo();
        whileDemo();
        breakContinueDemo();
        enhancedSwitchDemo();  // Java 14+
    }

    static void ifElseDemo() {
        System.out.println("--- 1. if / else if / else ---");

        int score = 85;

        if (score >= 90) {
            System.out.println("score=" + score + " → 优秀");
        } else if (score >= 80) {
            System.out.println("score=" + score + " → 良好");
        } else if (score >= 60) {
            System.out.println("score=" + score + " → 及格");
        } else {
            System.out.println("score=" + score + " → 不及格");
        }

        // 嵌套 if
        boolean isWeekend = true;
        if (score >= 80) {
            if (isWeekend) {
                System.out.println("周末出去庆祝一下！");
            }
        }

        // if 简写（单行可省略大括号，但不推荐）
        if (score == 100) System.out.println("满分！");

        System.out.println();
    }

    static void switchDemo() {
        System.out.println("--- 2. switch（传统写法） ---");

        int dayOfWeek = 3;

        switch (dayOfWeek) {
            case 1:
                System.out.println("星期一 → 新的一周！");
                break;
            case 2:
                System.out.println("星期二");
                break;
            case 3:
                System.out.println("星期三 → 一周过半！");
                break;
            case 4:
                System.out.println("星期四");
                break;
            case 5:
                System.out.println("星期五 → 即将周末！");
                break;
            case 6:
            case 7:
                System.out.println("周末！");
                break;
            default:
                System.out.println("无效的星期");
        }

        // 没有 break 会发生什么？——穿透（fall-through）
        System.out.print("case穿透示例: ");
        int n = 2;
        switch (n) {
            case 1: System.out.print("A");
            case 2: System.out.print("B");  // 没有break，继续执行
            case 3: System.out.print("C");
        }
        System.out.println(" ← 所有case都被执行了（因为没有break）");
        System.out.println();
    }

    static void forLoopDemo() {
        System.out.println("--- 3. for 循环 ---");

        // 基本 for
        System.out.print("1到5: ");
        for (int i = 1; i <= 5; i++) {
            System.out.print(i + " ");
        }
        System.out.println();

        // 倒序
        System.out.print("5到1: ");
        for (int i = 5; i >= 1; i--) {
            System.out.print(i + " ");
        }
        System.out.println();

        // 步长为2
        System.out.print("1-10的奇数: ");
        for (int i = 1; i <= 10; i += 2) {
            System.out.print(i + " ");
        }
        System.out.println();

        // 嵌套循环：九九乘法表
        System.out.println("九九乘法表:");
        for (int i = 1; i <= 9; i++) {
            for (int j = 1; j <= i; j++) {
                System.out.printf("%d×%d=%-3d", j, i, i * j);
            }
            System.out.println();
        }

        System.out.println();
    }

    static void whileDemo() {
        System.out.println("--- 4. while 与 do-while ---");

        // while：先判断，再执行
        System.out.print("while 1到5: ");
        int count = 1;
        while (count <= 5) {
            System.out.print(count + " ");
            count++;
        }
        System.out.println();

        // do-while：先执行，再判断（至少执行一次）
        System.out.print("do-while（至少一次）: ");
        int x = 10;
        do {
            System.out.print(x + " ");
            x++;
        } while (x <= 5);  // 条件为false，但已经执行了一次
        System.out.println("← 虽然条件不满足，但执行了一次");

        System.out.println();
    }

    static void breakContinueDemo() {
        System.out.println("--- 5. break 与 continue ---");

        // break：跳出循环
        System.out.print("break示例（遇到5就停止）: ");
        for (int i = 1; i <= 10; i++) {
            if (i == 5) break;
            System.out.print(i + " ");
        }
        System.out.println();

        // continue：跳过本次，继续下一次
        System.out.print("continue示例（跳过偶数）: ");
        for (int i = 1; i <= 10; i++) {
            if (i % 2 == 0) continue;
            System.out.print(i + " ");
        }
        System.out.println();

        // 带标签的 break（跳出多层循环）
        System.out.print("标签break（外层i=3时跳出）: ");
        outer:
        for (int i = 1; i <= 5; i++) {
            for (int j = 1; j <= 5; j++) {
                if (i == 3) break outer;  // 跳出外层循环
                System.out.print("(" + i + "," + j + ") ");
            }
        }
        System.out.println();
        System.out.println();
    }

    static void enhancedSwitchDemo() {
        System.out.println("--- 6. switch 表达式（Java 14+ 新写法） ---");

        int day = 3;
        // 箭头语法，不需要 break
        String type = switch (day) {
            case 1, 2, 3, 4, 5 -> "工作日";
            case 6, 7 -> "周末";
            default -> "无效";
        };
        System.out.println("星期" + day + " → " + type);

        // switch 作为表达式返回值（yield）
        String desc = switch (day) {
            case 1 -> "周一综合症";
            case 5 -> {
                System.out.println("  (多行代码块, 使用yield返回)");
                yield "TGIF!";
            }
            default -> "普通的一天";
        };
        System.out.println("描述: " + desc);
        System.out.println();
    }
}
