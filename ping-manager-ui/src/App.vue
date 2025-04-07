<template>
  <el-config-provider :locale="zhCn">
    <router-view />
  </el-config-provider>
</template>
<script setup lang="ts">
import zhCn from 'element-plus/es/locale/lang/zh-cn';
import { ElConfigProvider } from 'element-plus';
import { useThemeWatcher } from '@/store/theme';
import { onMounted } from 'vue';

onMounted(() => {
  useThemeWatcher();
});

</script>
<style>
@import './assets/css/main.css';
</style>
