import { defineStore } from 'pinia';
import { ThemeConfig } from '@/types/themeConfig';

const dfltaThemeConfig: ThemeConfig = {
    primary: '#409EFF',
    success: '#67C23A',
    warning: '#E6A23C',
    danger: '#F56C6C"',
    info: '#909399',

    headerBgColor: '#FFFFFF',
    headerBdColor: '#E4E7ED',
    headerColor: '#303133',

    bodyBgColor: '#E4E7ED',
    bodyBdColor: '#E4E7ED',
    bodyColor: '#E4E7ED',

    sidebarBgColor: '#F4F4F5',
    sidebarBdColor: '#D3D4D6',
    sidebarColor: '#606266',

    sidebarIndexBgColor: '#E4E7ED',
    sidebarIndexBdColor: '#D3D4D6',
    sidebarIndexColor: '#303133',

    cardbodyBgColor: '#FFFFFF',
    cardbodyBdColor: '#EBEEF5',
    cardbodyColor: '#303133',

    customhadowColor: '#000000'
};

function toKebabCase(str: string): string {
    const matches = str.match(/[A-Z]?[a-z]+|[A-Z]+(?![a-z])/g) || [];
    return `--${matches.join('-').toLowerCase()}`;
}

const initialThemeConfig = (() => {
    try {
        const storedConfig = localStorage.getItem("theme-config");
        return storedConfig ? { ...dfltaThemeConfig, ...JSON.parse(storedConfig) } : { ...dfltaThemeConfig };
    } catch (error) {
        console.error("Error parsing theme config from localStorage:", error);
        return { ...dfltaThemeConfig };
    }
})();

export const useThemeStore = defineStore('theme', {
    state: () => ({
        themeConfig: initialThemeConfig
    }),
    getters: {},
    actions: {
        saveThemeConfig(indexThemeConfig: ThemeConfig) {
            this.themeConfig = { ...dfltaThemeConfig, ...indexThemeConfig };
            localStorage.setItem("theme-config", JSON.stringify(this.themeConfig));
        }
    }
});

export const useThemeWatcher = () => {
    const themeStore = useThemeStore();

    const applyTheme = (themeConfig: Record<string, string>) => {
        Object.entries(themeConfig).forEach(([key, value]) => {
            const colorset = toKebabCase(key);
            console.info(">>:", colorset);
            document.documentElement.style.setProperty(colorset, value);
        });
    }

    applyTheme(themeStore.themeConfig)

    themeStore.$subscribe((_, state) => {
        applyTheme(state.themeConfig)
    })
}