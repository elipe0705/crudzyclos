package br.com.zyclos.service;

import br.com.zyclos.domain.Anuncio;
import br.com.zyclos.domain.Conversa;
import br.com.zyclos.domain.Mensagem;
import br.com.zyclos.domain.Usuario;
import br.com.zyclos.dto.request.MensagemRequest;
import br.com.zyclos.dto.request.NovaConversaRequest;
import br.com.zyclos.dto.response.ConversaResponse;
import br.com.zyclos.dto.response.MensagemResponse;
import br.com.zyclos.exception.OperacaoNaoPermitidaException;
import br.com.zyclos.exception.RecursoNaoEncontradoException;
import br.com.zyclos.exception.RegraDeNegocioException;
import br.com.zyclos.mapper.Conversores;
import br.com.zyclos.repository.ConversaRepository;
import br.com.zyclos.repository.MensagemRepository;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Conversas e mensagens entre usuarios. */
@Service
public class ServicoDeMensagem {

    private final ConversaRepository conversas;
    private final MensagemRepository mensagens;
    private final ServicoDePerfil perfis;
    private final ServicoDeAnuncio anuncios;
    private final ServicoDeNotificacao notificacoes;

    public ServicoDeMensagem(ConversaRepository conversas, MensagemRepository mensagens,
                             ServicoDePerfil perfis, ServicoDeAnuncio anuncios,
                             ServicoDeNotificacao notificacoes) {
        this.conversas = conversas;
        this.mensagens = mensagens;
        this.perfis = perfis;
        this.anuncios = anuncios;
        this.notificacoes = notificacoes;
    }

    @Transactional(readOnly = true)
    public List<ConversaResponse> listar(Usuario usuario) {
        return conversas.daPessoa(usuario).stream()
                .map(conversa -> resumir(conversa, usuario))
                .toList();
    }

    @Transactional
    public ConversaResponse abrir(Usuario solicitante, NovaConversaRequest pedido) {
        Usuario destinatario = perfis.porUsername(pedido.destinatario());
        if (Objects.equals(destinatario.getId(), solicitante.getId())) {
            throw new RegraDeNegocioException("Voce nao pode abrir uma conversa consigo mesmo.");
        }
        Anuncio anuncio = pedido.anuncioId() == null ? null : anuncios.porId(pedido.anuncioId());
        Conversa conversa = obterOuCriar(solicitante, destinatario, anuncio);

        if (pedido.mensagem() != null && !pedido.mensagem().isBlank()) {
            registrar(conversa, solicitante, destinatario, pedido.mensagem().trim());
        }
        return resumir(conversa, solicitante);
    }

    @Transactional(readOnly = true)
    public List<MensagemResponse> mensagensDe(Usuario solicitante, Long conversaId) {
        Conversa conversa = carregar(solicitante, conversaId);
        return mensagens.findByConversaOrderByEnviadaEmAsc(conversa).stream()
                .map(mensagem -> Conversores.mensagem(mensagem, solicitante))
                .toList();
    }

    @Transactional
    public MensagemResponse enviar(Usuario remetente, Long conversaId, MensagemRequest pedido) {
        Conversa conversa = carregar(remetente, conversaId);
        Usuario destinatario = conversa.interlocutorDe(remetente);
        Mensagem mensagem = registrar(conversa, remetente, destinatario, pedido.conteudo().trim());
        return Conversores.mensagem(mensagem, remetente);
    }

    @Transactional
    public int marcarComoLidas(Usuario solicitante, Long conversaId) {
        Conversa conversa = carregar(solicitante, conversaId);
        return mensagens.marcarRecebidasComoLidas(conversa, solicitante);
    }

    @Transactional(readOnly = true)
    public long naoLidas(Usuario usuario) {
        return conversas.daPessoa(usuario).stream()
                .mapToLong(conversa -> mensagens.naoLidasNaConversa(conversa, usuario))
                .sum();
    }

    /** Usado pelos outros servicos para avisar o vendedor dentro do proprio chat. */
    @Transactional
    public void enviarDoSistema(Usuario remetente, Usuario destinatario, Anuncio anuncio, String texto) {
        Conversa conversa = obterOuCriar(remetente, destinatario, anuncio);
        registrar(conversa, remetente, destinatario, texto);
    }

    private Conversa obterOuCriar(Usuario um, Usuario outro, Anuncio anuncio) {
        Usuario primeiro = um.getId() <= outro.getId() ? um : outro;
        Usuario segundo = primeiro == um ? outro : um;
        Long anuncioId = anuncio == null ? null : anuncio.getId();
        return conversas.existente(primeiro, segundo, anuncioId)
                .orElseGet(() -> conversas.save(Conversa.entre(um, outro, anuncio)));
    }

    private Mensagem registrar(Conversa conversa, Usuario remetente, Usuario destinatario, String texto) {
        Mensagem mensagem = mensagens.save(new Mensagem(conversa, remetente, texto));
        conversa.tocar();
        conversas.save(conversa);
        notificacoes.novaMensagem(destinatario, remetente);
        return mensagem;
    }

    private Conversa carregar(Usuario solicitante, Long conversaId) {
        Conversa conversa = conversas.findById(conversaId)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Conversa", conversaId));
        if (!conversa.participa(solicitante)) {
            throw new OperacaoNaoPermitidaException("Voce nao participa desta conversa.");
        }
        return conversa;
    }

    private ConversaResponse resumir(Conversa conversa, Usuario leitor) {
        String ultima = mensagens.findFirstByConversaOrderByEnviadaEmDesc(conversa)
                .map(Mensagem::getConteudo)
                .orElse(null);
        return new ConversaResponse(
                conversa.getId(),
                Conversores.autor(conversa.interlocutorDe(leitor)),
                ultima,
                conversa.getAtualizadoEm(),
                mensagens.naoLidasNaConversa(conversa, leitor),
                conversa.getAnuncio() == null ? null : Conversores.resumo(conversa.getAnuncio(), Set.of()));
    }
}
