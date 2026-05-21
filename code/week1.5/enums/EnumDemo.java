package enums;

// EnumMap 是专门给 Enum key 设计的高性能 Map
import java.util.EnumMap;
import java.util.EnumSet;

/**
 * Week 1.5 §04 配套示例：枚举
 */
public class EnumDemo {

    /** 简单枚举：只是几个值 */
    enum Color {
        RED, GREEN, BLUE
    }

    /**
     * 带字段和方法的枚举：本质是个特殊类
     */
    enum OrderStatus {
        // 每个枚举值后面括号传构造器参数
        DRAFT("草稿", 1),
        APPROVED("已审核", 2),
        DONE("已完成", 3),
        VOIDED("已作废", 9);

        // 字段 final（一旦确定不变）
        private final String label;
        private final int weight;

        // 构造器不能 public（枚举值只能在这里创建）
        OrderStatus(String label, int weight) {
            this.label = label;
            this.weight = weight;
        }

        public String getLabel() { return label; }
        public int getWeight() { return weight; }
    }

    /**
     * 实现接口的枚举：每个枚举值有自己的行为
     */
    interface Operation {
        int apply(int a, int b);
    }

    enum Op implements Operation {
        // 每个枚举值后面用 { } 重写接口方法
        ADD {
            @Override public int apply(int a, int b) { return a + b; }
        },
        SUB {
            @Override public int apply(int a, int b) { return a - b; }
        },
        MUL {
            @Override public int apply(int a, int b) { return a * b; }
        },
        DIV {
            @Override public int apply(int a, int b) {
                if (b == 0) throw new ArithmeticException("除数为 0");
                return a / b;
            }
        };
    }

    public static void main(String[] args) {
        basicEnum();
        statusEnum();
        operationEnum();
        switchEnum();
        enumMapDemo();
    }

    /** 基础枚举 */
    static void basicEnum() {
        System.out.println("\n=== 基础枚举 ===");

        Color c = Color.RED;
        // == 比较枚举值是 OK 的（编译器保证同一对象）
        System.out.println("c == RED? " + (c == Color.RED));

        // values() 返回所有枚举值
        for (Color v : Color.values()) {
            // name() 返回枚举名字符串；ordinal() 返回索引
            System.out.println("  " + v.name() + " (ordinal=" + v.ordinal() + ")");
        }

        // 字符串 → 枚举
        Color parsed = Color.valueOf("GREEN");
        System.out.println("parsed = " + parsed);

        try {
            Color bad = Color.valueOf("PURPLE");      // 不存在的枚举
        } catch (IllegalArgumentException e) {
            System.out.println("valueOf 失败: " + e.getMessage());
        }
    }

    /** 带字段枚举 */
    static void statusEnum() {
        System.out.println("\n=== 带字段枚举 ===");

        OrderStatus s = OrderStatus.APPROVED;
        System.out.println(s.name() + " label=" + s.getLabel() + " weight=" + s.getWeight());

        // 遍历找 weight > 1 的
        for (OrderStatus os : OrderStatus.values()) {
            if (os.getWeight() > 1) {
                System.out.println("  " + os.getLabel() + " (" + os.getWeight() + ")");
            }
        }
    }

    /** 实现接口的枚举：策略模式 */
    static void operationEnum() {
        System.out.println("\n=== 实现接口枚举 ===");

        // 直接当函数用
        System.out.println("2 + 3 = " + Op.ADD.apply(2, 3));
        System.out.println("2 * 3 = " + Op.MUL.apply(2, 3));

        // 按枚举遍历
        for (Op op : Op.values()) {
            // 注意 DIV 除数 0 会抛异常，这里用 1 避开
            System.out.println("  " + op.name() + ": " + op.apply(10, 2));
        }
    }

    /** switch 配合枚举 */
    static void switchEnum() {
        System.out.println("\n=== switch 枚举 ===");

        OrderStatus s = OrderStatus.DRAFT;

        // switch 表达式（Java 14+）
        // case 不用写枚举类型前缀（编译器从 switch 表达式推断）
        String result = switch (s) {
            case DRAFT    -> "草稿状态";
            case APPROVED -> "已审核状态";
            case DONE     -> "已完成状态";
            case VOIDED   -> "已作废状态";
            // 不写 default：编译器会检查所有枚举值都被覆盖
        };
        System.out.println(result);
    }

    /** EnumMap / EnumSet 性能优化 */
    static void enumMapDemo() {
        System.out.println("\n=== EnumMap / EnumSet ===");

        // EnumMap：key 是枚举时用这个，比 HashMap 快几倍
        // 构造器要传 Class
        EnumMap<OrderStatus, String> map = new EnumMap<>(OrderStatus.class);
        map.put(OrderStatus.DRAFT, "刚创建");
        map.put(OrderStatus.APPROVED, "已审核");
        System.out.println("EnumMap: " + map);

        // EnumSet：枚举专用 Set
        // of(...) 创建包含指定枚举值的 Set
        EnumSet<OrderStatus> active = EnumSet.of(OrderStatus.DRAFT, OrderStatus.APPROVED);
        // allOf 包含所有
        EnumSet<OrderStatus> all = EnumSet.allOf(OrderStatus.class);
        System.out.println("active: " + active);
        System.out.println("all: " + all);
    }
}
