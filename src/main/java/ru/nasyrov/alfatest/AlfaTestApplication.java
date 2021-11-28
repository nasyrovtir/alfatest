package ru.nasyrov.alfatest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class AlfaTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlfaTestApplication.class, args);

    }


}
