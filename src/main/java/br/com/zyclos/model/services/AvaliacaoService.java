package br.com.zyclos.model.services;

import br.com.zyclos.model.entity.Avaliacao;
import br.com.zyclos.model.repository.AvaliacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AvaliacaoService {

    @Autowired // Injeção de dependência automática
    private AvaliacaoRepository avaliacaoRepository;

    // Método responsável em listar todas as avaliações cadastradas no banco de dados

    public List<Avaliacao> findAll() {
        return avaliacaoRepository.findAll();
    }

    // Método responsável em criar a avaliação no banco de dados

    public Avaliacao save(Avaliacao avaliacao) {
        avaliacao.setDataCadastro(LocalDateTime.now());
        avaliacao.setCodStatus(true);
        return avaliacaoRepository.save(avaliacao);
    }

    // Método responsável em listar a avaliação por ID

    public Avaliacao findById(Long id) {
        return avaliacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Avaliação não encontrada com o id " + id));
    }

    // Método responsável em atualizar a avaliação

    public Avaliacao update(Long id, Avaliacao avaliacao) {
        Avaliacao avaliacaoExistente = findById(id);
        avaliacaoExistente.setNota(avaliacao.getNota());
        avaliacaoExistente.setComentario(avaliacao.getComentario());
        avaliacaoExistente.setAutorId(avaliacao.getAutorId());
        avaliacaoExistente.setAvaliadoId(avaliacao.getAvaliadoId());
        avaliacaoExistente.setAnuncioId(avaliacao.getAnuncioId());
        return avaliacaoRepository.save(avaliacaoExistente);
    }

    // Método responsável em excluir a avaliação ( exclusão física )

    public void delete(Long id) {
        Avaliacao avaliacaoExistente = findById(id);
        avaliacaoRepository.delete(avaliacaoExistente);
    }
}
