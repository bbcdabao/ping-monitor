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
        <div class="content-title">
          {{ t('resultMonitor') }}
          <div class="title-right">
            {{ t('control') }} :
            <el-switch
              v-model="showControl"
              size="small"
            />
            {{ t('chart') }} :
            <el-switch
              v-model="showScatter"
              size="small"
            />
            {{ t('style') }} :
            <el-select
              style="width: 70px;"
              size="small"
              v-model="resultStyle"
              placeholder="none"
            >
              <el-option
                v-for="item in resultStyleOpt"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </div>
        </div>
      </template>
      <div class="this-card">
        <el-descriptions
          v-if="showControl"
          style="width: 100%; margin-bottom: 10px;"
          :column="1"
          border
        >
          <el-descriptions-item
            label-align="right"
            align="left"
            width="200px"
          >
            <template #label>
              <div style="display: flex; justify-content: flex-end; align-items: center; gap: 6px;">
                 {{ getControlTitle() }}
              </div>
            </template>
            <el-select
              v-model="indexTaskName" 
              style="width: 100%"
              clearable
              filterable
            >
              <el-option
                v-for="item in resultDetails"
                :key="item.taskName"
                :label="item.taskName"
                :value="item.taskName"
              />
            </el-select>
          </el-descriptions-item>
        </el-descriptions>
        <div class="interval-line" />
        <vid
          v-if="showScatter"
          style="width: 100%;"
        >
          <resultinfo-scatter
            v-if="indexTaskName"
          />
          <resultinfo-canvas-smoothie
            v-else
          />
          <div class="interval-line" />
        </vid>
        <resultinfo-matrix
          v-if="initShow"
          style="margin-top: 10px;"
          :resultStyle="resultStyle"
        />
        <div
          v-else
          style="width: 100%; margin-top: 20px">
          <el-skeleton :rows="10" animated />
        </div>
      </div>
    </el-card>
  </div>
</template>
<script setup lang="ts">
import {
  ref,
  watch,
  onMounted,
  onBeforeUnmount
} from 'vue';
import {
  useI18n
} from 'vue-i18n';
import {
  throttle
} from 'lodash';
import {
  useResultinfoStore
} from '@/store/resultinfo';
import {
  useRobotgroupinfoStore
} from '@/store/robotgroupinfo';
import {
  getResultDetailInfos
} from '@/api';
import type {
  PingresultInfo,
  ResultDetailInfo
} from '@/types/result-sub';
import dayjs from 'dayjs';

import ResultinfoCanvasSmoothie from '@/components/resultinfo-canvas-smoothie.vue';
import ResultinfoScatter from '@/components/resultinfo-scatter.vue';
import ResultinfoMatrix from '@/components/resultinfo-matrix.vue';

const { t, locale } = useI18n();
const resultinfo = useResultinfoStore();
const robotgroupinfo = useRobotgroupinfoStore();

const initShow = ref(false);
const minWidth = ref(150);

const DEFAULT_RESULT_STYLE = "MAX";
const resultStyle = ref(localStorage.getItem('resultStyle') || DEFAULT_RESULT_STYLE);
watch(resultStyle, (newVal) => {
  localStorage.setItem('resultStyle', String(newVal));
})
const resultStyleOpt = [
  {
    value: 'MIN',
    label: 'Min',
  },
  {
    value: 'MID',
    label: 'Mid',
  },
  {
    value: 'MAX',
    label: 'Max',
  }
]

const showControl = ref(
  localStorage.getItem('showControl') !== 'false'
)
watch(showControl, (newVal) => {
  localStorage.setItem('showControl', String(newVal))
})
const getControlTitle = () => {
  return `( ${t('total')} : ${Object.keys(resultinfo.results).length} ) ${t('selectShow')} :`;
};

const showScatter = ref(
  localStorage.getItem('showScatter') !== 'false'
)
watch(showScatter, (newVal) => {
  localStorage.setItem('showScatter', String(newVal))
})

/**
 *  Choose a gathering place
 *  选择聚集观测
 */
const indexTaskName = ref('');
watch(indexTaskName, (newValue, oldValue) => {
  resultinfo.beginSource(indexTaskName.value);
})
const resultDetails = ref<ResultDetailInfo[]>([]);
const loadResultDetailInfos = async () => {
  resultDetails.value = await getResultDetailInfos(null);
};

onMounted(() => {
  robotgroupinfo.updateRobotgroups();
  resultinfo.beginSource(indexTaskName.value);
  loadResultDetailInfos();
  setTimeout(() => {
    initShow.value = true;
  }, 1000);
});
onBeforeUnmount(() => {
  resultinfo.closeSource();
});
</script>
<style scoped>
.this-card {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
}
.title-right {
  width: 330px;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 10px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.bottom-style {
  margin-left: 0px;
  width: 120;
  font-size: 14px;
  height: 28px;
}
.interval-line {
  background-color: var(--el-color-info);
  width: 100%;
  height: 2px;
  transform: scaleY(0.5);
}
</style>