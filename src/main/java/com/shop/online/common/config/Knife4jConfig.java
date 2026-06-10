package com.shop.online.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j 接口文档配置
 */
@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Shop-Online 电商平台接口文档")
                        .description("B2C 单体电商网站后端 API 接口")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Shop-Online Team")
                                .email("dev@shop-online.com")));
    }
}
