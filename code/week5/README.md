# Week 5 · 代码

> 配套笔记：[`../../docs/week5/`](../../docs/week5/)

## 模块清单

| 子目录 | 主题 | 跑法 |
|--------|------|------|
| `ioc-annotation/` | Spring IoC 容器：Controller / Service / Repository 三层 + 构造器注入 + @Value | `mvn compile exec:java -Dexec.mainClass=com.learning.Main` |
| `aop-log/` | AOP 切面：按包名切 + 按 @LogTime 注解切，打印入参/出参/耗时 | 同上，`com.learning.Main` |
| `tx-demo/` | 事务说明（实际操作放在 Week 7 博客项目实战） | 见 README |

## 本周自查

- [ ] ioc-annotation 跑通，理解为什么没 `new` 还能拿到 Controller 实例
- [ ] aop-log 跑通，控制台能看到 SERVICE / @LogTime 两组前后日志
- [ ] 改 `app.properties` 里 `app.greeting` 的值，看 UserService 输出变化
- [ ] 解释为什么 `@PostConstruct` 在 UserService 启动时跑了一次
- [ ] 在 aop-log 里加一个新方法不加 @LogTime，验证不会被切面打印（除非在 service 包下被通用切点切到）
