<template>
  <div class="demo-page">
    <div class="demo-header">
      <h1>🎉 AI呼叫中心管理系统演示</h1>
      <p>基于 Vue 3 + Element Plus + Spring Boot 构建的现代化呼叫中心解决方案</p>
    </div>

    <div class="demo-content">
      <!-- 功能特性 -->
      <div class="feature-section">
        <h2>✨ 功能特性</h2>
        <div class="feature-grid">
          <div class="feature-card">
            <div class="feature-icon">🏠</div>
            <h3>仪表板</h3>
            <p>实时统计概览、通话趋势图表、坐席状态分布</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon">👥</div>
            <h3>系统管理</h3>
            <p>用户管理、角色管理、菜单管理、企业管理</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon">🎧</div>
            <h3>坐席管理</h3>
            <p>坐席列表、技能组管理、状态控制</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon">📞</div>
            <h3>呼叫管理</h3>
            <p>通话记录、呼叫统计、录音播放</p>
          </div>
        </div>
      </div>

      <!-- 技术栈 -->
      <div class="tech-section">
        <h2>🛠 技术栈</h2>
        <div class="tech-grid">
          <div class="tech-category">
            <h3>前台技术</h3>
            <div class="tech-tags">
              <span class="tech-tag">Vue 3.3.4</span>
              <span class="tech-tag">Element Plus</span>
              <span class="tech-tag">Vue Router</span>
              <span class="tech-tag">Pinia</span>
              <span class="tech-tag">Axios</span>
              <span class="tech-tag">ECharts</span>
              <span class="tech-tag">Vite</span>
            </div>
          </div>
          <div class="tech-category">
            <h3>后台技术</h3>
            <div class="tech-tags">
              <span class="tech-tag">Spring Boot</span>
              <span class="tech-tag">MySQL</span>
              <span class="tech-tag">Redis</span>
              <span class="tech-tag">JWT</span>
              <span class="tech-tag">Swagger</span>
              <span class="tech-tag">FreeSwitch</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 快速开始 -->
      <div class="quick-start-section">
        <h2>🚀 快速开始</h2>
        <div class="steps">
          <div class="step">
            <div class="step-number">1</div>
            <div class="step-content">
              <h3>启动后台服务</h3>
              <div class="code-block">
                <code>cd cc-api && mvn spring-boot:run</code>
                <code>cd fs-api && mvn spring-boot:run</code>
              </div>
            </div>
          </div>
          <div class="step">
            <div class="step-number">2</div>
            <div class="step-content">
              <h3>启动前台系统</h3>
              <div class="code-block">
                <code>cd frontend && npm install</code>
                <code>npm run dev</code>
              </div>
            </div>
          </div>
          <div class="step">
            <div class="step-number">3</div>
            <div class="step-content">
              <h3>访问系统</h3>
              <div class="code-block">
                <code>前台地址: http://localhost:3000</code>
                <code>默认账号: admin / 12345678</code>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 系统截图 -->
      <div class="screenshot-section">
        <h2>📱 系统截图</h2>
        <div class="screenshot-grid">
          <div class="screenshot-item">
            <div class="screenshot-placeholder">
              <el-icon size="48"><Monitor /></el-icon>
              <p>登录页面</p>
            </div>
          </div>
          <div class="screenshot-item">
            <div class="screenshot-placeholder">
              <el-icon size="48"><Odometer /></el-icon>
              <p>仪表板</p>
            </div>
          </div>
          <div class="screenshot-item">
            <div class="screenshot-placeholder">
              <el-icon size="48"><User /></el-icon>
              <p>用户管理</p>
            </div>
          </div>
          <div class="screenshot-item">
            <div class="screenshot-placeholder">
              <el-icon size="48"><Phone /></el-icon>
              <p>呼叫管理</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 开始使用 -->
      <div class="action-section">
        <h2>🎯 开始使用</h2>
        <p>系统已准备就绪，点击下方按钮开始体验完整功能</p>
        <div class="action-buttons">
          <el-button type="primary" size="large" @click="goToLogin">
            <el-icon><Login /></el-icon>
            立即登录
          </el-button>
          <el-button size="large" @click="goToDashboard">
            <el-icon><Odometer /></el-icon>
            查看仪表板
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const goToLogin = () => {
  router.push('/login')
}

