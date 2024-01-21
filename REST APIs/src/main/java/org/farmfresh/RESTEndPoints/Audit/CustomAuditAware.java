package org.farmfresh.RESTEndPoints.Audit;

import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

public class CustomAuditAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of("Bertram");
    }

    @Bean
    public AuditorAware<String> auditorAware(){
        return new CustomAuditAware();
    }
}