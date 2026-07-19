# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run Commands

```bash
# Build all modules (skip tests)
mvn clean install -DskipTests=true

# Build with tests
mvn clean install

# Build single module
mvn clean install -pl cc-core -DskipTests=true

# Docker build (after mvn install)
./build.sh

# Frontend
cd frontend && npm install && npm run dev    # dev server on :3000, proxies to cc-api:7100 and fs-api:7200
cd frontend && npm run build                 # production build to dist/
```

## Architecture Overview

This is a **call center platform** built as a multi-module Maven project (Spring Boot 2.3 + Java 8 + Spring Cloud + Nacos).

### Modules

| Module | Port | Package | Purpose |
|--------|------|---------|---------|
| **cc-core** | — | `com.voice9.core` | Shared library: entities, MyBatis mappers, enums, constants, PO/VO classes, strategy interfaces |
| **cc-api** | 8080 | `com.voice9.api` | Admin REST API: company/user/agent/skill/group CRUD, statistics, Quartz scheduling |
| **fs-api** | 8081 | `org.voice9` | Call control service: connects to FreeSwitch via ESL, WebSocket for real-time agent comms, ACD routing |
| **frontend** | 3000 | — | Vue 3 + Vite + Element Plus + Pinia + JsSIP (softphone). Proxy `/cc-api` → `:7100`, `/fs-api` → `:7200` |

### Key Architectural Patterns

**Handler dispatch pattern** (fs-api): `@HandlerType("EVENT_NAME")` annotation on handler classes → `HandlerProcessor` (a `BeanFactoryPostProcessor`) scans and registers them into `HandlerContext` at startup. At runtime, `HandlerContext.getInstance(type)` returns the Spring bean. This pattern is used for:
- **FreeSwitch events** (`cc.fs.handler`): `FsAnswerHandler`, `FsBridgeHandler`, `FsHangupCompleteHandler`, etc. — each annotated with the ESL event name.
- **WebSocket commands** (`cc.websocket.handler`): `WsLoginHandler`, `WsMakeCallHandler`, `WsAnswerHandler`, `WsTransferHandler`, etc.
- **Command handlers** (`cc.command`): `GroupHandler`, `TransferCallHandler`, `OverFlowHandler`, etc.
- **TCP events** (`cc.tcp.handler`): agent state subscription.

**FreeSwitch integration** (`FsListen` at `fs-api/.../cc/fs/FsListen.java`):
- Maintains a pool of ESL inbound clients (one per FreeSwitch server, discovered from DB `station` table).
- Subscribes to ALL ESL events; `EventType` enum maps event names to Java event classes.
- Routes events to handlers via `HandlerContext`, using a fixed thread pool (configurable via `fs.thread.num`) where hangup events for the same `callId` go to the same thread.
- Makes outbound calls via `bgapi originate`, bridges/transfers via `uuid_bridge`/`uuid_transfer`.

**ACD routing** (fs-api `cc.acd`):
- `acd/assign/` — Agent selection strategies: `LeastAnswerAssign`, `LeastTalkAssign`, `LongReadyAssign`, `PollAssign`, `RandomAssign`, `TotalAfterTimeAssign`, etc. — all implementing `AgentStrategy`.
- `acd/lineup/` — Caller queuing strategies: `DefaultLineupStrategy`, `VipLineupStrategy`, `CustomLineupStrategy` — all implementing `LineupStrategy`.

**WebSocket layer** (`WebSocketManager` + `WebSocketHandler`): Establishes persistent connections with agent frontends. Receives commands (login, ready, makeCall, answer, transfer, hangup, hold), dispatches to `Ws*Handler` classes. Pushes events back to agents (call ringing, answered, hangup notifications).

**TCP server** (`TcpServer`): Optional agent state subscription endpoint for third-party systems.

### Database

MyBatis mappers in `cc-core/src/main/java/com/voice9/core/mapper/` (no MyBatis-Plus). Supports MySQL, PostgreSQL, Oracle, SQL Server. SQL schema in `cc-api/src/main/resources/sql/`. Redis used for caching agent state, call state, and session data.

### Frontend State

- `stores/user.js` — authentication (login/logout, JWT token)
- `stores/softphone.js` — JsSIP SIP client instance, call state, audio management

### Default Credentials

Admin login: `admin` / `12345678`
