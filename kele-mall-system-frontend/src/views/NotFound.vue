<script setup lang="ts">
import { useRouter } from 'vue-router'
const router = useRouter()
</script>

<template>
  <div class="h-screen w-full bg-bg-100 flex flex-col items-center justify-center relative overflow-hidden">
    <div class="absolute top-1/3 left-1/3 w-96 h-96 bg-primary-100 rounded-full blur-[120px] opacity-30 animate-pulse"></div>
    <div class="absolute bottom-1/4 right-1/4 w-64 h-64 bg-accent-100 rounded-full blur-[100px] opacity-20"></div>

    <h1 class="text-[10rem] md:text-[15rem] font-black text-transparent bg-clip-text bg-gradient-to-b from-gray-700 to-bg-100 select-none leading-none z-0">
      404
    </h1>

    <div class="z-10 text-center -mt-10">
      <h2 class="text-3xl font-bold text-white mb-4 tracking-wider">页面迷失在深海</h2>
      <p class="text-text-200 mb-8 text-lg">
        您访问的路径不存在，或者权限不足。
      </p>
      
      <div class="flex gap-4 justify-center">
        <el-button size="large" @click="router.back()" class="!bg-transparent !text-text-200 !border-bg-300 hover:!border-white hover:!text-white">
          返回上一页
        </el-button>
        <el-button type="primary" size="large" color="#3D5A80" class="w-32 font-bold" @click="router.push('/')">
          回到首页
        </el-button>
      </div>
    </div>
  </div>
</template>