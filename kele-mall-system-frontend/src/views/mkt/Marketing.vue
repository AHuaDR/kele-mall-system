<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { 
  Ticket, Timer, Medal, Plus, Search, 
  Edit, Delete, DataLine, Check, Minus 
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { debounce } from 'lodash-es'

// 导入封装好的请求方法
import { doGet, doPost, doPut, doDelete } from '../../http/httpRequest' // 路径按需调整

const activeTab = ref('coupon')

// ==========================================
// 1. 优惠券逻辑 (Coupons)
// ==========================================
const couponList: any = ref([])

const couponDialogVisible = ref(false)
const couponForm: any = reactive({
  type: 1,
  name: '',
  amount: 0,
  minPoint: 0,
  publishCount: 100,
  startTime: null,
  endTime: null,
})

// 获取优惠券列表
const fetchCoupons = async () => {
  try {
    const res = await doGet('/admin/coupon/list')
    
    // 手动映射字段
    couponList.value = (res.data || []).map((item: any) => {
      const now = new Date()
      const startTime = new Date(item.startTime)
      const endTime = new Date(item.endTime)
      
      let status = 0 // 0: 已结束, 1: 进行中, 2: 未开始
      if (now < startTime){
        status = 2
      } 
      else if (now >= startTime && now <= endTime){
        status = 1
      }
      console.log("status:",status)

      return {
        id: item.id,
        name: item.name,
        type: item.type,
        value: Number(item.amount),
        min: Number(item.minPoint),
        total: item.publishCount,
        taken: item.receiveCount,
        used: item.useCount,
        status, // 根据时间动态判断
        startTime: item.startTime,
        endTime: item.endTime
      }
    })
  } catch (error) {
    console.error('加载优惠券失败', error)
  }
}

const openCouponDialog = () => {
  couponForm.name = ''
  couponForm.amount = 0
  couponForm.minPoint = 0
  couponForm.publishCount = 100
  couponForm.startTime = null
  couponForm.endTime = null

  couponDialogVisible.value = true
}

const submitCoupon = async () => {
  if (!couponForm.name) return ElMessage.warning('请输入优惠券名称')

  try {
    const res = await doPost('/admin/coupon/create', couponForm)
    if(res.code === 200){
      ElMessage.success('优惠券创建成功')
      await fetchCoupons()
      couponDialogVisible.value = false
    }
  
    
  } catch (error) {
    // 已由拦截器提示错误
  }
}

const handleDeleteCoupon = async (row: any) => {
  ElMessageBox.confirm(`确认删除 [${row.name}] 吗？`, '删除警告', {
    confirmButtonText: '删除',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await doDelete(`/admin/coupon/delete/${row.id}`)
      couponList.value = couponList.value.filter((i: any) => i.id !== row.id)
      ElMessage.success('删除成功')
    } catch (error) {
      // 已提示
    }
  }).catch(() => {
    // 取消删除
  })
}

// ==========================================
// 2. 秒杀逻辑 (Flash Sale)
// ==========================================
const flashSaleList: any = ref([])

const flashDialogVisible = ref(false)
const flashForm: any = reactive({
  name: '', product: '', price: 0, origin: 0, stock: 100
})

// 获取秒杀活动列表
const fetchFlashSales = async () => {
  try {
    const res = await doGet('/admin/flash-sales/list')
    console.log("秒杀活动列表:",res.data)
    flashSaleList.value = res.data || []
  } catch (error) {
    // handled
  }
}

const openFlashDialog = () => {
  Object.assign(flashForm, {
    name: '',   //活动名称
    productId: 0,  //商品Id
    productName: '', //商品名
    startTime: null, //秒杀开始时间
    creator: '', //创建人
    endTime: null, //结束时间
    id: 0,
    limitPerUser: 1, //每人限购数量
    status: 0, //秒杀状态 0未开始 1进行中 2 已结束 3已关闭
    originalPrice: 0, //原价
    seckillPrice: 0, //秒杀价
    stockCount: 0, //秒杀总库存
    soldCount: 0, //已售数量

  })
  flashDialogVisible.value = true
}

