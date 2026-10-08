# 声枢 VoxHub · 暗色现代版（portal1）

与 `portal/` 内容相同、视觉全新的另一套门户。采用暗色主题 + 极光渐变 + Bento 网格 + 滚动动效，用于和原版对照比较。

## 本地运行

```bash
cd portal1
npm run dev          # http://localhost:4173
npm run check        # 语法检查 + 链接/资源校验
npm run build        # 构建到 dist/
npm run preview      # 预览 dist/
```

## 与原版（portal/）的差异

| 维度 | portal/（原版） | portal1/（本版） |
|------|----------------|------------------|
| 主题 | 浅色、薄荷绿极简 | 暗色、极光渐变 |
| 布局 | 规整网格 | Bento 网格 + 非对称 hero |
| 图标 | Unicode 字符（↗ ✓ ✧ 等） | 内联 SVG |
| 动效 | 少量 hover | 滚动渐入（stagger）+ 波形/光晕 |
| 字体 | 系统栈 | 系统栈（Inter 优先） |
| 内容 | 完全一致 | 完全一致 |

## 结构

- `index.html` 首页、`guide.html` 指南、`404.html`
- `assets/style.css` 视觉与响应式样式（暗色设计系统）
- `assets/main.js` 场景/演示逻辑 + 滚动动效
- `assets/solutions/*.svg` 5 张解决方案插画（暗色渐变风格）
- `scripts/` 本地服务器与构建脚本；`tests/` 链接与资源校验

## 说明

- 纯 HTML/CSS/JS，无 CDN、无追踪、无表单收集、无真实通话请求。
- 所有正常页面资源使用相对路径，适配 GitHub Pages 子目录。
- `prefers-reduced-motion` 下关闭滚动动效与光晕动画。
