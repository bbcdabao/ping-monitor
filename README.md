# ping-monitor

# Text description of the dial test system idea / 拨测系统思路文字描述：

- Zookeep model description:
```
/[path] (value describes "example") [
	/[subpath] (value describes "example") [
		...
	]
]
```
- 拨测系统为分布式应用，基于zookeeper协调，zk数据结构如下  
以下是基于一个zk命名空间下
```
/config (拨测配置) {
	拨测周期
}
/robot (拨测机器人路径) {
	/param-templates (拨测参数模版，注意 key是拨测插件名 value是模版描述 ) {
		com.xxx.sss.PingCallTest(采用Properties保存) = {字段名=JSON字符串描述该字段 主要包含 类型  中文描述  英文描述 ...}
		com.xxx.sss.HttpCallTest(采用Properties保存) = {字段名=JSON字符串描述该字段 主要包含 类型  中文描述  英文描述 ...}
		com.xxx.sss.XXXXCallTest(采用Properties保存) = {字段名=JSON字符串描述该字段 主要包含 类型  中文描述  英文描述 ...}
	}
	/register (机器人注册路径) {
		/rebot-xxx (机器人名字,里面多个实例都是一组的) {
			/ip@procid (机器人实例名字 __临时节点__，ip地址 + 进程ID 组合) {
				当前机器人插件列表
			}
			/ip@procid (机器人实例名字 __临时节点__，ip地址 + 进程ID 组合) {
				当前机器人插件列表
			}
			/tasks (拨测任务列表) {
				/task-01
				/task-02
			}
		}
	}
}

/tasks (拨测任务配置) {
	/task-01  (plugin = com.xxx.sss.PingCallTest) {
		mode (拨测调度形式，一般拨测，后续可能有高并发压测)
		config(采用Properties保存) = { ip=127.0.0.1, port=3251 }
		ping-result(拨测结果 里面节点带有TTL) {
			/rebot-xxx {300ms}
		}
	}
	/task-02  (plugin = com.xxx.sss.HttpCallTest) {
		mode (拨测调度形式，一般拨测，后续可能有高并发压测)
		config(采用Properties保存) = { url=https://baidu.com }
		ping-result(拨测结果 里面节点带有TTL) {
			/rebot-xxx {300ms}
		}
	}
}

```

- 代码模块设计，如下结构  
```
ping-monitor
│ 
│    
├── ping-common         // 公用部分代码
│   ├── src
│   └── pom.xml
│ 
├── ping-manager        // 拨测后台服务，内置zookeeper便于开发测试
│   ├── src
│   └── pom.xml
│ 
├── ping-manager-web    // 拨测后台服务，API网关
│   ├── src
│   └── pom.xml
│ 
├── ping-manager-ui     // 拨测后台服务，内置zookeeper便于开发测试
│   ├── src
│   └── package.json
│ 
├── ping-metric-exporter// 拨测结果监控感知，导出到Pro米修斯
│   ├── src
│   └── pom.xml
│
└── ping-robot          // 拨测机器人软件开发包
    │ 
    ├── ping-robot-api  // 支持机器人组开发件
    │   ├── src 
    │   └── pom.xml
    │ 
    ├── ping-robot-man  // 拨测机器人实现
    │   ├── src 
    │   └── pom.xml
    │ 
    └── pom.xml
```


https://chatgpt.com/share/6768b8f7-35f8-8003-89b0-10e4a36f09e2
