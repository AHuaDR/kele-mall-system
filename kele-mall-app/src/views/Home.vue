<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'

const router = useRouter()
const isLoading = ref(false)

const currentCity = ref('深圳')
const showCityPopup = ref(false)
const cityList = ['北京', '上海', '广州', '深圳', '杭州', '成都']
const searchValue = ref('')

// 1. 轮播图数据 (之前定义了没用到的就是这个)
const banners = [
  { id: 1, img: 'https://placehold.co/400x200/FFDDC2/000000?text=Grand+Opening', link: '/product/1004' },
  { id: 2, img: 'https://placehold.co/400x200/e0e0e0/333333?text=New+Arrivals', link: '/category' },
  { id: 3, img: 'https://placehold.co/400x200/FF6B00/ffffff?text=Flash+Sale', link: '/recommend' }
]

// 2. 胶囊导航
const capsuleNavs = [
  { id: 1, name: '奢侈品推荐', icon: 'gem-o', color: 'text-orange-500', categoryId: 1 },
  { id: 2, name: '智能设备', icon: 'clock-o', color: 'text-orange-500', categoryId: 0 },
  { id: 3, name: '精品家电', icon: 'gift-o', color: 'text-red-500', categoryId: 2 },
]

// 3. 商品列表
const productList = [
  { id: 1001, brand: 'Michael Kors', name: 'Mercer 中号皮质手提包', price: 3006, img: 'https://placehold.co/300x300/ffefef/bb2649?text=Bag', colors: ['#87CEEB', '#1A1A1A'] },
  { id: 1002, brand: 'Dior', name: '智能复古手表', price: 1599, img: 'https://placehold.co/300x300/e0e0e0/000000?text=Watch', colors: ['#333333'] },
]

// 4. 品牌推荐数据
const brandRecs = [
  { id: 1, name: '数码推荐', img: 'https://placehold.co/100x80/2F4F4F/FFFFFF?text=Digital', keyword: '数码' },
  { id: 2, name: '家电精选', img: 'https://placehold.co/100x80/8B4513/FFFFFF?text=Home', keyword: '家电' },
  { id: 3, name: '大牌美妆', img: 'https://placehold.co/100x80/191970/FFFFFF?text=Beauty', keyword: '美妆' },
]

const onRefresh = () => {
  setTimeout(() => { showToast('刷新成功'); isLoading.value = false }, 1000)
}

const onSelectCity = (city: string) => {
  currentCity.value = city; showCityPopup.value = false
}

const onSearch = () => {
  if (!searchValue.value) return showToast('请输入关键词')
  router.push({ path: '/search', query: { q: searchValue.value } })
}

// 跳转逻辑
const goDetail = (id: number) => router.push(`/product/${id}`)
const goCategory = (index: number = 0) => router.push({ path: '/category', query: { id: index } })
const goMessage = () => router.push('/message')
const goRecommend = () => router.push('/recommend')
const goBrandList = () => router.push('/brand')
const goBrandBlock = (keyword: string) => {
  router.push({ path: '/search', query: { q: keyword } })
}
const onBannerClick = (link: string) => {
  if (link) router.push(link)
}
</script>

<template>
  <van-pull-refresh v-model="isLoading" @refresh="onRefresh">
    <div class="bg-pageBg min-h-screen pb-20 px-4 pt-2">
      <div class="flex items-center justify-between gap-3 mb-4 sticky top-0 bg-pageBg z-50 py-2">
        <div class="flex items-center text-textMain font-medium text-sm shrink-0" @click="showCityPopup = true">
          {{ currentCity }} <van-icon name="arrow-down" class="ml-1 text-xs" />
        </div>
        <van-search v-model="searchValue" shape="round" placeholder="搜索好物" class="flex-1 !p-0 !bg-transparent" :clearable="false" @search="onSearch" />
        <van-icon name="chat-o" class="text-textMain text-xl shrink-0" badge="9" @click="goMessage" />
      </div>

      <div class="rounded-2xl overflow-hidden mb-6 shadow-soft">
        <van-swipe :autoplay="3000" indicator-color="white" class="h-36">
          <van-swipe-item v-for="item in banners" :key="item.id" @click="onBannerClick(item.link)">
            <img :src="item.img" class="w-full h-full object-cover" />
          </van-swipe-item>
        </van-swipe>
      </div>

      <div class="flex justify-between gap-3 mb-8 overflow-x-auto no-scrollbar">
         <div v-for="nav in capsuleNavs" :key="nav.id" 
           class="flex-1 bg-white rounded-full py-2.5 px-2 flex items-center justify-center gap-1.5 shadow-sm whitespace-nowrap min-w-[100px] active:bg-gray-50"
           @click="goCategory(nav.categoryId)">
            <van-icon :name="nav.icon" :class="nav.color" class="text-lg" />
            <span class="text-xs font-bold text-textMain">{{ nav.name }}</span>
         </div>
      </div>

      <div class="flex items-center justify-between mb-4">
        <h3 class="text-lg font-bold text-textMain">热门推荐</h3>
        <div class="flex items-center text-primary text-xs" @click="goRecommend">更多 <van-icon name="arrow" /></div>
      </div>

      <div class="grid grid-cols-2 gap-3 mb-8">
         <div v-for="p in productList" :key="p.id" class="bg-white rounded-xl p-3 shadow-soft relative flex flex-col active:scale-[0.98] transition-transform" @click="goDetail(p.id)">
            <div class="w-full aspect-square bg-gray-50 rounded-lg mb-3 flex items-center justify-center overflow-hidden">
               <img :src="p.img" class="w-full h-full object-cover mix-blend-multiply" />
            </div>
            <div class="flex-1 flex flex-col">
               <div class="text-[10px] text-gray-400 mb-0.5">{{ p.brand }}</div>
               <div class="text-sm font-medium text-textMain line-clamp-1 mb-2">{{ p.name }}</div>
               <div class="mt-auto flex justify-between items-end">
                  <div class="text-primary font-bold text-base"><span class="text-xs">¥</span>{{ p.price }}</div>
               </div>
            </div>
         </div>
      </div>

      <div class="mb-4">
         <div class="flex items-center justify-between mb-4">
           <h3 class="text-lg font-bold text-textMain">品牌推荐</h3>
           <div class="flex items-center text-primary text-xs" @click="goBrandList">更多 <van-icon name="arrow" /></div>
         </div>
         <div class="grid grid-cols-3 gap-3">
            <div v-for="b in brandRecs" :key="b.id" class="bg-white rounded-xl p-2 flex flex-col items-center shadow-soft active:bg-gray-50" @click="goBrandBlock(b.keyword)">
               <img :src="b.img" class="w-16 h-12 object-contain mb-2" />
               <span class="text-[10px] text-gray-500">{{ b.name }}</span>
            </div>
         </div>
      </div>

      <van-popup v-model:show="showCityPopup" position="bottom" round>
        <div class="p-4">
          <div class="text-center font-bold mb-4">选择城市</div>
          <div class="grid grid-cols-3 gap-3">
            <div v-for="city in cityList" :key="city" class="text-center py-2 rounded bg-gray-100 text-sm active:bg-primary active:text-white" @click="onSelectCity(city)">{{ city }}</div>
          </div>
        </div>
      </van-popup>
    </div>
  </van-pull-refresh>
</template>

<style scoped>
.no-scrollbar::-webkit-scrollbar { display: none; }
.no-scrollbar { -ms-overflow-style: none; scrollbar-width: none; }
.shadow-soft { box-shadow: 0 4px 20px rgba(0, 0, 0, 0.03); }
:deep(.van-search__content) { background-color: #fff; }
</style>