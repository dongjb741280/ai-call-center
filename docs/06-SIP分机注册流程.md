# SIP 分机注册流程

本文说明坐席分机从软电话发起 REGISTER，到 FreeSWITCH 通过 mod_xml_curl 查询后端、最终注册成功的完整链路。涉及的仓库/目录：

- 前端：`ai-call-center-web`（软电话 `src/views/SoftPhone.vue`）
- 后端：`ai-call-center` 的 `voxai-admin`（目录查询接口）、`voxai-common`（`AgentSip` / mapper）
- FreeSWITCH：本机 `/Users/dongjb/soft/freeswitch/etc/freeswitch`（生产为 Docker 镜像）

## 参与方

```text
软电话(Linphone/JsSIP)          FreeSWITCH(sofia internal)          voxai-admin             MySQL(cc_agent_sip)
     │  REGISTER                      │                                │                       │
     │ ────────────────────────────▶  │  directory lookup               │                       │
     │                                │ ────────────────────────────▶  │  selectBySip          │
     │                                │                                │ ───────────────────▶  │
     │                                │ ◀────────────────────────────  │ ◀───────────────────  │
     │ ◀────────────────────────────  │  (401 challenge + realm)        │                       │
     │  REGISTER(带摘要)              │                                │                       │
     │ ────────────────────────────▶  │  校验摘要 → 200 OK              │                       │
```

## 逐步流程

### 1. 软电话发起首次 REGISTER（不带认证）

- UDP 走 5060（`sip-port`），Web 端 JsSIP 走 `ws://<fs>:5066`（`ws-binding`）。
- 报文里 `From: <sip:870001@192.168.1.4>`，Request-URI `sip:192.168.1.4:5060`。

### 2. sofia 收到 REGISTER，先查目录（directory lookup）

sofia 需要拿到用户密码才能校验，于是触发 XML 目录查询。mod_xml_curl 向
`http://127.0.0.1:7100/voxai-admin/freeswitch/directory` **POST** 一包参数（即日志里的
`action=sip_auth&section=directory&user=870001&domain=192.168.1.4&sip_auth_username=...`）。

后端 `FreeSwitchDirectoryController` 用 `user` 调 `selectBySip`（只查 `status=1`），返回：

```xml
<document type="freeswitch/xml"><section name="directory">
  <domain name="192.168.1.4"><groups><group name="default"><users>
    <user id="870001">
      <params><param name="password" value="123456"/></params>
      <variables>
        <variable name="user_context" value="default"/>
        <variable name="accountcode" value="1"/>
      </variables>
    </user>
  </users></group></groups></domain>
</section></document>
```

查不到就返回空 directory，sofia 随后报 `Can't find user`。

### 3. FreeSWITCH 回 401 挑战

拿到明文密码后，sofia 发 `401 Unauthorized`：

```text
WWW-Authenticate: Digest realm="192.168.1.4", nonce="...", algorithm=MD5, qop="auth"
```

`realm` 由 `challenge-realm=auto_from` 决定，取 **From 头里的域**（`192.168.1.4`）。

### 4. 软电话算摘要，重发 REGISTER

```text
HA1     = MD5(username : realm : password)   // MD5("870001:192.168.1.4:123456")
HA2     = MD5(method   : uri)                 // MD5("REGISTER:sip:192.168.1.4:5060")
response= MD5(HA1 : nonce : nc : cnonce : qop : HA2)
```

放进 `Authorization: Digest ...` 再次 REGISTER。

### 5. FreeSWITCH 校验 → 注册成功

FS 用目录查来的密码，按**同样的 realm** 重算摘要，与客户端 response 比对。一致则回
`200 OK`，注册写进 sofia registry，可用 `sofia status profile internal reg` 查看。

## 为什么返回明文 password，而不是 a1-hash

- `a1-hash` 是预计算的 HA1 = `MD5(user:realm:password)`，等于把 realm **写死**。
- 本环境的 `challenge-realm=auto_from` 使 realm 取决于客户端 From 头（可能是 IP、可能是主机名），
  一旦与写死的 realm 不一致，摘要校验就会失败。
- 返回**明文 password**，FS 拿到密码后用"实际挑战用的 realm"现算 HA1，客户端也用同一 realm 算，天然一致。
- 密码本就明文存储在 `cc_agent_sip.sip_pwd`，内网经 xml_curl 返回可接受。

## 关键配置取值

| 项 | 值 | 出处 |
|---|---|---|
| 注册域 domain | `$${local_ip_v4}`（本机局域网 IP） | `vars.xml` |
| UDP 注册端口 | 5060（`internal_sip_port`） | `vars.xml` |
| WebSocket 端口 | 5066（`ws-binding`） | `sip_profiles/internal.xml` |
| 目录查询绑定 | `http://127.0.0.1:7100/voxai-admin/freeswitch/directory` | `autoload_configs/xml_curl.conf.xml` |
| 启用模块 | `mod_xml_curl` | `autoload_configs/modules.conf.xml` |
| realm 策略 | `auto_from`（From 头域） | `sip_profiles/internal.xml` |
| 强制注册域 | `force-register-domain=$${domain}` | `sip_profiles/internal.xml` |
| 用户名强制匹配 | `inbound-reg-force-matching-username=true` | `sip_profiles/internal.xml` |
| 密码来源 | `cc_agent_sip.sip_pwd`（明文，`status=1`） | `AgentSipMapper.xml` 的 `selectBySip` |

## 后端接口约定

`voxai-admin` 新增 `FreeSwitchDirectoryController`，路径
`/voxai-admin/freeswitch/directory`，同时接受 GET/POST（mod_xml_curl 默认 POST）。

实现要点：

- 返回体**不能带 `<?xml?>` 声明**。FreeSWITCH 预处理器 `preprocess()` 逐行丢弃含 `<?` 的行
  （`switch_xml.c`），单行响应会整篇被丢弃，导致 `Error Parsing Result!`。直接以
  `<document type="freeswitch/xml">` 开头。
- 查不到（无此分机号 / `status=0`）返回空 `<section name="directory"/>`，等价于删号即下线。
- 未继承 `BaseController`，也不在 `/call|/config|/admin/**` 拦截器范围内，xml_curl 无需 token。

## 排障对照

| 现象 | 原因 | 看哪 |
|---|---|---|
| `Can't find user` | 目录查不到该分机 | 后端接口是否返回 `<user>`；库里 `status` 是否为 1 |
| `Error Parsing Result!` | 响应不是合法 XML（常因 `<?xml?>` 声明被丢弃） | 去掉声明，curl 看返回体 |
| `Invalid password` / 摘要对不上 | realm 或密码不一致 | `challenge-realm`、客户端 From 域 vs `$${domain}` |
| 模块加载报 `No such module` | mod_xml_curl 未编译 | `ls <fs>/lib/freeswitch/mod/mod_xml_curl.so` |

验证命令：

```bash
# 目录接口（模拟 mod_xml_curl 的 POST）
curl -s -X POST 'http://127.0.0.1:7100/voxai-admin/freeswitch/directory' \
  -d 'user=870001&domain=192.168.1.4'

# 注册结果
sofia status profile internal reg
```

## 与批量创建的关系

前端"批量创建分机"（`SipBatchDialog`）只是逐条 `POST /voxai-admin/config/sip` 往
`cc_agent_sip` 写行；FreeSWITCH 侧不感知。分机能否注册，由注册时的目录查询按
`cc_agent_sip` 内容动态决定，二者通过上述链路衔接。
