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

import { defineStore } from 'pinia';
import i18n from '@/i18n';

export const useSidebarStore = defineStore('sidebar', {
  state: () => {
    const t = i18n.global.t;
    return {
      collapse: false,
      rawMenu: [
        {
          itemname: 'pingallview',
          itemicon: 'Monitor',
          route: '/manager',
        },
        {
          itemname: 'taskmanager',
          children: [
            {
              itemname: 'template',
              itemicon: 'Memo',
              route: '/template',
            },
            {
              itemname: 'taskinfo',
              itemicon: 'Tickets',
              route: '/taskinfo',
            },
            {
              itemname: 'taskrslt',
              itemicon: 'Ticket',
              route: '/taskrslt',
            }
          ]
        },        {
          itemname: 'robotmanager',
          children: [
            {
              itemname: 'robotorganiza',
              itemicon: 'CollectionTag',
              route: '/robotorganiza',
            },
            {
              itemname: 'robotinstance',
              itemicon: 'PriceTag',
              route: '/robotinstance',
            }
          ]
        },
        {
          itemname: 'thememanager',
          itemicon: 'Brush',
          route: '/theme',
        }
      ]
    };
  },
  getters: {
    sidemenu: (state) => {
      const t = i18n.global.t;
      const processMenu = (menu) => {
        return menu.map(item => ({
          ...item,
          itemtitle: t(item.itemname),
          children: item.children ? processMenu(item.children) : undefined
        }));
      };
      return processMenu(state.rawMenu);
    }
  },
  actions: {
    handleCollapse() {
      this.collapse = !this.collapse;
    }
  }
});
