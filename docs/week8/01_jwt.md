# Week 8 §01 · JWT 鉴权

> 前后端分离场景下的标准方案：**前端登录拿到 JWT Token，之后每次请求带这个 Token，后端验证 Token 是否有效**。

---

## 1. JWT 是什么

**JSON Web Token**：一段字符串，三部分用 `.` 分隔：

```
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbGljZSIsImV4cCI6MTcwMDAwMDAwMH0.signature
└─── Header ────┘ └────────── Payload ──────────┘ └ Signature ┘
```

- **Header**：算法（如 HS256）
- **Payload**：业务数据（用户 ID、角色、过期时间等，**Base64 编码可被任意解出来，所以不要放密码！**）
- **Signature**：用密钥对 Header + Payload 做签名，防止被篡改

**特点**
- **无状态**：服务器不存 session，扩容方便
- **跨域友好**：放在 HTTP Header 里
- **可包含用户信息**：免一次数据库查询

---

## 2. JWT vs Session 对比

| | Session | JWT |
|--|---------|-----|
| 状态 | 服务器存 | 服务器不存 |
| 扩容 | 需要 Redis 共享 session | 天然支持 |
| 撤销 | 直接删 session | 难（要黑名单） |
| 大小 | 一个 ID | 整个 token（KB 级） |

**实操**：大多数前后端分离项目用 JWT；要做细粒度撤销 / SSO 时可加 Redis 做黑名单。

---

## 3. 引入 jjwt 库

```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.6</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
```

---

## 4. JwtUtil 工具类

```java
@Component
public class JwtUtil {

    private final SecretKey key = Keys.hmacShaKeyFor(
        "your-32-bytes-or-longer-secret-key-here!!".getBytes());

    private final long expireMs = TimeUnit.DAYS.toMillis(7);     // 7 天有效

    /** 生成 token */
    public String generate(String username, Long userId, String role) {
        return Jwts.builder()
            .subject(username)
            .claim("userId", userId)
            .claim("role", role)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expireMs))
            .signWith(key)
            .compact();
    }

    /** 解析 token，无效时抛异常 */
    public Claims parse(String token) {
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }
}
```

---

## 5. 登录接口

```java
@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;

    @PostMapping("/login")
    public Result<Map<String, String>> login(@RequestBody LoginReq req) {
        // Spring Security 自动按 UserDetailsService + PasswordEncoder 校验
        Authentication auth = authManager.authenticate(
            new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));

        User user = userMapper.selectByUsername(req.getUsername());
        String token = jwtUtil.generate(user.getUsername(), user.getId(), user.getRole());

        return Result.ok(Map.of("token", token, "username", user.getUsername()));
    }
}
```

---

## 6. JwtFilter：拦截每个请求验证 Token

```java
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse rsp, FilterChain chain)
            throws ServletException, IOException {
        String auth = req.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            String token = auth.substring(7);
            try {
                Claims claims = jwtUtil.parse(token);
                String username = claims.getSubject();
                String role = claims.get("role", String.class);

                List<SimpleGrantedAuthority> authorities =
                    List.of(new SimpleGrantedAuthority("ROLE_" + role));

                UsernamePasswordAuthenticationToken token2 =
                    new UsernamePasswordAuthenticationToken(username, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(token2);
            } catch (Exception e) {
                // token 无效，不设置上下文 → 走未鉴权流程
            }
        }
        chain.doFilter(req, rsp);
    }
}
```

---

## 7. 把 JwtFilter 串到 SecurityFilterChain

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtFilter;

    @Bean
    public SecurityFilterChain chain(HttpSecurity http) throws Exception {
        http
            .csrf(c -> c.disable())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/login", "/api/register").permitAll()
                .anyRequest().authenticated())
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }
}
```

---

## 8. 调用流程

```
POST /api/login            { username, password }
       ↓
   认证成功
       ↓
返回 { token: "eyJhbGc..." }

之后所有请求：
GET /api/articles
Authorization: Bearer eyJhbGc...
       ↓
JwtFilter 解析 token，把用户身份放进 SecurityContext
       ↓
Controller 正常处理
```

---

## 9. 续期（refresh token）

简单方案：token 接近过期时，前端自动调 `/api/refresh` 拿新 token。

更安全：发两个 token，access token 短期（15 分钟）+ refresh token 长期（7 天），refresh token 存数据库可被撤销。

---

## 10. 自查

- [ ] 完成本周 auth-demo：注册 / 登录 / 拿到 token / 用 token 访问受保护接口
- [ ] 故意改一个字符让 token 失效，看 401 响应
- [ ] 让 token 过期（改成 1 分钟有效），看过期后访问受保护接口的行为
- [ ] 在受保护接口里通过 Authentication 拿到当前用户名

## 代码示例

→ [`code/week8/auth-demo/`](../../code/week8/auth-demo/)
