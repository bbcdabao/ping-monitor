/**
 * Copyright 2025 bbcdabao Team
 */

export interface TemplateField {
  desCn: string;
  desEn: string;
  
  /**
   * BYTE
   * SHORT
   * BOOLEAN
   * INT
   * LONG
   * FLOAT
   * DOUBLE
   * STRING
   */
  type: string;
}

export interface PlugInfo {
  plugName: string;
  plugTemp: Record<string, TemplateField>;
}
