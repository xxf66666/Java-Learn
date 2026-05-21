# Week 5 §01 · AOP 面向切面

> 解决的问题：日志、事务、缓存、权限这些**横切关注点**散布在每个方法里，污染业务代码。AOP 把这些"打包"成切面，自动在合适的时机执行。

---

## 1. 痛点举例

每个 Service 方法都想打印入参 + 耗时：

```java
public User createUser(String name) {
    long t = System.currentTimeMillis();
    log.info("createUser 入参: name={}", name);
    try {
        // 真实业务
        User u = userRepo.save(new User(name));
        log.info("createUser 返回: {}", u);
        return u;
    } finally {
        log.info("createUser 耗时: {}ms", System.currentTimeMillis() - t);
    }
}
```

10 个方法就要重复 10 次这种代码。AOP 让你**写一次**，应用到所有方法。

---

## 2. AOP 五大概念

| 术语 | 含义 |
|------|------|
| **Aspect**（切面） | 一个带横切逻辑的类 |
| **Pointcut**（切点） | 表达式：哪些方法要被切 |
| **Advice**（通知） | 切到的方法被调用前/后/包围时要做什么 |
| **JoinPoint**（连接点） | 被切到的具体方法调用 |
| **Weaving**（织入） | Spring 把切面"织入"到目标方法的过程（运行时动态代理） |

---

## 3. 五种 Advice 类型

| 注解 | 时机 |
|------|------|
| `@Before` | 方法**之前**执行 |
| `@After` | 方法之后，**无论成功失败** |
| `@AfterReturning` | 方法**正常返回后** |
| `@AfterThrowing` | 方法**抛异常后** |
| `@Around` | **包围方法**：能控制是否执行、修改入参出参（最强，最常用） |

---

## 4. 写第一个切面

```java
// pom.xml 加入
// <dependency>
//   <groupId>org.springframework.boot</groupId>
//   <artifactId>spring-boot-starter-aop</artifactId>
// </dependency>

@Aspect
@Component
public class LogAspect {

    private static final Logger log = LoggerFactory.getLogger(LogAspect.class);

    // 切点：所有 com.learning.service 包及子包下的方法
    @Pointcut("execution(* com.learning.service..*.*(..))")
    public void serviceMethods() {}

    @Around("serviceMethods()")
    public Object logAround(ProceedingJoinPoint pjp) throws Throwable {
        String methodName = pjp.getSignature().toShortString();
        Object[] args = pjp.getArgs();

        log.info("→ {} 入参 {}", methodName, Arrays.toString(args));

        long t = System.currentTimeMillis();
        try {
            Object result = pjp.proceed();          // 调用真实方法
            log.info("← {} 返回 {} 耗时 {}ms", methodName, result, System.currentTimeMillis() - t);
            return result;
        } catch (Throwable e) {
            log.error("✗ {} 抛异常 {}", methodName, e.toString());
            throw e;
        }
    }
}
```

这一份代码生效后，**所有 Service 方法**自动有了日志。

---

## 5. 切点表达式

```
execution([修饰符] 返回类型 [包名.类名.]方法名(参数))
```

```java
@Pointcut("execution(* com.learning.service.*.*(..))")
//                 ↑     ↑               ↑  ↑
//                返回    包             方法 参数
// * → 任意返回类型 / 任意类 / 任意方法
// .. → 包含子包 / 任意参数列表
```

常见模式：

```java
// service 包及子包所有方法
@Pointcut("execution(* com.learning.service..*.*(..))")

// 所有以 query 开头的方法
@Pointcut("execution(* com.learning..*.query*(..))")

// 标注了 @Cacheable 的方法（按注解切）
@Pointcut("@annotation(org.springframework.cache.annotation.Cacheable)")

// 标注了 @Controller 的类的所有方法
@Pointcut("@within(org.springframework.stereotype.Controller)")
```

---

## 6. 用自定义注解切

很多时候不想用包名切（耦合太紧），改用自定义注解：

```java
// 1. 定义注解
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogTime { }

// 2. 切面按注解
@Aspect
@Component
public class LogTimeAspect {
    @Around("@annotation(LogTime)")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        long t = System.currentTimeMillis();
        try {
            return pjp.proceed();
        } finally {
            log.info("{} 耗时 {}ms", pjp.getSignature(), System.currentTimeMillis() - t);
        }
    }
}

// 3. 业务方法标注
@LogTime
public User createUser(String name) { ... }
```

后面 ERP 项目的"操作日志" / "数据权限" / "字段脱敏" 都会用这套套路。

---

## 7. AOP 原理：动态代理

Spring AOP 不修改你的源码，而是在运行时给你的对象**包一层代理**：

```
你拿到的 userService 不是 UserService 本人，
而是 Spring 用 JDK Proxy / CGLib 生成的代理对象。
调用方法时，代理先跑切面逻辑、再调真实方法。
```

| 代理方式 | 触发条件 |
|---------|---------|
| JDK Proxy | 目标实现了接口 |
| CGLib | 目标没实现接口（继承生成子类） |

Spring Boot 默认偏好 CGLib，不必关心切换。

---

## 8. 自调用失效（常见坑）

```java
@Service
public class UserService {

    @LogTime
    public User createUser(String name) {
        return findById(1L);     // 同类内部调用！不走代理！@LogTime 失效
    }

    @LogTime
    public User findById(long id) { ... }
}
```

**为什么**：你直接通过 `this.findById(...)` 调，而不是通过代理对象，AOP 拦不到。

**解决方案**
1. 让 Bean 注入自己（但要避开循环依赖检查）
2. 用 `AopContext.currentProxy()` 拿当前代理对象
3. 拆成两个 Bean

事务 `@Transactional` 也有同样的问题，Week 5 §02 讲。

---

## 9. 自查

- [ ] 解释 AOP 是什么、解决什么问题
- [ ] 列出 5 种 Advice 类型 + 时机
- [ ] 写一个切面给所有 Service 方法打印入参、出参、耗时
- [ ] 写一个自定义注解 `@LogTime` 配合切面只切打了注解的方法
- [ ] 解释"自调用失效"的原因

## 代码示例

→ [`code/week5/aop-log/`](../../code/week5/aop-log/)
