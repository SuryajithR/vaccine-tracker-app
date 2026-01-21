package com.vt.backend.entity;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name="immunization_records")
@Data
public class ImmunizationRecord {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name="user_id", nullable=false)
  private User user;

  @ManyToOne
  @JoinColumn(name="vaccine_id", nullable=false)
  private Vaccine vaccine;

  private Integer doseNumber;

  private LocalDate takenDate;
  private LocalDate nextDueDate;
  private Boolean isClosed = false;

}
