<!-- Copyright 2025 bbcdabao Team -->

<template>
  <div>
    <el-card class="custom-shadow mgb6" shadow="hover">
      <template #header>
        <div class="content-title">任务列表</div>
      </template>
      <div class="this-card">
        <el-descriptions
          style="width: 100%;"
          :column="1"
          border
        >
          <el-descriptions-item
            :label="'选择分配'"
            label-align="right"
            align="left"
            width="200px"
          >
            <el-select
              v-model="selectedTaskName"
              placeholder="选择插件可创建任务"
              clearable
              filterable
            >
              <el-option
                v-for="taskInfo in taskInfos"
                :key="taskInfo.taskName"
                :label="taskInfo.taskName"
                :value="taskInfo.taskName"
              />
            </el-select>
          </el-descriptions-item>
          <el-descriptions-item
            :label="$t('taskCount')"
            label-align="right"
            align="left"
            width="200px"
          >
            {{ taskInfos.length }}
          </el-descriptions-item>
        </el-descriptions>
        <el-table
          :data="nowshowTaskInfos"
          style="width: 100%; margin-top: 10px;"
        >
          <el-table-column prop="taskName" label="任务名称" sortable />
          <el-table-column prop="plugName" label="插件名称" sortable />
          <el-table-column label="操作" width="100">
            <template #default="{ row }">
              <el-button
                type="danger"
                size="small"
                @click="handleDelete(row)"
              >
                <el-icon>
                  <Delete />
                </el-icon>
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-card>
    <transition name="fade-slide" appear>
    <el-card class="custom-shadow mgb20" shadow="hover" v-if="selectedTaskInfo != null">
      <template #header>
        <div class="content-title">任务详情 : {{ selectedTaskName }}</div>
      </template>
      <div class="this-card">
        <el-descriptions
          style="width: 100%;"
          :column="1"
          border
        >
          <el-descriptions-item
            v-for="(field, key) in taskInfoPropertys"
            label-align="right"
            align="left"
            width="200px"
            :key="key"
            :label="getTitle(field.plugTemp)"
          >
            {{ field.value }}
          </el-descriptions-item>
          <el-descriptions-item
            label-align="right"
            align="left"
            width="200px"
            :label="'分配哨兵组'"
          >
            <div
              v-for="(node, index) in robotGroupInfoNodes"
              :key="index"
            >
              <el-checkbox v-model="node.ischeck">
                {{ getRobotGroupInfoDes(node.robotGroupInfo) }}
              </el-checkbox>
            </div>
            <el-button
              class="bottom-style"
              style="margin-top: 20px;"
              type="primary"
              size="small"
              @click="confirmRobotGroupsSubmit"
            >
              <lucide-calendar-check style="width: 18px;" />&nbsp;
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
  useRoute
} from 'vue-router';
import {
  ElMessage
} from 'element-plus';
import {
  getTaskInfos,
  getPlugInfos,
  deleteAddTask,
  getRobotGroups,
  getRobotGroupsByTaskName,
  postRobotGroupsByTaskName,
  getCheckRobotGroupInfoByTaskName
} from '@/api';
import type {
  PlugInfo,
  TemplateField
} from '@/types/plug-sub';
import type {
  AddTaskRobotGroupsPayload,
  TaskInfo
} from '@/types/task-sub';
import type {
  RobotGroupInfo,
  CheckRobotGroupInfo
} from '@/types/robot-sub';

const { t, locale } = useI18n();

const route = useRoute();

const getTitle = (field: TemplateField) => {
  return (locale.value === 'zh' ? field.desCn : field.desEn) + ':';
};

let taskInfoMap: Record<string, TaskInfo> = {};
const taskInfos = ref<TaskInfo[]>([]);
const selectedTaskName = ref<string | null>(null);

const selectedTaskInfo = computed(() => {
  return selectedTaskName.value ? taskInfoMap[selectedTaskName.value] || null : null;
});
const nowshowTaskInfos = computed(() => {
  return selectedTaskInfo.value ? [selectedTaskInfo.value] : taskInfos.value;
});

interface TaskInfoProperty {
  plugTemp: TemplateField;
  value: string;
}
const taskInfoPropertys = ref<TaskInfoProperty[]>([]);
watch(selectedTaskInfo, async (newVal) => {
  if (newVal === null) {
    taskInfoPropertys.value = [];
    robotGroupInfoNodes.value = [];
    return;
  }
  const nowTaskInfos: TaskInfo[] = await getTaskInfos(newVal.taskName);
  if (nowTaskInfos.length <= 0) {
    return;
  }
  const indexTaskInfo = nowTaskInfos[0];
  const properties = indexTaskInfo.properties;
  if (!properties) {
    return;
  }
  const nowPlugInfos: PlugInfo[] = await getPlugInfos(indexTaskInfo.plugName);
  if (nowPlugInfos.length <= 0) {
    return;
  }
  const nowTaskInfoPropertys: TaskInfoProperty[] = [];
  const indexPlugInfo = nowPlugInfos[0];
  Object.entries(properties).forEach(([key, val]) => {
    const plugTemp = indexPlugInfo.plugTemp[key];
    if (!plugTemp) {
      return;
    }
    const property: TaskInfoProperty = {
      plugTemp: plugTemp,
      value: val
    };
    nowTaskInfoPropertys.push(property);

  });
  taskInfoPropertys.value = nowTaskInfoPropertys;
  const nowCheckRobotGroupInfos: CheckRobotGroupInfo[] = await getCheckRobotGroupInfoByTaskName(selectedTaskName.value);
  robotGroupInfoNodes.value = nowCheckRobotGroupInfos;
});

const getRobotGroupInfoDes = (field: RobotGroupInfo) => {
  return (locale.value === 'zh' ? field.descriptionCn : field.descriptionEn);
};
const robotGroupInfoNodes = ref<CheckRobotGroupInfo[]>([]);

const confirmRobotGroupsSubmit = async () => {
  if (!selectedTaskInfo.value) {
    ElMessage.error('请选择任务');
    return;
  }
  const taskName = selectedTaskInfo.value.taskName;
  const robotGroupNames: string[] = [];

  robotGroupInfoNodes.value.forEach(node => {
    if (node.ischeck) {
      robotGroupNames.push(node.robotGroupInfo.robotGroupName);
    }
  });

  const addTaskRobotGroupsPayload: AddTaskRobotGroupsPayload = {
    robotGroups: robotGroupNames
  };
  
  await postRobotGroupsByTaskName(taskName,addTaskRobotGroupsPayload);
  ElMessage.success(t('optSuccess'));
};

const loadTaskInfos = async () => {
  const resData: TaskInfo[] = await getTaskInfos(null);

  const indexTaskInfoMap: Record<string, TaskInfo> = {};
  resData.forEach(item => {
    indexTaskInfoMap[item.taskName] = item;
  });
  taskInfoMap = indexTaskInfoMap;

  taskInfos.value = resData

  const taskNameQuery = route.query.taskName as string | undefined;
  if (!taskNameQuery) {
    return;
  }
  const indexTaskInfo = taskInfoMap[taskNameQuery];
  if (!indexTaskInfo) {
    return;
  }
  selectedTaskName.value = indexTaskInfo.taskName;
};

const handleDelete = async (row: TaskInfo) => {
  await deleteAddTask(row.taskName);
  selectedTaskName.value = null;
  await loadTaskInfos();
};

onMounted(async () => {
  await loadTaskInfos();
});

</script>
<style scoped>
.this-card {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
}
.bottom-style {
  margin-left: 0px;
  width: 120;
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