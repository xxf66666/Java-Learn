package generic;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * 自己写的泛型栈，理解 &lt;T&gt; 怎么用。
 * （生产环境用 java.util.ArrayDeque 即可，这里只是练手）
 */
public class MyStack<T> {

    private final List<T> data = new ArrayList<>();

    public void push(T item) {
        data.add(item);
    }

    public T pop() {
        if (isEmpty()) throw new NoSuchElementException("栈是空的");
        return data.remove(data.size() - 1);
    }

    public T peek() {
        if (isEmpty()) throw new NoSuchElementException("栈是空的");
        return data.get(data.size() - 1);
    }

    public boolean isEmpty() { return data.isEmpty(); }
    public int size() { return data.size(); }

    public static void main(String[] args) {
        MyStack<String> s = new MyStack<>();
        s.push("a"); s.push("b"); s.push("c");
        while (!s.isEmpty()) {
            System.out.println(s.pop());      // c b a
        }

        // 同一个 MyStack 类可以装任何类型
        MyStack<Integer> nums = new MyStack<>();
        nums.push(1); nums.push(2); nums.push(3);
        System.out.println("栈顶 = " + nums.peek());
    }
}
