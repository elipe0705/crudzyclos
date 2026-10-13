package br.com.zyclos.model.services;

import br.com.zyclos.model.entity.Conversa;
import br.com.zyclos.model.repository.ConversaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ConversaService {

    @Autowired // Injeção de dependência automática
    private ConversaRepository conversaRepository;

    // Método responsável em listar todas as conversas cadastradas no banco de dados

    public List<Conversa> findAll() {
        return conversaRepository.findAll();
    }

    // Método responsável em criar a conversa no banco de dados

    public Conversa save(Conversa conversa) {
        conversa.setDataCadastro(LocalDateTime.now());
        conversa.setCodStatus(true);
        return conversaRepository.save(conversa);
    }

    // Método responsável em listar a conversa por ID

    public Conversa findById(Long id) {
        return conversaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conversa não encontrada com o id " + id));
    }

    // Método responsável em atualizar a conversa

    public Conversa update(Long id, Conversa conversa) {
        Conversa conversaExistente = findById(id);
        conversaExistente.setParticipanteAId(conversa.getParticipanteAId());
        conversaExistente.setParticipanteBId(conversa.getParticipanteBId());
        conversaExistente.setAnuncioId(conversa.getAnuncioId());
        conversaExistente.setDataAtualizacao(LocalDateTime.now());
        return conversaRepository.save(conversaExistente);
    }

    // Método responsável em excluir a conversa ( exclusão física )

    public void delete(Long id) {
        Conversa conversaExistente = findById(id);
        conversaRepository.delete(conversaExistente);
    }
}
