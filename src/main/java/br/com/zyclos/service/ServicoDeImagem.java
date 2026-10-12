package br.com.zyclos.service;

import br.com.zyclos.config.PropriedadesZyclos;
import br.com.zyclos.exception.RegraDeNegocioException;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Grava as fotos enviadas em disco e devolve a URL publica.
 *
 * <p>O destino padrao e a pasta dados/uploads ao lado do executavel. Em producao basta
 * apontar zyclos.upload.diretorio para um volume persistente.
 */
@Service
public class ServicoDeImagem {

    private static final Logger LOG = LoggerFactory.getLogger(ServicoDeImagem.class);

    private static final Map<String, String> EXTENSOES = Map.of(
            "image/png", ".png",
            "image/jpeg", ".jpg",
            "image/webp", ".webp");

    private final PropriedadesZyclos propriedades;
    private Path pasta;

    public ServicoDeImagem(PropriedadesZyclos propriedades) {
        this.propriedades = propriedades;
    }

    @PostConstruct
    void prepararPasta() {
        pasta = Paths.get(propriedades.getUpload().getDiretorio()).toAbsolutePath().normalize();
        try {
            Files.createDirectories(pasta);
            LOG.info("Fotos enviadas serao gravadas em {}", pasta);
        } catch (IOException erro) {
            throw new IllegalStateException("Nao foi possivel criar a pasta de uploads: " + pasta, erro);
        }
    }

    /** Grava o arquivo e devolve o caminho publico, por exemplo /api/imagens/abc.jpg. */
    public String guardar(MultipartFile arquivo) {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new RegraDeNegocioException("Selecione uma imagem.");
        }
        String tipo = arquivo.getContentType() == null
                ? "" : arquivo.getContentType().toLowerCase(Locale.ROOT);
        if (!propriedades.getUpload().getTiposPermitidos().contains(tipo)) {
            throw new RegraDeNegocioException("Formato nao aceito. Envie PNG, JPEG ou WebP.");
        }
        if (arquivo.getSize() > propriedades.getUpload().getTamanhoMaximoBytes()) {
            throw new RegraDeNegocioException("A imagem excede o tamanho maximo permitido.");
        }

        String nome = UUID.randomUUID() + EXTENSOES.getOrDefault(tipo, ".img");
        Path destino = pasta.resolve(nome).normalize();
        if (!destino.startsWith(pasta)) {
            throw new RegraDeNegocioException("Nome de arquivo invalido.");
        }
        try (InputStream conteudo = arquivo.getInputStream()) {
            Files.copy(conteudo, destino, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException erro) {
            throw new IllegalStateException("Falha ao gravar a imagem enviada.", erro);
        }
        return "/api/imagens/" + nome;
    }

    /** Apaga um arquivo gravado anteriormente. Ignora caminhos externos a pasta de uploads. */
    public void apagar(String url) {
        if (url == null || !url.startsWith("/api/imagens/")) {
            return;
        }
        Path alvo = pasta.resolve(url.substring("/api/imagens/".length())).normalize();
        if (!alvo.startsWith(pasta)) {
            return;
        }
        try {
            Files.deleteIfExists(alvo);
        } catch (IOException erro) {
            LOG.warn("Nao foi possivel apagar {}", alvo, erro);
        }
    }
}
