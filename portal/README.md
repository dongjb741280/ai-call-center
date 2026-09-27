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
将此目录作为新仓库 `voxhub-portal` 的根目录。推送 main 前后，在 Settings → Pages 将 Source 设置为 GitHub Actions。工作流位于 `.github/workflows/deploy.yml`，检查、构建并部署 `dist/`。PR 只构建验证，不发布。

仓库计划地址：https://github.com/dongjb741280/voxhub-portal


Pages 计划地址：https://dongjb741280.github.io/voxhub-portal/

也可直接选择 Deploy from a branch → main → /(root)，不需要构建。若采用这种方式，应停用 Actions 发布工作流，避免两种方式冲突。
自定义域名请在 Pages 中配置，并开启 HTTPS。

## 项目来源与真实性
内容来自本地 ai-call-center、ai-call-center-web、ai-call-center-test 与 IntelliCall-Pro 的 README、架构文档。业务能力不由静态门户实际提供，独立项目的组合需要完成接口集成。
IntelliCall-Pro 的半双工、整句 ASR/TTS 和尚未实机验证的高可用/合规模块在指南中明确说明。没有使用未经验证的性能数字、客户 Logo 或商业承诺。
ai-call-center-test 没有已配置的 GitHub 远程，因此门户指向本地编写的介绍章节，不杜撰仓库地址。

## 修改
`index.html`：首页内容；`guide.html`：指南；`assets/style.css`：视觉与响应式样式；`assets/main.js`：场景和演示数据。
`scripts/serve.mjs`：本地静态服务器；`scripts/build.mjs`：仅复制发布所需文件；`tests/site.test.mjs`：内部链接与资源校验。
