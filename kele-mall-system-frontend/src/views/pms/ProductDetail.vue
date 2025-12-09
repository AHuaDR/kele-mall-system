<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { UploadProps } from 'element-plus'
import { Plus, Delete, CirclePlus } from '@element-plus/icons-vue'
import { doGet, doPost } from '../../http/httpRequest'

// ==========================================
// 1. 基础配置与类型定义
// ==========================================
const route = useRoute()
const router = useRouter()
const routeId = Array.isArray(route.params.id) ? route.params.id[0] : route.params.id
const isEdit = computed(() => !!routeId)

interface ProductForm {
  id: number | null
  name: string
  subTitle: string
  categoryId: number | null
  brandId: number | null // [修改] 允许为空
  price: number
  originalPrice: number
  stock: number
  pic: string
  detailHtml: string
  sort: number
  publishStatus: number
}

interface SkuItem {
  id?: number | null
  productId?: number
  skuCode: string
  price: number
  stock: number
  sp1: string
  sp2: string
  sp3: string 
  pic?: string
  sale?: number
  _key?: string 
}

interface SpecDefinition {
  name: string
  values: string[]
}

const form = ref<ProductForm>({
  id: null,
  name: '',
  subTitle: '',
  categoryId: null,
  brandId: null, // [修改] 初始为空
  price: 0,
  originalPrice: 0,
  stock: 0,
  pic: '',
  detailHtml: '',
  sort: 0,
  publishStatus: 1
})

const categoryOptions = ref<any[]>([])
const brandOptions = ref<any[]>([]) // [新增] 品牌列表
const skuList = ref<SkuItem[]>([])

const specDefinitions = ref<SpecDefinition[]>([
  { name: '规格1', values: [] }
])

// ==========================================
// 2. 初始化逻辑
// ==========================================
onMounted(() => {
  loadCategories()
  loadBrands() // [新增] 加载品牌
  if (isEdit.value && routeId) {
    loadProductDetail(routeId)
  }
})

const loadCategories = () => {
  doGet('/admin/category/list', { page: 1, size: 1000 }).then((res: any) => {
    if (res.data) categoryOptions.value = res.data || []
  })
}

// [新增] 加载品牌列表
const loadBrands = () => {
  // 假设接口是 /admin/brand/list，通常带有分页，这里取前1000个
  doGet('/admin/brand/list', { page: 1, size: 1000 }).then((res: any) => {
    if (res.data) {
       // 兼容可能的返回结构：res.data.list 或 res.data
       brandOptions.value = res.data.list || res.data || []
    }
  })
}

const loadProductDetail = (productId: string) => {
  doGet(`/admin/product/info/${productId}`).then((res: any) => {
    if (res.code === 200 && res.data) {
      const data = res.data
      Object.assign(form.value, data)
      
      if (data.skuStockList && Array.isArray(data.skuStockList)) {
        skuList.value = data.skuStockList
        reverseAnalysisSpecs(data.skuStockList)
      }
    } else {
      ElMessage.error('商品详情加载失败')
    }
  })
}

const reverseAnalysisSpecs = (skus: SkuItem[]) => {
  const sp1Vals = new Set<string>()
  const sp2Vals = new Set<string>()
  const sp3Vals = new Set<string>()
  
  skus.forEach(item => {
    if (item.sp1) sp1Vals.add(item.sp1)
    if (item.sp2) sp2Vals.add(item.sp2)
    if (item.sp3) sp3Vals.add(item.sp3)
  })

  const newSpecs: SpecDefinition[] = []
  if (sp1Vals.size > 0) newSpecs.push({ name: '规格1', values: Array.from(sp1Vals) })
  if (sp2Vals.size > 0) newSpecs.push({ name: '规格2', values: Array.from(sp2Vals) })
  if (sp3Vals.size > 0) newSpecs.push({ name: '规格3', values: Array.from(sp3Vals) })
  
  if (newSpecs.length > 0) {
    specDefinitions.value = newSpecs
  }
}

