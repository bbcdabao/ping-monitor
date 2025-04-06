import { defineStore } from 'pinia';
import i18n from '@/i18n';

export const useSidebarStore = defineStore('sidebar', {
	state: () => {
        const t = i18n.global.t;
        const processMenu = (menu) => {
            return menu.map(item => ({
                ...item,
                itemtitle: t(`${item.itemname}`),
                children: item.children ? processMenu(item.children) : undefined
            }));
        };
		return {
			collapse: false,
            sidemenu: processMenu([
                {
                    itemname: 'pingallview',
                    itemicon: 'Histogram',
                    route: '/manager'
                },
                {
                    itemname: 'template',
                    itemicon: 'SetUp',
                    route: '/theme'
                },
                {
                    itemname: 'fortasks',
                    itemicon: 'Tickets',
                    route: '/aaaa1'
                },
                {
                    itemname: 'robotmonitor',
                    itemicon: 'Notification',
                    route: '/aaaa2'
                },
                {
                    itemname: 'robotaskinfo',
                    itemicon: 'Coordinate',
                    route: '/aaaa3'
                },
                {
                    itemname: 'roboworkinfo',
                    itemicon: 'Cpu',
                    route: '/aaaa4'
                },
                {
                    itemname: 'sysconfig',
                    itemicon: 'Tools',
                    route: '/aaaa5'
                }
            ])
		};
	},
	getters: {},
	actions: {
		handleCollapse() {
			this.collapse = !this.collapse;
		}
	}
});
