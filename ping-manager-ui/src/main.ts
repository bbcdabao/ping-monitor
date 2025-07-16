/**
 * Copyright 2025 bbcdabao Team
 */

import * as ElementPlusIconsVue from '@element-plus/icons-vue';
import dayjs from 'dayjs';
import utc from 'dayjs/plugin/utc';
import timezone from 'dayjs/plugin/timezone';

import {
  createApp
} from 'vue';
import {
  createPinia
} from 'pinia';
import piniaPersistedState from 'pinia-plugin-persistedstate';
import router from '@/router';
import App from '@/app.vue';
import i18n from '@/i18n';
import ElementPlus from 'element-plus' 
import 'element-plus/dist/index.css';
import '@/assets/css/icon.css';

import elementPlusZh from '@/locales/element-plus-zh';
import elementPlusEn from '@/locales/element-plus-en';

const elementPlusLocales = {
  zh: elementPlusZh,
  en: elementPlusEn,
}

const pinia = createPinia();

pinia.use(piniaPersistedState);

const app = createApp(App);
app.use(pinia);
app.use(router);
app.use(i18n);
app.use(ElementPlus, {
  locale: elementPlusLocales[i18n.global.locale.value] || elementPlusZh,
})

/**
 * Register elementplus
 */
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component);
}

/**
 * Register lucide uesd
 */
import * as Lucide from 'lucide-vue-next'
const lucideIconNames = [
  'User',
  'Users',
  'LayoutList',
  'List',
  'Receipt',
  'Video',
  'BrushCleaning',
  'Undo2',
  'Trello',
  'BriefcaseConveyorBelt',
  'CalendarCheck',
  'ChartCandlestick',
  'SquareSigma',
  'AlignEndVertical',
  'Minimize',
  'Maximize',
  'ChevronDown',
  'LogIn',
  'ChevronLeft',
  'Sparkles',
  'BadgeDollarSign',
  'Star',
  'Airplay',
  'SquareMenu',
  'Scroll',
  'ScrollText',
  'ListTree',
  'Building',
  'LandPlot',
  'Grid',
  'Languages'
]
for (const name of lucideIconNames) {
  const component = Lucide[name]
  if (component) {
    app.component('Lucide' + name, component)
  }
}

app.mount('#app');

dayjs.extend(utc);
dayjs.extend(timezone);