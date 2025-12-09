<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { Search, UserFilled } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { doGet, doPost } from '../../http/httpRequest'

// 1. 定义接口类型 (根据截图 image_92f19d.png 修正)
interface Member {
  id: number
  username: string 
  nickname: string
  phone: string
  avatar: string      
  levelId: number     // [修正] 截图显示为 levelId
  point: number       
  status: boolean     // [修正] 截图显示为 boolean (true/false)
  createTime: string  // [修正] 截图显示为 createTime
  
  // 前端辅助字段
  _levelName: string 
}

// 2. 状态数据
const customers = ref<Member[]>([])
const loading = ref(false)
const total = ref(0) 

// 查询参数
const queryParams = reactive({
  page: 1,  
  size: 10, 
  nickname: '' 
})

// 3. 获取会员列表
const fetchList = async () => {
  loading.value = true
  try {
    const res: any = await doGet('/admin/member/list', queryParams)
    
    if (res.code === 200) {
      // [修正] 根据截图，res.data 就是包含 list 和 total 的对象
      const pageData = res.data || {}
      const list = pageData.list || []
      
      // 更新总条数
      total.value = pageData.total || 0

      // 数据映射
      customers.value = list.map((item: any) => ({
        ...item, 
        
        // 1. 等级转换: 使用 levelId
        _levelName: mapLevelName(item.levelId),
        
        // 2. 兜底处理 (防止 null)
        nickname: item.nickname || item.username || '未命名用户',
        avatar: item.avatar || '',
        point: item.point || 0,
        createTime: item.createTime || '',
        // status 已经是 boolean，直接使用即可，无需转换
        status: item.status 
      }))
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 等级 ID 转名称映射
const mapLevelName = (levelId: number) => {
  const map: Record<number, string> = {
    1: '普通会员',
    2: '铂金会员',
    3: '黄金会员'
  }
  return map[levelId] || '普通会员'
}

// 获取标签颜色
const getLevelTagType = (levelId: number) => {
  switch (levelId) {
    case 1: return 'info'    
    case 2: return ''        
    case 3: return 'warning' 
    default: return 'info'
  }
}

// 4. 修改状态
const handleStatusChange = async (row: Member) => {
  // row.status 已经是切换后的 boolean 值
  // 后端虽然返回 boolean，但接收参数通常还是建议传 1/0 或者根据接口文档传 true/false
  // 这里假设 updateStatus 接口兼容 0/1
  const statusParam = row.status ? 1 : 0
  
  try {
    await doPost(`/admin/member/updateStatus/${row.id}`, { status: statusParam })
    ElMessage.success(`状态已更新`)
  } catch (error) {
    // 失败回滚
    row.status = !row.status
    ElMessage.error('状态修改失败')
  }
}

// 5. 分页处理
const handleSizeChange = (val: number) => {
  queryParams.size = val
  queryParams.page = 1 
  fetchList()
}

const handleCurrentChange = (val: number) => {
  queryParams.page = val
  fetchList()
}

// 搜索
const handleSearch = () => {
  queryParams.page = 1 
  fetchList()
}

onMounted(() => {
  fetchList()
})
</script>

<template>
  <div class="bg-bg-200 p-6 rounded-xl min-h-[calc(100vh-140px)] flex flex-col">
    <div class="flex justify-between mb-6">
      <h2 class="text-2xl font-bold text-white">C端会员列表</h2>
      <div class="flex gap-2">
        <el-input 
          v-model="queryParams.nickname" 
          placeholder="搜昵称" 
          :prefix-icon="Search" 
          class="w-64" 
          clearable
          @keyup.enter="handleSearch"
          @clear="handleSearch"
        />
        <el-button type="primary" color="#3D5A80" @click="handleSearch">查询</el-button>
      </div>
    </div>

    <div class="flex-1">
      <el-table :data="customers" v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        
        <el-table-column label="用户">
          <template #default="{ row }">
             <div class="flex items-center gap-3">
                <el-avatar :size="32" class="bg-accent-100" :src="row.avatar" :icon="UserFilled"></el-avatar>
                <span class="text-text-100 font-bold">{{ row.nickname }}</span>
             </div>
          </template>
        </el-table-column>
        
        <el-table-column prop="phone" label="手机号" />
        
        <el-table-column label="等级">
           <template #default="{ row }">
              <el-tag 
                :type="getLevelTagType(row.levelId)" 
                effect="dark" 
                class="border-none"
              >
                {{ row._levelName }}
              </el-tag>
           </template>
        </el-table-column>
        
        <el-table-column label="积分余额">
           <template #default="{ row }">
              <span class="text-accent-200">{{ row.point }}</span>
           </template>
        </el-table-column>
        
        <el-table-column prop="createTime" label="注册时间" width="180" />
        
        <el-table-column label="账号状态">
           <template #default="{ row }">
              <el-switch 
                v-model="row.status" 
                active-color="#3D5A80" 
                inactive-color="#374357" 
                @change="handleStatusChange(row)"
              />
           </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="flex justify-end mt-4 pt-4 border-t border-bg-100">
      <el-pagination
        v-model:current-page="queryParams.page"
        v-model:page-size="queryParams.size"
        :page-sizes="[10, 20, 50, 100]"
        :background="true"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<style scoped>
:deep(.el-pagination__total), :deep(.el-pagination__jump) {
  color: #a0aec0; 
}
</style>