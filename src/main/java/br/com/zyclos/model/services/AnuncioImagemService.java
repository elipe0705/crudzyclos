package br.com.zyclos.model.services;

import br.com.zyclos.model.entity.AnuncioImagem;
import br.com.zyclos.model.repository.AnuncioImagemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnuncioImagemService {

    @Autowired // Injeção de dependência automática
    private AnuncioImagemRepository anuncioImagemRepository;

    // Método responsável em listar todas as imagens de anúncios cadastradas no banco de dados

    public List<AnuncioImagem> findAll() {
        return anuncioImagemRepository.findAll();
    }

    // Método responsável em criar a imagem do anúncio no banco de dados

    public AnuncioImagem save(AnuncioImagem anuncioImagem) {
        anuncioImagem.setCodStatus(true);
        return anuncioImagemRepository.save(anuncioImagem);
    }

    // Método responsável em listar a imagem do anúncio por ID

    public AnuncioImagem findById(Long id) {
        return anuncioImagemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Imagem do anúncio não encontrada com o id " + id));
    }

    // Método responsável em atualizar a imagem do anúncio

    public AnuncioImagem update(Long id, AnuncioImagem anuncioImagem) {
        AnuncioImagem anuncioImagemExistente = findById(id);
        anuncioImagemExistente.setUrl(anuncioImagem.getUrl());
        anuncioImagemExistente.setPrincipal(anuncioImagem.isPrincipal());
        anuncioImagemExistente.setOrdem(anuncioImagem.getOrdem());
        anuncioImagemExistente.setAnuncioId(anuncioImagem.getAnuncioId());
        return anuncioImagemRepository.save(anuncioImagemExistente);
    }

    // Método responsável em excluir a imagem do anúncio ( exclusão física )

    public void delete(Long id) {
        AnuncioImagem anuncioImagemExistente = findById(id);
        anuncioImagemRepository.delete(anuncioImagemExistente);
    }
}
