package com.practice.object;

import java.util.HashMap;
import java.util.Map;

/**
 * 卡牌图鉴系统 —— 用 HashMap 管理你的卡牌收藏。
 *
 * 运行前请确保在 Card.java 中实现了三个 TODO：
 *   ① toString()  ② equals()  ③ hashCode()
 *
 * 练习步骤：
 *   1. 先不实现任何 TODO，直接运行 —— 感受 Object 默认行为的"坑"
 *   2. 实现 toString()，再运行 —— 打印变好看了
 *   3. 实现 equals() 但不实现 hashCode()，再运行 —— 观察 HashMap 的诡异行为
 *   4. 最后实现 hashCode()，再运行 —— 一切正常！
 */
public class CardCollectionDemo {

    public static void main(String[] args) {
        System.out.println("=== 魔法卡牌图鉴系统 v1.0 ===\n");

        // ── 第1关：toString 测试 ──
        System.out.println(">>> 第1关：打印卡牌信息");
        System.out.println("    如果看到 Card@xxxxx 乱码，说明 toString() 还没重写\n");

        Card fireball = new Card("火球术", 3, Card.Rarity.COMMON);
        Card blizzard = new Card("暴风雪", 7, Card.Rarity.EPIC);
        Card anotherFireball = new Card("火球术", 3, Card.Rarity.COMMON);

        System.out.println("    卡牌1: " + fireball);
        System.out.println("    卡牌2: " + blizzard);
        System.out.println("    卡牌3: " + anotherFireball);

        // ── 第2关：equals 测试 ──
        System.out.println("\n>>> 第2关：判断两张牌是否相同");
        System.out.println("    fireball 和 anotherFireball 同名同费用，应该相等\n");

        System.out.println("    fireball == anotherFireball ? " + (fireball == anotherFireball));
        System.out.println("    fireball.equals(anotherFireball) ? " + fireball.equals(anotherFireball));

        if (!fireball.equals(anotherFireball)) {
            System.out.println("\n    [WARN] equals 返回 false！默认的 equals 在比较内存地址");
            System.out.println("    提示：你需要重写 equals()，按 name + cost 比较内容");
        }

        // ── 第3关：hashCode 与 HashMap 测试 ──
        System.out.println("\n>>> 第3关：把卡牌装进 HashMap");

        Map<Card, Integer> collection = new HashMap<>();
        collection.put(fireball, 2);
        collection.put(blizzard, 1);
        collection.put(anotherFireball, 3);

        System.out.println("    HashMap 中的条目数: " + collection.size());
        System.out.println("    预期: 2（火球术 和 暴风雪）");
        System.out.println("    火球术的最终数量: " + collection.get(fireball));

        if (collection.size() != 2) {
            System.out.println("\n    [WARN] HashMap 把两张火球术当成了不同的 key！");
            System.out.println("    这说明 equals() 或 hashCode() 还没正确重写");
            System.out.println("    回忆 Object 契约：equals 为 true → hashCode 必须相等");
        }

        // ── 遍历展示 ──
        System.out.println("\n>>> 当前图鉴：");
        for (Map.Entry<Card, Integer> entry : collection.entrySet()) {
            System.out.println("    " + entry.getKey() + " x" + entry.getValue());
        }
    }
}
