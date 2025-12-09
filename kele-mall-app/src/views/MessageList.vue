<script setup lang="ts">
import { useRouter } from 'vue-router'
const router = useRouter()

const msgs = [
  { id: 1, title: '物流通知', desc: '您的订单 ORD_001 已发货，请注意查收。', time: '10:00', icon: 'logistics', color: '#1989fa' },
  { id: 2, title: '活动优惠', desc: '双11预热开启！全场满300减50。', time: '昨天', icon: 'gift-o', color: '#FF6B00' },
  { id: 3, title: '系统通知', desc: '您的账号已成功升级为黄金会员。', time: '11-01', icon: 'volume-o', color: '#07c160' },
]
</script>

<template>
  <div class="bg-pageBg min-h-screen">
    <van-nav-bar title="消息中心" left-arrow @click-left="router.back()" fixed placeholder />
    
    <div class="p-3">
      <div v-for="item in msgs" :key="item.id" class="bg-white rounded-xl p-4 mb-3 shadow-sm flex gap-3" @click="router.push('/message/detail')">
         <div :class="`w-10 h-10 rounded-full flex items-center justify-center shrink-0`" :style="{ backgroundColor: item.color + '20' }">
            <van-icon :name="item.icon" size="20" :color="item.color" />
         </div>
         <div class="flex-1">
            <div class="flex justify-between items-center mb-1">
               <span class="font-bold text-sm text-textMain">{{ item.title }}</span>
               <span class="text-xs text-gray-400">{{ item.time }}</span>
            </div>
            <div class="text-xs text-textSub leading-normal">{{ item.desc }}</div>
         </div>
      </div>
    </div>
  </div>
</template>