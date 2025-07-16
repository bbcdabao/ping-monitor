/**
 * Copyright 2025 bbcdabao Team
 */

import {
  createI18n
} from 'vue-i18n';
import zh from './locales/zh.json';
import en from './locales/en.json';

const lang = localStorage.getItem('ping-monitor-lang') || 'en';

const messages = {
  en,
  zh,
};

const i18n = createI18n({
  legacy: false,
  locale: lang,
  fallbackLocale: 'zh',
  messages,
});

export default i18n;