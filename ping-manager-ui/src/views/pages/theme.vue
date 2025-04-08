<template>
  <div>
    <div style="padding: 10px;">
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
          >
            <div
              :style="{
                width: '100%',
                height: '12px',
                backgroundColor: value.headerBgColor,
                borderLeft: '0px solid ' + value.sidebarBdColor,
                borderRight: '0px solid ' + value.sidebarBdColor,
                borderTop: '1px solid ' + value.headerBdColor,
                borderBottom: '1px solid ' + value.headerBdColor
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
                  height: '118px',
                  backgroundColor: value.sidebarBgColor,
                  borderLeft: '0px solid ' + value.sidebarBdColor,
                  borderRight: '1px solid ' + value.sidebarBdColor,
                  borderTop: '1px solid ' + value.sidebarBdColor,
                  borderBottom: '0px solid ' + value.sidebarBdColor
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
                  borderRadius: '4px',
                  border: '1px solid ' + value.cardbodyBdColor
                }"
              >
                <div style='fontSize: 9px; textAlign: left; marginLeft: 5px;'> xxxxxx </div>
                <div
                  :style="{
                    width: '100%',
                    height: '1px',
                    backgroundColor: value.cardbodyBdColor,
                    marginTop: '1px'
                  }"
                />
              </div>
            </div>
          </div>
        </div>
        <div class="flex-center">
          <el-button type="primary" @click="resetSystemTheme">{{ $t('resetTheme') }}</el-button>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ThemeConfig } from '@/types/themeConfig';
import { useThemeStore } from '@/store/theme';
import { reactive } from 'vue';
import { useI18n } from 'vue-i18n';

const { t } = useI18n();

const themeStore = useThemeStore();

const color = reactive({
  primary: localStorage.getItem('theme-primary') || '#409eff',
  success: localStorage.getItem('theme-success') || '#67c23a',
  warning: localStorage.getItem('theme-warning') || '#e6a23c',
  danger: localStorage.getItem('theme-danger') || '#f56c6c',
  info: localStorage.getItem('theme-info') || '#909399',
  headerBgColor: themeStore.themeConfig.headerBgColor,
  headerTextColor: themeStore.themeConfig.headerColor,
});

const themes = [
  {
    name: 'primary',
    color: themeStore.themeConfig.primary || color.primary
  },
  {
    name: 'success',
    color: themeStore.themeConfig.success || color.success
  },
  {
    name: 'warning',
    color: themeStore.themeConfig.warning || color.warning
  },
  {
    name: 'danger',
    color: themeStore.themeConfig.danger || color.danger
  },
  {
    name: 'info',
    color: themeStore.themeConfig.info || color.info
  }
];

const changeColor = (name: string) => {
  //themeStore.setPropertyColor(color[name], name)
};

const resetTheme = () => {
  //themeStore.resetTheme()
};

const getInverseColor = (color: string) => {
  color = color.substring(1);
  const r = parseInt(color.substring(0, 2), 16);
  const g = parseInt(color.substring(2, 4), 16);
  const b = parseInt(color.substring(4, 6), 16);
  const inverseR = (255 - r).toString(16).padStart(2, '0');
  const inverseG = (255 - g).toString(16).padStart(2, '0');
  const inverseB = (255 - b).toString(16).padStart(2, '0');
  return `#${inverseR}${inverseG}${inverseB}`;
};

const getGradientBackground = (color1: string, color2: string) => {
  return `linear-gradient(to bottom, ${color1} 0%, ${color1} 30%, ${color2} 0%, ${color2} 70%)`;
};

const systemmap: Record<string, ThemeConfig> = {
  '1': {
    primary: '#ff0000',
    success: '#ff0000',
    warning: '#ff0000',
    danger: '#ff0000"',
    info: '#ff0000',
    nprogressBarColor: '#4d4d4d',
    nprogressSpinnerTopColor: '#4d4d4d',
    nprogressSpinnerleftColor: '#4d4d4d',
    headerColor: '#ffffff',
    headerBgColor: '#000000',
    bodyColor: '#ffffff',
    bodyBgColor: '#4d4d4d',
    bodyLineColor: '#ff0000',
    scrollbarBgColor: '#000000',
    scrollbarIndexBgColor: '#4d4d4d',
    scrollbarIndexBdColor: '#000000',
    scrollbarIndexBgHoverColor: '#ffffff',
    sidebarColor: '#ffffff',
    sidebarBgColor: '#000000',
    sidebarIndexColor: '#ffffff',
    sidebarIndexBgColor: '#4d4d4d',
    sidebarIndexBeforeColor: '#336699',
    cardbodyColor: '#FFFFFF',
    cardbodyBdColor: '#4d4d4d',
    cardbodyBgColor: '#000000',
    formLabelColor: '#ffffff',
    formLabelBgColor: '#4d4d4d',
    customhadowColor: '#000000'
  }
};

const setSystemTheme = (data: any) => {
  console.info("theme:", data);
  themeStore.saveThemeConfig(data);
};

const resetSystemTheme = () => {
  resetTheme();
  location.reload();
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
  border: 1px solid #dcdfe6;
  border-radius: 2px;
  text-align: center;
}

.theme-color {
  margin: 20px 0;
}
</style>
