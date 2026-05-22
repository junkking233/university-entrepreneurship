<template>
  <el-container class="layout-container">
    <!-- 顶部导航栏 -->
    <el-header class="layout-header">
      <div class="header-left">
        <h2 class="logo" @click="$router.push('/')">
          <el-icon><School /></el-icon>
          大学生创业平台
        </h2>
      </div>
      <el-menu
        :default-active="activeMenu"
        mode="horizontal"
        :ellipsis="false"
        class="header-menu"
        @select="handleMenuSelect"
      >
        <el-menu-item index="/">首页</el-menu-item>
        <el-menu-item index="/projects">创业项目</el-menu-item>
        <el-menu-item index="/trainings">培训活动</el-menu-item>
        <el-menu-item index="/roadshows">路演预告</el-menu-item>
        <!-- 登录后显示 -->
        <template v-if="isLoggedIn">
          <el-menu-item v-if="userRole === 'student'" index="/student/dashboard">工作台</el-menu-item>
          <el-menu-item v-if="userRole === 'mentor'" index="/mentor/dashboard">导师中心</el-menu-item>
          <el-menu-item v-if="userRole === 'investor'" index="/investor/dashboard">投资中心</el-menu-item>
          <el-menu-item v-if="userRole === 'admin'" index="/admin/dashboard">管理后台</el-menu-item>
          <el-menu-item index="/messages">
            消息
            <el-badge :value="unreadCount" :hidden="unreadCount === 0" class="badge-item" />
          </el-menu-item>
        </template>
      </el-menu>
      <div class="header-right">
        <template v-if="!isLoggedIn">
          <el-button type="primary" size="small" @click="$router.push('/login')">登录</el-button>
          <el-button size="small" @click="$router.push('/register')">注册</el-button>
        </template>
        <template v-else>
          <el-dropdown @command="handleUserCommand">
            <span class="user-dropdown">
              <el-avatar :size="32" :icon="UserFilled" />
              <span class="username">{{ userName }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="dashboard">工作台</el-dropdown-item>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
      </div>
    </el-header>

    <!-- 主内容区 -->
    <el-main class="layout-main">
      <router-view />
    </el-main>

    <!-- 底部 -->
    <el-footer class="layout-footer">
      <div class="footer-content">
        <p>&copy; 2024 大学生创业平台. All rights reserved.</p>
        <p>支持大学生创新创业，助力梦想起航</p>
      </div>
    </el-footer>
  </el-container>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { UserFilled } from '@element-plus/icons-vue'
import { getUnreadCount } from '@/api/message'

const router = useRouter()
const route = useRoute()

const isLoggedIn = ref(false)
const userRole = ref('')
const userName = ref('')
const unreadCount = ref(0)

const activeMenu = computed(() => {
  const path = route.path
  if (path.startsWith('/student')) return '/student/dashboard'
  if (path.startsWith('/mentor')) return '/mentor/dashboard'
  if (path.startsWith('/investor')) return '/investor/dashboard'
  if (path.startsWith('/admin')) return '/admin/dashboard'
  if (path === '/messages') return '/messages'
  if (path.startsWith('/projects')) return '/projects'
  if (path.startsWith('/trainings')) return '/trainings'
  if (path.startsWith('/roadshows')) return '/roadshows'
  return '/'
})

function loadUserInfo() {
  const token = localStorage.getItem('token')
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || 'null')
  if (token && userInfo) {
    isLoggedIn.value = true
    userRole.value = userInfo.role || ''
    userName.value = userInfo.nickname || userInfo.username || '用户'
    fetchUnreadCount()
  } else {
    isLoggedIn.value = false
    userRole.value = ''
    userName.value = ''
  }
}

async function fetchUnreadCount() {
  try {
    const res = await getUnreadCount()
    unreadCount.value = res.data || 0
  } catch {
    unreadCount.value = 0
  }
}

function handleMenuSelect(index) {
  router.push(index)
}

function handleUserCommand(command) {
  switch (command) {
    case 'dashboard':
      const roleRoutes = {
        student: '/student/dashboard',
        mentor: '/mentor/dashboard',
        investor: '/investor/dashboard',
        admin: '/admin/dashboard'
      }
      router.push(roleRoutes[userRole.value] || '/')
      break
    case 'profile':
      const profileRoutes = {
        student: '/student/dashboard',
        mentor: '/mentor/profile',
        investor: '/investor/profile'
      }
      router.push(profileRoutes[userRole.value] || '/')
      break
    case 'logout':
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      isLoggedIn.value = false
      userRole.value = ''
      userName.value = ''
      router.push('/')
      break
  }
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.layout-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.layout-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  position: sticky;
  top: 0;
  z-index: 1000;
  height: 60px;
}

.header-left .logo {
  cursor: pointer;
  color: #409eff;
  font-size: 20px;
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0;
  white-space: nowrap;
}

.header-menu {
  flex: 1;
  border-bottom: none !important;
  margin: 0 20px;
}

.header-menu .el-menu-item {
  height: 60px;
  line-height: 60px;
}

.badge-item {
  margin-left: 6px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 10px;
  white-space: nowrap;
}

.user-dropdown {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  color: #303133;
}

.username {
  font-size: 14px;
}

.layout-main {
  flex: 1;
  background-color: #f5f7fa;
  padding: 20px;
}

.layout-footer {
  background-color: #f5f7fa;
  border-top: 1px solid #e4e7ed;
  padding: 20px;
  text-align: center;
}

.footer-content p {
  margin: 4px 0;
  color: #909399;
  font-size: 13px;
}
</style>
