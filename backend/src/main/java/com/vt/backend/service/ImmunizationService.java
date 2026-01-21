package com.vt.backend.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.vt.backend.entity.Vaccine;

@Service
public class ImmunizationService {

  public String nextDueType(Vaccine vaccine, int doseNumber) {
    if (doseNumber < vaccine.getTotalDoses()) return "NEXT_DOSE";
    if (vaccine.getBoosterGapDays() != null) return "BOOSTER";
    return "NONE";
  }

  public LocalDate calculateNextDue(Vaccine vaccine, int doseNumber, LocalDate takenDate) {
    if (doseNumber < vaccine.getTotalDoses()) {
      return takenDate.plusDays(vaccine.getDefaultGapDays());
    }
    if (vaccine.getBoosterGapDays() != null) {
      return takenDate.plusDays(vaccine.getBoosterGapDays());
    }
    return null;
  }
}

