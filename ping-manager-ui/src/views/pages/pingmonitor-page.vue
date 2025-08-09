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
            {{ t('scatter') }} :
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
        <resultinfo-scatter
          v-if="showScatter"
        />
        <div
          v-if="initShow"
          :class="getResultinfoListClass()"
        >
          <div
            v-for="(record, taskName) in resultinfo.results"
            :key="taskName"
            :class="getResultinfoItem()"
          >
            <div
              :class="getPieClass()"
              :style="getPieStyle(record.pingresults)"
            />
            <div v-if="resultStyle==='MID'">
              <div class="resultinfo-item-taskname">
                <div style="font-size: 12px; font-weight: bold;">
                  {{ t('taskPingName') }}
                </div>
                <div class="interval-line-primary" />
                <div style="font-size: 10px; font-weight: normal; text-align: left;">
                  {{ taskName }}
                </div> 
              </div>
            </div>
            <div v-else-if="resultStyle==='MAX'">
              <div class="resultinfo-item-taskname">
                <div style="font-size: 12px; font-weight: bold;">
                  {{ t('taskPingName') }}
                </div>
                <div class="interval-line-primary" />
                <div style="font-size: 10px; font-weight: normal; text-align: left;">
                  {{ taskName }}
                </div>
              </div>
              <div class="resultinfo-item-task-group">
                <div style="font-size: 12px; font-weight: bold;">
                  {{ t('sentinelGroup') }}
                </div>
                <div class="interval-line-info" />
                <div
                  v-for="(result, robotGroupName) in record.pingresults"
                  :key="robotGroupName"
                  :class="result.pingresult.success?
                  'resultinfo-item-task-group-success' : 'resultinfo-item-task-group-fail'"
                >
                  {{ getGroupDesc(result.robotGroupName) }}
                  <div class="interval-line-info" />
                  {{ t('delayTitle') }} : {{ result.pingresult.delay }} ms
                  <div class="interval-line-info" />
                  {{ t('updateTime')}} : {{ dayjs(result.timestamp).format('YYYY-MM-DD HH:mm:ss') }}
                </div>
              </div>
            </div>
          </div>
        </div>
        <div v-else style="width: 100%; margin-top: 20px">
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

import ResultinfoScatter from '@/components/resultinfo-scatter.vue';

const { t, locale } = useI18n();
const resultinfo = useResultinfoStore();
const robotgroupinfo = useRobotgroupinfoStore();

const initShow = ref(false);

const minWidth = ref(150);

const isChartMin = ref(false);

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

const indexTaskName = ref('');
watch(indexTaskName, (newValue, oldValue) => {
  resultinfo.beginSource(indexTaskName.value);
})
const resultDetails = ref<ResultDetailInfo[]>([]);
const loadResultDetailInfos = async () => {
  resultDetails.value = await getResultDetailInfos(null);
};

const getGroupDesc = (groupDesc: string) => {
  const groupInfo = robotgroupinfo.robotGroups[groupDesc];
  let retGroupDesc = groupDesc;
  if (groupInfo) {
    retGroupDesc = locale.value === 'zh' ? groupInfo.descriptionCn : groupInfo.descriptionEn;
  }
  return retGroupDesc;
}

const getResultinfoListClass = () => {
  switch (resultStyle.value) {
    case 'MIN': return 'resultinfo-list-min';
    case 'MID': return 'resultinfo-list-mid';
    case 'MAX': return 'resultinfo-list-max';
  }
  throw new Error('unknow resultStyle');
};
const getResultinfoItem = () => {
  switch (resultStyle.value) {
    case 'MIN': return '';
    case 'MID': return 'resultinfo-item';
    case 'MAX': return 'resultinfo-item';
  }
  throw new Error('unknow resultStyle');
};
const getPieClass = () => {
  switch (resultStyle.value) {
    case 'MIN': return 'pie-chart-min';
    case 'MID': return 'pie-chart-mid';
    case 'MAX': return 'pie-chart-mid';
  }
  throw new Error('unknow resultStyle');
};
const getPieStyle = (pingresults: Record<string, PingresultInfo>) => {
  const entries = Object.values(pingresults);
  const total = entries.length;
  let failureRate = 0;
  if (total > 0) {
    const failureCount = entries.filter(item => !item.pingresult.success).length;
    failureRate = Math.round((failureCount / total) * 100);
  }
  return {
    background: `conic-gradient(
      var(--el-color-danger) 0% ${failureRate}%,
      var(--el-color-success-light-5) ${failureRate}% 100%
    )`
  };
};

