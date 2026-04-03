package com.personal.Expense_Tracker.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private static final String SECURITY_SCHEME_NAME = "BearerAuth";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Personal Finance Tracker API")
                        .description("""
                                A personal finance management system that helps users track:
                                - **Expenses** with category and payment mode filtering
                                - **Income** with date range filtering
                                - **Debts** with partial payment tracking and ledger history
                                - **Receivables** with partial collection tracking and ledger history
                                - **User Balances** (Cash in Hand + Bank Balance)
                                
                                All endpoints (except /public/**) require a **Bearer JWT token**.
                                Use the Authorize button to set your token.
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Kavya")
                                .email("kavyaaggarwal460@gmail.com")))
                // Tell Swagger that this API uses Bearer JWT auth
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}
