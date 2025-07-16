/**
 * Copyright 2025 bbcdabao Team
 */

import { defineStore } from 'pinia';
import { ThemeConfig } from '@/types/theme-config';
import { setProperty } from '@/utils';

/**
 * Initial configuration
 */
const dfltaThemeConfig: ThemeConfig = {
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
  sidebarIndexBeforeColor: '#99cc00',
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
};

function toKebabCase(str: string): string {
  const matches = str.match(/[A-Z]?[a-z]+|[A-Z]+(?![a-z])/g) || [];
  return `--${matches.join('-').toLowerCase()}`;
}

/**
 * Initialization sequence
 * 1 localStorage
 * 2 dfltaThemeConfig
 */
const initialThemeConfig = (() => {
  try {
    const storedConfig = localStorage.getItem("theme-config");
    return storedConfig ? { ...dfltaThemeConfig, ...JSON.parse(storedConfig) } : { ...dfltaThemeConfig };
  } catch (error) {
    console.error("Error parsing theme config from localStorage:", error);
    return { ...dfltaThemeConfig };
  }
})();

/**
 * Main store
 */
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
      setProperty(colorset, value, document.documentElement);
    });
  }

  applyTheme(themeStore.themeConfig);

  themeStore.$subscribe((_, state) => {
    applyTheme(state.themeConfig);
  });
}