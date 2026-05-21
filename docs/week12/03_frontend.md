# Week 12 §03 · 前端对接说明

> 本仓库聚焦后端。前端建议直接复用成熟模板：**vue-element-admin** 或 **若依的前端**。

---

## 1. 推荐模板

| 模板 | 特点 | 链接 |
|------|------|------|
| **vue-element-admin** | 老牌、Vue 2 / 3 双版本、教程多 | https://github.com/PanJiaChen/vue-element-admin |
| **若依 ruoyi-vue3** | 完整 ERP 风格、已对接 RBAC | https://gitee.com/y_project/RuoYi-Vue3 |
| **vue-vben-admin** | 现代 Vue 3 + TypeScript + Vite，颜值最高 | https://github.com/vbenjs/vue-vben-admin |

学习阶段：直接克隆 **若依的前端**，改后端地址指向本仓库的 8080 端口最快。

---

## 2. 对接要点

### 2.1 Axios 基础配置

```js
// src/utils/request.js
import axios from 'axios';

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE,
  timeout: 10000
});

// 请求拦截器：加 token
request.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

// 响应拦截器：解 Result 封装
request.interceptors.response.use(response => {
  const r = response.data;
  if (r.code === 0) return r.data;        // 直接返回 data
  if (r.code === 401) {
    localStorage.removeItem('token');
    location.href = '/login';
  }
  ElMessage.error(r.message);
  return Promise.reject(r);
});

export default request;
```

### 2.2 登录流程

```js
async function login(username, password) {
  const data = await request.post('/api/login', { username, password });
  localStorage.setItem('token', data.token);
  localStorage.setItem('permissions', JSON.stringify(data.permissions));
}
```

### 2.3 菜单 / 路由

启动后调 `/api/menus/me` 拿后端返回的菜单树，动态注册路由（vue-router 的 `addRoute`）。

### 2.4 按钮权限指令

```js
// v-permission="['mat:material:add']"
app.directive('permission', {
  mounted(el, binding) {
    const perms = JSON.parse(localStorage.getItem('permissions') || '[]');
    if (perms.includes('*:*:*')) return;
    if (!binding.value.some(p => perms.includes(p))) {
      el.remove();
    }
  }
});
```

---

## 3. 最小对接流程

1. 克隆若依前端
2. 改 `.env.development` 里 `VITE_APP_BASE_API=http://localhost:8080`
3. `pnpm install && pnpm dev`
4. 用 `admin / admin123` 登录
5. 进物料 / 库存等模块对接接口

---

## 4. 自查

- [ ] 前端能登录拿到 token
- [ ] 菜单按权限渲染
- [ ] 物料 CRUD 全套接口走通
- [ ] 一键 `docker-compose up` + 前端 `pnpm dev` 整个系统跑起来

---

## 5. 如果不想做前端

直接用 **Knife4j 接口文档**（`http://localhost:8080/doc.html`）演示，能调所有接口，足以作为学习项目的演示成果。
