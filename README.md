[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![GitHub license](https://img.shields.io/github/license/bbcdabao/ping-monitor.svg)](https://github.com/bbcdabao/ping-monitor/blob/main/LICENSE)
[![GitHub code size in bytes](https://img.shields.io/github/languages/code-size/bbcdabao/ping-monitor.svg)](https://github.com/bbcdabao/ping-monitor)

# 📖 项目简介 ping-monitor
- **本项目是一个基于 ZooKeeper 的分布式拨测系统，能够对多种网络目标（如 主机、服务、API、端口，以及各类中间件如 Redis、Kafka 等）进行可用性探测与监控。系统采用 插件化架构，支持灵活扩展拨测方式，并通过与 Prometheus 集成输出指标，实现全链路的可观测性与告警能力。**
- **user:admin**
- **pass:123456**
# ✨ 主要特点
- **分布式调度:**

  _依托 ZooKeeper，支持机器人实例动态注册、任务分配、主从选举，保证任务在集群中可靠运行。_
- **插件化拨测:**
  
  _提供标准化模板机制，支持自定义扩展拨测插件（如 Ping、HTTP、TCP、redis、kafka 等，有专门的注解用户可自行开发查询快速接入系统）。_
- **配置中心:**
  
  _系统运行周期、任务配置、插件参数均存储在 ZooKeeper，集中管理，实时生效。_
- **可观测性支持:**
  
  _通过 ping-metric-exporter 模块对接 Prometheus，实现指标采集与告警。_ 
- **前后端分离:**
  
  _后端基于 Java，前端基于 Vue3 + TypeScript，提供统一的 Web 管理平台与 API 网关。_

# 📌 系统架构
  _系统核心，就是基于zookeeper分布式协调与数据感知，所有数据都保存在zookeeper上面，然后通过感知拨测数据变化  manager 后台通过 SSE 推动到 前端WEB实时展示。_
  <div style="display: flex; justify-content: space-between;">
    <img src="https://github.com/bbcdabao/ping-monitor/blob/main/docs/images/ping-monitor-frame.png" alt="" width="100%"/>
  </div>

# 🏗️ 核心模块
- **ping-common：**

  _公共封装模块，定义基础工具类与通用模型。_
- **ping-manager：**

  _后端服务，负责任务管理、调度控制、前端各种接口实现，和SSE推送，并且内部集成内置zookeeper，可以选择使用内部集成的zookeeper或者自己另行部署，单独部署zookeeper就增强稳定性_
- **ping-manager-ui：**

  _前端管理控制台（Vue3 + TS），任务配置、结果展示 等等..._
- **ping-manager-web：**

  _API 网关，负责统一鉴权与前端 UI 打包分发。_
- **ping-metric-exporter：**

  _Prometheus Exporter，对接监控系统 promeseus 等，将监控指标导出，该模块目前正在开发中，未实现。_
- **ping-robot：**

  _拨测机器人集群。_
- **ping-robot-api：**

  _机器人公共模块，定义机器人运行协议与接口，提供拨测插件化技术封装等..._
- **ping-robot-man：**

  _基于 ping-robot-api 插件实现模块，提供具体拨测插件（如 Ping、HTTP...）_

  **⚠️ 注意：拨测组件仅仅自测了 Ping、DNS 解析、TCP 链接测试 等这 3 个组件**
  
  _实现拨测插件方式很简单，继承一个接口IPingMoniterPlug，加上字段提取注解即可，它会自动模板化注入系统，例如:_
  ```java
  public class MyPingPlug implements IPingMoniterPlug {
    @ExtractionFieldMark(descriptionCn = "字段模板中文说明", descriptionEn = "Field template English description")
    private String xxxx01;

    @ExtractionFieldMark(descriptionCn = "字段模板中文说明", descriptionEn = "Field template English description")
    private Integer xxxx02;

    @Override
    public String doPingExecute(int timeOutMs) throws Exception {
      // TODO: Implementing dial test logic / 实现拨测逻辑
      return "OK";
    }
  }
  ```
  _上面的代码示例展示了一个插件的实现方式。您只需在 ping-robot-man 工程中新增自己的拨测插件，并启动 robot 拨测哨兵，系统即可自动完成插件的接入与运行，无需额外配置和处理_

# 🚀 快速开始（安装、运行、构建）
- __构建环境:__<br>
  - Useing jdk 17 or above version.
  - Useing node 22.4.0.
- __构建方法:__<br>
  - run "build-all.sh" in the root directory "ping-monitor".
- __构建结果:__<br>
  - manager.tar.gz
  - 上面为平台部署包，包括前后端和配套脚本和配置，如下图内容...
  ![manager](https://github.com/bbcdabao/ping-monitor/blob/main/docs/images/manager.png)
  - 上面内容：configs为配置文件目录，里面是前后端的配置，logs是系统运行日志输出目录，pids为保存启动的pid目录，ctl.sh是运行启动脚本，ping-manager.jar和ping-manager-web.jar 为后端 和 网关里面打包了前端UI。
  - robot.tar.gz
  - 上面为哨兵或机器人部署包，包括前后端和配套脚本和配置，如下图内容...
  ![manager](https://github.com/bbcdabao/ping-monitor/blob/main/docs/images/robot.png)
  - 上面内容：configs为配置文件目录，里面是多个可用去拨测哨兵或则机器人配置文件，logs是哨兵或机器人运行日志，pids为保存启动的pid目录，man.sh 是启动拨测哨兵或机器人脚本，ping-robot-man.jar是拨测机器人。
- __运行:__<br>
  - 首先部署平台 manager.tar.gz 然后启动 robot.tar.gz 实例注册到平台上，注意 manager 平台内置了zookeeper部署比较快捷，您也可以自己部署专门的zookeeper服务。
  - 部署步骤1：先解压 manager.tar.gz 和 robot.tar.gz 解压后如下图...
  ![manager](https://github.com/bbcdabao/ping-monitor/blob/main/docs/images/allinfo.png)
  - 部署步骤2：启动 manager 平台前端和后端
  ![manager](https://github.com/bbcdabao/ping-monitor/blob/main/docs/images/start-manager.png)
  ![manager](https://github.com/bbcdabao/ping-monitor/blob/main/docs/images/start-manager-web.png)
  - 部署步骤3：确认 manager 平台 前后端是否启动成功
  ![manager](https://github.com/bbcdabao/ping-monitor/blob/main/docs/images/manager-check.png)
  - 部署步骤4：启动 robot 实例，可以根据情况启动多个，下图是查看当前 robot 里面所有的配置
  ![manager](https://github.com/bbcdabao/ping-monitor/blob/main/docs/images/robot-list.png)
  - 部署步骤5：选择一个配置启动，可以start 多个配置，并且一个配置可以启动多次
  ![manager](https://github.com/bbcdabao/ping-monitor/blob/main/docs/images/robot-start-instance.png)
  - 部署步骤6：确认或查看启动的哨兵或机器人
  ![manager](https://github.com/bbcdabao/ping-monitor/blob/main/docs/images/robot-check.png)
     
# ⚙️ 配置说明
  _请查看configs目录下的配置文件，里面有注解说明，注意robot哨兵模块的configs里面可以增加或删除配置文件_
  ![manager](https://github.com/bbcdabao/ping-monitor/blob/main/docs/images/manager-config.png)
  ![manager](https://github.com/bbcdabao/ping-monitor/blob/main/docs/images/robot-config.png)

# 📊 示例效果（如截图或接口示例）
- __登录页面:__<br>
![manager](https://github.com/bbcdabao/ping-monitor/blob/main/docs/images/login.png)
- __观测结果页面，全局观测，提供平均SLA和RTT平滑滚动效果:__<br>
![manager](https://github.com/bbcdabao/ping-monitor/blob/main/docs/images/m1.png)
- __观测结果页面，选择重点观测，提供散点图:__<br>
![manager](https://github.com/bbcdabao/ping-monitor/blob/main/docs/images/m2.png)
- __哨兵模板页面，哨兵实例启动会把自己支持的组件信息模板自动注册到上面:__<br>
![manager](https://github.com/bbcdabao/ping-monitor/blob/main/docs/images/m3.png)
- __哨兵模板页面，选择一个模板，可以创建拨测任务:__<br>
![manager](https://github.com/bbcdabao/ping-monitor/blob/main/docs/images/m12.png)
- __哨兵组页面，目前注册的哨兵对应的组，也是自动收集的信息:__<br>
![manager](https://github.com/bbcdabao/ping-monitor/blob/main/docs/images/m7.png)
- __哨兵组页面，选择一个哨兵查看任务分配情况:__<br>
![manager](https://github.com/bbcdabao/ping-monitor/blob/main/docs/images/m8.png)
- __系统配置页面，可以修改立刻生效，会自动校验有效性:__<br>
![manager](https://github.com/bbcdabao/ping-monitor/blob/main/docs/images/m9.png)
- __主题选择页面:__<br>
![manager](https://github.com/bbcdabao/ping-monitor/blob/main/docs/images/m10.png)
![manager](https://github.com/bbcdabao/ping-monitor/blob/main/docs/images/m11.png)
- __任务信息页面，列出当前所有拨测任务:__<br>
![manager](https://github.com/bbcdabao/ping-monitor/blob/main/docs/images/m4.png)
- __任务信息页面，选择一个拨测任务，可以分配哨兵组:__<br>
![manager](https://github.com/bbcdabao/ping-monitor/blob/main/docs/images/m5.png)
- __任务结果页面，列出当前拨测结果列表，主要作用是可以临时删除一下结果:__<br>
![manager](https://github.com/bbcdabao/ping-monitor/blob/main/docs/images/m6.png)

# 🤝 贡献指南
  _目前是我个人的开源项目，也随时欢迎感兴趣的小伙伴加入_
