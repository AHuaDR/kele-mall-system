import { createRouter, createWebHistory } from 'vue-router'
import MainLayout from '../layout/MainLayout.vue'
import Login from '../views/login/Login.vue'
import Analysis from '../views/dashboard/Analysis.vue'

// PMS
import ProductList from '../views/pms/ProductList.vue'
import ProductDetail from '../views/pms/ProductDetail.vue'
import CategoryList from '../views/pms/CategoryList.vue'

// OMS
import OrderList from '../views/oms/OrderList.vue'
import ChatConsole from '../views/oms/ChatConsole.vue'

// MKT
import Marketing from '../views/mkt/Marketing.vue'

// CMS & UMS
import AdManagement from '../views/cms/AdManagement.vue'
import CustomerList from '../views/ums/CustomerList.vue'

// SYS
import SystemSettings from '../views/sys/SystemSettings.vue'
import UserProfile from '../views/sys/UserProfile.vue'
import InventoryList from '../views/inv/InventoryList.vue'

import NotFound from '../views/NotFound.vue'

import MessagePush from '../views/cms/MessagePush.vue'
import BrandList from '../views/pms/BrandList.vue'

const routes = [
  { path: '/login', component: Login, meta: { title: '登录' } },
  {
    path: '/',
    component: MainLayout,
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', component: Analysis, meta: { title: '数据看板' } },
      
      { path: 'pms/product', component: ProductList, meta: { title: '商品列表' } },
      { path: 'pms/create', component: ProductDetail, meta: { title: '发布商品' } },
      { path: 'pms/category', component: CategoryList, meta: { title: '类目管理' } },
      { path: 'pms/brand', component: BrandList, meta: { title: '品牌管理' } },
      
      { path: 'ums/customer', component: CustomerList, meta: { title: '会员列表' } },
      
      { path: 'oms/chat', component: ChatConsole, meta: { title: '售后客服' } },
      { path: 'oms/order', component: OrderList, meta: { title: '订单列表' } },
      
      { path: 'cms/ad', component: AdManagement, meta: { title: '广告管理' } },
      { path: 'cms/message', component: MessagePush, meta: { title: '消息推送' } },

      { path: 'inv', component: InventoryList, meta: { title: '库存管理' } },
      
      { path: 'mkt', component: Marketing, meta: { title: '营销中心' } },

      { path: 'sys/profile', component: UserProfile, meta: { title: '个人中心' } },
      { path: 'sys', component: SystemSettings, meta: { title: '系统设置' } },

      
    ]
  },
  { path: '/:pathMatch(.*)*', component: NotFound }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 修复 TS 警告：from -> _from
router.beforeEach((to, _from, next) => {
  const token = localStorage.getItem('cola_admin_token')
  if (to.path !== '/login' && !token) {
    next('/login')
  }else if (to.path === '/login' && token) {
    next('/') 
  } 
  else {
    next()
  }
})

export default router