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
  ResultInfo,
  ResultInfoRecord
} from '@/types/result-sub';

export const useResultinfoStore = defineStore('resultinfo', {
  state: () => ({
    _source: null as EventSource | null,
    taskNameIndex: null as string | null,
    results: {} as Record<string, ResultInfoRecord>,
    resultInfo: {} as ResultInfo
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
          this.results = {};
        }
      }
    },
    beginSource(taskName?: string | null) {
      this.closeSource();
      this.taskNameIndex = taskName;
      if (this.taskNameIndex) {
        this._source = new EventSource('/api/result/events/' + taskName);
      } else {
        this._source = new EventSource('/api/result/events');
      }
      this._source.onopen = () => {
        console.log('resultinfo:connected:ok');
      };

      this._source.addEventListener('create', (e) => {
        console.log('resultinfo:create:', e.data);
        const resultInfo = JSON.parse(e.data) as ResultInfo;
        const taskName = resultInfo.taskName;

        if (!this.results[taskName]) {
          this.results[taskName] = {
            taskName,
            pingresults: {}
          };
        }

        const pingresultInfo = resultInfo.pingresultInfo;
        if (!pingresultInfo) return;

        const resultInfoRecord = this.results[taskName];
        resultInfoRecord.pingresults[pingresultInfo.robotGroupName] = pingresultInfo;

        this.resultInfo = resultInfo;
      });

      this._source.addEventListener('update', (e) => {
        console.log('resultinfo:update:', e.data);
        const resultInfo = JSON.parse(e.data) as ResultInfo;
        const taskName = resultInfo.taskName;

        if (!this.results[taskName]) {
          this.results[taskName] = {
            taskName,
            pingresults: {}
          };
        }

        const pingresultInfo = resultInfo.pingresultInfo;
        if (!pingresultInfo) return;

        const resultInfoRecord = this.results[taskName];
        resultInfoRecord.pingresults[pingresultInfo.robotGroupName] = pingresultInfo;

        this.resultInfo = resultInfo;
      });

      this._source.addEventListener('delete', (e) => {
        console.log('resultinfo:delete:', e.data);
        const resultInfo = JSON.parse(e.data) as ResultInfo;
        const taskName = resultInfo.taskName;
        const pingresultInfo = resultInfo.pingresultInfo;

        if (!pingresultInfo) {
          delete this.results[taskName];
          return;
        }

        const resultInfoRecord = this.results[taskName];
        delete resultInfoRecord.pingresults[pingresultInfo.robotGroupName];
      });

      this._source.onerror = (error) => {
        console.log('resultinfo:error:', error);
        this.closeSource();
      };
    }
  }
});