// ==========================================
// 3. 上传相关
// ==========================================
const baseURL = 'http://localhost:8080'
const uploadAction = `${baseURL}/admin/common/upload`
const token = localStorage.getItem('cola_admin_token')
const uploadHeaders = { Authorization: token || '' }

const handlePicSuccess: UploadProps['onSuccess'] = (res) => {
  if (res.code === 200) {
    form.value.pic = res.data
    ElMessage.success('上传成功')
  } else {
    ElMessage.error(res.message || '上传失败')
  }
}

// ==========================================
// 4. 规格 & SKU 核心逻辑
// ==========================================

const addSpec = () => {
  if (specDefinitions.value.length >= 3) return ElMessage.warning('最多支持3种规格')
  specDefinitions.value.push({ name: '', values: [] })
}

const removeSpec = (index: number) => {
  specDefinitions.value.splice(index, 1)
  generateSkuList()
}

const addSpecValue = (specIndex: number) => {
  const spec = specDefinitions.value[specIndex]
  if (!spec) return
  if (!spec.name) return ElMessage.warning('请先输入规格名称')

  ElMessageBox.prompt(`请输入${spec.name}的值`, '添加规格值', { 
    confirmButtonText: '确定', cancelButtonText: '取消' 
  }).then(({ value }) => {
    if (value && !spec.values.includes(value)) {
      spec.values.push(value)
      generateSkuList()
    }
  }).catch(() => {})
}

const removeSpecValue = (specIndex: number, valIndex: number) => {
  const spec = specDefinitions.value[specIndex]
  if (spec && spec.values) {
    spec.values.splice(valIndex, 1)
    generateSkuList()
  }
}

const cartesianProduct = (arr: string[][]): string[][] => {
  if (arr.length === 0) return []
  let result: string[][] = [[]]
  for (const pool of arr) {
    const temp: string[][] = []
    for (const x of result) {
      for (const y of pool) {
        temp.push([...x, y])
      }
    }
    result = temp
  }
  return result
}

const generateSkuList = () => {
  const validSpecs = specDefinitions.value.filter(s => s.values && s.values.length > 0)
  if (validSpecs.length === 0) {
    skuList.value = []
    calcTotalStock()
    return
  }
  const valuesList = validSpecs.map(s => s.values)
  const combinations = cartesianProduct(valuesList)

  const newSkus: SkuItem[] = combinations.map((comb) => {
    const c0 = comb[0] || ''
    const c1 = comb[1] || ''
    const c2 = comb[2] || ''
    const key = [c0, c1, c2].filter(Boolean).join('|')
    const oldMatch = skuList.value.find(old => {
       const oldKey = [old.sp1, old.sp2, old.sp3].filter(Boolean).join('|')
       return oldKey === key
    })

    return {
      id: oldMatch ? oldMatch.id : null,
      productId: form.value.id || undefined,
      sp1: c0, sp2: c1, sp3: c2,
      skuCode: oldMatch ? oldMatch.skuCode : '',
      price: oldMatch ? oldMatch.price : form.value.price, 
      stock: oldMatch ? oldMatch.stock : 0,
      pic: oldMatch ? oldMatch.pic : '',
      _key: key
    }
  })
  skuList.value = newSkus
  calcTotalStock()
}

const calcTotalStock = () => {
  let total = 0
  skuList.value.forEach(item => total += Number(item.stock || 0))
  if (skuList.value.length > 0) form.value.stock = total
}

