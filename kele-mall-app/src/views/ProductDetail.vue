<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
// 👇 修复：删除了未使用的 showToast
import { useCartStore } from '../store/cart' 

const router = useRouter()
const route = useRoute()
const cartStore = useCartStore()
const showSku = ref(false)
const activeTab = ref(0)
const skuAction = ref<'cart' | 'buy'>('cart')

const product = reactive({
  id: 1001,
  title: 'Loading...',
  price: 0,
  images: [] as string[],
  stock: 99
})

const reviews = [
  { id: 1, user: 'ColaUser_007', avatar: '', content: '质量非常好，发货也很快！', time: '2023-11-20', rate: 5 },
  { id: 2, user: '张三', avatar: '', content: '颜色很正，喜欢。', time: '2023-11-18', rate: 4 },
]

onMounted(() => {
  const id = route.params.id || '1001'
  product.id = Number(id)
  product.title = 'Michael Kors Mercer 中号皮质手提包 (C端演示版)'
  product.price = 3006
  product.images = ['https://placehold.co/375x375/f2f4f8/FF6B00?text=Bag+Image']
})

const specs = reactive([
  { name: '颜色', list: ['经典黑', '珍珠白'], selected: '经典黑' },
  { name: '材质', list: ['真皮', '磨砂'], selected: '真皮' }
])
const buyCount = ref(1)

const selectedSkuName = computed(() => specs.map(s => s.selected).join(' + '))
const currentPrice = computed(() => product.price)

const selectSpec = (specIndex: number, item: string) => {
  // 修复：添加判断，防止未定义
  if (specs[specIndex]) {
    specs[specIndex].selected = item
  }
}

const openSku = (type: 'cart' | 'buy') => {
  skuAction.value = type
  showSku.value = true
}

const onSkuConfirm = () => {
  // 修复：添加 ?. 安全访问符，防止 specs[0] 报错
  const s1 = specs[0]?.selected || ''
  const s2 = specs[1]?.selected || ''

  const skuData = {
    selectedSkuComb: {
      id: `${product.id}-${s1}-${s2}`,
      price: currentPrice.value * 100,
      imgUrl: product.images?.[0] || '', // 修复图片访问
      s1_name: s1,
      s2_name: s2
    },
    selectedNum: buyCount.value
  }

  if (skuAction.value === 'cart') {
    cartStore.addToCart(product, skuData)
    showSku.value = false
  } else {
    const buyItem = {
      id: product.id,
      skuId: skuData.selectedSkuComb.id,
      name: product.title,
      price: skuData.selectedSkuComb.price,
      img: skuData.selectedSkuComb.imgUrl,
      desc: `${skuData.selectedSkuComb.s1_name} / ${skuData.selectedSkuComb.s2_name}`,
      count: skuData.selectedNum,
      checked: true
    }
    cartStore.setBuyNowItem(buyItem)
    showSku.value = false
    router.push('/checkout?mode=direct')
  }
}
</script>

<template>
  <div class="bg-pageBg min-h-screen pb-[80px]">
    <van-nav-bar left-arrow @click-left="router.back()" title="商品详情" fixed placeholder class="!bg-white" />

    <van-tabs v-model:active="activeTab" sticky offset-top="46">
      <van-tab title="商品">
        <van-swipe class="h-96 w-full bg-white">
          <van-swipe-item v-for="(img, idx) in product.images" :key="idx">
            <img :src="img" class="w-full h-full object-cover"/>
          </van-swipe-item>
        </van-swipe>

        <div class="bg-white p-4 mb-3 rounded-b-2xl shadow-sm">
          <div class="text-primary text-2xl font-bold mb-2">¥{{ product.price }}</div>
          <div class="text-lg font-bold text-textMain leading-snug mb-3">{{ product.title }}</div>
          <div class="flex justify-between text-xs text-gray-400 bg-gray-50 p-2 rounded-lg">
            <span>库存: {{ product.stock }}</span><span>销量: 200+</span><span>上海发货</span>
          </div>
        </div>

        <div class="bg-white p-4 mb-3 rounded-2xl shadow-sm mx-3" @click="openSku('cart')">
          <div class="flex justify-between items-center">
            <div class="flex gap-3"><span class="font-bold text-sm">选择</span><span class="text-sm text-gray-500">{{ selectedSkuName }}</span></div>
            <van-icon name="arrow" color="#ccc" />
          </div>
        </div>

        <div class="bg-white p-4 rounded-2xl shadow-sm mx-3 min-h-[200px]">
           <div class="font-bold mb-2">商品详情</div>
           <p class="text-sm text-gray-600">这里是图文详情...</p>
        </div>
      </van-tab>

      <van-tab title="评价(2)">
         <div class="p-3">
            <div v-for="r in reviews" :key="r.id" class="bg-white p-3 rounded-xl mb-3 shadow-sm">
               <div class="flex justify-between items-center mb-2">
                  <div class="flex items-center gap-2">
                     <div class="w-8 h-8 bg-gray-200 rounded-full flex items-center justify-center text-xs">User</div>
                     <span class="text-sm font-bold">{{ r.user }}</span>
                  </div>
                  <van-rate v-model="r.rate" size="12" readonly color="#FF6B00" />
               </div>
               <div class="text-sm text-gray-700 mb-2">{{ r.content }}</div>
               <div class="text-xs text-gray-400">{{ r.time }}</div>
            </div>
         </div>
      </van-tab>
    </van-tabs>

    <van-action-bar>
      <van-action-bar-icon icon="chat-o" text="客服" />
      <van-action-bar-icon icon="cart-o" text="购物车" :badge="cartStore.totalCount || ''" @click="router.push('/cart')" />
      <van-action-bar-button type="warning" text="加入购物车" color="#ffa500" @click="openSku('cart')" />
      <van-action-bar-button type="danger" text="立即购买" color="#FF6B00" @click="openSku('buy')" />
    </van-action-bar>

    <van-action-sheet v-model:show="showSku" title="选择规格">
      <div class="p-4 pb-20">
        <div class="flex gap-3 mb-6">
          <img :src="product.images[0]" class="w-24 h-24 rounded-lg bg-gray-100 object-cover" />
          <div class="flex flex-col justify-end pb-1">
             <div class="text-primary font-bold text-xl">¥{{ currentPrice }}</div>
             <div class="text-xs text-gray-500 mt-1">已选: {{ selectedSkuName }}</div>
          </div>
        </div>
        <div v-for="(spec, idx) in specs" :key="idx" class="mb-5">
           <div class="text-sm font-bold mb-3">{{ spec.name }}</div>
           <div class="flex flex-wrap gap-3">
              <span v-for="item in spec.list" :key="item" class="px-4 py-1.5 rounded-full text-xs border cursor-pointer" :class="spec.selected === item ? 'bg-orange-50 border-primary text-primary' : 'bg-white border-gray-200'" @click="selectSpec(idx, item)">{{ item }}</span>
           </div>
        </div>
        <div class="flex justify-between items-center mb-6 pt-4 border-t">
           <div class="text-sm font-bold">购买数量</div>
           <van-stepper v-model="buyCount" min="1" max="5" />
        </div>
        <div class="fixed bottom-0 left-0 right-0 p-3 bg-white border-t safe-area-inset-bottom">
           <van-button round block type="danger" color="#FF6B00" @click="onSkuConfirm">确定</van-button>
        </div>
      </div>
    </van-action-sheet>
  </div>
</template>