package com.kiosk.kioskavailabilityservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class KioskAvailabilityServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(KioskAvailabilityServiceApplication.class, args);
    }

}
