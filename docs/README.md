# AI 呼叫中心系统 · 业务文档

> 本文档基于代码库实际实现梳理而成，术语对齐行业通用呼叫中心。文档描述的均为仓库内真实存在的模块、流程与数据表。

## 文档导航

| 文档 | 内容 |
| --- | --- |
| [01-业务范围](01-业务范围.md) | 系统定位、多租户模型、功能清单（按领域划分） |
| [02-业务流程](02-业务流程.md) | 核心业务场景与 Mermaid 流程图（呼入/呼出/ACD/转接咨询/坐席状态机等） |
| [03-数据模型](03-数据模型.md) | 全部数据表、字段说明、表间关系（ER 图） |
| [04-外部引擎接口约定](04-外部引擎接口约定.md) | IVR 引擎、外呼任务引擎的对外接口约定与缺口 |

## 系统一句话定位

基于 **FreeSwitch + Spring Boot / Spring Cloud（Nacos）** 的企业级 **AI 呼叫中心**，提供多租户、
多技能组、ACD 智能路由、IVR 语音导航、AI（ASR/TTS）机器人、外呼任务、话单与录音、监控统计等完整能力。
适合**海量人工呼入/呼出**场景，也可作为 **AI 智能无人呼叫**的底座。

## 模块一览

| 模块 | 端口 | 包名 | 职责 |
| --- | --- | --- | --- |
| **voxai-common** | — | `com.voxai.core` | 公共库：实体、MyBatis Mapper、枚举、常量、PO/VO、策略接口 |
| **voxai-admin** | 7100（8080） | `com.voxai.api` | 管理 API：企业/坐席/技能组/技能/VDN/IVR/溢出/号码/路由/外呼任务/话单/统计/权限 CRUD，Quartz 定时统计 |
| **voxai-call** | 7200（8081） | `com.voxai` | 呼叫控制：FreeSwitch ESL 事件、ACD 路由、WebSocket 实时通信、TCP 状态订阅 |
| **ai-call-center-web**（独立项目） | 3000 | — | Vue3 + Element Plus + Pinia + JsSIP 软电话坐席前端 |

### 外部协作服务

`StationType` 枚举与 `TransferIvrHandler` 中体现的外部组件：

| 类型 | 说明 |
| --- | --- |
| `CC_API` | 管理 API（voxai-admin） |
| `FS_API` | FreeSwitch API |
| `CC_IVR`（:7300） | IVR 引擎，独立服务，通过 HTTP `/cc-ivr/index/start` 拉起 |
| `FS_MEDIA`（:7430） | FreeSwitch 媒体服务，录音文件经此拉取后上传 MinIO |

## 核心概念速览

- **企业（租户）**：`cc_company`，多租户隔离的基本单位，含计费、套餐额度（坐席数/IVR 通道/外呼并发等）。
- **坐席（Agent）**：`cc_agent`，工号 + 分机 + SIP 账号，属于技能组。
- **技能组（Group）**：`cc_group`，呼叫路由与排队的核心容器，绑定分配策略与溢出策略。
- **技能（Skill）**：`cc_skill`，可跨技能组的能力标签，用于精细路由。
- **VDN（呼入路由）**：`cc_vdn_code` + `cc_vdn_phone`，按特服号/被叫号码将呼入电话路由到技能组/IVR/坐席/放音/外线。
- **IVR**：`cc_ivr_workflow`，语音流程定义，由 `cc-ivr` 服务执行。
- **AI 引擎**：`cc_ai_engine`，ASR/TTS（MRCP）配置，用于机器人、振铃识别（ring_asr）、语音合成放音。
- **溢出策略**：`cc_overflow_config` 等，排队超时/队列超限时的处置（转组/转 IVR/转 VDN/挂机）。
- **话单**：`cc_call_log`（主话单）、`cc_call_detail`（流程轨迹）、`cc_call_device`（分机明细）。
- **外呼任务**：`cc_task_config` 等，预测式/巡航式外呼任务、名单（数据源）管理。

## 术语对照

| 本系统 | 说明 |
| --- | --- |
| VDN | 虚拟号码 / 呼入路由码（`vdn_code`），对应「呼入路由表」 |
| 特服号 | `vdn_phone` 中与 VDN 绑定的被叫号码 |
| 日程 | `vdn_schedule`，按时间段/星期生效的路由计划 |
| 字冠 | `route_call`，按被叫号码前缀路由到网关组 |
| 媒体网关 | `route_getway`，FreeSwitch 出局/入局的中继配置 |
| 排队策略 | 先进先出 / VIP / 自定义（`LineupStrategy`） |
| 分配策略 | 最长空闲/最少应答/最少通话/轮选/随机等（`AgentStrategy`） |
| 记忆坐席 | 按主叫号码匹配上次服务坐席（`cc_group_memory`） |
| 话后（AFTER） | 通话结束后的整理态，可自动转空闲 |
| 班长监控 | 监听（静默）/强插（三方）/耳语（单向） |

## 默认账号

管理端登录：`admin` / `12345678`
