package uit.is216.ai.battle.demo.securities;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityOpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("AI-Battle Authentication API - Claude Haiku Agent").version("1.0.0")
                        .description("JWT-based authentication API for AI-Battle application")
                        .contact(new Contact().name("AI-Battle Team").url("https://github.com/uit/ai-battle")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new io.swagger.v3.oas.models.Components().addSecuritySchemes("Bearer Authentication",
                        new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")
                                .description("Enter JWT token")));
    }
}
