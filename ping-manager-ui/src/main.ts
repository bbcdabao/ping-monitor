import * as ElementPlusIconsVue from '@element-plus/icons-vue';

import { createApp } from 'vue';
import App from '@/App.vue';
import { createPinia } from 'pinia';
import router from '@/router';
import i18n from '@/i18n';

import 'element-plus/dist/index.css';
import '@/assets/css/icon.css';

const app = createApp(App);
app.use(createPinia());
app.use(router);
app.use(i18n);

/**
 * Register elementplus
 */
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component);
}

app.mount('#app');
