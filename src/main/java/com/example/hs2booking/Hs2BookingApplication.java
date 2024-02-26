package com.example.hs2booking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class Hs2BookingApplication {

    public static void main(String[] args) {
        SpringApplication.run(Hs2BookingApplication.class, args);
    }

}
