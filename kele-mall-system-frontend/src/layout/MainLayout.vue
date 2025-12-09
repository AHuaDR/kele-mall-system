<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { 
  Goods, List, PriceTag, Box, Bell, Monitor,
  Tools, Message, Wallet, InfoFilled, 
  Picture, UserFilled 
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const isCollapse = ref(false)
const router = useRouter()

const menus: any = [
  { index: '/dashboard', icon: Monitor, title: '数据看板' },
  { 
    index: '/pms', icon: Goods, title: '商品管理', 
    children: [
      { index: '/pms/product', title: '商品列表' },
      { index: '/pms/brand', title: '品牌管理' },
      { index: '/pms/create', title: '发布商品' },
      { index: '/pms/category', title: '类目管理' }
    ]
  },
  { index: '/oms', icon: List, title: '订单管理',
    children: [
      { index: '/oms/order', title: '订单列表' },
      { index: '/oms/chat', title: '售后客服' }
    ]
  },
  { 
    index: '/ums', icon: UserFilled, title: '用户管理', 
    children: [
      { index: '/ums/customer', title: '会员列表' }
    ]
  },
  { 
    index: '/cms', icon: Picture, title: '内容管理', 
    children: [
      { index: '/cms/ad', title: '广告配置' },
      { index: '/cms/message', title: '消息推送' }
    ]
  },
  { 
  index: '/mkt', icon: PriceTag, title: '营销中心',
  },
  { index: '/inv', icon: Box, title: '库存管理' },
  { index: '/sys', icon: Tools, title: '系统设置' },
]

const msgList = ref([
  { id: 1, title: '新订单提醒', content: '用户张三刚刚下了一笔 ¥299 的订单', time: '5分钟前', type: 'order', read: false },
  { id: 2, title: '库存预警', content: 'iPhone 15 Pro Max 库存不足 10 件', time: '30分钟前', type: 'warning', read: false },
  { id: 3, title: '系统公告', content: '今晚 02:00 进行系统维护升级', time: '2小时前', type: 'system', read: true },
])

const unreadCount = computed(() => msgList.value.filter(m => !m.read).length)

const getIconColor = (type: string) => {
  if (type === 'order') return 'bg-blue-500'
  if (type === 'warning') return 'bg-orange-500'
  return 'bg-gray-500'
}

const handleRead = (item: any) => {
  if(!item.read) item.read = true
}

const clearAll = () => {
  msgList.value = []
  ElMessage.success('全部消息已清空')
}
</script>

<template>
  <el-container class="h-screen w-full">
    <el-aside 
      :width="isCollapse ? '64px' : '220px'" 
      class="transition-all duration-300 ease-in-out bg-bg-200 border-r border-bg-300 overflow-hidden"
    >
      <div class="h-16 flex items-center justify-center border-b border-bg-300 whitespace-nowrap overflow-hidden">
        <span v-if="!isCollapse" class="text-xl font-bold text-white tracking-wider">COLA ADMIN</span>
        <span v-else class="text-xl font-bold text-accent-200">C</span>
      </div>
      
      <el-menu
        active-text-color="#acc2ef"
        background-color="#1f2b3e"
        class="border-none el-menu-vertical-demo" 
        text-color="#e0e0e0"
        :collapse="isCollapse"
        :collapse-transition="false"
        :router="true"
        :default-active="router.currentRoute.value.path"
      >
        <template v-for="(item, idx) in menus" :key="idx">
          <el-sub-menu v-if="item.children" :index="item.index">
            <template #title>
              <el-icon><component :is="item.icon" /></el-icon>
              <span>{{ item.title }}</span>
            </template>
            <el-menu-item v-for="sub in item.children" :key="sub.index" :index="sub.index">
              {{ sub.title }}
            </el-menu-item>
          </el-sub-menu>
          <el-menu-item v-else :index="item.index">
            <el-icon><component :is="item.icon" /></el-icon>
            <template #title>{{ item.title }}</template>
          </el-menu-item>
        </template>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="bg-bg-200 h-16 flex items-center justify-between border-b border-bg-300 px-6">
        <div class="flex items-center gap-4">
          <div class="cursor-pointer text-text-200 hover:text-white transition-colors" @click="isCollapse = !isCollapse">
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16"></path></svg>
          </div>
          <el-breadcrumb separator="/" class="hidden md:flex ml-4">
            <template v-for="(matched, index) in router.currentRoute.value.matched" :key="index">
              <el-breadcrumb-item v-if="matched.meta && matched.meta.title">
                <span :class="index === router.currentRoute.value.matched.length - 1 ? 'text-white font-bold' : 'text-text-200'">
                  {{ matched.meta.title }}
                </span>
              </el-breadcrumb-item>
            </template>
          </el-breadcrumb>
        </div>
        
        <div class="flex items-center gap-6">
          <el-popover placement="bottom" :width="320" trigger="click" popper-class="msg-popover">
            <template #reference>
              <div class="relative cursor-pointer mt-1">
                 <el-badge :value="unreadCount" :hidden="unreadCount === 0" :max="99" class="item">
                    <el-icon :size="20" class="text-text-200 hover:text-white transition-colors"><Bell /></el-icon>
                 </el-badge>
              </div>
            </template>
            <div class="flex flex-col h-full">
               <div class="flex justify-between items-center px-4 py-3 border-b border-bg-300">
                  <span class="font-bold text-white">消息通知 ({{ unreadCount }})</span>
                  <el-button link type="danger" size="small" @click="clearAll">清空</el-button>
               </div>
               <div class="max-h-[300px] overflow-y-auto">
                  <div v-for="msg in msgList" :key="msg.id" 
                    class="flex gap-3 px-4 py-3 border-b border-bg-300 hover:bg-bg-300 cursor-pointer transition-colors"
                    :class="{'opacity-50': msg.read}" @click="handleRead(msg)">
                     <div :class="`w-8 h-8 rounded-full flex items-center justify-center text-white shrink-0 ${getIconColor(msg.type)}`">
                        <el-icon v-if="msg.type==='order'"><Wallet /></el-icon>
                        <el-icon v-else-if="msg.type==='warning'"><InfoFilled /></el-icon>
                        <el-icon v-else><Message /></el-icon>
                     </div>
                     <div class="flex-1">
                        <div class="flex justify-between items-start mb-1">
                           <span class="text-white text-sm font-medium line-clamp-1">{{ msg.title }}</span>
                           <span class="text-xs text-text-200 shrink-0 ml-2">{{ msg.time }}</span>
                        </div>
                        <p class="text-text-200 text-xs line-clamp-2">{{ msg.content }}</p>
                     </div>
                     <div v-if="!msg.read" class="w-2 h-2 rounded-full bg-red-500 mt-2 shrink-0"></div>
                  </div>
               </div>
            </div>
          </el-popover>

          <el-dropdown trigger="click">
            <div class="flex items-center gap-3 cursor-pointer outline-none">
              <el-avatar :size="32" class="bg-accent-100">PM</el-avatar>
              <span class="text-sm text-text-200">Admin</span>
            </div>
            <template #dropdown>
              <el-dropdown-menu class="!bg-bg-200 !border-bg-300">
                <el-dropdown-item @click="router.push('/sys/profile')"><span class="text-text-200">个人中心</span></el-dropdown-item>
                <el-dropdown-item divided @click="router.push('/login')"><span class="text-red-400">退出登录</span></el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="bg-bg-100 p-6">
        <router-view v-slot="{ Component }">
          <transition name="el-fade-in" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>