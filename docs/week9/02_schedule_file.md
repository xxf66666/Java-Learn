# Week 9 §02 · 定时任务 + 文件上传

> 两个独立但常用的功能，一次讲完。

---

## 1. Spring Task 定时任务（简单场景够用）

### 启用

```java
@SpringBootApplication
@EnableScheduling
public class Application { ... }
```

### 写任务

```java
@Component
public class StatsJob {

    private static final Logger log = LoggerFactory.getLogger(StatsJob.class);

    @Scheduled(cron = "0 0 2 * * ?")              // 每天 2 点跑
    public void dailyReport() {
        log.info("跑日报...");
    }

    @Scheduled(fixedRate = 5000)                   // 每 5 秒跑一次（启动后立即开始）
    public void heartbeat() {
        log.debug("心跳");
    }

    @Scheduled(fixedDelay = 60000)                 // 上一次结束后 60 秒再跑
    public void cleanup() { ... }
}
```

### Cron 表达式

```
秒 分 时 日 月 周
0  0  2  *  *  ?       每天 2:00:00
0  */5 * * * ?         每 5 分钟
0  0  0  1  *  ?       每月 1 号 0 点
0  0  9 ?  *  MON-FRI  工作日 9 点
```

`?` 用在 "日" 和 "周" 里，表示"不关心"（这两个字段不能同时具体）。

### 局限性

- 单机：多节点部署会**每台都跑**，要么加锁，要么换 XXL-Job
- 不能动态修改：要改时间需要重启
- 没有失败重试、监控、日志查看

---

## 2. XXL-Job（生产推荐）

`spring-task` 之后的工业级选择。功能：
- Web UI 管理 / 暂停 / 立即执行
- 分布式调度（任务分片）
- 失败重试 / 告警
- 历史执行日志

集成步骤（简略）：
1. 下载 `xxl-job-admin`（管理后台 Spring Boot 项目）跑起来
2. 业务项目引入 `xxl-job-core` 依赖
3. 写一个 `@XxlJob("jobName")` 注解的方法
4. 在管理后台配置该任务的 cron 和参数

更多见 <https://www.xuxueli.com/xxl-job/>。学习阶段先用 `@Scheduled` 够用。

---

## 3. 文件上传：基础版

### 配置

```yaml
spring:
  servlet:
    multipart:
      enabled: true
      max-file-size: 10MB
      max-request-size: 50MB
```

### 接口

```java
@PostMapping("/upload")
public Result<String> upload(@RequestParam("file") MultipartFile file) throws IOException {
    if (file.isEmpty()) throw new BusinessException(400, "文件不能为空");

    String orig = file.getOriginalFilename();
    String ext = orig.substring(orig.lastIndexOf('.'));
    String name = UUID.randomUUID() + ext;

    Path target = Path.of("uploads", name);
    Files.createDirectories(target.getParent());
    file.transferTo(target);

    return Result.ok("/files/" + name);   // 返回给前端
}
```

### 下载

```java
@GetMapping("/files/{name}")
public ResponseEntity<Resource> download(@PathVariable String name) {
    Path p = Path.of("uploads", name);
    if (!Files.exists(p)) return ResponseEntity.notFound().build();

    Resource r = new FileSystemResource(p);
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + name + "\"")
        .body(r);
}
```

---

## 4. MinIO：对象存储（生产推荐）

存本地的问题：
- 多实例部署：A 实例存的文件，B 实例读不到
- 备份 / 扩容麻烦
- 不能直接给浏览器加速访问

生产环境用 OSS（阿里云 / S3）或自建 **MinIO**（开源、协议兼容 S3）。

```bash
docker run -d --name minio \
  -p 9000:9000 -p 9001:9001 \
  -e MINIO_ROOT_USER=admin \
  -e MINIO_ROOT_PASSWORD=admin123 \
  minio/minio server /data --console-address ":9001"

# 控制台：http://localhost:9001  (admin/admin123)
```

```xml
<dependency>
    <groupId>io.minio</groupId>
    <artifactId>minio</artifactId>
    <version>8.5.11</version>
</dependency>
```

```java
@Service
public class MinioService {

    private final MinioClient client = MinioClient.builder()
        .endpoint("http://localhost:9000")
        .credentials("admin", "admin123")
        .build();

    public String upload(MultipartFile file, String bucket) throws Exception {
        String name = UUID.randomUUID() + "_" + file.getOriginalFilename();
        client.putObject(PutObjectArgs.builder()
            .bucket(bucket)
            .object(name)
            .stream(file.getInputStream(), file.getSize(), -1)
            .contentType(file.getContentType())
            .build());
        return name;
    }

    public InputStream download(String bucket, String name) throws Exception {
        return client.getObject(GetObjectArgs.builder()
            .bucket(bucket).object(name).build());
    }
}
```

---

## 5. 自查

- [ ] 写一个 `@Scheduled(cron = "0/10 * * * * ?")`，每 10 秒打印一次时间
- [ ] 写一个 `@Scheduled(cron = "0 0 2 * * ?")`，注释里写明它什么时候跑
- [ ] 写一个 MultipartFile 文件上传接口，存到本地
- [ ] 改成存到 MinIO，能通过控制台看到文件

## 代码示例

→ [`code/week9/scheduled-task/`](../../code/week9/scheduled-task/)
→ [`code/week9/file-upload/`](../../code/week9/file-upload/)
