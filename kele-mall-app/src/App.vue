<script setup lang="ts">
import { useRoute } from 'vue-router'
const route = useRoute()
</script>

<template>
  <router-view />

  <van-tabbar 
    v-if="!route.meta.hideTabbar" 
    route fixed placeholder safe-area-inset-bottom 
    active-color="#FF6B00" inactive-color="#999"
  >
    <van-tabbar-item replace to="/" icon="wap-home-o">首页</van-tabbar-item>
    <van-tabbar-item replace to="/category" icon="apps-o">分类</van-tabbar-item>
    <van-tabbar-item replace to="/cart" icon="cart-o">购物车</van-tabbar-item>
    <van-tabbar-item replace to="/user" icon="user-o">我的</van-tabbar-item>
  </van-tabbar>
</template>

<style>
body {
  background-color: #F2F4F8; 
  margin: 0;
  -webkit-font-smoothing: antialiased;
  color: #1A1A1A;
}
.van-nav-bar::after { display: none; }
</style>