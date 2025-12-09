<script setup lang="ts">
import { ref, onMounted, nextTick, onUnmounted } from 'vue'
import { Promotion, Refresh } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { doGet, doPost } from '../../http/httpRequest'

// ==========================================
// 1. 接口定义
// ==========================================
interface ChatSession {
  sessionId: number
  memberId: number
  memberUsername: string
  memberIcon: string
  lastContent: string
  createTime: string
  unreadCount: number
  user: string
  avatar: string
  lastMsg: string
  time: string
  unread: number
}

interface ChatMessage {
  id: number
  sessionId: number
  content: string
  senderType: boolean
  createTime: string
  isRead: boolean
  type: 'user' | 'admin'
}

// ==========================================
// 2. 状态数据
// ==========================================
const sessions = ref<ChatSession[]>([])
const messages = ref<ChatMessage[]>([])
const activeSessionId = ref<number | null>(null)
const replyText = ref('')
const messageContainer = ref<HTMLElement | null>(null)
const timer = ref<any>(null)
const isLoading = ref(false)
const isActiveWindow = ref(true) // 页面是否聚焦

// ==========================================
// 3. 生命周期与初始化
// ==========================================
onMounted(() => {
  loadSessionList()
  startPolling()
  window.addEventListener('focus', handleWindowFocus)
  window.addEventListener('blur', handleWindowBlur)
})

onUnmounted(() => {
  stopPolling()
  window.removeEventListener('focus', handleWindowFocus)
  window.removeEventListener('blur', handleWindowBlur)
})

// ==========================================
// 4. 窗口焦点处理
// ==========================================
const handleWindowFocus = () => {
  isActiveWindow.value = true
  if (activeSessionId.value) {
    markSessionAsRead(activeSessionId.value)
  }
}

const handleWindowBlur = () => {
  isActiveWindow.value = false
}

// ==========================================
// 5. 轮询机制（每3秒拉取一次）
// ==========================================
const startPolling = () => {
  stopPolling()
  timer.value = setInterval(async () => {
    await loadSessionList(true) // 静默更新列表
    if (activeSessionId.value) {
      await loadHistory(activeSessionId.value, true) // 静默加载历史
    }
  }, 3000)
}

const stopPolling = () => {
  if (timer.value) clearInterval(timer.value)
}

// ==========================================
// 6. 核心业务方法
// ==========================================

// 加载会话列表
const loadSessionList = async (isSilent = false) => {
  try {
    const res: any = await doGet('/admin/chat/session/list')
    if (res.code === 200) {
      const list = Array.isArray(res.data) ? res.data : []

      // 安全映射会话列表
      sessions.value = list
        .map((item: any) => {
          // 跳过无效数据项
          if (!item || typeof item.id !== 'number') return null

          return {
            ...item,
            sessionId: item.id,
            user: item.memberNickName || item.memberNick || '匿名用户',
            avatar: item.memberAvatar || '',
            lastMsg: item.lastContent || '',
            time: formatTime(item.createTime),
            unread: item.unreadCount || 0
          }
        })
        // 确保类型安全：过滤掉 null 并保留 ChatSession 类型
        .filter((session: any): session is ChatSession => session !== null)

      // 安全选择第一个有效会话
      if (!isSilent && !activeSessionId.value && sessions.value.length > 0) {
        const firstValidSession = sessions.value.find(s => s.sessionId !== undefined)
        if (firstValidSession) {
          await selectSession(firstValidSession.sessionId)
        }
      }
    }
  } catch (error) {
    console.error('加载会话列表失败:', error)
  }
}

// 加载聊天记录
const loadHistory = async (sessionId: number, isSilent = false) => {
  try {
    const res: any = await doGet('/admin/chat/history', { sessionId })
    if (res.code === 200) {
      const list = res.data || []

      messages.value = list.map((item: any) => ({
        ...item,
        isRead: Boolean(item.isRead),
        type: item.senderType ? 'user' : 'admin'
      }))

      // 每次加载都滚动到底
      scrollToBottom()

      // 🔥 关键逻辑：只有在当前页面激活 && 当前会话打开时，才自动标记为已读
      if (isActiveWindow.value && activeSessionId.value === sessionId) {
        const hasUnreadFromUser = messages.value.some(
          m => m.type === 'user' && !m.isRead
        )
        if (hasUnreadFromUser) {
          await markSessionAsRead(sessionId)
        }
      }
    }
  } catch (error) {
    console.error('加载聊天记录失败:', error)
  }
}