const submitFlash = async () => {
  if (!flashForm.name || !flashForm.productName) {
    return ElMessage.warning('请填写必要信息')
  }

  try {
    const res = await doPost('/admin/flash-sales/create', flashForm)
    if(res.code === 200){
      ElMessage.success('秒杀活动已发布')
      await fetchFlashSales()
      flashDialogVisible.value = false
    }
  } catch (error) {
    // handled
  }
}

//-----------------------------------------------------------------------------------------------
// 商品搜索相关
const productOptions = ref<{ value: string; productId: number; price: number }[]>([])
const loadingProducts = ref(false)

// 模拟或调用真实接口搜索商品
const searchProducts = async (query: string) => {
  if (!query) {
    productOptions.value = []
    return
  }

  loadingProducts.value = true
  try {
    // 调用商品搜索接口（假设存在）
    const res = await doGet('/admin/product/search', { params: { keyword: query } })
    
    productOptions.value = (res.data || []).map((item: any) => ({
      value: item.name, // 显示文本
      productId: item.id,
      price: item.price
    }))
  } catch (error) {
    console.error('搜索商品失败', error)
    productOptions.value = []
  } finally {
    loadingProducts.value = false
  }
}

// 当前选中的商品对象
const selectedProduct = ref<any>(null)

// 处理选中商品
const handleSelectProduct = (item: any) => {
  selectedProduct.value = item
  flashForm.productName = item.value
  flashForm.productId = item.productId
  flashForm.originalPrice = item.price // 自动填入原价（可选）
}


// 防抖包装函数（防止每输入一个字就发请求）
const searchProductsDebounced = debounce((queryString: string, cb: Function) => {
  searchProducts(queryString).then(() => {
    cb(productOptions.value)
  })
}, 500)



// ==========================================
// 3. 会员权益逻辑 (Membership)
// ==========================================
const vipLevels: any = ref([])

const vipDialogVisible = ref(false)
const currentVip: any = ref({})
const vipForm: any = reactive({
  minGrowth: 0,
  discountRate: 0,
  equityList: [] as string[]
})

// 获取 VIP 等级列表
const fetchVipLevels = async () => {
  try {
    const res = await doGet('/admin/vip-levels/list')
    console.log("会员权益列表:",res.data)

    vipLevels.value = (res.data || []).map((item: any) => {
      let equityArray: string[] = []

      // 安全解析 equityJson 字段
      try {
        if (typeof item.equityJson === 'string') {
          equityArray = JSON.parse(item.equityJson)
        } else if (Array.isArray(item.equityJson)) {
          equityArray = item.equityJson
        } else {
          equityArray = []
        }
      } catch (e) {
        console.error('解析 equityJson 失败:', item.equityJson, e)
        equityArray = []
      }

      return {
        level: item.level,
        name: item.name,
        icon: item.icon,
        color: item.colorClass, // 后端叫 colorClass → 前端叫 color
        equity: equityArray,    // 已转为数组
        minGrowth: item.minGrowth,
        discountRate: item.discountRate,
        annualFee: item.annualFee,
        description: item.description,
        isEnabled: item.isEnabled,
        updateTime: item.updateTime
      }
    })
  } catch (error) {
    ElMessage.error('加载会员等级失败')
    console.error('VIP 加载失败', error)
  }
}

const openVipEdit = (vip: any) => {
  currentVip.value = vip
  // vip.equity 是数组，直接复制
  vipForm.equityList = [...vip.equity] 
  vipForm.minGrowth = vip.minGrowth
  vipForm.discountRate = vip.discountRate
  vipDialogVisible.value = true
}

const addEquityItem = () => vipForm.  equityList.push('新权益')
const removeEquityItem = (index: number) => vipForm.  equityList.splice(index, 1)

const submitVip = async () => {
  try {
    const payload = {
      equityJson: JSON.stringify(vipForm.equityList) // 必须是字符串
      // 其他字段可根据需求添加（如 discountRate 等）
    }

    await doPut(`/admin/vip-levels/${currentVip.value.level}`, payload)

    // 更新原始数据中的 equity 数组（不是字符串！）
    currentVip.value.equity = [...vipForm.equityList]

    ElMessage.success(`已更新 [${currentVip.value.name}] 的权益配置`)
    vipDialogVisible.value = false
  } catch (error) {
    ElMessage.error('保存失败，请重试')
    console.error('提交 VIP 权益失败', error)
  }
}

