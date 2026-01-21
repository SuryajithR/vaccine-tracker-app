package com.vt.backend.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vt.backend.entity.ImmunizationRecord;
import com.vt.backend.entity.Vaccine;

public interface VaccineRepository extends JpaRepository<Vaccine, Integer> {

    Optional<ImmunizationRecord> findById(long longValue);
}
