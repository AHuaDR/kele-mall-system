<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()
const active = ref(0)

// 模拟订单数据
const orders = ref([
  { id: 'ORD_001', status: '待发货', price: 299.00, goods: [{ title: 'iPhone 15 Case', img: 'https://placehold.co/60' }] },
  { id: 'ORD_002', status: '已完成', price: 99.00, goods: [{ title: '可乐卫衣', img: 'https://placehold.co/60' }] },
])

onMounted(() => {
  // 根据 URL 参数设置选中的 Tab
  if (route.query.tab) {
    active.value = Number(route.query.tab)
  }
})
</script>

<template>
  <div class="bg-pageBg min-h-screen">
    <van-nav-bar title="我的订单" left-arrow @click-left="router.back()" fixed placeholder />
    
    <van-tabs v-model:active="active" sticky color="#FF6B00">
      <van-tab title="全部"></van-tab>
      <van-tab title="待付款"></van-tab>
      <van-tab title="待发货"></van-tab>
      <van-tab title="待收货"></van-tab>
      <van-tab title="待评价"></van-tab>
    </van-tabs>

    <div class="p-3">
      <div v-for="order in orders" :key="order.id" class="bg-white rounded-lg p-3 mb-3 shadow-sm">
         <div class="flex justify-between text-xs mb-3">
            <span class="text-gray-500">订单号: {{ order.id }}</span>
            <span class="text-primary">{{ order.status }}</span>
         </div>
         <div v-for="(g, i) in order.goods" :key="i" class="flex gap-2 mb-2">
            <img :src="g.img" class="w-16 h-16 rounded bg-gray-100" />
            <div class="flex-1">
               <div class="text-sm font-bold text-textMain">{{ g.title }}</div>
            </div>
         </div>
         <div class="text-right border-t pt-2 mt-2">
            <span class="text-xs text-gray-500 mr-2">实付款</span>
            <span class="text-base font-bold text-textMain">¥{{ order.price }}</span>
         </div>
         <div class="flex justify-end gap-2 mt-3">
            <van-button size="small" round @click="router.push('/logistics/123')">查看物流</van-button>
            <van-button size="small" round type="primary" plain color="#FF6B00">确认收货</van-button>
         </div>
      </div>
    </div>
  </div>
</template>