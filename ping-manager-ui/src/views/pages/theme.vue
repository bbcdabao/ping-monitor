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
          :style="{ backgroundColor: invertColor(value.headerBgColor) }"
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
import { reactive } from 'vue';
import { useThemeStore } from '@/store/theme';
import { ThemeConfig } from '@/types/themeConfig';
import { useI18n } from 'vue-i18n';

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
    elColorPrimary: '#409EFF',
    elColorSuccess: '#67C23A',
    elColorWarning: '#E6A23C',
    elColorDanger: '#F56C6C"',
    elColorInfo: '#909399',
    nprogressBarColor: '#4d4d4d',
    nprogressSpinnerTopColor: '#4d4d4d',
    nprogressSpinnerleftColor: '#4d4d4d',
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
    sidebarIndexBeforeColor: '#336699',
    cardbodyColor: '#ffffff',
    cardbodyBdColor: '#4d4d4d',
    cardbodyBgColor: '#000000',
    elementColor: '#ffffff',
    elementBgColor: '#ffffff',
    elementBdColor: '#ffffff',
    customhadowColor: '#000000'
  },
  '2': {
    elColorPrimary: '#409EFF',
    elColorSuccess: '#67C23A',
    elColorWarning: '#E6A23C',
    elColorDanger: '#F56C6C"',
    elColorInfo: '#909399',
    nprogressBarColor: '#ff3d00',
    nprogressSpinnerTopColor: '#ffc400',
    nprogressSpinnerleftColor: '#29b6f6',
    headerColor: '#ffffff',
    headerBgColor: '#1a1a1a',
    headerLineColor: '#424242',
    bodyColor: '#e0e0e0',
    bodyBgColor: '#121212',
    scrollbarBgColor: '#2c2c2c',
    scrollbarIndexBgColor: '#383838',
    scrollbarIndexBdColor: '#ff3d00',
    scrollbarIndexBgHoverColor: '#ff3d00',
    sidebarColor: '#ffffff',
    sidebarBgColor: '#1f1f1f',
    sidebarIndexColor: '#ff3d00',
    sidebarIndexBgColor: '#333333',
    sidebarIndexBeforeColor: '#29b6f6',
    cardbodyColor: '#ffffff',
    cardbodyBdColor: '#2c2c2c',
    cardbodyBgColor: '#1b1b1b',
    elementColor: '#ffffff',
    elementBgColor: '#2e2e2e',
    elementBdColor: '#424242',
    customhadowColor: '#ff3d00'
  },
  '3': {
    elColorPrimary: '#409EFF',
    elColorSuccess: '#67C23A',
    elColorWarning: '#E6A23C',
    elColorDanger: '#F56C6C"',
    elColorInfo: '#909399',
    nprogressBarColor: '#4d4d4d',
    nprogressSpinnerTopColor: '#4d4d4d',
    nprogressSpinnerleftColor: '#4d4d4d',
    headerColor: '#ffffff',
    headerBgColor: '#0099cc',
    headerLineColor: '#99ccff',
    bodyColor: '#000000',
    bodyBgColor: '#99ccff',
    scrollbarBgColor: '#0099cc',
    scrollbarIndexBgColor: '#99ccff',
    scrollbarIndexBdColor: '#000000',
    scrollbarIndexBgHoverColor: '#ffffff',
    sidebarColor: '#ffffff',
    sidebarBgColor: '#0099CC',
    sidebarIndexColor: '#ffffff',
    sidebarIndexBgColor: '#99ccff',
    sidebarIndexBeforeColor: '#006633',
    cardbodyColor: '#ffffff',
    cardbodyBdColor: '#99ccff',
    cardbodyBgColor: '#0099cc',
    elementColor: '#ffffff',
    elementBgColor: '#ffffff',
    elementBdColor: '#ffffff',
    customhadowColor: '#000000'
  },
  '4': {
    elColorPrimary: '#409EFF',
    elColorSuccess: '#67C23A',
    elColorWarning: '#E6A23C',
    elColorDanger: '#F56C6C"',
    elColorInfo: '#909399',
    nprogressBarColor: '#ff5722',
    nprogressSpinnerTopColor: '#ffc107',
    nprogressSpinnerleftColor: '#ff8f00',
    headerColor: '#ffffff',
    headerBgColor: '#3e2723',
    headerLineColor: '#ff7043',
    bodyColor: '#fbe9e7',
    bodyBgColor: '#2b1b17',
    scrollbarBgColor: '#4e342e',
    scrollbarIndexBgColor: '#5d4037',
    scrollbarIndexBdColor: '#ff5722',
    scrollbarIndexBgHoverColor: '#ff7043',
    sidebarColor: '#ffffff',
    sidebarBgColor: '#4e342e',
    sidebarIndexColor: '#ff5722',
    sidebarIndexBgColor: '#6d4c41',
    sidebarIndexBeforeColor: '#ffc107',
    cardbodyColor: '#ffffff',
    cardbodyBdColor: '#5d4037',
    cardbodyBgColor: '#3e2723',
    elementColor: '#ffffff',
    elementBgColor: '#ff7043',
    elementBdColor: '#d84315',
    customhadowColor: '#ff8f00'
  },
  '5': {
    elColorPrimary: '#409EFF',
    elColorSuccess: '#67C23A',
    elColorWarning: '#E6A23C',
    elColorDanger: '#F56C6C"',
    elColorInfo: '#909399',
    nprogressBarColor: '#4d4d4d',
    nprogressSpinnerTopColor: '#4d4d4d',
    nprogressSpinnerleftColor: '#4d4d4d',
    headerColor: '#ffffff',
    headerBgColor: '#336666',
    headerLineColor: '#993333',
    bodyColor: '#ffffff',
    bodyBgColor: '#003300',
    scrollbarBgColor: '#336666',
    scrollbarIndexBgColor: '#003300',
    scrollbarIndexBdColor: '#336666',
    scrollbarIndexBgHoverColor: '#336666',
    sidebarColor: '#ffffff',
    sidebarBgColor: '#336666',
    sidebarIndexColor: '#ffffff',
    sidebarIndexBgColor: '#003300',
    sidebarIndexBeforeColor: '#993333',
    cardbodyColor: '#ffffff',
    cardbodyBdColor: '#003300',
    cardbodyBgColor: '#336666',
    elementColor: '#ffffff',
    elementBgColor: '#ffffff',
    elementBdColor: '#ffffff',
    customhadowColor: '#993333'
  },
  '6': {
    elColorPrimary: '#409EFF',
    elColorSuccess: '#67C23A',
    elColorWarning: '#E6A23C',
    elColorDanger: '#F56C6C"',
    elColorInfo: '#909399',
    nprogressBarColor: '#00e5ff',
    nprogressSpinnerTopColor: '#00e5ff',
    nprogressSpinnerleftColor: '#7c4dff',
    headerColor: '#ffffff',
    headerBgColor: '#1a1a2e',
    headerLineColor: '#1a237e',
    bodyColor: '#e0f7fa',
    bodyBgColor: '#0f0f1a',
    scrollbarBgColor: '#1a1a2e',
    scrollbarIndexBgColor: '#21213b',
    scrollbarIndexBdColor: '#00e5ff',
    scrollbarIndexBgHoverColor: '#00e5ff',
    sidebarColor: '#ffffff',
    sidebarBgColor: '#1f1f2e',
    sidebarIndexColor: '#00e5ff',
    sidebarIndexBgColor: '#2a2a3d',
    sidebarIndexBeforeColor: '#7c4dff',
    cardbodyColor: '#ffffff',
    cardbodyBdColor: '#2a2a3d',
    cardbodyBgColor: '#1a1a2e',
    elementColor: '#ffffff',
    elementBgColor: '#ffffff',
    elementBdColor: '#ffffff',
    customhadowColor: '#00e5ff'
  },
  '7': {
    elColorPrimary: '#409EFF',
    elColorSuccess: '#67C23A',
    elColorWarning: '#E6A23C',
    elColorDanger: '#F56C6C"',
    elColorInfo: '#909399',
    nprogressBarColor: '#f4c542',
    nprogressSpinnerTopColor: '#33ccff',
    nprogressSpinnerleftColor: '#c11b17',
    headerColor: '#ffffff',
    headerBgColor: '#0d0d0d',
    headerLineColor: '#2f2f2f',
    bodyColor: '#ffffff',
    bodyBgColor: '#1a1a1a',
    scrollbarBgColor: '#2f2f2f',
    scrollbarIndexBgColor: '#9e1b1b',
    scrollbarIndexBdColor: '#f4c542',
    scrollbarIndexBgHoverColor: '#ffffff',
    sidebarColor: '#ffffff',
    sidebarBgColor: '#0d0d0d',
    sidebarIndexColor: '#f4c542',
    sidebarIndexBgColor: '#9e1b1b',
    sidebarIndexBeforeColor: '#33ccff',
    cardbodyColor: '#ffffff',
    cardbodyBdColor: '#2f2f2f',
    cardbodyBgColor: '#1a1a1a',
    elementColor: '#ffffff',
    elementBgColor: '#9e1b1b',
    elementBdColor: '#f4c542',
    customhadowColor: '#000000'
  },
  '8': {
    elColorPrimary: '#409EFF',
    elColorSuccess: '#67C23A',
    elColorWarning: '#E6A23C',
    elColorDanger: '#F56C6C"',
    elColorInfo: '#909399',
    nprogressBarColor: '#9b59b6',
    nprogressSpinnerTopColor: '#ffffff',
    nprogressSpinnerleftColor: '#8e44ad',
    headerColor: '#ffffff',
    headerBgColor: '#1a1a2e',
    headerLineColor: '#6c3483',
    bodyColor: '#ffffff',
    bodyBgColor: '#1a1a2e',
    scrollbarBgColor: '#2e2e3a',
    scrollbarIndexBgColor: '#6c3483',
    scrollbarIndexBdColor: '#8e44ad',
    scrollbarIndexBgHoverColor: '#ffffff',
    sidebarColor: '#ffffff',
    sidebarBgColor: '#1a1a2e',
    sidebarIndexColor: '#ffffff',
    sidebarIndexBgColor: '#6c3483',
    sidebarIndexBeforeColor: '#9b59b6',
    cardbodyColor: '#ffffff',
    cardbodyBdColor: '#6c3483',
    cardbodyBgColor: '#2e2e3a',
    elementColor: '#ffffff',
    elementBgColor: '#8e44ad',
    elementBdColor: '#9b59b6',
    customhadowColor: '#000000'
  },
  '9': {
    elColorPrimary: '#409EFF',
    elColorSuccess: '#67C23A',
    elColorWarning: '#E6A23C',
    elColorDanger: '#F56C6C"',
    elColorInfo: '#909399',
    nprogressBarColor: '#f1c40f',
    nprogressSpinnerTopColor: '#ffffff',
    nprogressSpinnerleftColor: '#f39c12',
    headerColor: '#2c3e50',
    headerBgColor: '#fdf2e9',
    headerLineColor: '#f39c12',
    bodyColor: '#2c3e50',
    bodyBgColor: '#fdf2e9',
    scrollbarBgColor: '#f7f1e1',
    scrollbarIndexBgColor: '#f39c12',
    scrollbarIndexBdColor: '#e67e22',
    scrollbarIndexBgHoverColor: '#ffffff',
    sidebarColor: '#2c3e50',
    sidebarBgColor: '#fdf2e9',
    sidebarIndexColor: '#ffffff',
    sidebarIndexBgColor: '#e67e22',
    sidebarIndexBeforeColor: '#2ecc71',
    cardbodyColor: '#2c3e50',
    cardbodyBdColor: '#f39c12',
    cardbodyBgColor: '#f7f1e1',
    elementColor: '#ffffff',
    elementBgColor: '#f39c12',
    elementBdColor: '#f1c40f',
    customhadowColor: '#000000'
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