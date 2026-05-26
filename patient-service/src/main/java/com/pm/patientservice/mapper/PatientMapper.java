package com.pm.patientservice.mapper;

import java.time.LocalDate;

import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.model.Patient;

public class PatientMapper {
    public static PatientResponseDTO toPatientResponseDTO(Patient patient) {
        PatientResponseDTO patientDto = new PatientResponseDTO();
        patientDto.setId(patient.getId().toString());
        patientDto.setName(patient.getName());
        patientDto.setEmail(patient.getEmail());
        patientDto.setAddress(patient.getAddress());
        patientDto.setDateOfBirth(patient.getDateOfBirth().toString());
        if(patient.getRegisteredDate()==null){
            patientDto.setRegisteredDate(LocalDate.now().toString());
        }else{
            patientDto.setRegisteredDate(patient.getRegisteredDate().toString());
        }
        return patientDto;
    }

    public static Patient toPatient(PatientRequestDTO patientDto) {
        Patient patient = new Patient();
        patient.setName(patientDto.getName());
        patient.setEmail(patientDto.getEmail());
        patient.setAddress(patientDto.getAddress());
        patient.setDateOfBirth(LocalDate.parse(patientDto.getDateOfBirth()));
        patient.setRegisteredDate(LocalDate.parse(patientDto.getRegisteredDate()));
        if(patientDto.getRegisteredDate()==null){
            patient.setRegisteredDate(LocalDate.now());
        }else{
            patient.setRegisteredDate(LocalDate.parse(patientDto.getRegisteredDate()));
        }
        return patient;
    }

    
}
