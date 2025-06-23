package org.example.qraft_technologies;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class QraftTechnologiesApplication {

    public static void main(String[] args) {
        SpringApplication.run(QraftTechnologiesApplication.class, args);
    }

}
