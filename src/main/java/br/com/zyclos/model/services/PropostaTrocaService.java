package br.com.zyclos.model.services;

import br.com.zyclos.model.entity.PropostaTroca;
import br.com.zyclos.model.repository.PropostaTrocaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PropostaTrocaService {

    @Autowired // Injeção de dependência automática
    private PropostaTrocaRepository propostaTrocaRepository;

    // Método responsável em listar todas as propostas de troca cadastradas no banco de dados

    public List<PropostaTroca> findAll() {
        return propostaTrocaRepository.findAll();
    }

    // Método responsável em criar a proposta de troca no banco de dados

    public PropostaTroca save(PropostaTroca propostaTroca) {
        if (propostaTroca.getStatus() == null) {
            propostaTroca.setStatus("PENDENTE");
        }
        propostaTroca.setDataCadastro(LocalDateTime.now());
        propostaTroca.setCodStatus(true);
        return propostaTrocaRepository.save(propostaTroca);
    }

    // Método responsável em listar a proposta de troca por ID

    public PropostaTroca findById(Long id) {
        return propostaTrocaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proposta de troca não encontrada com o id " + id));
    }

    // Método responsável em atualizar a proposta de troca

    public PropostaTroca update(Long id, PropostaTroca propostaTroca) {
        PropostaTroca propostaTrocaExistente = findById(id);
        propostaTrocaExistente.setMensagem(propostaTroca.getMensagem());
        propostaTrocaExistente.setStatus(propostaTroca.getStatus());
        propostaTrocaExistente.setProponenteId(propostaTroca.getProponenteId());
        propostaTrocaExistente.setDestinatarioId(propostaTroca.getDestinatarioId());
        propostaTrocaExistente.setAnuncioDesejadoId(propostaTroca.getAnuncioDesejadoId());
        propostaTrocaExistente.setAnuncioOferecidoId(propostaTroca.getAnuncioOferecidoId());
        propostaTrocaExistente.setDataAtualizacao(LocalDateTime.now());
        return propostaTrocaRepository.save(propostaTrocaExistente);
    }

    // Método responsável em excluir a proposta de troca ( exclusão física )

    public void delete(Long id) {
        PropostaTroca propostaTrocaExistente = findById(id);
        propostaTrocaRepository.delete(propostaTrocaExistente);
    }
}
