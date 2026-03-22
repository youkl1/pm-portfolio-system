package com.pmportfolio.hub.common;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("产品经理个人作品展示系统 API")
                        .version("1.0.0")
                        .description("产品经理个人作品展示系统后端API文档")
                        .termsOfService("http://localhost:8080")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }
}
