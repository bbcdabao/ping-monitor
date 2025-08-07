<!--
  Copyright 2025 bbcdabao Team

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->

<template>
  <div>
    <el-card class="custom-shadow mgb6" shadow="hover">
      <template #header>
        <div class="content-title">{{ t('template') }}</div>
      </template>
      <div class="this-card">
        <el-descriptions
          style="width: 100%;"
          :column="1"
          border
        >
          <el-descriptions-item
            :label="t('selectCreate')"
            label-align="right"
            align="left"
            width="200px"
          >
            <el-select
              v-model="selectedPlugName"
              :placeholder="t('selectPlugForCreateTask')"
              clearable
              filterable
            >
              <el-option
                v-for="plugInfo in plugInfos"
                :key="plugInfo.plugName"
                :label="plugInfo.plugName"
                :value="plugInfo.plugName"
              />
            </el-select>
          </el-descriptions-item>
          <el-descriptions-item
            :label="$t('templateCount')"
            label-align="right"
            align="left"
            width="200px"
          >
            {{ plugInfos.length }}
          </el-descriptions-item>
        </el-descriptions>
        <div
          class="interval-line"
        />
        <div
          class="template-composition"
        >
          <div
            class="template-item-style"
            v-for="(plugInfo, index) in nowshowPlugInfos"
            :key="plugInfo.plugName"
          >
            <div
              class="template-item-inner-style"
            >
              <div
                class="template-item-inner-name"
              >
                {{ t('plugName') }} : {{ plugInfo.plugName }}
              </div>
              <div
                class="template-node-container"
              >
                <div
                  class="template-node-frame"
                />
                <div
                  class="template-node-style"
                >
                  <div
                    class="template-node-property"
                    v-for="(field, fieldKey) in plugInfo.plugTemp"
                    :key="fieldKey"
                  >
                    <div
                      class="template-node-sub0"
                    >
                      {{ t('params') }}:{{ fieldKey }}
                    </div>
                    <div
                      class="template-node-sub1"
                    >
                      {{ t('types') }}:{{ field.type }}
                    </div>
                    <el-tooltip
                      v-if="selectedPlugInfo != null"
                      :content="getDescs(field)"
                      placement="top"
                      effect="dark"
                    >
                      <div
                        class="template-node-sub3"
                      >
                        {{ getDescs(field) }}
                      </div>
                    </el-tooltip>
                    <div
                      v-else
                      class="template-node-sub3"
                    >
                      {{ getDescs(field) }}
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="interval-line" />
      </div>
    </el-card>

    <transition name="fade-slide" appear>
    <el-card class="custom-shadow mgb20" shadow="hover" v-if="selectedPlugInfo != null">
      <template #header>
        <div class="content-title">{{ t('createTask') }}</div>
      </template>
      <div class="this-card">
        <el-descriptions
          style="width: 100%;"
          :column="1"
          border
        >
          <el-descriptions-item
            label-align="right"
            align="left"
            width="200px"
            :label="t('taskName') + ':'"
          >
            <el-input
              :placeholder="t('taskNameRequired')"
              v-model="addTaskName"
            />
          </el-descriptions-item>
          <el-descriptions-item
            v-for="(field, key) in selectedPlugInfo.plugTemp"
            label-align="right"
            align="left"
            width="200px"
            :key="key"
            :label="getTitle(field)"
          >
            <el-input-number
              v-if="['BYTE', 'SHORT', 'INT', 'LONG', 'FLOAT', 'DOUBLE'].includes(field.type)"
              controls-position="right"
              v-model="addTaskData[key]"
            />
            <el-switch
              v-else-if="field.type === 'BOOLEAN'"
              v-model="addTaskData[key]"
            />
            <el-input
              v-else-if="field.type === 'STRING'"
              :placeholder="t('enterPlease')"
              v-model="addTaskData[key]"
            />
            <span v-else>{{t('unSuportly')}} : {{ field.type }}</span>
          </el-descriptions-item>
          <el-descriptions-item
            label-align="right"
            align="left"
            width="200px"
            :label="'操作:'"
          >
            <el-button
              class="bottom-style"
              type="primary"
              size="small"
              @click="createTaskSubmit"
            >
              <lucide-calendar-plus style="width: 18px;" />&nbsp;
              {{ t('confirm') }}
            </el-button>
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-card>
    </transition>

  </div>
</template>

<script setup lang="ts">
import {
  ref,
  toRaw,
  watch,
  computed,
  onMounted
} from 'vue';
import {
  useI18n
} from 'vue-i18n';
import {
  ElMessage
} from 'element-plus';
import {
  useRouter
} from 'vue-router';
import {
  getPlugInfos,
  postAddTask
} from '@/api';
import {
  promptYesOrNo
} from '@/utils/dialog-inputs';
import type {
  PlugInfo,
  TemplateField
} from '@/types/plug-sub';
import type {
  AddTaskPayload
} from '@/types/task-sub';

const { t, locale } = useI18n();
const router = useRouter();

const getDescs = (field: TemplateField) => {
  return `${t('illustrate')}:  ` + (locale.value === 'zh' ? field.desCn : field.desEn);
};

const getTitle = (field: TemplateField) => {
  return (locale.value === 'zh' ? field.desCn : field.desEn) + ':';
};

