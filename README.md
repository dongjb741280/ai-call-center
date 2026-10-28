# AI呼叫中心系统

基于FreeSwitch和Spring Boot构建的企业级AI呼叫中心系统，支持多租户、多技能组、智能路由等完整呼叫中心功能。

[![GitHub stars](https://img.shields.io/github/stars/dongjb741280/ai-call-center?style=social)](https://github.com/dongjb741280/ai-call-center)

> 如果这个项目对你有帮助，恳请点个 Star 支持！

官网：<https://dongjb741280.github.io/ai-call-center/>

## 项目概述

这是一个采用微服务架构的AI呼叫中心解决方案，集成了FreeSwitch作为呼叫引擎，提供完整的呼叫管理、坐席管理、智能路由、监控统计等功能。系统支持多企业、多技能组管理，具备高并发、高可用的企业级特性。

> 本项目主要适用于巨量人工的呼入，呼出场景。或者作为AI智能无人呼叫的底座。
>
> 纯粹的AI呼叫中心(无人优先)，请参照项目
>
> <https://github.com/dongjb741280/IntelliCall-Pro>

## 技术架构

### 项目结构

```text
ai-call-center/
├── voxai-common/          # 核心业务模块（数据层）
├── voxai-admin/           # 管理API服务（端口 7100）
├── voxai-call/            # FreeSwitch集成服务（端口 7200）
├── voxai-ivr/             # IVR 引擎服务（端口 7300）
├── freeswitch/            # FreeSwitch配置和Docker镜像
├── portal/                # 产品门户（声枢 VoxHub）
├── docs/                  # 业务文档（范围/流程/数据模型/引擎接口）
├── .github/workflows/     # GitHub Actions（portal CI/CD）
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

### voxai-ivr（IVR 引擎服务）

- **流程引擎**: 解析 `cc_ivr_workflow.content` 为节点图，按 play/input/transfer 执行
- **交互**: 通过 FreeSwitch `playback` / `play_and_get_digits` 放音与收键
- **回控**: 完成后回调 `voxai-call` 转技能组/坐席/VDN/挂机

## 核心功能

### 1. 呼叫管理

- **呼入处理**: 自动路由到技能组
- **呼出功能**: 坐席主动外呼
- **呼叫转接**: 坐席间转接、技能组转接
- **呼叫保持**: 保持/取消保持
- **静音控制**: 坐席静音/取消静音

### 2. 呼入路由（VDN / IVR）

- **VDN 识别**: 特服号匹配 `cc_vdn_phone` → `cc_vdn_code`，按 `cc_vdn_schedule` 日程（星期/日期/时间段）选生效路由
- **路由类型**（`cc_vdn_config.route_type`）: 技能组 / 放音 / IVR / 坐席 / 外呼
- **按键导航**: 放音后 `play_and_get_digits` 收键，按 `cc_vdn_dtmf` 按键匹配转技能组/IVR/路由字码/坐席分机/挂机
- **IVR 引擎**: `voxai-ivr` 解析 `cc_ivr_workflow` 流程定义，按 play/input/transfer 执行后回控

### 3. 智能路由（ACD）

- **多种分配策略**:
  - 最少接听次数
  - 最少通话时长
  - 最长就绪时间
  - 随机分配
  - 轮询分配
  - 自定义分配

### 4. 技能组管理

- **排队策略**: 优先级排队、VIP排队
- **溢出处理**: 排队超时、技能组溢出
- **记忆坐席**: 客户历史坐席匹配

### 5. 坐席管理

- **状态管理**: 就绪、忙碌、通话中、离线等
- **多登录方式**: SIP、WebRTC、电话
- **权限控制**: 基于角色的权限管理

### 6. 监控统计

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
2. 初始化数据库：建表脚本与迁移 SQL 见 `docs/sql/`（`voxai.sql` 全量库导出、`migration_*.sql` 增量迁移）
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

# voxai-ivr（IVR 引擎，端口 7300）
java -jar voxai-ivr/target/voxai-ivr-1.0.0.jar
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
- **voxai-ivr**: IVR 引擎服务（端口：7300）
- **freeswitch**: 呼叫引擎（端口：5060）

### 数据存储

- **MySQL**: 业务数据存储
- **Redis**: 缓存和会话存储
- **MinIO**: 录音文件和媒体存储

### CI/CD 流水线

```text
编译 → 单元测试 → SonarQube → 打包 → Jib 构建(Harbor) → Nexus 发布 → 1Panel API 部署
                                                                         ↓
                                                              Watchtower(k8s-work-4)
                                                              双重保障，自动拉取重启
```

- **CI（GitLab）**：构建、测试、代码扫描，Jib 推送镜像到 Harbor
- **Harbor**：本地镜像仓库（`localhost:8444`）
- **CD 双重保障**：
  - **方案 A（1Panel API）**：CI 主动触发 `images/pull` + `containers/upgrade`
  - **方案 B（Watchtower）**：k8s-work-4 后台运行，定时检测自动部署
  - 两者并存互不冲突，任一成功即可

#### Watchtower 部署（k8s-work-4）

```bash
docker run -d \
  --name watchtower --restart unless-stopped \
  -v /var/run/docker.sock:/var/run/docker.sock \
  -v /root/.docker/config.json:/config.json \
  -e DOCKER_CONFIG=/ \
  containrrr/watchtower \
  --interval 30 --cleanup \
  voxai-admin voxai-call
```

> 依赖 `/root/.docker/config.json` 中的 Harbor 登录凭据

#### GitLab CI Variables 配置

| 变量 | 说明 |
| ------ | ------ |
| `HARBOR_REGISTRY` | Harbor 镜像仓库地址 |
| `HARBOR_REGISTRY_USER` | Harbor 用户名 |
| `HARBOR_REGISTRY_PASS` | Harbor 密码 |
| `ONEPANEL_URL` | 1Panel 面板地址（方案 A） |
| `ONEPANEL_API_KEY` | 1Panel API Key（方案 A） |
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

> 管理端上下文 `/voxai-admin`（端口 7100），呼叫端上下文 `/voxai-call`（端口 7200）。

### 主要接口

#### 登录与探活

- `POST /voxai-admin/index/login` - 管理员登录
- `GET /voxai-admin/index/logout` - 管理员退出
- `GET /voxai-admin/index/health` - 管理端健康检查
- `GET /voxai-call/index/health` - 呼叫端健康检查

#### 坐席与技能组（voxai-admin `/config`）

- `GET /voxai-admin/config/agent` - 坐席列表
- `POST /voxai-admin/config/agent` - 添加坐席
- `GET /voxai-admin/config/group` - 技能组列表
- `POST /voxai-admin/config/group` - 添加技能组
- `GET /voxai-admin/config/group/agent/{id}` - 技能组坐席

#### 呼叫控制（voxai-call `/cti`）

- `POST /voxai-call/cti/call/makeCall` - 发起呼叫
- `POST /voxai-call/cti/call/hangup` - 挂断
- `POST /voxai-call/cti/call/hold` / `cancelHold` - 保持 / 取消保持
- `POST /voxai-call/cti/call/readyMute` / `cancelMute` - 静音 / 取消静音
- `POST /voxai-call/cti/agent/ready` / `notReady` - 置闲 / 置忙
- `POST /voxai-call/cti/agent/logout` - 坐席登出
- `GET /voxai-call/cti/admin/call` - 通话列表

## 监控和运维

### 健康检查

- `GET /voxai-admin/index/health` - voxai-admin 健康检查（端口 7100）
- `GET /voxai-call/index/health` - voxai-call 健康检查（端口 7200）

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

```text
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
- 邮箱: <dongjb741280@gmail.com>
- 项目地址: [GitHub Repository](https://github.com/dongjb741280/ai-call-center)

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
