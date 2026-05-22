package s03_encapsulation;

public class EncapsulationDemo {

    public static void main(String[] args) {

        // ====== 封装：BankAccount ======
        System.out.println("\n=== 封装 BankAccount ===");
        BankAccount acc = new BankAccount(100);
        acc.deposit(50);                      // 走方法，会校验
        acc.withdraw(30);
        System.out.println("余额 = " + acc.getBalance());      // 120

        // acc.balance = -999;                // ❌ 编译错误：balance 是 private，看不到

        // setter 里的校验会拒绝非法操作
        try {
            acc.withdraw(99999);              // 余额不够
        } catch (IllegalArgumentException e) {
            System.out.println("捕获到: " + e.getMessage());
        }

        // ====== 实例 vs 静态字段 ======
        System.out.println("\n=== 实例 vs 静态 ===");
        Counter c1 = new Counter();
        Counter c2 = new Counter();
        Counter c3 = new Counter();

        // 每个对象的 instanceCount 各自是 1
        System.out.println("c1.instanceCount = " + c1.getInstanceCount());     // 1
        System.out.println("c2.instanceCount = " + c2.getInstanceCount());     // 1

        // totalCount 是共享的，3 个对象都 +1 了
        // 注意：用类名 Counter.getTotalCount() 调，不是 c1.getTotalCount()
        System.out.println("Counter.getTotalCount() = " + Counter.getTotalCount());   // 3

        // ====== 静态工具类 ======
        System.out.println("\n=== 静态工具类 ===");
        System.out.println("isBlank('  ')? " + StringUtils.isBlank("  "));     // true
        System.out.println("reverse('hello') = " + StringUtils.reverse("hello"));  // olleh
        System.out.println("isEven(4)? " + StringUtils.isEven(4));              // true
    }
}
