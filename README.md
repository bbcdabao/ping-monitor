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


📌 项目简介

✨ 功能特性

🏗️ 系统架构/模块说明

🚀 快速开始（安装、运行、构建）

⚙️ 配置说明

📊 示例效果（如截图或接口示例）

🤝 贡献指南

📄 License
