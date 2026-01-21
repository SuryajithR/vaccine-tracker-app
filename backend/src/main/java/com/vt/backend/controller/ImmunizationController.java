package com.vt.backend.controller;

import java.time.LocalDate;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import com.vt.backend.dto.AddRecordRequest;
import com.vt.backend.entity.ImmunizationRecord;
import com.vt.backend.entity.User;
import com.vt.backend.entity.Vaccine;
import com.vt.backend.repo.ImmunizationRecordRepository;
import com.vt.backend.repo.UserRepository;
import com.vt.backend.repo.VaccineRepository;
import com.vt.backend.service.ImmunizationService;

@RestController
@RequestMapping("/records")
public class ImmunizationController {

  private final ImmunizationRecordRepository recordRepo;
  private final VaccineRepository vaccineRepo;
  private final UserRepository userRepo;
  private final ImmunizationService immunizationService;

  public ImmunizationController(ImmunizationRecordRepository recordRepo,
                                VaccineRepository vaccineRepo,
                                UserRepository userRepo,
                                ImmunizationService immunizationService) {
    this.recordRepo = recordRepo;
    this.vaccineRepo = vaccineRepo;
    this.userRepo = userRepo;
    this.immunizationService = immunizationService;
  }

  @GetMapping
  public List<ImmunizationRecord> myRecords(HttpServletRequest req) {
    Integer userId = (Integer) req.getAttribute("userId");
    return recordRepo.findByUserIdOrderByTakenDateDesc(userId);
  }

  @PostMapping
  public ImmunizationRecord add(@RequestBody AddRecordRequest req, HttpServletRequest httpReq) {
    Integer userId = (Integer) httpReq.getAttribute("userId");

    User user = userRepo.findById(userId).orElseThrow();
    Vaccine vaccine = vaccineRepo.findById(req.vaccineId).orElseThrow();

    // Validate dose order
    int expectedDose = recordRepo
        .findTopByUserIdAndVaccineIdOrderByDoseNumberDesc(userId, vaccine.getId())
        .map(last -> last.getDoseNumber() + 1)
        .orElse(1);
    
    var lastOpt = recordRepo.findTopByUserIdAndVaccineIdOrderByDoseNumberDesc(userId, vaccine.getId());
    if (lastOpt.isPresent() && Boolean.TRUE.equals(lastOpt.get().getIsClosed())) {
      throw new RuntimeException("This vaccine is already marked as finished.");
    }

    int maxDoseAllowed = vaccine.getTotalDoses();
    if (vaccine.getBoosterGapDays() != null) {
      maxDoseAllowed = vaccine.getTotalDoses() + 1; // allow 1 booster only
    }

    if (req.doseNumber > maxDoseAllowed) {
      throw new RuntimeException("Dose exceeds allowed limit. Max allowed: " + maxDoseAllowed);
    }

        

    if (req.doseNumber == null || req.doseNumber != expectedDose) {
      throw new RuntimeException("Invalid dose number. Expected dose: " + expectedDose);
    }

    LocalDate taken = LocalDate.parse(req.takenDate);
    // Prevent multiple doses of same vaccine on same day
    boolean alreadyExists = recordRepo.existsByUserIdAndVaccineIdAndTakenDate(userId, vaccine.getId(), taken);
    if (alreadyExists) {
      throw new RuntimeException("Duplicate entry: this vaccine already has a record for today.");
    }

    LocalDate nextDue = immunizationService.calculateNextDue(vaccine, req.doseNumber, taken);

    ImmunizationRecord rec = new ImmunizationRecord();
    rec.setUser(user);
    rec.setVaccine(vaccine);
    rec.setDoseNumber(req.doseNumber);
    rec.setTakenDate(taken);
    rec.setNextDueDate(nextDue);

    return recordRepo.save(rec);
  }

  @DeleteMapping("/{id}")
  public String delete(@PathVariable Integer id, HttpServletRequest httpReq) {
    Integer userId = (Integer) httpReq.getAttribute("userId");
    ImmunizationRecord rec = recordRepo.findById(id).orElseThrow();

    if (!rec.getUser().getId().equals(userId)) {
      throw new RuntimeException("Not allowed");
    }

    recordRepo.deleteById(id);
    return "OK";
  }

  @PutMapping("/{id}/finish")
  public ImmunizationRecord finish(@PathVariable Integer id, HttpServletRequest httpReq) {
    Integer userId = (Integer) httpReq.getAttribute("userId");

    ImmunizationRecord rec = recordRepo.findById(id).orElseThrow();

    if (!rec.getUser().getId().equals(userId)) {
      throw new RuntimeException("Not allowed");
    }

    // mark finished
    rec.setIsClosed(true);
    rec.setNextDueDate(null);

    return recordRepo.save(rec);
  }

}
