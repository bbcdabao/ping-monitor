<!-- Copyright 2025 bbcdabao Team -->

<template>
  <div>
    <el-card class="custom-shadow mgb6" shadow="hover">
      <template #header>
        <div class="content-title">结果监控</div>
      </template>
      <div class="this-card">
        <div class="task-list">
          <div
            v-for="(record, taskName) in resultinfo.results"
            :key="taskName"
            class="task-item"
          >
            {{ taskName }}
            <div
              v-for="(result, robotGroupName) in record.pingresults"
              :key="robotGroupName"
              style="font-size: 14px;"
            >
              {{ robotGroupName }} --- {{ result.success }} --- {{ result.delay }} --- {{ result.info }}
            </div>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>
<script setup lang="ts">
import {
  onMounted,
  onUnmounted
} from 'vue';
import {
  useI18n
} from 'vue-i18n';
import {
  useResultinfoStore
} from '@/store/resultinfo';

const { t } = useI18n();
const resultinfo = useResultinfoStore();

onMounted(() => {
  resultinfo.beginSource();
});
onUnmounted(() => {
  resultinfo.closeSource();
});
</script>
<style scoped>
.this-card {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
}
.bottom-style {
  margin-left: 0px;
  width: 120;
  font-size: 14px;
  height: 28px;
}
.task-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.task-item {
  padding: 6px 12px;
  font-size: 20px;
  border: 1px solid #ccc;
  border-radius: 4px;
}
</style>