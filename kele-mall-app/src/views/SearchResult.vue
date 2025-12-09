<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()
const keyword = ref('')
const categoryTitle = ref('')


const scrollToTop = () => {
  window.scrollTo(0, 0)
}

// 模拟数据库
const allProducts = [
  { id: 101, title: 'iPhone 15 Pro', catId: 101, price: 7999, img: 'https://placehold.co/300x300?text=Phone', sales: 1000 },
  { id: 102, title: '华为 Mate 60', catId: 101, price: 6999, img: 'https://placehold.co/300x300?text=Huawei', sales: 2000 },
  { id: 201, title: '耐克 Air Jordan', catId: 204, price: 1299, img: 'https://placehold.co/300x300?text=Nike', sales: 500 },
  { id: 202, title: '阿迪达斯 运动鞋', catId: 204, price: 899, img: 'https://placehold.co/300x300?text=Adidas', sales: 300 },
]

const list = ref<any[]>([])

onMounted(() => {
  // 1. 如果有 q 参数，说明是搜索
  if (route.query.q) {
    keyword.value = route.query.q as string
    list.value = allProducts.filter(p => p.title.toLowerCase().includes(keyword.value.toLowerCase()))
  } 
  // 2. 如果有 category_id (来自分类页跳转)
  else if (route.query.category_id) {
    const catId = Number(route.query.category_id)
    // 简单映射标题 (实际应从后端取)
    categoryTitle.value = '分类精选' 
    list.value = allProducts.filter(p => p.catId === catId)
    // 如果没找到数据，放点假的充数，防止空白难看
    if (list.value.length === 0) {
      list.value = allProducts // 兜底显示全部
    }
  } else {
    list.value = allProducts
  }
  scrollToTop()
})

const goDetail = (id: number) => router.push(`/product/${id}`)
const pageTitle = computed(() => keyword.value ? `搜索: ${keyword.value}` : (categoryTitle.value || '商品列表'))
</script>

<template>
  <div class="bg-pageBg min-h-screen">
    <van-nav-bar :title="pageTitle" left-arrow @click-left="router.back()" fixed placeholder />
    
    <div class="p-3 grid grid-cols-2 gap-3">
      <div v-for="item in list" :key="item.id" class="bg-white rounded-xl overflow-hidden shadow-sm flex flex-col" @click="goDetail(item.id)">
        <div class="w-full aspect-square bg-gray-100"><img :src="item.img" class="w-full h-full object-cover" /></div>
        <div class="p-2 flex-1 flex flex-col justify-between">
           <div class="text-sm text-textMain line-clamp-2 h-10 mb-1">{{ item.title }}</div>
           <div class="flex justify-between items-end">
              <span class="text-primary font-bold text-base"><span class="text-xs">¥</span>{{ item.price }}</span>
              <span class="text-textSub text-[10px]">{{ item.sales }}人付款</span>
           </div>
        </div>
      </div>
    </div>
    
    <div v-if="list.length === 0" class="text-center text-gray-400 py-10">暂无相关商品</div>
  </div>
</template>