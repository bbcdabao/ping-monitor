<template>
    <div class="this-page">
        <el-menu
            class="sidebar-el-menu"
            :default-active="onRoutes"
            :collapse="sidebar.collapse"
            :default-openeds="['1']"
            router
        >
            <template v-for="(menu, index) in sidebar.sidemenu" :key="index">
                <el-menu-item v-if="menu.route" :index="menu.route">
                    <el-icon v-if="menu.itemicon">
                        <component :is="icons[menu.itemicon]" />
                    </el-icon>
                    <span>{{ menu.itemtitle }}</span>
                </el-menu-item>
                <el-sub-menu v-else :index="String(index)">
                    <template #title>
                        <el-icon><Menu /></el-icon>
                        <span>{{ menu.itemtitle }}</span>
                    </template>
                    <el-menu-item
                        v-for="(child, subIndex) in menu.children"
                        :key="subIndex"
                        :index="child.route"
                    >
                        <el-icon v-if="child.itemicon">
                            <component :is="icons[child.itemicon]" />
                        </el-icon>
                        <span>{{ child.itemtitle }}</span>
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
import * as ElementPlusIconsVue from '@element-plus/icons-vue';

const route = useRoute();
const onRoutes = computed(() => {
    return route.path;
});

const icons = ElementPlusIconsVue;
const sidebar = useSidebarStore();
const themeStore = useThemeStore();

</script>
<style scoped>
.sidebar-el-menu:not(.el-menu--collapse) {
    width: 250px;
    border-top: 1px solid var(--sidebar-bd-color);
    border-right: 1px solid var(--sidebar-bd-color);
}
.sidebar-el-menu {
    min-height: 100%;
    background-color: var(--sidebar-bg-color);
    border-top: 1px solid var(--sidebar-bd-color);
    border-right: 1px solid var(--sidebar-bd-color);
}
.el-menu-item.is-active {
    font-size: 14px;
    color: var(--sidebar-index-color) !important;
    background-color: var(--sidebar-index-bg-color) !important;
    font-weight: bold !important;
}
.el-menu-item:hover {
    font-size: 14px;
    color: var(--sidebar-index-color) !important;
    background-color: var(--sidebar-index-bg-color) !important;
}
.el-menu-item {
    font-size: 14px;
    color: var(--sidebar-color) !important;
    background-color: var(--sidebar-bg-color) !important;
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
