<script setup lang="ts">
import { useCartStore } from '../store/cart'
import { useRouter } from 'vue-router'
import { showConfirmDialog } from 'vant'

const cartStore = useCartStore()
const router = useRouter()

const onSubmit = () => {
  if (cartStore.totalPrice === 0) return
  // 跳转到结算页
  router.push('/checkout')
}

const onDelete = (id: string) => {
  showConfirmDialog({ title: '确认删除?', message: '删除后无法恢复' })
    .then(() => cartStore.removeItems([id]))
}
</script>

<template>
  <div class="bg-pageBg min-h-screen pb-24">
    <van-nav-bar title="购物车" fixed placeholder />
    
    <div v-if="cartStore.cartList.length === 0" class="flex flex-col items-center justify-center pt-20">
      <van-icon name="shopping-cart-o" size="64" color="#ddd" />
      <p class="text-gray-400 mt-4 text-sm">购物车空空如也</p>
      <van-button size="small" round class="mt-4 w-24" to="/">去逛逛</van-button>
    </div>

    <div v-else class="px-3 pt-3">
      <van-swipe-cell v-for="item in cartStore.cartList" :key="item.skuId" class="mb-3 rounded-xl overflow-hidden shadow-sm bg-white">
        <van-card
          :price="(item.price / 100).toFixed(2)"
          :desc="item.desc"
          :title="item.name"
          :thumb="item.img"
        >
          <template #tags>
             <van-checkbox v-model="item.checked" checked-color="#FF6B00" class="mt-2"></van-checkbox>
          </template>
          <template #num>
             <van-stepper v-model="item.count" theme="round" button-size="20" disable-input />
          </template>
        </van-card>
        <template #right>
          <van-button square text="删除" type="danger" class="h-full" @click="onDelete(item.skuId)" />
        </template>
      </van-swipe-cell>
    </div>

    <van-submit-bar 
      :price="cartStore.totalPrice" 
      button-text="去结算" 
      @submit="onSubmit"
      class="mb-[50px]"
    />
  </div>
</template>