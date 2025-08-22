/**
 * Licensed to the bbcdabao Team under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership. The bbcdabao Team licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

export interface SysconfigPayload {
  /**
   * Sentry dial test cycle
   * 哨兵拨测周期
   */
  cronTask: string;
  /**
   * Sentinel master node detection cycle
   * 哨兵主节点检测周期
   */
  cronMain: string;
  /**
   * Sentinel node detection period
   * 哨兵节点检测周期
   */
  cronInst: string;
  /**
   * Ping time out
   * 拨测超时
   */
  timeOutMs: number;
}

export interface SysconfigInfo {
  /**
   * Sentry dial test cycle
   * 哨兵拨测周期
   */
  cronTask: string;
  /**
   * Sentinel master node detection cycle
   * 哨兵主节点检测周期
   */
  cronMain: string;
  /**
   * Sentinel node detection period
   * 哨兵节点检测周期
   */
  cronInst: string;
  /**
   * Ping time out
   * 拨测超时
   */
  timeOutMs: number;
  /**
   * Result store type
   */
  rsType: string;
}