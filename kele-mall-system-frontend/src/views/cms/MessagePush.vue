<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { Bell, Promotion, Plus as PlusIcon, Delete } from '@element-plus/icons-vue'
import { ElMessage, type UploadProps } from 'element-plus'
import dayjs from 'dayjs' 
import { doGet, doPost } from '../../http/httpRequest'

// 接口定义
interface Notice {
  id: number
  title: string
  content: string
  icon: string | null
  type: number        // 1:活动优惠, 2:系统公告, 3:物流通知
  targetType: number  // 1:全体, 2:指定
  targetMemberIds: string | null
  publisherId: number | null
  // sendStatus 移交给后端处理，前端展示时可能有后端返回的状态
  sendStatus?: number 
  sendTime: string | null
  createTime: string 
}

interface TreeOption {
  value: number
  label: string
  children?: TreeOption[]
}

// 状态数据
const history = ref<Notice[]>([])
const loading = ref(false)
const total = ref(0) 
const memberOptions = ref<TreeOption[]>([])
const queryParams = reactive({ pageNum: 1, pageSize: 10 })

// 表单数据
const form = reactive({
  title: '',
  content: '',
  icon: '', 
  type: 1,            
  targetType: 1, 
  targetMemberIds: [] as number[], 
  isScheduled: false,
  sendTime: ''
})

// ==========================================
// 1. 初始化
// ==========================================
onMounted(() => {
  fetchList()    
  fetchMembers() 
})

// 获取会员列表
const fetchMembers = async () => {
  try {
    const res: any = await doGet('/admin/member/memberlist', {})
    if (res.code === 200) {
      const data = res.data || {}
      const transform = (users: any[]) => users.map((u: any) => ({ value: u.id, label: u.nickname || u.username }))
      const treeData = [
        { value: -1, label: '黄金会员', children: transform(data.goldMembers || []) },
        { value: -2, label: '白银会员', children: transform(data.silverMembers || []) },
        { value: -3, label: '普通会员', children: transform(data.normalMembers || []) }
      ]
      memberOptions.value = treeData.filter(group => group.children && group.children.length > 0)
    }
  } catch (error) { console.error(error) }
}

// 获取历史消息列表
const fetchList = async () => {
  loading.value = true
  try {
    const res: any = await doGet('/admin/home/notice/list', queryParams)
    if (res.code === 200) {
      const pageData = res.data || {}
      history.value = Array.isArray(pageData) ? pageData : (pageData.list || [])
      total.value = pageData.total || 0
    }
  } catch (error) { console.error(error) } finally { loading.value = false }
}

const handleSizeChange = (val: number) => { queryParams.pageSize = val; fetchList() }
const handleCurrentChange = (val: number) => { queryParams.pageNum = val; fetchList() }

// ==========================================
// 2. 图片上传配置
// ==========================================
const baseURL = 'http://localhost:8080' 
const uploadAction = `${baseURL}/admin/common/upload`
const token = localStorage.getItem('cola_admin_token')
const uploadHeaders = { Authorization: token || '' }

const beforeUpload: UploadProps['beforeUpload'] = (rawFile) => {
  if (rawFile.type !== 'image/jpeg' && rawFile.type !== 'image/png') {
    ElMessage.error('图标只能是 JPG/PNG 格式!')
    return false
  }
  if (rawFile.size / 1024 / 1024 > 2) {
    ElMessage.error('图标大小不能超过 2MB!')
    return false
  }
  return true
}

const handleUploadSuccess: UploadProps['onSuccess'] = (res) => {
  if (res.code === 200) {
    form.icon = typeof res.data === 'string' ? res.data : res.data.url
    ElMessage.success('图标上传成功')
  } else {
    ElMessage.error(res.message || '上传失败')
  }
}

const handleRemovePic = () => { form.icon = '' }

// ==========================================
// 3. [修改] 提交逻辑 (精简版)
// ==========================================
const onSubmit = async () => {
  // 校验
  if(!form.title) return ElMessage.warning('请输入消息标题')
  if(!form.content) return ElMessage.warning('请输入消息内容')
  if(form.targetType === 2 && form.targetMemberIds.length === 0) return ElMessage.warning('请选择至少一个指定用户')

  let finalSendTime = form.sendTime
  // 如果勾选了定时但没填时间
  if (form.isScheduled && !finalSendTime) return ElMessage.warning('请选择发送时间')
  
  // 如果没勾选定时，或者时间为空，则由后端决定是否设为当前时间
  // 这里我们也可以显式传当前时间，或者传 null 让后端处理
  if (!form.isScheduled) finalSendTime = dayjs().format('YYYY-MM-DD HH:mm:ss')

  // 过滤掉分组ID (负数)
  const realUserIds = form.targetMemberIds.filter(id => id > 0)
  if (form.targetType === 2 && realUserIds.length === 0) return ElMessage.warning('所选分组中没有有效用户')

  // 发送请求 (移除了 sendStatus)
  const res: any = await doPost('/admin/home/notice/create', {
    title: form.title, 
    content: form.content, 
    icon: form.icon, 
    type: form.type, 
    targetType: form.targetType,
    targetMemberIds: form.targetType === 1 ? '' : realUserIds.join(','), 
    sendTime: finalSendTime // 只传时间，状态由后端定
  })

  if (res.code === 200) {
    ElMessage.success('消息推送已提交')
    resetForm()
    queryParams.pageNum = 1
    fetchList()
  } else { 
    ElMessage.error(res.message || '操作失败') 
  }
}

