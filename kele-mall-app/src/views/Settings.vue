<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import { showConfirmDialog, showToast } from 'vant'

const router = useRouter()
const userStore = useUserStore()

// 👇 修复：封装返回函数，解决 TS 报错 "属性 router 不存在"
const onClickLeft = () => router.back()

const onLogout = () => {
  showConfirmDialog({ title: '提示', message: '确定要退出登录吗？' })
    .then(() => {
      userStore.logout()
      showToast('已退出')
      router.replace('/') // 回首页
    })
}
</script>

<template>
  <div class="bg-pageBg min-h-screen">
    <van-nav-bar title="设置" left-arrow @click-left="onClickLeft" fixed placeholder />

    <div class="mt-3">
      <van-cell-group inset>
        <van-cell title="个人资料" is-link to="/profile/edit" />
        <van-cell title="账号安全" is-link value="密码/手机号" to="/security" />
        <van-cell title="支付设置" is-link to="/static/payment" />
      </van-cell-group>
    </div>

    <div class="mt-3">
      <van-cell-group inset>
        <van-cell title="关于我们" is-link value="v1.0.0" to="/about" />
        <van-cell title="隐私政策" is-link to="/static/privacy" />
        <van-cell title="用户协议" is-link to="/static/agreement" />
      </van-cell-group>
    </div>

    <div class="mx-4 mt-8">
      <van-button block color="#fff" class="!text-red-500 !border-none shadow-sm" @click="onLogout">
        退出登录
      </van-button>
    </div>
  </div>
</template>