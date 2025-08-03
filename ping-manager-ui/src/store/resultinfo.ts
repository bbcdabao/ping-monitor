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
      beginSource() {
        this.closeSource();     
        this._source = new EventSource('/api/results/events');
        this._source.onopen = () => {
          console.log('resultinfo:connected:ok')
        };
        this._source.addEventListener('create', (e) => {
          console.log('resultinfo:create:', e.data);
          const resultInfo = JSON.parse(e.data) as ResultInfo;
          const taskName = resultInfo.taskName;
          if (!this.results[taskName]) {
            this.results[taskName] = {
              taskName: taskName,
              pingresults: {}
            };
          }
          const pingresultInfo = resultInfo.pingresultInfo;
          if (!pingresultInfo) {
            return;
          }
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
              taskName: taskName,
              pingresults: {}
            };
          }
          const pingresultInfo = resultInfo.pingresultInfo;
          if (!pingresultInfo) {
            return;
          }
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
