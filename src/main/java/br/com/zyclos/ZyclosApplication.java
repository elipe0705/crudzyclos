package br.com.zyclos;

import br.com.zyclos.config.PropriedadesZyclos;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/** Ponto de entrada da API ZYCLOS. */
@SpringBootApplication
@EnableConfigurationProperties(PropriedadesZyclos.class)
public class ZyclosApplication {

    public static void main(String[] argumentos) {
        SpringApplication.run(ZyclosApplication.class, argumentos);
    }
}
