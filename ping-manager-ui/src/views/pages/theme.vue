<template>
    <div class="this-page">
        <div style="padding: 10px;">
            <el-card class="mgb20 custom-shadow" shadow="hover">
                <template #header>
                    <div class="content-title">{{ $t('sysTheme') }}</div>
                </template>
                <div class="theme-list mgb20">
                    <div
                        class="theme-item"
                        @click="setSystemTheme(value)"
                        v-for="[key, value] in Object.entries(systemmap)"
                    >
                        <div
                            :style="{
                                width: '100%',
                                height: '10px',
                                backgroundColor: value.headerBgColor,
                                border: '1px solid ' + value.headerBdColor
                            }"
                        />
                        <div
                            :style="{
                                width: '100%',
                                height: '60px',
                                backgroundColor: value.bodyBgColor,
                                border: '1px solid ' + value.bodyBdColor
                            }"
                        >
                            <div
                                :style="{
                                    width: '30%',
                                    height: '59px',
                                    backgroundColor: value.sidebarBgColor,
                                    border: '1px solid ' + value.sidebarBdColor
                                }"
                            />
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
    color = color.substring(1)
    const r = parseInt(color.substring(0, 2), 16)
    const g = parseInt(color.substring(2, 4), 16)
    const b = parseInt(color.substring(4, 6), 16)
    const inverseR = (255 - r).toString(16).padStart(2, '0')
    const inverseG = (255 - g).toString(16).padStart(2, '0')
    const inverseB = (255 - b).toString(16).padStart(2, '0')
    return `#${inverseR}${inverseG}${inverseB}`
};

const getGradientBackground = (color1: string, color2: string) => {
    return `linear-gradient(to bottom, ${color1} 0%, ${color1} 30%, ${color2} 0%, ${color2} 70%)`;
};

const systemmap: Record<string, ThemeConfig> = {
    '1': {
        primary: '#ff0000',
        success: '#ff0000',
        warning: '#ff0000',
        danger: '#ff0000',
        info: '#ff0000',
        headerBgColor: '#000000',
        headerBdColor: '#4d4d4d',
        headerColor: '#ffffff',
        bodyBgColor: '#4d4d4d',
        bodyBdColor: '#4d4d4d',
        bodyColor: '#ffffff',
        sidebarBgColor: '#000000',
        sidebarBdColor: '#4d4d4d',
        sidebarColor: '#ffffff',
        sidebarIndexBgColor: '#4d4d4d',
        sidebarIndexBdColor: '#4d4d4d',
        sidebarIndexColor: '#ffffff',
        cardbodyBgColor: '#000000',
        cardbodyBdColor: '#4d4d4d',
        cardbodyColor: '#ffffff',
        customhadowColor: '#000000'
    },
    '2': {
        primary: '#ff0000',
        success: '#ff0000',
        warning: '#ff0000',
        danger: '#ff0000',
        info: '#ff0000',
        headerBgColor: '#336699',
        headerBdColor: '#0099CC',
        headerColor: '#ffffff',
        bodyBgColor: '#0099CC',
        bodyBdColor: '#0099CC',
        bodyColor: '#ffffff',
        sidebarBgColor: '#336699',
        sidebarBdColor: '#0099CC',
        sidebarColor: '#ffffff',
        sidebarIndexBgColor: '#0099CC',
        sidebarIndexBdColor: '#0099CC',
        sidebarIndexColor: '#ffffff',
        cardbodyBgColor: '#336699',
        cardbodyBdColor: '#0099CC',
        cardbodyColor: '#ffffff',
        customhadowColor: '#336699'
    },
    '3': {
        primary: '#ff0000',
        success: '#ff0000',
        warning: '#ff0000',
        danger: '#ff0000',
        info: '#ff0000',
        headerBgColor: '#333399',
        headerBdColor: '#ccffff',
        headerColor: '#ffffff',
        bodyBgColor: '#ccffff',
        bodyBdColor: '#ccffff',
        bodyColor: '#000000',
        sidebarBgColor: '#333399',
        sidebarBdColor: '#ccffff',
        sidebarColor: '#ffffff',
        sidebarIndexBgColor: '#ccffff',
        sidebarIndexBdColor: '#ccffff',
        sidebarIndexColor: '#000000',
        cardbodyBgColor: '#333399',
        cardbodyBdColor: '#ccffff',
        cardbodyColor: '#ffffff',
        customhadowColor: '#333399'
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
    width: 100px;
    border: 1px solid #dcdfe6;
    border-radius: 2px;
    text-align: center;
}

.theme-color {
    margin: 20px 0;
}
</style>
