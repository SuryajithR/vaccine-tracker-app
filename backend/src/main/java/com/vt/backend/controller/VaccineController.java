package com.vt.backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.vt.backend.entity.Vaccine;
import com.vt.backend.repo.VaccineRepository;

@RestController
@RequestMapping("/vaccines")
public class VaccineController {

  private final VaccineRepository vaccineRepo;

  public VaccineController(VaccineRepository vaccineRepo) {
    this.vaccineRepo = vaccineRepo;
  }

  @GetMapping
  public List<Vaccine> all() {
    return vaccineRepo.findAll();
  }
}
