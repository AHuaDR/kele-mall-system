<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { Search, Box, Refresh, Notebook } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { doGet, doPost } from '../../http/httpRequest'

// 1. 定义接口类型
interface SkuStock {
  id: number
  skuCode: string
  productName: string 
  stock: number
  lowStock: number    
  lockStock: number   
  warehouse?: string  
}

interface StockLog {
  id: number
  skuCode: string
  changeCount: number
  finalStock: number
  type: number // 1:订单扣减, 2:退货回补, 3:管理员修改, 4:补货
  operator: string
  note: string
  createTime: string
}

// 状态数据
const inventoryData = ref<SkuStock[]>([])
const loading = ref(false)
const total = ref(0)
const totalStockCount = ref(0) 
const lowStockCount = ref(0)   

// 查询参数
const queryParams = reactive({
  keyword: '', 
  pageNum: 1,
  pageSize: 10
})

// 补货弹窗
const dialogVisible = ref(false)
const currentItem: any = ref({})
const addCount = ref(10)

// 流水弹窗
const logDialogVisible = ref(false)
const logList = ref<StockLog[]>([])
const logLoading = ref(false)
const currentSkuCode = ref('')

// ==========================================
// 2. 初始化与加载
// ==========================================
onMounted(() => {
  fetchList()
})

