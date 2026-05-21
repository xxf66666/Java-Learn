package student;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
// Collectors 提供 groupingBy / counting 等收集器
import java.util.stream.Collectors;

/**
 * 学生仓库：内存里管理一组学生，并支持 CSV 持久化。
 *
 * 在企业项目里这一层叫"Repository"或"DAO"，专门管数据访问。
 */
public class StudentRepository {

    // LinkedHashMap：基于哈希但**保留插入顺序**
    // key = 学号 (String)，value = Student 对象
    // 用 final + 直接初始化：保证 store 引用不变，但里面内容可变
    private final Map<String, Student> store = new LinkedHashMap<>();

    /** 添加，学号重复抛异常 */
    public void add(Student s) {
        // containsKey 判断 key 是否存在
        if (store.containsKey(s.getId())) {
            throw new IllegalArgumentException("学号已存在: " + s.getId());
        }
        store.put(s.getId(), s);
    }

    /** 删除，返回是否真删了 */
    public boolean removeById(String id) {
        // Map.remove(key) 返回被删的 value（不存在则返回 null）
        // != null 表示真删除了
        return store.remove(id) != null;
    }

    /**
     * 按学号查
     * Optional<T> 用来表示"可能没有"，避免直接返回 null
     * 调用方可以 .ifPresent / .orElse 等链式处理
     */
    public Optional<Student> findById(String id) {
        // ofNullable：参数是 null 就返回 Optional.empty()，否则包装起来
        return Optional.ofNullable(store.get(id));
    }

    /** 全部学生（按插入顺序）*/
    public List<Student> findAll() {
        // values() 返回 Map 的所有值视图（Collection）
        // new ArrayList<>(...) 拷一份出来，外部修改不影响内部
        return new ArrayList<>(store.values());
    }

    /** 按专业筛选 */
    public List<Student> findByMajor(String major) {
        // stream() 把集合转流，链式处理
        // filter 接受 Predicate Lambda
        // toList() (Java 16+) 收集成不可变 List
        return store.values().stream()
                    .filter(s -> s.getMajor().equals(major))
                    .toList();
    }

    public int size() { return store.size(); }

    // ====== CSV 持久化 ======

    /** 从 CSV 文件加载（如果文件不存在直接返回，什么都不做）*/
    public void loadFromCsv(Path csv) throws IOException {
        if (!Files.exists(csv)) return;

        // 重新加载前先清空
        store.clear();

        // 一次读所有行
        List<String> lines = Files.readAllLines(csv);

        // 跳过第一行表头（"id,name,age,major"），从索引 1 开始
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) continue;        // 空行跳过

            // 反序列化：CSV → Student
            Student s = Student.fromCsv(line);
            store.put(s.getId(), s);
        }
    }

    /** 写回 CSV 文件 */
    public void saveToCsv(Path csv) throws IOException {
        // 准备所有要写的行
        List<String> lines = new ArrayList<>();
        lines.add("id,name,age,major");                                // 表头

        // forEach 遍历所有 Student，把每个转成 CSV 行追加到 lines
        // s -> lines.add(s.toCsv()) 是 Consumer Lambda
        store.values().forEach(s -> lines.add(s.toCsv()));

        // Files.write(path, lines)：把行列表写入文件（一行一个元素）
        Files.write(csv, lines);
    }

    // ====== 统计：每个专业的人数 ======
    public Map<String, Long> countByMajor() {
        // Collectors.groupingBy(分组键, 下游收集器)
        // 分组键：Student::getMajor 是方法引用，等价于 s -> s.getMajor()
        // 下游：counting() 数每组多少个元素
        return store.values().stream()
                    .collect(Collectors.groupingBy(Student::getMajor, Collectors.counting()));
    }
}
