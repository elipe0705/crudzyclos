package br.com.zyclos.web;

import br.com.zyclos.service.ServicoDeImagem;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/** Envio das fotos de anuncios e de perfil. */
@RestController
@RequestMapping("/api/uploads")
@Tag(name = "Uploads", description = "Envio das fotos usadas nos anuncios e no perfil")
public class ControladorDeUpload {

    private final ServicoDeImagem imagens;

    public ControladorDeUpload(ServicoDeImagem imagens) {
        this.imagens = imagens;
    }

    @PostMapping(value = "/imagem", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Envia uma imagem e devolve a URL publica")
    public Map<String, String> enviar(@RequestParam("arquivo") MultipartFile arquivo) {
        return Map.of("url", imagens.guardar(arquivo));
    }

    @PostMapping(value = "/imagens", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Envia varias imagens de uma vez e devolve as URLs na mesma ordem")
    public Map<String, List<String>> enviarVarias(@RequestParam("arquivos") MultipartFile[] arquivos) {
        List<String> urls = Arrays.stream(arquivos).map(imagens::guardar).toList();
        return Map.of("urls", urls);
    }
}
