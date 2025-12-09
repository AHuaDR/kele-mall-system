<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { showToast, type AddressEditInfo } from 'vant'

const router = useRouter()
const route = useRoute()

// 地区数据 (暂时留空，实际开发需引入 area-data)
const areaList = {
  province_list: { 110000: '北京市', 120000: '天津市' },
  city_list: { 110100: '北京市', 120100: '天津市' },
  county_list: { 110101: '东城区', 110102: '西城区' },
}

const searchResult = ref([])
const initInfo = ref<AddressEditInfo>({} as AddressEditInfo)

onMounted(() => {
  // 如果是编辑模式，回显数据
  if (route.query.id) {
    // 修复点：删除了 id 属性，解决 TS 报错
    initInfo.value = {
      name: '张三',
      tel: '13000000000',
      province: '北京市',
      city: '北京市',
      county: '东城区',
      addressDetail: '长安街1号',
      areaCode: '110101',
      isDefault: true,
    }
  }
})

const onSave = () => {
  showToast('保存成功')
  router.back()
}

const onDelete = () => {
  showToast('删除成功')
  router.back()
}
</script>

<template>
  <div class="bg-gray-50 min-h-screen">
    <van-nav-bar :title="route.query.id ? '编辑地址' : '新增地址'" left-arrow @click-left="router.back()" />
    
    <van-address-edit
      :area-list="areaList"
      :address-info="initInfo"
      show-postal
      show-delete
      show-set-default
      show-search-result
      :search-result="searchResult"
      :area-columns-placeholder="['请选择', '请选择', '请选择']"
      @save="onSave"
      @delete="onDelete"
    />
  </div>
</template>