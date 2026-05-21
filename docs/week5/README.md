# Week 5 · Spring Core（IoC / AOP / 事务）

> 目标：理解 Spring 的两大基石——**IoC 容器**和 **AOP 切面**。这两个理解了，后面 Spring Boot 一切水到渠成。

## 笔记顺序

| 序号 | 文件 | 主题 |
|------|------|------|
| 00 | [`00_ioc.md`](00_ioc.md) | IoC / DI 是什么、Bean、注解配置、构造器注入 |
| 01 | [`01_aop.md`](01_aop.md) | AOP 概念 + 自己写一个 "日志切面" |
| 02 | [`02_tx.md`](02_tx.md) | 声明式事务 `@Transactional` 七种传播行为 |

## 配套代码

→ [`../../code/week5/`](../../code/week5/)

## 本周里程碑

到周末你应该能：
- 用一段话说清"IoC 容器解决了什么问题"
- 写一个三层结构（Controller → Service → Dao）的 Spring 项目，全用 `@Component` + `@Autowired`
- 自己写一个 AOP 切面，自动打印所有 Service 方法的入参、返回值、耗时
- 解释 `@Transactional` 传播行为 `REQUIRED` 和 `REQUIRES_NEW` 的差异
- 知道 "事务自调用失效" 的坑及解决方案
