package com.vt.backend.dto;

import lombok.Data;

@Data
public class AddRecordRequest {
  public Integer vaccineId;
  public Integer doseNumber;
  public String takenDate; // yyyy-mm-dd
}
