<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'

const router = useRouter()

// 模拟收藏数据
const list = ref([
  { id: 1001, title: 'Michael Kors Mercer 中号皮质手提包', price: 3006, img: 'https://placehold.co/200x200/f2f4f8/bb2649?text=Bag' },
  { id: 1003, title: 'SK-II 神仙水护肤精华露', price: 1540, img: 'https://placehold.co/200x200/fff0f5/ff69b4?text=SK-II' },
  { id: 1004, title: 'Sony/索尼 WH-1000XM5 头戴式降噪耳机', price: 2499, img: 'https://placehold.co/200x200/e0e0e0/333?text=Sony' },
])

const goDetail = (id: number) => router.push(`/product/${id}`)

const onRemove = (id: number) => {
  list.value = list.value.filter(item => item.id !== id)
  showToast('已取消收藏')
}
</script>

<template>
  <div class="bg-pageBg min-h-screen">
    <van-nav-bar title="我的收藏" left-arrow @click-left="router.back()" fixed placeholder />
    
    <div v-if="list.length > 0" class="p-3">
       <div v-for="item in list" :key="item.id" class="bg-white p-3 rounded-xl mb-3 shadow-sm flex gap-3">
          <img :src="item.img" class="w-24 h-24 rounded-lg bg-gray-100 object-cover shrink-0" @click="goDetail(item.id)" />
          <div class="flex-1 flex flex-col justify-between">
             <div class="text-sm font-bold text-textMain line-clamp-2" @click="goDetail(item.id)">{{ item.title }}</div>
             <div class="flex justify-between items-end">
                <span class="text-primary font-bold text-lg"><span class="text-xs">¥</span>{{ item.price }}</span>
                <van-button size="mini" round plain type="danger" @click="onRemove(item.id)">取消收藏</van-button>
             </div>
          </div>
       </div>
    </div>

    <div v-else class="flex flex-col items-center justify-center pt-20">
       <van-icon name="star-o" size="64" color="#ddd" />
       <p class="text-gray-400 mt-4 text-sm">暂无收藏商品</p>
       <van-button size="small" round class="mt-4 w-24" to="/">去逛逛</van-button>
    </div>
  </div>
</template>