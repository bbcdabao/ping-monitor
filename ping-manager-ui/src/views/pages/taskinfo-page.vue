<!-- Copyright 2025 bbcdabao Team -->

<template>
  <div>
    <el-card class="custom-shadow mgb20" shadow="hover">
      <template #header>
        <div class="content-title">任务列表</div>
      </template>
      <div class="this-card">
        <div>

        </div>
      </div>
    </el-card>
  </div>
</template>
<script setup lang="ts">
import {
  useI18n
} from 'vue-i18n';

const { t } = useI18n();

</script>
<style scoped>
.this-card {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
}

</style>