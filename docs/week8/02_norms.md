# Week 8 §02 · 工程规范

> 统一响应 + 全局异常 + 参数校验 + 接口文档，这四件是企业项目的"标配"。

---

## 1. 统一响应封装

```java
public class Result<T> {
    private int code;
    private String message;
    private T data;
    private long timestamp = System.currentTimeMillis();

    public static <T> Result<T> ok(T data) { ... }
    public static <T> Result<T> fail(int code, String msg) { ... }
}
```

**code 约定**（参考阿里 / 若依）：

| 范围 | 含义 |
|------|------|
| `0` | 成功 |
| `400` | 参数错误 |
| `401` | 未登录 |
| `403` | 无权限 |
| `404` | 资源不存在 |
| `500` | 服务器异常 |
| `1xxxx` | 业务错误（如 `10001` 用户已存在） |

### 分页响应

```java
public class PageResult<T> {
    private List<T> records;
    private long total;
    private long pages;
    private long current;
    private long size;
}
```

---

## 2. 业务异常

```java
public class BusinessException extends RuntimeException {
    private final int code;
    public BusinessException(int code, String msg) { super(msg); this.code = code; }
}

// 抛
throw new BusinessException(40401, "用户不存在");
```

**抽出错误码枚举**：

```java
public enum ErrorCode {
    SUCCESS(0, "成功"),
    BAD_REQUEST(400, "参数错误"),
    UNAUTHORIZED(401, "未登录"),
    FORBIDDEN(403, "无权限"),
    NOT_FOUND(404, "资源不存在"),
    SERVER_ERROR(500, "服务器异常"),

    USER_NOT_FOUND(10001, "用户不存在"),
    USER_ALREADY_EXISTS(10002, "用户已存在"),
    PASSWORD_INCORRECT(10003, "密码错误"),
    ;

    private final int code;
    private final String message;
    // 构造 + getter
}

public class BusinessException extends RuntimeException {
    private final int code;
    public BusinessException(ErrorCode e) {
        super(e.getMessage());
        this.code = e.getCode();
    }
}

// 使用
throw new BusinessException(ErrorCode.USER_NOT_FOUND);
```

---

## 3. 全局异常处理 `@RestControllerAdvice`

```java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 业务异常
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBiz(BusinessException e) {
        return Result.fail(e.getCode(), e.getMessage());
    }

    // 参数校验失败（@Valid）
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValid(MethodArgumentNotValidException e) {
        var fe = e.getBindingResult().getFieldError();
        return Result.fail(400, fe != null ? fe.getDefaultMessage() : "参数校验失败");
    }

    // 鉴权失败
    @ExceptionHandler(AuthenticationException.class)
    public Result<Void> handleAuth(AuthenticationException e) {
        return Result.fail(401, "未登录或登录已过期");
    }

    // 权限不足
    @ExceptionHandler(AccessDeniedException.class)
    public Result<Void> handleAccessDenied(AccessDeniedException e) {
        return Result.fail(403, "无权限");
    }

    // 兜底
    @ExceptionHandler(Exception.class)
    public Result<Void> handleOther(Exception e) {
        log.error("未捕获异常", e);
        return Result.fail(500, "服务器异常");
    }
}
```

---

## 4. 参数校验

### 4.1 基础注解

```java
public class UserCreateReq {
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 16, message = "用户名长度 3-16 字符")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, message = "密码至少 6 位")
    private String password;

    @Email(message = "邮箱格式不正确")
    private String email;

    @Min(value = 0, message = "年龄不能为负")
    @Max(value = 150, message = "年龄不能超过 150")
    private Integer age;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式错误")
    private String phone;

    @NotNull(message = "性别不能为空")
    private Integer gender;
}
```

### 4.2 触发校验

```java
@PostMapping
public Result<User> create(@RequestBody @Valid UserCreateReq req) { ... }
```

### 4.3 自定义校验注解

```java
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MobileValidator.class)
public @interface Mobile {
    String message() default "手机号格式错误";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

public class MobileValidator implements ConstraintValidator<Mobile, String> {
    private static final Pattern P = Pattern.compile("^1[3-9]\\d{9}$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext ctx) {
        return value == null || P.matcher(value).matches();
    }
}

// 使用
@Mobile
private String phone;
```

---

## 5. 接口文档：SpringDoc / Knife4j

### 5.1 SpringDoc（官方）

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.5.0</version>
</dependency>
```

访问 `http://localhost:8080/swagger-ui/index.html` 自动生成文档。

### 5.2 Knife4j（国产增强 UI，推荐）

```xml
<dependency>
    <groupId>com.github.xiaoymin</groupId>
    <artifactId>knife4j-openapi3-jakarta-spring-boot-starter</artifactId>
    <version>4.5.0</version>
</dependency>
```

访问 `http://localhost:8080/doc.html`，UI 更友好（中文界面、调试更顺手）。

### 5.3 注解

```java
@Tag(name = "用户管理")
@RestController
public class UserController {

    @Operation(summary = "创建用户")
    @PostMapping
    public Result<User> create(@RequestBody UserCreateReq req) { ... }

    @Operation(summary = "按 ID 查询")
    @GetMapping("/{id}")
    public Result<User> get(@Parameter(description = "用户 ID") @PathVariable Long id) { ... }
}

@Schema(description = "用户创建请求")
public class UserCreateReq {
    @Schema(description = "用户名", example = "alice")
    private String username;
}
```

---

## 6. 配 Security 放行 Swagger 路径

```java
.requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/doc.html", "/webjars/**").permitAll()
```

---

## 7. 自查

- [ ] 实现 `Result<T>` + `ErrorCode` 枚举
- [ ] 实现全局异常处理，覆盖业务异常 / 校验失败 / 401 / 403 / 500
- [ ] 给一个 DTO 加 5 个以上 `@NotNull / @NotBlank / @Email / @Pattern` 注解
- [ ] 写一个自定义校验注解 `@Mobile`
- [ ] 集成 Knife4j，访问 `/doc.html` 看到文档
- [ ] 在 Controller 加 `@Tag` / `@Operation` 注解让文档更友好

## 代码示例

→ [`code/week8/auth-demo/`](../../code/week8/auth-demo/) —— 完整覆盖本节内容