const fetchList = async () => {
  loading.value = true
  try {
    // API: GET /admin/sku/stock/list
    const res: any = await doGet('/admin/sku/stock/list', queryParams)
    
    if (res.code === 200) {
      const pageData = res.data || {}
      const list = pageData.list || []
      
      total.value = pageData.total || 0
      
      // 数据映射
      inventoryData.value = list.map((item: any) => ({
        id: item.id,
        productId: item.productId,
        skuCode: item.skuCode,
        productName: item.productName || '未知商品', 
        stock: item.stock || 0,
        lowStock: item.lowStock || 10, 
        lockStock: item.lockStock || 0,
        warehouse: '主仓库' 
      }))

      // 简单计算统计数据
      totalStockCount.value = inventoryData.value.reduce((acc, cur) => acc + cur.stock, 0)
      lowStockCount.value = inventoryData.value.filter(i => i.stock < i.lowStock).length
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  queryParams.pageNum = 1
  fetchList()
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
// 3. 补货逻辑
// ==========================================
const openRestock = (row: any) => {
  currentItem.value = row
  dialogVisible.value = true
}

const handleRestock = async () => {
  // API: POST /admin/sku/stock/update
  const res: any = await doPost('/admin/sku/stock/update', {
    id: currentItem.value.id,
    productId: currentItem.value.productId,
    stock: Number(currentItem.value.stock) + Number(addCount.value), // 累加
    lowStock: currentItem.value.lowStock 
  })

  if (res.code === 200) {
    ElMessage.success(`补货成功！`)
    dialogVisible.value = false
    fetchList() 
  } else {
    ElMessage.error(res.message || '补货失败')
  }
}

// ==========================================
// 4. 🔥 流水查看逻辑 (新增)
// ==========================================
const showHistory = async (row: any) => {
  currentSkuCode.value = row.skuCode
  logDialogVisible.value = true
  logLoading.value = true
  logList.value = []

  try {
    // API: GET /admin/sku/stock/log/{skuId}
    const res: any = await doGet(`/admin/sku/stock/log/${row.skuCode}`)
    if (res.code === 200) {
      logList.value = res.data || []
    }
  } catch (error) {
    console.error(error)
  } finally {
    logLoading.value = false
  }
}

// 辅助函数：格式化类型
const formatLogType = (type: number) => {
  const map: any = { 1: '订单扣减', 2: '退货回补', 3: '管理员修改', 4: '补货入库' }
  return map[type] || '未知操作'
}

const getLogTypeTag = (type: number) => {
  if (type === 1) return 'warning' // 扣减
  if (type === 2 || type === 4) return 'success' // 增加
  return 'info'
}
</script>

<template>
  <div class="bg-bg-200 p-6 rounded-xl min-h-[calc(100vh-140px)] flex flex-col">
    <div class="flex justify-between items-center mb-6">
      <div>
        <h2 class="text-2xl font-bold text-white">库存管理</h2>
        <p class="text-text-200 text-sm mt-1">实时监控各仓库SKU库存水位</p>
      </div>
      <div class="flex gap-3">
        <el-input 
          v-model="queryParams.keyword" 
          placeholder="SKU编码/商品名" 
          class="w-64" 
          :prefix-icon="Search"
          clearable
          @keyup.enter="handleSearch"
          @clear="handleSearch"
        />
        <el-button type="primary" color="#3D5A80" @click="handleSearch">查询</el-button>
      </div>
    </div>

    <div class="grid grid-cols-3 gap-4 mb-6">
      <div class="bg-bg-300 p-4 rounded border border-bg-300 flex items-center gap-4">
        <div class="bg-blue-500/20 p-3 rounded text-blue-400"><el-icon :size="24"><Box /></el-icon></div>
        <div>
           <div class="text-text-200 text-xs">库存总数 (本页)</div>
           <div class="text-xl font-bold text-white">{{ totalStockCount }}</div>
        </div>
      </div>
      <div class="bg-bg-300 p-4 rounded border border-bg-300 flex items-center gap-4">
        <div class="bg-red-500/20 p-3 rounded text-red-400"><el-icon :size="24"><Refresh /></el-icon></div>
        <div>
           <div class="text-text-200 text-xs">缺货预警 SKU</div>
           <div class="text-xl font-bold text-white">{{ lowStockCount }}</div>
        </div>
      </div>
    </div>

    <div class="flex-1">
      <el-table :data="inventoryData" v-loading="loading" style="width: 100%">
        <el-table-column prop="skuCode" label="SKU 编码" width="180" />
        <el-table-column prop="productName" label="商品名称" min-width="200" />
        <el-table-column prop="warehouse" label="仓库" width="150">
          <template #default="{ row }">
             <el-tag type="info" effect="dark" class="border-none bg-bg-300">{{ row.warehouse }}</el-tag>
          </template>
        </el-table-column>
        
        <el-table-column label="可用库存" width="180">
          <template #default="{ row }">
            <div class="flex items-center gap-2">
              <div class="w-16 h-2 bg-bg-300 rounded-full overflow-hidden">
                 <div 
                   class="h-full rounded-full transition-all duration-500" 
                   :class="row.stock < row.lowStock ? 'bg-red-500' : 'bg-green-500'"
                   :style="`width: ${Math.min((row.stock / 200) * 100, 100)}%`"
                 ></div>
              </div>
              <span :class="row.stock < row.lowStock ? 'text-red-400 font-bold' : 'text-white'">{{ row.stock }}</span>
            </div>
          </template>
        </el-table-column>
  
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="openRestock(row)">补货</el-button>
            <el-button type="info" link :icon="Notebook" @click="showHistory(row)">流水</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="flex justify-end mt-4 pt-4 border-t border-bg-100">
      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :page-sizes="[10, 20, 50]"
        :background="true"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <el-dialog v-model="dialogVisible" title="库存补货" width="400px">
      <div class="text-text-200 mb-4">正在为 <span class="text-accent-200">{{ currentItem.productName }}</span> 补货</div>
      <el-form label-position="top">
        <el-form-item label="入库数量">
           <el-input-number v-model="addCount" :min="1" class="!w-full" />
        </el-form-item>
        <el-form-item label="备注">
           <el-input type="textarea" placeholder="例如：采购单 PO-20231128" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" color="#3D5A80" @click="handleRestock">确认入库</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="logDialogVisible" :title="`库存流水 - ${currentSkuCode}`" width="700px">
      <el-table :data="logList" height="400" v-loading="logLoading">
         <el-table-column prop="createTime" label="时间" width="160" />
         <el-table-column prop="type" label="类型" width="100">
            <template #default="{ row }">
               <el-tag size="small" :type="getLogTypeTag(row.type)">{{ formatLogType(row.type) }}</el-tag>
            </template>
         </el-table-column>
         <el-table-column prop="changeCount" label="变动" width="100">
            <template #default="{ row }">
               <span :class="row.changeCount > 0 ? 'text-green-500' : 'text-red-500'">
                 {{ row.changeCount > 0 ? '+' : '' }}{{ row.changeCount }}
               </span>
            </template>
         </el-table-column>
         <el-table-column prop="finalStock" label="结余" width="80" />
         <el-table-column prop="operator" label="操作人" width="100" />
         <el-table-column prop="note" label="备注" />
      </el-table>
    </el-dialog>
  </div>
</template>

<style scoped>
:deep(.el-pagination__total), :deep(.el-pagination__jump) {
  color: #a0aec0; 
}
</style>