<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

// 模拟物流轨迹
const steps = [
  { text: '【深圳市】快件已签收，签收人：门口暂存。如有问题请电联快递员', desc: '2023-11-29 14:30', active: true },
  { text: '【深圳市】快件已到达 深圳南山科技园公司', desc: '2023-11-29 09:12' },
  { text: '【深圳市】深圳转运中心 已发出', desc: '2023-11-28 22:40' },
  { text: '【杭州市】快件已揽收', desc: '2023-11-27 18:20' },
  { text: '商家已发货', desc: '2023-11-27 16:00' },
]
</script>

<template>
  <div class="bg-pageBg min-h-screen">
    <van-nav-bar title="物流详情" left-arrow @click-left="router.back()" fixed placeholder />

    <div class="bg-white p-4 mb-3 flex gap-3 items-center">
       <div class="w-12 h-12 bg-gray-100 rounded flex items-center justify-center text-xl">🚚</div>
       <div>
          <div class="text-sm font-bold text-textMain">顺丰速运</div>
          <div class="text-xs text-textSub mt-1">运单号：SF1234567890</div>
          <div class="text-xs text-textSub">官方电话：95338</div>
       </div>
    </div>

    <div class="bg-white p-4">
       <van-steps direction="vertical" :active="0" active-color="#FF6B00">
          <van-step v-for="(step, index) in steps" :key="index">
             <h3 :class="index === 0 ? 'text-textMain font-bold' : 'text-gray-500'">{{ step.text }}</h3>
             <p>{{ step.desc }}</p>
          </van-step>
       </van-steps>
    </div>
  </div>
</template>