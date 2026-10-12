package br.com.zyclos.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

/** Configuracoes da aplicacao, lidas do prefixo zyclos no application.yml. */
@ConfigurationProperties(prefix = "zyclos")
public class PropriedadesZyclos {

    private Jwt jwt = new Jwt();
    private Cors cors = new Cors();
    private Upload upload = new Upload();
    private Seed seed = new Seed();

    public Jwt getJwt() {
        return jwt;
    }

    public void setJwt(Jwt jwt) {
        this.jwt = jwt;
    }

    public Cors getCors() {
        return cors;
    }

    public void setCors(Cors cors) {
        this.cors = cors;
    }

    public Upload getUpload() {
        return upload;
    }

    public void setUpload(Upload upload) {
        this.upload = upload;
    }

    public Seed getSeed() {
        return seed;
    }

    public void setSeed(Seed seed) {
        this.seed = seed;
    }

    /** Parametros de assinatura e validade do token JWT. */
    public static class Jwt {
        /** Chave HMAC em Base64. Deve ser trocada por variavel de ambiente em producao. */
        private String segredo = "";
        private long minutosDeValidade = 720;
        private String emissor = "zyclos-api";

        public String getSegredo() {
            return segredo;
        }

        public void setSegredo(String segredo) {
            this.segredo = segredo;
        }

        public long getMinutosDeValidade() {
            return minutosDeValidade;
        }

        public void setMinutosDeValidade(long minutosDeValidade) {
            this.minutosDeValidade = minutosDeValidade;
        }

        public String getEmissor() {
            return emissor;
        }

        public void setEmissor(String emissor) {
            this.emissor = emissor;
        }
    }

    /** Origens liberadas para o front-end. */
    public static class Cors {
        private List<String> origensPermitidas = List.of("http://localhost:3000", "http://localhost:5173");

        public List<String> getOrigensPermitidas() {
            return origensPermitidas;
        }

        public void setOrigensPermitidas(List<String> origensPermitidas) {
            this.origensPermitidas = origensPermitidas;
        }
    }

    /** Pasta onde as fotos enviadas ficam gravadas. */
    public static class Upload {
        private String diretorio = "dados/uploads";
        private long tamanhoMaximoBytes = 5L * 1024 * 1024;
        private List<String> tiposPermitidos = List.of("image/png", "image/jpeg", "image/webp");

        public String getDiretorio() {
            return diretorio;
        }

        public void setDiretorio(String diretorio) {
            this.diretorio = diretorio;
        }

        public long getTamanhoMaximoBytes() {
            return tamanhoMaximoBytes;
        }

        public void setTamanhoMaximoBytes(long tamanhoMaximoBytes) {
            this.tamanhoMaximoBytes = tamanhoMaximoBytes;
        }

        public List<String> getTiposPermitidos() {
            return tiposPermitidos;
        }

        public void setTiposPermitidos(List<String> tiposPermitidos) {
            this.tiposPermitidos = tiposPermitidos;
        }
    }

    /** Dados de demonstracao carregados na primeira execucao. */
    public static class Seed {
        private boolean habilitado = true;
        /** Senha unica das contas de demonstracao criadas pelo seed. */
        private String senhaPadrao = "zyclos123";

        public boolean isHabilitado() {
            return habilitado;
        }

        public void setHabilitado(boolean habilitado) {
            this.habilitado = habilitado;
        }

        public String getSenhaPadrao() {
            return senhaPadrao;
        }

        public void setSenhaPadrao(String senhaPadrao) {
            this.senhaPadrao = senhaPadrao;
        }
    }
}
