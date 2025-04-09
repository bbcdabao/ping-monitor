<template>
  <el-config-provider :locale="zhCn">
    <router-view />
  </el-config-provider>
</template>
<script setup lang="ts">
import { onMounted } from 'vue';
import { useThemeWatcher } from '@/store/theme';
import zhCn from 'element-plus/es/locale/lang/zh-cn';

onMounted(() => {
  useThemeWatcher();
});
</script>
<style>
@import './assets/css/main.css';
</style>
