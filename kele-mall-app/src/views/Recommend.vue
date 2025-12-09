<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const loading = ref(false)
const finished = ref(false)
const list = ref<any[]>([])

const onLoad = () => {
  setTimeout(() => {
    for (let i = 0; i < 10; i++) {
      const id = list.value.length + 1
      list.value.push({
        id: id,
        img: 'https://placehold.co/300x400/f2f4f8/FF6B00?text=Hot',
        title: 'Michael Kors 2024 春夏新款轻奢女包',
        price: '2999',
        sales: '500'
      })
    }
    loading.value = false
    if (list.value.length >= 20) finished.value = true
  }, 500)
}

const goDetail = (id: number) => router.push(`/product/${id}`)
</script>

<template>
  <div class="bg-pageBg min-h-screen">
    <van-nav-bar title="好物推荐" left-arrow @click-left="router.back()" fixed placeholder />
    <van-list v-model:loading="loading" :finished="finished" finished-text="没有更多了" @load="onLoad">
      <div class="p-3 grid grid-cols-2 gap-3">
        <div v-for="item in list" :key="item.id" class="bg-white rounded-xl overflow-hidden shadow-sm flex flex-col" @click="goDetail(item.id)">
          <div class="w-full bg-gray-100"><img :src="item.img" class="w-full h-full object-cover" /></div>
          <div class="p-2 flex-1 flex flex-col justify-between">
             <div class="text-sm text-textMain line-clamp-2 h-10 mb-2">{{ item.title }}</div>
             <div class="flex justify-between items-end">
                <span class="text-primary font-bold text-base"><span class="text-xs">¥</span>{{ item.price }}</span>
                <span class="text-textSub text-[10px]">{{ item.sales }}人付款</span>
             </div>
          </div>
        </div>
      </div>
    </van-list>
  </div>
</template>