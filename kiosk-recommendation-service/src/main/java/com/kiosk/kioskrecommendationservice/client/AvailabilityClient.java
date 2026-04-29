package com.kiosk.kioskrecommendationservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
// Feign is an HTTP client library that lets one microservice call another microservice's REST API as if it were a simple Java method call — without writing any HTTP boilerplate code.
@FeignClient(name = "kiosk-availability-service")
public interface AvailabilityClient {

    @GetMapping("/api/availability/doctors/department/{departmentCode}")
    List<DoctorResponse> getDoctorsByDepartment(@PathVariable String departmentCode);

    @GetMapping("/api/availability/slots/doctor/{doctorId}/load")
    Long getDoctorLoad(@PathVariable String doctorId);

    @GetMapping("/api/availability/doctors/{id}")
    DoctorResponse getDoctorById(@PathVariable String id);
}
