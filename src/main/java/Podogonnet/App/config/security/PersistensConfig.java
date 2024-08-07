package Podogonnet.App.config.security;

import Podogonnet.App.auditor.AuditorAwareImp;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class PersistensConfig {
    @Bean
    public AuditorAware<String>auditorProvider(){
        return new AuditorAwareImp();
    }

}
