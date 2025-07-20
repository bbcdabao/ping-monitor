<!-- Copyright 2025 bbcdabao Team -->

<template>
  <div>
    <el-card class="custom-shadow mgb20" shadow="hover">
      <template #header>
        <div class="content-title">任务模板</div>
      </template>
      <div class="this-card">
        <el-descriptions
          style="min-width: 500px; width: 100%;"
          :column="2"
          border
        >
          <el-descriptions-item
            :label="$t('templateCount')"
            label-align="right"
            align="left"
            width="200px"
          >
            8
          </el-descriptions-item>
          <el-descriptions-item
            :label="'过滤操作'"
            label-align="right"
            align="left"
            width="200px"
          >
            
          </el-descriptions-item>
        </el-descriptions>
        <div class="interval-line" />
        <div class="template-composition">
          <div
            class="template-item-style"
            v-for="(plugInfo, index) in plugInfos"
            :key="plugInfo.plugName"
          >
            <div class="template-item-inner-style">
              <div class="template-item-inner-name">
                {{'模板名称'}} : {{ plugInfo.plugName }}
              </div>
              <div class="template-node-container">
                <div class="template-node-frame">
                  <div style="
                    margin-top: 20px;
                      display: flex;
                        text-align: center;
  flex-direction: column; /* 垂直排列 */
                  ">

                      <el-button circle size="small" type="primary">
                        <lucide-view class="icon-style" />
                      </el-button> 
        
                  </div>
                </div>
                <div class="template-node-style">
                  <div
                    class="template-node-property"
                    v-for="(field, fieldKey) in plugInfo.plugTemp"
                    :key="fieldKey"
                  >
                    <div class="template-node-sub0">
                      {{ '参数' }}:{{ fieldKey }}
                    </div>
                    <div class="template-node-sub1">
                      {{ '类型' }}:{{ field.type }}
                    </div>
                    <el-tooltip
                      :content="getDesc(field)"
                      placement="top"
                      effect="dark"
                    >
                      <div class="template-node-sub3">
                         {{ getDesc(field) }}
                      </div>
                    </el-tooltip>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="interval-line" />
      </div>
    </el-card>
  </div>
</template>
<script setup lang="ts">
import { 
  ref,
  toRaw,
  watch,
  reactive,
  onMounted
} from 'vue';
import {
  useI18n
} from 'vue-i18n';
import {
  getPlugInfos
} from '@/api';
import type {
  PlugInfo
} from '@/types/plug-sub';

const { t, locale } = useI18n();

const getDesc = (field: any) => {
  return '说明: ' + (locale.value === 'zh' ? field.desCn : field.desEn);
};

const plugInfos = ref<PlugInfo[]>([]);

const loadPlugInfos = async () => {
  const resData : PlugInfo[] = await getPlugInfos(null);
  plugInfos.value = resData;
};

onMounted( async () => {
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
  color:  var(--element-index-color);
  background-color: var(--element-index-bg-color);
  font-weight: bold;
  width: 100%;
  min-width: 300px;
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
  width: 40px;
  flex: 0 0 40px;
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

  width: 150px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.template-node-sub1 {
  border: 1px solid var(--el-color-primary);
  color: var(--el-text-color-regular);
  background-color: var(--el-color-warning-light-7);
  padding: 0 8px;

  width: 150px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.template-node-sub3 {
  border: 1px solid var(--el-color-primary);
  color: var(--el-text-color-regular);
  background-color: var(--el-color-info-light-7);
  padding: 0 8px;

  width: 150px;
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
</style>