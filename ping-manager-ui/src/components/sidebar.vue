<template>
    <div class="sidebar">
        <el-menu
            class="sidebar-el-menu"
            :default-active="onRoutes"
            :collapse="sidebar.collapse"
            :background-color="themeStore.sidebarBgColor"
            :text-color="themeStore.sidebarTextColor"
            :default-openeds="['1']"
            router
        >
            <template v-for="(menu, index) in sidebar.sidemenu" :key="index">
                <el-menu-item v-if="menu.route" :index="menu.route">
                    <i :class="menu.itemicon"></i>
                    <span>{{ menu.itemname }}</span>
                </el-menu-item>
                <el-sub-menu v-else :index="String(index)">
                    <template #title>
                        <el-icon><Menu /></el-icon>
                        <span>{{ menu.itemname }}</span>
                    </template>
                    <el-menu-item
                        v-for="(child, subIndex) in menu.children"
                        :key="subIndex"
                        :index="child.route"
                    >
                        <i :class="child.itemicon"></i>
                        <span>{{ child.itemname }}</span>
                    </el-menu-item>
                </el-sub-menu>
            </template>
        </el-menu>
    </div>
</template>
<script setup lang="ts">
import { computed } from 'vue';
import { useSidebarStore } from '@/store/sidebar';
import { useThemeStore } from '@/store/theme';
import { useRoute } from 'vue-router';
import { ElMenu } from 'element-plus';

const route = useRoute();
const onRoutes = computed(() => {
    return route.path;
});

const sidebar = useSidebarStore();
const themeStore = useThemeStore();
</script>
<style scoped>
.sidebar {
    display: block;
    position: absolute;
    left: 0;
    top: var(--header-height);
    bottom: 0;
    overflow-y: scroll;
}
.sidebar::-webkit-scrollbar {
    width: 0;
}
.sidebar-el-menu:not(.el-menu--collapse) {
    width: 249px;
}
.sidebar-el-menu {
    min-height: 100%;
}
.sidebar-el-menu .el-menu-item.is-active {
  font-size: 14px;
  background-color: var(--sidebar-index-bg-color) !important;
  color: var(--sidebar-index-text-color) !important;
  font-weight: bold !important;
}
.el-sub-menu .el-menu-item i {
    font-size: 18px;
}

</style>
