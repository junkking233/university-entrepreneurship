import { createRouter, createWebHistory } from 'vue-router'
import { verifyToken } from '@/api/auth'

const routes = [
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/views/Layout.vue'),
    children: [
      {
        path: '',
        name: 'Home',
        component: () => import('@/views/Home.vue')
      },
      {
        path: 'projects',
        name: 'ProjectList',
        component: () => import('@/views/ProjectList.vue')
      },
      {
        path: 'projects/:id',
        name: 'ProjectDetail',
        component: () => import('@/views/ProjectDetail.vue')
      },
      {
        path: 'trainings',
        name: 'TrainingList',
        component: () => import('@/views/TrainingList.vue')
      },
      {
        path: 'roadshows',
        name: 'RoadshowList',
        component: () => import('@/views/RoadshowList.vue')
      },
      {
        path: 'login',
        name: 'Login',
        component: () => import('@/views/Login.vue')
      },
      {
        path: 'register',
        name: 'Register',
        component: () => import('@/views/Register.vue')
      },
      {
        path: 'student/dashboard',
        name: 'StudentDashboard',
        component: () => import('@/views/StudentDashboard.vue'),
        meta: { requiresAuth: true, role: 'student' }
      },
      {
        path: 'student/projects',
        name: 'MyProjects',
        component: () => import('@/views/MyProjects.vue'),
        meta: { requiresAuth: true, role: 'student' }
      },
      {
        path: 'student/projects/create',
        name: 'CreateProject',
        component: () => import('@/views/CreateProject.vue'),
        meta: { requiresAuth: true, role: 'student' }
      },
      {
        path: 'student/projects/edit/:id',
        name: 'EditProject',
        component: () => import('@/views/CreateProject.vue'),
        meta: { requiresAuth: true, role: 'student' }
      },
      {
        path: 'student/consultations',
        name: 'MyConsultations',
        component: () => import('@/views/MyConsultations.vue'),
        meta: { requiresAuth: true, role: 'student' }
      },
      {
        path: 'student/profile',
        name: 'StudentProfile',
        component: () => import('@/views/StudentProfile.vue'),
        meta: { requiresAuth: true, role: 'student' }
      },
      {
        path: 'mentor/dashboard',
        name: 'MentorDashboard',
        component: () => import('@/views/MentorDashboard.vue'),
        meta: { requiresAuth: true, role: 'mentor' }
      },
      {
        path: 'mentor/profile',
        name: 'MentorProfile',
        component: () => import('@/views/MentorProfile.vue'),
        meta: { requiresAuth: true, role: 'mentor' }
      },
      {
        path: 'mentor/consultations',
        name: 'MentorConsultations',
        component: () => import('@/views/MentorConsultations.vue'),
        meta: { requiresAuth: true, role: 'mentor' }
      },
      {
        path: 'mentor/trainings',
        name: 'MentorTrainings',
        component: () => import('@/views/MentorTrainings.vue'),
        meta: { requiresAuth: true, role: 'mentor' }
      },
      {
        path: 'investor/dashboard',
        name: 'InvestorDashboard',
        component: () => import('@/views/InvestorDashboard.vue'),
        meta: { requiresAuth: true, role: 'investor' }
      },
      {
        path: 'investor/profile',
        name: 'InvestorProfile',
        component: () => import('@/views/InvestorProfile.vue'),
        meta: { requiresAuth: true, role: 'investor' }
      },
      {
        path: 'investor/investments',
        name: 'MyInvestments',
        component: () => import('@/views/MyInvestments.vue'),
        meta: { requiresAuth: true, role: 'investor' }
      },
      {
        path: 'admin/dashboard',
        name: 'AdminDashboard',
        component: () => import('@/views/AdminDashboard.vue'),
        meta: { requiresAuth: true, role: 'admin' }
      },
      {
        path: 'admin/projects',
        name: 'ProjectAudit',
        component: () => import('@/views/ProjectAudit.vue'),
        meta: { requiresAuth: true, role: 'admin' }
      },
      {
        path: 'admin/users',
        name: 'UserManagement',
        component: () => import('@/views/UserManagement.vue'),
        meta: { requiresAuth: true, role: 'admin' }
      },
      {
        path: 'admin/feedbacks',
        name: 'FeedbackManagement',
        component: () => import('@/views/FeedbackManagement.vue'),
        meta: { requiresAuth: true, role: 'admin' }
      },
      {
        path: 'messages',
        name: 'Messages',
        component: () => import('@/views/Messages.vue'),
        meta: { requiresAuth: true }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

function clearAuth() {
  localStorage.removeItem('token')
  localStorage.removeItem('userInfo')
}

function getStoredUserInfo() {
  try {
    const raw = localStorage.getItem('userInfo')
    return raw ? JSON.parse(raw) : null
  } catch {
    clearAuth()
    return null
  }
}

// 路由守卫
router.beforeEach(async (to, from, next) => {
  const token = localStorage.getItem('token')
  const userInfo = getStoredUserInfo()

  if (to.meta.requiresAuth) {
    if (!token) {
      next({ name: 'Login', query: { redirect: to.fullPath } })
      return
    }

    try {
      await verifyToken()
    } catch {
      clearAuth()
      next({ name: 'Login', query: { redirect: to.fullPath } })
      return
    }

    if (to.meta.role && userInfo && userInfo.role !== to.meta.role) {
      // 角色不匹配，跳转到对应 dashboard
      const roleRoutes = {
        student: '/student/dashboard',
        mentor: '/mentor/dashboard',
        investor: '/investor/dashboard',
        admin: '/admin/dashboard'
      }
      next(roleRoutes[userInfo.role] || '/')
      return
    }
  }

  next()
})

export default router
