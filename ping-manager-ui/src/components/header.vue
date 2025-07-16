<!-- Copyright 2025 bbcdabao Team -->

<template>
  <div class="header">
    <!-- Collapse button -->
    <div class="header-left">
      <div class="header-btn" @click="collapseChange">
        <el-icon v-if="sidebar.collapse">
          <Expand />
        </el-icon>
        <el-icon v-else>
          <Fold />
        </el-icon>
      </div>
      <div class="collapse-img" v-if="!sidebar.collapse">
        <img :src="logoTitle" alt="ElectricityMarket" class="logo-title">
      </div>
    </div>
    <!--
    <div v-if="userinfoStore.isadmin" style="font-weight: bold;"></div>
    -->
    <div class="header-right">
      <div class="header-btn">
        <el-tooltip effect="dark" :content="$t('setTheme')" placement="bottom">
          <el-icon @click="router.push('/theme-page')">
            <lucide-brush-cleaning />
          </el-icon>
        </el-tooltip>
      </div>
      <div class="header-btn">
        <el-tooltip effect="dark" :content="$t('fullScreen')" placement="bottom">
          <el-icon v-if="isFullScreen"  @click="setFullScreen">
            <lucide-minimize />
          </el-icon>
          <el-icon v-else  @click="setFullScreen">
            <lucide-maximize />
          </el-icon>
        </el-tooltip>
        <!-- Language selection -->
      </div>
      <div class="header-btn">
        <vLanguage />
      </div>
      <div class="header-btn">
        <!-- User avatar -->
        <el-avatar style="margin-right: 4px;" shape="square" :size="32" :src="avatarImage" />
        <!-- Username drop-down menu -->
        <el-dropdown trigger="click" @command="handleCommand">
          <span class="el-dropdown-link">
            {{ header.titlesp }}
            <el-icon class="el-icon--right">
              <lucide-chevron-down />
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
 */

import {
  ref,
  watch,
  onMounted,
  onBeforeUnmount
} from 'vue';
import {
  useRouter
} from 'vue-router';
import {
  useHeaderStore
} from '@/store/header';
import {
  useSidebarStore
} from '@/store/sidebar';

import avatarImage from '@/assets/img/user-logo.png';
import vLanguage from '@/components/language.vue';
import logoTitle from '@/assets/img/title-logo.png';

const sidebar = useSidebarStore();
const header = useHeaderStore();

const collapseChange = () => {
  sidebar.handleCollapse();
};

const router = useRouter();
const handleCommand = (command: string) => {
  if (command === 'loginout') {
    router.replace('/login');
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

watch(
  () => sidebar.collapse,
  (newVal, oldVal) => {
    window.dispatchEvent(new Event('resize'));
  }
);

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
  background-color: var(--header-bg-color);
  border-bottom: 1px solid var(--header-line-color);
}
.header-left {
  display: flex;
  align-items: center;
  padding-left: 10px;
  height: 100%;
}
.header-right {
  display: flex;
  justify-content: flex-end;
  padding-right: 50px;
  height: 100%;
}
.header-btn {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  margin: 0 12px;
  cursor: pointer;
  opacity: 1;
  font-size: 22px;
}
.header-btn:hover {
  opacity: 1;
}
.collapse-img {
  margin-top: 10px;
  margin-left: 0px;
  align-items: center;
}
.logo-title {
  height: 29px;
  margin-left: 0px;
  animation: graduallyShow 2s ease-in-out;
}
@keyframes graduallyShow {
  0% {
    opacity: 0;
  }
  100% {
    opacity: 1;
  }
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