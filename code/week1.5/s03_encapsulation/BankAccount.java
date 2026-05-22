package s03_encapsulation;

/**
 * 经典封装案例：银行账户
 *
 * 关键点：
 *  - balance 字段 private，外面看不到也改不了
 *  - 通过 deposit / withdraw 方法间接操作，方法里有校验
 *  - 不让外界把余额改成负数
 */
public class BankAccount {

    // private 字段：只有本类能访问
    private double balance;

    /** 构造器：开户初始余额 */
    public BankAccount(double initial) {
        // 初始化时就要校验
        if (initial < 0) {
            // 主动抛异常 = 拒绝创建非法对象
            throw new IllegalArgumentException("初始余额不能为负: " + initial);
        }
        this.balance = initial;
    }

    /** getter：让外界能"读"余额 */
    public double getBalance() {
        return balance;
    }

    // 注意：我们**故意不写 setBalance**
    // 不允许外界随便改余额，只能通过 deposit / withdraw 走业务流程

    /** 存钱 */
    public void deposit(double amount) {
        // 业务校验：必须 > 0
        if (amount <= 0) {
            throw new IllegalArgumentException("存款金额必须 > 0");
        }
        balance += amount;       // 等价于 balance = balance + amount
    }

    /** 取钱 */
    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("取款金额必须 > 0");
        }
        if (amount > balance) {
            throw new IllegalArgumentException("余额不足，当前 " + balance);
        }
        balance -= amount;
    }
}
