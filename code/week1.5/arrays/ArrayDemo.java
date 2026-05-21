package arrays;

// Arrays 是 java.util 包里的数组工具类
import java.util.Arrays;
import java.util.List;

/**
 * Week 1.5 §01 配套示例：数组 + 可变参数
 */
public class ArrayDemo {

    public static void main(String[] args) {
        onedDemo();
        twodDemo();
        arraysUtil();
        varargsDemo();
    }

    /** 一维数组 */
    static void onedDemo() {
        System.out.println("\n=== 一维数组 ===");

        // 方式 1：new + 默认值
        int[] a = new int[5];
        // 数组元素默认是类型的零值
        System.out.println("默认值: " + Arrays.toString(a));    // [0, 0, 0, 0, 0]

        // 方式 2：花括号字面量
        int[] arr = {10, 20, 30, 40, 50};

        // 数组长度是 .length 属性（不是 size() 方法）
        System.out.println("长度 = " + arr.length);

        // 下标从 0 开始
        System.out.println("arr[0] = " + arr[0]);
        arr[0] = 99;
        System.out.println("修改后: " + Arrays.toString(arr));

        // 越界会抛 ArrayIndexOutOfBoundsException
        try {
            int x = arr[100];
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("越界异常: " + e.getMessage());
        }
    }

    /** 多维数组 */
    static void twodDemo() {
        System.out.println("\n=== 二维数组 ===");

        // 3 行 4 列
        int[][] grid = new int[3][4];
        grid[1][2] = 99;
        System.out.println("二维: " + Arrays.deepToString(grid));

        // 字面量
        int[][] m = {
            {1, 2, 3},
            {4, 5, 6}
        };
        // deepToString 处理嵌套数组
        System.out.println("字面量: " + Arrays.deepToString(m));

        // 锯齿数组：每行长度不同
        int[][] jagged = new int[3][];
        jagged[0] = new int[]{1};
        jagged[1] = new int[]{1, 2};
        jagged[2] = new int[]{1, 2, 3};
        System.out.println("锯齿: " + Arrays.deepToString(jagged));

        // 双层 for 遍历
        System.out.println("遍历 m:");
        for (int i = 0; i < m.length; i++) {
            // m[i] 是第 i 行（int[]）
            for (int j = 0; j < m[i].length; j++) {
                System.out.print(m[i][j] + " ");
            }
            System.out.println();
        }
    }

    /** Arrays 工具类常用方法 */
    static void arraysUtil() {
        System.out.println("\n=== Arrays 工具类 ===");

        int[] arr = {3, 1, 4, 1, 5, 9, 2, 6};

        // 排序：原地修改 arr
        Arrays.sort(arr);
        System.out.println("排序: " + Arrays.toString(arr));

        // 复制前 5 个
        int[] copy = Arrays.copyOf(arr, 5);
        System.out.println("copyOf 5: " + Arrays.toString(copy));

        // 复制 [2, 5) 区间
        int[] slice = Arrays.copyOfRange(arr, 2, 5);
        System.out.println("copyOfRange [2,5): " + Arrays.toString(slice));

        // 填充
        int[] zeros = new int[5];
        Arrays.fill(zeros, 7);
        System.out.println("fill 7: " + Arrays.toString(zeros));

        // 二分查找（前提：已排序）
        int idx = Arrays.binarySearch(arr, 4);
        System.out.println("binarySearch 4 → 索引 " + idx);

        // 比较：== 比较引用，equals 比较内容
        int[] x = {1, 2, 3};
        int[] y = {1, 2, 3};
        System.out.println("x == y: " + (x == y));                  // false
        System.out.println("Arrays.equals(x, y): " + Arrays.equals(x, y)); // true

        // 数组转 List
        // 注意：返回的 List 是固定大小，不能 add / remove
        List<String> list = Arrays.asList("a", "b", "c");
        System.out.println("asList: " + list);
    }

    /** 可变参数 varargs */
    static void varargsDemo() {
        System.out.println("\n=== 可变参数 ===");

        // 调用任意个 int 都行
        System.out.println("sum() = " + sum());                 // 0
        System.out.println("sum(1) = " + sum(1));               // 1
        System.out.println("sum(1,2,3) = " + sum(1, 2, 3));     // 6
        // 也能传数组
        System.out.println("sum(arr) = " + sum(new int[]{10, 20, 30}));   // 60

        // 找最大值
        System.out.println("max = " + max(3, 1, 4, 1, 5, 9, 2, 6));    // 9
    }

    /**
     * 可变参数：方法签名里写 int... nums
     * 调用时可以传 0 个、1 个、N 个 int，或一个 int[]
     * 方法体内 nums 类型就是 int[]
     */
    static int sum(int... nums) {
        int total = 0;
        for (int n : nums) total += n;
        return total;
    }

    /** 找最大值 */
    static int max(int... nums) {
        if (nums.length == 0) throw new IllegalArgumentException("至少一个参数");
        int best = nums[0];
        for (int n : nums) if (n > best) best = n;
        return best;
    }
}
