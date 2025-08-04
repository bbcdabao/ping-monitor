<!--
  Licensed to the bbcdabao Team under one or more contributor license agreements.
  See the NOTICE file distributed with this work for additional information
  regarding copyright ownership. The bbcdabao Team licenses this file to you under
  the Apache License, Version 2.0 (the "License"); you may not use this file except
  in compliance with the License. You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software distributed
  under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific language
  governing permissions and limitations under the License.
-->

<template>
  <el-dropdown trigger="click" @command="handleLangCommand">
    <span class="el-dropdown-link">
      {{ showLangs[idxLanguage] }}
      <el-icon class="el-icon--right">
        <lucide-chevron-down />
      </el-icon>
    </span>
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item
          v-for="(item, index) in showLangs"
          divided
          :command="index"
          :key="index"
        >
          {{ item }}
        </el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>
<script setup lang="ts">

/**
 * Copyright 2025 bbcdabao Team
 */

import {
  ref,
  watch
} from 'vue';
import {
  useI18n
} from 'vue-i18n';

const { t, locale } = useI18n();

const showLangs: Record<string, string> = {
  en: 'EN',
  zh: '中文'
};

const getLanguage = () => {
  locale.value = localStorage.getItem('ping-monitor-lang') as 'en' | 'zh' || 'en';
  return locale.value;
};

const idxLanguage = ref(getLanguage());

const handleLangCommand = (command: string) => {
  localStorage.setItem('ping-monitor-lang', command);
  idxLanguage.value = getLanguage();
};

watch(idxLanguage, () => {
  window.location.reload();
});
</script>
<style scoped>
.el-dropdown-link {
  width: 50px;
  color: var(--header-color);
  cursor: pointer;
  display: flex;
  align-items: center;
}
.el-dropdown-menu__item {
  text-align: center;
}
</style>