package br.com.zyclos.service;

import br.com.zyclos.domain.Notificacao;
import br.com.zyclos.domain.TipoNotificacao;
import br.com.zyclos.domain.Usuario;
import br.com.zyclos.dto.response.NotificacaoResponse;
import br.com.zyclos.exception.RecursoNaoEncontradoException;
import br.com.zyclos.mapper.Conversores;
import br.com.zyclos.repository.NotificacaoRepository;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Central de notificacoes do usuario. */
@Service
public class ServicoDeNotificacao {

    private final NotificacaoRepository notificacoes;

    public ServicoDeNotificacao(NotificacaoRepository notificacoes) {
        this.notificacoes = notificacoes;
    }

    @Transactional(readOnly = true)
    public List<NotificacaoResponse> listar(Usuario usuario) {
        return notificacoes.findByUsuarioOrderByCriadoEmDesc(usuario).stream()
                .map(Conversores::notificacao)
                .toList();
    }

    @Transactional(readOnly = true)
    public long naoLidas(Usuario usuario) {
        return notificacoes.countByUsuarioAndLidaFalse(usuario);
    }

    @Transactional
    public NotificacaoResponse marcarComoLida(Usuario usuario, Long id) {
        Notificacao notificacao = notificacoes.findById(id)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Notificacao", id));
        if (!Objects.equals(notificacao.getUsuario().getId(), usuario.getId())) {
            throw RecursoNaoEncontradoException.de("Notificacao", id);
        }
        notificacao.marcarComoLida();
        return Conversores.notificacao(notificacoes.save(notificacao));
    }

    @Transactional
    public int marcarTodasComoLidas(Usuario usuario) {
        return notificacoes.marcarTodasComoLidas(usuario);
    }

    /** Cria uma notificacao sem notificar o proprio autor da acao. */
    @Transactional
    public void criar(Usuario destinatario, String texto, TipoNotificacao tipo, String destino) {
        if (destinatario == null) {
            return;
        }
        notificacoes.save(new Notificacao(destinatario, texto, tipo, destino));
    }

    public void boasVindas(Usuario usuario) {
        criar(usuario, "Bem-vindo a ZYCLOS! Publique sua primeira peca.",
                TipoNotificacao.SISTEMA, "/criar-anuncio");
    }

    public void novoSeguidor(Usuario seguido, Usuario seguidor) {
        criar(seguido, seguidor.getNome() + " comecou a seguir voce.",
                TipoNotificacao.PERFIL, "/perfil/" + seguidor.getUsername());
    }

    public void anuncioFavoritado(Usuario vendedor, String tituloDoAnuncio) {
        criar(vendedor, "Seu anuncio " + tituloDoAnuncio + " recebeu um favorito.",
                TipoNotificacao.FAVORITO, "/perfil");
    }

    public void novaMensagem(Usuario destinatario, Usuario remetente) {
        criar(destinatario, remetente.getNome() + " enviou uma nova mensagem.",
                TipoNotificacao.MENSAGEM, "/mensagens");
    }

    public void propostaRecebida(Usuario destinatario, Usuario proponente) {
        criar(destinatario, proponente.getNome() + " enviou uma proposta de troca.",
                TipoNotificacao.TROCA, "/trocas");
    }

    public void propostaRespondida(Usuario proponente, boolean aceita) {
        criar(proponente,
                aceita ? "Sua proposta foi aceita." : "Sua proposta foi recusada.",
                aceita ? TipoNotificacao.SUCESSO : TipoNotificacao.TROCA,
                "/trocas");
    }

    public void novaAvaliacao(Usuario avaliado, Usuario autor) {
        criar(avaliado, autor.getNome() + " avaliou seu perfil.",
                TipoNotificacao.PERFIL, "/perfil/" + avaliado.getUsername());
    }

    public void denunciaResolvida(Usuario denunciante, String motivo) {
        criar(denunciante, "Sua denuncia sobre " + motivo + " foi analisada.",
                TipoNotificacao.DENUNCIA, "/notificacoes");
    }
}
