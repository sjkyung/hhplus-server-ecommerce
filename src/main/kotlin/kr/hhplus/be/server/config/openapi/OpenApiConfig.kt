package kr.hhplus.be.server.config.openapi

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Bean
    fun openApi(): OpenAPI{
        return OpenAPI()
            .info(
                Info()
                    .title("hhplus E-commerce API Document")
                    .description("항해 플러스 이커머스 API 명세")
                    .version("v1.0.0")
            )
    }
}