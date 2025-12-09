<script setup lang="ts">
import { useRouter } from 'vue-router'
const router = useRouter()
</script>

<template>
  <div class="bg-pageBg min-h-screen">
    <van-nav-bar title="消息详情" left-arrow @click-left="router.back()" fixed placeholder />
    <div class="p-4">
      <h2 class="text-xl font-bold mb-2">双11预热开启！全场满300减50</h2>
      <div class="text-xs text-gray-400 mb-6">2023-11-20 10:00</div>
      <div class="bg-white p-4 rounded-xl shadow-sm text-sm text-gray-600 leading-relaxed min-h-[200px]">
        <p>亲爱的用户：</p>
        <p class="mt-2">ColaMall 双11大促即将开始！我们为您准备了丰厚的优惠券。</p>
        <ul class="list-disc pl-5 mt-2 space-y-1">
          <li>全场商品每满300减50，上不封顶</li>
          <li>黄金会员额外享受 95 折</li>
          <li>每日 10:00 限量抢免单</li>
        </ul>
        <p class="mt-4 text-right">ColaMall 运营团队</p>
      </div>
    </div>
  </div>
</template>