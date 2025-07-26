/**
 * Copyright 2025 bbcdabao Team
 */

import { defineStore } from 'pinia';
import i18n from '@/i18n';

export const useSidebarStore = defineStore('sidebar', {
  state: () => {
    const t = i18n.global.t;
    return {
      collapse: false,
      menuIndx: [
        {
          itemname: 'pingallview',
          itemicon: 'lucide-airplay',
          route: '/manager',
        },
        {
          itemname: 'taskmanager',
          children: [
            {
              itemname: 'template',
              itemicon: 'lucide-scroll',
              route: '/template-page',
            },
            {
              itemname: 'taskinfo',
              itemicon: 'lucide-scroll-text',
              route: '/taskinfo-page',
            },
            {
              itemname: 'taskrslt',
              itemicon: 'lucide-list-tree',
              route: '/taskrslt',
            }
          ]
        },
        {
          itemname: 'robotmanager',
          children: [
            {
              itemname: 'robotorganiza',
              itemicon: 'lucide-grid',
              route: '/robotorganiza',
            },
            {
              itemname: 'robotinstance',
              itemicon: 'lucide-land-plot',
              route: '/robotinstance',
            }
          ]
        },
        {
          itemname: 'systemConfig',
          itemicon: 'lucide-settings',
          route: '/system-config-page',
        },
        {
          itemname: 'thememanager',
          itemicon: 'lucide-brush-cleaning',
          route: '/theme-page',
        }
      ]
    };
  },
  getters: {
  },
  actions: {
    handleCollapse() {
      this.collapse = !this.collapse;
    }
  }
});
