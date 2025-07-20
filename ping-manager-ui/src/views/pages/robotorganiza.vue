<template>
  <div>
    <el-card class="custom-shadow mgb20" shadow="hover">
      <template #header>
        <div class="content-title">哨兵组列表</div>
      </template>
      <div class="rebot-list mgb20">
        <el-table :data="robotRegister.robotGroups" height="100%" style="width: 100%">
          <el-table-column prop="name" label="Name" width="250" />
          <el-table-column label="操作">
            <template #default="scope">
              <el-button size="small" type="primary">详情</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-card>
  </div>
</template>
<script setup lang="ts">
import { onMounted } from 'vue';
import { useRobotRegisterStore } from '@/store/robot-register';

const robotRegister = useRobotRegisterStore();
onMounted(() => {
  robotRegister.updateRobotGroupNames();
});

</script>
<style scoped>
.rebot-list {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
}
</style>