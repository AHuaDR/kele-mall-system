<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()
const activeIndex = ref(0)

// 完善的分类数据
const items = [
  { 
    text: '数码手机', 
    id: 0, // 对应索引 0
    children: [
      { text: '智能手机', id: 101, img: 'https://placehold.co/100x100/f0f0f0/333333?text=Phone' },
      { text: '蓝牙耳机', id: 102, img: 'https://placehold.co/100x100/f0f0f0/333333?text=Headset' },
      { text: '笔记本', id: 103, img: 'https://placehold.co/100x100/f0f0f0/333333?text=Laptop' },
      { text: '智能手表', id: 104, img: 'https://placehold.co/100x100/f0f0f0/333333?text=Watch' },
      { text: '平板电脑', id: 105, img: 'https://placehold.co/100x100/f0f0f0/333333?text=Pad' }
    ] 
  },
  { 
    text: '潮流服饰', 
    id: 1, // 对应索引 1
    children: [
      { text: '当季新品', id: 201, img: 'https://placehold.co/100x100/ffe4e1/bb2649?text=New' },
      { text: '男装', id: 202, img: 'https://placehold.co/100x100/f0f0f0/333333?text=Men' },
      { text: '女装', id: 203, img: 'https://placehold.co/100x100/fff0f5/ff69b4?text=Women' },
      { text: '运动鞋', id: 204, img: 'https://placehold.co/100x100/f0f0f0/333333?text=Shoes' },
      { text: '包袋', id: 205, img: 'https://placehold.co/100x100/f0f0f0/333333?text=Bag' }
    ] 
  },
  { 
    text: '家用电器', 
    id: 2, // 对应索引 2
    children: [
      { text: '智能电视', id: 301, img: 'https://placehold.co/100x100/f0f0f0/333333?text=TV' },
      { text: '空调', id: 302, img: 'https://placehold.co/100x100/f0f0f0/333333?text=Air' },
      { text: '洗衣机', id: 303, img: 'https://placehold.co/100x100/f0f0f0/333333?text=Wash' },
      { text: '冰箱', id: 304, img: 'https://placehold.co/100x100/f0f0f0/333333?text=Fridge' }
    ] 
  },
  { 
    text: '美妆护肤', 
    id: 3, 
    children: [
      { text: '口红', id: 401, img: 'https://placehold.co/100x100/fff0f5/ff69b4?text=Lip' },
      { text: '香水', id: 402, img: 'https://placehold.co/100x100/f0f0f0/333333?text=Perfume' },
      { text: '面膜', id: 403, img: 'https://placehold.co/100x100/f0f0f0/333333?text=Mask' }
    ] 
  },
  { 
    text: '生鲜食品', 
    id: 4, 
    children: [
      { text: '新鲜水果', id: 501, img: 'https://placehold.co/100x100/f0fff0/008000?text=Fruit' },
      { text: '海鲜水产', id: 502, img: 'https://placehold.co/100x100/e0ffff/0000ff?text=Sea' },
      { text: '休闲零食', id: 503, img: 'https://placehold.co/100x100/fffacd/ffa500?text=Snack' }
    ] 
  },
]

// 核心逻辑：监听路由参数，自动切换 Tab
onMounted(() => {
  const queryId = route.query.id
  if (queryId !== undefined && queryId !== null) {
    const index = Number(queryId)
    // 防止越界
    if (index >= 0 && index < items.length) {
      activeIndex.value = index
    }
  }
})
</script>

<template>
  <div class="bg-white h-screen flex flex-col">
    <van-nav-bar title="全部分类" fixed placeholder />
    
    <van-tree-select
      v-model:main-active-index="activeIndex"
      height="100%"
      :items="items"
    >
      <template #content>
        <div class="p-3 h-full overflow-y-auto pb-20">
           <div class="flex items-center justify-between mb-4 mt-2">
             <span class="font-bold text-sm text-gray-800">{{ items[activeIndex]?.text }}专区</span>
             <span class="text-xs text-gray-400">热销榜 ></span>
           </div>
           
           <div class="grid grid-cols-3 gap-y-6 gap-x-4 text-center">
              <div 
                v-for="sub in (items[activeIndex]?.children || [])" 
                :key="sub.id" 
                class="flex flex-col items-center group" 
                @click="router.push(`/search?category_id=${sub.id}`)"
              >
                 <div class="w-16 h-16 rounded-full bg-gray-50 mb-2 overflow-hidden border border-transparent group-active:border-primary transition-colors">
                    <img :src="sub.img" class="w-full h-full object-cover" />
                 </div>
                 <span class="text-xs text-gray-600 group-active:text-primary">{{ sub.text }}</span>
              </div>
           </div>

           <div class="mt-8 rounded-lg overflow-hidden h-24 relative">
              <img src="https://placehold.co/300x100/FF6B00/ffffff?text=AD" class="w-full h-full object-cover" />
              <div class="absolute bottom-1 right-1 text-[8px] text-white bg-black/20 px-1 rounded">广告</div>
           </div>
        </div>
      </template>
    </van-tree-select>
  </div>
</template>