<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Plus, Edit, Delete } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { UploadProps } from 'element-plus' // 引入上传类型
import { doGet, doPost } from '../../http/httpRequest'

// 定义接口类型
interface Brand {
  id?: number
  name: string
  logo: string
  sort: number
}

// 状态定义
const brands = ref<Brand[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)

// 表单数据
const form = ref<Brand>({
  name: '',
  logo: '',
  sort: 0
})

// ----------------------------------------------------
// 1. 上传配置 (新增部分)
// ----------------------------------------------------

// [重要] 获取 Token，因为 el-upload 不经过 axios 拦截器，必须手动传 header
const token = localStorage.getItem('cola_admin_token')
const uploadHeaders = {
  Authorization: token // 根据后端要求，可能是 'Bearer ' + token
}

// [重要] 这里填写你后端真实的文件上传接口地址
const uploadAction = 'http://localhost:8080/admin/common/upload' 

// 上传成功回调
const handleLogoSuccess: UploadProps['onSuccess'] = (response, uploadFile) => {
  // 根据你后端返回的结构修改
  // 假设后端返回: { code: 200, data: "http://oss.com/xxx.jpg", message: "success" }
  if (response.code === 200) {
    form.value.logo = response.data 
    ElMessage.success('图片上传成功')
  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

// 上传前校验 (大小/格式)
const beforeLogoUpload: UploadProps['beforeUpload'] = (rawFile) => {
  const isJPGOrPNG = rawFile.type === 'image/jpeg' || rawFile.type === 'image/png'
  const isLt2M = rawFile.size / 1024 / 1024 < 2

  if (!isJPGOrPNG) {
    ElMessage.error('上传头像图片只能是 JPG/PNG 格式!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('上传头像图片大小不能超过 2MB!')
    return false
  }
  return true
}

// ----------------------------------------------------
// 2. 业务逻辑 (保持原有逻辑)
// ----------------------------------------------------

const fetchList = async () => {
  loading.value = true
  try {
    const res: any = await doGet('/admin/brand/list', { page: 1, size: 100 })
    brands.value = res.data?.list || res.data || []
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const openDialog = (row?: Brand) => {
  if (row) {
    isEdit.value = true
    form.value = { ...row }
  } else {
    isEdit.value = false
    form.value = { name: '', logo: '', sort: 0 }
  }
  dialogVisible.value = true
}

const handleSave = async () => {
  if (!form.value.name) return ElMessage.warning('请输入品牌名称')
  if (!form.value.logo) return ElMessage.warning('请上传品牌Logo') // 增加校验

  try {
    if (isEdit.value && form.value.id) {
      await doPost(`/admin/brand/update/${form.value.id}`, form.value)
      ElMessage.success('更新成功')
    } else {
      await doPost('/admin/brand/create', form.value)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    fetchList()
  } catch (error) {
    // error handled by interceptor
  }
}

const handleDelete = (row: Brand) => {
  ElMessageBox.confirm(`确认删除品牌 "${row.name}" 吗?`, '提示', {
    confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
  }).then(async () => {
    try {
      await doPost(`/admin/brand/delete/${row.id}`)
      ElMessage.success('删除成功')
      fetchList()
    } catch (error) {}
  })
}

onMounted(() => {
  fetchList()
})
</script>

<template>
  <div class="bg-bg-200 p-6 rounded-xl min-h-[calc(100vh-140px)]">
    <div class="flex justify-between mb-6">
      <h2 class="text-2xl font-bold text-white">品牌管理</h2>
      <el-button type="primary" :icon="Plus" color="#3D5A80" @click="openDialog()">新增品牌</el-button>
    </div>

    <el-table :data="brands" v-loading="loading">
      <el-table-column label="Logo" width="100">
         <template #default="{ row }">
            <img :src="row.logo" class="w-10 h-10 rounded-full object-cover bg-white" />
         </template>
      </el-table-column>
      <el-table-column prop="name" label="品牌名称" />
      <el-table-column prop="sort" label="排序权重" />
      <el-table-column label="操作">
         <template #default="{ row }">
            <el-button link type="primary" :icon="Edit" @click="openDialog(row)">编辑</el-button>
            <el-button link type="danger" :icon="Delete" @click="handleDelete(row)">删除</el-button>
         </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑品牌' : '新增品牌'" width="400px">
       <el-form label-position="top">
          <el-form-item label="品牌名称" required>
            <el-input v-model="form.name" placeholder="请输入名称" />
          </el-form-item>
          
          <el-form-item label="品牌Logo" required>
            <el-upload
              class="avatar-uploader"
              :action="uploadAction"
              :headers="uploadHeaders"
              :show-file-list="false"
              :on-success="handleLogoSuccess"
              :before-upload="beforeLogoUpload"
            >
              <img v-if="form.logo" :src="form.logo" class="avatar" />
              <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
            </el-upload>
            <div class="text-xs text-gray-400 mt-2">支持 JPG/PNG，小于 2MB</div>
          </el-form-item>

          <el-form-item label="排序">
            <el-input-number v-model="form.sort" :min="0" />
          </el-form-item>
       </el-form>
       <template #footer>
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" color="#3D5A80" @click="handleSave">确定</el-button>
       </template>
    </el-dialog>
  </div>
</template>

<style scoped>
/* 样式：让上传框看起来像个方块 */
.avatar-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  text-align: center;
  /* 使得图标垂直居中 */
  line-height: 100px; 
}

.avatar {
  width: 100px;
  height: 100px;
  display: block;
  object-fit: cover;
}
</style>