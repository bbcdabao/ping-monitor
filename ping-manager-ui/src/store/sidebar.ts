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
                    itemname: 'overview',
                    itemicon: 'Histogram',
                    route: '/manager'
                },
                {
                    itemname: 'template',
                    itemicon: 'Tickets',
                    route: '/theme'
                },
                {
                    itemname: 'MainTest3',
                    itemicon: 'el-icon-lx-full',
                    route: '/main3'
                },
                {
                    itemname: 'MainTest4',
                    itemicon: 'el-icon-lx-full',
                    children: [
                        {
                            itemname: 'Child1',
                            itemicon: 'HomeFilled',
                            route: '/child1'
                        },
                        {
                            itemname: 'Child2',
                            itemicon: 'el-icon-lx-full',
                            route: '/child2'
                        },
                        {
                            itemname: 'Child3',
                            itemicon: 'el-icon-lx-full',
                            route: '/child3'
                        }
                    ]
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
