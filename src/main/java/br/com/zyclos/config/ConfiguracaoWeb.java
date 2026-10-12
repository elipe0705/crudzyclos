package br.com.zyclos.config;

import br.com.zyclos.domain.Condicao;
import br.com.zyclos.domain.StatusAnuncio;
import br.com.zyclos.domain.StatusDenuncia;
import br.com.zyclos.domain.StatusProposta;
import br.com.zyclos.domain.StatusUsuario;
import br.com.zyclos.domain.TipoNegociacao;
import br.com.zyclos.domain.TipoNotificacao;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Serve as fotos enviadas e ensina o Spring a ler os enums do dominio nos parametros de consulta.
 *
 * <p>Sem esses conversores, um filtro como negociacao=Venda ou troca seria rejeitado, porque
 * a conversao padrao so aceita o nome da constante.
 */
@Configuration
public class ConfiguracaoWeb implements WebMvcConfigurer {

    private final PropriedadesZyclos propriedades;

    public ConfiguracaoWeb(PropriedadesZyclos propriedades) {
        this.propriedades = propriedades;
    }

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registro) {
        Path pasta = Paths.get(propriedades.getUpload().getDiretorio()).toAbsolutePath().normalize();
        registro.addResourceHandler("/api/imagens/**")
                .addResourceLocations(pasta.toUri().toString())
                .setCachePeriod(3600);
    }

    @Override
    public void addFormatters(@NonNull FormatterRegistry registro) {
        registro.addConverter(String.class, TipoNegociacao.class, TipoNegociacao::de);
        registro.addConverter(String.class, Condicao.class, Condicao::de);
        registro.addConverter(String.class, StatusAnuncio.class, StatusAnuncio::de);
        registro.addConverter(String.class, StatusUsuario.class, StatusUsuario::de);
        registro.addConverter(String.class, StatusDenuncia.class, StatusDenuncia::de);
        registro.addConverter(String.class, StatusProposta.class, StatusProposta::de);
        registro.addConverter(String.class, TipoNotificacao.class, TipoNotificacao::de);
    }
}