// ========== 生命周期 ==========
onMounted(() => {
  fetchCoupons()
  fetchFlashSales()
  fetchVipLevels()
})
</script>

<template>
  <div class="bg-bg-200 p-6 rounded-xl min-h-[calc(100vh-140px)]">
    
    <div class="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
      <div class="bg-gradient-to-r from-blue-900/40 to-bg-300 border border-bg-300 p-4 rounded-lg flex items-center gap-4">
         <div class="bg-blue-500/20 p-3 rounded-full text-blue-400"><el-icon :size="24"><Ticket /></el-icon></div>
         <div><div class="text-text-200 text-xs">进行中优惠券</div><div class="text-xl font-bold text-white">{{ couponList.length }} 场</div></div>
      </div>
      <div class="bg-gradient-to-r from-purple-900/40 to-bg-300 border border-bg-300 p-4 rounded-lg flex items-center gap-4">
         <div class="bg-purple-500/20 p-3 rounded-full text-purple-400"><el-icon :size="24"><Timer /></el-icon></div>
         <div><div class="text-text-200 text-xs">秒杀活动</div><div class="text-xl font-bold text-white">{{ flashSaleList.length }} 场</div></div>
      </div>
      <div class="bg-gradient-to-r from-orange-900/40 to-bg-300 border border-bg-300 p-4 rounded-lg flex items-center gap-4">
         <div class="bg-orange-500/20 p-3 rounded-full text-orange-400"><el-icon :size="24"><DataLine /></el-icon></div>
         <div><div class="text-text-200 text-xs">今日营销GMV</div><div class="text-xl font-bold text-white">¥ 45,200</div></div>
      </div>
    </div>

    <el-tabs v-model="activeTab">
      
      <el-tab-pane label="优惠券管理" name="coupon">
        <div class="flex justify-between mb-4">
           <div class="flex gap-2">
             <el-input placeholder="搜索优惠券名称" :prefix-icon="Search" class="w-64" />
           </div>
           <el-button type="primary" color="#3D5A80" :icon="Plus" @click="openCouponDialog">新建优惠券</el-button>
        </div>
        
        <el-table :data="couponList">
           <el-table-column prop="name" label="优惠券名称" min-width="150" />
           <el-table-column label="面额/折扣">
             <template #default="{ row }">
               <span v-if="row.type == 0" class="text-accent-200 font-bold" style="color: chocolate;">{{ row.value }}折</span>
               <span v-else class="text-accent-200 font-bold">¥{{ row.value }}</span>
               <span class="text-xs text-text-200 ml-2">(满{{ row.min }}可用)</span>
             </template>
           </el-table-column>
           <el-table-column label="发放进度" width="200">
             <template #default="{ row }">
               <el-progress 
                 :percentage="Math.floor((row.taken / row.total) * 100)" 
                 :color="row.status ? '#3D5A80' : '#6b7280'"
               />
               <div class="text-xs text-text-200 mt-1">已领 {{ row.taken }} / 总量 {{ row.total }}</div>
             </template>
           </el-table-column>
           <el-table-column prop="status" label="状态" width="100">
             <template #default="{ row }">
               <el-tag :type="row.status ? 'success' : 'info'" effect="dark" class="border-none">
                 {{ row.status == 0 ? '已结束' : (row.status == 1 ? '进行中' : '未开始') }}
               </el-tag>
             </template>
           </el-table-column>
           <el-table-column label="操作" width="150">
             <template #default="{ row }">
               <el-button link type="danger" :icon="Delete" @click="handleDeleteCoupon(row)">删除</el-button>
             </template>
           </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="限时秒杀" name="flash">
        <div class="flex justify-end mb-4">
           <el-button type="primary" color="#cf1322" :icon="Plus" @click="openFlashDialog">创建秒杀</el-button>
        </div>
        <el-table :data="flashSaleList">
           <el-table-column prop="name" label="活动名称" min-width="150" />
           <el-table-column prop="productName" label="秒杀商品" />
           <el-table-column label="秒杀价">
             <template #default="{ row }">
               <div class="flex flex-col">
                  <span class="text-red-400 font-bold">¥{{ row.seckillPrice }}</span>
                  <span class="text-text-200 text-xs line-through">¥{{ row.originalPrice }}</span>
               </div>
             </template>
           </el-table-column>
           <el-table-column label="库存进度" width="180">
             <template #default="{ row }">
               <el-progress 
                  :percentage="Math.floor((row.soldCount / row.stockCount) * 100)" 
                  striped striped-flow
               />
               <span class="text-xs text-text-200">剩余 {{ row.stockCount - row.soldCount }} 件</span>
             </template>
           </el-table-column>
           <el-table-column label="状态" width="120">
             <template #default="{ row }">
                <span v-if="row.status === 1" class="text-green-400 animate-pulse font-bold">● 进行中</span>
                <span v-if="row.status === 2" class="text-green-400 animate-pulse font-bold">● 已结束</span>
                <span v-if="row.status === 3" class="text-green-400 animate-pulse font-bold">● 已关闭</span>
                <span v-if="row.status === 0" class="text-blue-400">未开始</span>
             </template>
           </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="会员权益配置" name="vip">
         <div class="grid grid-cols-1 md:grid-cols-3 gap-8 mt-4">
            <div v-for="vip in vipLevels" :key="vip.level" 
                 class="relative overflow-hidden rounded-xl h-[320px] shadow-xl transition-transform duration-300 hover:-translate-y-1"
            >
               <div :class="`absolute inset-0 bg-gradient-to-br ${vip.color}`"></div>
               <div class="absolute inset-0 bg-black bg-opacity-20 backdrop-blur-sm"></div>
               
               <div class="relative z-10 p-6 flex flex-col h-full text-white">
                  <div class="flex justify-between items-center mb-4">
                     <span class="text-5xl">{{ vip.icon }}</span>
                     <span class="text-2xl font-bold opacity-50 italic">LV.{{ vip.level }}</span>
                  </div>
                  
                  <h3 class="text-2xl font-bold mb-4">{{ vip.name }}</h3>
                  
                  <ul class="space-y-3 flex-1 overflow-y-auto mb-4">
                     <li v-for="(item, i) in vip.equity" :key="i" class="flex items-center gap-2 text-sm" style="line-height: 0.85rem">
                        <div class="bg-white/20 rounded-full p-1"><el-icon><Check /></el-icon></div>
                        {{ item }}
                     </li>
                  </ul>

                  <el-button 
                    class="w-full !bg-white/20 !border-none !text-white hover:!bg-white/30 !h-10 mt-auto"
                    @click="openVipEdit(vip)"
                  >
                    <el-icon class="mr-2"><Edit /></el-icon> 编辑权益
                  </el-button>
               </div>
            </div>
         </div>
      </el-tab-pane>

    </el-tabs>

    <el-dialog v-model="couponDialogVisible" title="新建优惠券" width="500px">
      <el-form label-position="top">
        <el-form-item label="优惠券名称">
          <el-input v-model="couponForm.name" placeholder="如：新人满减券" />
        </el-form-item>
        <div class="grid grid-cols-2 gap-4">
           <el-form-item label="类型">
             <el-select v-model="couponForm.type" class="w-full">
               <el-option label="满减券" :value="1" />
               <el-option label="折扣券" :value="0" />
             </el-select>
           </el-form-item>
           <el-form-item label="面额/折扣">
             <el-input-number v-model="couponForm.amount" class="!w-full" :min="0" />
           </el-form-item>
        </div>
        <div class="grid grid-cols-2 gap-4">
           <el-form-item label="开始时间">
             <el-date-picker
                v-model="couponForm.startTime"
                type="datetime"
                placeholder="Select date and time"
              />
           </el-form-item>
           <el-form-item label="结束时间">
             <el-date-picker
                v-model="couponForm.endTime"
                type="datetime"
                placeholder="Select date and time"
              />
           </el-form-item>
        </div>
        <div class="grid grid-cols-2 gap-4">
           <el-form-item label="使用门槛 (满)">
             <el-input-number v-model="couponForm.minPoint" class="!w-full" :min="0" />
           </el-form-item>
           <el-form-item label="发行总量">
             <el-input-number v-model="couponForm.publishCount" class="!w-full" :min="1" />
           </el-form-item>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="couponDialogVisible = false">取消</el-button>
        <el-button type="primary" color="#3D5A80" @click="submitCoupon">立即创建</el-button>
      </template>
    </el-dialog>

    <!-- 
      createTime: null, //创建时间
      creator: '', //创建人
      id: 0,
    -->

    <el-dialog v-model="flashDialogVisible" title="创建秒杀活动" width="500px">
      <el-form label-position="top">
        <el-form-item label="活动名称">
          <el-input v-model="flashForm.name" placeholder="如：双12整点秒杀" />
        </el-form-item>
        <el-form-item label="关联商品">
          <el-autocomplete
            v-model="flashForm.productName"
            :fetch-suggestions="searchProductsDebounced"
            @select="handleSelectProduct"
            placeholder="请输入商品名称进行搜索"
            :loading="loadingProducts"
            clearable
            class="w-full"
          >
            <template #default="{ item }">
              <div class="flex justify-between w-full">
                <span>{{ item.value }}</span>
                <span class="text-orange-500 font-medium">¥{{ item.price }}</span>
              </div>
            </template>
          </el-autocomplete>
        </el-form-item>
        <div class="grid grid-cols-2 gap-4">
           <el-form-item label="原价">
             <el-input-number v-model="flashForm.originalPrice" class="!w-full" :min="0" />
           </el-form-item>
           <el-form-item label="秒杀价">
             <el-input-number v-model="flashForm.seckillPrice" class="!w-full" :min="0" />
           </el-form-item>
        </div>
        <div class="grid grid-cols-2 gap-4">
           <el-form-item label="开始时间">
             <el-date-picker
                v-model="flashForm.startTime"
                type="datetime"
                placeholder="Select date and time"
              />
           </el-form-item>
           <el-form-item label="结束时间">
             <el-date-picker
                v-model="flashForm.endTime"
                type="datetime"
                placeholder="Select date and time"
              />
           </el-form-item>
        </div>
        <div class="grid grid-cols-2 gap-4">
           <el-form-item label="秒杀库存">
              <el-input-number v-model="flashForm.stockCount" class="!w-full" :min="1" />
            </el-form-item>
           <el-form-item label="每人限购数量">
              <el-input-number v-model="flashForm.limitPerUser" class="!w-full" :min="1" />
            </el-form-item>
        </div>
        
      </el-form>
      <template #footer>
        <el-button @click="flashDialogVisible = false">取消</el-button>
        <el-button type="primary" color="#cf1322" @click="submitFlash">发布活动</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="vipDialogVisible" :title="`编辑权益 - ${currentVip.name}`" width="400px">
      <div class="mb-4 text-text-200 text-sm">请配置该等级会员可享受的特权服务：</div>
              <el-form-item label="所需最低成长值">
                  <el-input-number v-model="vipForm.minGrowth" :min="0" />
              </el-form-item>
              <el-form-item label="购物折扣率(0~1)">
                  <el-input-number v-model="vipForm.discountRate" :min="0" :max="1" :precision="2" :step="0.1" />
              </el-form-item>
      <div v-for="(item, index) in vipForm.equityList" :key="index" class="flex gap-2 mb-2">
        <el-input v-model="vipForm.equityList[index]" />
        <el-button type="danger" :icon="Minus" circle @click="removeEquityItem(index)" />
      </div>
      <el-button class="w-full mt-2 border-dashed" :icon="Plus" @click="addEquityItem">
        添加一项权益
      </el-button>

  <template #footer>
    <el-button @click="vipDialogVisible = false">取消</el-button>
    <el-button type="primary" color="#3D5A80" @click="submitVip">保存权益</el-button>
  </template>
</el-dialog>

  </div>
</template>

<style scoped>
:deep(.el-progress-bar__inner) { text-align: center; }
</style>