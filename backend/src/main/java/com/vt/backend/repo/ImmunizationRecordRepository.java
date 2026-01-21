package com.vt.backend.repo;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import com.vt.backend.entity.ImmunizationRecord;

public interface ImmunizationRecordRepository extends JpaRepository<ImmunizationRecord, Integer> {
  List<ImmunizationRecord> findByUserIdOrderByTakenDateDesc(Integer userId);

  Optional<ImmunizationRecord> findTopByUserIdAndVaccineIdOrderByDoseNumberDesc(Integer userId, Integer vaccineId);

  boolean existsByUserIdAndVaccineIdAndTakenDate(Integer userId, Integer vaccineId, LocalDate takenDate);
}

