package com.pm.patient_service.service;

import com.pm.patient_service.dto.PatientRequestDto;
import com.pm.patient_service.dto.PatientResponseDto;
import com.pm.patient_service.exception.EmailAlreadyExitsException;
import com.pm.patient_service.exception.PatientNotFoundException;
import com.pm.patient_service.grpc.BillingServiceGrpcClient;
import com.pm.patient_service.mapper.PatientMapper;
import com.pm.patient_service.model.Patient;
import com.pm.patient_service.repository.PatientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class PatientService {

    private static final Logger log = LoggerFactory.getLogger(PatientService.class);
    private final PatientRepository patientRepository;
    private final BillingServiceGrpcClient billingServiceGrpcClient;

    public PatientService(PatientRepository patientRepository, BillingServiceGrpcClient billingServiceGrpcClient) {
        this.patientRepository = patientRepository;
        this.billingServiceGrpcClient = billingServiceGrpcClient;
    }

    //fetch all the patients from db
    public List<PatientResponseDto> getPatients(){
        List<Patient> patients = patientRepository.findAll();

        return patients.stream()
                .map(PatientMapper::toDTO)
                .toList();
    }

    //insert new patient details in db
    public PatientResponseDto createPatient(PatientRequestDto patientRequestDto){
        if(patientRepository.existsByEmail(patientRequestDto.getEmail())){
            throw new EmailAlreadyExitsException("A patient with this EmailId already exists");
        }
        Patient newPatient = patientRepository.save(PatientMapper.toModel(patientRequestDto));

        log.info("entering grpc method ");
        try {
            billingServiceGrpcClient.createBillingAccount(
                    newPatient.getId().toString(),
                    newPatient.getName(),
                    newPatient.getEmail()
            );
            log.info("gRPC call successful");

        } catch (Exception e) {
            log.error("Error while calling Billing Service via gRPC", e);

            // Option 1: Fail the whole request (strict consistency)
            throw new RuntimeException("Billing service is unavailable. Please try again later.");

            // Option 2 (alternative): Don't fail, just log (eventual consistency)
            // log.warn("Continuing without billing account creation");
        }
        log.info("exiting grpc method ");

        return PatientMapper.toDTO(newPatient);
    }

    //update an existing patient details
    public PatientResponseDto updatePatient(UUID id, PatientRequestDto patientRequestDto){

        Patient patient = patientRepository.findById(id).orElseThrow(
                () -> new PatientNotFoundException("Patient Not found with ID", id));

        if(patientRepository.existsByEmailAndIdNot(patientRequestDto.getEmail(), id)){
            throw new EmailAlreadyExitsException("A patient with this EmailId already exists" + patientRequestDto.getEmail());
        }

        patient.setName(patientRequestDto.getName());
        patient.setEmail(patientRequestDto.getEmail());
        patient.setAddress(patientRequestDto.getAddress());
        patient.setDateofbirth(LocalDate.parse(patientRequestDto.getDateOfBirth()));

        Patient updatePatient = patientRepository.save(patient);
        return PatientMapper.toDTO(updatePatient);
    }

    public void deletePatient(UUID id){
        patientRepository.deleteById(id);
    }
}
