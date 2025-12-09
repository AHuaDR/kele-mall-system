<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, User, Iphone, Message, Lock } from '@element-plus/icons-vue'
import type { UploadProps } from 'element-plus'
import { doGet, doPost } from '../../http/httpRequest'

// ==========================================
// 1. 数据定义
// ==========================================

// B端管理员实体接口
interface AdminInfo {
  id: number | null
  username: string // 登录账号
  nickName: string // [注意] 后端通常叫 nickName (小驼峰)
  email: string
  icon: string     // 头像
  note: string     // 备注/简介
  createTime: string
  loginTime: string
  status: number
}

const form = ref<AdminInfo>({
  id: null,
  username: '',
  nickName: '', // 初始化字段名需对应
  email: '',
  icon: '',
  note: '',
  createTime: '',
  loginTime: '',
  status: 1
})

const pwdForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// ==========================================
// 2. 初始化与加载
// ==========================================
onMounted(() => {
  loadAdminInfo()
})

// 获取当前登录管理员信息
const loadAdminInfo = () => {
  // API: GET /admin/info (获取当前登录用户信息)
  doGet('/admin/info').then((res: any) => {
    if (res.code === 200 && res.data) {
      // 假设后端返回结构: { username: "admin", icon: "...", menus: [...] }
      // 如果 /admin/info 返回字段不全，可能需要根据 username 再查一次详情
      // 这里假设 /admin/info 返回了必要的基础信息
      Object.assign(form.value, res.data)
      
      // 容错处理：如果后端返回的是下划线格式，手动映射一下
      if (!form.value.nickName && res.data.nickname) {
        form.value.nickName = res.data.nickname
      }
    }
  })
}

// ==========================================
// 3. 上传头像
// ==========================================
const baseURL = 'http://localhost:8080' 
const uploadAction = `${baseURL}/admin/common/upload`
const token = localStorage.getItem('cola_admin_token')
const uploadHeaders = { Authorization: token || '' }

const handleAvatarSuccess: UploadProps['onSuccess'] = (res) => {
  if (res.code === 200) {
    form.value.icon = res.data
    ElMessage.success('头像上传成功，请点击保存')
  } else {
    ElMessage.error(res.message || '上传失败')
  }
}

const beforeAvatarUpload: UploadProps['beforeUpload'] = (rawFile) => {
  if (rawFile.type !== 'image/jpeg' && rawFile.type !== 'image/png') {
    ElMessage.error('头像必须是 JPG/PNG 格式!')
    return false
  }
  return true
}

// ==========================================
// 4. 提交修改
// ==========================================

// 更新基本信息
const updateInfo = () => {
  if (!form.value.id) return ElMessage.error('用户信息未加载')

  // API: POST /admin/update/{id}
  const url = `/admin/update/${form.value.id}`
  
  // 构造 DTO，只传需要修改的字段
  const payload = {
    nickName: form.value.nickName,
    email: form.value.email,
    icon: form.value.icon,
    note: form.value.note,
    // 如果后端要求传完整对象，也可以直接传 form.value
    status: form.value.status 
  }

  doPost(url, payload).then((res: any) => {
    if (res.code === 200) {
      ElMessage.success('个人信息已更新')
      // 刷新数据（可选，防止本地数据与服务器不一致）
      loadAdminInfo() 
    } else {
      ElMessage.error(res.message || '更新失败')
    }
  })
}

// 修改密码
const updatePwd = () => {
  if (!pwdForm.oldPassword) return ElMessage.warning('请输入旧密码')
  if (!pwdForm.newPassword) return ElMessage.warning('请输入新密码')
  if (pwdForm.newPassword !== pwdForm.confirmPassword) return ElMessage.error('两次新密码输入不一致')

  // API: POST /admin/admin/updatePassword
  // 按照规范，通常需要传用户名来定位是改谁的密码
  const payload = {
    username: form.value.username,
    oldPassword: pwdForm.oldPassword,
    newPassword: pwdForm.newPassword
  }

  doPost('/admin/updatePassword', payload).then((res: any) => {
    if (res.code === 200) {
      ElMessage.success('密码修改成功，请重新登录')
      // 登出逻辑
      localStorage.removeItem('cola_admin_token')
      setTimeout(() => location.reload(), 1000)
    } else {
      ElMessage.error(res.message || '修改失败')
    }
  })
}
</script>

