package com.wiiee.server.push;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan("com.wiiee.server.common.domain")
@EnableJpaAuditing
@SpringBootApplication
@EnableJpaRepositories("com.wiiee.server.push.repository")
public class PushApplication {

    public static void main(String[] args) {
        SpringApplication.run(PushApplication.class, args);
    }

}
