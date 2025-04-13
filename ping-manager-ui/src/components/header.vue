<template>
  <div class="header">
    <!-- Collapse button -->
    <div class="header-left">
      <div class="collapse-btn" @click="collapseChange">
        <el-icon v-if="sidebar.collapse">
          <Expand />
        </el-icon>
        <el-icon v-else>
          <Fold />
        </el-icon>
      </div>
      <div class="collapse-img" v-if="!sidebar.collapse">
        <img :src="pingMonitor" alt="PingMonitor" class="ping-monitor">
      </div>
    </div>
    <div>{{ header.titlesp }}</div>
    <div class="header-right">
      <div class="header-user-con">
        <div class="btn-icon" @click="router.push('/theme')">
          <el-tooltip effect="dark" :content="$t('setTheme')" placement="bottom">
            <Brush />
          </el-tooltip>
        </div>
        <div class="btn-icon" @click="setFullScreen">
          <el-tooltip effect="dark" :content="$t('fullScreen')" placement="bottom">
            <i v-if="isFullScreen" class="el-icon-lx-exit"></i>
            <i v-else class="el-icon-lx-full"></i>
          </el-tooltip>
        </div>
        <!-- Language selection -->
        <vLanguage style="margin-top: 6px;" />
        <!-- User avatar -->
        <el-avatar class="user-avator" :size="32" :src="avatarImage" />
        <!-- Username drop-down menu -->
        <el-dropdown trigger="click" @command="handleCommand">
          <span class="el-dropdown-link">
            {{ username }}
            <el-icon class="el-icon--right">
              <arrow-down />
            </el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item divided command="loginout">
                {{ $t('exitLogin') }}
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">

/**
 * Copyright 2025 bbcdabao Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { onMounted, onBeforeUnmount, ref } from 'vue';
import { useSidebarStore } from '@/store/sidebar';
import { useHeaderStore } from '@/store/header';
import { useRouter } from 'vue-router';
import vLanguage from '@/components/language.vue';
import avatarImage from '@/assets/img/user-logo.jpg';
import pingMonitor from '@/assets/img/pm-logo-title.png';

const sidebar = useSidebarStore();
const header = useHeaderStore();
const username: string | null = localStorage.getItem('vuems_name');

const collapseChange = () => {
  sidebar.handleCollapse();
};

const router = useRouter();
const handleCommand = (command: string) => {
  if (command === 'loginout') {
    localStorage.removeItem('vuems_name');
    router.push('/login');
  }
};

const isFullScreen = ref(false);

const updateFullScreenStatus = () => {
  isFullScreen.value = !!document.fullscreenElement;
};

onMounted(() => {
  if (document.body.clientWidth < 600) {
    collapseChange();
  }
  document.addEventListener('fullscreenchange', updateFullScreenStatus);
  updateFullScreenStatus();
});

onBeforeUnmount(() => {
  document.removeEventListener('fullscreenchange', updateFullScreenStatus);
});

const setFullScreen = () => {
  if (document.fullscreenElement) {
    document.exitFullscreen();
  } else {
    document.documentElement.requestFullscreen();
  }
};

</script>
<style scoped>
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-sizing: border-box;
  width: 100%;
  height: var(--header-height);
  color: var(--header-color);
  border-bottom: 1px solid var(--header-line-color);
  background-color: var(--header-bg-color);
}
.header-left {
  display: flex;
  align-items: center;
  padding-left: 10px;
  height: 100%;
}
.collapse-btn {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  padding: 0 12px;
  cursor: pointer;
  opacity: 0.8;
  font-size: 22px;
}
.collapse-btn:hover {
  opacity: 1;
}
.collapse-img {
  margin-top: 5px;
  align-items: center;
}
@keyframes graduallyShow {
  0% {
    opacity: 0;
  }
  100% {
    opacity: 1;
  }
}
.ping-monitor {
  height: 32px;
  margin-left: 0px;
  animation: slideInRight 0.3s ease-in-out;
}
@keyframes slideInRight {
  from {
    transform: translateX(-100%);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}
.header-right {
  display: flex;
  justify-content: flex-end;
  padding-right: 50px;
}
.header-user-con {
  display: flex;
  height: var(--header-height);
  align-items: center;
}
.btn-icon {
  position: relative;
  width: 30px;
  height: 30px;
  text-align: center;
  cursor: pointer;
  display: flex;
  align-items: center;
  color: var(--header-color);
  margin: 0 5px;
  font-size: 20px;
}
.user-avator {
  margin: 0 10px 0 20px;
}
.el-dropdown-link {
  color: var(--header-color);
  cursor: pointer;
  display: flex;
  align-items: center;
}
.el-dropdown-menu__item {
  text-align: center;
}
</style>
