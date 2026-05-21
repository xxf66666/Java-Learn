package generic;

import java.util.ArrayList;
import java.util.List;
// 当栈空时尝试 pop / peek 抛这个异常
import java.util.NoSuchElementException;

/**
 * 自己写的泛型栈，理解 &lt;T&gt; 怎么用。
 * （生产环境用 java.util.ArrayDeque 即可，这里只是练手）
 *
 * 泛型 = 编译期类型参数
 * <T> 在类名后声明：表示这个类带一个类型参数 T
 * 使用时填具体类型：MyStack<String> / MyStack<Integer>
 */
public class MyStack<T> {

    // private final + 内部初始化：保证 data 引用不会被改（指向同一个 ArrayList）
    // 但 ArrayList 内容可以变（final 只锁引用不锁内容）
    private final List<T> data = new ArrayList<>();

    // 入栈：把元素放到 List 末尾
    public void push(T item) {
        data.add(item);
    }

    // 出栈：取出末尾元素并删除
    public T pop() {
        if (isEmpty()) {
            // 主动抛异常：栈空还要 pop 是调用方逻辑错误
            throw new NoSuchElementException("栈是空的");
        }
        // remove(index) 按下标删除，并返回被删的元素
        // size() - 1 是末尾下标
        return data.remove(data.size() - 1);
    }

    // 看栈顶但不删除
    public T peek() {
        if (isEmpty()) throw new NoSuchElementException("栈是空的");
        return data.get(data.size() - 1);
    }

    // 判空和大小
    public boolean isEmpty() { return data.isEmpty(); }
    public int size() { return data.size(); }

    public static void main(String[] args) {
        // 使用时填具体类型 String
        // <> 是菱形语法（Java 7+），让编译器自己推断类型参数
        MyStack<String> s = new MyStack<>();
        s.push("a"); s.push("b"); s.push("c");

        // while + isEmpty：栈非空就一直 pop
        while (!s.isEmpty()) {
            // 后进先出：c b a
            System.out.println(s.pop());
        }

        // 同一个 MyStack 类换个类型参数就能装别的
        MyStack<Integer> nums = new MyStack<>();
        nums.push(1); nums.push(2); nums.push(3);
        // peek 看栈顶 3，但不删除
        System.out.println("栈顶 = " + nums.peek());
    }
}
