/**
 * Copyright 2025 bbcdabao Team
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
        redirect: '/theme-page',
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