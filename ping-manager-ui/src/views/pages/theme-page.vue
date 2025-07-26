<!-- Copyright 2025 bbcdabao Team -->

<template>
  <div>
    <el-card class="custom-shadow mgb20" shadow="hover">
      <template #header>
        <div class="content-title">{{ $t('sysTheme') }}</div>
      </template>
      <div class="theme-list mgb20">
        <div
          class="theme-item"
          @click="setSystemTheme(value)"
          v-for="[key, value] in Object.entries(systemmap)"
          :key="key"
          :style="{ backgroundColor: getDefaultThemBackColor(value.bodyBgColor) }"
        >
          <div style="margin-bottom: 4px; font-weight: bolder;"> {{ t(key) }} </div>
          <div
            :style="{
              width: '100%',
              height: '12px',
              backgroundColor: value.headerBgColor,
              borderBottom: '1px solid ' + value.headerLineColor
            }"
          />
          <div
            :style="{
              width: '100%',
              height: '120px',
              border: '0',
              backgroundColor: value.bodyBgColor,
              display: 'flex',
              justifyContent: 'space-between'
            }"
          >
            <div
              :style="{
                width: '30%',
                height: '120px',
                backgroundColor: value.sidebarBgColor
              }"
            >
              <div
                :style="{
                  width: '100%',
                  height: '10%',
                  backgroundColor: value.sidebarBgColor
                }"
              />
              <div
                :style="{
                  width: '100%',
                  height: '10%',
                  backgroundColor: value.sidebarBgColor
                }"
              />
              <div
                :style="{
                  width: '100%',
                  height: '10%',
                  color: value.sidebarIndexColor,
                  fontSize: '11px',
                  fontWeight: 'bold',
                  backgroundColor: value.sidebarIndexBgColor
                }"
              >
                XXXXX
              </div>
            </div>
            <div
              :style="{
                width: '65%',
                height: '70%',
                margin: '6px',
                backgroundColor: value.cardbodyBgColor,
                borderRadius: '6px',
                border: '1px solid ' + value.cardbodyBdColor
              }"
            >
              <div style="font-size: 9px; text-align: left; margin-left: 5px;">
                xxxxxx
              </div>
              <div
                :style="{
                  width: '100%',
                  height: '1px',
                  backgroundColor: value.cardbodyBdColor,
                  marginTop: '1px'
                }"
              />
            </div>
            <div
              :style="{
                width: '8px',
                height: '100%',
                backgroundColor: value.scrollbarBgColor
              }"
            />
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>
<script setup lang="ts">
import {
  useI18n
} from 'vue-i18n';
import {
  useThemeStore
} from '@/store/theme';
import {
  ThemeConfig
} from '@/types/theme-config';

const { t } = useI18n();
const themeStore = useThemeStore();

const setSystemTheme = (data: any) => {
  console.info('theme:', data);
  themeStore.saveThemeConfig(data);
};

