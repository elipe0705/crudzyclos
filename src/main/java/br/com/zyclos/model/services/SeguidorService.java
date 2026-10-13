package br.com.zyclos.model.services;

import br.com.zyclos.model.entity.Seguidor;
import br.com.zyclos.model.repository.SeguidorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SeguidorService {

    @Autowired // Injeção de dependência automática
    private SeguidorRepository seguidorRepository;

    // Método responsável em listar todos os seguidores cadastrados no banco de dados

    public List<Seguidor> findAll() {
        return seguidorRepository.findAll();
    }

    // Método responsável em criar o seguidor no banco de dados

    public Seguidor save(Seguidor seguidor) {
        seguidor.setDataCadastro(LocalDateTime.now());
        seguidor.setCodStatus(true);
        return seguidorRepository.save(seguidor);
    }

    // Método responsável em listar o seguidor por ID

    public Seguidor findById(Long id) {
        return seguidorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Seguidor não encontrado com o id " + id));
    }

    // Método responsável em atualizar o seguidor

    public Seguidor update(Long id, Seguidor seguidor) {
        Seguidor seguidorExistente = findById(id);
        seguidorExistente.setSeguidorId(seguidor.getSeguidorId());
        seguidorExistente.setSeguidoId(seguidor.getSeguidoId());
        return seguidorRepository.save(seguidorExistente);
    }

    // Método responsável em excluir o seguidor ( exclusão física )

    public void delete(Long id) {
        Seguidor seguidorExistente = findById(id);
        seguidorRepository.delete(seguidorExistente);
    }
}
