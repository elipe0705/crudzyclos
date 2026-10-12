package br.com.zyclos.service;

import br.com.zyclos.domain.Anuncio;
import br.com.zyclos.domain.PropostaTroca;
import br.com.zyclos.domain.StatusAnuncio;
import br.com.zyclos.domain.StatusProposta;
import br.com.zyclos.domain.Usuario;
import br.com.zyclos.dto.request.PropostaTrocaRequest;
import br.com.zyclos.dto.response.PropostaTrocaResponse;
import br.com.zyclos.exception.ConflitoException;
import br.com.zyclos.exception.OperacaoNaoPermitidaException;
import br.com.zyclos.exception.RecursoNaoEncontradoException;
import br.com.zyclos.exception.RegraDeNegocioException;
import br.com.zyclos.mapper.Conversores;
import br.com.zyclos.repository.AnuncioRepository;
import br.com.zyclos.repository.FavoritoRepository;
import br.com.zyclos.repository.PropostaTrocaRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Propostas de troca entre pecas, com as abas enviadas, recebidas e historico. */
@Service
public class ServicoDeTroca {

    private static final Set<StatusProposta> ABERTAS = Set.of(StatusProposta.PENDENTE, StatusProposta.ACEITA);
    private static final Set<StatusProposta> ENCERRADAS =
            Set.of(StatusProposta.RECUSADA, StatusProposta.CANCELADA, StatusProposta.CONCLUIDA);

    private final PropostaTrocaRepository propostas;
    private final AnuncioRepository anuncios;
    private final FavoritoRepository favoritos;
    private final ServicoDeNotificacao notificacoes;
    private final ServicoDeMensagem mensagens;

    public ServicoDeTroca(PropostaTrocaRepository propostas, AnuncioRepository anuncios,
                          FavoritoRepository favoritos, ServicoDeNotificacao notificacoes,
                          ServicoDeMensagem mensagens) {
        this.propostas = propostas;
        this.anuncios = anuncios;
        this.favoritos = favoritos;
        this.notificacoes = notificacoes;
        this.mensagens = mensagens;
    }

    @Transactional(readOnly = true)
    public List<PropostaTrocaResponse> enviadas(Usuario usuario) {
        return converter(propostas.findByProponenteAndStatusInOrderByCriadoEmDesc(usuario, ABERTAS), usuario);
    }

    @Transactional(readOnly = true)
    public List<PropostaTrocaResponse> recebidas(Usuario usuario) {
        return converter(propostas.findByDestinatarioAndStatusInOrderByCriadoEmDesc(usuario, ABERTAS), usuario);
    }

    @Transactional(readOnly = true)
    public List<PropostaTrocaResponse> historico(Usuario usuario) {
        return converter(propostas.historico(usuario, ENCERRADAS), usuario);
    }

    @Transactional
    public PropostaTrocaResponse propor(Usuario proponente, PropostaTrocaRequest pedido) {
        Anuncio desejado = carregar(pedido.anuncioDesejadoId());
        Anuncio oferecido = carregar(pedido.anuncioOferecidoId());

        if (Objects.equals(desejado.getId(), oferecido.getId())) {
            throw new RegraDeNegocioException("Escolha duas pecas diferentes.");
        }
        if (desejado.pertenceA(proponente)) {
            throw new RegraDeNegocioException("Esta peca ja e sua.");
        }
        if (!oferecido.pertenceA(proponente)) {
            throw new OperacaoNaoPermitidaException("Voce so pode oferecer pecas que anunciou.");
        }
        if (!desejado.disponivel() || !oferecido.disponivel()) {
            throw new RegraDeNegocioException("Uma das pecas nao esta mais disponivel.");
        }
        if (!desejado.getTipoNegociacao().aceitaTroca()) {
            throw new RegraDeNegocioException("Esta peca nao esta aberta para troca.");
        }
        if (propostas.existsByProponenteAndAnuncioDesejadoAndStatus(
                proponente, desejado, StatusProposta.PENDENTE)) {
            throw new ConflitoException("Voce ja tem uma proposta pendente para esta peca.");
        }

        PropostaTroca proposta = propostas.save(
                new PropostaTroca(proponente, desejado, oferecido, pedido.mensagem()));
        notificacoes.propostaRecebida(desejado.getVendedor(), proponente);
        mensagens.enviarDoSistema(proponente, desejado.getVendedor(), desejado,
                "Proposta de troca: ofereco " + oferecido.getTitulo() + " pela sua peca "
                        + desejado.getTitulo() + ".");
        return Conversores.proposta(proposta, proponente, idsFavoritos(proponente));
    }

