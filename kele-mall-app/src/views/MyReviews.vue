<script setup lang="ts">
import { useRouter } from 'vue-router'
const router = useRouter()

const reviews = [
  { id: 1, title: 'iPhone 15 Pro', img: 'https://placehold.co/100', content: '手感很好，物流很快！', rate: 5, time: '2023-11-20' },
  { id: 2, title: '卫衣', img: 'https://placehold.co/100', content: '稍微有点起球，习惯好评。', rate: 4, time: '2023-10-01' },
]
</script>

<template>
  <div class="bg-pageBg min-h-screen">
    <van-nav-bar title="我的评价" left-arrow @click-left="router.back()" fixed placeholder />
    <div class="p-3">
       <div v-for="item in reviews" :key="item.id" class="bg-white p-4 rounded-xl mb-3 shadow-sm">
          <div class="flex gap-3 mb-3 border-b border-gray-50 pb-3">
             <img :src="item.img" class="w-12 h-12 rounded bg-gray-100" />
             <div>
                <div class="text-sm font-bold">{{ item.title }}</div>
                <van-rate v-model="item.rate" size="12" readonly color="#FF6B00" />
             </div>
          </div>
          <div class="text-sm text-gray-700">{{ item.content }}</div>
          <div class="text-xs text-gray-400 mt-2">{{ item.time }}</div>
       </div>
    </div>
  </div>
</template>