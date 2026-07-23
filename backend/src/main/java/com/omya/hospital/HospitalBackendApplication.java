package com.omya.hospital;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HospitalBackendApplication {

  public static void main(String[] args) {
    SpringApplication.run(
      HospitalBackendApplication.class,

      args
    );
  }
}
