/**
 * Copyright 2025 bbcdabao Team
 */

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T | null;
}

export interface PageResult<T> {
  rows: T[];
  total?: number;
}
