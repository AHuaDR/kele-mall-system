<script setup lang="ts">
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()

// 获取 URL 参数中的 type (例如: /static/about -> about)
const type = route.params.type as string

const title = computed(() => {
  const map: Record<string, string> = { 
    'about': '关于我们', 
    'privacy': '隐私政策', 
    'agreement': '用户协议', 
    'security': '账号安全', 
    'payment': '支付设置' 
  }
  return map[type] || '详情'
})

const content = computed(() => {
  const map: Record<string, string> = {
    'about': 'ColaMall 是一个专注于高品质生活的电商平台。我们致力于为您提供最优质的商品和最贴心的服务。\n\n当前版本：v1.0.0 (Beta)',
    'privacy': '1. 我们收集什么信息？\n我们收集您的基本个人信息以提供服务。\n\n2. 我们如何使用信息？\n用于订单处理和个性化推荐。\n\n3. 信息安全\n我们采用加密技术保护您的数据。',
    'agreement': '欢迎使用 ColaMall。\n\n1. 服务条款\n使用本App即代表您同意本协议。\n\n2. 用户行为\n禁止发布违规内容。\n\n3. 免责声明\n...',
    'security': '当前账号状态：安全\n\n已绑定手机：138****8888\n已设置登录密码\n未开启双重验证',
    'payment': '微信支付：已绑定\n支付宝：未绑定\n\n免密支付：未开启'
  }
  return map[type] || '暂无内容'
})
</script>

<template>
  <div class="bg-pageBg min-h-screen">
    <van-nav-bar :title="title" left-arrow @click-left="router.back()" fixed placeholder />
    <div class="p-4 m-3 bg-white rounded-lg shadow-sm">
      <div class="text-sm text-gray-600 leading-relaxed whitespace-pre-wrap min-h-[200px]">
        {{ content }}
      </div>
    </div>
  </div>
</template>