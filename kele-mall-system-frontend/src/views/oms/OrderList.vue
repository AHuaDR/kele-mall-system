<script setup lang="ts">
import { ref, reactive, onMounted, watch } from 'vue'
import { Van } from '@element-plus/icons-vue' 
import { ElMessage } from 'element-plus'
import { doGet, doPost } from '../../http/httpRequest'

// 1. 定义接口类型 (适配后端返回)
interface Order {
  id: number
  orderSn: string        // 订单号
  receiverName: string   // 收货人
  totalAmount: number    // 金额
  status: number         // 0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭
  createTime: string
  payAmount: number
  freightAmount: number
  payType: number
  deliveryCompany: string
  deliverySn: string
  couponAmount: number
  receiverPhone: string
  receiverProvince: string
  receiverCity: string
  receiverRegion: string
  receiverDetailAddress: string
  note: string
  paymentTime: string
  deliveryTime: string
  receiveTime: string
  orderItemDOList?: any[]  // 订单商品列表 (详情用)
}

const activeTab = ref('all') // all, 1(待发货), 2(已发货)
const tableData = ref<Order[]>([])
const loading = ref(false)
const total = ref(0)

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  status: undefined as number | undefined, // 状态筛选
  orderSn: ''
})

// 状态映射表
const statusMap: Record<number, { text: string; type: string }> = {
  0: { text: '待付款', type: 'danger' },
  1: { text: '待发货', type: 'warning' },
  2: { text: '已发货', type: 'success' },
  3: { text: '已完成', type: 'success' },
  4: { text: '已关闭', type: 'info' },
  5: { text: '无效订单', type: 'danger'}
}

// 支付方式映射表
const payTypeMap: Record<number, { text: string; type: string }> = {
  0: { text: '待付款', type: 'danger' },
  1: { text: '支付宝', type: 'warning' },
  2: { text: '微信', type: 'success' },
}

// 解析商品规格属性
const formatProductAttr = (attrJson: string) => {
  if (!attrJson) return '';
  try {
    // 尝试解析 JSON
    const attrs = JSON.parse(attrJson);
    if (Array.isArray(attrs)) {
      // 如果解析出来是数组，拼接成 "颜色:黑色; 容量:12+256G" 的格式
      return attrs.map((a: any) => `${a.key}:${a.value}`).join('; ');
    }
    return attrJson; // 如果不是数组，直接返回原字符串
  } catch (e) {
    // 解析失败，直接返回原字符串
    return attrJson;
  }
}

// ==========================================
// 2. 初始化与加载
// ==========================================
onMounted(() => {
  fetchList()
})

