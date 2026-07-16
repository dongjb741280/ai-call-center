<template>
  <div class="softphone-page">
    <div class="softphone-container">
      <!-- 左侧：控制面板 -->
      <div class="panel-left">
        <!-- 登录参数 -->
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon><Key /></el-icon>
              <span>登录参数</span>
            </div>
          </template>
          <el-form :model="loginForm" label-width="72px" size="default">
            <el-form-item label="账号">
              <el-input v-model="loginForm.agentKey" placeholder="agent1001" />
            </el-form-item>
            <el-form-item label="密码">
              <el-input v-model="loginForm.passwd" type="password" show-password placeholder="请输入密码" />
            </el-form-item>
            <el-form-item label="接听方式">
              <el-select v-model="loginForm.loginType" style="width: 100%">
                <el-option label="SIP号登录" :value="1" />
                <el-option label="WebRTC" :value="2" />
                <el-option label="手机号" :value="3" />
              </el-select>
            </el-form-item>
            <el-form-item label="工作类型">
              <el-select v-model="loginForm.workType" style="width: 100%">
                <el-option label="普通" :value="1" />
                <el-option label="预测" :value="2" />
              </el-select>
            </el-form-item>
          </el-form>
          <div class="card-actions">
            <el-button type="primary" @click="handleConnect" :loading="connecting" :disabled="connected">
              <el-icon><Link /></el-icon> 连接
            </el-button>
            <el-button type="danger" @click="handleLogout" :disabled="!connected">
              <el-icon><SwitchButton /></el-icon> 登出
            </el-button>
          </div>
        </el-card>

        <!-- 坐席状态 -->
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon><UserFilled /></el-icon>
              <span>坐席状态</span>
              <el-tag v-if="connected" :type="agentState === 'READY' ? 'success' : 'warning'" size="small" style="margin-left: 8px">
                {{ agentState === 'READY' ? '空闲' : agentState === 'BUSY' ? '忙碌' : agentState || '未知' }}
              </el-tag>
            </div>
          </template>
          <div class="card-actions">
            <el-button type="warning" @click="handleBusy" :disabled="!connected">
              <el-icon><RemoveFilled /></el-icon> 忙碌
            </el-button>
            <el-button type="success" @click="handleReady" :disabled="!connected">
              <el-icon><CircleCheckFilled /></el-icon> 空闲
            </el-button>
          </div>
        </el-card>

        <!-- 通话控制 -->
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon><Phone /></el-icon>
              <span>通话控制</span>
              <el-tag v-if="callState" type="danger" size="small" style="margin-left: 8px">{{ callState }}</el-tag>
            </div>
          </template>
          <div class="call-section">
            <div class="call-row">
              <el-input v-model="phoneNum" placeholder="输入被叫号码" style="flex: 1" />
              <el-button type="primary" @click="handleMakeCall" :disabled="!connected">
                <el-icon><PhoneFilled /></el-icon> 外呼
              </el-button>
            </div>
            <div class="call-row">
              <el-button type="success" @click="handleAcceptCall" :disabled="!connected">
                <el-icon><PhoneFilled /></el-icon> 应答
              </el-button>
              <el-button type="danger" @click="handleHangup" :disabled="!connected">
                <el-icon><CloseBold /></el-icon> 挂机
              </el-button>
            </div>
            <el-divider />
            <div class="call-row">
              <el-input v-model="transferNum" placeholder="转接目标" style="flex: 1" />
              <el-button @click="handleTransfer" :disabled="!connected" type="warning">转接</el-button>
            </div>
            <div class="call-row">
              <el-input v-model="consultNum" placeholder="咨询目标" style="flex: 1" />
              <el-button @click="handleConsult" :disabled="!connected" type="warning">咨询</el-button>
              <el-button @click="handleConsultCancel" :disabled="!connected">取消咨询</el-button>
            </div>
            <div class="call-row">
              <el-input v-model="consultTransferNum" placeholder="咨询转接目标" style="flex: 1" />
              <el-button @click="handleConsultTransfer" :disabled="!connected" type="warning">咨询转接</el-button>
              <el-button @click="handleConsultParty" :disabled="!connected">转三方</el-button>
            </div>
          </div>
        </el-card>

        <!-- 通话状态控制 -->
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon><SetUp /></el-icon>
              <span>通话状态</span>
            </div>
          </template>
          <div class="card-actions">
            <el-button @click="handleMute" :disabled="!connected">
              <el-icon><MuteNotification /></el-icon> 静音
            </el-button>
            <el-button @click="handleCancelMute" :disabled="!connected">
              <el-icon><Notification /></el-icon> 取消静音
            </el-button>
            <el-button @click="handleHold" :disabled="!connected" type="warning">
              <el-icon><VideoPause /></el-icon> 保持
            </el-button>
            <el-button @click="handleCancelHold" :disabled="!connected" type="success">
              <el-icon><VideoPlay /></el-icon> 取消保持
            </el-button>
          </div>
        </el-card>
      </div>

      <!-- 右侧：消息日志 -->
      <div class="panel-right">
        <el-card shadow="hover" class="message-card">
          <template #header>
            <div class="card-header">
              <el-icon><ChatLineSquare /></el-icon>
              <span>消息日志</span>
              <el-button size="small" @click="clearMessages" style="margin-left: auto">清除</el-button>
            </div>
          </template>
          <div class="message-body" ref="messageBodyRef">
            <div v-if="messages.length === 0" class="message-empty">暂无消息</div>
            <div v-for="(msg, index) in messages" :key="index" class="message-item">
              <span class="message-time">{{ msg.time }}</span>
              <span :class="['message-type', msg.level]">{{ msg.type }}</span>
              <pre class="message-content">{{ msg.content }}</pre>
            </div>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { agentLogin } from '@/api/softphone'

