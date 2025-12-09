<script setup lang="ts">
// 不需要任何 import，全靠路由控制
</script>

<template>
  <router-view />
</template>

<style>
/* 确保 body 占满全屏并应用深色背景 */
html, body, #app {
  height: 100%;
  margin: 0;
  padding: 0;
  background-color: #0F1C2E; /* 强制背景色: bg-100 */
}
</style>