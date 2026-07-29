# JMeter 集成测试

将 `.jmx` 测试计划文件放入此目录，CI pipeline 的 `jmeter-test` 阶段将自动执行。

## 测试计划要求

- 使用 JMeter 5.x 创建
- 非 GUI 模式执行 (`jmeter -n`)
- 被测服务需要在 pipeline 前已部署运行

## 变量配置

测试计划中可使用 JMeter 属性实现参数化：

```
${__P(target.host, localhost)}
${__P(target.port, 8080)}
${__P(target.protocol, http)}
```

## 目录结构

```
tests/jmeter/
├── README.md
├── voxai-admin-api.jmx     # 管理接口测试计划
└── voxai-call-api.jmx      # 呼叫接口测试计划
```
