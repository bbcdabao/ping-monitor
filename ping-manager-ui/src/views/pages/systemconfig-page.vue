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
  <div>
    <el-card class="custom-shadow mgb6" shadow="hover">
      <template #header>
        <div class="content-title">{{ '系统配置' }}</div>
      </template>
      <div class="this-card">
        <el-descriptions
          style="width: 100%;"
          :column="1"
          border
        >
          <el-descriptions-item
            :label="'拨测超时 (ms)'"
            label-align="right"
            align="left"
            width="200px"
          >
          </el-descriptions-item>
          <el-descriptions-item
            :label="'拨测周期'"
            label-align="right"
            align="left"
            width="200px"
          >
          </el-descriptions-item>
          <el-descriptions-item
            :label="'节点检测周期'"
            label-align="right"
            align="left"
            width="200px"
          >
          </el-descriptions-item>
          <el-descriptions-item
            :label="'节点检测周期 (master)'"
            label-align="right"
            align="left"
            width="200px"
          >
          </el-descriptions-item>
          <el-descriptions-item
            :label="'操作'"
            label-align="right"
            align="left"
            width="200px"
          >
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-card>
  </div>
</template>
<script setup lang="ts">
import {
  ref,
  watch,
  computed,
  onMounted,
  onBeforeUnmount
} from 'vue';
import {
  ElMessage
} from 'element-plus';
import {
  useI18n
} from 'vue-i18n';

import * as parser from 'cron-parser';

const { t } = useI18n();

const isValidCronLite = (expr: string) => {
  try {
    // parseExpression 返回 CronExpression 对象，需要使用 parser.parseExpression
    const interval = parser.parseExpression(expr.trim(), { currentDate: new Date() })
    return { valid: true, errors: [], next: interval.next().toString() }
  } catch (e: any) {
    return { valid: false, errors: [e?.message || '非法的 cron 表达式'] }
  }
}

onMounted(() => {

  const istaaa = isValidCronLite('asdfsafdsadf');
  debugger
});
onBeforeUnmount(() => {
});
</script>
<style scoped>
.this-card {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
}

</style>