/**
 * Copyright 2025 bbcdabao Team
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
  pingresult: Pingresult;
}

export interface ResultInfo {
  taskName: string;
  pingresultInfo: PingresultInfo;
}

export interface ResultInfoRecord {
  taskName: string;
  pingresults: Record<string, Pingresult>;
}
