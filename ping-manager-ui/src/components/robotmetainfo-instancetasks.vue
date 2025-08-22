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
<div class="instancetasks-info">
  <div style="font-size: 12px; font-weight: bold;">
    {{ '哨兵任务分配:' }}
  </div>
  <div class="interval-line-primary" />
  <div
    :style="{ height: robotRuninfoInstanceTasks.instanceTasksCount * 70 + 'px', width: '100%' }"
    ref="chartRef"
  >
  </div>
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
  useRobotRuninfoInstanceTasksStore
} from '@/store/robotruninfo-instancetasks';
import type {
  RobotGroupInstanceTaskInfo
} from '@/types/robot-sub';

import * as echarts from 'echarts';
import dayjs from 'dayjs';

import pingTask from '@/assets/img/pingTask.png';
import pingRobot from '@/assets/img/pingRobot.png';
import pingGroup from '@/assets/img/pingGroup.png';

const preloadImages = (...urls: string[]) => {
  urls.forEach(url => {
    const img = new Image();
    img.src = url;
  });
};
preloadImages(pingRobot, pingGroup, pingTask);

const props = defineProps<{
  robotGroupName: string;
}>();

const { t } = useI18n();
const robotRuninfoInstanceTasks = useRobotRuninfoInstanceTasksStore();

const elColorSuccessLight9 = getComputedStyle(document.documentElement).getPropertyValue('--el-color-success-light-9').trim();
const elColorSuccess = getComputedStyle(document.documentElement).getPropertyValue('--el-color-success').trim();
const elTextColorSuccess = getComputedStyle(document.documentElement).getPropertyValue('--el-text-color-success').trim();

const elColorPrimaryLight9 = getComputedStyle(document.documentElement).getPropertyValue('--el-color-primary-light-9').trim();
const elColorPrimary = getComputedStyle(document.documentElement).getPropertyValue('--el-color-primary').trim();
const elTextPrimarySuccess = getComputedStyle(document.documentElement).getPropertyValue('--el-text-color-primary').trim();

const elColorInfoLight9 = getComputedStyle(document.documentElement).getPropertyValue('--el-color-info-light-9').trim();
const elColorInfo = getComputedStyle(document.documentElement).getPropertyValue('--el-color-info').trim();
const elTextInfoSuccess = getComputedStyle(document.documentElement).getPropertyValue('--el-text-color-info').trim();

const chartOption = {
  tooltip: {
    trigger: 'item',
    triggerOn: 'mousemove'
  },
  grid: {
    top: 0,
    bottom: 0,
    left:0,
    right: 0
  },
  series: [{
    type: 'tree',
    top: 0,
    data: [
    ],
    symbolSize: 32,
    label: {
    formatter: (params: any) => {
      return `{${params.data.type}|${params.data.name}}`;
    },
    rich: {
      robotStyle: {
        backgroundColor: elColorSuccessLight9,
        borderColor: elColorSuccess,
        borderWidth: 2,
        color: elTextColorSuccess,
        padding: [8, 8],
        borderRadius: 4
      },
      taskStyle: {
        backgroundColor: elColorPrimaryLight9,
        borderColor: elColorPrimary,
        borderWidth: 2,
        color: elTextPrimarySuccess,
        padding: [8, 8],
        borderRadius: 4
      },
      robotgroupStyle: {
        backgroundColor: elColorInfoLight9,
        borderColor: elColorInfo,
        borderWidth: 2,
        color: elTextInfoSuccess,
        padding: [8, 8],
        borderRadius: 4
      }
    },
      position: 'left',
      verticalAlign: 'middle',
      align: 'right',
      fontSize: 12,
      distance: 10
    },
    leaves: {
      label: {
        position: 'right',
        verticalAlign: 'middle',
        align: 'left'
      }
    },
    emphasis: {
      focus: 'descendant'
    },
    expandAndCollapse: true,
    animationDuration: 550,
    animationDurationUpdate: 750
  }]
};

