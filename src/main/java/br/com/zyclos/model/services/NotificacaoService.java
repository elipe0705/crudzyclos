package br.com.zyclos.model.services;

import br.com.zyclos.model.entity.Notificacao;
import br.com.zyclos.model.repository.NotificacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificacaoService {

    @Autowired // Injeção de dependência automática
    private NotificacaoRepository notificacaoRepository;

    // Método responsável em listar todas as notificações cadastradas no banco de dados

    public List<Notificacao> findAll() {
        return notificacaoRepository.findAll();
    }

    // Método responsável em criar a notificação no banco de dados

    public Notificacao save(Notificacao notificacao) {
        if (notificacao.getTipo() == null) {
            notificacao.setTipo("SISTEMA");
        }
        notificacao.setDataCadastro(LocalDateTime.now());
        notificacao.setCodStatus(true);
        return notificacaoRepository.save(notificacao);
    }

    // Método responsável em listar a notificação por ID

    public Notificacao findById(Long id) {
        return notificacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notificação não encontrada com o id " + id));
    }

    // Método responsável em atualizar a notificação

    public Notificacao update(Long id, Notificacao notificacao) {
        Notificacao notificacaoExistente = findById(id);
        notificacaoExistente.setTexto(notificacao.getTexto());
        notificacaoExistente.setTipo(notificacao.getTipo());
        notificacaoExistente.setDestino(notificacao.getDestino());
        notificacaoExistente.setLida(notificacao.isLida());
        notificacaoExistente.setUsuarioId(notificacao.getUsuarioId());
        return notificacaoRepository.save(notificacaoExistente);
    }

    // Método responsável em excluir a notificação ( exclusão física )

    public void delete(Long id) {
        Notificacao notificacaoExistente = findById(id);
        notificacaoRepository.delete(notificacaoExistente);
    }
}
