package com.practice.exceptions;

/**
 * 魔法银行安全系统 —— 转账挑战
 *
 * 场景：银行有三个账户，转账时余额不足必须精确报告并让调用方决定如何处理。
 */
public class BankTransfer {

    // ---------- 账户类 ----------
    static class Account {
        String name;
        int balance;

        Account(String name, int balance) {
            this.name = name;
            this.balance = balance;
        }
    }

    public static void main(String[] args) throws InsufficientBalanceException {
        Account alice = new Account("Alice", 500);
        Account bob   = new Account("Bob",   100);
        Account bank  = new Account("Bank",  999);

        // ---------- 核心挑战：尝试三笔转账 ----------
        // TODO #1: 下面三行调用会导致编译错误——因为 transfer 方法还没声明 throws
        // 等你完成了步骤，取消注释，验证每笔转账的结果

            safeTransfer(alice, bob, -100);
            safeTransfer(alice, bob, 200);   // 应该成功：Alice 500→300, Bob 100→300
            safeTransfer(bob, alice, 500);   // 应该失败：Bob 余额不足
            safeTransfer(bank, bob, 1000);// 应该失败：Bank 余额不足


        System.out.println("=== 转账结束 ===");
        System.out.println(alice.name + " 余额: " + alice.balance);
        System.out.println(bob.name   + " 余额: " + bob.balance);
        System.out.println(bank.name  + " 余额: " + bank.balance);
    }

    /**
     * 转账方法
     *
     * TODO #2: 实现转账逻辑：
     *   1. 检查 from.balance 是否足够（余额不足 → 抛出 InsufficientBalanceException）
     *   2. 执行扣款 from.balance -= amount
     *   3. 执行加款 to.balance += amount
     *   4. 打印转账成功信息
     *
     * TODO #3: 在方法签名上加 throws 声明
     *
     *
     */
    public  static  void safeTransfer(Account from, Account to, int amount){
        try{
            transfer(from,to,amount);
        }catch (InsufficientBalanceException e){
            System.out.println(e.getMessage());
        }

    }

    public static void transfer(Account from, Account to, int amount)throws InsufficientBalanceException {
            if (amount <= 0) {
                throw new IllegalArgumentException("转账金额必须大于0，实际: " + amount);
            }
            if(from.balance<amount){
                throw new  InsufficientBalanceException(from.name+"给"+to.name+"转账,余额不足,需要"+amount+"实际差"+(amount-from.balance));
            }
            from.balance=from.balance- amount ;
            to.balance=to.balance+amount;
        // TODO: 余额不足时，throw new InsufficientBalanceException(消息);
        // 消息里要包含：谁给谁转账、需要多少、实际差多少
    }
    public static class InsufficientBalanceException extends  RuntimeException{
        public InsufficientBalanceException(String message) {
            super(message);
        }
    }
}
