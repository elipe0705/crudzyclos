package br.com.zyclos.model.services;

import br.com.zyclos.model.entity.Mensagem;
import br.com.zyclos.model.repository.MensagemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MensagemService {

    @Autowired // Injeção de dependência automática
    private MensagemRepository mensagemRepository;

    // Método responsável em listar todas as mensagens cadastradas no banco de dados

    public List<Mensagem> findAll() {
        return mensagemRepository.findAll();
    }

    // Método responsável em criar a mensagem no banco de dados

    public Mensagem save(Mensagem mensagem) {
        mensagem.setDataEnvio(LocalDateTime.now());
        mensagem.setCodStatus(true);
        return mensagemRepository.save(mensagem);
    }

    // Método responsável em listar a mensagem por ID

    public Mensagem findById(Long id) {
        return mensagemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mensagem não encontrada com o id " + id));
    }

    // Método responsável em atualizar a mensagem

    public Mensagem update(Long id, Mensagem mensagem) {
        Mensagem mensagemExistente = findById(id);
        mensagemExistente.setConteudo(mensagem.getConteudo());
        mensagemExistente.setLida(mensagem.isLida());
        mensagemExistente.setConversaId(mensagem.getConversaId());
        mensagemExistente.setRemetenteId(mensagem.getRemetenteId());
        return mensagemRepository.save(mensagemExistente);
    }

    // Método responsável em excluir a mensagem ( exclusão física )

    public void delete(Long id) {
        Mensagem mensagemExistente = findById(id);
        mensagemRepository.delete(mensagemExistente);
    }
}
