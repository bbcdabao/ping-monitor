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
        <div class="content-title">{{ t('systemConfig') }}</div>
      </template>
      <div class="this-card">
        <el-descriptions
          style="width: 100%;"
          :column="1"
          border
        >
          <el-descriptions-item
            :label="t('resultType')"
            label-align="right"
            align="left"
            width="200px"
          >
            {{ rsType }}
          </el-descriptions-item>
          <el-descriptions-item
            :label="t('pingTimeOut')"
            label-align="right"
            align="left"
            width="200px"
          >
            <el-input-number
              style="width: 100%;"
              v-model="timeOutS"
              :min="10"
              :step="10"
              :placeholder="t('enterTimeOut')"
            />
          </el-descriptions-item>
          <el-descriptions-item
            :label="t('robotDiscoverCyc')"
            label-align="right"
            align="left"
            width="200px"
          >
            <el-input
              style="width: 100%;"
              v-model="cronTask"
              :placeholder="t('enterCronInfo')"
            />
            <span v-if="cronTaskError" style="color:red; margin-left: 0px;">
              {{ cronTaskError }}
            </span>
            <span v-else-if="cronTaskNext" style="margin-left: 0px;">
              {{ t('nextTimeSub') }}: {{ cronTaskNext }}
            </span>
          </el-descriptions-item>
          <el-descriptions-item
            :label="t('robotCheckCyc')"
            label-align="right"
            align="left"
            width="200px"
          >
            <el-input
              style="width: 100%;"
              v-model="cronInst"
              :placeholder="t('enterCronInfo')"
            />
            <span v-if="cronInstError" style="color:red; margin-left: 0px;">
              {{ cronInstError }}
            </span>
            <span v-else-if="cronInstNext" style="margin-left: 0px;">
              {{ t('nextTimeSub') }}: {{ cronInstNext }}
            </span>
          </el-descriptions-item>
          <el-descriptions-item
            :label="t('robotMainCheckCyc')"
            label-align="right"
            align="left"
            width="200px"
          >
            <el-input
              style="width: 100%;"
              v-model="cronMain"
              :placeholder="t('enterCronInfo')"
            />
            <span v-if="cronMainError" style="color:red; margin-left: 0px;">
              {{ cronMainError }}
            </span>
            <span v-else-if="cronMainNext" style="margin-left: 0px;">
              {{ t('nextTimeSub') }}: {{ cronMainNext }}
            </span>
          </el-descriptions-item>
          <el-descriptions-item
            :label="t('operation')"
            label-align="right"
            align="left"
            width="200px"
          >
            <el-button
              class="bottom-style"
              type="primary"
              size="small"
              :disabled="!canConfirm"
              @click="submitConfig"
            >
              <lucide-calendar-check style="width: 18px;" />&nbsp;
              {{ t('confirm') }}
            </el-button>
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
import {
  postSysconfigInfo,
  getSysconfigInfo
} from '@/api';
import type {
  SysconfigPayload,
  SysconfigInfo
} from '@/types/sysconfig-sub';

import { CronExpressionParser } from 'cron-parser';

const { t } = useI18n();

const rsType = ref('');

const timeOutS = ref<number>(10);

const isValidCronLite = (expr: string) => {
  try {
    const interval = CronExpressionParser.parse(expr.trim(), { currentDate: new Date() });
    return { valid: true, errors: [], next: interval.next().toString() };
  } catch (e: any) {
    return { valid: false, errors: [e?.message || '非法的 cron 表达式'] };
  }
};

const cronTask = ref('');
const cronTaskError = ref('');
const cronTaskNext = ref('');
watch(cronTask, (newVal) => {
  if (!newVal) {
    cronTaskError.value = '';
    cronTaskNext.value = '';
    return
  }
  const result = isValidCronLite(newVal)
  if (result.valid) {
    cronTaskError.value = '';
    cronTaskNext.value = result.next;
  } else {
    cronTaskError.value = result.errors[0];
    cronTaskNext.value = '';
  }
});

const cronInst = ref('');
const cronInstError = ref('');
const cronInstNext = ref('');
watch(cronInst, (newVal) => {
  if (!newVal) {
    cronInstError.value = '';
    cronInstNext.value = '';
    return
  }
  const result = isValidCronLite(newVal)
  if (result.valid) {
    cronInstError.value = '';
    cronInstNext.value = result.next;
  } else {
    cronInstError.value = result.errors[0];
    cronInstNext.value = '';
  }
});

const cronMain = ref('');
const cronMainError = ref('');
const cronMainNext = ref('');
watch(cronMain, (newVal) => {
  if (!newVal) {
    cronMainError.value = '';
    cronMainNext.value = '';
    return
  }
  const result = isValidCronLite(newVal)
  if (result.valid) {
    cronMainError.value = '';
    cronMainNext.value = result.next;
  } else {
    cronMainError.value = result.errors[0];
    cronMainNext.value = '';
  }
});

const canConfirm = computed(() => {
  return (
    !cronTaskError.value &&
    !cronInstError.value &&
    !cronMainError.value &&
    cronTask.value &&
    cronInst.value &&
    cronMain.value 
  )
})

const submitConfig = async () => {
  const sysconfigPayload: SysconfigPayload = {
    timeOutMs: timeOutS.value * 1000,
    cronTask: cronTask.value,
    cronInst: cronInst.value,
    cronMain: cronMain.value,
  }
  try {
    await postSysconfigInfo(sysconfigPayload)
    ElMessage.success(t('saveSuccess'))
  } catch (e) {
    ElMessage.error(t('saveFail'))
  }
};

onMounted(async () => {
  const resData = await getSysconfigInfo();
  cronTask.value = resData.cronTask;
  cronInst.value = resData.cronInst;
  cronMain.value = resData.cronMain;

  timeOutS.value = resData.timeOutMs / 1000;
  rsType.value = resData.rsType;
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