// 标记会话为已读（调用接口 + 更新本地状态）
const markSessionAsRead = async (sessionId: number) => {
  try {
    const res: any = await doPost('/admin/chat/session/markRead', { sessionId })
    if (res.code === 200) {
      // 更新会话列表中的未读数
      const session = sessions.value.find(s => s.sessionId === sessionId)
      if (session) {
        session.unread = 0
        sessions.value = [...sessions.value] // 触发响应式更新
      }

      // 本地消息中标记所有用户消息为已读
      messages.value = messages.value.map(msg => {
        if (msg.type === 'user') {
          return { ...msg, isRead: true }
        }
        return msg
      })
    }
  } catch (error) {
    console.error('标记已读失败', error)
  }
}

// 选择会话
const selectSession = async (id: number) => {
  activeSessionId.value = id
  await loadHistory(id) // 加载历史
  await markSessionAsRead(id) // 打开即标记为已读
}

// 发送回复
const handleSend = async () => {
  if (!replyText.value.trim()) {
    ElMessage.warning('请输入消息内容')
    return
  }
  if (!activeSessionId.value) {
    ElMessage.warning('请先选择一个会话')
    return
  }

  isLoading.value = true
  try {
    const res: any = await doPost('/admin/chat/reply', {
      sessionId: activeSessionId.value,
      content: replyText.value,
      senderType: false
    })

    if (res.code === 200) {
      replyText.value = ''
      await loadHistory(activeSessionId.value)
      await loadSessionList(true)
      scrollToBottom()
      await markSessionAsRead(activeSessionId.value) // 保险起见再标记一次
    } else {
      ElMessage.error(res.message || '发送失败')
    }
  } catch (error) {
    console.error('发送消息失败:', error)
    ElMessage.error('发送失败，请检查网络连接')
  } finally {
    isLoading.value = false
  }
}

// 滚动到底部
const scrollToBottom = () => {
  nextTick(() => {
    if (messageContainer.value) {
      messageContainer.value.scrollTop = messageContainer.value.scrollHeight
    }
  })
}

// 时间格式化
const formatTime = (timeStr: string) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  const now = new Date()
  const isToday = date.toDateString() === now.toDateString()

  if (isToday) {
    return timeStr.substring(11, 16)
  }
  return timeStr.substring(5, 10)
}

// 获取当前会话
const getCurrentSession = () => {
  return sessions.value.find(s => s.sessionId === activeSessionId.value)
}
</script>

