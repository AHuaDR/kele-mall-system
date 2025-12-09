import { createRouter, createWebHashHistory } from 'vue-router'
// 保持原有的 import
import Home from '../views/Home.vue'
import Category from '../views/Category.vue'
import Cart from '../views/Cart.vue'
import User from '../views/User.vue'
import Login from '../views/Login.vue'
import ProductDetail from '../views/ProductDetail.vue'
import AddressList from '../views/AddressList.vue'
import AddressEdit from '../views/AddressEdit.vue'
import CouponList from '../views/CouponList.vue'
import OrderList from '../views/OrderList.vue'
import OrderDetail from '../views/OrderDetail.vue'
import SearchResult from '../views/SearchResult.vue'
import Settings from '../views/Settings.vue'
import MessageList from '../views/MessageList.vue'
import MessageDetail from '../views/MessageDetail.vue' // 👈 新增
import Checkout from '../views/Checkout.vue'
import Recommend from '../views/Recommend.vue'
import BrandList from '../views/BrandList.vue'
import ServiceChat from '../views/ServiceChat.vue'
import StaticPage from '../views/StaticPage.vue'
import Security from '../views/Security.vue'
import About from '../views/About.vue'
import MyReviews from '../views/MyReviews.vue'
import PaymentSettings from '../views/PaymentSettings.vue'
import ProfileEdit from '../views/ProfileEdit.vue'
import SecurityMod from '../views/SecurityMod.vue'
import MyFavorites from '../views/MyFavorites.vue'

const routes = [
  { path: '/', component: Home, meta: { title: '首页' } },
  { path: '/category', component: Category, meta: { title: '分类' } },
  { path: '/cart', component: Cart, meta: { title: '购物车' } },
  { path: '/user', component: User, meta: { title: '个人中心' } },
  
  // 修复：登录页隐藏 Tabbar
  { path: '/login', component: Login, meta: { title: '登录', hideTabbar: true } },
  
  { path: '/product/:id', component: ProductDetail, meta: { title: '商品详情', hideTabbar: true } },
  { path: '/address', component: AddressList, meta: { title: '地址列表', hideTabbar: true } },
  { path: '/address/edit', component: AddressEdit, meta: { title: '编辑地址', hideTabbar: true } },
  { path: '/coupon', component: CouponList, meta: { title: '优惠券', hideTabbar: true } },
  { path: '/order', component: OrderList, meta: { title: '订单列表', hideTabbar: true } },
  { path: '/order/detail/:id', component: OrderDetail, meta: { title: '订单详情', hideTabbar: true } },
  { path: '/search', component: SearchResult, meta: { title: '搜索', hideTabbar: true } },
  { path: '/settings', component: Settings, meta: { title: '设置', hideTabbar: true } },
  { path: '/message', component: MessageList, meta: { title: '消息中心', hideTabbar: true } },
  { path: '/message/detail', component: MessageDetail, meta: { title: '消息详情', hideTabbar: true } }, // 👈 新增
  { path: '/checkout', component: Checkout, meta: { title: '结算', hideTabbar: true } },
  { path: '/recommend', component: Recommend, meta: { title: '好物推荐', hideTabbar: true } },
  { path: '/brand', component: BrandList, meta: { title: '品牌专区', hideTabbar: true } },
  { path: '/service', component: ServiceChat, meta: { title: '在线客服', hideTabbar: true } },
  { path: '/static/:type', component: StaticPage, meta: { title: '详情', hideTabbar: true } }, // 替换了原来的 /static/payment
  { path: '/static/payment', component: PaymentSettings, meta: { title: '支付设置', hideTabbar: true } }, // 👈 单独指向新页面
  { path: '/security', component: Security, meta: { title: '账号安全', hideTabbar: true } },
  { path: '/about', component: About, meta: { title: '关于我们', hideTabbar: true } },
  { path: '/my-reviews', component: MyReviews, meta: { title: '我的评价', hideTabbar: true } },
  { path: '/profile/edit', component: ProfileEdit, meta: { title: '编辑资料', hideTabbar: true } },
  { path: '/security/mod/:type', component: SecurityMod, meta: { title: '安全设置' } },
  { path: '/favorites', component: MyFavorites, meta: { title: '我的收藏' } },
]

const router = createRouter({
  history: createWebHashHistory(),
  routes,
  scrollBehavior(_to, _from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    } else {
      return { top: 0 }
    }
  }
})


// 3. 定义白名单 (不需要登录就能访问的页面)
// 注意：动态路由如 /product/123 需要单独逻辑判断
const whiteList = [
  '/', 
  '/login', 
  '/category', 
  '/recommend', 
  '/brand', 
  '/service', 
  '/about'
]

// 4. 全局路由守卫
router.beforeEach((to, _from, next) => {
  // 读取 C端 专用 Token Key
  const token = localStorage.getItem('cola_app_token')

  // 判断目标页面是否在白名单中
  // 1. 在静态白名单数组里
  // 2. 或者是以 /product/ 开头的商品详情页
  // 3. 或者是以 /search 开头的搜索页
  // 4. 或者是以 /static/ 开头的静态页
  const isPublic = whiteList.includes(to.path) 
    || to.path.startsWith('/product/') 
    || to.path.startsWith('/search')
    || to.path.startsWith('/static/')

  // 情况 A: 已登录
  if (token) {
    if (to.path === '/login') {
      // 已登录还去登录页 -> 踢回首页
      next('/') 
    } else {
      // 其他页面放行
      next() 
    }
  } 
  // 情况 B: 未登录
  else {
    if (isPublic) {
      // 如果是公开页面 -> 放行
      next() 
    } else {
      // 否则 -> 强制跳转登录页，并带上 redirect 参数以便登录后跳回
      next(`/login?redirect=${encodeURIComponent(to.fullPath)}`) 
    }
  }
})

export default router