    @Transactional
    public PropostaTrocaResponse aceitar(Usuario destinatario, Long id) {
        PropostaTroca proposta = carregarProposta(id, destinatario);
        exigirDestinatario(proposta, destinatario);
        exigirPendente(proposta);

        proposta.setStatus(StatusProposta.ACEITA);
        marcar(proposta.getAnuncioDesejado(), StatusAnuncio.NEGOCIADO);
        marcar(proposta.getAnuncioOferecido(), StatusAnuncio.NEGOCIADO);
        recusarConcorrentes(proposta);

        notificacoes.propostaRespondida(proposta.getProponente(), true);
        return Conversores.proposta(propostas.save(proposta), destinatario, idsFavoritos(destinatario));
    }

    @Transactional
    public PropostaTrocaResponse recusar(Usuario destinatario, Long id) {
        PropostaTroca proposta = carregarProposta(id, destinatario);
        exigirDestinatario(proposta, destinatario);
        exigirPendente(proposta);

        proposta.setStatus(StatusProposta.RECUSADA);
        notificacoes.propostaRespondida(proposta.getProponente(), false);
        return Conversores.proposta(propostas.save(proposta), destinatario, idsFavoritos(destinatario));
    }

    @Transactional
    public PropostaTrocaResponse cancelar(Usuario proponente, Long id) {
        PropostaTroca proposta = carregarProposta(id, proponente);
        if (!Objects.equals(proposta.getProponente().getId(), proponente.getId())) {
            throw new OperacaoNaoPermitidaException("Somente quem enviou a proposta pode cancela-la.");
        }
        exigirPendente(proposta);
        proposta.setStatus(StatusProposta.CANCELADA);
        return Conversores.proposta(propostas.save(proposta), proponente, idsFavoritos(proponente));
    }

    /** Fecha a troca depois que as duas partes combinaram a entrega. */
    @Transactional
    public PropostaTrocaResponse concluir(Usuario solicitante, Long id) {
        PropostaTroca proposta = carregarProposta(id, solicitante);
        if (proposta.getStatus() != StatusProposta.ACEITA) {
            throw new RegraDeNegocioException("Somente uma proposta aceita pode ser concluida.");
        }
        proposta.setStatus(StatusProposta.CONCLUIDA);
        notificacoes.criar(proposta.getProponente().getId().equals(solicitante.getId())
                        ? proposta.getDestinatario() : proposta.getProponente(),
                "Troca concluida. Nao esqueca de avaliar a outra pessoa.",
                br.com.zyclos.domain.TipoNotificacao.SUCESSO, "/trocas");
        return Conversores.proposta(propostas.save(proposta), solicitante, idsFavoritos(solicitante));
    }

    /** Depois de aceitar uma proposta, as demais pendentes sobre a mesma peca caem. */
    private void recusarConcorrentes(PropostaTroca aceita) {
        propostas.findByDestinatarioAndStatusInOrderByCriadoEmDesc(
                        aceita.getDestinatario(), Set.of(StatusProposta.PENDENTE)).stream()
                .filter(outra -> !outra.getId().equals(aceita.getId()))
                .filter(outra -> outra.getAnuncioDesejado().getId().equals(aceita.getAnuncioDesejado().getId()))
                .forEach(outra -> {
                    outra.setStatus(StatusProposta.RECUSADA);
                    propostas.save(outra);
                    notificacoes.propostaRespondida(outra.getProponente(), false);
                });
    }

    private void marcar(Anuncio anuncio, StatusAnuncio status) {
        anuncio.setStatus(status);
        anuncios.save(anuncio);
    }

    private PropostaTroca carregarProposta(Long id, Usuario solicitante) {
        PropostaTroca proposta = propostas.findById(id)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Proposta", id));
        if (!proposta.envolve(solicitante)) {
            throw new OperacaoNaoPermitidaException("Esta proposta nao e sua.");
        }
        return proposta;
    }

    private void exigirDestinatario(PropostaTroca proposta, Usuario usuario) {
        if (!Objects.equals(proposta.getDestinatario().getId(), usuario.getId())) {
            throw new OperacaoNaoPermitidaException("Somente quem recebeu a proposta pode responde-la.");
        }
    }

    private void exigirPendente(PropostaTroca proposta) {
        if (proposta.getStatus() != StatusProposta.PENDENTE) {
            throw new RegraDeNegocioException("Esta proposta ja foi respondida.");
        }
    }

    private Anuncio carregar(Long id) {
        return anuncios.findById(id)
                .orElseThrow(() -> RecursoNaoEncontradoException.de("Anuncio", id));
    }

    private List<PropostaTrocaResponse> converter(List<PropostaTroca> lista, Usuario solicitante) {
        Set<Long> marcados = idsFavoritos(solicitante);
        return lista.stream().map(proposta -> Conversores.proposta(proposta, solicitante, marcados)).toList();
    }

    private Set<Long> idsFavoritos(Usuario usuario) {
        return usuario == null ? Set.of() : new HashSet<>(favoritos.idsDosAnunciosFavoritados(usuario));
    }
}
