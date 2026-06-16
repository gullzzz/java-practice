package com.practice.object;

import java.util.Objects;

/**
 * 魔法卡牌 —— 一张拥有名称、费用、稀有度的卡牌。
 *
 * 当前状态：Object 的三个核心方法都还没重写。
 * 你的任务就是把下面三个 TODO 全部实现。
 */
public class Card {

    public enum Rarity {
        COMMON("普通"), RARE("稀有"), EPIC("史诗"), LEGENDARY("传说");

        private final String label;

        Rarity(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    private final String name;
    private final int cost;
    private final Rarity rarity;

    public Card(String name, int cost, Rarity rarity) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("卡牌名称不能为空");
        }
        if (cost < 0) {
            throw new IllegalArgumentException("费用不能为负数");
        }
        this.name = name;
        this.cost = cost;
        this.rarity = rarity;
    }

    public String getName() { return name; }
    public int getCost() { return cost; }
    public Rarity getRarity() { return rarity; }

    // ═══════════════════════════════════════════════════════════
    // TODO ①：重写 toString()
    //   让 println(card) 打印出人类可读的信息，例如：
    //   "火球术 (费用:3, 稀有度:稀有)"
    //
    //   现在不重写的话，打印出来是 "Card@2f4d3709" 这种天书。
    // ═══════════════════════════════════════════════════════════
    // TODO: 在这里重写 toString()
    @Override
    public String toString() {
        return (getName()+"("+ "费用:"+getCost()+","+"稀有度:" + getRarity().getLabel()+")");
    }

    // ═══════════════════════════════════════════════════════════
    // TODO ②：重写 equals(Object obj)
    //   让两张 "同名 + 同费用" 的牌被视为同一张牌。
    //   步骤：
    //     1. if (this == obj) return true  ← 同一个引用直接返回 true
    //     2. if (!(obj instanceof Card)) return false  ← 类型不对直接返回 false
    //     3. Card other = (Card) obj;  ← 安全转换
    //     4. 比较 name 和 cost（提示：用 Objects.equals(a, b) 防 null）
    //
    //   想一想：稀有度要不要参与相等判断？这取决于你的业务规则。
    // ═══════════════════════════════════════════════════════════
    // TODO: 在这里重写 equals(Object obj)

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Card other)) return false;
        if(other.getName().equals(this.getName())&&other.getCost()==this.getCost()) return true;
        return false;
    }


    // ═══════════════════════════════════════════════════════════
    // TODO ③：重写 hashCode()
    //   必须和 equals 用相同的字段！
    //   提示：用 Objects.hash(name, cost) 一行就能搞定。
    //
    //   如果你重写了 equals 但不重写 hashCode 会怎样？
    //   试一下：把两张 equals 为 true 的牌分别 put 进 HashMap，
    //   看 size() 是 1 还是 2。
    // ═══════════════════════════════════════════════════════════
    // TODO: 在这里重写 hashCode()

    @Override
    public int hashCode() {
        return Objects.hash(name,cost);
    }
}