onMounted(() => {
  robotgroupinfo.updateRobotgroups();
  resultinfo.beginSource(indexTaskName.value);
  loadResultDetailInfos();
  setTimeout(() => {
    initShow.value = true;
  }, 2000);
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

.resultinfo-list-min {
  display: grid;
  width: 100%;
  row-gap: 16px;
  column-gap: 6px;
  grid-template-columns: repeat(auto-fill, minmax(70px, 1fr));
  justify-content: center;
}
.resultinfo-list-mid {
  display: grid;
  width: 100%;
  row-gap: 6px;
  column-gap: 6px;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  justify-content: center;
}
.resultinfo-list-max {
  display: grid;
  width: 100%;
  row-gap: 6px;
  column-gap: 6px;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  justify-content: center;
}

.resultinfo-item {
  display: flex;
  align-items: center;
  flex-direction: column;
  border: 2px solid var(--el-color-success);
  color: var(--el-text-color-primary);
  background-color: var(--el-color-primary-light-9);
  font-weight: bold;
  height: calc(100% - 16px);
  border-radius: 6px;
  padding: 6px 6px;
  text-align: center;

  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.resultinfo-item-taskname {
  border: 1px solid var(--el-color-primary);
  color: var(--el-text-color-primary);
  background-color: var(--el-color-primary-light-7);
  padding: 4px;
  border-radius: 6px;
  width: 180px;
  height: 34px;
  margin-bottom: 4px;
}

.resultinfo-item-taskdelay {
  border: 1px solid var(--el-color-warning);
  color: var(--el-text-color-primary);
  background-color: var(--el-color-warning-light-7);
  padding: 4px;
  border-radius: 6px;
  width: 180px;
  height: 32px;
  margin-bottom: 4px;
}

.resultinfo-item-task-group {
  border: 1px solid var(--el-color-info);
  color: var(--el-text-color-primary);
  background-color: var(--el-color-info-light-7);
  padding: 4px;
  border-radius: 6px;
  width: 180px;
  height: calc(100% - 56px);
}

.resultinfo-item-task-group-success {
  border: 1px solid var(--el-color-success);
  color: var(--el-text-color-primary);
  background-color: var(--el-color-success-light-7);
  padding: 4px;
  border-radius: 6px;
  width: 172px;
  height: 50px;
  font-size: 10px;
  font-weight: normal;
  text-align: left;
  margin-top: 4px;
  margin-bottom: 4px;
}

.resultinfo-item-task-group-fail {
  border: 1px solid var(--el-color-danger);
  color: var(--el-text-color-primary);
  background-color: var(--el-color-danger-light-7);
  padding: 4px;
  border-radius: 6px;
  width: 172px;
  height: 50px;
  font-size: 10px;
  font-weight: normal;
  text-align: left;
  margin-top: 4px;
  margin-bottom: 4px;
}

.pie-chart-min {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  border: 2px solid var(--el-color-success);
}
.pie-chart-mid {
  margin-bottom: 10px;
  width: 60px;
  height: 60px;
  border-radius: 50%;
  border: 2px solid var(--el-color-success);
}

.interval-line-primary {
  background-color: var(--el-color-primary);
  width: 100%;
  height: 2px;
  transform: scaleY(0.5);
  margin-top: 2px;
  margin-bottom: 2px;
}
.interval-line-info {
  background-color: var(--el-color-info);
  width: 100%;
  height: 2px;
  transform: scaleY(0.5);
  margin-top: 2px;
  margin-bottom: 2px;
}
.interval-line {
  background-color: var(--el-color-info);
  width: 100%;
  height: 2px;
  transform: scaleY(0.5);
  margin-bottom: 10px;
}
</style>