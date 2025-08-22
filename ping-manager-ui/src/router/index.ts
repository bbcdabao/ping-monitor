/**
 * Licensed to the bbcdabao Team under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership. The bbcdabao Team licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { 
  createRouter,
  RouteRecordRaw,
  createWebHashHistory
} from 'vue-router';

import Home from '../views/home.vue';
import NProgress from 'nprogress';

import 'nprogress/nprogress.css';

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'Home',
    component: Home,
    children: [
      {
        path: '',
        name: 'home-default',
        redirect: '/pingmonitor-page',
      },
      {
        path: '/pingmonitor-page',
        name: 'pingmonitor',
        meta: {},
        component: () => import('@/views/pages/pingmonitor-page.vue'),
      },
      {
        path: '/template-page',
        name: 'template',
        meta: {},
        component: () => import('@/views/pages/template-page.vue'),
      },
      {
        path: '/taskinfo-page',
        name: 'taskinfo',
        meta: {},
        component: () => import('@/views/pages/taskinfo-page.vue'),
      },
      {
        path: '/resultinfo-page',
        name: 'resultinfo',
        meta: {},
        component: () => import('@/views/pages/resultinfo-page.vue'),
      },
      {
        path: '/robotgroupinfo-page',
        name: 'robotgroupinfo',
        meta: {},
        component: () => import('@/views/pages/robotgroupinfo-page.vue'),
      },
      {
        path: '/systemconfig-page',
        name: 'systemconfig',
        meta: {},
        component: () => import('@/views/pages/systemconfig-page.vue'),
      },
      {
        path: '/theme-page',
        name: 'theme',
        meta: {},
        component: () => import('@/views/pages/theme-page.vue'),
      }
    ]
  },
  {
    path: '/login',
    meta: { title: 'login', noAuth: true },
    component: () => import('@/views/pages/login.vue'),
  },
  {
    path: '/404',
    meta: { title: 'cannotfound', noAuth: true },
    component: () => import('@/views/pages/404.vue'),
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/404',
  }
];

const router = createRouter({
  history: createWebHashHistory(),
  routes,
});

router.beforeEach((to, from, next) => {
  NProgress.start();
  next();
});

router.afterEach(() => {
  NProgress.done();
});

export default router;