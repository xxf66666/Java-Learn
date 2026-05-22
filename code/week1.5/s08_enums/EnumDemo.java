package s08_enums;

public class EnumDemo {

    /** 最简枚举：只是几个值 */
    enum Color { RED, GREEN, BLUE }

    /** 带字段方法的枚举 */
    enum OrderStatus {
        // 每个枚举值后括号传构造器参数
        DRAFT("草稿", 1),
        APPROVED("已审核", 2),
        DONE("已完成", 3),
        VOIDED("已作废", 9);

        // 字段 final（枚举值是单例，不能改）
        private final String label;
        private final int weight;

        // 构造器不能 public（默认私有）
        OrderStatus(String label, int weight) {
            this.label = label;
            this.weight = weight;
        }

        public String getLabel() { return label; }
        public int getWeight() { return weight; }
    }

    /** 实现接口的枚举：每个值有自己的行为 */
    interface Operation {
        int apply(int a, int b);
    }

    enum Op implements Operation {
        ADD {
            @Override public int apply(int a, int b) { return a + b; }
        },
        SUB {
            @Override public int apply(int a, int b) { return a - b; }
        },
        MUL {
            @Override public int apply(int a, int b) { return a * b; }
        };
    }

    public static void main(String[] args) {
        basicEnum();
        statusEnum();
        switchEnum();
        opEnum();
    }

    static void basicEnum() {
        System.out.println("\n=== 基础枚举 ===");

        Color c = Color.RED;
        // == 比较枚举值是 OK 的
        System.out.println("c == RED? " + (c == Color.RED));

        // 拿所有枚举值
        for (Color v : Color.values()) {
            // name 是字符串名；ordinal 是位置索引
            System.out.println("  " + v.name() + " idx=" + v.ordinal());
        }

        // 字符串 → 枚举
        Color parsed = Color.valueOf("GREEN");
        System.out.println("valueOf: " + parsed);

        // 不存在的名字会抛异常
        try {
            Color.valueOf("PURPLE");
        } catch (IllegalArgumentException e) {
            System.out.println("找不到 PURPLE: " + e.getMessage());
        }
    }

    static void statusEnum() {
        System.out.println("\n=== 带字段枚举 ===");

        OrderStatus s = OrderStatus.APPROVED;
        System.out.println(s.name() + " 中文=" + s.getLabel() + " 权重=" + s.getWeight());

        // 遍历找特定的
        System.out.println("权重 > 1 的状态:");
        for (OrderStatus os : OrderStatus.values()) {
            if (os.getWeight() > 1) {
                System.out.println("  " + os.getLabel());
            }
        }
    }

    static void switchEnum() {
        System.out.println("\n=== switch 枚举 ===");

        OrderStatus s = OrderStatus.DRAFT;

        // switch 表达式 + 枚举：case 不用写前缀
        // 编译器自动检查所有枚举值是否都覆盖到
        String desc = switch (s) {
            case DRAFT    -> "刚创建的草稿";
            case APPROVED -> "审核通过";
            case DONE     -> "已完成";
            case VOIDED   -> "废了";
        };
        System.out.println(desc);
    }

    static void opEnum() {
        System.out.println("\n=== 实现接口的枚举 ===");

        // 调用：枚举值.方法
        System.out.println("2 + 3 = " + Op.ADD.apply(2, 3));
        System.out.println("2 * 3 = " + Op.MUL.apply(2, 3));

        // 遍历所有运算符
        for (Op op : Op.values()) {
            System.out.println("  " + op + ": 10 op 2 = " + op.apply(10, 2));
        }
    }
}
