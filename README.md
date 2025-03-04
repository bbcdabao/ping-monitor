# ping-monitor

# Text description of the dial test system idea：
- Zookeep model description:
```
/[path] (value describes "example") [
	/[subpath] (value describes "example") [
		...
	]
]
```
- The dial test system is a distributed application, based on zookeeper coordination. The zk data structure is as follows
The following is based on a zk namespace
```
/sysconfig (JSON format system configuration "{pingcycle: 60000}")
/robot (Robot root directory) [
	/templates (Robot plug-in template) [
		/com_xxx_sss_PingCallTest(JSON format template "{pingTimeout:{type:LONG,desCn:超时时间,desEn:timeout},ipaddr:192.168.10.8}")
		/com_xxx_sss_HttpCallTest(JSON format template "{pingTimeout:{type:LONG,desCn:超时时间,desEn:timeout},url:http://test.com}")
		/com_xxx_sss_XXXXCallTest(JSON format template "{pingTimeout:{type:LONG,desCn:超时时间,desEn:timeout},calres:http://a.com}")
	]
	/register (Robot registration directory) [
		/rebot-xxx (The name of the robot group. The instances inside are temporary nodes) [
			/instance/UUID01 (Temporary node "ip@procid")
			/instance/UUID02 (Temporary node "ip@procid")
			/instance/UUID03 (Temporary node "ip@procid")
			/config (Robot group configuration "{executionType:master or all}")
			/tasks (Dial test task list, child nodes cannot be repeated) [
				/task-01 (Scheduling concurrent configuration)
				/task-02 (Scheduling concurrent configuration)
			]
		]
	]
]
/tasks [
	/task-01 (Robot plug-in template name "com_xxx_sss_PingCallTest") [
		/config (Save using Properties "{ip=127.0.0.1, port=3251}")
		/result (Dial test results, sub-nodes with TTL) [
			/rebot-xxx (300ms)
		]
	}
	/task-02 (Robot plug-in template name "com_xxx_sss_HttpCallTest") [
		config (Save using Properties "{url=https://baidu.com}")
		/result (Dial test results, sub-nodes with TTL) [
			/rebot-xxx {300ms}
		]
	]
]
```

# 拨测系统思路文字描述：
- Zookeep 模型说明:
```
/[路径] (节点值描述 "例子") [
	/[子路径] (节点值描述 "例子") [
		...
	]
]
```
- 拨测系统为分布式应用，基于zookeeper协调，zk数据结构如下
以下是基于一个zk命名空间下
```
/sysconfig (JSON格式系统配置 "{pingcycle: 60000}")
/robot (机器人根目录) [
	/templates (机器人插件模板) [
		/com_xxx_sss_PingCallTest(JSON格式模板 "{pingTimeout:{type:LONG,desCn:超时时间,desEn:timeout},ipaddr:192.168.10.8}")
		/com_xxx_sss_HttpCallTest(JSON格式模板 "{pingTimeout:{type:LONG,desCn:超时时间,desEn:timeout},url:http://test.com}")
		/com_xxx_sss_XXXXCallTest(JSON格式模板 "{pingTimeout:{type:LONG,desCn:超时时间,desEn:timeout},calres:http://a.com}")
	]
	/register (机器人注册目录) [
		/rebot-xxx (机器人组名称,里面的实例都是临时节点) [
			/instance/UUID01 (临时节点 "ip@procid")
			/instance/UUID02 (临时节点 "ip@procid")
			/instance/UUID03 (临时节点 "ip@procid")
			/config (机器人组配置 "{executionType:master or all}")
			/tasks (拨测任务列表,子节点不可重复) [
				/task-01 (调度并发配置)
				/task-02 (调度并发配置)
			]
		]
	]
]
/tasks [
	/task-01 (机器人插件模板名称 "com_xxx_sss_PingCallTest") [
		/config (采用Properties保存 "{ip=127.0.0.1, port=3251}")
		/result (拨测结果,子结点带有TTL) [
			/rebot-xxx (300ms)
		]
	}
	/task-02 (机器人插件模板名称 "com_xxx_sss_HttpCallTest") [
		config (采用Properties保存 "{url=https://baidu.com}")
		/result (拨测结果,子结点带有TTL) [
			/rebot-xxx {300ms}
		]
	]
]
```

- Code module design, the following structure
```
ping-monitor
│ 
│    
├── ping-common         // Common part code
│   ├── src
│   └── pom.xml
│ 
├── ping-manager        // Dial and test background services, built-in zookeeper facilitates development and testing
│   ├── src
│   └── pom.xml
│ 
├── ping-manager-web    // Dial test background service, API gateway
│   ├── src
│   └── pom.xml
│ 
├── ping-manager-ui     // Dial and test background services, built-in zookeeper facilitates development and testing
│   ├── src
│   └── package.json
│ 
├── ping-metric-exporter// Dial test results to monitor perception and export to Prometheus
│   ├── src
│   └── pom.xml
│
└── ping-robot          // Dial test robot software development kit
    │ 
    ├── ping-robot-api  // Support robot group development components
    │   ├── src 
    │   └── pom.xml
    │ 
    ├── ping-robot-man  // Dial test robot implementation
    │   ├── src 
    │   └── pom.xml
    │ 
    └── pom.xml
```


https://chatgpt.com/share/6768b8f7-35f8-8003-89b0-10e4a36f09e2


# Zookeeper 节点结构设计

## /sysconfig
- **内容**: JSON格式系统配置
  - `{pingcycle: 60000}`

## /robot
- **内容**: 机器人根目录
  - **/templates**: 机器人插件模板
    - **/com_xxx_sss_PingCallTest**
      - **内容**: JSON格式模板
        - `{pingTimeout:{type:LONG, desCn:超时时间, desEn:timeout}, ipaddr:192.168.10.8}`
    - **/com_xxx_sss_HttpCallTest**
      - **内容**: JSON格式模板
        - `{pingTimeout:{type:LONG, desCn:超时时间, desEn:timeout}, url:[http://test.com](http://test.com)}`
    - **/com_xxx_sss_XXXXCallTest**
      - **内容**: JSON格式模板
        - `{pingTimeout:{type:LONG, desCn:超时时间, desEn:timeout}, calres:[http://a.com](http://a.com)}`
  - **/register**: 机器人注册目录
    - **/rebot-xxx**: 机器人组名称（临时节点）
      - **/instance**: 实例子节点（临时节点）
        - **/UUID01**: 临时节点 `ip@procid`
        - **/UUID02**: 临时节点 `ip@procid`
        - **/UUID03**: 临时节点 `ip@procid`
      - **/tasks**: 拨测任务列表（子节点不可重复）
        - **/task-01**: 调度并发配置
        - **/task-02**: 调度并发配置

## /tasks
- **内容**: 拨测任务配置
  - **/task-01**: 机器人插件模板名称 `com_xxx_sss_PingCallTest`
    - **/config**: 使用 `Properties` 保存
      - `{ip=127.0.0.1, port=3251}`
  - **/task-02**: 机器人插件模板名称 `com_xxx_sss_HttpCallTest`
    - **/config**: 使用 `Properties` 保存
      - `{url:[https://baidu.com](https://baidu.com)}`

## /result
- **内容**: 拨测结果（子节点带有 TTL）
  - **/task-01**
    - **/rebot-xxx**: `300ms`
  - **/task-02**
    - **/rebot-xxx**: `500ms`

