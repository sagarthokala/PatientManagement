package com.pm.patient_service.mapper;

import com.pm.patient_service.dto.PatientRequestDto;
import com.pm.patient_service.dto.PatientResponseDto;
import com.pm.patient_service.model.Patient;

import java.time.LocalDate;

public class PatientMapper {

    public static PatientResponseDto toDTO(Patient patient){
        PatientResponseDto patientDTO = new PatientResponseDto();

        patientDTO.setId(patient.getId());
        patientDTO.setName(patient.getName());
        patientDTO.setEmail(patient.getName());
        patientDTO.setAddress(patient.getAddress());
        patientDTO.setDateofbirth(patient.getDateofbirth().toString());

        return patientDTO;
    }

    public static Patient toModel(PatientRequestDto patientRequestDto){
        Patient patient = new Patient();

        patient.setName(patientRequestDto.getName());
        patient.setEmail(patientRequestDto.getEmail());
        patient.setAddress(patientRequestDto.getAddress());
        patient.setDateofbirth(LocalDate.parse(patientRequestDto.getDateOfBirth()));
        patient.setRegisteredDate(LocalDate.parse(patientRequestDto.getRegisteredDate()));

        return patient;
    }
}