const connecting = ref(false)
const connected = ref(false)
const agentState = ref('')
const callState = ref('')
const phoneNum = ref('13300001234')
const transferNum = ref('1002@test')
const consultNum = ref('1002@test')
const consultTransferNum = ref('1002@test')
const messages = ref([])
const messageBodyRef = ref(null)
const agentToken = ref('')

let voice9Instance = null

const loginForm = reactive({
  agentKey: 'agent1001',
  passwd: '12345678',
  loginType: 1,
  workType: 1
})

const addMessage = (type, content, level = 'info') => {
  const now = new Date()
  const time = now.toLocaleTimeString('zh-CN', { hour12: false })
  messages.value.push({ time, type, content, level })
  nextTick(() => {
    if (messageBodyRef.value) {
      messageBodyRef.value.scrollTop = messageBodyRef.value.scrollHeight
    }
  })
}

const clearMessages = () => {
  messages.value = []
}

// 动态加载 Voice9 SDK
const loadVoice9Sdk = () => {
  return new Promise((resolve, reject) => {
    if (window.Voice9) {
      resolve()
      return
    }
    const script = document.createElement('script')
    script.src = '/fs-api/voice9.sdk.js'
    script.onload = () => {
      if (window.Voice9) {
        resolve()
      } else {
        reject(new Error('Voice9 SDK 加载失败'))
      }
    }
    script.onerror = () => reject(new Error('Voice9 SDK 脚本加载失败'))
    document.head.appendChild(script)
  })
}

// 初始化 Voice9 事件监听
const initVoice9 = (loginData) => {
  voice9Instance = new window.Voice9()

  voice9Instance.addEventListener('message', (data) => {
    const msg = JSON.stringify(data)
    if (data.type === 'pong') return

    // 根据消息类型更新状态
    if (data.type === 'stateChange') {
      agentState.value = data.agentState || ''
      callState.value = data.callState || ''
    }

    addMessage('RECV', msg)
  })

  voice9Instance.addEventListener('logout', () => {
    addMessage('EVENT', 'logout', 'warning')
    connected.value = false
    agentState.value = ''
    callState.value = ''
    voice9Instance = null
  })

  voice9Instance.init(loginData)
}


