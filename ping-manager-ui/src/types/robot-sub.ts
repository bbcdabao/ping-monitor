/**
 * Copyright 2025 bbcdabao Team
 */

export interface RobotGroupInfo {
  robotGroupName: string;
  descriptionCn: string;
  descriptionEn: string;
}

export interface CheckRobotGroupInfo {
  robotGroupInfo: RobotGroupInfo;
  ischeck: boolean;
}