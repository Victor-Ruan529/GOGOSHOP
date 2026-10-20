import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)


const originalPush = VueRouter.prototype.push
VueRouter.prototype.push = function push (location) {
  return originalPush.call(this, location).catch(err => err)
}

const routes = [
  {
    path: '/',
    name: 'Manager',
    component: () => import('../views/Manager.vue'),
    children: [
      { path: '403', name: 'NoAuth', meta: { name: '无权限' }, component: () => import('../views/manager/403') },
      { path: 'home', name: 'Home', meta: { name: '系统首页' }, component: () => import('../views/manager/Home') },
      { path: 'admin', name: 'Admin', meta: { name: '管理员信息' }, component: () => import('../views/manager/Admin') },
      { path: 'business', name: 'Business', meta: { name: '商家信息' }, component: () => import('../views/manager/Business') },
      { path: 'adminPerson', name: 'AdminPerson', meta: { name: '管理员信息' }, component: () => import('../views/manager/AdminPerson') },
      { path: 'businessPerson', name: 'BusinessPerson', meta: { name: '商家信息' }, component: () => import('../views/manager/BusinessPerson') },
      { path: 'user', name: 'User', meta: { name: '用户信息' }, component: () => import('../views/manager/User') },
      { path: 'password', name: 'Password', meta: { name: '修改密码' }, component: () => import('../views/manager/Password') },
      { path: 'notice', name: 'Notice', meta: { name: '公告信息' }, component: () => import('../views/manager/Notice') },
      { path: 'type', name: 'Type', meta: { name: '分类信息' }, component: () => import('../views/manager/Type') },
      { path: 'goods', name: 'Goods', meta: { name: '商品信息' }, component: () => import('../views/manager/Goods') },
      { path: 'orders', name: 'Orders', meta: { name: '订单信息' }, component: () => import('../views/manager/Orders') },
      { path: 'carousel', name: 'Carousel', meta: { name: '轮播图' }, component: () => import('../views/manager/Carousel') },
    ]
  },
  {
    path: '/front',
    name: 'Front',
    component: () => import('../views/Front.vue'),
    children: [
      { path: 'home', name: 'FrontHome', meta: { name: '系统首页' }, component: () => import('../views/front/Home') },
      { path: 'person', name: 'FrontPerson', meta: { name: '个人信息' }, component: () => import('../views/front/Person') },
      { path: 'detail', name: 'FrontDetail', meta: { name: '商品详情' }, component: () => import('../views/front/Detail') },
      { path: 'type', name: 'FrontType', meta: { name: '分类商品' }, component: () => import('../views/front/Type') },
      { path: 'search', name: 'FrontSearch', meta: { name: '搜索页面' }, component: () => import('../views/front/Search') },
      { path: 'collect', name: 'FrontCollect', meta: { name: '我的收藏' }, component: () => import('../views/front/Collect') },
      { path: 'orders', name: 'FrontOrders', meta: { name: '我的订单' }, component: () => import('../views/front/Orders') },
    ]
  },
  { path: '/login', name: 'Login', meta: { name: '登录' }, component: () => import('../views/Login.vue') },
  { path: '/register', name: 'Register', meta: { name: '注册' }, component: () => import('../views/Register.vue') },
  { path: '*', name: 'NotFound', meta: { name: '无法访问' }, component: () => import('../views/404.vue') },
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})


// 路由守卫：未登录只能访问登录/注册页；普通用户只能访问前台，管理员和商家只能访问后台
// （真正的权限控制在后端，这里只是改善体验）
const PUBLIC_PATHS = ['/login', '/register']
router.beforeEach((to, from, next) => {
  let user = JSON.parse(localStorage.getItem("xm-user") || '{}');
  const home = user.role === 'USER' ? '/front/home' : '/home'
  if (PUBLIC_PATHS.includes(to.path)) {
    next()
  } else if (!user.token) {
    next('/login')
  } else if (to.path === '/') {
    next(home)
  } else if (to.path.startsWith('/front') !== (user.role === 'USER') && to.name !== 'NotFound') {
    next(home)
  } else {
    next()
  }
})

export default router
