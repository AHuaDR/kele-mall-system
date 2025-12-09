<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { showToast } from 'vant'

const router = useRouter()
const route = useRoute()
const type = route.params.type as string

const form = ref({
  oldVal: '',
  newVal: '',
  confirmVal: '',
  code: ''
})

// 根据 type 动态计算页面标题和文案
const config = computed(() => {
  switch(type) {
    case 'password':
      return { title: '修改登录密码', label1: '旧密码', label2: '新密码', placeholder: '请输入' }
    case 'phone':
      return { title: '换绑手机号', label1: '原手机号', label2: '新手机号', placeholder: '请输入' }
    case 'pay_password':
      return { title: '修改支付密码', label1: '旧支付密码', label2: '新支付密码', placeholder: '6位数字' }
    default:
      return { title: '安全设置', label1: '', label2: '', placeholder: '' }
  }
})

const onSubmit = () => {
  if (!form.value.oldVal || !form.value.newVal) return showToast('请填写完整')
  showToast(`${config.value.title}成功`)
  router.back()
}
</script>

<template>
  <div class="bg-pageBg min-h-screen">
    <van-nav-bar :title="config.title" left-arrow @click-left="router.back()" fixed placeholder />
    
    <div class="mt-4">
      <van-cell-group inset>
        <van-field 
          v-model="form.oldVal" 
          :label="config.label1" 
          :placeholder="`请输入${config.label1}`" 
          :type="type.includes('password') ? 'password' : 'text'"
        />
        <van-field 
          v-model="form.newVal" 
          :label="config.label2" 
          :placeholder="`请输入${config.label2}`" 
          :type="type.includes('password') ? 'password' : 'text'"
        />
        <van-field 
          v-model="form.confirmVal" 
          :label="'确认' + config.label2" 
          :placeholder="`请再次输入${config.label2}`" 
          :type="type.includes('password') ? 'password' : 'text'"
        />
        <van-field v-model="form.code" center clearable label="验证码" placeholder="请输入短信验证码">
          <template #button>
            <van-button size="small" type="primary" color="#FF6B00">发送验证码</van-button>
          </template>
        </van-field>
      </van-cell-group>

      <div class="mx-4 mt-8">
        <van-button block color="#FF6B00" round @click="onSubmit">确认提交</van-button>
      </div>
    </div>
  </div>
</template>