// ==========================================
// 5. 提交逻辑
// ==========================================
const onSubmit = () => {
  if (!form.value.name) return ElMessage.warning('请输入商品名称')
  if (!form.value.categoryId) return ElMessage.warning('请选择商品分类')
  if (!form.value.brandId) return ElMessage.warning('请选择商品品牌') // [新增校验]
  if (!form.value.pic) return ElMessage.warning('请上传商品主图')
  
  const hasSpecs = specDefinitions.value.some(s => s.values.length > 0)
  if (hasSpecs && skuList.value.length === 0) return ElMessage.warning('请完善规格值以生成SKU')

  const payload = { ...form.value, skuStockList: skuList.value }
  const url = isEdit.value ? `/admin/product/update/${routeId}` : `/admin/product/create`
  
  doPost(url, payload).then((res: any) => {
    if (res.code === 200) {
      ElMessage.success(isEdit.value ? '修改成功' : '发布成功')
      router.back()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  })
}
</script>

<template>
  <div class="bg-bg-200 p-6 rounded-xl min-h-[calc(100vh-140px)]">
    <div class="flex justify-between mb-6">
      <h2 class="text-2xl font-bold text-white">{{ isEdit ? '编辑商品' : '发布新商品' }}</h2>
      <el-button @click="router.back()">返回列表</el-button>
    </div>

    <el-form :model="form" label-position="top" class="max-w-5xl">
      
      <div class="bg-bg-300 p-6 rounded-lg mb-6 border border-bg-100">
        <h3 class="text-lg font-bold text-white mb-4 border-l-4 border-primary-100 pl-3">基础信息</h3>
        
        <el-form-item label="商品名称" required>
          <el-input v-model="form.name" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="副标题">
          <el-input v-model="form.subTitle" placeholder="请输入卖点/副标题" />
        </el-form-item>
        
        <div class="grid grid-cols-2 gap-6">
          <el-form-item label="商品分类" required>
            <el-cascader
              v-model="form.categoryId"
              :options="categoryOptions"
              :props="{ value: 'id', label: 'name', children: 'children', emitPath: false }"
              placeholder="请选择分类"
              class="w-full"
              clearable
            />
          </el-form-item>
          
          <el-form-item label="商品品牌" required>
            <el-select v-model="form.brandId" placeholder="请选择品牌" class="w-full" clearable filterable>
               <el-option 
                 v-for="item in brandOptions" 
                 :key="item.id" 
                 :label="item.name" 
                 :value="item.id" 
               />
            </el-select>
          </el-form-item>
        </div>

        <div class="grid grid-cols-2 gap-6 mt-4">
           <el-form-item label="基础价格">
             <el-input-number v-model="form.price" class="!w-full" :min="0" :precision="2" />
           </el-form-item>
           <el-form-item label="总库存">
             <el-input-number v-model="form.stock" class="!w-full" :disabled="skuList.length > 0" />
           </el-form-item>
        </div>

        <el-form-item label="商品主图" required class="mt-4">
          <el-upload
            class="avatar-uploader"
            :action="uploadAction"
            :headers="uploadHeaders"
            :show-file-list="false"
            :on-success="handlePicSuccess"
          >
            <img v-if="form.pic" :src="form.pic" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
      </div>

      <div class="bg-bg-300 p-6 rounded-lg mb-6 border border-bg-100">
        <div class="flex justify-between items-center mb-4 border-l-4 border-primary-100 pl-3">
            <h3 class="text-lg font-bold text-white">规格 / SKU</h3>
            <el-button v-if="specDefinitions.length < 3" type="primary" link :icon="CirclePlus" @click="addSpec">添加规格项</el-button>
        </div>
        
        <div v-for="(spec, idx) in specDefinitions" :key="idx" class="mb-4 p-4 bg-bg-200 rounded relative group">
           <el-button 
             v-if="specDefinitions.length > 1"
             class="absolute right-2 top-2 opacity-0 group-hover:opacity-100 transition" 
             type="danger" 
             link 
             :icon="Delete" 
             @click="removeSpec(idx)"
           >删除该行</el-button>

           <div class="flex items-center gap-4 mb-2">
              <span class="text-white font-bold text-sm">规格名:</span>
              <el-input v-model="spec.name" size="small" class="!w-32" placeholder="例: 颜色"/>
              <el-button size="small" type="primary" link :icon="Plus" @click="addSpecValue(idx)">添加值</el-button>
           </div>
           <div class="flex flex-wrap gap-2">
              <el-tag 
                v-for="(val, vIdx) in spec.values" 
                :key="vIdx" 
                closable 
                effect="dark"
                @close="removeSpecValue(idx, vIdx)"
              >
                {{ val }}
              </el-tag>
           </div>
        </div>

        <el-table :data="skuList" border v-if="skuList.length > 0">
           <el-table-column 
             v-for="(spec, idx) in specDefinitions" 
             :key="idx" 
             :label="spec.name || '规格'"
           >
             <template #default="{ row }">
                {{ row[`sp${idx+1}`] }}
             </template>
           </el-table-column>

           <el-table-column label="销售价格" width="160">
              <template #default="{ row }">
                 <el-input-number v-model="row.price" size="small" :precision="2" :controls="false" class="!w-full"/>
              </template>
           </el-table-column>
           <el-table-column label="库存" width="160">
              <template #default="{ row }">
                 <el-input-number v-model="row.stock" size="small" :controls="false" class="!w-full" @change="calcTotalStock"/>
              </template>
           </el-table-column>
           <el-table-column label="SKU编码">
              <template #default="{ row }">
                 <el-input v-model="row.skuCode" size="small" placeholder="自定义编码" />
              </template>
           </el-table-column>
        </el-table>
        <div v-else class="text-gray-400 text-sm text-center py-4">
           请先添加规格名及规格值，系统将自动生成 SKU 表格
        </div>
      </div>

      <div class="bg-bg-300 p-6 rounded-lg mb-6 border border-bg-100">
        <h3 class="text-lg font-bold text-white mb-4 border-l-4 border-primary-100 pl-3">图文详情</h3>
        <el-input 
          v-model="form.detailHtml" 
          type="textarea" 
          :rows="8" 
          placeholder="请输入 HTML 内容..." 
        />
      </div>

      <div class="flex justify-end pb-10">
         <el-button size="large" @click="router.back()">取消</el-button>
         <el-button type="primary" size="large" color="#3D5A80" class="w-32" @click="onSubmit">
            {{ isEdit ? '保存修改' : '立即发布' }}
         </el-button>
      </div>
    </el-form>
  </div>
</template>

<style scoped>
:deep(.el-input__wrapper), :deep(.el-textarea__inner) {
  background-color: #1f2b3e; 
  box-shadow: 0 0 0 1px #374357;
  color: #e0e0e0;
}
:deep(.el-input-number__decrease), :deep(.el-input-number__increase) {
  background: #374357;
  border-color: #4B5563;
  color: white;
}
:deep(.el-table) {
  --el-table-bg-color: #1f2b3e;
  --el-table-tr-bg-color: #1f2b3e;
  --el-table-header-bg-color: #2d3e52;
  --el-table-border-color: #374357;
  --el-table-text-color: #e0e0e0;
  --el-table-header-text-color: #ffffff;
}
:deep(.el-table--enable-row-hover .el-table__body tr:hover > td.el-table__cell) {
  background-color: #2d3e52;
}
:deep(.el-select__wrapper) {
  background-color: #1f2b3e;
  box-shadow: 0 0 0 1px #374357;
}
:deep(.el-select__placeholder) {
  color: #a8abb2;
}

.avatar-uploader .el-upload {
  border: 1px dashed #4B5563; 
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  background-color: #1f2b3e; 
  transition: .3s;
}
.avatar-uploader .el-upload:hover {
  border-color: #409EFF;
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 140px; 
  height: 140px;
  text-align: center;
  line-height: 140px;
}
.avatar {
  width: 140px;
  height: 140px;
  display: block;
  object-fit: cover;
}
</style>