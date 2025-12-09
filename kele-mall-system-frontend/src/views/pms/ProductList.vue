<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { Plus, Edit, Delete, Search } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import { doGet, doPost } from '../../http/httpRequest'

const router = useRouter()

// 表格数据与分页
const tableData = ref([])
const loading = ref(false)
const total = ref(0)
const query = reactive({
  page: 1,
  size: 10,
  keyword: '',
  // categoryId: null, // 如果有分类筛选可加
  // status: null      // 如果有状态筛选可加
})

// 1. 获取商品列表
const loadData = () => {
  loading.value = true
  doGet('/admin/product/list', query)
    .then((res: any) => {
      if (res.code === 200) {
        let list = res.data.list || res.data.records || []
        
        // 【核心修改】遍历列表，转换状态字段类型：布尔(Boolean) -> 整数(Number)
        list.forEach((item: any) => {
          // 如果后端返回的是布尔值，将其转换为 1 或 0
          item.publishStatus = item.publishStatus ? 1 : 0;
        });
        
        tableData.value = list
        total.value = res.data.total || 0
      }
    })
    .finally(() => {
      loading.value = false
    })
}

// 2. 上下架状态切换
const handleStatus = (row: any) => {
  const newStatus = row.publishStatus === 1 ? 0 : 1 // 注意：数据库字段是 publishStatus 还是 status？根据API文档调整，这里假设是 publishStatus
  // API: POST /admin/product/updateStatus/{id}?status=0
  // 这里直接拼在URL里最稳妥，或者看后端是否接收 Body
  doPost(`/admin/product/updateStatus/${row.id}?status=${newStatus}`)
    .then((res: any) => {
      if (res.code === 200) {
        row.publishStatus = newStatus ? 1 : 0;
        ElMessage.success(newStatus ? '已上架' : '已下架')
      }
    })
}

// 3. 删除商品
const handleDelete = (row: any) => {
  ElMessageBox.confirm('确认删除该商品吗？删除后无法恢复。', '警告', { 
    confirmButtonText: '删除', 
    cancelButtonText: '取消', 
    type: 'warning'
  }).then(() => {
    doPost(`/admin/product/delete/${row.id}`)
      .then((res: any) => {
        if (res.code === 200) {
          ElMessage.success('已删除')
          // 删除后重新加载当前页，如果当前页只有一条且被删了，可以考虑 query.page--
          loadData()
        }
      })
  })
}

// --- 快速编辑逻辑 ---
const editDialog = ref(false)
const editForm: any = reactive({ id: '', price: 0, stock: 0 })

const openQuickEdit = (row: any) => {
  // 回显数据
  editForm.id = row.id
  editForm.price = row.price
  editForm.stock = row.stock
  editDialog.value = true
}

const submitEdit = () => {
  // API: POST /admin/product/update/{id}
  // 注意：快速编辑只传了价格和库存，后端 Update 需要支持部分更新(DynamicUpdate)
  // 如果后端需要完整对象，这里可能需要先 fetch 详情再提交，或者后端做处理
  doPost(`/admin/product/update/${editForm.id}`, editForm)
    .then((res: any) => {
      if (res.code === 200) {
        ElMessage.success('更新成功')
        editDialog.value = false
        loadData() // 刷新列表
      }
    })
}

// 分页事件
const handleSizeChange = (val: number) => {
  query.size = val
  loadData()
}
const handleCurrentChange = (val: number) => {
  query.page = val
  loadData()
}

// 搜索
const handleSearch = () => {
  query.page = 1
  loadData()
}

// 初始化
onMounted(() => {
  loadData()
})
</script>

<template>
  <div class="bg-bg-200 p-6 rounded-lg min-h-[calc(100vh-140px)]">
    <div class="flex justify-between mb-6">
      <div class="flex gap-2">
        <h2 class="text-2xl font-bold text-white mr-4">商品列表</h2>
        <el-input 
          v-model="query.keyword" 
          placeholder="搜索商品名称..." 
          class="!w-64" 
          :prefix-icon="Search"
          clearable
          @clear="handleSearch"
          @keyup.enter="handleSearch"
        />
        <el-button type="primary" color="#3D5A80" @click="handleSearch">搜索</el-button>
      </div>
      <el-button type="primary" :icon="Plus" color="#3D5A80" @click="router.push('/pms/create')">发布商品</el-button>
    </div>
    
    <el-table :data="tableData" v-loading="loading" element-loading-background="rgba(0, 0, 0, 0.5)">
      <el-table-column prop="id" label="ID" width="80"/>
      <el-table-column label="图片" width="80">
        <template #default="{ row }">
          <img :src="row.pic" class="w-10 h-10 object-cover rounded" />
        </template>
      </el-table-column>
      <el-table-column prop="name" label="名称" show-overflow-tooltip min-width="150" />
      <el-table-column prop="price" label="价格" width="120">
         <template #default="{ row }">
           <span class="text-accent-100 font-bold">¥{{ row.price }}</span>
         </template>
      </el-table-column>
      <el-table-column prop="stock" label="库存" width="100" />
      <el-table-column prop="sale" label="销量" width="100" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.publishStatus === 1 ? 'success' : 'info'" class="border-none">
            {{ row.publishStatus === 1 ? '上架' : '下架' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" :icon="Edit" @click="openQuickEdit(row)">快编</el-button>
          <el-button link :type="row.publishStatus === 1 ? 'warning' : 'success'" @click="handleStatus(row)">
            {{ row.publishStatus === 1 ? '下架' : '上架' }}
          </el-button>
          <el-button link type="danger" :icon="Delete" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="mt-4 flex justify-end">
      <el-pagination
        v-model:current-page="query.page"
        v-model:page-size="query.size"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <el-dialog v-model="editDialog" title="快速调整" width="400px">
       <el-form label-position="top">
         <el-form-item label="价格 (元)">
            <el-input-number v-model="editForm.price" class="!w-full" :min="0" :precision="2" />
         </el-form-item>
         <el-form-item label="库存">
            <el-input-number v-model="editForm.stock" class="!w-full" :min="0" :precision="0" />
         </el-form-item>
       </el-form>
       <template #footer>
          <el-button @click="editDialog = false">取消</el-button>
          <el-button type="primary" color="#3D5A80" @click="submitEdit">保存</el-button>
       </template>
    </el-dialog>
  </div>
</template>

<style scoped>
/* 修复 el-pagination 在深色背景下的显示 */
:deep(.el-pagination) {
  --el-pagination-bg-color: transparent;
  --el-pagination-button-bg-color: #374357; /* bg-300 */
  --el-pagination-text-color: #e0e0e0; /* text-200 */
  --el-pagination-button-color: #e0e0e0;
}
:deep(.el-pagination .el-pager li) {
    background: #374357;
    border: 1px solid #374357;
}
:deep(.el-pagination .el-pager li.is-active) {
    background-color: #3D5A80; /* accent-100 */
    border-color: #3D5A80;
    color: white;
}
</style>