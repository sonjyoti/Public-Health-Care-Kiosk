package com.kiosk.kioskappointmentservice.controller;

import com.kiosk.kioskappointmentservice.model.Department;
import com.kiosk.kioskappointmentservice.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/appointments/departments")
@RequiredArgsConstructor
public class DepartmentController {
    private final DepartmentRepository departmentRepository;

    @GetMapping
    public ResponseEntity<List<Department>> getAllActive(){
        return ResponseEntity
                .ok(departmentRepository.findByIsActiveTrue());
    }

    @GetMapping("{code}")
    public ResponseEntity<Department> getByCode(@PathVariable String code){
        return departmentRepository.findByCode(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
