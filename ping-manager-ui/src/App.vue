<!-- Copyright 2025 bbcdabao Team -->

<template>
  <el-config-provider>
    <router-view />
  </el-config-provider>
</template>
<script setup lang="ts">

import { onMounted } from 'vue';
import { useThemeWatcher } from '@/store/theme';
import emitter from '@/event-bus';
import { useI18n } from 'vue-i18n';

const { t } = useI18n();

emitter.on('go-login', () => {
});

onMounted(() => {
  useThemeWatcher();
});
</script>
<style>
@import '@/assets/css/main.css';
</style>