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
import type {
  RobotGroupInstanceTaskInfo
} from '@/types/robot-sub';

export const useRobotRuninfoInstanceTasksStore = defineStore('robotRuninfoInstanceTasks', {
  state: () => ({
    _source: null as EventSource | null,
    instanceTasks: {} as Record<string, RobotGroupInstanceTaskInfo>
  }),
  getters: {
  },
  actions: {
    closeSource() {
      if (this._source) {
        try {
          this._source.close();
        } finally {
          this._source = null;
          this.instanceTasks = {};
        }
      }
    },
    beginSource(robotGroupName: string) {
      this.closeSource();
      if (!robotGroupName) {
        return;
      }
      this._source = new EventSource(`/api/robot/groups/${robotGroupName}/instancetasks/events`);
      this._source.onopen = () => {
        console.log('resultinfo:connected:ok');
      };
      this._source.addEventListener('create', (e) => {
        console.log('resultinfo:create:', e.data);
        const instanceTaskInfo = JSON.parse(e.data) as RobotGroupInstanceTaskInfo;
        this.instanceTasks[instanceTaskInfo.taskName] = instanceTaskInfo;
      });
      this._source.addEventListener('delete', (e) => {
        console.log('resultinfo:delete:', e.data);
        const instanceTaskInfo = JSON.parse(e.data) as RobotGroupInstanceTaskInfo;
        delete this.instanceTasks[instanceTaskInfo.taskName];
      });
    }
  }
});