const invertColor = (hex: string) =>{
  hex = hex.replace(/^#/, '');
  if (hex.length === 3) {
    hex = hex.split('').map(char => char + char).join('');
  }
  const r = (255 - parseInt(hex.substring(0, 2), 16)).toString(16).padStart(2, '0');
  const g = (255 - parseInt(hex.substring(2, 4), 16)).toString(16).padStart(2, '0');
  const b = (255 - parseInt(hex.substring(4, 6), 16)).toString(16).padStart(2, '0');
  return `#${r}${g}${b}`;
}

const getDefaultThemBackColor = (hex: string): string => {
  return '#666666';
}

const getHighContrastColor = (hex: string): string => {
  hex = hex.replace(/^#/, '');
  if (hex.length === 3) {
    hex = hex.split('').map(char => char + char).join('');
  }
  const r = parseInt(hex.substring(0, 2), 16);
  const g = parseInt(hex.substring(2, 4), 16);
  const b = parseInt(hex.substring(4, 6), 16);
  const luminance = 0.299 * r + 0.587 * g + 0.114 * b;
  return luminance > 186 ? '#000000' : '#ffffff';
};

const systemmap: Record<string, ThemeConfig> = {
  'grayscaleTheme': {
    elColorPrimary: '#409eff',
    elColorSuccess: '#67c23a',
    elColorWarning: '#e6a23c',
    elColorDanger: '#f56c6c',
    elColorInfo: '#909399',
    nprogressBarColor: '#888888',
    nprogressSpinnerTopColor: '#888888',
    nprogressSpinnerleftColor: '#ffffff',
    headerColor: '#ffffff',
    headerBgColor: '#1a1a1a',
    headerLineColor: '#666666',
    bodyColor: '#ffffff',
    bodyBgColor: '#4d4d4d',
    scrollbarBgColor: '#1a1a1a',
    scrollbarIndexBgColor: '#4d4d4d',
    scrollbarIndexBdColor: '#4d4d4d',
    scrollbarIndexBgHoverColor: '#4d4d4d',
    sidebarColor: '#ffffff',
    sidebarBgColor: '#1a1a1a',
    sidebarIndexColor: '#ffffff',
    sidebarIndexBgColor: '#4d4d4d',
    sidebarIndexBeforeColor: '#999999',
    cardbodyColor: '#ffffff',
    cardbodyBdColor: '#4d4d4d',
    cardbodyBgColor: '#1a1a1a',
    elementColor: '#ffffff',
    elementBdColor: '#666666',
    elementBgColor: '#1a1a1a',
    elementIndexColor: '#ffffff',
    elementIndexBdColor: '#666666',
    elementIndexBgColor: '#4d4d4d',
    customShadowColor: '#999999'
  },
  'greentintTheme': {
    elColorPrimary: '#409eff',
    elColorSuccess: '#67c23a',
    elColorWarning: '#e6a23c',
    elColorDanger: '#f56c6c',
    elColorInfo: '#909399',
    nprogressBarColor: '#4caf50',
    nprogressSpinnerTopColor: '#4caf50',
    nprogressSpinnerleftColor: '#ffffff',
    headerColor: '#ffffff',
    headerBgColor: '#0d1f1a',
    headerLineColor: '#cccccc',
    bodyColor: '#ffffff',
    bodyBgColor: '#3d5f4a',
    scrollbarBgColor: '#0d1f1a',
    scrollbarIndexBgColor: '#3d5f4a',
    scrollbarIndexBdColor: '#3d5f4a',
    scrollbarIndexBgHoverColor: '#3d5f4a',
    sidebarColor: '#ffffff',
    sidebarBgColor: '#0d1f1a',
    sidebarIndexColor: '#ffffff',
    sidebarIndexBgColor: '#3d5f4a',
    sidebarIndexBeforeColor: '#00aa88',
    cardbodyColor: '#ffffff',
    cardbodyBdColor: '#3d5f4a',
    cardbodyBgColor: '#0d1f1a',
    elementColor: '#ffffff',
    elementBdColor: '#cccccc',
    elementBgColor: '#0d1f1a',
    elementIndexColor: '#ffffff',
    elementIndexBdColor: '#cccccc',
    elementIndexBgColor: '#3d5f4a',
    customShadowColor: '#00aa88'
  },
  'warmerTheme': {
    elColorPrimary: '#409eff',
    elColorSuccess: '#67c23a',
    elColorWarning: '#e6a23c',
    elColorDanger: '#f56c6c',
    elColorInfo: '#909399',
    nprogressBarColor: '#0a9393',
    nprogressSpinnerTopColor: '#0a9393',
    nprogressSpinnerleftColor: '#000000',
    headerColor: '#000000',
    headerBgColor: '#f5f0dc',
    headerLineColor: '#333333',
    bodyColor: '#000000',
    bodyBgColor: '#b4a278',
    scrollbarBgColor: '#f5f0dc',
    scrollbarIndexBgColor: '#b4a278',
    scrollbarIndexBdColor: '#b4a278',
    scrollbarIndexBgHoverColor: '#b4a278',
    sidebarColor: '#000000',
    sidebarBgColor: '#f5f0dc',
    sidebarIndexColor: '#000000',
    sidebarIndexBgColor: '#b4a278',
    sidebarIndexBeforeColor: '#ff6666',  // 反色 of #009999
    cardbodyColor: '#000000',
    cardbodyBdColor: '#b4a278',
    cardbodyBgColor: '#f5f0dc',
    elementColor: '#000000',
    elementBdColor: '#333333',
    elementBgColor: '#f5f0dc',
    elementIndexColor: '#000000',
    elementIndexBdColor: '#333333',
    elementIndexBgColor: '#b4a278',
    customShadowColor: '#ff6666'
  },
  'coolerTheme': {
    elColorPrimary: '#409eff',
    elColorSuccess: '#67c23a',
    elColorWarning: '#e6a23c',
    elColorDanger: '#f56c6c',
    elColorInfo: '#909399',
    nprogressBarColor: '#f56c6c',
    nprogressSpinnerTopColor: '#f56c6c',
    nprogressSpinnerleftColor: '#ffffff',
    headerColor: '#ffffff',
    headerBgColor: '#0a0f23',
    headerLineColor: '#cccccc',
    bodyColor: '#ffffff',
    bodyBgColor: '#4b5d87',
    scrollbarBgColor: '#0a0f23',
    scrollbarIndexBgColor: '#4b5d87',
    scrollbarIndexBdColor: '#4b5d87',
    scrollbarIndexBgHoverColor: '#4b5d87',
    sidebarColor: '#ffffff',
    sidebarBgColor: '#0a0f23',
    sidebarIndexColor: '#ffffff',
    sidebarIndexBgColor: '#4b5d87',
    sidebarIndexBeforeColor: '#009999',
    cardbodyColor: '#ffffff',
    cardbodyBdColor: '#4b5d87',
    cardbodyBgColor: '#0a0f23',
    elementColor: '#ffffff',
    elementBdColor: '#cccccc',
    elementBgColor: '#0a0f23',
    elementIndexColor: '#ffffff',
    elementIndexBdColor: '#cccccc',
    elementIndexBgColor: '#4b5d87',
    customShadowColor: '#009999'
  },
  'whiteTheme': {
    elColorPrimary: '#409eff',
    elColorSuccess: '#67c23a',
    elColorWarning: '#e6a23c',
    elColorDanger: '#f56c6c',
    elColorInfo: '#909399',
    nprogressBarColor: '#409eff',
    nprogressSpinnerTopColor: '#409eff',
    nprogressSpinnerleftColor: '#333333',
    headerColor: '#333333',
    headerBgColor: '#ffffff',
    headerLineColor: '#666666',
    bodyColor: '#333333',
    bodyBgColor: '#cccccc',
    scrollbarBgColor: '#ffffff',
    scrollbarIndexBgColor: '#666666',
    scrollbarIndexBdColor: '#666666',
    scrollbarIndexBgHoverColor: '#666666',
    sidebarColor: '#333333',
    sidebarBgColor: '#ffffff',
    sidebarIndexColor: '#409eff',
    sidebarIndexBgColor: '#cccccc',
    sidebarIndexBeforeColor: '#409eff',
    cardbodyColor: '#333333',
    cardbodyBdColor: '#cccccc',
    cardbodyBgColor: '#ffffff',
    elementColor: '#333333',
    elementBdColor: '#cccccc',
    elementBgColor: '#ffffff',
    elementIndexColor: '#409eff',
    elementIndexBdColor: '#e0e0e0',
    elementIndexBgColor: '#cccccc',
    customShadowColor: '#000000'
  }
};

</script>
<style scoped>
.theme-list {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
}
.theme-item {
  margin-right: 10px;
  margin-top: 10px;
  padding: 10px;
  width: 260px;
  background-color: #ffffff;
  border-radius: 8px;
  text-align: center;
}
.theme-color {
  margin: 20px 0;
}
</style>