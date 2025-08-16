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
import App from '@/App.vue';
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
  'Languages',
  'Linkedin',
  'FolderTree',
  'View',
  'CalendarPlus',
  'Settings',
  'Bot',
  'BotMessageSquare'
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

console.log(`
****************************************************
*                                                  *
*   __        __   _                               *
*   \\ \\      / /__| | ___ ___  _ __ ___   ___      *
*    \\ \\ /\\ / / _ \\ |/ __/ _ \\| '_ \` _ \\ / _ \\     *
*     \\ V  V /  __/ | (_| (_) | | | | | |  __/     *
*      \\_/\\_/ \\___|_|\\___\\___/|_| |_| |_|\\___|     *
*                                                  *
*               Welcome to bbcdabao                *
*                                                  *
****************************************************
`);