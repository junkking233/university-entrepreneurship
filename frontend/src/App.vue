<template>
  <router-view v-slot="{ Component }">
    <transition name="page" mode="out-in">
      <component :is="Component" />
    </transition>
  </router-view>
</template>

<script setup>
</script>

<style>
@import './styles/global.css';

:root {
  color-scheme: light;

  /* Brand palette */
  --primary-color: #409eff;
  --primary-hover: #2f88e6;
  --primary-active: #1d6fd1;
  --primary-light: #79bbff;
  --primary-lighter: #d9ecff;
  --primary-bg: #ecf5ff;

  /* Semantic palette */
  --success-color: #10b981;
  --success-bg: #ecfdf5;
  --warning-color: #f59e0b;
  --warning-bg: #fffbeb;
  --danger-color: #f56c6c;
  --danger-bg: #fff1f0;
  --info-color: #64748b;
  --info-bg: #f1f5f9;

  /* Neutral palette */
  --bg-color: #f5f7fb;
  --bg-soft: #f8fafc;
  --bg-white: #ffffff;
  --text-primary: #1f2937;
  --text-regular: #344054;
  --text-secondary: #667085;
  --text-placeholder: #98a2b3;
  --border-color: #e5e7eb;
  --border-light: #eef2f7;
  --divider-color: #edf2f7;

  /* Elevation and shape */
  --radius-sm: 6px;
  --radius-md: 8px;
  --radius-lg: 12px;
  --radius-xl: 16px;
  --shadow-xs: 0 1px 2px rgba(16, 24, 40, 0.04);
  --shadow-sm: 0 2px 6px rgba(16, 24, 40, 0.06);
  --shadow-md: 0 8px 20px rgba(16, 24, 40, 0.08);
  --shadow-lg: 0 16px 40px rgba(16, 24, 40, 0.1);
  --shadow-card: 0 8px 24px rgba(16, 24, 40, 0.07);
  --shadow-card-hover: 0 14px 34px rgba(16, 24, 40, 0.11);

  /* Motion */
  --transition-fast: 160ms cubic-bezier(0.4, 0, 0.2, 1);
  --transition-base: 240ms cubic-bezier(0.4, 0, 0.2, 1);
  --transition-slow: 360ms cubic-bezier(0.16, 1, 0.3, 1);

  /* Element Plus theme tokens */
  --el-color-primary: var(--primary-color);
  --el-color-primary-light-3: var(--primary-light);
  --el-color-primary-light-5: #a0cfff;
  --el-color-primary-light-7: #c6e2ff;
  --el-color-primary-light-8: var(--primary-lighter);
  --el-color-primary-light-9: var(--primary-bg);
  --el-color-primary-dark-2: var(--primary-active);
  --el-color-success: var(--success-color);
  --el-color-warning: var(--warning-color);
  --el-color-danger: var(--danger-color);
  --el-color-error: var(--danger-color);
  --el-color-info: var(--info-color);
  --el-bg-color: var(--bg-white);
  --el-bg-color-page: var(--bg-color);
  --el-text-color-primary: var(--text-primary);
  --el-text-color-regular: var(--text-regular);
  --el-text-color-secondary: var(--text-secondary);
  --el-text-color-placeholder: var(--text-placeholder);
  --el-border-color: var(--border-color);
  --el-border-color-light: var(--border-light);
  --el-border-radius-base: var(--radius-md);
  --el-border-radius-small: var(--radius-sm);
  --el-box-shadow-light: var(--shadow-card);
  --el-transition-duration: 0.24s;
  --el-transition-duration-fast: 0.16s;
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC',
    'Hiragino Sans GB', 'Microsoft YaHei', Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: var(--text-primary);
  background:
    radial-gradient(circle at top left, rgba(64, 158, 255, 0.08), transparent 34rem),
    linear-gradient(180deg, #f8fbff 0%, var(--bg-color) 42%, #f3f6fb 100%);
}

#app {
  min-height: 100vh;
  animation: app-fade-in var(--transition-slow) ease-out both;
}

@keyframes app-fade-in {
  from {
    opacity: 0;
    transform: translateY(8px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 页面过渡动画 */
.page-enter-active,
.page-leave-active {
  transition: opacity 250ms cubic-bezier(0.4, 0, 0.2, 1),
              transform 250ms cubic-bezier(0.4, 0, 0.2, 1);
}

.page-enter-from {
  opacity: 0;
  transform: translateY(12px);
}

.page-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}

/* 全局滚动条 */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}
::-webkit-scrollbar-track {
  background: transparent;
}
::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 999px;
}
::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}
</style>
