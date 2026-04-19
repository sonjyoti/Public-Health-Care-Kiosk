package com.kiosk.kioskdiscovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class KioskDiscoveryApplication {

    public static void main(String[] args) {
        SpringApplication.run(KioskDiscoveryApplication.class, args);
    }

}
