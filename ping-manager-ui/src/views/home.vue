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
  box-sizing: border-box;
  width: auto;
  height: 100%;
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
