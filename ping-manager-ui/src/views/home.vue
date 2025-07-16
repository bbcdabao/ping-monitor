<!-- Copyright 2025 bbcdabao Team -->

<template>
  <div class="wrapper">
    <v-header />
    <v-sidebar />
    <div class="content-box" :class="{ 'content-box-collapse': sidebar.collapse }">
      <div class="content">
        <router-view v-slot="{ Component }" :key="$route.fullPath">
          <transition name="move" mode="out-in">
            <component class="content-page pd6" :is="Component"></component>
          </transition>
        </router-view>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">

import { useSidebarStore } from '@/store/sidebar';
import vHeader from '@/components/header.vue';
import vSidebar from '@/components/sidebar.vue';

const sidebar = useSidebarStore();

</script>
<style>
.wrapper {
  height: 100vh;
  overflow: hidden;
}
.content-box {
  position: absolute;
  top: var(--header-height);
  bottom: 0;
  left: var(--sidebar-width);
  right: 0;
  overflow: hidden;
  -webkit-transition: left 0.3s ease-in-out;
  transition: left 0.3s ease-in-out;
}
.content-box-collapse {
  left: var(--sidebar-width-collapse);
}
.content {
  width: auto;
  height: 100%;
  padding: 0px;
  box-sizing: border-box;
}
.content::-webkit-scrollbar {
  width: 0;
}
.content-page {
  width: auto;
  height: calc(100% - 10px);
  overflow-y: scroll;
  background: var(--body-bg-color);
  scrollbar-width: auto;
  scrollbar-color: var(--scrollbar-index-bg-color) var(--scrollbar-bg-color);
}
.content-page::-webkit-scrollbar {
  width: 12px;
}
.content-page::-webkit-scrollbar-track {
  background: var(--scrollbar-bg-color);
  border-radius: 0px;
}
.content-page::-webkit-scrollbar-thumb {
  background-color: var(--scrollbar-index-bg-color);
  border-radius: 4px;
  border: 2px solid var(--scrollbar-index-bd-color);
}
.content-page::-webkit-scrollbar-thumb:hover {
  background-color: var(--scrollbar-index-bg-hover-color);
}
</style>