const fetchList = async () => {
  loading.value = true
  
  // 处理 Tab 切换逻辑
  if (activeTab.value === 'all') {
    queryParams.status = undefined
  } else {
    // 转换为数字: '1' -> 1
    queryParams.status = Number(activeTab.value)
  }

  try {
    // API: GET /admin/order/list
    const res: any = await doGet('/admin/order/list', queryParams)
    
    if (res.code === 200) {
      const pageData = res.data || {}
      const list = pageData.list || []
      total.value = pageData.total || 0
      
      tableData.value = list
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 监听 Tab 时重置页码并刷新
watch(activeTab, (newVal) => {
  queryParams.pageNum = 1 // 切换 tab 重置页码
  // 这里不需要手动赋值 queryParams.status，因为 fetchList 里会根据 activeTab.value (也就是 newVal) 重新计算
  fetchList()
})

// 分页
const handleSizeChange = (val: number) => {
  queryParams.pageSize = val
  fetchList()
}
const handleCurrentChange = (val: number) => {
  queryParams.pageNum = val
  fetchList()
}

// ==========================================
// 3. 详情抽屉
// ==========================================
const drawerVisible = ref(false)
const currentOrder = ref<Order>({} as Order)
const orderItems = ref<any[]>([]) // 商品清单

const handleDetail = async (row: Order) => {
  // 先展示基础信息
  currentOrder.value = row
  drawerVisible.value = true
  
  // API: GET /admin/order/detail/{id} 获取完整详情(含商品)
  const res: any = await doGet(`/admin/order/detail/${row.id}`)
  if (res.code === 200 && res.data) {
    console.log("订单详情数据:",res.data)
    currentOrder.value = res.data
    orderItems.value = res.data.orderItemDOList || []
    console.log("orderItems:",orderItems.value)
  }
}

// ==========================================
// 4. 发货弹窗
// ==========================================
const shipDialogVisible = ref(false)
const shipForm = reactive({
  orderId: 0,
  orderSn: '',
  deliveryCompany: '顺丰速运',
  deliverySn: ''
})
const logisticsCompanies = ['顺丰速运', '中通快递', '圆通速递', '京东物流', 'EMS']

const openShipDialog = (row: Order) => {
  shipForm.orderId = row.id
  shipForm.orderSn = row.orderSn
  shipForm.deliveryCompany = '顺丰速运'
  shipForm.deliverySn = ''
  shipDialogVisible.value = true
}

const handleShipConfirm = async () => {
  if (!shipForm.deliverySn) return ElMessage.warning('请填写运单号')
  
  // API: POST /admin/order/delivery
  const res: any = await doPost('/admin/order/delivery', {
    orderId: shipForm.orderId,
    deliveryCompany: shipForm.deliveryCompany,
    deliverySn: shipForm.deliverySn
  })

  if (res.code === 200) {
    ElMessage.success(`订单发货成功！`)
    shipDialogVisible.value = false
    fetchList() // 刷新列表
  } else {
    ElMessage.error(res.message || '发货失败')
  }
}
</script>

<template>
  <div class="bg-bg-200 p-6 rounded-xl min-h-[calc(100vh-140px)] flex flex-col">
    <h2 class="text-2xl font-bold text-white mb-6">订单管理</h2>
    
    <el-tabs v-model="activeTab" class="mb-4" >
      <el-tab-pane label="全部订单" name="all" />
      <el-tab-pane label="待发货" name="1" />
      <el-tab-pane label="已发货" name="2" />
      <el-tab-pane label="已关闭" name="4" />
    </el-tabs>

    <div class="flex-1">
      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="orderSn" label="订单号" width="220" />
        <el-table-column prop="receiverName" label="收货人" />
        <el-table-column label="金额">
          <template #default="{ row }">¥{{ row.totalAmount }}</template>
        </el-table-column>
        <el-table-column label="状态">
          <template #default="{ row }">
            <el-tag :type="statusMap[row.status]?.type" effect="dark" class="border-none">
              {{ statusMap[row.status]?.text || '未知' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button 
              link type="primary" 
              :icon="Van" 
              v-if="row.status === 1" 
              @click="openShipDialog(row)"
            >
              去发货
            </el-button>
            <el-button link type="info" @click="handleDetail(row)">详情</el-button>
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

    <el-drawer v-model="drawerVisible" title="订单详情" size="500px" class="!bg-bg-200">
       <div class="p-4 bg-bg-300 rounded mb-4 text-sm">
          <div class="mb-2 text-gray-400">基本信息</div>
          <div class="grid grid-cols-2 gap-2">
             <p class="text-text-200">订单号：<span class="text-white">{{ currentOrder.orderSn }}</span></p>
             <p class="text-text-200">买家：<span class="text-white">{{ currentOrder.receiverName }}</span></p>
             <p class="text-text-200">下单时间：<span class="text-white">{{ currentOrder.createTime }}</span></p>
             <p class="text-text-200">订单金额：<span class="text-accent-200">¥{{ currentOrder.totalAmount }}</span></p>
             <p class="text-text-200">状态：<span class="text-accent-200">{{ statusMap[currentOrder.status]?.text || '未知' }}</span></p>
             <p class="text-text-200">应付金额：<span class="text-white">¥{{ currentOrder.payAmount }}</span></p>
             <p class="text-text-200">优惠券抵用：<span class="text-white">¥{{ currentOrder.couponAmount }}</span></p>
             <p class="text-text-200">物流公司：<span class="text-white">{{ currentOrder.deliveryCompany }}</span></p>
             <p class="text-text-200">物流单号：<span class="text-white">{{ currentOrder.deliverySn }}</span></p>
             <p class="text-text-200">运费金额：<span class="text-white">¥{{ currentOrder.freightAmount }}</span></p>
             <p class="text-text-200">支付方式：<span class="text-white">{{ payTypeMap[currentOrder.payType]?.text || '未知' }}</span></p>
             <p class="text-text-200">收货人电话：<span class="text-accent-200">{{ currentOrder.receiverPhone }}</span></p>
             <p class="text-text-200">省市/直辖市：<span class="text-accent-200">{{ currentOrder.receiverProvince }}</span></p>
             <p class="text-text-200">城市：<span class="text-accent-200">{{ currentOrder.receiverCity }}</span></p>
             <p class="text-text-200">区：<span class="text-accent-200">{{ currentOrder.receiverRegion }}</span></p>
             <p class="text-text-200">详细地址：<span class="text-accent-200">{{ currentOrder.receiverDetailAddress }}</span></p>
             <p class="text-text-200">订单备注：<span class="text-accent-200">{{ currentOrder.note }}</span></p>
             <p class="text-text-200">支付时间：<span class="text-accent-200">{{ currentOrder.paymentTime }}</span></p>
             <p class="text-text-200">发货时间：<span class="text-accent-200">{{ currentOrder.deliveryTime }}</span></p>
             <p class="text-text-200">确认收货时间：<span class="text-accent-200">{{ currentOrder.receiveTime }}</span></p>
          </div>
       </div>
       
       <h3 class="text-white font-bold mb-2">商品清单</h3>
       <div class="bg-bg-300 rounded overflow-hidden">
          <div v-for="item in orderItems" :key="item.id" class="flex items-center gap-3 p-3 border-b border-bg-200 last:border-none">
             <img :src="item.productPic" class="w-12 h-12 rounded object-cover bg-gray-600" />
             <div class="flex-1">
                <div class="text-white text-sm line-clamp-1">{{ item.productName }}</div>
                <div class="text-gray-400 text-xs mt-1">规格: {{ formatProductAttr(item.productAttr) }}</div>
             </div>
             <div class="text-right">
                <div class="text-white">¥{{ item.productPrice }}</div>
                <div class="text-gray-400 text-xs">x{{ item.productQuantity }}</div>
             </div>
          </div>
          <div v-if="orderItems.length === 0" class="p-4 text-center text-gray-500">暂无商品信息</div>
       </div>
    </el-drawer>

    <el-dialog v-model="shipDialogVisible" title="订单发货" width="450px">
      <el-form label-position="top">
        <el-form-item label="订单编号">
          <el-input v-model="shipForm.orderSn" disabled />
        </el-form-item>
        <el-form-item label="物流公司">
          <el-select v-model="shipForm.deliveryCompany" class="w-full">
            <el-option v-for="c in logisticsCompanies" :key="c" :label="c" :value="c" />
          </el-select>
        </el-form-item>
        <el-form-item label="快递单号">
          <el-input v-model="shipForm.deliverySn" placeholder="请输入运单号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="shipDialogVisible = false">取消</el-button>
        <el-button type="primary" color="#3D5A80" @click="handleShipConfirm">确认发货</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
:deep(.el-pagination__total), :deep(.el-pagination__jump) {
  color: #a0aec0; 
}
:deep(.el-drawer__header) {
  margin-bottom: 0;
  color: #fff;
}
</style>