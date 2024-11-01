package com.aegis.project.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;


@Component
public class CustomHealthIndicator implements HealthIndicator{
    
    @Override
    public Health health() {
        try{
            return Health.up()
                .withDetail("app", "running")
                .build();
        } catch (Exception e) {
            return Health.down()
                .withException(e)
                .build();
        }
    }
}
