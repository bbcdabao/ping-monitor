[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![GitHub license](https://img.shields.io/github/license/bbcdabao/ping-monitor.svg)](https://github.com/bbcdabao/ping-monitor/blob/main/LICENSE)
[![GitHub code size in bytes](https://img.shields.io/github/languages/code-size/bbcdabao/ping-monitor.svg)](https://github.com/bbcdabao/ping-monitor)
[![GitHub release](https://img.shields.io/github/release/bbcdabao/ping-monitor.svg)](https://github.com/bbcdabao/ping-monitor/releases)

# 📖 Project Overview - ping-monitor
- **This project is a ZooKeeper-based distributed monitoring system designed to probe the availability of various network targets (e.g., hosts, services, APIs, ports, and middleware such as Redis, Kafka, etc.). The system adopts a plugin-based architecture to support flexible extension of monitoring methods and integrates with Prometheus to expose metrics, enabling full observability and alerting capabilities.**
- **Demo URL:**  
- **http://bbcdabao.com**

# ✨ Key Features
- **Distributed Scheduling:**

  _Based on ZooKeeper, supporting dynamic robot instance registration, task assignment, leader election, and ensuring reliable execution across the cluster._
- **Plugin-based Monitoring:**
  
  _Provides a standardized template mechanism to support custom probe plugins (such as Ping, HTTP, TCP, Redis, Kafka, etc.). With dedicated annotations, users can easily develop and integrate new probes into the system._
- **Configuration Center:**
  
  _System lifecycle, task configurations, and plugin parameters are all stored and managed centrally in ZooKeeper, taking effect in real time._
- **Observability:**
  
  _Integrated with Prometheus via the `ping-metric-exporter` module for metrics collection and alerting._
- **Frontend & Backend Separation:**
  
  _Backend is built with Java, frontend with Vue3 + TypeScript, providing a unified web management platform and API gateway._

# 📌 System Architecture
  _The core is based on ZooKeeper for distributed coordination and data awareness. All data is stored in ZooKeeper. Changes in monitoring data are sensed and pushed from the backend manager to the frontend in real-time via SSE._
  <div style="display: flex; justify-content: space-between;">
    <img src="https://github.com/bbcdabao/ping-monitor/blob/main/docs/images/ping-monitor-frame.png" alt="" width="100%"/>
  </div>

# 🏗️ Core Modules
- **ping-common:**

  _Common utility module, defining base utilities and shared models._
- **ping-manager:**

  _Backend service responsible for task management, scheduling, frontend APIs, and SSE push. It integrates an embedded ZooKeeper but can also connect to an external deployment for higher stability._
- **ping-manager-ui:**

  _Frontend admin console (Vue3 + TS) for task configuration, result visualization, etc._
- **ping-manager-web:**

  _API gateway responsible for unified authentication and frontend UI packaging & distribution._
- **ping-metric-exporter:**

  _Prometheus Exporter for exporting monitoring metrics. (Currently under development)_
- **ping-robot:**

  _Monitoring robot cluster._
- **ping-robot-api:**

  _Robot API module defining protocols and interfaces, encapsulating plugin-based monitoring logic._
- **ping-robot-man:**

  _Implementation module providing actual monitoring plugins (e.g., Ping, HTTP, etc.)._

  _Creating a custom monitoring plugin is simple: just implement the `IPingMoniterPlug` interface and annotate fields. The system will automatically template and inject it. Example:_
  ```java
  public class MyPingPlug implements IPingMoniterPlug {
    @ExtractionFieldMark(descriptionCn = "Field template CN description", descriptionEn = "Field template EN description")
    private String xxxx01;

    @ExtractionFieldMark(descriptionCn = "Field template CN description", descriptionEn = "Field template EN description")
    private Integer xxxx02;

    @Override
    public String doPingExecute(int timeOutMs) throws Exception {
      // TODO: Implement monitoring logic
      return "OK";
    }
  }
