<script setup lang="ts">
import { ref, nextTick } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const msgText = ref('')
const list = ref([
  { id: 1, type: 'kf', content: '您好，我是智能客服，请问有什么可以帮您？' },
])

const onSend = () => {
  if (!msgText.value) return
  // 1. 发送用户消息
  list.value.push({ id: Date.now(), type: 'user', content: msgText.value })
  const text = msgText.value
  msgText.value = ''
  
  // 滚动到底部
  scrollToBottom()
  
  // 2. 模拟客服自动回复
  setTimeout(() => {
    list.value.push({ id: Date.now()+1, type: 'kf', content: `收到您的反馈：“${text}”。人工客服忙线中，稍后会联系您。` })
    scrollToBottom()
  }, 1000)
}

const scrollToBottom = () => {
  nextTick(() => {
    const container = document.getElementById('chat-container')
    if (container) container.scrollTop = container.scrollHeight
  })
}
</script>

<template>
  <div class="bg-gray-100 h-screen flex flex-col">
    <van-nav-bar title="官方客服" left-arrow @click-left="router.back()" fixed placeholder />
    
    <div id="chat-container" class="flex-1 p-4 overflow-y-auto pb-24">
      <div v-for="msg in list" :key="msg.id" class="mb-4 flex" :class="msg.type === 'user' ? 'justify-end' : 'justify-start'">
        <div v-if="msg.type === 'kf'" class="w-10 h-10 rounded-full bg-blue-600 flex items-center justify-center text-white text-xs mr-2 shadow-sm shrink-0">
           <van-icon name="service" size="20" />
        </div>
        
        <div class="max-w-[70%] p-3 rounded-xl text-sm shadow-sm leading-relaxed break-words" 
             :class="msg.type === 'user' ? 'bg-[#FF6B00] text-white rounded-tr-none' : 'bg-white text-black rounded-tl-none'">
          {{ msg.content }}
        </div>
        
        <div v-if="msg.type === 'user'" class="w-10 h-10 rounded-full bg-gray-300 flex items-center justify-center text-white text-xs ml-2 shadow-sm shrink-0">
           <van-icon name="user" size="20" />
        </div>
      </div>
    </div>

    <div class="fixed bottom-0 left-0 right-0 bg-white p-3 flex items-center gap-3 border-t safe-area-inset-bottom z-50 shadow-[0_-2px_10px_rgba(0,0,0,0.05)]">
      <van-icon name="add-o" size="24" class="text-gray-500" />
      <input 
        v-model="msgText" 
        type="text" 
        class="flex-1 bg-gray-100 rounded-full h-10 px-4 text-sm outline-none border-none" 
        placeholder="请输入您的问题..." 
        @keyup.enter="onSend"
      >
      <van-button round type="primary" size="small" color="#FF6B00" class="!px-5" @click="onSend">发送</van-button>
    </div>
  </div>
</template>