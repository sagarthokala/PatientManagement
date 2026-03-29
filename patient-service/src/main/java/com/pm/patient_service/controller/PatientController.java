package com.pm.patient_service.controller;

import com.pm.patient_service.dto.PatientRequestDto;
import com.pm.patient_service.dto.PatientResponseDto;
import com.pm.patient_service.dto.validators.CreatePatientValidationGroup;
import com.pm.patient_service.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.groups.Default;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/patients")
@Tag(name = "Patient", description = "API for managing patient")
public class PatientController {

    private PatientService patientService;

    public PatientController(PatientService patientService){
        this.patientService = patientService;
    }

    @GetMapping
    @Operation(summary = "get all the patients")
    public ResponseEntity<List<PatientResponseDto>> getPatient(){
        List<PatientResponseDto> patientsList = patientService.getPatients();
        return ResponseEntity.ok().body(patientsList);
    }

    @PostMapping()
    @Operation(summary = "create patient")
    public ResponseEntity<PatientResponseDto> createPatient(
            @Validated({Default.class, CreatePatientValidationGroup.class}) @RequestBody PatientRequestDto patientRequestDto){
       PatientResponseDto patientResponseDto =  patientService.createPatient(patientRequestDto);
        return ResponseEntity.ok().body(patientResponseDto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing patient")
    public ResponseEntity<PatientResponseDto> updatePatient(
            @PathVariable UUID id, @Validated({Default.class}) @RequestBody PatientRequestDto patientRequestDto){
        PatientResponseDto patientResponseDto =  patientService.updatePatient(id, patientRequestDto);
        return ResponseEntity.ok().body(patientResponseDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a patient")
    public ResponseEntity<Void> deletePatient(@PathVariable UUID id){
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }
}
