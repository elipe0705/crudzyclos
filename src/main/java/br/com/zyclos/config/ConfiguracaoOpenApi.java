package br.com.zyclos.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Documentacao interativa publicada em /swagger-ui.html. */
@Configuration
public class ConfiguracaoOpenApi {

    private static final String ESQUEMA = "bearerAuth";

    @Bean
    public OpenAPI documentacao() {
        return new OpenAPI()
                .info(new Info()
                        .title("ZYCLOS API")
                        .version("1.0.0")
                        .description("""
                                Backend da plataforma de moda circular ZYCLOS.
                                Cobre contas, catalogo de anuncios, favoritos, conversas,
                                propostas de troca, avaliacoes, notificacoes e o painel administrativo.
                                """)
                        .contact(new Contact().name("Equipe ZYCLOS"))
                        .license(new License().name("Uso academico")))
                .components(new Components().addSecuritySchemes(ESQUEMA, new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("Token devolvido por POST /api/auth/login")))
                .addSecurityItem(new SecurityRequirement().addList(ESQUEMA));
    }
}
