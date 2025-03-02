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
Zookeep model description:
/[path] (value describes "example") [
	/[subpath] (value describes "example") [
		...
	]
]

/sysconfig (JSON format system configuration "{pingcycle: 60000}")
/robot (Robot root directory) [
	/templates (Robot plug-in template) [
		/com_xxx_sss_PingCallTest(JSON format template "{pingTimeout:{type:LONG,desCn:超时时间,desEn:timeout},ipaddr:192.168.10.8}")
		/com_xxx_sss_HttpCallTest(JSON format template "{pingTimeout:{type:LONG,desCn:超时时间,desEn:timeout},url:http://test.com}")
		/com_xxx_sss_XXXXCallTest(JSON format template "{pingTimeout:{type:LONG,desCn:超时时间,desEn:timeout},calres:http://a.com}")
	]
	/register (Robot registration directory) [
		/rebot-xxx (The name of the robot group. The instances inside are temporary nodes) [
			/UUID01 (Temporary node "ip@procid")
			/UUID02 (Temporary node "ip@procid")
			/UUID03 (Temporary node "ip@procid")
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
/[path] (value describes "example") [
	/[subpath] (value describes "example") [
		...
	]
]
```
- The dial test system is a distributed application, based on zookeeper coordination. The zk data structure is as follows
The following is based on a zk namespace
```
Zookeep model description:
/[path] (value describes "example") [
	/[subpath] (value describes "example") [
		...
	]
]

/sysconfig (JSON format system configuration "{pingcycle: 60000}")
/robot (Robot root directory) [
	/templates (Robot plug-in template) [
		/com_xxx_sss_PingCallTest(JSON format template "{pingTimeout:{type:LONG,desCn:超时时间,desEn:timeout},ipaddr:192.168.10.8}")
		/com_xxx_sss_HttpCallTest(JSON format template "{pingTimeout:{type:LONG,desCn:超时时间,desEn:timeout},url:http://test.com}")
		/com_xxx_sss_XXXXCallTest(JSON format template "{pingTimeout:{type:LONG,desCn:超时时间,desEn:timeout},calres:http://a.com}")
	]
	/register (Robot registration directory) [
		/rebot-xxx (The name of the robot group. The instances inside are temporary nodes) [
			/UUID01 (Temporary node "ip@procid")
			/UUID02 (Temporary node "ip@procid")
			/UUID03 (Temporary node "ip@procid")
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

- Code module design, the following structure
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
