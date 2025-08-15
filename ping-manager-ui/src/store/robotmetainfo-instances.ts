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
  RobotInstanceInfo
} from '@/types/robot-sub';

export const useRobotMetainfoInstancesStore = defineStore('robotMetainfoInstances', {
  state: () => ({
    _source: null as EventSource | null,
    instances: {} as Record<string, RobotInstanceInfo>
  }),
  getters: {
    isInstancesEmpty(state): boolean {
      return Object.keys(state.instances).length === 0;
    },
    instancesCount(state): number {
      return Object.keys(state.instances).length;
    }
  },
  actions: {
    closeSource() {
      if (this._source) {
        try {
          this._source.close();
        } finally {
          this._source = null;
          this.instances = {};
        }
      }
    },
    beginSource(robotGroupName: string) {
      this.closeSource();
      if (!robotGroupName) {
        return;
      }
      this._source = new EventSource(`/api/robot/groups/${robotGroupName}/instances/events`);
      this._source.onopen = () => {
        console.log('resultinfo:connected:ok');
      };
      this._source.addEventListener('create', (e) => {
        console.log('resultinfo:create:', e.data);
        const robotInstanceInfo = JSON.parse(e.data) as RobotInstanceInfo;
        this.instances[robotInstanceInfo.robotUUID] = robotInstanceInfo;
      });
      this._source.addEventListener('delete', (e) => {
        console.log('resultinfo:delete:', e.data);
        const robotInstanceInfo = JSON.parse(e.data) as RobotInstanceInfo;
        delete this.instances[robotInstanceInfo.robotUUID];
      });
    }
  }
});
