<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showLoadingToast } from 'vant'

const router = useRouter()
const version = '1.0.0'
const isNewest = ref(true)

// 👇 修复：封装返回函数
const onClickLeft = () => router.back()

const onCheckUpdate = () => {
  const toast = showLoadingToast({ message: '检查更新中...', forbidClick: true });
  setTimeout(() => {
    toast.close();
    if (isNewest.value) {
      showToast('当前已是最新版本')
    } else {
      showToast('发现新版本 v1.0.1')
    }
  }, 1000)
}

const onRate = () => showToast('跳转应用商店评分...')
const onWebsite = () => showToast('复制官网地址成功')
</script>

<template>
  <div class="bg-pageBg min-h-screen">
    <van-nav-bar title="关于我们" left-arrow @click-left="onClickLeft" fixed placeholder />

    <div class="flex flex-col items-center py-10">
      <div class="w-20 h-20 bg-white rounded-2xl shadow-sm flex items-center justify-center mb-3">
        <span class="text-3xl">🎁</span>
      </div>
      <h2 class="text-lg font-bold text-textMain">ColaMall</h2>
      <p class="text-xs text-gray-400 mt-1">Version {{ version }} Build 20231129</p>
    </div>

    <van-cell-group inset>
      <van-cell title="版本更新" is-link @click="onCheckUpdate">
        <template #value>
          <span v-if="isNewest" class="text-gray-400">已是最新</span>
          <span v-else class="w-2 h-2 bg-red-500 rounded-full inline-block"></span>
        </template>
      </van-cell>
      <van-cell title="去评分" is-link @click="onRate" />
      <van-cell title="功能介绍" is-link to="/static/about" />
      <van-cell title="官方网站" is-link value="www.colamall.com" @click="onWebsite" />
    </van-cell-group>

    <div class="fixed bottom-8 left-0 right-0 text-center text-[10px] text-gray-300">
      Copyright © 2023 ColaMall. All Rights Reserved.
    </div>
  </div>
</template>