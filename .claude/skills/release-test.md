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

## 前置步骤

读取测试工程的 `project.config.json`，加载以下配置段：

- **fileClassification** — 变更分类规则（glob pattern → 类别 → 影响级别）
- **frontendApiMapping** — 前端 API 文件 → 受影响模块
- **controllerMapping** — 后端 Controller 关键字 → 前端模块/页面
- **moduleSpecs** — 模块名 → spec 文件路径 + 菜单父节点
- **globalImpactModules** / **smokeModules** — 全局影响时的回退列表

> project.config.json 是规则的单点来源。skill 不再內联规则表，避免不同步。

## 流程

### Step 1: 获取变更列表

```bash
cd ai-call-center
git diff <prev-tag>..<new-tag> --name-only
git diff <prev-tag>..<new-tag> --stat
git log <prev-tag>..<new-tag> --oneline
```

### Step 2: 变更分类

对每个变更文件，按 `project.config.json` → `fileClassification` 的顺序匹配 glob pattern，命中即停。

- `impact: direct` → 文件路径直接对应路由（如 views/<module>/Page.vue）
- `impact: module` → 文件所属模块的所有页面受影响
- `impact: mapped` → 按 controllerMapping 查表
- `impact: trace` → 向上追溯调用链到 Controller，再查 controllerMapping
- `impact: global` → 全量冒烟
- `impact: none` → 跳过

### Step 3: 影响面映射

根据分类结果映射到前端模块：

- **frontend-view**：文件路径 `views/<module>/<Page>.vue` → 模块 `module`、页面 `/<module>/<page>`
- **frontend-api**：查 `frontendApiMapping` 获取受影响模块
- **frontend-store / frontend-util**：搜索 import 引用该文件的页面，反推模块
- **backend-controller**：文件名匹配 `controllerMapping` 中的 keyword（子串匹配），获取 `modules` 和 `pages`
- **backend-service**：搜索调用该 Service 的 Controller，再查表
- **backend-mapper**：搜索调用该 Mapper 的 Service → Controller，再查表
- **backend-common**：所有模块 + `globalImpactModules`

合并去重所有受影响的模块名。

### Step 4: 生成 diff-mapping.json

将分析结果写入测试工程的 `e2e/releases/<prev>--<new>/diff-mapping.json`：

```json
{
  "prevTag": "v1.0.0",
  "newTag": "v1.1.0",
  "changedFiles": [
    { "file": "voxai-admin/.../controller/UserController.java", "category": "backend-controller" }
  ],
  "affectedModules": ["admin", "service"],
  "affectedPages": ["/admin/users", "/service/agent-list"],
  "riskLevel": "medium"
}
```

> 注意：`specsToRun` 由 `run-changed.js` 根据 moduleSpecs 自动推导，无需手工写入。

### Step 5: 执行测试

```bash
cd ai-call-center-test

# 方式 1: run-changed.js（推荐）
node scripts/run-changed.js e2e/releases/<prev>--<new>/diff-mapping.json

# 方式 2: 手动指定模块
node scripts/run-changed.js --modules admin,service

# 方式 3: 全量
npm test
```

`run-changed.js` 会从 diff-mapping.json 读取 `affectedModules`，再查 `project.config.json` 的 `moduleSpecs` 找到对应 spec 文件并执行。

### Step 6: 检查覆盖率（仅 --explore）

如果指定了 `--explore`，对 `affectedModules` 中不在 `moduleSpecs` 的模块：

1. 启动 Playwright MCP browser
2. 导航到目标页面
3. 执行基本流程（列表加载、搜索、弹窗开闭）
4. 记录关键选择器
5. 按以下模板生成 `e2e/scripts/<module>/baseline.spec.js`：

```js
const { test, expect } = require('../../fixtures/auth');
const { goToMenu, waitForLoadingComplete } = require('../../helpers/navigation');

const MODULES = [
  { label: '<页面名>' },
];

for (const mod of MODULES) {
  test.describe(mod.label, () => {
    test('page renders with table', async ({ page }) => {
      await page.goto('/');
      await goToMenu(page, '<父菜单>', mod.label);
      await waitForLoadingComplete(page);
      await expect(page.locator('.el-table, .el-empty, .el-table__empty-block').first()).toBeVisible({ timeout: 8000 });
    });
  });
}
```

6. 追加新模块到 `project.config.json` → `moduleSpecs`

### Step 7: 归档结果

将测试报告复制到 `ai-call-center-test/e2e/releases/<prev>--<new>/`：

```
e2e/releases/v1.0.0-v1.1.0/
├── impact-report.md      # Claude 影响面分析报告
├── diff-mapping.json     # 变更映射（机器可读）
├── test-results.json     # Playwright JSON 报告（从根目录复制）
└── test-output.txt       # 控制台输出
```

### Step 8: Git 提交

```bash
cd ai-call-center-test
git add e2e/scripts/ e2e/releases/ project.config.json
git commit -m "test(e2e): release <prev> → <new> 增量测试"
```

## 优先级矩阵（38 页面的分层策略）

| 优先级 | 模块 | 页面数 | 条件 |
|--------|------|--------|------|
| **P0** | Login, Dashboard, SoftPhone | 3 | 始终执行 |
| **P1** | Admin (7), Service (7) | 14 | 有变更时执行 |
| **P2** | Incoming (5), Outbound (4), Call (5), Report (7) | 21 | 有变更时执行 |

## 注意事项

- **首次使用**：先执行 `cd ai-call-center-test && npm run login` 生成登录态
- **无 tag 时**：提示用户先打 tag（`git tag v1.0.0`）
- **脚本去重**：同一页面多个 release 都有变更时，追加 test case 而非覆盖
- **规则变更**：修改影响面映射逻辑时，改 `project.config.json` 而非 skill 文件或 DESIGN.md
- **软电话模块**：涉及 WebSocket/JsSIP 实时通信，建议优先用 MCP 探索式测试
