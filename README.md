# AI呼叫中心系统

基于FreeSwitch和Spring Boot构建的企业级AI呼叫中心系统，支持多租户、多技能组、智能路由等完整呼叫中心功能。

## 项目概述

这是一个采用微服务架构的AI呼叫中心解决方案，集成了FreeSwitch作为呼叫引擎，提供完整的呼叫管理、坐席管理、智能路由、监控统计等功能。系统支持多企业、多技能组管理，具备高并发、高可用的企业级特性。

## 技术架构

### 项目结构
```
ai-call-center/
├── voxai-common/          # 核心业务模块（数据层）
├── voxai-admin/           # 管理API服务（端口 7100）
├── voxai-call/            # FreeSwitch集成服务（端口 7200）
├── freeswitch/            # FreeSwitch配置和Docker镜像
├── .gitlab-ci.yml         # GitLab CI/CD 流水线
└── Dockerfile             # 多阶段Docker构建
```

### 技术栈
- **后端框架**: Spring Boot 2.3.12
- **微服务**: Spring Cloud + Nacos
- **数据库**: MySQL/PostgreSQL/Oracle/SQL Server（多数据库支持）
- **缓存**: Redis
- **消息队列**: RabbitMQ（可选Kafka）
- **文件存储**: MinIO
- **呼叫引擎**: FreeSwitch
- **构建工具**: Maven + Jib（无需 Docker Daemon 构建镜像）
- **镜像仓库**: Harbor
- **容器化**: Docker
- **CI/CD**: GitLab CI + Watchtower 自动部署

## 核心模块

### voxai-common（核心业务模块）
- **实体类**: 企业、坐席、技能组、通话记录等业务实体
- **数据访问**: MyBatis映射器
- **工具类**: 加密、ID生成等通用工具

### voxai-admin（管理API服务）
- **管理功能**: 坐席管理、技能组管理、企业配置
- **认证授权**: Spring Security + JWT
- **定时任务**: Quartz调度器
- **数据统计**: 通话统计、坐席状态统计
- **文件管理**: MinIO集成

### voxai-call（FreeSwitch集成服务）
- **呼叫控制**: 呼入/呼出、转接、保持、静音等
- **智能路由**: ACD（自动呼叫分配）算法
- **坐席状态**: 实时状态管理和监控
- **WebSocket**: 实时通信
- **TCP服务**: 坐席状态订阅
- **事件处理**: FreeSwitch事件监听和处理

## 核心功能

### 1. 呼叫管理
- **呼入处理**: 自动路由到技能组
- **呼出功能**: 坐席主动外呼
- **呼叫转接**: 坐席间转接、技能组转接
- **呼叫保持**: 保持/取消保持
- **静音控制**: 坐席静音/取消静音

### 2. 智能路由（ACD）
- **多种分配策略**:
  - 最少接听次数
  - 最少通话时长
  - 最长就绪时间
  - 随机分配
  - 轮询分配
  - 自定义分配

### 3. 技能组管理
- **排队策略**: 优先级排队、VIP排队
- **溢出处理**: 排队超时、技能组溢出
- **记忆坐席**: 客户历史坐席匹配

### 4. 坐席管理
- **状态管理**: 就绪、忙碌、通话中、离线等
- **多登录方式**: SIP、WebRTC、电话
- **权限控制**: 基于角色的权限管理

### 5. 监控统计
- **实时监控**: 坐席状态、通话状态
- **数据统计**: 通话量、接通率、平均通话时长
- **报表导出**: Excel报表导出

## 快速开始

### 环境要求
- JDK 1.8+
- Maven 3.6+
- MySQL 5.7+ / PostgreSQL 10+ / Oracle 11g+ / SQL Server 2016+
- Redis 5.0+
- FreeSwitch 1.10+
- Docker & Docker Compose

### 数据库配置
1. 创建数据库
2. 执行SQL脚本：`voxai-common/src/main/resources/sql/tables_mysql_innodb.sql`
3. 修改配置文件中的数据库连接信息

### 配置文件
修改 `application-dev.properties` 中的配置：
```properties
# 数据库配置
spring.datasource.url=jdbc:mysql://localhost:3306/voxai
spring.datasource.username=root
spring.datasource.password=your_password

# Redis配置
spring.redis.host=localhost
spring.redis.port=6379
spring.redis.password=your_redis_password

# MinIO配置
voxai.minio.endpoint=http://localhost:9000
voxai.minio.access.key=admin
voxai.minio.secret.key=your_secret_key
```

### 构建和运行

#### 1. 编译项目
```bash
# 编译全部模块（跳过测试）
mvn clean install -DskipTests

# 编译单个模块
mvn clean install -pl voxai-admin -DskipTests
```

#### 2. 本地启动
```bash
# voxai-admin（管理后台，端口 7100）
java -jar voxai-admin/target/voxai-admin-1.0.0.jar

# voxai-call（呼叫服务，端口 7200）
java -jar voxai-call/target/voxai-call-1.0.0.jar
```

#### 3. Jib 构建镜像（无需 Docker Daemon）
```bash
# 构建并推送到 Harbor
mvn compile jib:build -pl voxai-admin,voxai-call \
  -DsendCredentialsOverHttp=true \
  -Ddocker.repostory=localhost:8444 \
  -Djib.to.tags=latest

# 或构建到本地 Docker Daemon
mvn compile jib:dockerBuild -pl voxai-admin
```

#### 4. Docker 运行
```bash
# 从 Harbor 拉取并启动
docker pull localhost:8444/voxai/voxai-admin:latest
docker run -d --name voxai-admin --network host \
  localhost:8444/voxai/voxai-admin:latest
```

### 接口文档（Swagger/OpenAPI）

