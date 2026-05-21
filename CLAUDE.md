# Java-Learn · 项目说明（给未来的 Claude / 协作者读）

## 这是什么

xxf 的 Java + Spring + ERP 个人学习仓库，参照 `~/Desktop/project/github/CNN-Learn` 的"周计划 + docs 理论 + code 实战"范式组织。学习者背景：C++ / Python 老兵，第一次系统学 Java，目标做企业级 ERP 后端，IDE 是 IntelliJ IDEA。

详细路线见 [`docs/00_learning_plan.md`](docs/00_learning_plan.md)（12 周、4 阶段）。

## 仓库结构

```
Java-Learn/
├── README.md                # 项目首页 + 进度概览
├── CLAUDE.md                # 本文件
├── docs/
│   ├── 00_learning_plan.md  # 详细路线
│   └── weekN/               # 每周笔记
├── code/
│   ├── weekN/               # 每周代码（IDEA 可直接打开）
│   └── project/             # 最终 ERP 项目（多模块 Maven，从 Week 10 开始）
├── assets/                  # 截图、架构图、UML
└── scripts/                 # SQL 脚本、Docker compose
```

## 工作约定

- **写新章节时**：先在 `docs/weekN/` 加 Markdown 笔记，再到 `code/weekN/` 配可运行示例，最后在 `code/weekN/README.md` 列文件清单 + 自查表
- **代码风格**：注释里写"为什么这么写"和"和 C++/Python 的差异"，而不是字面翻译代码
- **环境**：JDK 21 LTS、Spring Boot 3.x、MyBatis-Plus 3.5、MySQL 8、Redis 7
- **依赖管理**：Week 4 之前用 IDEA 默认；Week 4 起切换 Maven，最终 ERP 用 Maven 多模块
- **不要无谓扩散**：用户偏好聚焦内容，不需要的样板文件（CI、test 占位、空 README）不要预先生成
- **遵循根仓库 `~/Desktop/project/github/CLAUDE.md` 中的 Ruflo 规则**：编辑文件前先读、文件 <500 行、不提交 .env / 密钥

## 学习者已知偏好

- 来自 [`CNN-Learn`](../CNN-Learn/) 的成功体验：周计划 + 数学/原理推导 + 手写 + 工业级实现 + 可玩 demo
- 喜欢和 C++ / Python 对照学习；解释要给出"等价物"
- 用 macOS + IDEA + Docker
- 目标是做"会用 Spring 写 ERP" 而不是"刷面试题"，避免过度学术化的内容（编译原理、JVM 字节码细节等）

## 后续协作提示

如果用户说 "进入 Week N" 或 "我学完 Week N 了"：
1. 检查 `docs/week(N-1)/` 和 `code/week(N-1)/` 是否完整、有 README、有自查表
2. 创建 `docs/weekN/` 的核心笔记（先 `00_*.md` 概览，再分主题）
3. 创建 `code/weekN/` 的最小可运行示例，每个示例配中文注释解释关键点
4. 更新 `docs/weekN/README.md`（如果没有）和文件交叉链接
5. 把本周自查表写清楚，让用户能自我验收

如果用户问 ERP 相关问题：
- 参考开源项目 [若依 ruoyi-vue-pro](https://gitee.com/zhijiantianya/ruoyi-vue-pro) 的表设计和模块划分
- 优先单体 Spring Boot 多模块，不要急于上微服务
- 业务字段以"中国企业 / 中文场景"为准（人民币、增值税、统一社会信用代码）
