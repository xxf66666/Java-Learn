package student;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 学生仓库：增删改查 + CSV 导入导出。
 *
 * 用 LinkedHashMap 保持插入顺序，key 是学号。
 */
public class StudentRepository {

    private final Map<String, Student> store = new LinkedHashMap<>();

    public void add(Student s) {
        if (store.containsKey(s.getId())) {
            throw new IllegalArgumentException("学号已存在: " + s.getId());
        }
        store.put(s.getId(), s);
    }

    public boolean removeById(String id) {
        return store.remove(id) != null;
    }

    public Optional<Student> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<Student> findAll() {
        return new ArrayList<>(store.values());
    }

    public List<Student> findByMajor(String major) {
        return store.values().stream()
                    .filter(s -> s.getMajor().equals(major))
                    .toList();
    }

    public int size() { return store.size(); }

    // ====== CSV 持久化 ======
    public void loadFromCsv(Path csv) throws IOException {
        if (!Files.exists(csv)) return;
        store.clear();
        List<String> lines = Files.readAllLines(csv);
        for (int i = 1; i < lines.size(); i++) {       // 跳过表头
            String line = lines.get(i).trim();
            if (line.isEmpty()) continue;
            Student s = Student.fromCsv(line);
            store.put(s.getId(), s);
        }
    }

    public void saveToCsv(Path csv) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("id,name,age,major");
        store.values().forEach(s -> lines.add(s.toCsv()));
        Files.write(csv, lines);
    }

    // ====== 统计：每个专业的人数 ======
    public Map<String, Long> countByMajor() {
        return store.values().stream()
                    .collect(Collectors.groupingBy(Student::getMajor, Collectors.counting()));
    }
}