- voxai-admin：`http://localhost:7100/voxai-admin/swagger-ui/index.html`
- voxai-call：`http://localhost:7200/voxai-call/swagger-ui/index.html`

## 部署架构

### 微服务部署
- **voxai-admin**: 管理后台服务（端口：7100）
- **voxai-call**: 呼叫控制服务（端口：7200）
- **freeswitch**: 呼叫引擎（端口：5060）

### 数据存储
- **MySQL**: 业务数据存储
- **Redis**: 缓存和会话存储
- **MinIO**: 录音文件和媒体存储

### CI/CD 流水线

```
编译 → 单元测试 → SonarQube → 打包 → Jib 构建(Harbor) → Nexus 发布
                                                         ↓
                                              Watchtower(k8s-work-4)
                                              自动拉取镜像并重启容器
```

- **CI（GitLab）**：负责构建、测试、代码扫描，Jib 构建镜像并推送到 Harbor
- **Harbor**：本地镜像仓库（`localhost:8444`）
- **CD（Watchtower）**：部署在目标服务器上，定时检测 Harbor 中新镜像，自动拉取并重启容器

#### Watchtower 部署（k8s-work-4）

```bash
docker run -d \
  --name watchtower \
  --restart unless-stopped \
  -v /var/run/docker.sock:/var/run/docker.sock \
  -v /root/.docker/config.json:/config.json \
  -e DOCKER_CONFIG=/ \
  containrrr/watchtower \
  --interval 30 \
  --cleanup \
  voxai-admin voxai-call
```

> 依赖 `/root/.docker/config.json` 中的 Harbor 登录凭据（`docker login` 后自动生成）

#### GitLab CI Variables 配置

| 变量 | 说明 |
|------|------|
| `HARBOR_REGISTRY` | Harbor 镜像仓库地址 |
| `HARBOR_REGISTRY_USER` | Harbor 用户名 |
| `HARBOR_REGISTRY_PASS` | Harbor 密码 |
| `SONAR_HOST_URL` | SonarQube 服务地址 |
| `SONAR_TOKEN` | SonarQube Token |
| `NEXUS_URL` / `NEXUS_USER` / `NEXUS_PASSWORD` | Nexus 仓库认证 |

## 配置说明

### 多环境支持
- 开发环境（dev）
- 生产环境（prod）
- Nacos配置中心集成

### 多数据库支持
- MySQL（主要）
- PostgreSQL
- Oracle
- SQL Server

### 安全特性
- JWT Token认证
- 密码加密存储
- 接口权限控制

## API文档

### 主要接口

#### 坐席管理
- `POST /index/login` - 坐席登录
- `GET /index/logout` - 坐席退出
- `GET /agent/list` - 坐席列表
- `POST /agent/add` - 添加坐席

#### 呼叫管理
- `POST /call/make` - 发起呼叫
- `POST /call/answer` - 接听呼叫
- `POST /call/hangup` - 挂断呼叫
- `POST /call/transfer` - 转接呼叫

#### 技能组管理
- `GET /group/list` - 技能组列表
- `POST /group/add` - 添加技能组
- `POST /group/agent/add` - 添加坐席到技能组

## 监控和运维

### 健康检查
- `GET /voxai-admin/manager/health` - voxai-admin 健康检查（端口 7100）
- `GET /voxai-call/manager/health` - voxai-call 健康检查（端口 7200）

### 日志管理
- 支持Logback日志配置
- 支持ELK日志收集
- 结构化日志输出

### 性能监控
- Prometheus指标收集
- JVM性能监控
- 数据库连接池监控

## 开发指南

### 代码结构
```
src/main/java/
├── com.voxai.api/          # API服务
│   ├── web/                 # 控制器层
│   ├── service/             # 服务层
│   └── configration/        # 配置类
├── com.voxai.core/         # 核心模块
│   ├── entity/              # 实体类
│   ├── mapper/              # 数据访问层
│   └── enums/               # 枚举类
└── com.voxai/              # FreeSwitch集成
    ├── cc/                  # 呼叫控制
    ├── fs/                  # FreeSwitch事件处理
    └── websocket/           # WebSocket通信
```

### 开发规范
- 遵循阿里巴巴Java开发手册
- 使用统一的异常处理机制
- 支持多租户数据隔离
- 异步处理提升性能

## 常见问题

### Q: 如何配置FreeSwitch？
A: 参考 `freeswitch/` 目录下的配置文件，支持CentOS和Debian两种系统。

### Q: 如何添加新的分配策略？
A: 在 `voxai-call/src/main/java/com/voxai/cc/acd/assign/` 目录下实现新的分配算法。

### Q: 如何集成第三方系统？
A: 通过WebHook回调或消息队列进行集成，支持HTTP和RabbitMQ。

### Q: 如何扩展坐席功能？
A: 在 `voxai-common` 模块中添加新的实体和接口，在 `voxai-admin` 中实现具体业务逻辑。

## 许可证

本项目采用 MIT 许可证，详情请参阅 [LICENSE](LICENSE) 文件。

## 贡献指南

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 联系方式

- 作者: dongjianbin
- 邮箱: dongjb741280@gmail.com
- 项目地址: [GitHub Repository](https://github.com/your-username/ai-call-center)

## 更新日志

### v1.0.1 (2026-07-31)
- 集成 Jib 构建镜像，免除 Docker Daemon 依赖
- 接入 GitLab CI + Harbor + Watchtower 自动部署流水线
- 统一 Harbor 环境变量命名（`HARBOR_REGISTRY` 前缀）

### v1.0.0 (2024-01-01)
- 初始版本发布
- 支持基本的呼叫中心功能
- 集成FreeSwitch呼叫引擎
- 支持多租户管理
- 提供完整的API接口

---

**注意**: 本项目仅供学习和研究使用，商业使用请确保遵守相关法律法规。
