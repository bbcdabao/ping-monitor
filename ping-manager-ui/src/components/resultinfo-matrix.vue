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
  <div
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
      <div
        style="width: 100%;" 
        v-if="resultStyle === 'MID'"
      >
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
      <div
        style="width: 100%;"
        v-else-if="resultStyle === 'MAX'"
      >
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
            :class="result.pingresult.success
              ? 'resultinfo-item-task-group-success'
              : 'resultinfo-item-task-group-fail'"
          >
            {{ robotgroupinfo.getGroupDesc(result.robotGroupName) }}
            <div class="interval-line-info" />
            {{ t('delayTitle') }} : {{ result.pingresult.delay }} ms
            <div class="interval-line-info" />
            {{ t('updateTime') }} : {{ dayjs(result.timestamp).format('YYYY-MM-DD HH:mm:ss') }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
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
import type {
  PingresultInfo
} from '@/types/result-sub';

import * as echarts from 'echarts';
import dayjs from 'dayjs';

const props = withDefaults(defineProps<{
  resultStyle?: 'MIN' | 'MID' | 'MAX'
}>(), {
  resultStyle: 'MAX'
})

const { t } = useI18n();
const resultinfo = useResultinfoStore();
const robotgroupinfo = useRobotgroupinfoStore();

const getResultinfoListClass = () => {
  switch (props.resultStyle) {
    case 'MIN': return 'resultinfo-list-min';
    case 'MID': return 'resultinfo-list-mid';
    case 'MAX': return 'resultinfo-list-max';
  }
  throw new Error('unknow resultStyle');
};
const getResultinfoItem = () => {
  switch (props.resultStyle) {
    case 'MIN': return '';
    case 'MID': return 'resultinfo-item';
    case 'MAX': return 'resultinfo-item';
  }
  throw new Error('unknow resultStyle');
};
const getPieClass = () => {
  switch (props.resultStyle) {
    case 'MIN': return 'pie-chart';
    case 'MID': return 'pie-chart';
    case 'MAX': return 'pie-chart';
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
</script>
<style scoped>
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
  box-sizing: border-box;
  display: flex;
  align-items: center;
  flex-direction: column;
  border: 2px solid var(--el-color-success);
  color: var(--el-text-color-primary);
  background-color: var(--el-color-success-light-9);
  font-weight: bold;
  width: 100%;
  height: 100%;
  border-radius: 6px;
  padding: 6px 2px;
  text-align: center;

  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.resultinfo-item-taskname {
  box-sizing: border-box;
  border: 1px solid var(--el-color-primary);
  color: var(--el-text-color-primary);
  background-color: var(--el-color-primary-light-7);
  padding: 4px;
  border-radius: 6px;
  width: 100%;
  height: 44px;
  margin-bottom: 4px;
}

.resultinfo-item-task-group {
  box-sizing: border-box;
  border: 1px solid var(--el-color-info);
  color: var(--el-text-color-primary);
  background-color: var(--el-color-info-light-7);
  padding: 4px;
  border-radius: 6px;
  width: 100%;
  height: calc(100% - 44px);
}

.resultinfo-item-task-group-success {
  box-sizing: border-box;
  border: 1px solid var(--el-color-success);
  color: var(--el-text-color-primary);
  background-color: var(--el-color-success-light-7);
  padding: 4px;
  border-radius: 6px;
  width: 100%;
  height: 60px;
  font-size: 10px;
  font-weight: normal;
  text-align: left;
  margin-top: 4px;
  margin-bottom: 4px;
}
.resultinfo-item-task-group-fail {
  box-sizing: border-box;
  border: 1px solid var(--el-color-danger);
  color: var(--el-text-color-primary);
  background-color: var(--el-color-danger-light-7);
  padding: 4px;
  border-radius: 6px;
  width: 100%;
  height: 60px;
  font-size: 10px;
  font-weight: normal;
  text-align: left;
  margin-top: 4px;
  margin-bottom: 4px;
}

.pie-chart {
  box-sizing: border-box;
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
</style>