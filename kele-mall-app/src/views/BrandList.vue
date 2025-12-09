<script setup lang="ts">
import { useRouter } from 'vue-router'
const router = useRouter()

// 品牌数据
const brands = [
  { id: 1, name: 'Apple', logo: 'https://placehold.co/100x100/000/fff?text=Apple' },
  { id: 2, name: '华为', logo: 'https://placehold.co/100x100/cf0a2c/fff?text=HUAWEI' },
  { id: 3, name: '小米', logo: 'https://placehold.co/100x100/ff6700/fff?text=MI' },
  { id: 4, name: 'Dior', logo: 'https://placehold.co/100x100/000/fff?text=Dior' },
  { id: 5, name: 'Nike', logo: 'https://placehold.co/100x100/000/fff?text=Nike' },
  { id: 6, name: 'Sony', logo: 'https://placehold.co/100x100/000/fff?text=Sony' },
]

const goSearch = (name: string) => {
  // 点击品牌，去搜索结果页搜该品牌
  router.push({ path: '/search', query: { q: name } })
}
</script>

<template>
  <div class="bg-pageBg min-h-screen">
    <van-nav-bar title="品牌专区" left-arrow @click-left="router.back()" fixed placeholder />
    
    <div class="p-3 grid grid-cols-3 gap-3">
      <div 
        v-for="brand in brands" :key="brand.id" 
        class="bg-white rounded-xl flex flex-col items-center justify-center p-4 shadow-sm active:scale-95 transition-transform"
        @click="goSearch(brand.name)"
      >
        <img :src="brand.logo" class="w-12 h-12 rounded-full mb-2 object-cover" />
        <span class="text-sm font-medium text-textMain">{{ brand.name }}</span>
      </div>
    </div>
  </div>
</template>