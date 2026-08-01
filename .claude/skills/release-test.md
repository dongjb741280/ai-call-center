---
name: release-test
description: 基于 Git Tag Diff 分析增量变更，识别受影响的前端功能，通过 Playwright 执行增量 E2E 测试并归档脚本。
argument-hint: <prev-tag> <new-tag> [--explore]
---

# /release-test

基于 Git Tag Diff 的增量 E2E 测试流程。分析两个 release tag 之间的代码变更，识别受影响的前端功能模块，执行（或生成）Playwright 测试脚本并归档结果。

## 用法

```
/release-test <prev-tag> <new-tag> [--explore]
```

- `prev-tag`: 上一个发布 tag（如 `v1.0.0`）
- `new-tag`: 当前发布 tag（如 `v1.1.0`）
- `--explore`: 对无脚本覆盖的功能，使用 Playwright MCP 探索并自动生成脚本

## 流程

### Step 1: 获取变更列表

```bash
git diff <prev-tag>..<new-tag> --name-only
git diff <prev-tag>..<new-tag> --stat
git log <prev-tag>..<new-tag> --oneline
```

### Step 2: 变更分类

将变更文件按以下规则分类：

| 分类 | 匹配规则 | 影响范围 |
|------|----------|----------|
| **frontend-view** | `ai-call-center-web/src/views/**/*.vue` | 直接对应路由页面 |
| **frontend-api** | `ai-call-center-web/src/api/**/*.js` | 对应模块所有页面 |
| **frontend-store** | `ai-call-center-web/src/stores/**/*.js` | 全局影响，所有需认证页面 |
| **frontend-util** | `ai-call-center-web/src/utils/**/*.js` | 引用该 util 的页面 |
| **backend-controller** | `voxai-admin/.../controller/**/*.java` | 对应前端 API 模块的前端页面 |
| **backend-service** | `*/.../service/**/*.java` | 追溯 Controller → 前端页面 |
| **backend-mapper** | `voxai-common/.../mapper/**/*.xml` | 追溯 → Service → Controller → 前端页面 |
| **backend-common** | `voxai-common/**/*.java` | 全量后端，保守评估为所有页面 |
| **config** | `*.yml, *.xml, *.properties, Dockerfile` | 基础设施，不影响前端功能 |
| **docs** | `*.md` | 无需测试 |

### Step 3: 影响面映射

根据变更分类和文件名，映射到前端功能模块（7 大模块）：

```
变更文件 → 前端路由 → 功能模块 → e2e/scripts/ 对应脚本
```

**映射规则详表：**

##### 后端 Controller → 前端 API 模块 → 前端路由

