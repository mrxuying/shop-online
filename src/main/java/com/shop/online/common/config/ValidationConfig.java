package com.shop.online.common.config;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.hibernate.validator.HibernateValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 参数校验配置 — 快速失败模式，第一个校验失败即返回
 */
@Configuration
public class ValidationConfig {

    @Bean
    public Validator validator() {
        try (ValidatorFactory factory = Validation.byProvider(HibernateValidator.class)
                .configure()
                .failFast(true)
                .buildValidatorFactory()) {
            return factory.getValidator();
        }
    }
}
