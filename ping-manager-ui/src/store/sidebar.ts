import { defineStore } from 'pinia';
import { Sshitem } from '@/types/sshitem';

export const useSidebarStore = defineStore('sidebar', {
	state: () => {
		return {
			collapse: false,
            sidemenu: [
                {
                    itemname: 'MainTest1',
                    itemicon: 'el-icon-lx-full',
                    route: '/manager'
                },
                {
                    itemname: 'MainTest2',
                    itemicon: 'el-icon-lx-full',
                    route: '/main2'
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
                            itemicon: 'el-icon-lx-full',
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
            ]
		};
	},
	getters: {},
	actions: {
		handleCollapse() {
			this.collapse = !this.collapse;
		}
	}
});
