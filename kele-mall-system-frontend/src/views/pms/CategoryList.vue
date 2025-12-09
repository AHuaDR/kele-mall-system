<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { Plus, Edit, Delete, FolderAdd } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { UploadProps } from 'element-plus'
import { doGet, doPost } from '../../http/httpRequest'

// 1. 接口定义 (严格对应后端 PmsCategory)
interface Category {
  id?: number
  parentId: number 
  name: string
  level: number // 0=一级, 1=二级
  productCount: number // 原 goodsCount
  icon: string
  sort: number
  showStatus: boolean // 原 status
  children?: Category[] // 后端返回的嵌套结构
}

// 2. 状态定义
const categoryTree = ref<Category[]>([]) // 这里的树也是一级类目的列表
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)

// 表单数据
const formData = ref<Category>({
  parentId: 0,
  name: '',
  level: 0,
  productCount: 0,
  icon: '',
  sort: 0,
  showStatus: true
})

// ------------------------------------------------------
// 3. 业务逻辑
// ------------------------------------------------------

// 获取列表 (对应 GET /admin/category/list)
const fetchList = async () => {
  loading.value = true
  try {
    const res: any = await doGet('/admin/category/list', { page: 1, size: 1000 })
    
    // [关键修改] 后端直接返回树形结构，无需前端转换
    // res.data 应该直接就是那个 List<PmsCategory> 结构
    categoryTree.value = res.data || []
    
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 图片上传配置
const baseURL = 'http://localhost:8080'
const uploadAction = `${baseURL}/admin/common/upload`
const token = localStorage.getItem('cola_admin_token')
const uploadHeaders = { Authorization: token }

const handleIconSuccess: UploadProps['onSuccess'] = (res) => {
  if (res.code === 200) formData.value.icon = res.data
  else ElMessage.error(res.message)
}

// 打开弹窗
// row: 当前行数据 (编辑用)
// parentId: 指定父ID (新增下级用)
const openDialog = (row?: Category, parentId: number = 0) => {
  dialogVisible.value = true
  
  if (row) {
    // --- 编辑模式 ---
    isEdit.value = true
    formData.value = { ...row } // 回显
  } else {
    // --- 新增模式 ---
    isEdit.value = false
    // 判断层级: 如果 parentId 是 0，说明是 level 0 (一级); 否则是 level 1 (二级)
    const level = parentId === 0 ? 0 : 1
    
    formData.value = {
      parentId: parentId,
      name: '',
      level: level,
      productCount: 0,
      icon: '',
      sort: 0,
      showStatus: true
    }
  }
}

// 提交表单 (POST /create 或 /update)
const handleSubmit = async () => {
  if (!formData.value.name) return ElMessage.warning('请输入类目名称')
  if (!formData.value.icon) return ElMessage.warning('请上传类目图标')

  // 再次确认 level (防止用户在下拉框改了上级，但 level 没变)
  formData.value.level = formData.value.parentId === 0 ? 0 : 1

  try {
    const url = isEdit.value && formData.value.id 
      ? `/admin/category/update/${formData.value.id}` 
      : '/admin/category/create'
      
    await doPost(url, formData.value)
    ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
    dialogVisible.value = false
    fetchList() // 刷新
  } catch (error) { }
}

// 删除
const handleDelete = (row: Category) => {
  // 后端返回的 children 可能为 null，也可能为空数组
  if (row.children && row.children.length > 0) {
    return ElMessage.warning('请先删除该类目下的子类目')
  }
  
  ElMessageBox.confirm(`确认删除 "${row.name}" 吗?`, '警告', { type: 'warning' })
    .then(async () => {
      await doPost(`/admin/category/delete/${row.id}`)
      ElMessage.success('已删除')
      fetchList()
    })
}

// 开关状态切换
const handleStatusChange = async (row: Category) => {
  try {
    // 通常复用 update 接口，或者有专门的 /updateStatus 接口
    await doPost(`/admin/category/update/${row.id}`, row)
    ElMessage.success('状态已更新')
  } catch (error) {
    row.showStatus = !row.showStatus // 失败回滚
  }
}

onMounted(() => {
  fetchList()
})
</script>

<template>
  <div class="bg-bg-200 p-6 rounded-xl min-h-[calc(100vh-140px)]">
    <div class="flex justify-between mb-6">
      <h2 class="text-2xl font-bold text-white">类目管理</h2>
      <el-button type="primary" :icon="Plus" color="#3D5A80" @click="openDialog(undefined, 0)">新建一级类目</el-button>
    </div>

    <el-table 
      :data="categoryTree" 
      v-loading="loading"
      row-key="id"
      :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
    >
      <el-table-column prop="name" label="类目名称" min-width="200" />
      
      <el-table-column label="图标" width="80">
        <template #default="{ row }">
           <img :src="row.icon" class="w-8 h-8 rounded object-cover bg-white" />
        </template>
      </el-table-column>

      <el-table-column label="级别" width="100">
        <template #default="{ row }">
           <el-tag v-if="row.level === 0" effect="dark" type="success">一级</el-tag>
           <el-tag v-else effect="plain" type="info">二级</el-tag>
        </template>
      </el-table-column>
      
      <el-table-column prop="productCount" label="商品数" width="100" />
      <el-table-column prop="sort" label="排序" width="80" />
      
      <el-table-column label="显示状态" width="100">
        <template #default="{ row }">
           <el-switch 
             v-model="row.showStatus" 
             size="small" 
             active-color="#3D5A80" 
             inactive-color="#374357"
             @change="handleStatusChange(row)"
           />
        </template>
      </el-table-column>

      <el-table-column label="操作" width="280">
        <template #default="{ row }">
           <el-button 
             v-if="row.level === 0" 
             link type="primary" 
             :icon="FolderAdd" 
             @click="openDialog(undefined, row.id)"
           >
             新增下级
           </el-button>
           <el-button link type="primary" :icon="Edit" @click="openDialog(row)">编辑</el-button>
           <el-button link type="danger" :icon="Delete" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑类目' : '新建类目'" width="450px">
       <el-form label-position="top">
         
         <el-form-item label="上级分类">
           <el-select v-model="formData.parentId" placeholder="请选择" class="w-full">
             <el-option label="无 (作为一级类目)" :value="0" />
             <el-option 
               v-for="item in categoryTree" 
               :key="item.id" 
               :label="item.name" 
               :value="item.id" 
               :disabled="item.id === formData.id" 
             />
           </el-select>
         </el-form-item>

         <el-form-item label="类目名称" required>
            <el-input v-model="formData.name" placeholder="请输入名称" />
         </el-form-item>

         <el-form-item label="类目图标" required>
            <el-upload
              class="avatar-uploader"
              :action="uploadAction"
              :headers="uploadHeaders"
              :show-file-list="false"
              :on-success="handleIconSuccess"
            >
              <img v-if="formData.icon" :src="formData.icon" class="avatar" />
              <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
            </el-upload>
         </el-form-item>

         <div class="flex gap-4">
            <el-form-item label="排序" class="flex-1">
               <el-input-number v-model="formData.sort" :min="0" class="!w-full" />
            </el-form-item>
            <el-form-item label="是否显示" class="flex-1">
               <el-switch v-model="formData.showStatus" active-text="显示" />
            </el-form-item>
         </div>
       </el-form>
       
       <template #footer>
         <el-button @click="dialogVisible = false">取消</el-button>
         <el-button type="primary" color="#3D5A80" @click="handleSubmit">确定</el-button>
       </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.avatar-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  background-color: #f5f7fa;
}
.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 80px;
  height: 80px;
  text-align: center;
  line-height: 80px;
}
.avatar {
  width: 80px;
  height: 80px;
  display: block;
  object-fit: cover;
}
</style>