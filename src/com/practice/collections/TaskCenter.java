package com.practice.collections;

import java.util.*;

/**
 * 【任务调度官·初阶】脚手架代码
 *
 * 用 Deque 实现一个任务调度中心：普通任务排队，紧急任务插队。
 *
 * 运行方式：
 *   javac -encoding UTF-8 -d out src/com/practice/collections/*.java
 *   java -cp out com.practice.collections.TaskCenter
 */
public class TaskCenter {

    // TODO ①：选 ArrayDeque 还是 LinkedList？追求最快速度
    private final Deque<String> tasks = new ArrayDeque<>();

    /**
     * 提交普通任务（排到队尾）
     *
     */
    public boolean checkInput(String str){
        if(str==null|| str.trim().isEmpty()){
            System.out.println("任务不能为空");
            return false;
        }
        return true;
    }

    public void submitTask(String taskName) {
        // TODO ②：null/空字符串防御
        if(!checkInput(taskName)){
            return ;
        }
        tasks.offerLast(taskName);


        // TODO ③：用哪个方法加到队尾？
    }

    /**
     * 提交紧急任务（插到队首）
     */
    public void submitUrgentTask(String taskName) {
        // TODO ④：null/空字符串防御
        if(!checkInput(taskName)){
            return ;
        }
        tasks.offerFirst(taskName);



        // TODO ⑤：用哪个方法插到队首？
    }

    /**
     * 预览下一个要处理的任务（不取出）
     * @return 任务名称，没有任务返回 null
     */
    public String peekNext() {
        // TODO ⑥：偷看队头
        return tasks.peekFirst();
    }

    /**
     * 处理下一个任务（取出队首）
     * @return 任务名称，没有任务返回 null
     */
    public String processNext() {
        // TODO ⑦：取出队头
         return tasks.pollFirst();

    }

    /**
     * 剩余任务数
     */
    public int pendingCount() {
        // TODO ⑧
        return tasks.size();
    }

    /**
     * 列出所有待处理任务（从队首到队尾）
     */
    public List<String> listAllTasks() {
        // TODO ⑨：遍历 Deque，保持顺序
        List<String>list=new ArrayList<>();
        tasks.forEach(list::add);

        return list;
    }

    // ========== 测试入口 ==========
    public static void main(String[] args) {
        TaskCenter center = new TaskCenter();

        // 提交普通任务
        center.submitTask("备份数据库");
        center.submitTask("生成周报");
        center.submitTask("发送邮件通知");

        // 插一个紧急任务
        center.submitUrgentTask("服务器宕机修复");

        // 再提交一个普通任务
        center.submitTask("更新SSL证书");

        System.out.println("待处理任务: " + center.listAllTasks());
        // 期望：[服务器宕机修复, 备份数据库, 生成周报, 发送邮件通知, 更新SSL证书]

        System.out.println("预览下一个: " + center.peekNext());
        // 期望：服务器宕机修复

        System.out.println("处理: " + center.processNext());
        // 期望：服务器宕机修复

        System.out.println("处理: " + center.processNext());
        // 期望：备份数据库

        System.out.println("剩余: " + center.pendingCount());
        // 期望：3
    }
}
