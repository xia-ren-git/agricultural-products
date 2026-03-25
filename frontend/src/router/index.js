import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/views/Layout.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { title: '注册' }
  },
  {
    path: '/',
    component: Layout,
    redirect: '/shop',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '首页概览', requiresAdmin: true }
      },
      {
        path: 'user',
        name: 'User',
        component: () => import('@/views/User.vue'),
        meta: { title: '用户管理', requiresAdmin: true }
      },
      {
        path: 'category',
        name: 'Category',
        component: () => import('@/views/Category.vue'),
        meta: { title: '分类管理', requiresAdmin: true }
      },
      {
        path: 'product',
        name: 'Product',
        component: () => import('@/views/Product.vue'),
        meta: { title: '产品管理', requiresAdmin: true }
      },
      {
        path: 'order',
        name: 'Order',
        component: () => import('@/views/Order.vue'),
        meta: { title: '订单管理', requiresAdmin: true }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/Profile.vue'),
        meta: { title: '个人信息' }
      },
      {
        path: 'shop',
        name: 'Shop',
        component: () => import('@/views/Shop.vue'),
        meta: { title: '商品浏览' }
      },
      {
        path: 'cart',
        name: 'Cart',
        component: () => import('@/views/Cart.vue'),
        meta: { title: '购物车' }
      },
      {
        path: 'product/:id',
        name: 'ProductDetail',
        component: () => import('@/views/ProductDetail.vue'),
        meta: { title: '商品详情' }
      },
      {
        path: 'my-order',
        name: 'MyOrder',
        component: () => import('@/views/MyOrder.vue'),
        meta: { title: '我的订单' }
      },
      {
        path: 'checkout',
        name: 'Checkout',
        component: () => import('@/views/Checkout.vue'),
        meta: { title: '确认订单' }
      },
      {
        path: 'favorite',
        name: 'Favorite',
        component: () => import('@/views/Favorite.vue'),
        meta: { title: '我的收藏' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 农产品销售管理系统` : '农产品销售管理系统'
  const token = localStorage.getItem('token')
  const user = JSON.parse(localStorage.getItem('user') || 'null')
  
  if (!token && to.path !== '/login' && to.path !== '/register') {
    next('/login')
    return
  }
  
  if (to.meta.requiresAdmin && (!user || user.username !== 'admin')) {
    next('/shop')
    return
  }
  
  next()
})

export default router