// 连接（登录 + 初始化 SDK）
const handleConnect = async () => {
  if (!loginForm.agentKey || !loginForm.passwd) {
    ElMessage.warning('请输入账号和密码')
    return
  }
  connecting.value = true
  try {
    // 1. 加载 Voice9 SDK
    await loadVoice9Sdk()

    // 2. 调用后台登录接口
    const res = await agentLogin({
      agentKey: loginForm.agentKey,
      passwd: loginForm.passwd,
      loginType: loginForm.loginType,
      workType: loginForm.workType
    })

    if (res.code !== 0) {
      ElMessage.error(res.message || '登录失败')
      return
    }

    addMessage('LOGIN', JSON.stringify(res.data))
    ElMessage.success('登录成功')

    // 3. 保存 token 并初始化 WebRTC SDK
    agentToken.value = res.data.token
    initVoice9(res.data)
    connected.value = true
    agentState.value = 'LOGIN'
  } catch (e) {
    ElMessage.error('连接失败: ' + (e.message || '未知错误'))
  } finally {
    connecting.value = false
  }
}

// 登出
const handleLogout = () => {
  if (voice9Instance) {
    voice9Instance.logout()
  }
  connected.value = false
  agentState.value = ''
  callState.value = ''
  voice9Instance = null
  agentToken.value = ''
  addMessage('EVENT', '手动登出', 'warning')
}

// 忙碌
const handleBusy = () => {
  if (voice9Instance) {
    voice9Instance.setBusy()
    agentState.value = 'BUSY'
    addMessage('CMD', 'setBusy')
  }
}

// 空闲
const handleReady = () => {
  if (voice9Instance) {
    voice9Instance.setReady()
    agentState.value = 'READY'
    addMessage('CMD', 'setReady')
  }
}

// 外呼
const handleMakeCall = () => {
  if (!phoneNum.value) {
    ElMessage.warning('请输入被叫号码')
    return
  }
  if (voice9Instance) {
    voice9Instance.makeCall(phoneNum.value)
    callState.value = 'CALLING'
    addMessage('CMD', `makeCall: ${phoneNum.value}`)
  }
}

// 应答
const handleAcceptCall = () => {
  if (voice9Instance) {
    voice9Instance.acceptCall()
    callState.value = 'TALKING'
    addMessage('CMD', 'acceptCall')
  }
}

// 挂机
const handleHangup = () => {
  if (voice9Instance) {
    voice9Instance.closeCall()
    callState.value = ''
    addMessage('CMD', 'closeCall')
  }
}

// 转接
const handleTransfer = () => {
  if (!transferNum.value) {
    ElMessage.warning('请输入转接目标')
    return
  }
  if (voice9Instance) {
    voice9Instance.phoneTransfer(transferNum.value)
    addMessage('CMD', `phoneTransfer: ${transferNum.value}`)
  }
}

// 咨询
const handleConsult = () => {
  if (!consultNum.value) {
    ElMessage.warning('请输入咨询目标')
    return
  }
  if (voice9Instance) {
    voice9Instance.phoneConsult(consultNum.value)
    addMessage('CMD', `phoneConsult: ${consultNum.value}`)
  }
}

// 取消咨询
const handleConsultCancel = () => {
  if (voice9Instance) {
    voice9Instance.phoneConsultCancel()
    addMessage('CMD', 'phoneConsultCancel')
  }
}

// 咨询转接
const handleConsultTransfer = () => {
  if (!consultTransferNum.value) {
    ElMessage.warning('请输入咨询转接目标')
    return
  }
  if (voice9Instance) {
    voice9Instance.phoneConsultTransfer(consultTransferNum.value)
    addMessage('CMD', `phoneConsultTransfer: ${consultTransferNum.value}`)
  }
}

// 转三方
const handleConsultParty = () => {
  if (voice9Instance) {
    voice9Instance.phoneConsultParty()
    addMessage('CMD', 'phoneConsultParty')
  }
}

// 静音
const handleMute = () => {
  if (voice9Instance) {
    voice9Instance.mutePhone()
    addMessage('CMD', 'mutePhone')
  }
}

// 取消静音
const handleCancelMute = () => {
  if (voice9Instance) {
    voice9Instance.cancelMute()
    addMessage('CMD', 'cancelMute')
  }
}

// 保持
const handleHold = () => {
  if (voice9Instance) {
    voice9Instance.holdTalking()
    addMessage('CMD', 'holdTalking')
  }
}

// 取消保持
const handleCancelHold = () => {
  if (voice9Instance) {
    voice9Instance.cancelHold()
    addMessage('CMD', 'cancelHold')
  }
}

