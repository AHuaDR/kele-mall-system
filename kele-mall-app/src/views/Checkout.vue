<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useCartStore } from '../store/cart'
import { showLoadingToast, showSuccessToast } from 'vant'

const router = useRouter()
const route = useRoute()
const cartStore = useCartStore()

// 1. 地址逻辑
const currentAddress = ref({
  name: '请选择收货地址',
  tel: '',
  detail: ''
})

// 2. 优惠券
const showCoupon = ref(false)
const chosenCouponIndex = ref(-1)
const coupons = ref([
  { id: 1, available: 1, condition: '无门槛', reason: '', value: 150, name: '新人券', startAt: 1680000000, endAt: 1740000000, valueDesc: '1.5', unitDesc: '元', description: '全场通用' }
])

// 3. 商品列表获取逻辑 (核心修复)
const orderItems = computed(() => {
  // 如果 URL 参数 mode=direct，则读取 store 里的 buyNowItem
  if (route.query.mode === 'direct' && cartStore.buyNowItem) {
    return [cartStore.buyNowItem]
  }
  // 否则默认读取购物车中选中的商品
  return cartStore.cartList.filter(item => item.checked)
})

// 计算商品总价
const itemsPrice = computed(() => {
  return orderItems.value.reduce((sum, item) => sum + item.price * item.count, 0)
})

const discountAmount = computed(() => {
  const index = chosenCouponIndex.value
  if (index === -1 || !coupons.value[index]) return 0
  return coupons.value[index].value
})

const actualPrice = computed(() => Math.max(0, itemsPrice.value - discountAmount.value))

onMounted(() => {
  if (route.query.addressId) {
    currentAddress.value = { name: '李四', tel: '139****9999', detail: '上海市浦东新区' }
  } else {
    currentAddress.value = { name: '张三', tel: '138****0000', detail: '浙江省杭州市西湖区' }
  }
})

const onSelectAddress = () => router.push(`/address?from=checkout&mode=${route.query.mode || 'cart'}`)

const onPay = () => {
  if (orderItems.value.length === 0) return showLoadingToast('并没有商品需要支付')
  
  showLoadingToast({ message: '支付中...', forbidClick: true, duration: 1500 });
  setTimeout(() => {
    // 如果是购物车购买，支付成功后需清空购物车选中项 (这里简化为清空已购)
    if (route.query.mode !== 'direct') {
      const ids = orderItems.value.map(i => i.skuId)
      cartStore.removeItems(ids)
    }
    showSuccessToast('支付成功');
    router.replace('/order') 
  }, 1500);
}
</script>

<template>
  <div class="bg-pageBg min-h-screen pb-20">
    <van-nav-bar title="确认订单" left-arrow @click-left="router.back()" />
    
    <div class="bg-white p-4 mb-3 flex justify-between items-center" @click="onSelectAddress">
       <div v-if="currentAddress.tel">
         <div class="font-bold text-base mb-1">{{ currentAddress.name }} {{ currentAddress.tel }}</div>
         <div class="text-sm text-gray-500">{{ currentAddress.detail }}</div>
       </div>
       <div v-else class="font-bold text-base">请选择收货地址</div>
       <van-icon name="arrow" color="#999" />
    </div>

    <div class="bg-white p-3 mb-3">
       <div v-for="item in orderItems" :key="item.skuId" class="flex gap-2 mb-2 bg-gray-50 p-2 rounded">
          <img :src="item.img" class="w-16 h-16 object-cover rounded" />
          <div class="flex-1">
             <div class="text-sm line-clamp-1">{{ item.name }}</div>
             <div class="text-xs text-gray-400 mt-1">{{ item.desc }}</div>
             <div class="flex justify-between mt-2">
                <span class="text-primary font-bold">¥{{ (item.price/100).toFixed(2) }}</span>
                <span class="text-gray-500">x{{ item.count }}</span>
             </div>
          </div>
       </div>
       <div v-if="orderItems.length === 0" class="text-center text-gray-400 py-4 text-xs">
         暂无商品数据，请重新下单
       </div>
    </div>

    <van-coupon-cell :coupons="coupons" :chosen-coupon="chosenCouponIndex" @click="showCoupon = true" class="mb-3" />

    <div class="bg-white p-3 text-sm space-y-2 text-textSub mb-20">
       <div class="flex justify-between"><span>商品总价</span><span>¥{{ (itemsPrice / 100).toFixed(2) }}</span></div>
       <div class="flex justify-between"><span>运费</span><span>免运费</span></div>
       <div class="flex justify-between text-primary"><span>优惠立减</span><span>-¥{{ (discountAmount / 100).toFixed(2) }}</span></div>
    </div>

    <van-submit-bar :price="actualPrice" button-text="提交订单" button-color="#FF6B00" @submit="onPay" />

    <van-popup v-model:show="showCoupon" round position="bottom" style="height: 90%; padding-top: 4px;">
      <van-coupon-list :coupons="coupons" :chosen-coupon="chosenCouponIndex" @change="(index) => { showCoupon = false; chosenCouponIndex = index }" />
    </van-popup>
  </div>
</template>