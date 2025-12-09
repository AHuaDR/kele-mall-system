<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { showToast, showConfirmDialog } from 'vant'

const router = useRouter()
const route = useRoute()

// 模拟订单数据
const order = ref<any>({
  id: '',
  status: '',
  statusText: '',
  address: {},
  goods: [],
  price: 0,
  freight: 0,
  total: 0,
  time: ''
})

// 倒计时 (30分钟)
const time = ref(30 * 60 * 1000)

onMounted(() => {
  const id = route.params.id as string
  // 模拟从后端拉取详情
  order.value = {
    id: id || 'ORD_20231129001',
    status: 'pending', // pending, shipped, completed
    statusText: '等待买家付款',
    address: {
      name: '张三',
      tel: '138****0000',
      detail: '浙江省杭州市西湖区文三路 123 号'
    },
    goods: [
      { id: 101, title: 'Michael Kors Mercer 中号皮质手提包', sku: '经典黑', price: 3006, num: 1, img: 'https://placehold.co/100x100/f2f4f8/bb2649?text=Bag' },
      { id: 102, title: 'Dior 智能复古手表', sku: '银色表带', price: 1599, num: 1, img: 'https://placehold.co/100x100/e0e0e0/000000?text=Watch' }
    ],
    price: 4605,
    freight: 0,
    total: 4605,
    time: '2023-11-29 14:30:00'
  }
})

const onPay = () => {
  showToast('支付成功')
  order.value.status = 'shipped'
  order.value.statusText = '商家已发货'
}

const onCancel = () => {
  showConfirmDialog({ title: '提示', message: '确认取消订单吗？' })
    .then(() => {
      showToast('订单已取消')
      router.back()
    })
}
</script>

<template>
  <div class="bg-pageBg min-h-screen pb-20">
    <van-nav-bar title="订单详情" left-arrow @click-left="router.back()" fixed placeholder />

    <div class="bg-gradient-to-r from-[#FF6B00] to-[#FF8F00] px-6 py-8 text-white">
      <div class="text-xl font-bold mb-1">{{ order.statusText }}</div>
      <div class="text-xs opacity-90 flex items-center gap-1" v-if="order.status === 'pending'">
        剩 <van-count-down :time="time" format="mm分ss秒" class="text-white font-bold" /> 自动关闭
      </div>
      <div class="text-xs opacity-90" v-else>感谢您对ColaMall的信任</div>
    </div>

    <div class="bg-white mx-3 -mt-4 rounded-xl p-4 shadow-sm relative z-10 flex gap-3 items-center mb-3">
      <div class="w-8 h-8 rounded-full bg-orange-100 flex items-center justify-center text-primary shrink-0">
        <van-icon name="location" />
      </div>
      <div>
        <div class="font-bold text-sm mb-1 text-textMain">
          {{ order.address.name }} <span class="text-textSub font-normal ml-2">{{ order.address.tel }}</span>
        </div>
        <div class="text-xs text-textSub leading-tight">{{ order.address.detail }}</div>
      </div>
    </div>

    <div class="bg-white mx-3 rounded-xl p-3 shadow-sm mb-3">
      <div v-for="item in order.goods" :key="item.id" class="flex gap-3 mb-3 last:mb-0">
        <img :src="item.img" class="w-20 h-20 rounded bg-gray-50 object-cover shrink-0" />
        <div class="flex-1 flex flex-col justify-between">
          <div>
            <div class="text-sm font-bold text-textMain line-clamp-2">{{ item.title }}</div>
            <div class="text-xs text-textSub mt-1 bg-gray-50 inline-block px-1 rounded">{{ item.sku }}</div>
          </div>
          <div class="flex justify-between items-end">
            <span class="text-textMain text-sm">¥{{ item.price }}</span>
            <span class="text-textSub text-xs">x{{ item.num }}</span>
          </div>
        </div>
      </div>
    </div>

    <div class="bg-white mx-3 rounded-xl p-3 shadow-sm mb-3 text-sm">
      <div class="flex justify-between mb-2 text-textSub">
        <span>商品总价</span>
        <span>¥{{ order.price }}</span>
      </div>
      <div class="flex justify-between mb-2 text-textSub">
        <span>运费</span>
        <span>¥{{ order.freight }}</span>
      </div>
      <div class="flex justify-between pt-2 border-t border-gray-50 items-center">
        <span class="font-bold text-textMain">实付款</span>
        <span class="text-primary text-lg font-bold">¥{{ order.total }}</span>
      </div>
    </div>

    <div class="bg-white mx-3 rounded-xl p-3 shadow-sm text-xs text-textSub space-y-2 mb-4">
      <div class="flex justify-between">
        <span>订单编号</span>
        <span class="text-textMain">{{ order.id }} <span class="text-primary ml-1">复制</span></span>
      </div>
      <div class="flex justify-between">
        <span>下单时间</span>
        <span>{{ order.time }}</span>
      </div>
      <div class="flex justify-between">
        <span>支付方式</span>
        <span>微信支付</span>
      </div>
    </div>

    <div class="fixed bottom-0 left-0 right-0 bg-white p-3 border-t flex justify-end gap-3 safe-area-inset-bottom z-50">
      <template v-if="order.status === 'pending'">
        <van-button round size="small" @click="onCancel">取消订单</van-button>
        <van-button round size="small" color="#FF6B00" @click="onPay">立即支付</van-button>
      </template>
      <template v-else-if="order.status === 'shipped'">
        <van-button round size="small" @click="router.push('/logistics/123')">查看物流</van-button>
        <van-button round size="small" color="#FF6B00" plain>确认收货</van-button>
      </template>
      <template v-else>
        <van-button round size="small">申请售后</van-button>
        <van-button round size="small" color="#FF6B00" plain>再次购买</van-button>
      </template>
    </div>
  </div>
</template>