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
  <el-config-provider>
    <router-view />
  </el-config-provider>
</template>
<script setup lang="ts">

import { onMounted } from 'vue';
import { useThemeWatcher } from '@/store/theme';
import emitter from '@/event-bus';
import { useI18n } from 'vue-i18n';

const { t } = useI18n();

emitter.on('go-login', () => {
});

onMounted(() => {
  useThemeWatcher();
});
</script>
<style>
@import '@/assets/css/main.css';
</style>