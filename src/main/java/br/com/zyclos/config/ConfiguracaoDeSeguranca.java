package br.com.zyclos.config;

import br.com.zyclos.security.FiltroDeToken;
import br.com.zyclos.security.RespostasDeSeguranca;
import br.com.zyclos.security.ServicoDeDetalhesDoUsuario;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/** Regras de autenticacao, autorizacao e CORS da API. */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class ConfiguracaoDeSeguranca {

    private final FiltroDeToken filtroDeToken;
    private final RespostasDeSeguranca respostas;
    private final PropriedadesZyclos propriedades;

    public ConfiguracaoDeSeguranca(FiltroDeToken filtroDeToken, RespostasDeSeguranca respostas,
                                   PropriedadesZyclos propriedades) {
        this.filtroDeToken = filtroDeToken;
        this.respostas = respostas;
        this.propriedades = propriedades;
    }

    @Bean
    public SecurityFilterChain cadeiaDeFiltros(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(origens()))
                .sessionManagement(sessao -> sessao.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(erros -> erros
                        .authenticationEntryPoint(respostas)
                        .accessDeniedHandler(respostas))
                .authorizeHttpRequests(regras -> regras
                        // Entrada na plataforma e recuperacao de senha.
                        // Os demais caminhos de /api/auth, como /eu e /alterar-senha, exigem token.
                        .requestMatchers(HttpMethod.POST,
                                "/api/auth/login",
                                "/api/auth/cadastro",
                                "/api/auth/recuperar-senha",
                                "/api/auth/redefinir-senha").permitAll()
                        // Catalogo publico
                        .requestMatchers(HttpMethod.GET, "/api/anuncios", "/api/anuncios/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/categorias", "/api/categorias/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/filtros").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/perfis/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/imagens/**").permitAll()
                        // Documentacao e monitoramento
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**").permitAll()
                        .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // Painel administrativo
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .addFilterBefore(filtroDeToken, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource origens() {
        CorsConfiguration configuracao = new CorsConfiguration();
        configuracao.setAllowedOriginPatterns(propriedades.getCors().getOrigensPermitidas());
        configuracao.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuracao.setAllowedHeaders(List.of("*"));
        configuracao.setExposedHeaders(List.of("Location"));
        configuracao.setAllowCredentials(true);
        configuracao.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource fonte = new UrlBasedCorsConfigurationSource();
        fonte.registerCorsConfiguration("/**", configuracao);
        return fonte;
    }

    @Bean
    public PasswordEncoder codificadorDeSenha() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Monta o gerenciador de autenticacao sobre o repositorio de contas.
     *
     * <p>O provedor e criado aqui dentro, e nao exposto como bean, para que o Spring Security
     * nao tente montar uma segunda cadeia automatica a partir do UserDetailsService.
     */
    @Bean
    public AuthenticationManager gerenciadorDeAutenticacao(ServicoDeDetalhesDoUsuario detalhes,
                                                           PasswordEncoder codificador) {
        DaoAuthenticationProvider provedor = new DaoAuthenticationProvider(detalhes);
        provedor.setPasswordEncoder(codificador);
        return new ProviderManager(provedor);
    }
}
