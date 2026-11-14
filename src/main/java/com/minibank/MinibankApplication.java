package com.minibank;

import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MinibankApplication {

    public static void main(String[] args) {
        // All timestamps are handled in UTC, independent of the host machine's zone.
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        SpringApplication.run(MinibankApplication.class, args);
    }
}
