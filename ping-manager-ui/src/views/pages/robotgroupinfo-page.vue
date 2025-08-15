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
        <div class="content-title">{{ '哨兵组织管理' }}</div>
      </template>
      <div class="this-card">
        <el-descriptions
          style="width: 100%;"
          :column="1"
          border
        >
          <el-descriptions-item
            :label="'选择查看'"
            label-align="right"
            align="left"
            width="200px"
          >
            <el-select
              v-model="selectedRobotGroupName"
              :placeholder="'选择哨兵组织可监控查看哨兵实例'"
              clearable
              filterable
            >
              <el-option
                v-for="item in robotgroupinfos"
                :key="item.robotGroupName"
                :label="item.robotGroupName"
                :value="item.robotGroupName"
              />
            </el-select>
          </el-descriptions-item>
          <el-descriptions-item
            :label="'组织总数'"
            label-align="right"
            align="left"
            width="200px"
          >
            {{ Object.keys(robotgroupinfo.robotGroups).length }}
          </el-descriptions-item>
        </el-descriptions>
        <el-table
          :data="nowRobotgroupinfos"
          style="width: 100%; margin-top: 10px;"
        >
          <el-table-column prop="robotGroupName" :label="'组织名称'" sortable>
            <template #default="{ row }">
              <div style="font-weight: bold;">
                {{ row.robotGroupName }}
              </div>
            </template>
          </el-table-column>
          <el-table-column :label="'组织描述'">
            <template #default="{ row }">
              {{ locale === 'zh' ? row.descriptionCn : row.descriptionEn }}
            </template>
          </el-table-column>
        </el-table>
        <div
          style="width: 100%; margin-top: 6px;"
          v-if="selectedRobotGroupName"
        >
          <robotmetainfo-instances
            :robotGroupName="selectedRobotGroupName"
          />
        </div>
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
  useRobotgroupinfoStore
} from '@/store/robotgroupinfo';
import type {
  RobotGroupInfo
} from '@/types/robot-sub';

import dayjs from 'dayjs';

import RobotmetainfoInstances from '@/components/robotmetainfo-instances.vue';
import RobotmetainfoInstancetasks from '@/components/robotmetainfo-instancetasks.vue';


const { t, locale } = useI18n();
const robotgroupinfo = useRobotgroupinfoStore();

const selectedRobotGroupName = ref<string | null>(null);

const robotgroupinfos = ref<RobotGroupInfo[]>([]);
watch(
  () => robotgroupinfo.robotGroups,
  (groups) => {
    robotgroupinfos.value = Object.values(groups);
  },
  { deep: false, immediate: true }
);

/**
 * 当前显示的组织列表
 */
const nowRobotgroupinfos = computed<RobotGroupInfo[]>(() => {
  if (!selectedRobotGroupName.value) {
    return robotgroupinfos.value;
  }
  const info = robotgroupinfo.robotGroups[selectedRobotGroupName.value];
  return info ? [info] : [];
});

onMounted(() => {
  robotgroupinfo.updateRobotgroups();
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

.instance-item {
  border: 1px solid var(--el-color-success);
  color: var(--el-text-color-primary);
  background-color: var(--el-color-success-light-7);
  padding: 2px;
  border-radius: 6px;
  width: 100%;
  height: 28px;
}
</style>