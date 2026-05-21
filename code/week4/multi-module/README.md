# Multi-Module Maven 示例骨架

为 Week 10 ERP 项目铺垫的多模块结构示例。

```
multi-module/
├── pom.xml                  ← 父 pom (packaging: pom)
├── common/pom.xml           ← 公共工具
├── service/pom.xml          ← 业务层
└── web/pom.xml              ← 主启动
```

学完 Week 4 后，可以自己手敲一个三模块的最小 demo：
- `common` 提供一个工具类
- `service` 依赖 `common`，提供一个业务方法
- `web` 依赖 `service`，main 方法调用业务方法打印结果

Week 10 ERP 项目会以此为模板真实落地。
