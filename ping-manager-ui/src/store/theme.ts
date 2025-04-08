import { defineStore } from 'pinia';
import { ThemeConfig } from '@/types/themeConfig';

const dfltaThemeConfig: ThemeConfig = {
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
  bodyLineColor: '#4d4d4d',

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

  applyTheme(themeStore.themeConfig);

  themeStore.$subscribe((_, state) => {
    applyTheme(state.themeConfig);
  });
}
