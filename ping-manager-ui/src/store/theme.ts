/**
 * Licensed to the bbcdabao Team under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership. The bbcdabao Team licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
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
import { ThemeConfig } from '@/types/theme-config';
import { setProperty } from '@/utils';

const dfltaThemeConfig: ThemeConfig = {
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
      setProperty(colorset, value, document.documentElement);
    });
  }

  applyTheme(themeStore.themeConfig);

  themeStore.$subscribe((_, state) => {
    applyTheme(state.themeConfig);
  });
}