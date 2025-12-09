<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'

const router = useRouter()

// 修复点：给每个对象添加 description 字段
const coupons = ref([
  {
    id: 1,
    available: 1,
    condition: '无使用门槛\n最多优惠1.5元',
    reason: '',
    value: 150,
    name: '新人专享券',
    startAt: 1680000000,
    endAt: 1740000000,
    valueDesc: '1.5',
    unitDesc: '元',
    description: '仅限新用户注册使用，全场通用' // 👈 补全这个字段
  },
  {
    id: 2, 
    available: 1,
    condition: '满200元可用',
    reason: '',
    value: 5000, // 50元
    name: '品类满减券',
    startAt: 1680000000,
    endAt: 1740000000,
    valueDesc: '50',
    unitDesc: '元',
    description: '仅限购买数码家电类商品使用' // 👈 补全这个字段
  }
])

const chosenCoupon = ref(-1)

const onExchange = () => showToast('兑换成功')
</script>

<template>
  <div class="bg-gray-50 min-h-screen pb-4">
    <van-nav-bar title="我的优惠券" left-arrow @click-left="router.back()" fixed placeholder />
    
    <van-coupon-list
      :coupons="coupons"
      :chosen-coupon="chosenCoupon"
      :disabled-coupons="[]"
      :show-close-button="false"
      @exchange="onExchange"
    />
  </div>
</template>