onMounted(async () => {
  // 预加载 SDK
  try {
    await loadVoice9Sdk()
    addMessage('INFO', 'Voice9 SDK 已就绪')
  } catch (e) {
    // SDK 加载失败，后续连接时会重试
  }
})

onBeforeUnmount(() => {
  if (voice9Instance) {
    voice9Instance.logout()
  }
})
</script>

<style scoped>
.softphone-page {
  height: 100%;
  box-sizing: border-box;
  overflow: hidden;
}

.softphone-container {
  display: grid;
  grid-template-columns: 380px 1fr;
  gap: 12px;
  height: 100%;
  max-width: 1500px;
  margin: 0 auto;
}

/* ---- 左侧控制面板 ---- */
.panel-left {
  min-width: 0;
  overflow-y: auto;
  padding-right: 2px;
}

.panel-left .el-card {
  border-radius: var(--radius-md);
  margin-bottom: 8px;
}

.panel-left .el-card:last-child {
  margin-bottom: 0;
}

.panel-left :deep(.el-card__body) {
  padding: 12px 14px;
}

.panel-left :deep(.el-card__header) {
  padding: 10px 14px;
}

/* ---- 右侧消息面板 ---- */
.panel-right {
  min-width: 280px;
  min-height: 0;
}

.message-card {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.message-card :deep(.el-card__body) {
  flex: 1;
  overflow: hidden;
  padding: 0;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

/* ---- 卡片组件 ---- */
.card-header {
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: 600;
  font-size: var(--font-size-sm);
}

.card-actions {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.card-actions .el-button {
  font-size: var(--font-size-xs);
}

/* ---- 呼叫控制 ---- */
.call-section {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.call-row {
  display: flex;
  gap: 6px;
  align-items: center;
}

.call-row .el-button {
  flex-shrink: 0;
  white-space: nowrap;
  font-size: var(--font-size-xs);
}

.panel-left :deep(.el-form-item) {
  margin-bottom: 10px;
}

.panel-left :deep(.el-form-item__label) {
  font-size: var(--font-size-xs);
}

.panel-left :deep(.el-divider) {
  margin: 6px 0;
}

.panel-left :deep(.el-select .el-input__inner) {
  font-size: var(--font-size-xs);
}

/* ---- 消息日志 ---- */
.message-body {
  flex: 1;
  overflow-y: auto;
  padding: 10px 12px;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: 12px;
  line-height: 1.5;
}

.message-empty {
  color: var(--text-secondary);
  text-align: center;
  padding-top: 40px;
  font-size: var(--font-size-sm);
}

.message-item {
  padding: 3px 0;
  border-bottom: 1px solid var(--border-color-light);
}

.message-time {
  color: var(--text-secondary);
  margin-right: 6px;
}

.message-type {
  display: inline-block;
  padding: 0 5px;
  border-radius: 3px;
  font-size: 10px;
  font-weight: 600;
  margin-right: 6px;
}

.message-type.info {
  background: var(--color-info-bg);
  color: var(--color-info);
}

.message-type.warning {
  background: var(--color-warning-bg);
  color: var(--color-warning);
}

.message-type.error {
  background: var(--color-danger-bg);
  color: var(--color-danger);
}

.message-content {
  margin: 2px 0 0 0;
  white-space: pre-wrap;
  word-break: break-all;
  color: var(--text-primary);
  font-size: 11px;
}

/* ---- 响应式 ---- */
@media (max-width: 1100px) {
  .softphone-container {
    grid-template-columns: 340px 1fr;
  }
}

@media (max-width: 900px) {
  .softphone-container {
    grid-template-columns: 1fr;
    grid-template-rows: auto 1fr;
    gap: 8px;
    overflow-y: auto;
  }

  .panel-left {
    overflow-y: visible;
  }

  .panel-right {
    min-width: 0;
    min-height: 320px;
  }
}

@media (max-width: 480px) {
  .panel-left {
    gap: 6px;
  }

  .call-row {
    flex-wrap: wrap;
  }

  .call-row .el-button {
    font-size: 11px;
    padding: 6px 10px;
  }

  .card-actions {
    gap: 4px;
  }

  .card-actions .el-button {
    padding: 6px 10px;
  }
}

</style>
