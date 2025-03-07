# ping-monitor

# Text description of the dial test system idea：
- The dial test system is a distributed application, based on zookeeper coordination. The zk data structure is as follows
The following is based on a zk namespace
```
/sysconfig
  └── (JSON format system configuration) "{pingcycle: 60000}"

/robot (Robot root directory)
  ├── /templates (Robot plugin templates)
  │   ├── /com_xxx_sss_PingCallTest
  │   │     └── (JSON format template) "{pingTimeout: {type: LONG, desCn: Timeout, desEn: timeout}, ipaddr: 192.168.10.8}"
  │   ├── /com_xxx_sss_HttpCallTest
  │   │     └── (JSON format template) "{pingTimeout: {type: LONG, desCn: Timeout, desEn: timeout}, url: http://test.com}"
  │   ├── /com_xxx_sss_XXXXCallTest
  │         └── (JSON format template) "{pingTimeout: {type: LONG, desCn: Timeout, desEn: timeout}, calres: http://a.com}"
  ├── /register (Robot registration directory)
  │   ├── /rebot-xxx (Robot group name)
  │   │   ├──meta-info (Robot and task inf)
  │   │   │   ├── /instance (Instance child nodes, all temporary nodes)
  │   │   │   │   ├── /UUID01 ("ip@procid")
  │   │   │   │   ├── /UUID02 ("ip@procid")
  │   │   │   │   └── /UUID03 ("ip@procid")
  │   │   │   ├── /tasks (Monitoring task list, child nodes must be unique)
  │   │   │   │   ├── /task-01 (Scheduling concurrency configuration)
  │   │   │   │   └── /task-02 (Scheduling concurrency configuration)
  │   │   ├──run-info (Runing controle info)
  │   │   │   ├── /election (Robot instance election)
  │   │   │   ├── /task-fire (Task trigger)
  │   │   │   │   ├── /task-01 (task-01)
  │   │   │   │   ├── /task-02 (task-02)
  │   │   │   ├── /task-avge (Avg child nodes, all temporary nodes)
  │   │   │   │   ├── /UUID01 ("ip@procid")
  │   │   │   │   │   ├──/task-02
  │   │   │   │   ├── /UUID02 ("ip@procid")
  │   │   │   │   │   ├──/task-02
  │   │   │   │   └── /UUID03 ("ip@procid")

/tasks (Task configuration)
  ├── /task-01 (Robot plugin template: com_xxx_sss_PingCallTest)
  │   └── /config (Properties format) "{ip=127.0.0.1, port=3251}"
  ├── /task-02 (Robot plugin template: com_xxx_sss_HttpCallTest)
  │   └── /config (Properties format) "{url=https://baiduaa.com}"

/result (Monitoring results, child nodes have TTL)
  ├── /task-01
  │   ├── /rebot-xxx (300ms)
  │   └── /rebot-xxx (300ms)
  ├── /task-02
  │   ├── /rebot-xxx (300ms)
  │   └── /rebot-xxx (500ms)

```

# 拨测系统思路文字描述：
- 拨测系统是一个分布式应用，基于zookeeper协调，zk数据结构如下
下面是基于一个zk namespace:
```
/sysconfig
  └── (JSON格式系统配置) "{pingcycle: 60000}"

/robot (机器人根目录)
  ├── /templates (机器人插件模板)
  │   ├── /com_xxx_sss_PingCallTest 
  │   │     └── (JSON格式模板) "{pingTimeout: {type: LONG, desCn: 超时时间, desEn: timeout}, ipaddr: 192.168.10.8}"
  │   ├── /com_xxx_sss_HttpCallTest 
  │   │     └── (JSON格式模板) "{pingTimeout: {type: LONG, desCn: 超时时间, desEn: timeout}, url: http://test.com}"
  │   ├── /com_xxx_sss_XXXXCallTest 
  │         └── (JSON格式模板) "{pingTimeout: {type: LONG, desCn: 超时时间, desEn: timeout}, calres: http://a.com}"
  │
  ├── /register (机器人注册目录)
  │   ├── /rebot-xxx (机器人组名称)
  │   │   ├── /instance (实例子节点，都是临时节点)
  │   │   │   ├── /UUID01 ("ip@procid")
  │   │   │   ├── /UUID02 ("ip@procid")
  │   │   │   └── /UUID03 ("ip@procid")
  │   │   ├── /tasks (拨测任务列表，子节点不可重复)
  │   │   │   ├── /task-01 (调度并发配置)
  │   │   │   └── /task-02 (调度并发配置)

/tasks (任务配置)
  ├── /task-01 (机器人插件模板: com_xxx_sss_PingCallTest)
  │   └── /config (Properties格式) "{ip=127.0.0.1, port=3251}"
  ├── /task-02 (机器人插件模板: com_xxx_sss_HttpCallTest)
  │   └── /config (Properties格式) "{url=https://baiduaa.com}"

/result (拨测结果，子结点带有TTL)
  ├── /task-01
  │   ├── /rebot-xxx (300ms)
  │   └── /rebot-xxx (300ms)
  ├── /task-02
  │   ├── /rebot-xxx (300ms)
  │   └── /rebot-xxx (500ms)
```

- Code module design, the following structure (chinese:下面是代码结构设计)
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
