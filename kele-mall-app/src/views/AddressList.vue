<script setup lang="ts">
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()
const chosenAddressId = ref('1')

const list = ref([
  { id: '1', name: '张三', tel: '138****0000', address: '浙江省杭州市西湖区文三路 123 号', isDefault: true },
  { id: '2', name: '李四', tel: '139****9999', address: '上海市浦东新区陆家嘴环路 100 号', isDefault: false },
])

const onAdd = () => router.push('/address/edit')
const onEdit = (item: any) => router.push(`/address/edit?id=${item.id}`)

// 核心：选中地址的回调
const onSelect = (item: any) => {
  if (route.query.from === 'checkout') {
    // 如果是从结算页来的，选完跳回去，并带上 ID
    router.replace(`/checkout?addressId=${item.id}`)
  }
}
</script>

<template>
  <div class="bg-pageBg min-h-screen">
    <van-nav-bar title="收货地址" left-arrow @click-left="router.back()" />
    <van-address-list
      v-model="chosenAddressId"
      :list="list"
      default-tag-text="默认"
      @add="onAdd"
      @edit="onEdit"
      @select="onSelect" 
    />
  </div>
</template>