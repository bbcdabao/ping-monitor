import { defineStore } from 'pinia';

export const useHeaderStore = defineStore('header', {
    state: () => {
        return {
            messages: [] as string[],
            connected: false,
            eventSource: null as EventSource | null
        };
    },
    getters: {
    },
    actions: {
      metaInfoconnectToSSE() {
        if (this.eventSource) return // 已连接则不重复连接
        const source = new EventSource('http://your-server/sse-endpoint')
      
            source.onopen = () => {
              this.connected = true
              console.log('SSE 连接已建立')
            }
      
            source.onmessage = (event) => {
              console.log('收到消息：', event.data)
              this.messages.push(event.data)
            }
      
            source.onerror = (error) => {
              console.error('SSE 错误', error)
              source.close()
              this.connected = false
              this.eventSource = null
            }
      
            this.eventSource = source
          },
      
          disconnectSSE() {
            if (this.eventSource) {
              this.eventSource.close()
              this.connected = false
              this.eventSource = null
              console.log('SSE 连接已关闭')
            }
          },
    }
});