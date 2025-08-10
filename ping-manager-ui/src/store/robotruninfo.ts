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
  defineStore
} from 'pinia';
import {
  getRobotGroups
} from '@/api';
import type {
  RobotGroupInfo
} from '@/types/robot-sub';

import i18n from '@/i18n';

export const useRobotRuninfoStore = defineStore('robotruninfo', {
  state: () => ({
    robotGroups: {} as Record<string, RobotGroupInfo>
  }),
  getters: {
    getGroupDesc: (state) => (robotGroupName: string) => {
      const groupInfo = state.robotGroups[robotGroupName];
      if (!groupInfo) return robotGroupName;
      return i18n.global.locale.value === 'zh' ? 
      groupInfo.descriptionCn : groupInfo.descriptionEn;
    }
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