const resetForm = () => {
  Object.assign(form, { title: '', content: '', icon: '', type: 1, targetType: 1, targetMemberIds: [], isScheduled: false, sendTime: '' })
}

// 辅助函数
const getStatusLabel = (status: number) => ['待发送','已发送','已撤回'][status] || '未知' // 稍微改了下文案
const getStatusClass = (status: number) => ['text-yellow-500','text-green-500','text-red-500'][status] || 'text-gray-400'
const getTypeLabel = (type: number) => ({1:'活动优惠',2:'系统公告',3:'物流通知'}[type] || '未知类型')
const getTypeTag = (type: number) => ({1:'danger',2:'warning',3:'success'}[type] || 'info')
</script>

<template>
  <div class="flex gap-6 h-[calc(100vh-140px)]">
    <div class="w-1/3 bg-bg-200 p-6 rounded-xl overflow-y-auto">
       <h2 class="text-xl font-bold text-white mb-6 flex items-center gap-2">
         <el-icon><Promotion /></el-icon> 新建推送
       </h2>
       
       <el-form label-position="top">
         <el-form-item label="消息标题" required>
            <el-input v-model="form.title" placeholder="请输入标题" />
         </el-form-item>
         
         <el-form-item label="消息封面/图标">
            <el-upload
              class="banner-uploader w-full"
              :action="uploadAction"
              :headers="uploadHeaders"
              :show-file-list="false"
              :on-success="handleUploadSuccess"
              :before-upload="beforeUpload"
              accept="image/jpeg,image/png"
            >
              <div v-if="form.icon" class="uploaded-box w-full h-32 relative rounded overflow-hidden group bg-black/20">
                 <img :src="form.icon" class="w-full h-full object-contain" />
                 <div class="absolute inset-0 bg-black/50 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-all cursor-default" @click.stop>
                    <el-icon class="text-white text-2xl cursor-pointer hover:text-red-400" @click.stop="handleRemovePic">
                       <Delete />
                    </el-icon>
                 </div>
              </div>
              <div v-else class="upload-placeholder w-full h-32 bg-bg-100 flex flex-col items-center justify-center rounded border border-dashed border-gray-600 hover:border-accent-100 transition cursor-pointer">
                 <el-icon class="text-3xl text-gray-500 mb-2"><PlusIcon /></el-icon>
                 <span class="text-xs text-gray-400">上传图标 (可选)</span>
              </div>
            </el-upload>
         </el-form-item>

         <el-form-item label="消息类型">
            <el-radio-group v-model="form.type">
               <el-radio-button :label="1">活动优惠</el-radio-button>
               <el-radio-button :label="2">系统公告</el-radio-button>
               <el-radio-button :label="3">物流通知</el-radio-button>
            </el-radio-group>
         </el-form-item>
         <el-form-item label="发送对象">
            <el-radio-group v-model="form.targetType">
               <el-radio :label="1" class="!text-white mr-4">全体用户</el-radio>
               <el-radio :label="2" class="!text-white">指定用户</el-radio>
            </el-radio-group>
         </el-form-item>

         <div v-if="form.targetType === 2" class="mb-4">
            <el-tree-select
              v-model="form.targetMemberIds"
              :data="memberOptions"
              multiple
              show-checkbox
              :check-strictly="false"
              collapse-tags
              collapse-tags-tooltip
              placeholder="请从列表中选择用户"
              no-data-text="暂无会员数据"
              class="w-full"
              popper-class="dark-tree-popper"
            />
            <div class="text-xs text-gray-400 mt-1"> * 支持按分组全选 </div>
         </div>

         <el-form-item label="消息内容" required>
            <el-input v-model="form.content" type="textarea" :rows="5" placeholder="推送的具体内容..." />
         </el-form-item>
         <el-form-item label="发送时间">
            <div class="flex items-center gap-4 w-full">
              <el-switch v-model="form.isScheduled" active-text="定时" inactive-text="立即" inline-prompt style="--el-switch-on-color: #3D5A80; --el-switch-off-color: #13ce66" />
              <el-date-picker v-if="form.isScheduled" v-model="form.sendTime" type="datetime" placeholder="选择具体时间" value-format="YYYY-MM-DD HH:mm:ss" class="flex-1" />
            </div>
         </el-form-item>
         
         <div class="mt-6">
            <el-button type="primary" color="#3D5A80" class="w-full h-10 text-lg" @click="onSubmit">
               <el-icon class="mr-2"><Promotion /></el-icon> 确认推送
            </el-button>
         </div>
       </el-form>
    </div>

    <div class="w-2/3 bg-bg-200 p-6 rounded-xl flex flex-col" v-loading="loading">
       <div class="flex-none"><h2 class="text-xl font-bold text-white mb-6 flex items-center gap-2"><el-icon><Bell /></el-icon> 推送历史</h2></div>
       <div class="flex-1 overflow-y-auto pr-2">
         <el-timeline v-if="history.length > 0">
           <el-timeline-item v-for="item in history" :key="item.id" :timestamp="item.createTime" placement="top" color="#3D5A80">
              <el-card class="!bg-bg-300 !border-none text-text-100">
                 <div class="flex gap-4">
                    <div v-if="item.icon" class="w-16 h-16 rounded overflow-hidden flex-shrink-0 bg-black/20">
                       <img :src="item.icon" class="w-full h-full object-cover" />
                    </div>
                    <div class="flex-1">
                       <div class="flex justify-between items-start mb-2">
                          <div class="flex items-center gap-2"><h4 class="font-bold text-lg">{{ item.title }}</h4><span v-if="item.sendStatus === 2" class="text-xs text-white bg-red-500 px-1 rounded">已撤回</span></div>
                          <div class="flex gap-2">
                            <el-tag size="small" effect="dark" color="#2c3e50" class="border-none">{{ item.targetType === 1 ? '全体用户' : '指定用户' }}</el-tag>
                            <el-tag size="small" :type="getTypeTag(item.type)">{{ getTypeLabel(item.type) }}</el-tag>
                          </div>
                       </div>
                       <p class="text-text-200 whitespace-pre-wrap mb-3">{{ item.content }}</p>
                       <div class="flex justify-between items-center text-xs text-gray-500 border-t border-gray-700 pt-2">
                          <span>计划时间: {{ item.sendTime || '立即' }}</span>
                          <div class="flex items-center gap-1 font-bold" :class="getStatusClass(item.sendStatus || 0)">
                             <div class="w-2 h-2 rounded-full bg-current"></div>{{ getStatusLabel(item.sendStatus || 0) }}
                          </div>
                       </div>
                    </div>
                 </div>
              </el-card>
           </el-timeline-item>
         </el-timeline>
         <div v-else class="flex flex-col items-center justify-center h-full text-gray-500"><el-icon size="40" class="mb-2"><Bell /></el-icon><p>暂无推送记录</p></div>
       </div>
       <div class="flex-none flex justify-end mt-4 pt-4 border-t border-bg-100">
          <el-pagination v-model:current-page="queryParams.pageNum" v-model:page-size="queryParams.pageSize" :page-sizes="[5, 10, 20]" :background="true" layout="total, sizes, prev, pager, next, jumper" :total="total" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
       </div>
    </div>
  </div>
