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
  <div class="instance-info">
    <div style="font-size: 12px; font-weight: bold;">
      {{ '注册实例信息:' }}
      <span
        v-for="(record, robotUUID) in robotRuninfoMaster.masters"
        :key="robotUUID"
      >
        [ info: {{ record.masterRobotInfo.info }} ] [ latency: {{ record.masterRobotInfo.latency }} ms ]
        <span v-if="record.masterRobotInfo.act">
          <lucide-bot-message-square style="width: 12px; height: 12px; flex-shrink: 0;"/>
        </span>
      </span>
    </div>
    <div class="interval-line-primary" />
    <div class="instance-list">
      <div
        v-for="(record, robotUUID) in robotMetainfoInstances.instances"
        :key="robotUUID"
  
      >
        <div :class="getInstanceClass(robotUUID)">
          <lucide-bot style="width: 38px; height: 38px; flex-shrink: 0;"/> 
          <div style="flex-direction: column; width: 100%">
            <div style="font-size: 12px; font-weight: bold; margin-top: 5px;">
              {{ robotUUID }}
            </div>
            <div class="interval-line-success" />
            <div style="font-size: 12px; font-weight: normal;">
              {{ record.robotInfo }}
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import {
  ref,
  watch,
  onMounted,
  onBeforeUnmount
} from 'vue';
import {
  useI18n
} from 'vue-i18n';
import {
  throttle
} from 'lodash';
import {
  useRobotRuninfoMasterStore
} from '@/store/robotruninfo-master';
import {
  useRobotMetainfoInstancesStore
} from '@/store/robotmetainfo-instances';
import type {
  RobotGroupMasterInfo
} from '@/types/robot-sub';

const props = defineProps<{
  robotGroupName: string;
}>();

const getInstanceClass = (robotUUID: string) => {
  const robotGroupMasterInfo = robotRuninfoMaster.masters[robotUUID];
  if (!robotGroupMasterInfo) {
    return 'instance-item';
  }
  return 'instance-item-master'
};

const { t } = useI18n();
const robotRuninfoMaster = useRobotRuninfoMasterStore();
const robotMetainfoInstances = useRobotMetainfoInstancesStore();

watch(
  () => props.robotGroupName,
  () => {
    robotRuninfoMaster.beginSource(props.robotGroupName);
    robotMetainfoInstances.beginSource(props.robotGroupName);
  },
  { immediate: true }
);

onMounted(() => {
});
onBeforeUnmount(() => {
  robotRuninfoMaster.closeSource();
  robotMetainfoInstances.closeSource();
});
</script>
<style scoped>
.instance-info {
  box-sizing: border-box;
  border: 2px solid var(--el-color-primary);
  color: var(--el-text-color-primary);
  background-color: var(--el-color-primary-light-9);
  border-radius: 6px;
  padding: 4px;
  width: 100%;
  text-align: left;
}
.instance-list {
  box-sizing: border-box;
  display: grid;
  width: 100%;
  row-gap: 6px;
  column-gap: 6px;
  grid-template-columns: repeat(auto-fill, minmax(200px, 300px));
}
.instance-item {
  box-sizing: border-box;
  display: flex;
  flex-direction: row;
  border: 2px solid var(--el-color-success);
  color: var(--el-text-color-primary);
  background-color: var(--el-color-success-light-9);
  font-weight: bold;
  width: 100%;
  height: 100%;
  border-radius: 6px;
  padding: 6px 6px;
  text-align: center;

  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.instance-item-master {
  box-sizing: border-box;
  display: flex;
  flex-direction: row;
  border: 4px solid var(--el-color-success);
  color: var(--el-text-color-primary);
  background-color: var(--el-color-success-light-7);
  font-weight: bold;
  width: 100%;
  height: 100%;
  border-radius: 6px;
  padding: 6px 6px;
  text-align: center;

  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.interval-line-primary {
  background-color: var(--el-color-primary);
  width: 100%;
  height: 2px;
  transform: scaleY(0.5);
  margin-top: 6px;
  margin-bottom: 6px;
}
.interval-line-success {
  background-color: var(--el-color-success);
  width: 100%;
  height: 2px;
  transform: scaleY(0.5);
  margin-top: 2px;
  margin-bottom: 2px;
}
.interval-line-info {
  background-color: var(--el-color-info);
  width: 100%;
  height: 2px;
  transform: scaleY(0.5);
  margin-top: 2px;
  margin-bottom: 2px;
}
.interval-line {
  background-color: var(--el-color-info);
  width: 100%;
  height: 2px;
  transform: scaleY(0.5);
  margin-top: 2px;
  margin-bottom: 2px;
}
</style>