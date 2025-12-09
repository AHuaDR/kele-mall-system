<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showConfirmDialog } from 'vant'

const router = useRouter()

const faceId = ref(false)
const twoFactor = ref(false)

// 返回上一页
const onClickLeft = () => router.back()

const onToggleFaceId = (checked: boolean) => {
  showToast(checked ? '已开启面容ID登录' : '已关闭面容ID登录')
}

const onLogoff = () => {
  showConfirmDialog({
    title: '注销账号风险提示',
    message: '注销后无法恢复，所有数据将清空，确定要继续吗？',
    confirmButtonColor: '#ee0a24'
  }).then(() => {
    showToast('申请已提交，进入7天冷静期')
  })
}
</script>

<template>
  <div class="bg-pageBg min-h-screen">
    <van-nav-bar title="账号安全" left-arrow @click-left="onClickLeft" fixed placeholder />

    <div class="mt-3">
      <van-cell-group inset title="登录安全">
        <van-cell title="修改登录密码" is-link to="/security/mod/password" />
        <van-cell title="换绑手机号" is-link value="138****8888" to="/security/mod/phone" />
      </van-cell-group>

      <van-cell-group inset title="生物识别与验证" class="mt-3">
        <van-cell title="开启面容/指纹登录" center>
          <template #right-icon>
            <van-switch v-model="faceId" size="20" active-color="#FF6B00" @change="onToggleFaceId" />
          </template>
        </van-cell>
        <van-cell title="双重验证 (2FA)" center label="在新设备登录时需要验证码">
          <template #right-icon>
            <van-switch v-model="twoFactor" size="20" active-color="#FF6B00" />
          </template>
        </van-cell>
      </van-cell-group>

      <van-cell-group inset title="账号管理" class="mt-3">
        <van-cell title="设备管理" is-link value="iPhone 15 Pro Max" />
        <van-cell title="注销账号" is-link @click="onLogoff" class="!text-red-500" />
      </van-cell-group>
    </div>
  </div>
</template>