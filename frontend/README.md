# AI呼叫中心前台管理系统

基于 Vue 3 + Element Plus 构建的现代化呼叫中心管理界面。

## 功能特性

### 🏠 仪表板
- 实时统计概览
- 通话趋势图表
- 坐席状态分布
- 最近通话记录

### 👥 系统管理
- **用户管理**: 用户增删改查、角色分配、状态管理
- **角色管理**: 角色权限配置、菜单绑定
- **菜单管理**: 系统菜单配置、权限控制
- **企业管理**: 企业信息管理、状态控制

### 🎧 坐席管理
- **坐席列表**: 坐席信息管理、状态控制
- **技能组管理**: 技能组配置、坐席分配
- **状态管理**: 空闲/忙碌状态切换

### 📞 呼叫管理
- **通话记录**: 通话历史查询、详情查看
- **呼叫统计**: 数据统计、图表展示
- **录音播放**: 通话录音回放

## 技术栈

- **框架**: Vue 3.3.4
- **UI库**: Element Plus 2.3.8
- **路由**: Vue Router 4.2.4
- **状态管理**: Pinia 2.1.6
- **HTTP客户端**: Axios 1.5.0
- **图表**: ECharts 5.4.3
- **构建工具**: Vite 4.4.9

## 快速开始

### 环境要求

- Node.js >= 16.0.0
- npm >= 8.0.0

### 安装依赖

```bash
npm install
```

### 开发环境

```bash
npm run dev
```

访问 http://localhost:3000

### 生产构建

```bash
npm run build
```

### 预览构建结果

```bash
npm run preview
```

## 项目结构

```
frontend/
├── public/                 # 静态资源
├── src/
│   ├── api/               # API接口
│   │   ├── request.js     # Axios配置
│   │   ├── auth.js        # 认证相关API
│   │   ├── admin.js       # 管理相关API
│   │   ├── agent.js       # 坐席相关API
│   │   └── call.js        # 呼叫相关API
│   ├── components/        # 公共组件
│   ├── layout/           # 布局组件
│   │   └── Index.vue     # 主布局
│   ├── router/           # 路由配置
│   │   └── index.js      # 路由定义
│   ├── stores/           # 状态管理
│   │   └── user.js       # 用户状态
│   ├── views/            # 页面组件
│   │   ├── Login.vue     # 登录页
│   │   ├── Dashboard.vue # 仪表板
│   │   ├── admin/        # 管理页面
│   │   ├── agent/        # 坐席页面
│   │   └── call/         # 呼叫页面
│   ├── App.vue           # 根组件
│   ├── main.js           # 入口文件
│   └── style.css         # 全局样式
├── index.html            # HTML模板
├── package.json          # 项目配置
├── vite.config.js        # Vite配置
└── README.md            # 项目说明
```

## API接口

### 认证接口
- `POST /api/index/login` - 用户登录
- `GET /api/index/logout` - 用户登出

### 管理接口
- `GET /api/admin/user` - 用户列表
- `POST /api/admin/user` - 添加用户
- `PUT /api/admin/user` - 更新用户
- `DELETE /api/admin/user/{id}` - 删除用户

### 坐席接口
- `GET /voxai-call/agent/list` - 坐席列表
- `POST /voxai-call/cti/agent/ready` - 坐席空闲
- `POST /voxai-call/cti/agent/notReady` - 坐席忙碌

### 呼叫接口
- `GET /voxai-call/cti/admin/call` - 通话记录
- `POST /voxai-call/cti/call/makeCall` - 发起呼叫

## 配置说明

### 代理配置

开发环境已配置API代理：

```javascript
// vite.config.js
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8080',  // voxai-admin服务
      changeOrigin: true,
      rewrite: (path) => path.replace(/^\/api/, '')
    },
    '/voxai-call': {
      target: 'http://localhost:8081',  // voxai-call服务
      changeOrigin: true,
      rewrite: (path) => path.replace(/^\/voxai-call/, '')
    }
  }
}
```

### 环境变量

可以创建 `.env` 文件配置环境变量：

```bash
# API基础地址
VITE_API_BASE_URL=http://localhost:8080
VITE_FS_API_BASE_URL=http://localhost:8081

# 应用标题
VITE_APP_TITLE=AI呼叫中心管理系统
```

## 开发指南

### 添加新页面

1. 在 `src/views` 下创建页面组件
2. 在 `src/router/index.js` 中添加路由配置
3. 在侧边栏菜单中添加菜单项

### 添加新API

1. 在 `src/api` 下创建对应的API文件
2. 在 `src/api/request.js` 中配置请求拦截器
3. 在页面组件中调用API

### 状态管理

使用 Pinia 进行状态管理：

```javascript
// stores/example.js
import { defineStore } from 'pinia'

export const useExampleStore = defineStore('example', () => {
  const state = ref('')
  
  const updateState = (newState) => {
    state.value = newState
  }
  
  return { state, updateState }
})
```

## 部署说明

### Nginx配置

```nginx
server {
    listen 80;
    server_name your-domain.com;
    
    location / {
        root /path/to/dist;
        try_files $uri $uri/ /index.html;
    }
    
    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
    
    location /voxai-call {
        proxy_pass http://localhost:8081;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

### Docker部署

```dockerfile
FROM node:16-alpine as build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

## 浏览器支持

- Chrome >= 87
- Firefox >= 78
- Safari >= 14
- Edge >= 88

## 许可证

MIT License


