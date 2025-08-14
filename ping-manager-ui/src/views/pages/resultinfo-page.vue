<!--
  Licensed to the bbcdabao Team under one or more contributor license agreements.
  See the NOTICE file distributed with this work for additional information
  regarding copyright ownership. The bbcdabao Team licenses this file to you under
  the Apache License, Version 2.0 (the "License"); you may not use this file except
  in compliance with the License. You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software distributed
  under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific language
  governing permissions and limitations under the License.
-->

<template>
  <div>
    <el-card class="custom-shadow mgb6" shadow="hover">
      <template #header>
        <div class="content-title">{{ '任务结果管理' }}</div>
      </template>
      <div class="this-card">
        <el-descriptions
          style="width: 100%;"
          :column="1"
          border
        >
          <el-descriptions-item
            :label="'结果过滤'"
            label-align="right"
            align="left"
            width="200px"
          >
            <el-input
              v-model="filterTextInf"
              clearable
              placeholder="请输入部分任务名"
            />
          </el-descriptions-item>
          <el-descriptions-item
            :label="'结果总数'"
            label-align="right"
            align="left"
            width="200px"
          >
            {{ resultDetails.length }}
          </el-descriptions-item>
        </el-descriptions>
        <el-table
          :data="filterDetails"
          style="width: 100%; margin-top: 10px;"
        >
          <el-table-column prop="taskName" :label="t('taskName')" sortable />
          <el-table-column width="150">
            <template #header>
              <span>{{ t('operation') }}:</span>
              <el-button
                type="danger"
                size="small"
                @click="handleBatchDelete"
                style="margin-left: 8px"
              >
                <el-icon>
                  <Delete />
                </el-icon>
              </el-button>
            </template>
            <template #default="{ row }">
              <el-checkbox
                v-model="row.ischeck"
                size="small"
              />
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-card>
  </div>
</template>
<script setup lang="ts">
import {
  ref,
  watch,
  computed,
  onMounted,
  onBeforeUnmount
} from 'vue';
import {
  ElMessage
} from 'element-plus';
import {
  useI18n
} from 'vue-i18n';
import {
  getResultDetailInfos,
  deleteResultDetailInfos
} from '@/api';
import {
  promptYesOrNo
} from '@/utils/dialog-inputs';
import type {
  ResultDetailInfo
} from '@/types/result-sub';
import dayjs from 'dayjs';

const { t } = useI18n();

const filterTextInf = ref('');

const filterDetails = computed(() => {
  if (!filterTextInf.value.trim()) return resultDetails.value;
  return resultDetails.value.filter(item =>
    item.taskName?.toLowerCase().includes(filterTextInf.value.trim().toLowerCase())
  );
});

const resultDetails = ref<ResultDetailInfo[]>([]);
const loadResultDetailInfos = async () => {
  const resData = await getResultDetailInfos(null);
  resultDetails.value = resData.map(item => ({
    ...item,
    ischeck: false
  }));
};

const handleBatchDelete = async () => {
  const deleteTasks: string[] = [];
  resultDetails.value.forEach(task => {
    if (task.ischeck) {
      deleteTasks.push(task.taskName);
    }
  });
  if (deleteTasks.length <= 0) {
    return;
  }
  const confirmed = await promptYesOrNo(t, t('deleteTask') + ' : ' +  deleteTasks.join(', '));
  if (confirmed) {
    for (const taskName of deleteTasks) {
      try {
        await deleteResultDetailInfos(taskName);
      } catch (error) {
        ElMessage.warning('delete error:' + taskName);
      }
    }
    filterTextInf.value = '';
    await loadResultDetailInfos();
  }
};

onMounted(async() => {
  await loadResultDetailInfos();
});
onBeforeUnmount(() => {
});
</script>
<style scoped>
.this-card {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
}
</style>