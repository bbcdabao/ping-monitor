# 📖 项目简介 ping-monitor
- **本项目是一个基于 ZooKeeper 的分布式拨测系统，能够对多种网络目标（如 主机、服务、API、端口，以及各类中间件如 Redis、Kafka 等）进行可用性探测与监控。系统采用 插件化架构，支持灵活扩展拨测方式，并通过与 Prometheus 集成输出指标，实现全链路的可观测性与告警能力。**
# ✨ 主要特点
- **分布式调度:**

  _依托 ZooKeeper，支持机器人实例动态注册、任务分配、主从选举，保证任务在集群中可靠运行。_
- **插件化拨测:**
  
  _提供标准化模板机制，支持自定义扩展拨测插件（如 Ping、HTTP、TCP、redis、kafka 等，有专门的注解用户可自行开发查询快速接入系统）。_
- **配置中心:**
  
  _系统运行周期、任务配置、插件参数均存储在 ZooKeeper，集中管理，实时生效。_
- **多级存储结果:**
  
  _小规模拨测结果可存储于 ZooKeeper，大规模场景下可写入 Redis，提升性能与可扩展性。_
- **可观测性支持:**
  
  _通过 ping-metric-exporter 模块对接 Prometheus，实现指标采集与告警。_ 
- **前后端分离:**
  
  _后端基于 Java，前端基于 Vue3 + TypeScript，提供统一的 Web 管理平台与 API 网关。_

# 📌 系统架构
  _系统核心，就是基于zookeeper分布式协调与数据感知，所有数据都保存在zookeeper上面，然后通过感知拨测数据变化  manager 后台通过 SSE 推动到 前端WEB实时展示。_
  <div style="display: flex; justify-content: space-between;">
    <img src="https://github.com/bbcdabao/ping-monitor/blob/main/docs/images/ping-monitor-frame.png" alt="" width="100%"/>
  </div>

🏗️ 核心模块
ping-common：公共封装模块，定义基础工具类与通用模型。
ping-manager：后端服务，负责任务管理、调度控制、结果处理。
ping-manager-ui：前端管理控制台（Vue3 + TS），支持任务配置、结果展示。
ping-manager-web：API 网关，负责统一鉴权与前端 UI 打包分发。
ping-metric-exporter：Prometheus Exporter，对接监控系统。
ping-robot：拨测机器人集群
ping-robot-api：机器人公共模块，定义机器人运行协议与接口。
ping-robot-man：插件实现模块，提供具体拨测插件（如 Ping、HTTP）。

🚀 快速开始（安装、运行、构建）

⚙️ 配置说明

📊 示例效果（如截图或接口示例）

🤝 贡献指南

📄 License


# Text description of the dial test system idea：
- The dial test system is a distributed application, based on zookeeper coordination. The zk data structure is as follows
The following is based on a zk namespace
```
/sysconfig
  └── (JSON format system configuration) "{pingcycle: 60000}"

/robot (Robot root directory)
├── /templates (Robot plugin templates)
│   ├── /com_xxx_sss_PingCallTest
│   │   └── (JSON format template) "{pingTimeout: {type: LONG, desCn: Timeout, desEn: timeout}, ipaddr: 192.168.10.8}"
│   ├── /com_xxx_sss_HttpCallTest
│   │   └── (JSON format template) "{pingTimeout: {type: LONG, desCn: Timeout, desEn: timeout}, url: http://test.com}"
│   ├── /com_xxx_sss_XXXXCallTest
│   │   └── (JSON format template) "{pingTimeout: {type: LONG, desCn: Timeout, desEn: timeout}, calres: http://a.com}"
├── /register (Robot registration directory)
│   ├── /rebot-xxx (Robot group name)
│   │   ├── /meta-info (Robot and task inf)
│   │   │   ├── /instance (Instance child nodes, all EPHEMERAL nodes)
│   │   │   │   ├── /UUID01 ("ip@procid")
│   │   │   │   ├── /UUID02 ("ip@procid")
│   │   │   ├── /tasks (Monitoring task list)
│   │   │   │   ├── /task-01
│   │   │   │   └── /task-02
│   │   ├── /run-info (Runing controle info)
│   │   │   ├── /election (Robot instance election)
│   │   │   ├── /master-instance (EPHEMERAL nodes)
│   │   │   ├── /tasks (Assigned tasks)
│   │   │   │   ├── /UUID01
│   │   │   │   │   └── /Utask-02
│   │   │   │   ├── /UUID02 ()
│   │   │   │   │   └── /Utask-01
/tasks (Task configuration)
├── /task-01 (Robot plugin template: com_xxx_sss_PingCallTest)
│   └── /config (Properties format) "{ip=127.0.0.1, port=3251}"
├── /task-02 (Robot plugin template: com_xxx_sss_HttpCallTest)
│   └── /config (Properties format) "{url=https://baiduaa.com}"
│
/result (Monitoring results, Use Zookeeper for small scale and Redis for large capacity)
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
│   │   └── (JSON格式模板) "{pingTimeout: {type: LONG, desCn: Timeout, desEn: timeout}, ipaddr: 192.168.10.8}"
│   ├── /com_xxx_sss_HttpCallTest
│   │   └── (JSON格式模板) "{pingTimeout: {type: LONG, desCn: Timeout, desEn: timeout}, url: http://test.com}"
│   ├── /com_xxx_sss_XXXXCallTest
│   │   └── (JSON格式模板) "{pingTimeout: {type: LONG, desCn: Timeout, desEn: timeout}, calres: http://a.com}"
├── /register (机器人注册目录)
│   ├── /rebot-xxx (机器人组名称)
│   │   ├── /meta-info
│   │   │   ├── /instance (实例子节点，都是临时节点)
│   │   │   │   ├── /UUID01 ("ip@procid")
│   │   │   │   ├── /UUID02 ("ip@procid")
│   │   │   ├── /tasks (拨测任务列表，子节点不可重复)
│   │   │   │   ├── /task-01
│   │   │   │   └── /task-02
│   │   ├── /run-info (运行调度控制信息)
│   │   │   ├── /election (各个实例选主)
│   │   │   ├── /master-instance (主机器人工作信息)
│   │   │   ├── /tasks (分派触发调度)
│   │   │   │   ├── /UUID01
│   │   │   │   │   └── /Utask-02
│   │   │   │   ├── /UUID02 ()
│   │   │   │   │   └── /Utask-01
/tasks (任务配置)
├── /task-01 (Robot plugin template: com_xxx_sss_PingCallTest)
│   └── /config (Properties format) "{ip=127.0.0.1, port=3251}"
├── /task-02 (Robot plugin template: com_xxx_sss_HttpCallTest)
│   └── /config (Properties format) "{url=https://baiduaa.com}"
│
/result (拨测结果， 小规模保存在本地Zookeeper，大规模保存在Redis)
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

            <template v-for="(menu, index) in menuData" :key="index">
                <!-- 如果存在 rout，则是一级菜单 -->
                <el-menu-item v-if="menu.rout" :index="menu.rout">
                <i :class="menu.icon"></i>
                <span>{{ menu.title }}</span>
                </el-menu-item>

                <!-- 否则，如果有 child，则是二级菜单 -->
                <el-sub-menu v-else :index="String(index)">
                <template #title>
                    <i :class="menu.icon"></i>
                    <span>{{ menu.title }}</span>
                </template>
                <el-menu-item
                    v-for="(child, subIndex) in menu.child"
                    :key="subIndex"
                    :index="child.rout"
                >
                    <i :class="child.icon"></i>
                    <span>{{ child.title }}</span>
                </el-menu-item>
                </el-sub-menu>
            </template>

