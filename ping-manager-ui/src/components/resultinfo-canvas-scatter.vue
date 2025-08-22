<template>
  <canvas ref="canvasData" style="border:1px solid #000000; margin-bottom: 10px;"></canvas>
</template>

<script lang="ts" setup>
import {
  ref,
  watch,
  onMounted,
  onBeforeUnmount
} from 'vue';
import {
  useResultinfoStore
} from '@/store/resultinfo';
import {
  useRobotgroupinfoStore
} from '@/store/robotgroupinfo';

import scatterPointe from '@/assets/img/scatter-point-e.png';
const scatterPointeImg = new Image();
scatterPointeImg.src = scatterPointe;

const resultinfo = useResultinfoStore();
const robotgroupinfo = useRobotgroupinfoStore();

const subMs = (60 * 1000 * 10);
const exbMs = subMs / 10;

let idxMs = new Date().getTime();
idxMs = idxMs - (idxMs % exbMs);
let begMs = idxMs - subMs;
let endMs = idxMs + exbMs;
let allMs = endMs - begMs;
const updateScatterParam = () => {
  idxMs = new Date().getTime();
  begMs = idxMs - subMs;
  endMs = idxMs + exbMs;
  allMs = endMs - begMs;
};

const drawPoints = (timestamp: number, delay: number) => {
  let nowMs = timestamp - begMs;
  let xAxis = (setWt * nowMs) / allMs;

  let yAxis = (delay * setHt) / 30000;

  if (xAxis >= setWt) {
    updateScatterParam();

    const shtPx = (setWt * exbMs) / allMs;
    // 整体平移画布
    canvasDataCtx.drawImage(
      canvasData.value!,
      shtPx, 0, setWt - shtPx, setHt,
      0, 0, setWt - shtPx, setHt
    );

    // 填充右侧新空白
    canvasDataCtx.fillStyle = '#eee';
    canvasDataCtx.fillRect(setWt - shtPx, 0, shtPx, setHt);

    nowMs = timestamp - begMs;
    xAxis = (setWt * nowMs) / allMs;
  }

  // 绘制散点
  canvasDataCtx.drawImage(scatterPointeImg, xAxis, yAxis, 10, 10);
};


const canvasData = ref<HTMLCanvasElement | null>(null);
let canvasDataCtx: CanvasRenderingContext2D | null = null;
let setWt = 0;
let setHt = 0;
const initCanvasData = () => {
  if (!canvasData.value) return;

  // 获取 CSS 中的宽高
  setWt = canvasData.value.clientWidth;
  setHt = canvasData.value.clientHeight;

  // 设置 canvas 内部的像素大小，防止拉伸
  canvasData.value.width = setWt;
  canvasData.value.height = setHt;

  canvasDataCtx = canvasData.value.getContext('2d');
  if (!canvasDataCtx) return;

  canvasDataCtx.fillStyle = '#eee'
  canvasDataCtx.fillRect(0, 0, setWt, setHt);
};

watch(
  () => resultinfo.resultInfo,
  (newVal, oldVal) => {
    const resultInfo = newVal;
    const pingresultInfo = newVal.pingresultInfo;
    const pingresult = pingresultInfo.pingresult;
    const timestamp = pingresultInfo.timestamp;

    drawPoints(timestamp, pingresult.delay);

  },
  {
    deep: false
  }
);

const reloadCanvas = () => {
  // 重置时间窗口
  idxMs = new Date().getTime();
  idxMs = idxMs - (idxMs % exbMs);
  begMs = idxMs - subMs;
  endMs = idxMs + exbMs;
  allMs = endMs - begMs;

  // 重新初始化 canvas
  initCanvasData();
};

// 监听窗口宽度变化
const handleResize = () => {
  reloadCanvas();
};

onMounted(() => {
  initCanvasData();
  window.addEventListener('resize', handleResize);
});

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize);
});
</script>