</template>

<style scoped>
/* 保持原有样式不变 */
:deep(.el-input__wrapper), :deep(.el-textarea__inner) { background-color: #1f2b3e; box-shadow: 0 0 0 1px #374357; color: #e0e0e0; }
:deep(.el-radio-button__inner) { background-color: #1f2b3e; border-color: #374357; color: #e0e0e0; }
:deep(.el-radio-button:first-child .el-radio-button__inner) { border-left: 1px solid #374357; }
:deep(.el-radio__label) { color: #e0e0e0 !important; }
:deep(.el-pagination__total), :deep(.el-pagination__jump) { color: #a0aec0; }
:deep(.el-tree-select) { --el-fill-color-blank: #1f2b3e; --el-border-color: #374357; --el-text-color-regular: #e0e0e0; }
:deep(.el-select__tags .el-tag) { background-color: #374357; color: #e0e0e0; }
:deep(.banner-uploader .el-upload) { width: 100%; display: block; }
</style>

<style>
/* 保持原有全局样式不变 */
.el-popper.dark-tree-popper { background: #1f2b3e !important; border: 1px solid #374357 !important; }
.dark-tree-popper .el-tree { background: transparent !important; color: #e0e0e0 !important; --el-tree-node-hover-bg-color: #2d3d52 !important; --el-fill-color-light: #2d3d52 !important; }
.dark-tree-popper .el-tree-node__content:hover, .dark-tree-popper .el-tree-node.is-current > .el-tree-node__content, .dark-tree-popper .el-tree-node:focus > .el-tree-node__content { background-color: #2d3d52 !important; }
.dark-tree-popper .el-tree-node__label { color: #e0e0e0 !important; }
.dark-tree-popper .el-tree-node__expand-icon { color: #a0aec0 !important; }
.dark-tree-popper .el-checkbox__inner { background-color: transparent !important; border-color: #a0aec0 !important; }
.dark-tree-popper .el-checkbox__input.is-checked .el-checkbox__inner { background-color: #409eff !important; border-color: #409eff !important; }
</style>