const goToDashboard = () => {
  router.push('/')
}
</script>

<style scoped>
.demo-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40px 20px;
}

.demo-header {
  text-align: center;
  color: white;
  margin-bottom: 60px;
}

.demo-header h1 {
  font-size: 3rem;
  font-weight: 700;
  margin: 0 0 16px 0;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.demo-header p {
  font-size: 1.2rem;
  margin: 0;
  opacity: 0.9;
}

.demo-content {
  max-width: 1200px;
  margin: 0 auto;
}

.feature-section,
.tech-section,
.quick-start-section,
.screenshot-section,
.action-section {
  background: white;
  border-radius: 16px;
  padding: 40px;
  margin-bottom: 40px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.feature-section h2,
.tech-section h2,
.quick-start-section h2,
.screenshot-section h2,
.action-section h2 {
  font-size: 2rem;
  font-weight: 600;
  color: #303133;
  margin: 0 0 32px 0;
  text-align: center;
}

.feature-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 24px;
}

.feature-card {
  text-align: center;
  padding: 24px;
  border-radius: 12px;
  background: #f8f9fa;
  transition: transform 0.3s ease;
}

.feature-card:hover {
  transform: translateY(-4px);
}

.feature-icon {
  font-size: 3rem;
  margin-bottom: 16px;
}

.feature-card h3 {
  font-size: 1.25rem;
  font-weight: 600;
  color: #303133;
  margin: 0 0 12px 0;
}

.feature-card p {
  color: #606266;
  margin: 0;
  line-height: 1.6;
}

.tech-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 32px;
}

.tech-category h3 {
  font-size: 1.25rem;
  font-weight: 600;
  color: #303133;
  margin: 0 0 16px 0;
}

.tech-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tech-tag {
  background: #409EFF;
  color: white;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 0.875rem;
  font-weight: 500;
}

.steps {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.step {
  display: flex;
  align-items: flex-start;
  gap: 16px;
}

.step-number {
  width: 40px;
  height: 40px;
  background: #409EFF;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  flex-shrink: 0;
}

.step-content h3 {
  font-size: 1.125rem;
  font-weight: 600;
  color: #303133;
  margin: 0 0 8px 0;
}

.code-block {
  background: #f5f7fa;
  border-radius: 8px;
  padding: 12px 16px;
  margin-top: 8px;
}

.code-block code {
  display: block;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: 0.875rem;
  color: #303133;
  margin: 4px 0;
}

.screenshot-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 24px;
}

.screenshot-item {
  aspect-ratio: 4/3;
}

.screenshot-placeholder {
  width: 100%;
  height: 100%;
  background: #f5f7fa;
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #909399;
  border: 2px dashed #dcdfe6;
}

.screenshot-placeholder p {
  margin: 8px 0 0 0;
  font-size: 0.875rem;
}

.action-section {
  text-align: center;
}

.action-section p {
  font-size: 1.125rem;
  color: #606266;
  margin: 0 0 32px 0;
}

.action-buttons {
  display: flex;
  gap: 16px;
  justify-content: center;
}

/* 响应式 */
@media (max-width: 768px) {
  .demo-header h1 {
    font-size: 2rem;
  }
  
  .demo-header p {
    font-size: 1rem;
  }
  
  .feature-section,
  .tech-section,
  .quick-start-section,
  .screenshot-section,
  .action-section {
    padding: 24px;
  }
  
  .tech-grid {
    grid-template-columns: 1fr;
  }
  
  .action-buttons {
    flex-direction: column;
    align-items: center;
  }
}
</style>


