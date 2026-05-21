# tx-demo · 事务实战

事务必须在真实数据库环境下才能演示（H2 内存数据库也行，但配置多）。建议在 Week 7 完成 MyBatis-Plus 学习后，回到博客系统项目里实战 `@Transactional` 的各种行为。

本周笔记 [`docs/week5/02_tx.md`](../../../docs/week5/02_tx.md) 完整覆盖：
- `@Transactional` 注解
- 七种传播行为
- 三种失效场景
- `rollbackFor` 配置

**手动验证传播行为**（接 Week 7 博客项目后）：
1. 写 `OrderService.createOrder()` 默认 REQUIRED
2. 写 `LogService.log()` 改成 REQUIRES_NEW
3. 让 `LogService.log()` 抛异常，看 OrderService 主事务是否回滚
4. 把 `LogService.log()` 改回 REQUIRED，再看一次行为差异
