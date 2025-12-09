<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
// [修改] 引入 Plus 图标用于上传占位
import { Plus, Delete, Link as LinkIcon } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { UploadProps } from 'element-plus'
import { doGet, doPost } from '../../http/httpRequest'

// 1. 定义接口结构
interface Banner {
  id: number
  name: string
  pic: string    
  url: string    
  sort: number   
  status: number 
  note?: string  
  _status: boolean 
}

const banners = ref<Banner[]>([])
const loading = ref(false)
const total = ref(0) 
const dialogVisible = ref(false)

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 6, 
  type: 1      
})

// 表单数据
const form = reactive({
  id: null,
  name: '',
  url: '',
  pic: '',
  sort: 0,
  status: 1,
  note: ''
})

// ==========================================
// 2. 初始化与加载
// ==========================================
onMounted(() => {
  fetchList()
})

const fetchList = async () => {
  loading.value = true
  try {
    const res: any = await doGet('/admin/home/advertise/list', queryParams)
    
    if (res.code === 200) {
      const pageData = res.data || {}
      const list = pageData.list || []
      
      total.value = pageData.total || 0
      
      banners.value = list.map((item: any) => ({
        ...item,
        _status: item.status === 1
      }))
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleSizeChange = (val: number) => {
  queryParams.pageSize = val
  fetchList()
}

const handleCurrentChange = (val: number) => {
  queryParams.pageNum = val
  fetchList()
}

// ==========================================
// 3. [修改] 图片上传配置 (升级版)
// ==========================================
// 建议使用相对路径或从环境变量获取，这里暂时保留你的 localhost 配置
const baseURL = 'http://localhost:8080' 
const uploadAction = `${baseURL}/admin/common/upload`
const token = localStorage.getItem('cola_admin_token')
const uploadHeaders = { Authorization: token || '' }

// [新增] 上传前校验
const beforeUpload: UploadProps['beforeUpload'] = (rawFile) => {
  if (rawFile.type !== 'image/jpeg' && rawFile.type !== 'image/png') {
    ElMessage.error('图片只能是 JPG/PNG 格式!')
    return false
  }
  if (rawFile.size / 1024 / 1024 > 5) { // 轮播图通常较大，限制放宽到 5MB
    ElMessage.error('图片大小不能超过 5MB!')
    return false
  }
  return true
}

const handleUploadSuccess: UploadProps['onSuccess'] = (res) => {
  if (res.code === 200) {
    // 假设后端直接返回 URL 字符串或 { url: '...' }，请根据实际情况调整
    // 如果 res.data 是字符串直接赋值，如果是对象则取 res.data.url
    form.pic = typeof res.data === 'string' ? res.data : res.data.url
    ElMessage.success('图片上传成功')
  } else {
    ElMessage.error(res.message || '上传失败')
  }
}

// [新增] 删除已上传图片
const handleRemovePic = () => {
  form.pic = ''
}

// ==========================================
// 4. 增删改逻辑
// ==========================================

const openAdd = () => {
  form.id = null
  form.name = ''
  form.url = ''
  form.pic = ''
  form.sort = 0
  form.status = 1
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!form.name) return ElMessage.warning('请输入广告名称')
  if (!form.pic) return ElMessage.warning('请上传广告图片')

  const url = form.id ? `/admin/home/advertise/update/${form.id}` : '/admin/home/advertise/create'
  
  // 如果是修改，需要保持原有 ID
  const payload = {
    ...form, // 包含 id, name, pic, url, sort, status
    type: 1
  }

  const res: any = await doPost(url, payload)

  if (res.code === 200) {
    ElMessage.success(form.id ? '更新成功' : '创建成功')
    dialogVisible.value = false
    fetchList()
  }
}

const handleDelete = (id: number) => {
  ElMessageBox.confirm('确认删除该广告位吗？', '提示', { type: 'warning' })
    .then(async () => {
      await doPost(`/admin/home/advertise/delete/${id}`)
      ElMessage.success('删除成功')
      fetchList()
    })
}

// [修改] 打开编辑弹窗
const openEdit = (row: Banner) => {
  form.id = row.id as any
  form.name = row.name
  form.url = row.url
  form.pic = row.pic
  form.sort = row.sort
  form.status = row.status
  dialogVisible.value = true
}

// 列表中的 Switch 开关更新状态
const handleStatusChange = async (row: Banner) => {
  const statusInt = row._status ? 1 : 0
  await doPost(`/admin/home/advertise/update/${row.id}`, {
    ...row,
    status: statusInt
  })
  ElMessage.success('状态更新成功')
}
</script>

<template>
  <div class="bg-bg-200 p-6 rounded-xl min-h-[calc(100vh-140px)] flex flex-col">
    <div class="flex justify-between items-center mb-6">
      <h2 class="text-2xl font-bold text-white">首页轮播配置</h2>
      <el-button type="primary" :icon="Plus" color="#3D5A80" @click="openAdd">新建广告</el-button>
    </div>

    <div class="flex-1">
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6" v-loading="loading">
        <div v-for="item in banners" :key="item.id" class="bg-bg-300 rounded-lg overflow-hidden border border-bg-300 group hover:border-accent-100 transition-colors">
          
          <div class="relative h-48 bg-gray-800 cursor-pointer" @click="openEdit(item)">
            <img :src="item.pic" class="w-full h-full object-cover" />
            <div class="absolute inset-0 bg-black/50 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity gap-4">
               <el-button type="primary" circle :icon="LinkIcon" @click.stop="openEdit(item)" title="编辑" />
               <el-button type="danger" circle :icon="Delete" @click.stop="handleDelete(item.id)" title="删除" />
            </div>
          </div>
          
          <div class="p-4">
            <div class="flex justify-between items-start mb-2">
              <h3 class="text-white font-bold truncate pr-2">{{ item.name }}</h3>
              <el-switch 
                v-model="item._status" 
                active-color="#3D5A80" 
                inactive-color="#374357" 
                size="small" 
                @change="handleStatusChange(item)"
              />
            </div>
            
            <div class="flex items-center gap-2 text-xs text-text-200 mb-2 truncate">
               <el-icon><LinkIcon /></el-icon> {{ item.url || '无跳转链接' }}
            </div>
            
            <div class="flex items-center gap-2">
               <span class="text-xs text-text-200">排序权重:</span>
               <span class="text-sm font-bold text-accent-100">{{ item.sort }}</span>
            </div>
          </div>
        </div>
      </div>
      
      <div v-if="banners.length === 0 && !loading" class="text-gray-500 text-center py-10">
        暂无轮播图数据
      </div>
    </div>

    <div class="flex justify-end mt-6 pt-4 border-t border-bg-100">
      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :page-sizes="[6, 12, 24]"
        :background="true"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑广告' : '新建广告'" width="500px">
      <el-form label-position="top">
        <el-form-item label="广告名称" required>
          <el-input v-model="form.name" placeholder="例如：春节大促" />
        </el-form-item>
        
        <el-form-item label="跳转链接 (C端路由)">
          <el-input v-model="form.url" placeholder="例如：/product/1001" />
        </el-form-item>
        
        <el-form-item label="广告图片" required>
          <el-upload
            class="banner-uploader w-full"
            :action="uploadAction"
            :headers="uploadHeaders"
            :show-file-list="false"
            :on-success="handleUploadSuccess"
            :before-upload="beforeUpload"
            accept="image/jpeg,image/png"
          >
            <div v-if="form.pic" class="uploaded-box w-full h-48 relative rounded overflow-hidden group">
               <img :src="form.pic" class="w-full h-full object-cover" />
               <div class="absolute inset-0 bg-black/50 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-all cursor-default" @click.stop>
                  <el-icon class="text-white text-2xl cursor-pointer hover:text-red-400" @click.stop="handleRemovePic">
                    <Delete />
                  </el-icon>
               </div>
            </div>
            
            <div v-else class="upload-placeholder w-full h-48 bg-bg-100 flex flex-col items-center justify-center rounded border border-dashed border-gray-600 hover:border-accent-100 transition cursor-pointer">
               <el-icon class="text-3xl text-gray-500 mb-2"><Plus /></el-icon>
               <span class="text-xs text-gray-400">点击上传 (建议 800x400)</span>
            </div>
          </el-upload>
          <div class="text-xs text-gray-400 mt-2">支持 JPG/PNG，大小不超过 5MB</div>
        </el-form-item>
        
        <el-form-item label="排序 (数值越大越靠前)">
           <el-input-number v-model="form.sort" :min="0" controls-position="right" class="!w-full" />
        </el-form-item>
        
        <el-form-item label="状态">
           <el-radio-group v-model="form.status">
              <el-radio :label="1">启用</el-radio>
              <el-radio :label="0">禁用</el-radio>
           </el-radio-group>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" color="#3D5A80" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
/* 深度选择器修改 Upload 样式 */
:deep(.banner-uploader .el-upload) {
  width: 100%;
  display: block; /* 确保占满容器 */
}

/* 输入框深色适配 */
:deep(.el-input__wrapper), :deep(.el-input-number__decrease), :deep(.el-input-number__increase) {
  background-color: #1f2b3e; 
  box-shadow: 0 0 0 1px #374357;
  color: #e0e0e0;
}

/* 分页组件深色适配 */
:deep(.el-pagination__total), :deep(.el-pagination__jump) {
  color: #a0aec0; 
}
</style>