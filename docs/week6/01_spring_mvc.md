# Week 6 §01 · Spring MVC

> 目标：写出标准的 REST API，能接收 / 返回 JSON，能处理路径参数 / 查询参数 / 请求体。

---

## 1. Controller 基础

```java
@RestController                  // = @Controller + @ResponseBody（自动 JSON 序列化）
@RequestMapping("/api/users")     // 类级别前缀
public class UserController {

    @GetMapping("/{id}")          // GET /api/users/1
    public User getById(@PathVariable Long id) {
        return userService.findById(id);
    }

    @GetMapping                    // GET /api/users?name=...&page=...
    public List<User> list(@RequestParam(required = false) String name,
                           @RequestParam(defaultValue = "1") int page) {
        return userService.list(name, page);
    }

    @PostMapping                   // POST /api/users  body: {...}
    public User create(@RequestBody User user) {
        return userService.create(user);
    }

    @PutMapping("/{id}")          // PUT /api/users/1
    public User update(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        return userService.update(user);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }
}
```

---

## 2. 路径映射注解对照

| 注解 | 等价于 | HTTP 方法 |
|------|--------|-----------|
| `@GetMapping` | `@RequestMapping(method = GET)` | GET（查） |
| `@PostMapping` | ...POST | POST（增） |
| `@PutMapping` | ...PUT | PUT（全量改） |
| `@PatchMapping` | ...PATCH | PATCH（部分改） |
| `@DeleteMapping` | ...DELETE | DELETE（删） |

---

## 3. 参数绑定四件套

### 3.1 `@PathVariable` —— URL 路径里的变量

```java
@GetMapping("/users/{id}/orders/{orderId}")
public Order get(@PathVariable Long id, @PathVariable("orderId") String oid) { ... }
```

### 3.2 `@RequestParam` —— ?key=value 查询参数

```java
@GetMapping
public List<User> search(
    @RequestParam String name,                            // 必填
    @RequestParam(defaultValue = "0") int offset,         // 可选 + 默认值
    @RequestParam(required = false) String email          // 可选
) { ... }
```

### 3.3 `@RequestBody` —— 请求体 JSON

```java
@PostMapping
public User create(@RequestBody UserCreateReq req) { ... }
```

```json
{"name": "Alice", "age": 20}
```

### 3.4 直接绑定对象（不加注解）—— 表单提交

```java
@GetMapping("/search")
public List<User> search(UserSearchReq req) { ... }
// 等价于把所有同名查询参数塞到 UserSearchReq 的字段里
```

---

## 4. 统一响应封装

API 通常返回这样的结构：

```json
{
  "code": 0,
  "message": "ok",
  "data": { ... }
}
```

写一个 `Result<T>`：

```java
public class Result<T> {
    private int code;
    private String message;
    private T data;

    public static <T> Result<T> ok(T data) {
        Result<T> r = new Result<>();
        r.code = 0; r.message = "ok"; r.data = data;
        return r;
    }

    public static <T> Result<T> fail(int code, String message) {
        Result<T> r = new Result<>();
        r.code = code; r.message = message;
        return r;
    }
    // getters
}
```

Controller 返回：
```java
@GetMapping("/{id}")
public Result<User> getById(@PathVariable Long id) {
    return Result.ok(userService.findById(id));
}
```

---

## 5. 全局异常处理

每个 Controller 都 try-catch 太丑。用 `@RestControllerAdvice` 集中处理：

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBiz(BusinessException e) {
        return Result.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidation(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldError().getDefaultMessage();
        return Result.fail(400, msg);
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleOther(Exception e) {
        log.error("未捕获异常", e);
        return Result.fail(500, "服务器异常");
    }
}
```

---

## 6. 参数校验

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

```java
public class UserCreateReq {
    @NotBlank(message = "姓名不能为空")
    @Size(max = 32, message = "姓名最长 32 字符")
    private String name;

    @Min(value = 0, message = "年龄不能为负")
    @Max(value = 150, message = "年龄不能超过 150")
    private int age;

    @Email(message = "邮箱格式不正确")
    private String email;
}
```

```java
@PostMapping
public Result<User> create(@RequestBody @Valid UserCreateReq req) { ... }
// @Valid 触发校验，失败抛 MethodArgumentNotValidException
```

---

## 7. Jackson 配置

Spring Boot 默认用 Jackson 做 JSON 序列化。

```java
public class Order {
    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonIgnore                 // 不参与 JSON 输出
    private String internalNote;

    @JsonProperty("user_name")   // JSON 字段名映射
    private String userName;
}
```

全局格式化日期：

```yaml
spring:
  jackson:
    date-format: yyyy-MM-dd HH:mm:ss
    time-zone: Asia/Shanghai
    default-property-inclusion: non_null     # 不输出 null 字段
```

---

## 8. 跨域 CORS

前后端分离时浏览器会拦跨域请求。最简配置：

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
```

---

## 9. 文件上传 / 下载（简版）

```java
@PostMapping("/upload")
public Result<String> upload(@RequestParam("file") MultipartFile file) throws IOException {
    String name = file.getOriginalFilename();
    file.transferTo(Path.of("uploads", name));
    return Result.ok(name);
}

@GetMapping("/download/{name}")
public ResponseEntity<Resource> download(@PathVariable String name) {
    Resource r = new FileSystemResource("uploads/" + name);
    return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=" + name)
            .body(r);
}
```

```yaml
spring:
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 50MB
```

---

## 10. 自查

- [ ] 写出商品 CRUD 四个接口（GET 列表 / GET by id / POST 新建 / PUT 更新 / DELETE 删除）
- [ ] 用 Apifox / Postman / IDEA HTTP Client 调通五个接口
- [ ] 加 `Result<T>` 统一封装，所有接口包装返回
- [ ] 加 `@RestControllerAdvice` 全局异常处理
- [ ] 给入参加 `@Valid` 校验，故意传错参数看错误响应
- [ ] 改 Jackson 配置让 LocalDateTime 输出 `yyyy-MM-dd HH:mm:ss`

## 代码示例

→ [`code/week6/rest-api/`](../../code/week6/rest-api/) —— 商品 CRUD 完整接口
