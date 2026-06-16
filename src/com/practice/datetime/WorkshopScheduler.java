package com.practice.datetime;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * 魔法工坊·日程管家
 *
 * 功能：添加活动、检测时间冲突、列出日程
 */
public class WorkshopScheduler {

    // 每个活动记录：名称、日期、开始时间、结束时间
    static class Workshop {
        String name;
        LocalDate date;
        LocalTime startTime;
        LocalTime endTime;

        Workshop(String name, LocalDate date, LocalTime startTime, int durationMinutes) {
            this.name = name;
            this.date = date;
            this.startTime = startTime;
            // TODO ①：用 startTime.plusMinutes(durationMinutes) 计算 endTime
            // （提示：LocalTime.plusMinutes() 返回新的 LocalTime）
            endTime=startTime.plusMinutes(durationMinutes);
        }


        @Override
        public String toString() {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");
            return String.format("[%s] %s  %s - %s",
                    date, name, startTime.format(fmt), endTime.format(fmt));
        }
    }

    private final List<Workshop> schedule = new ArrayList<>();

    /**
     * 添加活动——先检查冲突，没有冲突才加入
     * @return true=添加成功，false=存在时间冲突
     */
    public boolean addWorkshop(String name, LocalDate date, LocalTime startTime, int durationMinutes) {
        Workshop newOne = new Workshop(name, date, startTime, durationMinutes);

        Objects.requireNonNull(name, "活动名不能为null");
        Objects.requireNonNull(date, "日期不能为null");
        // TODO ②：遍历已有 schedule，检查 newOne 是否与任一已有活动冲突

        for(Workshop workshop : schedule) {
            if(newOne.date.equals(workshop.date)) {
                boolean noConflict = newOne.endTime.compareTo(workshop.startTime) <= 0
                        || newOne.startTime.compareTo(workshop.endTime) >= 0;
                if (!noConflict) {
                    System.out.println("[冲突]{" + newOne.name + "}与{" + workshop.name + "}时间冲突");
                    return false;
                }
            }



//            if(newOne.date.equals(workshop.date)) {
//                if(newOne.startTime.isAfter(workshop.startTime)){
//                    if(newOne.startTime.isBefore(workshop.endTime)){
//                        System.out.println("[冲突]{"+newOne.name+"}与{"+workshop.name+"}时间冲突");
//                        return false;
//                    }
//                }else{
//                    if(workshop.startTime.isBefore(newOne.endTime)){
//                        System.out.println("[冲突]{"+newOne.name+"}与{"+workshop.name+"}时间冲突");
//                        return false;
//                    }
//                }
//
//
//            }


        }
        // 冲突条件：同一天 且 时间段有重叠
        // 同一天用 date.equals(w.date) 判断
        // 重叠逻辑参考你刚才的思路：
        //   找早开始的，把它.plusMinutes() 和晚开始.startTime 比
        //
        // 提示：LocalTime.isBefore() 看谁先开始
        // 如果冲突，打印 "[冲突] {新活动} 与 {已有活动} 时间重叠！" 并 return false

        schedule.add(newOne);
        System.out.println("[已添加] " + newOne);
        return true;
    }

    /**
     * 按日期+时间排序，列出所有活动
     */
    public void listAll() {
        if (schedule.isEmpty()) {
            System.out.println("(日程为空)");
            return;
        }
        Comparator<Workshop> c = Comparator.comparing(w -> w.date);
        schedule.sort(c.thenComparing(w -> w.startTime));

        // TODO ③：对 schedule 排序
        // 排序规则：先按日期，同日期的按开始时间
        // 提示：用 Collections.sort() + Comparator.comparing()
        //   Comparator.comparing(w -> w.date) 再 .thenComparing(w -> w.startTime)


        System.out.println("===== 魔法工坊日程表 =====");
        for (Workshop w : schedule) {
            System.out.println("  " + w);
        }
    }

    /**
     * 计算从今天到某个活动的倒计时天数
     */
    public long daysUntil(int index) {
        // TODO ④（可选挑战）：用 ChronoUnit.DAYS.between(today, w.date) 实现
        Workshop w=schedule.get(index);
        return ChronoUnit.DAYS.between(LocalDate.now(), w.date);

        // 如果活动日期已过，返回负数

    }

    // ============ 下面是测试入口，不用改 ============

    public static void main(String[] args) {
        WorkshopScheduler ws = new WorkshopScheduler();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        System.out.println("=== 魔法工坊日程管家启动 ===\n");

        // 测试数据
        LocalDate today = LocalDate.now();

        // 场景1：正常添加
        ws.addWorkshop("魔药课", today, LocalTime.of(9, 0), 90);
        ws.addWorkshop("炼金术实验", today, LocalTime.of(10, 30), 120);
        ws.addWorkshop("扫帚飞行训练", today, LocalTime.of(14, 0), 60);

        // 场景2：尝试添加冲突活动——应该被拒绝
        System.out.println();
        ws.addWorkshop("魔法史（冲突测试）", today, LocalTime.of(10, 0), 60);

        // 场景3：紧挨着不冲突的——应该成功
        System.out.println();
        ws.addWorkshop("魔法史（紧接）", today, LocalTime.of(12, 30), 90);

        // 打印全日程
        System.out.println();
        ws.listAll();
    }
}