<template>
  <div class="chat-admin-container">
    <div class="bg-bg-200 rounded-xl h-[calc(100vh-140px)] flex overflow-hidden border border-bg-300">
      
      <!-- 左侧会话列表 -->
      <div class="w-64 border-r border-bg-300 flex flex-col bg-bg-300">
        <div class="p-4 border-b border-bg-200 text-white font-bold flex justify-between items-center">
          <span>最近会话 ({{ sessions.length }})</span>
        </div>
        
        <div class="flex-1 overflow-y-auto">
          <div 
            v-for="s in sessions" 
            :key="s.sessionId"
            class="p-4 flex gap-3 cursor-pointer hover:bg-bg-200 transition-colors relative"
            :class="activeSessionId === s.sessionId ? 'bg-bg-200' : ''"
            @click="selectSession(s.sessionId)"
          >
            <el-avatar :size="40" class="bg-primary-100 shrink-0" :src="s.avatar">
              {{ s.user?.substring(0,1) }}
            </el-avatar>
            
            <div class="overflow-hidden flex-1">
              <div class="flex justify-between items-center mb-1">
                <span class="text-text-100 font-bold text-sm truncate">{{ s.user }}</span>
                <span class="text-text-200 text-xs">{{ s.time }}</span>
              </div>
              <div class="text-text-200 text-xs truncate">{{ s.lastMsg }}</div>
            </div>
            
            <!-- 右上角未读数 -->
            <div 
              v-if="s.unread > 0 && activeSessionId !== s.sessionId" 
              class="absolute top-2 right-2 min-w-[18px] h-[18px] px-1 bg-red-500 rounded-full text-[10px] text-white flex items-center justify-center"
            >
              {{ s.unread > 99 ? '99+' : s.unread }}
            </div>
          </div>
          
          <div v-if="sessions.length === 0" class="p-4 text-text-200 text-center">
            暂无会话记录
          </div>
        </div>
      </div>

      <!-- 右侧聊天区域 -->
      <div class="flex-1 flex flex-col bg-bg-200">
        <!-- 聊天头部 -->
        <div class="h-14 border-b border-bg-300 flex items-center justify-between px-6">
          <span class="text-white font-bold">
            {{ activeSessionId ? `正在与 ${getCurrentSession()?.user || '用户'} 对话` : '请选择会话' }}
          </span>
          
          <!-- ❌ “清空记录”按钮已被删除 -->
          <div></div>
        </div>
        
        <!-- 消息列表 -->
        <div ref="messageContainer" class="flex-1 overflow-y-auto p-6 space-y-4">
          <div 
            v-for="msg in messages" 
            :key="msg.id" 
            class="flex flex-col" 
            :class="msg.type === 'admin' ? 'items-end' : 'items-start'"
          >
            <div class="max-w-[70%] flex flex-col" :class="msg.type === 'admin' ? 'items-end' : 'items-start'">
              <div 
                class="p-3 rounded-lg text-sm break-all message-bubble"
                :class="msg.type === 'admin' 
                  ? 'bg-primary-100 text-white rounded-tr-none' 
                  : 'bg-bg-300 text-text-100 rounded-tl-none'"
              >
                {{ msg.content }}
              </div>
              
              <div class="text-xs text-text-200 mt-1 flex gap-2">
                <span>{{ formatTime(msg.createTime) }}</span>
                <span :class="msg.isRead ? 'text-green-400' : 'text-red-400'">
                  {{ msg.type === 'user' ? (msg.isRead ? '已读' : '未读') : '' }}
                </span>
              </div>
            </div>
          </div>
          
          <div v-if="messages.length === 0 && activeSessionId" class="text-center text-text-200 py-8">
            暂无聊天记录
          </div>
        </div>

        <!-- 输入区域 -->
        <div class="h-40 border-t border-bg-300 bg-bg-300 p-4 flex flex-col">
          <textarea 
            v-model="replyText" 
            class="flex-1 bg-transparent resize-none outline-none text-text-100 placeholder-text-200"
            placeholder="输入回复内容，Enter 发送..."
            :disabled="!activeSessionId || isLoading"
            @keydown.enter.prevent="handleSend"
          ></textarea>
          
          <div class="flex justify-between items-center mt-2">
            <div class="text-text-200 text-xs">
              {{ activeSessionId ? '按 Enter 发送消息' : '请先选择会话' }}
            </div>
            
            <div class="flex gap-2">
              <el-button 
                type="primary" 
                :icon="Promotion" 
                @click="handleSend"
                :loading="isLoading"
                :disabled="!activeSessionId || !replyText.trim()"
              >
                发送
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.chat-admin-container {
  height: 100%;
  background: #1a202c;
}

.refresh-btn {
  background: #3D5A80;
  border: none;
}

.refresh-btn:hover {
  background: #4c6b94;
}

.message-bubble {
  max-width: 100%;
  word-wrap: break-word;
}

/* 滚动条样式 */
.flex-1::-webkit-scrollbar {
  width: 6px;
}

.flex-1::-webkit-scrollbar-track {
  background: #2d3748;
}

.flex-1::-webkit-scrollbar-thumb {
  background: #4a5568;
  border-radius: 3px;
}

.flex-1::-webkit-scrollbar-thumb:hover {
  background: #718096;
}
</style>