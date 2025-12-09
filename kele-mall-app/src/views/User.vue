<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import { showToast } from 'vant'

const router = useRouter()
const userStore = useUserStore()
const showRights = ref(false)

// 模拟从 B 端获取的会员权益配置
const vipRights = [
  { icon: 'points', title: '1.2倍积分', desc: '购物返积分倍率提升' },
  { icon: 'service', title: '专属客服', desc: '24小时优先接入' },
  { icon: 'logistics', title: '极速退款', desc: '售后审核绿色通道' },
  { icon: 'gift-o', title: '生日礼包', desc: '每年生日赠送大额券' },
]

const goAddress = () => {
  if(!userStore.isLogin) return router.push('/login')
  router.push('/address')
}

const goOrder = (tabIndex: number) => {
  if(!userStore.isLogin) return router.push('/login')
  router.push({ path: '/order', query: { tab: tabIndex } })
}

const showMsg = (msg: string) => showToast(msg)
</script>

<template>
  <div class="pb-20">
    <div class="bg-[#F6F7F9] pt-12 pb-6 px-6" @click="!userStore.isLogin && router.push('/login')">
      <div class="flex items-center gap-4">
        <van-image round width="64" height="64" :src="userStore.userInfo.avatar || 'https://placehold.co/64x64/dddddd/333333?text=User'" />
        <div>
          <div class="text-xl font-bold text-textMain">{{ userStore.isLogin ? userStore.userInfo.nickname : '点击登录' }}</div>
          
          <div v-if="userStore.isLogin" class="mt-2 inline-flex items-center gap-1 bg-gradient-to-r from-orange-400 to-red-500 text-white text-[10px] px-2 py-0.5 rounded-full cursor-pointer" @click.stop="showRights = true">
             <van-icon name="gem" />
             <span>黄金会员</span>
             <van-icon name="arrow" />
          </div>
          <div class="text-xs text-gray-400 mt-1" v-else>登录体验更多功能</div>
        </div>
      </div>
    </div>

    <div class="bg-white mx-4 -mt-4 rounded-xl p-4 shadow-sm relative z-10 mb-4">
       <div class="flex justify-between border-b pb-3 mb-3 text-sm" @click="goOrder(0)">
          <span class="font-bold text-textMain">我的订单</span>
          <span class="text-gray-400 text-xs">全部 ></span>
       </div>
       <div class="grid grid-cols-5 text-center">
          <div class="flex flex-col items-center gap-1" @click="goOrder(1)"><van-icon name="pending-payment" size="24" class="text-textMain" /><span class="text-xs text-gray-600">待付款</span></div>
          <div class="flex flex-col items-center gap-1" @click="goOrder(2)"><van-icon name="logistics" size="24" class="text-textMain" /><span class="text-xs text-gray-600">待发货</span></div>
          <div class="flex flex-col items-center gap-1" @click="goOrder(3)"><van-icon name="send-gift-o" size="24" class="text-textMain" badge="2" /><span class="text-xs text-gray-600">待收货</span></div>
          <div class="flex flex-col items-center gap-1" @click="showMsg('暂无评价')"><van-icon name="comment-o" size="24" class="text-textMain" /><span class="text-xs text-gray-600">评价</span></div>
          <div class="flex flex-col items-center gap-1" @click="router.push('/service')">
            <van-icon name="after-sale" size="24" class="text-textMain" />
            <span class="text-xs text-gray-600">售后</span>
          </div>
       </div>
    </div>

    <van-cell-group inset class="!mx-4 shadow-sm rounded-xl overflow-hidden">
      <van-cell title="收货地址" is-link icon="location-o" @click="goAddress" />
      <van-cell title="我的收藏" is-link icon="star-o" to="/favorites" />
      <van-cell title="我的评价" is-link icon="comment-o" to="/my-reviews" />
      <van-cell title="优惠券" is-link icon="coupon-o" value="2张可用" to="/coupon" />
      <van-cell title="联系客服" is-link icon="service-o" @click="showMsg('客服电话: 400-888-8888')" />
      <van-cell title="设置" is-link icon="setting-o" to="/settings" />
    </van-cell-group>

    <van-popup v-model:show="showRights" position="bottom" round closeable :style="{ height: '50%' }">
      <div class="p-6">
        <div class="text-center font-bold text-lg mb-6">黄金会员权益</div>
        <div class="grid grid-cols-2 gap-4">
           <div v-for="(right, i) in vipRights" :key="i" class="bg-gray-50 p-4 rounded-xl flex flex-col items-center text-center">
              <div class="w-10 h-10 rounded-full bg-orange-100 text-orange-500 flex items-center justify-center mb-2">
                 <van-icon :name="right.icon" size="20" />
              </div>
              <div class="font-bold text-sm text-textMain">{{ right.title }}</div>
              <div class="text-xs text-gray-400 mt-1">{{ right.desc }}</div>
           </div>
        </div>
      </div>
    </van-popup>
  </div>
</template>