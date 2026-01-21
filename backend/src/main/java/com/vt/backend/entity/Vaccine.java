package com.vt.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name="vaccines")
@Data
public class Vaccine {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(unique = true, nullable = false)
  private String name;

  private Integer totalDoses;
  private Integer defaultGapDays;

  private Integer boosterGapDays; // nullable
}
