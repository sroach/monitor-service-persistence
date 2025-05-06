package gy.roach.monitor.persistence.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.servers.Server
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig(
    @Value("\${server.servlet.context-path:}") private val contextPath: String
) {

    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("Monitor Service Persistence API")
                    .version("1.0.0")
                    .description("API for monitoring service persistence operations")
                    .contact(
                        Contact()
                            .name("Steve Roach")
                            .email("support@roach.gy")
                    )
                    .license(
                        License()
                            .name("Apache 2.0")
                            .url("https://www.apache.org/licenses/LICENSE-2.0")
                    )
            )
            .addServersItem(
                Server()
                    .url(contextPath)
                    .description("Current environment")
            )
    }
}