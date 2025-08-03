import {
  defineStore
} from 'pinia';
import {
  getRobotGroups
} from '@/api';
import type {
  RobotGroupInfo
} from '@/types/robot-sub';

export const useRobotgroupinfoStore = defineStore('robotgroupinfo', {
    state: () => ({
      robotGroups: {} as Record<string, RobotGroupInfo>
    }),
    getters: {
    },
    actions: {
      async updateRobotgroups() {
        const resData: RobotGroupInfo[] = await getRobotGroups(null);
        const groupMap: Record<string, RobotGroupInfo> = {};
        for (const item of resData) {
          groupMap[item.robotGroupName] = item;
        }
        this.robotGroups = groupMap;
      }
    }
});
