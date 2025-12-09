import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  // 👇 修改点 1：默认设为 true
  const isLogin = ref(true) 
  
  // 👇 修改点 2：默认填充假数据
  const userInfo = ref({
    nickname: 'Cola体验官',
    avatar: 'https://placehold.co/200x200/FF6B00/ffffff?text=VIP',
    phone: '138****8888'
  })
  
  const login = (phone: string) => {
    isLogin.value = true
    userInfo.value = {
      nickname: `用户${phone.slice(-4)}`,
      avatar: 'https://placehold.co/200x200/FF6B00/ffffff?text=User',
      phone: phone
    }
  }

  const logout = () => {
    isLogin.value = false
    userInfo.value = { nickname: '未登录', avatar: '', phone: '' }
  }

  return { isLogin, userInfo, login, logout }
})