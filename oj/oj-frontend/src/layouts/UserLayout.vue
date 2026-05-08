<template>
  <div class="user-layout">
    <el-container class="layout-container">
      <el-header class="header">
        <div class="header-content">
          <div class="logo" @click="goHome">
            <!-- <el-icon :size="28"><Collection /></el-icon> -->
            <span class="logo-text">OJ 在线判题系统</span>
          </div>
        </div>
      </el-header>
      <el-main class="main">
        <div class="main-content">
          <RouterView />
        </div>
      </el-main>
      <el-footer class="footer">
        <div class="footer-content">
          <span>© 2026 OJ 在线判题系统 - 回归基本功</span>
        </div>
      </el-footer>
    </el-container>
  </div>
</template>

<script setup lang="ts">
// import { Collection } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const goHome = () => {
  router.push('/')
}
</script>

<style scoped>
.user-layout {
  min-height: 100vh;
}

.layout-container {
  min-height: 100vh;
}

.header {
  padding: 0;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  position: sticky;
  top: 0;
  z-index: 100;
  background: #fff;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  height: 100%;
  display: flex;
  align-items: center;
  padding: 0 24px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  transition: opacity 0.2s;
}

.logo:hover {
  opacity: 0.8;
}

.logo-text {
  font-size: 20px;
  font-weight: 600;
  color: #409eff;
}

.main {
  padding: 0;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8f0 100%);
  display: flex;
  justify-content: center;
  align-items: flex-start;
}

.main-content {
  width: 100%;
  max-width: 1200px;
  padding: 40px 24px;
}

.footer {
  padding: 20px 0;
  background: #f5f7fa;
  border-top: 1px solid #e4e8f0;
}

.footer-content {
  text-align: center;
  color: #909399;
  font-size: 14px;
}
</style>
