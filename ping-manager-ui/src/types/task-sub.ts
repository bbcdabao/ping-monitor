/**
 * Copyright 2025 bbcdabao Team
 */

export interface AddTaskPayload {
  plugName: string;
  properties: Record<string, any>;
}

export interface AddTaskRobotGroupsPayload {
  robotGroups: string[];
}

export interface TaskInfo {
  taskName: string;
  plugName: string;
  properties: Record<string, string>;
}