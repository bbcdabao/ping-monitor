/**
 * Copyright 2025 bbcdabao Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { defineStore } from 'pinia';
import { ThemeConfig } from '@/types/themeConfig';

/**
 * Initial configuration
 */
const dfltaThemeConfig: ThemeConfig = {
  elColorPrimary: '#0000ff',
  elColorSuccess: '#ff0000',
  elColorWarning: '#ff0000',
  elColorDanger: '#ff0000',
  elColorInfo: '#ff0000',
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
  cardbodyColor: '#FFFFFF',
  cardbodyBdColor: '#4d4d4d',
  cardbodyBgColor: '#000000',
  elementColor: '#ffffff',
  elementBgColor: '#ffffff',
  elementBdColor: '#ffffff',
  elementIndexColor: '#ffffff',
  elementIndexBgColor: '#ffffff',
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
      document.documentElement.style.setProperty(colorset, value);
    });
  }

  applyTheme(themeStore.themeConfig);

  themeStore.$subscribe((_, state) => {
    applyTheme(state.themeConfig);
  });
}