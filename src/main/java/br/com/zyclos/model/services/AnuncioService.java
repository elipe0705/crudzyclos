package br.com.zyclos.model.services;

import br.com.zyclos.model.entity.Anuncio;
import br.com.zyclos.model.repository.AnuncioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AnuncioService {

    @Autowired // Injeção de dependência automática
    private AnuncioRepository anuncioRepository;

    // Método responsável em listar todos os anúncios cadastrados no banco de dados

    public List<Anuncio> findAll() {
        return anuncioRepository.findAll();
    }

    // Método responsável em criar o anúncio no banco de dados

    public Anuncio save(Anuncio anuncio) {
        if (anuncio.getTipoNegociacao() == null) {
            anuncio.setTipoNegociacao("VENDA");
        }
        if (anuncio.getStatus() == null) {
            anuncio.setStatus("ATIVO");
        }
        anuncio.setDataCadastro(LocalDateTime.now());
        anuncio.setCodStatus(true);
        return anuncioRepository.save(anuncio);
    }

    // Método responsável em listar o anúncio por ID

    public Anuncio findById(Long id) {
        return anuncioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Anúncio não encontrado com o id " + id));
    }

    // Método responsável em atualizar o anúncio

    public Anuncio update(Long id, Anuncio anuncio) {
        Anuncio anuncioExistente = findById(id);
        anuncioExistente.setTitulo(anuncio.getTitulo());
        anuncioExistente.setDescricao(anuncio.getDescricao());
        anuncioExistente.setPreco(anuncio.getPreco());
        anuncioExistente.setMarca(anuncio.getMarca());
        anuncioExistente.setTamanho(anuncio.getTamanho());
        anuncioExistente.setCondicao(anuncio.getCondicao());
        anuncioExistente.setEstilo(anuncio.getEstilo());
        anuncioExistente.setCidade(anuncio.getCidade());
        anuncioExistente.setEstado(anuncio.getEstado());
        anuncioExistente.setTipoNegociacao(anuncio.getTipoNegociacao());
        anuncioExistente.setStatus(anuncio.getStatus());
        anuncioExistente.setCategoriaId(anuncio.getCategoriaId());
        anuncioExistente.setVendedorId(anuncio.getVendedorId());
        anuncioExistente.setDataAtualizacao(LocalDateTime.now());
        return anuncioRepository.save(anuncioExistente);
    }

    // Método responsável em excluir o anúncio ( exclusão física )

    public void delete(Long id) {
        Anuncio anuncioExistente = findById(id);
        anuncioRepository.delete(anuncioExistente);
    }
}