<template>
  <div class="flex flex-col md:flex-row gap-6">
    <div class="w-full md:w-1/3">
      <div class="bg-bg-200 p-8 rounded-xl flex flex-col items-center text-center border border-bg-300">
         <el-upload
            class="avatar-uploader mb-4"
            :action="uploadAction"
            :headers="uploadHeaders"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
            :before-upload="beforeAvatarUpload"
          >
            <div v-if="form.icon" class="w-24 h-24 rounded-full overflow-hidden border-2 border-accent-100">
               <img :src="form.icon" class="w-full h-full object-cover" />
            </div>
            <div v-else class="w-24 h-24 rounded-full bg-bg-300 flex items-center justify-center border-2 border-dashed border-text-200 hover:border-accent-100 transition-colors">
               <el-icon :size="24" class="text-text-200"><Plus /></el-icon>
            </div>
         </el-upload>
         
         <h2 class="text-xl font-bold text-white mb-1">{{ form.nickName || form.username }}</h2>
         <p class="text-text-200 text-sm mb-6">{{ form.note || '超级管理员' }}</p>
         
         <div class="w-full flex justify-between text-sm px-4 py-2 bg-bg-300 rounded mb-2">
             <span class="text-text-200">创建时间</span>
             <span class="text-white">{{ form.createTime ? form.createTime.substring(0, 10) : '-' }}</span>
         </div>
         <div class="w-full flex justify-between text-sm px-4 py-2 bg-bg-300 rounded">
             <span class="text-text-200">最近登录</span>
             <span class="text-white">{{ form.loginTime ? form.loginTime.substring(0, 10) : '刚刚' }}</span>
         </div>
      </div>
    </div>

    <div class="w-full md:w-2/3">
       <div class="bg-bg-200 p-6 rounded-xl border border-bg-300 min-h-[500px]">
         <el-tabs>
             <el-tab-pane label="基本资料">
                <el-form label-position="top" class="mt-4 max-w-lg">
                   <el-form-item label="登录账号">
                      <el-input v-model="form.username" disabled :prefix-icon="User" />
                      <div class="text-xs text-gray-500 mt-1">账号不可修改</div>
                   </el-form-item>
                   <el-form-item label="管理员昵称">
                      <el-input v-model="form.nickName" :prefix-icon="User" placeholder="请输入昵称" />
                   </el-form-item>
                   <el-form-item label="联系邮箱">
                      <el-input v-model="form.email" :prefix-icon="Message" placeholder="请输入邮箱" />
                   </el-form-item>
                   <el-form-item label="备注信息">
                      <el-input v-model="form.note" type="textarea" rows="4" placeholder="请输入备注或简介" />
                   </el-form-item>
                   <el-button type="primary" color="#3D5A80" @click="updateInfo">保存修改</el-button>
                </el-form>
             </el-tab-pane>

             <el-tab-pane label="安全设置">
                <el-form label-position="top" class="mt-4 max-w-lg">
                   <el-form-item label="旧密码">
                      <el-input v-model="pwdForm.oldPassword" type="password" show-password :prefix-icon="Lock" />
                   </el-form-item>
                   <el-form-item label="新密码">
                      <el-input v-model="pwdForm.newPassword" type="password" show-password :prefix-icon="Lock" />
                   </el-form-item>
                   <el-form-item label="确认新密码">
                      <el-input v-model="pwdForm.confirmPassword" type="password" show-password :prefix-icon="Lock" />
                   </el-form-item>
                   <el-button type="danger" @click="updatePwd">修改密码</el-button>
                </el-form>
             </el-tab-pane>
         </el-tabs>
       </div>
    </div>
  </div>
</template>

<style scoped>
:deep(.el-upload) {
  cursor: pointer;
}
/* 修复输入框在暗黑模式下的显示 */
:deep(.el-input__wrapper), :deep(.el-textarea__inner) {
  background-color: #1f2b3e; 
  box-shadow: 0 0 0 1px #374357;
  color: #e0e0e0;
}
:deep(.el-input.is-disabled .el-input__wrapper) {
   background-color: #1a202c;
   box-shadow: 0 0 0 1px #2d3748;
}
</style>