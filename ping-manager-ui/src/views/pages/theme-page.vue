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
  return '#c0c0c0';
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
  '1': {
    elColorPrimary: '#409eff',
    elColorSuccess: '#67c23a',
    elColorWarning: '#e6a23c',
    elColorDanger: '#f56c6c',
    elColorInfo: '#909399',
    nprogressBarColor: '#cccccc',
    nprogressSpinnerTopColor: '#cccccc',
    nprogressSpinnerleftColor: '#cccccc',
    headerColor: '#000000',
    headerBgColor: '#ffffff',
    headerLineColor: '#cccccc',
    bodyColor: '#000000',
    bodyBgColor: '#cccccc',
    scrollbarBgColor: '#ffffff',
    scrollbarIndexBgColor: '#cccccc',
    scrollbarIndexBdColor: '#cccccc',
    scrollbarIndexBgHoverColor: '#cccccc',
    sidebarColor: '#000000',
    sidebarBgColor: '#ffffff',
    sidebarIndexColor: '#000000',
    sidebarIndexBgColor: '#cccccc',
    sidebarIndexBeforeColor: '#000000',
    cardbodyColor: '#000000',
    cardbodyBdColor: '#cccccc',
    cardbodyBgColor: '#ffffff',
    elementColor: '#000000',
    elementBgColor: '#ffffff',
    elementBdColor: '#ffffff',
    elementIndexColor: '#000000',
    elementIndexBgColor: '#cccccc',
    elementIndexBdColor: '#cccccc',
    customShadowColor: '#000000'
  },
  '2': {
    elColorPrimary: '#409eff',
    elColorSuccess: '#67c23a',
    elColorWarning: '#e6a23c',
    elColorDanger: '#f56c6c',
    elColorInfo: '#909399',
    nprogressBarColor: '#ff0000',
    nprogressSpinnerTopColor: '#ff0000',
    nprogressSpinnerleftColor: '#ff0000',
    headerColor: '#ffffff',
    headerBgColor: '#000000',
    headerLineColor: '#4d4d4d',
    bodyColor: '#ffffff',
    bodyBgColor: '#4d4d4d',
    scrollbarBgColor: '#000000',
    scrollbarIndexBgColor: '#4d4d4d',
    scrollbarIndexBdColor: '#000000',
    scrollbarIndexBgHoverColor: '#ffffff',
    sidebarColor: '#ffffff',
    sidebarBgColor: '#000000',
    sidebarIndexColor: '#ffffff',
    sidebarIndexBgColor: '#4d4d4d',
    sidebarIndexBeforeColor: '#3399cc',
    cardbodyColor: '#ffffff',
    cardbodyBdColor: '#4d4d4d',
    cardbodyBgColor: '#000000',
    elementColor: '#ffffff',
    elementBgColor: '#000000',
    elementBdColor: '#000000',
    elementIndexColor: '#ffffff',
    elementIndexBgColor: '#4d4d4d',
    elementIndexBdColor: '#4d4d4d',
    customShadowColor: '#ff0000'
  },
  '3': {
    elColorPrimary: '#409eff',
    elColorSuccess: '#67c23a',
    elColorWarning: '#e6a23c',
    elColorDanger: '#f56c6c',
    elColorInfo: '#909399',
    nprogressBarColor: '#4d4d4d',
    nprogressSpinnerTopColor: '#4d4d4d',
    nprogressSpinnerleftColor: '#4d4d4d',
    headerColor: '#ffffff',
    headerBgColor: '#003366',
    headerLineColor: '#99ccff',
    bodyColor: '#ffffff',
    bodyBgColor: '#0099cc',
    scrollbarBgColor: '#003366',
    scrollbarIndexBgColor: '#0099cc',
    scrollbarIndexBdColor: '#0099cc',
    scrollbarIndexBgHoverColor: '#99cc00',
    sidebarColor: '#ffffff',
    sidebarBgColor: '#003366',
    sidebarIndexColor: '#ffffff',
    sidebarIndexBgColor: '#0099cc',
    sidebarIndexBeforeColor: '#99cc00',
    cardbodyColor: '#ffffff',
    cardbodyBdColor: '#0099cc',
    cardbodyBgColor: '#003366',
    elementColor: '#ffffff',
    elementBgColor: '#003366',
    elementBdColor: '#003366',
    elementIndexColor: '#ffffff',
    elementIndexBgColor: '#0099cc',
    elementIndexBdColor: '#0099cc',
    customShadowColor: '#99cc00'
  },
  '4': {
    elColorPrimary: '#409eff',
    elColorSuccess: '#67c23a',
    elColorWarning: '#e6a23c',
    elColorDanger: '#f56c6c',
    elColorInfo: '#909399',
    nprogressBarColor: '#ccff99',
    nprogressSpinnerTopColor: '#ccff99',
    nprogressSpinnerleftColor: '#ccff99',
    headerColor: '#000000',
    headerBgColor: '#009999',
    headerLineColor: '#ccff99',
    bodyColor: '#000000',
    bodyBgColor: '#ffffff',
    scrollbarBgColor: '#ccffff',
    scrollbarIndexBgColor: '#009999',
    scrollbarIndexBdColor: '#009999',
    scrollbarIndexBgHoverColor: '#009999',
    sidebarColor: '#000000',
    sidebarBgColor: '#ccffff',
    sidebarIndexColor: '#000000',
    sidebarIndexBgColor: '#ffffff',
    sidebarIndexBeforeColor: '#009999',
    cardbodyColor: '#000000',
    cardbodyBdColor: '#009999',
    cardbodyBgColor: '#ccffff',
    elementColor: '#000000',
    elementBgColor: '#ccffff',
    elementBdColor: '#ccffff',
    elementIndexColor: '#000000',
    elementIndexBgColor: '#ccff99',
    elementIndexBdColor: '#ccff99',
    customShadowColor: '#009999'
  },
  '5': {
    elColorPrimary: '#409eff',
    elColorSuccess: '#67c23a',
    elColorWarning: '#e6a23c',
    elColorDanger: '#f56c6c',
    elColorInfo: '#909399',
    nprogressBarColor: '#ccff99',
    nprogressSpinnerTopColor: '#ccff99',
    nprogressSpinnerleftColor: '#ccff99',
    headerColor: '#ffffff',
    headerBgColor: '#333333',
    headerLineColor: '#ccff99',
    bodyColor: '#ffffff',
    bodyBgColor: '#cccccc',
    scrollbarBgColor: '#333333',
    scrollbarIndexBgColor: '#cccccc',
    scrollbarIndexBdColor: '#cccccc',
    scrollbarIndexBgHoverColor: '#cccccc',
    sidebarColor: '#ffffff',
    sidebarBgColor: '#333333',
    sidebarIndexColor: '#000000',
    sidebarIndexBgColor: '#cccccc',
    sidebarIndexBeforeColor: '#009999',
    cardbodyColor: '#ffffff',
    cardbodyBdColor: '#ccff99',
    cardbodyBgColor: '#333333',
    elementColor: '#ffffff',
    elementBdColor: '#333333',
    elementBgColor: '#333333',
    elementIndexColor: '#ffffff',
    elementIndexBgColor: '#333333',
    elementIndexBdColor: '#cccccc',
    customShadowColor: '#009999'
  },
  '6': {
    elColorPrimary: '#409eff',
    elColorSuccess: '#67c23a',
    elColorWarning: '#e6a23c',
    elColorDanger: '#f56c6c',
    elColorInfo: '#909399',
    nprogressBarColor: '#cccccc',
    nprogressSpinnerTopColor: '#cccccc',
    nprogressSpinnerleftColor: '#cccccc',
    headerColor: '#000000',
    headerBgColor: '#fff8e7',
    headerLineColor: '#cccccc',
    bodyColor: '#000000',
    bodyBgColor: '#cccccc',
    scrollbarBgColor: '#fff8e7',
    scrollbarIndexBgColor: '#cccccc',
    scrollbarIndexBdColor: '#cccccc',
    scrollbarIndexBgHoverColor: '#cccccc',
    sidebarColor: '#000000',
    sidebarBgColor: '#fff8e7',
    sidebarIndexColor: '#000000',
    sidebarIndexBgColor: '#cccccc',
    sidebarIndexBeforeColor: '#99cc00',
    cardbodyColor: '#000000',
    cardbodyBdColor: '#cccccc',
    cardbodyBgColor: '#fff8e7',
    elementColor: '#000000',
    elementBgColor: '#fff8e7',
    elementBdColor: '#fff8e7',
    elementIndexColor: '#000000',
    elementIndexBgColor: '#cccccc',
    elementIndexBdColor: '#cccccc',
    customShadowColor: '#99cc00'
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