| Controller 文件关键字 | 前端 API | 前端路由模块 | 受影响页面 |
|----------------------|----------|-------------|-----------|
| `UserController` | `admin.js` (getUserList 等) | `admin` | /admin/users |
| `RoleController` | `admin.js` (getRoleList 等) | `admin` | /admin/roles |
| `GatewayController` | `admin.js` | `admin` | /admin/gateways |
| `NumberRouteController` | `admin.js` | `admin` | /admin/number-route |
| `BlacklistController` | `admin.js` | `admin` | /admin/blacklist |
| `ModulesController` | `admin.js` | `admin` | /admin/modules |
| `SipGatewayController` | `admin.js` | `admin` | /admin/sip-gateway |
| `AiEngineController` | `admin.js` | `admin` | /admin/ai-engine |
| `AgentController` | `agent.js` | `service` | /service/agent-list, /service/agent-groups |
| `GroupController` | `agent.js` | `service` | /service/agent-groups |
| `SkillController` | `config.js` | `service` | /service/skills |
| `SipNumberController` | `config.js` | `service` | /service/sip-numbers |
| `DisplayNumberController` | `config.js` | `service` | /service/display-numbers |
| `VoiceFileController` | `config.js` | `service` | /service/voice-file |
| `NumberPoolController` | `config.js` | `service` | /service/number-pool |
| `Incoming*Controller` | `call.js` | `incoming` | /incoming/* 全部 |
| `Outbound*Controller` | `call.js` | `outbound` | /outbound/* 全部 |
| `TaskController` | `call.js` | `outbound` | /outbound/task, /outbound/monitor |
| `Call*Controller` | `call.js` | `call` | /call/* 全部 |
| `Report*Controller` | `call.js` | `report` | /report/* 全部 |
| `Login/Auth/Token*` | `auth.js` | 全局 | Login |
| `SoftPhone/CallSdk*` | `softphone.js` | 全局 | SoftPhone |
| `Common/Base*` | 所有 | 全局 | 所有页面 |

##### 前端文件 → 路由

| 前端文件路径 | 对应路由 | 模块 |
|-------------|---------|------|
| `src/views/Dashboard.vue` | `/` | dashboard |
| `src/views/Login.vue` | `/login` | login |
| `src/views/SoftPhone.vue` | `/softphone` | softphone |
| `src/views/admin/*.vue` | `/admin/*` | admin |
| `src/views/agent/*.vue` | `/service/agent-*` | service |
| `src/views/service/*.vue` | `/service/*` | service |
| `src/views/incoming/*.vue` | `/incoming/*` | incoming |
| `src/views/outbound/*.vue` | `/outbound/*` | outbound |
| `src/views/call/*.vue` | `/call/*` | call |
| `src/views/report/*.vue` | `/report/*` | report |
| `src/api/admin.js` | `/admin/*` 全部 | admin |
| `src/api/agent.js` | `/service/agent-*` | service |
| `src/api/config.js` | `/service/*`, `/admin/*` | service, admin |
| `src/api/call.js` | `/incoming/*`, `/outbound/*`, `/call/*`, `/report/*` | incoming, outbound, call, report |
| `src/api/auth.js` | `/login` | login |
| `src/api/softphone.js` | `/softphone` | softphone |
| `src/stores/*` | 所有需登录页面 | 全部 |
| `src/utils/*` | 按引用路径判断 | 按实际引用 |

### Step 4: 生成 diff-mapping.json

将分析结果写入测试工程的 `e2e/releases/<prev>--<new>/diff-mapping.json`：

```json
{
  "prevTag": "v1.0.0",
  "newTag": "v1.1.0",
  "changedFiles": [
    { "file": "...", "category": "frontend-view" }
  ],
  "affectedModules": ["admin", "service"],
  "affectedPages": ["/admin/users", "/service/agent-list"],
  "riskLevel": "medium",
  "specsToRun": ["admin/baseline.spec.js", "service/baseline.spec.js"]
}
```

### Step 5: 检查已有脚本覆盖

对每个受影响的模块，检查 `ai-call-center-test/e2e/scripts/` 下是否存在对应脚本：

```
e2e/scripts/
├── admin/baseline.spec.js      ← 已覆盖
├── service/baseline.spec.js    ← 已覆盖
├── incoming/baseline.spec.js   ← 不存在，需要新建
```

- **已有脚本**：检查是否需要追加新 test case（如新增按钮/新增流程）
- **无脚本**：进入 Step 6 生成脚本

### Step 6: 生成/更新测试脚本

**方法 A（--explore 模式）：Playwright MCP 探索**
1. 启动 Playwright MCP browser
2. 导航到目标页面
3. 执行页面基本流程（列表加载、搜索、新增/编辑弹窗）
4. 记录关键选择器和操作流
5. 生成 `.spec.js` 并写入 `e2e/scripts/<module>/baseline.spec.js`

**方法 B（手动编写）：** 基于已知的页面结构直接编写脚本。

**脚本模板：**
```js
const { test, expect } = require('../fixtures/auth');
const { goToMenu, waitForTable } = require('../helpers/navigation');

test.describe('<模块名>', () => {
  test('<页面名> 页面加载正常', async ({ page }) => {
    await page.goto('/');
    await page.waitForTimeout(800);
    await goToMenu(page, '<一级菜单>', '<二级菜单>');
    await page.waitForTimeout(500);
    await expect(page.locator('.el-table, .el-empty').first()).toBeVisible({ timeout: 8000 });
  });
});
```

### Step 7: 执行测试

```bash
cd ai-call-center-test

# 执行受影响的 spec
npx playwright test e2e/scripts/admin/baseline.spec.js e2e/scripts/service/baseline.spec.js

# 或通过 run-changed.js
node scripts/run-changed.js --modules admin,service
```

### Step 8: 归档结果

将测试结果写入 `ai-call-center-test/e2e/releases/<prev>--<new>/`：

```
e2e/releases/v1.0.0-v1.1.0/
├── impact-report.md      # Claude 影响面分析报告
├── diff-mapping.json     # 变更映射（机器可读）
├── test-results.json     # Playwright JSON 报告
└── test-output.txt       # 控制台输出
```

### Step 9: Git 提交

```bash
cd ai-call-center-test
git add e2e/scripts/ e2e/releases/
git commit -m "test(e2e): release <prev> → <new> 增量测试"
```

## 优先级矩阵（40+ 页面的分层策略）

| 优先级 | 模块 | 页面数 | 条件 |
|--------|------|--------|------|
| **P0** | Login, Dashboard, SoftPhone | 3 | 始终执行 |
| **P1** | Admin (9), Service (7) | 16 | 有变更时执行 |
| **P2** | Incoming (5), Outbound (4), Call (5), Report (7) | 21 | 有变更时执行 |

## 注意事项

- **首次使用**：先执行 `cd ai-call-center-test && npm run login` 生成登录态
- **无 tag 时**：提示用户先打 tag（`git tag v1.0.0`）
- **脚本去重**：同一页面多个 release 都有变更时，追加 test case 而非覆盖
- **软电话模块**：涉及 WebSocket/JsSIP 实时通信，建议优先用 MCP 探索式测试
