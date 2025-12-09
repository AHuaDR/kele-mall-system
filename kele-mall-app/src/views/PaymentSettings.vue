<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'

const router = useRouter()
const wechatPay = ref(true)
const alipay = ref(false)
const noSecret = ref(false)

const onBindCard = () => showToast('跳转银行卡绑定流程...')
</script>

<template>
  <div class="bg-pageBg min-h-screen">
    <van-nav-bar title="支付设置" left-arrow @click-left="router.back()" fixed placeholder />
    
    <div class="mt-3">
      <div class="px-4 py-2 text-xs text-gray-400">支付方式</div>
      <van-cell-group inset>
        <van-cell title="微信支付" center>
          <template #right-icon><van-switch v-model="wechatPay" size="20" active-color="#07c160" /></template>
        </van-cell>
        <van-cell title="支付宝" center>
          <template #right-icon><van-switch v-model="alipay" size="20" active-color="#1677ff" /></template>
        </van-cell>
        <van-cell title="银行卡" is-link value="已绑定 1 张" @click="onBindCard" />
      </van-cell-group>

      <div class="px-4 py-2 text-xs text-gray-400 mt-2">安全设置</div>
      <van-cell-group inset>
        <van-cell title="修改支付密码" is-link to="/security/mod/pay_password" />
        <van-cell title="小额免密支付" center label="单笔 200 元内免密">
          <template #right-icon><van-switch v-model="noSecret" size="20" active-color="#FF6B00" /></template>
        </van-cell>
        <van-cell title="生物支付" is-link value="面容ID" />
      </van-cell-group>
    </div>
  </div>
</template>