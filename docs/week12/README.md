# Week 12 · 整合 + 前后端联调 + Docker 部署

> 收尾：把 Week 10-11 的 ERP 项目打磨好，加上导入导出 + 一键部署。

## 笔记顺序

| 序号 | 文件 | 主题 |
|------|------|------|
| 00 | [`00_excel.md`](00_excel.md) | EasyExcel 导入导出 |
| 01 | [`01_dashboard_charts.md`](01_dashboard_charts.md) | Dashboard 概念 + 后端聚合接口 |
| 02 | [`02_docker_deploy.md`](02_docker_deploy.md) | Dockerfile + docker-compose 部署全栈 |
| 03 | [`03_frontend.md`](03_frontend.md) | 前端模板对接说明（vue-element-admin / ruoyi-vue） |

## 配套代码

→ [`../../code/project/`](../../code/project/) —— 在 Week 10-11 的基础上加 Excel / Dashboard / Docker
→ [`../../docker/`](../../docker/) —— Dockerfile + compose

## 本周里程碑

- 物料 / 库存能导出 Excel
- Dashboard 接口：今日入库 / 出库 / 库存总值 / 各物料库存饼图
- `docker-compose up -d` 一键起 MySQL + Redis + 应用
- 仓库 README 含架构图、截图、部署文档
- （可选）跑通 vue-element-admin 前端
