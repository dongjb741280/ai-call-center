# 声枢 VoxHub
智能客户联络平台中文静态门户。Vox 代表声音，Hub 代表枢纽，以「连接每一种声音，成就每一次服务」为产品表达。

## 本地运行
主项目的一个目录

## 内容
- 首页：产品能力（含预留能力）、场景切换与流程演示、行业应用、架构、四个项目介绍、FAQ。
- 项目指南：项目分工、实施状态（含预留功能规划）、业务接入、自动化测试与门户发布。
- 移动导航、键盘可操作场景页签、原生 dialog 焦点管理、减少动画设置。
- 原生 HTML/CSS/JS，无 CDN、追踪、表单收集或真实通话请求。演示均为预设内容。
- 所有正常页面资源采用相对路径，适配 GitHub Pages 仓库子目录。

## 发布
本目录通过主仓库根目录的 `.github/workflows/portal-ci.yml` 部署到 GitHub Pages：
1. 在仓库 Settings → Pages → Build and deployment 中，将 Source 设为 `GitHub Actions`。
2. 推送 `main` 或改动 `portal/**` 时，工作流执行检查、构建并部署 `portal/dist`。PR 只构建验证，不发布。
3. 站点地址：https://dongjb741280.github.io/ai-call-center/

## 项目来源与真实性
内容来自本地 ai-call-center、ai-call-center-web、ai-call-center-test 与 IntelliCall-Pro 的 README、架构文档。业务能力不由静态门户实际提供，独立项目的组合需要完成接口集成。
IntelliCall-Pro 的半双工、整句 ASR/TTS 和尚未实机验证的高可用/合规模块在指南中明确说明。没有使用未经验证的性能数字、客户 Logo 或商业承诺。
ai-call-center-test 没有已配置的 GitHub 远程，因此门户指向本地编写的介绍章节，不杜撰仓库地址。

## 修改
`index.html`：首页内容；`guide.html`：指南；`assets/style.css`：视觉与响应式样式；`assets/main.js`：场景和演示数据。
`scripts/serve.mjs`：本地静态服务器；`scripts/build.mjs`：仅复制发布所需文件；`tests/site.test.mjs`：内部链接与资源校验。
