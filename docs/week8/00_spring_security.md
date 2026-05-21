# Week 8 §00 · Spring Security 6

> Spring Security 是 Spring 官方的安全框架，**功能强大但门槛高**。本周只学最常用的：表单登录 → JWT 鉴权 → 接口权限。

---

## 1. 核心概念

| 概念 | 含义 |
|------|------|
| **认证 Authentication** | 你是谁？（登录） |
| **授权 Authorization** | 你能做什么？（角色 / 权限） |
| **过滤器链 SecurityFilterChain** | 一组 Filter，每个请求经过它们 |
| **UserDetailsService** | 加载用户信息（从数据库） |
| **PasswordEncoder** | 密码加密（BCrypt 等） |

---

## 2. 引入依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

加上之后**所有接口默认要登录**才能访问。访问任意接口浏览器会跳出登录框；终端访问会返回 401。

启动时控制台会打一个随机密码，用户名是 `user`。

---

## 3. 最小配置（开放所有接口）

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain chain(HttpSecurity http) throws Exception {
        http
            .csrf(c -> c.disable())                       // 关闭 CSRF（前后端分离 + JWT 不需要）
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll())                 // 所有请求都放行
            .formLogin(f -> f.disable())                   // 不要默认表单登录页
            .httpBasic(b -> b.disable());                  // 不要 HTTP Basic
        return http.build();
    }
}
```

加上这段后，所有接口都能匿名访问，回到了没装 Security 之前的状态。

---

## 4. 区分需要登录和不需要登录

```java
http.authorizeHttpRequests(auth -> auth
    .requestMatchers("/api/login", "/api/register", "/v3/api-docs/**", "/doc.html").permitAll()
    .anyRequest().authenticated());
```

`permitAll()` 放行的接口能匿名访问；其他全部需要登录。

---

## 5. 自己实现 `UserDetailsService`

```java
@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
            .eq(User::getUsername, username));
        if (user == null) throw new UsernameNotFoundException("用户不存在");

        return org.springframework.security.core.userdetails.User.builder()
            .username(user.getUsername())
            .password(user.getPassword())              // 加密后的密码
            .authorities("ROLE_" + user.getRole())     // 角色：ROLE_USER / ROLE_ADMIN
            .build();
    }
}
```

---

## 6. 密码加密

**永远不要明文存密码**。Spring Security 提供 BCrypt：

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}

// 注册时
String encoded = passwordEncoder.encode("rawPassword");
// 保存到数据库

// 登录时框架自动比对
```

BCrypt 每次加密结果都不同（自带 salt），但能正确校验。

---

## 7. 方法级权限

```java
@EnableMethodSecurity(prePostEnabled = true)
@Configuration
class SecurityConfig { ... }
```

```java
@PreAuthorize("hasRole('ADMIN')")
@GetMapping("/api/admin/users")
public List<User> list() { ... }

@PreAuthorize("hasAuthority('user:delete')")
@DeleteMapping("/api/users/{id}")
public void delete(@PathVariable Long id) { ... }
```

`hasRole('ADMIN')` 会自动补成 `ROLE_ADMIN` 去匹配。

---

## 8. 获取当前用户

```java
@GetMapping("/api/me")
public Result<?> me(Authentication auth) {
    return Result.ok(Map.of(
        "username", auth.getName(),
        "authorities", auth.getAuthorities()
    ));
}

// 或在任意地方
SecurityContextHolder.getContext().getAuthentication();
```

---

## 9. CORS / CSRF

前后端分离 + JWT 场景：

```java
http
    .csrf(c -> c.disable())
    .cors(c -> {});       // 走全局 CorsConfig
```

CORS 全局配置见 Week 6 §01。

---

## 10. 自查

- [ ] 引入 Spring Security 后，跑应用看到自动登录页（说明已生效）
- [ ] 写一个 `SecurityConfig` 关闭表单登录、放行 `/api/login`
- [ ] 自己实现 `UserDetailsService` 从数据库查用户
- [ ] 用 `BCryptPasswordEncoder` 加密密码
- [ ] 在某个 Controller 上加 `@PreAuthorize("hasRole('ADMIN')")`，无权限访问返回 403
- [ ] 用 `Authentication` 拿到当前登录用户名

## 代码示例

→ [`code/week8/auth-demo/`](../../code/week8/auth-demo/)
