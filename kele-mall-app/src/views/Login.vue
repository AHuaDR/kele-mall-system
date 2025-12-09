<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import { showToast, showLoadingToast } from 'vant'
import { doPost } from '../http/httpRequest' // 确保路径正确

const router = useRouter()
const userStore = useUserStore()

const phone = ref('')
const password = ref('')

const onLogin = () => {
  if (!phone.value || !password.value) return showToast('请输入完整信息')
  
  const toast = showLoadingToast({ message: '登录中...', forbidClick: true, duration: 0 })

  // 发送请求：传递 phone 和 password
  doPost('/api/sso/login', {
    phone: phone.value,
    password: password.value
  })
  .then((resp: any) => {
    if (resp.code === 200) {
      // 兼容直接返回 token 字符串或返回对象的情况
      const token = typeof resp.data === 'string' 
          ? resp.data 
          : (resp.data.tokenHead + resp.data.token)

      localStorage.setItem('cola_app_token', token)
      userStore.login(phone.value)
      
      showToast('登录成功')
      router.replace('/')
    } else {
      showToast(resp.message || '登录失败')
    }
  })
  .catch((err: any) => {
    console.error(err)
  })
  .finally(() => {
    toast.close()
  })
}

const onWechatLogin = () => {
  showToast('暂未开放')
}
</script>

<template>
  <div class="pt-24 px-8 bg-white min-h-screen flex flex-col">
    <div class="mb-12">
      <h1 class="text-3xl font-bold text-textMain mb-2">账号登录</h1>
      <p class="text-gray-400 text-sm">ColaMall - 高品质生活电商</p>
    </div>
    
    <div class="space-y-4">
      <div class="bg-gray-50 rounded-xl px-4 py-3">
        <div class="text-xs text-gray-400 mb-1">手机号</div>
        <input v-model="phone" type="tel" class="w-full bg-transparent outline-none text-lg font-medium" placeholder="请输入手机号">
      </div>

      <div class="bg-gray-50 rounded-xl px-4 py-3 flex items-center justify-between">
        <div class="w-full">
          <div class="text-xs text-gray-400 mb-1">密码</div>
          <input 
            v-model="password" 
            type="password" 
            class="w-full bg-transparent outline-none text-lg font-medium" 
            placeholder="请输入登录密码"
            @keyup.enter="onLogin" 
          >
        </div>
      </div>
    </div>

    <van-button block color="#FF6B00" round class="!mt-8 !h-12 !text-lg !font-bold shadow-lg shadow-orange-200" @click="onLogin">
      立即登录
    </van-button>
    
    <div class="mt-4 flex justify-between text-sm text-gray-500 px-2">
       <span>注册账号</span>
       <span>忘记密码?</span>
    </div>
    
    <div class="mt-auto mb-10 text-center">
      <div class="relative mb-6">
        <div class="absolute inset-0 flex items-center"><div class="w-full border-t border-gray-100"></div></div>
        <div class="relative flex justify-center text-xs text-gray-400"><span class="bg-white px-2">第三方登录</span></div>
      </div>
      <div class="flex justify-center gap-8">
        <div class="w-12 h-12 rounded-full bg-[#f0fff0] flex items-center justify-center active:scale-90 transition-transform" @click="onWechatLogin">
          <van-icon name="wechat" size="28" color="#07c160"/>
        </div>
      </div>
    </div>
  </div>
</template>