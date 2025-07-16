<!-- Copyright 2025 bbcdabao Team -->
 
<template>
  <div class="sidebar-page">
    <el-menu
      class="sidebar-page-menu"
      :default-active="activeMenuIndex"
      :collapse="sidebar.collapse"
      :default-openeds="['1']"
      router
    >
      <template v-for="(menu, index) in sidemenu" :key="index">
        <el-menu-item v-if="menu.route" :index="menu.route">
          <el-icon v-if="menu.itemicon">
            <component :is="menu.itemicon" />
          </el-icon>
          <span>{{ menu.itemtitle }}</span>
        </el-menu-item>
        <el-sub-menu v-else :index="String(index)">
          <template #title>
            <el-icon>
              <Menu />
            </el-icon>
            <span>{{ menu.itemtitle }}</span>
          </template>
          <el-menu-item
            v-for="(child, subIndex) in menu.children"
            :key="subIndex"
            :index="child.route"
          >
            <el-icon v-if="child.itemicon">
              <component :is="child.itemicon" />
            </el-icon>
            <span>{{ child.itemtitle }}</span>
          </el-menu-item>
        </el-sub-menu>
      </template>
    </el-menu>
  </div>
</template>
<script setup lang="ts">

/**
 * Copyright 2025 bbcdabao Team
 */

import {
  computed
} from 'vue';
import {
  useI18n
} from 'vue-i18n';
import {
  ElMenu
} from 'element-plus';
import {
  useRoute
} from 'vue-router';
import {
  useSidebarStore
} from '@/store/sidebar';
import type {
  RouteLocationNormalizedLoaded
} from 'vue-router';

const { t } = useI18n();
const route = useRoute();
const activeMenuIndex = computed(() => route.path);

const sidebar = useSidebarStore();

const sidemenu = computed (() => {
  const processMenu = (menu: any[]) => {
  return menu
    .map(item => ({
      ...item,
      itemtitle: t(item.itemname),
      children: item.children ? processMenu(item.children) : undefined
    }));
  };
  return processMenu(sidebar.menuIndx);
});

</script>
<style scoped>
.sidebar-page {
  width: calc(var(--sidebar-width) + 1px);
  height: 100%;
}
.sidebar-page-menu:not(.el-menu--collapse) {
  width: auto;
}
.sidebar-page-menu {
  min-height: 100%;
  width: calc(var(--sidebar-width-collapse) + 1px);
  background-color: var(--sidebar-bg-color);
}
.el-menu-item.is-active {
  font-size: 14px;
  font-weight: bold !important;
  color: var(--sidebar-index-color) !important;
  background-color: var(--sidebar-index-bg-color) !important;
}
.el-menu-item.is-active::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 8px;
  height: 100%;
  background-color: var(--sidebar-index-before-color);
  border-radius: 0 8px 8px 0;
}
.el-menu-item {
  font-size: 14px;
  color: var(--sidebar-color) !important;
  background-color: var(--sidebar-bg-color) !important;
}
.el-menu-item:hover {
  font-size: 14px;
  color: var(--sidebar-index-color) !important;
  background-color: var(--sidebar-index-bg-color) !important;
}
.el-sub-menu {
  font-size: 14px;
  color: var(--sidebar-color) !important;
  background-color: var(--sidebar-bg-color) !important;
}
:deep(.el-sub-menu__title) {
  color: var(--sidebar-color);
}
:deep(.el-sub-menu__title:hover) {
  color: var(--sidebar-color);
  background-color: var(--sidebar-bg-color) !important;
}
</style>