const chartRef = ref<HTMLDivElement | null>(null);
let chartInstance: echarts.ECharts | null = null;
let obserInstance: ResizeObserver | null = null;
const resizeHandler = () => {
  chartInstance?.resize();
};
const initChart = () => {
  if (!chartRef.value) return;
  chartInstance = echarts.init(chartRef.value);
  chartInstance.setOption(chartOption);
  obserInstance = new ResizeObserver(() => {
    chartInstance?.resize();
  });
  obserInstance.observe(chartRef.value);
  window.addEventListener('resize', resizeHandler);
};
const clseChart = async () => {
  obserInstance?.unobserve(chartRef.value);
  obserInstance?.disconnect();
  obserInstance = null;
  chartInstance?.dispose();
  chartInstance = null;
  window.removeEventListener('resize', resizeHandler);
};



const getChartData = () => {
  const taskDatas: Record<
    string,
    { name: string; symbol:string; type: string; children: { name: string; symbol:string; type: string; children: any[] }[] }
  > = {};
  Object.values(robotRuninfoInstanceTasks.instanceTasks).forEach(instanceTask => {
    let taskData = taskDatas[instanceTask.robotUUID];
    if (!taskData) {
      taskData = {
        name: instanceTask.robotUUID,
        symbol: `image://${pingRobot}`,
        type: 'robotStyle',
        children: []
      };
      taskDatas[instanceTask.robotUUID] = taskData;
    }
    taskData.children.push({
      name: instanceTask.taskName,
      symbol: `image://${pingTask}`,
      type: 'taskStyle',
      children: []
    });
  });
  return {
    name: props.robotGroupName,
    symbol: `image://${pingGroup}`,
    type: 'robotgroupStyle',
    children: Object.values(taskDatas)
  };
};

watch(
  () => props.robotGroupName,
  () => {
    robotRuninfoInstanceTasks.beginSource(props.robotGroupName);
  },
  {
    immediate: true
  }
);

const throttledUpdateChart = throttle(() => {
  if (chartInstance) {
    chartInstance.setOption({
      series: [{ data: [getChartData()] }]
    });
  }
}, 1000);
watch(
  () => robotRuninfoInstanceTasks.instanceTasks,
  () => {
    throttledUpdateChart();
  },
  {
    immediate: true,
    deep: true
  }
);

onMounted(() => {
  initChart();
});
onBeforeUnmount(() => {
  robotRuninfoInstanceTasks.closeSource();
  clseChart();
});
</script>
<style scoped>
.instancetasks-info {
  box-sizing: border-box;
  border: 2px solid var(--el-color-primary);
  color: var(--el-text-color-primary);
  background-color: var(--el-color-primary-light-9);
  border-radius: 6px;
  padding: 4px;
  width: 100%;
  text-align: left;
}
.instance-list {
  box-sizing: border-box;
  display: grid;
  width: 100%;
  row-gap: 6px;
  column-gap: 6px;
  grid-template-columns: repeat(auto-fill, minmax(200px, 300px));
}
.instance-item {
  box-sizing: border-box;
  display: flex;
  flex-direction: row;
  border: 2px solid var(--el-color-success);
  color: var(--el-text-color-primary);
  background-color: var(--el-color-success-light-9);
  font-weight: bold;
  width: 100%;
  height: 100%;
  border-radius: 6px;
  padding: 6px 6px;
  text-align: center;

  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.instance-item-master {
  box-sizing: border-box;
  display: flex;
  flex-direction: row;
  border: 4px solid var(--el-color-success);
  color: var(--el-text-color-primary);
  background-color: var(--el-color-success-light-7);
  font-weight: bold;
  width: 100%;
  height: 100%;
  border-radius: 6px;
  padding: 6px 6px;
  text-align: center;

  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.interval-line-primary {
  background-color: var(--el-color-primary);
  width: 100%;
  height: 2px;
  transform: scaleY(0.5);
  margin-top: 2px;
  margin-bottom: 2px;
}
.interval-line-success {
  background-color: var(--el-color-success);
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
  margin-top: 2px;
  margin-bottom: 2px;
}
</style>