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

export interface Pingresult {
  /**
   * ping 是否成功
   */
  success: boolean;

  /**
   * ping 的延迟时间（毫秒）
   */
  delay: number;

  /**
   * 错误信息（如果有）
   */
  info: string;
}

export interface PingresultInfo {
  robotGroupName: string;
  timestamp: number;
  pingresult: Pingresult;
}

export interface ResultInfo {
  taskName: string;
  pingresultInfo: PingresultInfo;
}

export interface ResultInfoRecord {
  taskName: string;
  pingresults: Record<string, PingresultInfo>;
}
