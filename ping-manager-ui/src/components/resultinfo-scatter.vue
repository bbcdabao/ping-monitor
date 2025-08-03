<!-- Copyright 2025 bbcdabao Team -->

<template>
  <div
    style="height: 200px; width: 100%;"
    ref="chartRef"
  />
  <el-slider
    style="width: 100%; margin-bottom: 10px;"
    v-model="winSize"
    :min="10"
    :max="90"
    :step="10"
    show-stops
    :format-tooltip="sliderTooltip"
  />
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

import * as echarts from 'echarts';
import dayjs from 'dayjs';

const { t, locale } = useI18n();
const resultinfo = useResultinfoStore();
const robotgroupinfo = useRobotgroupinfoStore();

const DEFAULT_WIN_SIZE = 10
const winSize = ref(
  Number(localStorage.getItem('winSize') || DEFAULT_WIN_SIZE)
);
watch(winSize, (newVal) => {
  localStorage.setItem('winSize', String(newVal));
})
const sliderTooltip = (value: number) => {
  return `最近：${value} 分钟`
};

const elementColor = getComputedStyle(document.documentElement).getPropertyValue('--element-color').trim();
const chartOption = {
  animation: false,
  title: {
    text: '流动窗口拨测散点',
    left: 'left',
    textStyle: {
      fontSize: 12,
      fontWeight: 'normal',
      color: elementColor,
    }
  },
  tooltip: {
    trigger: 'item',
    formatter: (params) => {
      const [time, delay] = params.value;
      return `${t('timeSub')}: ${new Date(time).toLocaleTimeString()}<br/>
              ${t('delaySub')}: ${delay}ms<br/>
              ${t('nameSub')}: ${params.name}`;
    }
  },
  grid: {
    top: 30,
    bottom: 25,
    left:50,
    right: 60
  },
  xAxis: {
    type: 'time',
    name: '时间轴',
    nameLocation: 'start',
    nameGap: 10,
    axisLabel: {
      formatter: (value) => new Date(value).toLocaleTimeString(),
      margin: 16
    },
    splitLine: {
      show: false
    },
    axisLine: {
      lineStyle: {
        color: elementColor
      }
    },
  },
  yAxis: {
    type: 'value',
    name: '延迟 (ms)',
    min: 0,
    max: 10,
    position: 'right',
    axisLine: {
      show: true,
      lineStyle: {
        color: elementColor
      }
    },
    axisLabel: {
      show: true
    },
    splitLine: {
      show: false
    },
    axisTick: {
      show: true,
      length: 5,
      lineStyle: {
        width: 1
      }
    },
  },
  series: [
    {
      type: 'scatter',
      data: [],
      symbolSize: 7,
      itemStyle: {
        opacity: 0.6
      }
    }
  ]
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

const MAX_POINTS = 1500;
const resultInfos: Array<any> = [];
let firstTimestamp: number = Date.now();
let lastTimestamp: number = Date.now();
let maxDelay = 0;
const addPoint = (point: any) => {
  const nowDelay = point.value[1];
  if (maxDelay < nowDelay) {
    maxDelay = nowDelay + 1;
  }
  resultInfos.push(point);
  if (resultInfos.length > MAX_POINTS) {
    resultInfos.splice(0, resultInfos.length - MAX_POINTS);
  }
  if (resultInfos.length > 0) {
    firstTimestamp = resultInfos[0].value[0];
    lastTimestamp = point.value[0];
  }
};

const throttledUpdateChart = throttle(() => {
  if (chartInstance) {
    const now = Date.now();
    const windowSize = winSize.value * 60000;
    chartInstance.setOption({
      xAxis: {
        min: now - windowSize,
        max: now
      },
      yAxis: {max: maxDelay},
      series: [{ data: resultInfos }]
    });
  }
}, 1000);

let updateTimerId: ReturnType<typeof setInterval> | null = null;
const clrUpdateTimer = () => {
  if (updateTimerId === null) {
    return;
  }
  clearInterval(updateTimerId);
  updateTimerId = null;
};
const setUpdateTimer = async () => {
  if (updateTimerId !== null) {
    return;
  }
  updateTimerId = setInterval(() => {
    const now = Date.now();
    const windowSize = winSize.value * 60000;
    chartInstance.setOption({
      xAxis: {
        min: now - windowSize,
        max: now
      },
      yAxis: {max: maxDelay},
      series: [{ data: resultInfos }]
    });
  }, 1000);
};

watch(
  () => resultinfo.resultInfo,
  (newVal, oldVal) => {
    const resultInfo = newVal;
    const pingresultInfo = resultInfo.pingresultInfo;
    const pingresult = pingresultInfo.pingresult;
    const groupInfo = robotgroupinfo.robotGroups[pingresultInfo.robotGroupName];
    let groupDesc = pingresultInfo.robotGroupName;
    if (groupInfo) {
      groupDesc = locale.value === 'zh' ? groupInfo.descriptionCn : groupInfo.descriptionEn;
    }
    const nameSub = `${resultInfo.taskName} ( ${groupDesc} )`;
    const nowTimestamp = pingresultInfo.timestamp;
    addPoint({
      value: [nowTimestamp, pingresult.delay],
      name: nameSub,
      itemStyle: { color: pingresult.success ? 'green' : 'red' }
    });
  },
  {
    deep: true
  }
);

onMounted(() => {
  initChart();
  setUpdateTimer();
});
onBeforeUnmount(() => {
  clseChart();
  clrUpdateTimer();
});

</script>
<style scoped>

</style>