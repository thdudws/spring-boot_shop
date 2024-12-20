package com.shop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing //Auditing 활성화
//Auditing -> 엔티티의 생성, 수정, 삭제와 같은 작업에 대해 자동으로 정보를 기록하는 기능
public class AuditConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return new AuditorAwareImpl(); //등록자, 수정자 처리해주는 AuditorAware bean 등록
    }

}