let plugInfoMap: Record<string, PlugInfo> = {};
const plugInfos = ref<PlugInfo[]>([]);
const selectedPlugName = ref<string | null>(null);
const selectedPlugInfo = computed(() => {
  return selectedPlugName.value ? plugInfoMap[selectedPlugName.value] || null : null;
});
const nowshowPlugInfos = computed(() => {
  return selectedPlugInfo.value ? [selectedPlugInfo.value] : plugInfos.value;
});

const addTaskName = ref('');
const addTaskData = ref<Record<string, any>>({});
watch(selectedPlugInfo, (newVal) => {
  if (newVal) {
    const temp: Record<string, any> = {};
    for (const key in newVal.plugTemp) {
      const field = newVal.plugTemp[key];
      switch (field.type) {
        case 'BOOLEAN':
          temp[key] = false;
          break;
        case 'BYTE':
        case 'SHORT':
        case 'INT':
        case 'LONG':
        case 'FLOAT':
        case 'DOUBLE':
          temp[key] = 0;
          break;
        case 'STRING':
          temp[key] = '';
          break;
        default:
          temp[key] = null;
      }
    }
    addTaskData.value = temp;
  } else {
    addTaskData.value = {};
  }
});

const createTaskSubmit = async () => {
  if (addTaskName.value.trim() === '') {
    ElMessage.warning(t('taskNameRequired'));
    return;
  }
  const taskData = toRaw(addTaskData.value);
  const plugName = selectedPlugInfo.value?.plugName || '';
  const addTaskPayload : AddTaskPayload = {
    plugName: plugName,
    properties: taskData
  };
  await postAddTask(addTaskName.value, addTaskPayload);
  const confirmed = await promptYesOrNo(t, t('addTaskSuccessJump'));
  if (confirmed) {
    router.push({
      path: '/taskinfo-page',
      query: {
        taskName: addTaskName.value
      }
    });
  }
};

const loadPlugInfos = async () => {
  const resData: PlugInfo[] = await getPlugInfos(null);
  if (resData.length <= 0) {
    ElMessage.primary(t('loadPlugInfosMsg'));
    return;
  }
  const indexPlugInfoMap: Record<string, PlugInfo> = {};
  resData.forEach(item => {
    indexPlugInfoMap[item.plugName] = item;
  });
  plugInfoMap = indexPlugInfoMap;

  plugInfos.value = resData
};

onMounted(async () => {
  await loadPlugInfos();
});
</script>

<style scoped>
.this-card {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
}
.template-composition {
  display: grid;
  width: 100%;
  margin-right: 2px;
  grid-template-columns: 1fr;
  row-gap: 10px;
  justify-content: center;
}
.template-item-style {
  border: 1px solid var(--element-index-bd-color);
  color: var(--element-index-color);
  background-color: var(--element-index-bg-color);
  font-weight: bold;
  width: 100%;
  min-width: 266px;
  height: 100%;
  border-radius: 4px;
  text-align: left;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.template-item-style:hover {
  box-shadow: 0 2px 12px var(--custom-shadow-color);
  transition: all 0.3s ease;
}
.template-item-inner-style {
  margin: 10px 10px;
}
.template-item-inner-name {
  color: var(--el-text-color-regular);
  background-color: var(--el-color-success-light-5);
  font-size: 12px;
  font-weight: bold;
  width: 100%;
  height: 24px;
  border-radius: 4px;
  text-align: left;
  line-height: 24px;
  box-sizing: border-box;
  padding: 0 22px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.template-node-container {
  display: flex;
  flex-direction: row;
  margin-top: 4px;
  gap: 4px;
}
.template-node-frame {
  color: var(--el-text-color-regular);
  background-color: var(--el-color-success-light-5);
  width: 20px;
  flex: 0 0 20px;
  text-align: center;
  margin-top: -10px;
  border-radius: 4px;
}
.template-node-style {
  display: flex;
  flex-wrap: wrap;
  text-align: left;
  gap: 4px;
}
.template-node-property {
  border: 2px solid var(--el-color-primary);
  border-radius: 2px;
  font-size: 12px;
  font-weight: bold;
  height: 80px;
  text-align: left;
  line-height: 24px;
  box-sizing: border-box;
}
.template-node-sub0 {
  border: 1px solid var(--el-color-primary);
  color: var(--el-text-color-regular);
  background-color: var(--el-color-primary-light-7);
  padding: 0 8px;
  width: 200px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.template-node-sub1 {
  border: 1px solid var(--el-color-primary);
  color: var(--el-text-color-regular);
  background-color: var(--el-color-warning-light-7);
  padding: 0 8px;
  width: 200px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.template-node-sub3 {
  border: 1px solid var(--el-color-primary);
  color: var(--el-text-color-regular);
  background-color: var(--el-color-info-light-7);
  padding: 0 8px;
  width: 200px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.icon-style {
  width: 14px;
  height: 14px;
}
.interval-line {
  background-color: var(--cardbody-bd-color);
  width: 100%;
  height: 1px;
  margin-top: 10px;
  margin-bottom: 10px;
}
.bottom-style {
  margin-left: 0px;
  width: 120px;
  font-size: 14px;
  height: 28px;
}

.fade-slide-enter-active {
  transition: all 1s ease;
}
.fade-slide-enter-from {
  opacity: 0;
  transform: translateY(100px);
}
.fade-slide-enter-to {
  opacity: 1;
  transform: translateY(0);
}
</style>