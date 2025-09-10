# AI呼叫中心系统 - 前台管理系统

基于 Vue 3 + Element Plus 构建的现代化呼叫中心管理界面，完美对接后台API服务。

## 🎯 项目概述

这是一个完整的AI呼叫中心解决方案，包含：
- **后台服务**: Spring Boot微服务架构 (cc-api + fs-api)
- **前台系统**: Vue 3 + Element Plus现代化界面
- **呼叫引擎**: FreeSwitch语音通信

## 🚀 快速启动

### 1. 启动后台服务

```bash
# 启动cc-api管理服务 (端口8080)
cd cc-api
mvn spring-boot:run

# 启动fs-api呼叫服务 (端口8081) 
cd fs-api
mvn spring-boot:run
```

### 2. 启动前台系统

```bash
# 进入前台目录
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
# 或者使用启动脚本
./start.sh
```

### 3. 访问系统

- **前台管理界面**: http://localhost:3000
- **后台API文档**: http://localhost:7100/swagger-ui.html
- **呼叫API文档**: http://localhost:7200/swagger-ui.html

**默认登录账号**: `admin` / `12345678`

## 📋 功能特性

### 🏠 仪表板
- 📊 实时统计概览
- 📈 通话趋势图表  
- 👥 坐席状态分布
- 📞 最近通话记录

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

## 🛠 技术架构

### 前台技术栈
- **框架**: Vue 3.3.4 + Composition API
- **UI库**: Element Plus 2.3.8
- **路由**: Vue Router 4.2.4
- **状态管理**: Pinia 2.1.6
- **HTTP客户端**: Axios 1.5.0
- **图表**: ECharts 5.4.3
- **构建工具**: Vite 4.4.9

### 后台技术栈
- **框架**: Spring Boot 2.7+
- **数据库**: MySQL + Redis
- **认证**: JWT Token
- **API文档**: Swagger/OpenAPI 3
- **呼叫引擎**: FreeSwitch

## 📁 项目结构

```
ai-call-center/
├── cc-api/                 # 管理后台服务
│   ├── src/main/java/     # Java源码
│   └── src/main/resources/ # 配置文件
├── fs-api/                # 呼叫控制服务  
│   ├── src/main/java/     # Java源码
│   └── src/main/resources/ # 配置文件
├── cc-core/               # 核心业务模块
├── freeswitch/            # FreeSwitch配置
├── frontend/              # 前台管理系统
│   ├── src/
│   │   ├── api/          # API接口
│   │   ├── views/        # 页面组件
│   │   ├── layout/       # 布局组件
│   │   ├── router/       # 路由配置
│   │   └── stores/       # 状态管理
│   ├── package.json      # 依赖配置
│   └── vite.config.js    # 构建配置
└── README.md             # 项目说明
```

## 🔧 开发指南

### 前台开发

1. **添加新页面**
   ```bash
   # 在 src/views 下创建页面组件
   # 在 src/router/index.js 中添加路由
   # 在侧边栏菜单中添加菜单项
   ```

2. **添加新API**
   ```bash
   # 在 src/api 下创建API文件
   # 在页面组件中调用API
   ```

3. **状态管理**
   ```javascript
   // 使用 Pinia 进行状态管理
   import { defineStore } from 'pinia'
   export const useExampleStore = defineStore('example', () => {
     const state = ref('')
     return { state }
   })
   ```

### 后台开发

1. **添加新接口**
   ```java
   @RestController
   @RequestMapping("api/example")
   public class ExampleController {
       @GetMapping("list")
       public CommonResponse list() {
           return new CommonResponse();
       }
   }
   ```

2. **数据库操作**
   ```java
   @Service
   public class ExampleService {
       @Autowired
       private ExampleMapper exampleMapper;
       
       public List<Example> getList() {
           return exampleMapper.selectList();
       }
   }
   ```

## 🌐 API接口

### 认证接口
- `POST /api/index/login` - 用户登录
- `GET /api/index/logout` - 用户登出

### 管理接口
- `GET /api/admin/user` - 用户列表
- `POST /api/admin/user` - 添加用户
- `PUT /api/admin/user` - 更新用户
- `DELETE /api/admin/user/{id}` - 删除用户

### 坐席接口
- `GET /fs-api/agent/list` - 坐席列表
- `POST /fs-api/cti/agent/ready` - 坐席空闲
- `POST /fs-api/cti/agent/notReady` - 坐席忙碌

### 呼叫接口
- `GET /fs-api/cti/admin/call` - 通话记录
- `POST /fs-api/cti/call/makeCall` - 发起呼叫

## 🚀 部署说明

### Docker部署

```bash
# 构建镜像
./build.sh

# 使用Docker Compose启动
cd freeswitch/centos
docker-compose up -d
```

### 生产环境

1. **构建前台**
   ```bash
   cd frontend
   npm run build
   ```

2. **部署后台**
   ```bash
   # 打包
   mvn clean package
   
   # 运行
   java -jar cc-api/target/cc-api-1.0.0.jar
   java -jar fs-api/target/fs-api-1.0.0.jar
   ```

3. **Nginx配置**
   ```nginx
   server {
       listen 80;
       server_name your-domain.com;
       
       location / {
           root /path/to/frontend/dist;
           try_files $uri $uri/ /index.html;
       }
       
       location /api {
           proxy_pass http://localhost:8080;
       }
       
       location /fs-api {
           proxy_pass http://localhost:8081;
       }
   }
   ```

## 🔍 故障排除

### 常见问题

1. **前台无法连接后台**
   - 检查后台服务是否启动
   - 检查端口是否正确 (8080, 8081)
   - 检查代理配置

2. **登录失败**
   - 检查用户名密码是否正确
   - 检查后台数据库连接
   - 查看后台日志

3. **页面空白**
   - 检查浏览器控制台错误
   - 检查网络请求是否正常
   - 清除浏览器缓存

### 日志查看

```bash
# 后台日志
tail -f logs/cc-api.log
tail -f logs/fs-api.log

# 前台日志
# 查看浏览器开发者工具控制台
```

## 📞 技术支持

如有问题，请查看：
1. 项目README文档
2. API文档 (Swagger)
3. 浏览器控制台错误信息
4. 后台服务日志

## 📄 许可证

MIT License

---

**🎉 恭喜！您已成功搭建了完整的AI呼叫中心系统！**
