package br.com.zyclos.bootstrap;

import br.com.zyclos.config.PropriedadesZyclos;
import br.com.zyclos.domain.Anuncio;
import br.com.zyclos.domain.AnuncioImagem;
import br.com.zyclos.domain.Avaliacao;
import br.com.zyclos.domain.Categoria;
import br.com.zyclos.domain.Condicao;
import br.com.zyclos.domain.Conversa;
import br.com.zyclos.domain.Denuncia;
import br.com.zyclos.domain.Favorito;
import br.com.zyclos.domain.Mensagem;
import br.com.zyclos.domain.Notificacao;
import br.com.zyclos.domain.Papel;
import br.com.zyclos.domain.PreferenciasUsuario;
import br.com.zyclos.domain.PropostaTroca;
import br.com.zyclos.domain.Seguidor;
import br.com.zyclos.domain.StatusDenuncia;
import br.com.zyclos.domain.StatusProposta;
import br.com.zyclos.domain.StatusUsuario;
import br.com.zyclos.domain.TipoNegociacao;
import br.com.zyclos.domain.TipoNotificacao;
import br.com.zyclos.domain.Usuario;
import br.com.zyclos.repository.AnuncioRepository;
import br.com.zyclos.repository.AvaliacaoRepository;
import br.com.zyclos.repository.CategoriaRepository;
import br.com.zyclos.repository.ConversaRepository;
import br.com.zyclos.repository.DenunciaRepository;
import br.com.zyclos.repository.FavoritoRepository;
import br.com.zyclos.repository.MensagemRepository;
import br.com.zyclos.repository.NotificacaoRepository;
import br.com.zyclos.repository.PropostaTrocaRepository;
import br.com.zyclos.repository.SeguidorRepository;
import br.com.zyclos.repository.UsuarioRepository;
import br.com.zyclos.service.ServicoDeCategoria;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

/**
 * Carga de demonstracao da ZYCLOS.
 *
 * <p>Reproduz no banco exatamente o conteudo que o front-end mantinha em src/data/mock.js:
 * as oito categorias, os seis anuncios com seus vendedores, as contas listadas no painel,
 * as avaliacoes, as conversas, as notificacoes, as denuncias e as propostas de troca.
 * Roda apenas quando o banco esta vazio e pode ser desligada com zyclos.seed.habilitado=false.
 */
@Configuration
public class CargaInicial {

    private static final Logger LOG = LoggerFactory.getLogger(CargaInicial.class);

    private static final List<String> CATEGORIAS = List.of(
            "Camisetas", "Calcas", "Moletons", "Jaquetas", "Shorts", "Vestidos", "Tenis", "Acessorios");

    private static final List<String> IMAGENS_SEMENTE = List.of(
            "product-hoodie.jpg", "product-sneakers.jpg", "product-jacket.jpg", "product-collection.jpg");

    @Bean
    ApplicationRunner semear(PropriedadesZyclos propriedades, Semeador semeador) {
        return argumentos -> {
            if (!propriedades.getSeed().isHabilitado()) {
                LOG.info("Carga de demonstracao desligada por configuracao.");
                return;
            }
            semeador.executar(propriedades.getSeed().getSenhaPadrao(),
                    propriedades.getUpload().getDiretorio());
        };
    }

    /** Separado em um bean proprio para que a carga rode dentro de uma transacao. */
    @org.springframework.stereotype.Component
    public static class Semeador {

        private final UsuarioRepository usuarios;
        private final CategoriaRepository categorias;
        private final AnuncioRepository anuncios;
        private final FavoritoRepository favoritos;
        private final SeguidorRepository seguidores;
        private final AvaliacaoRepository avaliacoes;
        private final ConversaRepository conversas;
        private final MensagemRepository mensagens;
        private final NotificacaoRepository notificacoes;
        private final PropostaTrocaRepository propostas;
        private final DenunciaRepository denuncias;
        private final PasswordEncoder codificador;
        private final JdbcTemplate jdbc;

        public Semeador(UsuarioRepository usuarios, CategoriaRepository categorias, AnuncioRepository anuncios,
                        FavoritoRepository favoritos, SeguidorRepository seguidores,
                        AvaliacaoRepository avaliacoes, ConversaRepository conversas,
                        MensagemRepository mensagens, NotificacaoRepository notificacoes,
                        PropostaTrocaRepository propostas, DenunciaRepository denuncias,
                        PasswordEncoder codificador, JdbcTemplate jdbc) {
            this.usuarios = usuarios;
            this.categorias = categorias;
            this.anuncios = anuncios;
            this.favoritos = favoritos;
            this.seguidores = seguidores;
            this.avaliacoes = avaliacoes;
            this.conversas = conversas;
            this.mensagens = mensagens;
            this.notificacoes = notificacoes;
            this.propostas = propostas;
            this.denuncias = denuncias;
            this.codificador = codificador;
            this.jdbc = jdbc;
        }

        @Transactional
        public void executar(String senhaPadrao, String pastaDeUploads) {
            if (usuarios.count() > 0) {
                LOG.info("Banco ja populado. Carga de demonstracao ignorada.");
                return;
            }
            LOG.info("Populando o banco com os dados de demonstracao da ZYCLOS.");

            Map<String, String> imagens = copiarImagens(pastaDeUploads);
            Map<String, Categoria> catalogo = criarCategorias();
            Map<String, Usuario> pessoas = criarPessoas(senhaPadrao);
            Map<String, Anuncio> pecas = criarAnuncios(catalogo, pessoas, imagens);

            criarRelacoesDePerfil(pessoas);
            criarAvaliacoes(pessoas);
            criarFavoritos(pessoas, pecas);
            criarConversas(pessoas, pecas);
            criarPropostas(pessoas, pecas);
            criarDenuncias(pessoas, pecas);
            criarNotificacoes(pessoas);

            LOG.info("Carga concluida. Entre com luis@exemplo.com ou admin@zyclos.com usando a senha {}.",
                    senhaPadrao);
        }

        private Map<String, Categoria> criarCategorias() {
            Map<String, Categoria> catalogo = new LinkedHashMap<>();
            CATEGORIAS.forEach(nome -> catalogo.put(nome,
                    categorias.save(new Categoria(nome, ServicoDeCategoria.gerarSlug(nome)))));
            return catalogo;
        }

        private Map<String, Usuario> criarPessoas(String senhaPadrao) {
            Map<String, Usuario> pessoas = new LinkedHashMap<>();
            String hash = codificador.encode(senhaPadrao);

            pessoas.put("admin", pessoa(hash, "Administracao ZYCLOS", "zyclos.admin", "admin@zyclos.com",
                    "Conta de moderacao da plataforma.", "Sao Paulo", "SP",
                    Papel.ADMIN, StatusUsuario.ATIVO, true, LocalDate.of(2024, 1, 10)));

            pessoas.put("luis", pessoa(hash, "Luis Fernando", "luis.zyclos", "luis@exemplo.com",
                    "Garimpando pecas com historia e dando destino ao que nao uso mais.",
                    "Sao Paulo", "SP", Papel.USUARIO, StatusUsuario.ATIVO, false, LocalDate.of(2025, 2, 18)));

            pessoas.put("livia", pessoa(hash, "Livia Rocha", "liviar", "livia@exemplo.com",
                    "Apaixonada por garimpos, pecas atemporais e novas formas de consumir moda. "
                            + "Todas as pecas sao cuidadas com carinho.",
                    "Sao Paulo", "SP", Papel.USUARIO, StatusUsuario.ATIVO, true, LocalDate.of(2024, 3, 14)));

            pessoas.put("caio", pessoa(hash, "Caio Mendes", "caiom", "caio@exemplo.com",
                    "Colecionador de tenis e fa de pecas retro.",
                    "Campinas", "SP", Papel.USUARIO, StatusUsuario.ATIVO, false, LocalDate.of(2026, 9, 4)));

            pessoas.put("nina", pessoa(hash, "Nina Costa", "ninac", "nina@exemplo.com",
                    "Streetwear e pecas de alfaiataria.",
                    "Rio de Janeiro", "RJ", Papel.USUARIO, StatusUsuario.SUSPENSO, false,
                    LocalDate.of(2026, 8, 22)));

            pessoas.put("ravi", pessoa(hash, "Ravi Alves", "ravialves", "ravi@exemplo.com",
                    "Camisetas de estampa e algodao leve.",
                    "Belo Horizonte", "MG", Papel.USUARIO, StatusUsuario.ATIVO, false,
                    LocalDate.of(2026, 8, 17)));

            pessoas.put("marina", pessoa(hash, "Marina Luz", "marinaluz", "marina@exemplo.com",
                    "Workwear, jeans e pecas que duram.",
                    "Curitiba", "PR", Papel.USUARIO, StatusUsuario.ATIVO, false, LocalDate.of(2025, 5, 2)));

            pessoas.put("bia", pessoa(hash, "Bia Nunes", "bianunes", "bia@exemplo.com",
                    "Vintage de verdade, com sinais de uso que contam historia.",
                    "Recife", "PE", Papel.USUARIO, StatusUsuario.ATIVO, false, LocalDate.of(2025, 6, 20)));

            pessoas.put("helena", pessoa(hash, "Helena Martins", "helenam", "helena@exemplo.com",
                    null, "Santos", "SP", Papel.USUARIO, StatusUsuario.ATIVO, false, LocalDate.of(2025, 7, 1)));

            pessoas.put("lucas", pessoa(hash, "Lucas Freire", "lucasf", "lucas@exemplo.com",
                    null, "Niteroi", "RJ", Papel.USUARIO, StatusUsuario.ATIVO, false, LocalDate.of(2025, 7, 9)));

            pessoas.put("joana", pessoa(hash, "Joana Lima", "joanalima", "joana@exemplo.com",
                    null, "Salvador", "BA", Papel.USUARIO, StatusUsuario.ATIVO, false,
                    LocalDate.of(2025, 8, 3)));

            return pessoas;
        }

        private Usuario pessoa(String hash, String nome, String username, String email, String bio,
                               String cidade, String estado, Papel papel, StatusUsuario status,
                               boolean verificado, LocalDate cadastro) {
            Usuario usuario = new Usuario(nome, username, email, hash);
            usuario.setBio(bio);
            usuario.setCidade(cidade);
            usuario.setEstado(estado);
            usuario.setPapel(papel);
            usuario.setStatus(status);
            usuario.setVerificado(verificado);
            usuario.setPreferencias(new PreferenciasUsuario(usuario));
            usuarios.saveAndFlush(usuario);
            retroagir("usuario", usuario.getId(), cadastro.atStartOfDay().toInstant(ZoneOffset.UTC));
            return usuario;
        }

        private Map<String, Anuncio> criarAnuncios(Map<String, Categoria> catalogo, Map<String, Usuario> pessoas,
                                                   Map<String, String> imagens) {
            Map<String, Anuncio> pecas = new LinkedHashMap<>();

            pecas.put("moletom", anuncio(pessoas.get("livia"), catalogo.get("Moletons"),
                    "Moletom lilas oversized",
                    "Moletom macio, pouco usado e em otimo estado. Modelagem ampla e acabamento impecavel.",
                    new BigDecimal("180.00"), "Nike", "M", Condicao.EXCELENTE, "Streetwear",
                    TipoNegociacao.VENDA_OU_TROCA, Set.of("conforto", "oversized"),
                    imagens.get("product-hoodie.jpg"), 5));

            pecas.put("tenis", anuncio(pessoas.get("caio"), catalogo.get("Tenis"),
                    "Tenis retro laranja",
                    "Tenis confortavel, higienizado e com marcas discretas de uso na sola.",
                    new BigDecimal("350.00"), "Nike", "39", Condicao.MUITO_BOM, "Casual",
                    TipoNegociacao.VENDA, Set.of("retro", "urbano"),
                    imagens.get("product-sneakers.jpg"), 4));

            pecas.put("jaquetaOliva", anuncio(pessoas.get("marina"), catalogo.get("Jaquetas"),
                    "Jaqueta workwear oliva",
                    "Jaqueta estruturada com bolsos amplos. Uma peca versatil para meia-estacao.",
                    null, "Carhartt", "G", Condicao.EXCELENTE, "Workwear",
                    TipoNegociacao.TROCA, Set.of("atemporal", "algodao"),
                    imagens.get("product-jacket.jpg"), 3));

            pecas.put("camiseta", anuncio(pessoas.get("ravi"), catalogo.get("Camisetas"),
                    "Camiseta natureza",
                    "Camiseta confortavel de algodao com estampa inspirada na natureza.",
                    new BigDecimal("100.00"), "Adidas", "P", Condicao.BOM, "Casual",
                    TipoNegociacao.VENDA_OU_TROCA, Set.of("estampa", "leve"),
                    imagens.get("product-collection.jpg"), 2));

            pecas.put("cargo", anuncio(pessoas.get("nina"), catalogo.get("Calcas"),
                    "Calca cargo preta",
                    "Calca cargo de cintura alta, tecido resistente e seis bolsos funcionais.",
                    new BigDecimal("145.00"), "Zara", "40", Condicao.MUITO_BOM, "Streetwear",
                    TipoNegociacao.VENDA, Set.of("cargo", "preta"),
                    imagens.get("product-collection.jpg"), 1));

            pecas.put("jaquetaVintage", anuncio(pessoas.get("bia"), catalogo.get("Jaquetas"),
                    "Jaqueta vintage verde",
                    "Peca vintage autentica, com sinais naturais de uso que fazem parte da historia.",
                    new BigDecimal("220.00"), "Levis", "M", Condicao.BOM, "Vintage",
                    TipoNegociacao.VENDA_OU_TROCA, Set.of("vintage", "verde"),
                    imagens.get("product-jacket.jpg"), 0));

            // Peca do usuario de demonstracao, usada para montar propostas de troca.
            pecas.put("minhaJaqueta", anuncio(pessoas.get("luis"), catalogo.get("Jaquetas"),
                    "Jaqueta jeans clara",
                    "Jaqueta jeans leve, lavagem clara, perfeita para meia-estacao.",
                    new BigDecimal("160.00"), "Levis", "M", Condicao.MUITO_BOM, "Casual",
                    TipoNegociacao.VENDA_OU_TROCA, Set.of("jeans", "classica"),
                    imagens.get("product-jacket.jpg"), 2));

            return pecas;
        }

        private Anuncio anuncio(Usuario vendedor, Categoria categoria, String titulo, String descricao,
                                BigDecimal preco, String marca, String tamanho, Condicao condicao,
                                String estilo, TipoNegociacao negociacao, Set<String> tags,
                                String imagem, int mesesAtras) {
            Anuncio anuncio = new Anuncio(titulo, descricao, categoria, vendedor);
            anuncio.setPreco(preco);
            anuncio.setMarca(marca);
            anuncio.setTamanho(tamanho);
            anuncio.setCondicao(condicao);
            anuncio.setEstilo(estilo);
            anuncio.setTipoNegociacao(negociacao);
            anuncio.setCidade(vendedor.getCidade());
            anuncio.setEstado(vendedor.getEstado());
            anuncio.setTags(tags);
            if (imagem != null) {
                anuncio.adicionarImagem(new AnuncioImagem(imagem));
            }
            anuncios.saveAndFlush(anuncio);
            retroagir("anuncio", anuncio.getId(),
                    Instant.now().minus(mesesAtras * 30L, ChronoUnit.DAYS));
            return anuncio;
        }

        private void criarRelacoesDePerfil(Map<String, Usuario> pessoas) {
            Usuario livia = pessoas.get("livia");
            List.of("luis", "caio", "marina", "bia", "helena", "lucas", "joana")
                    .forEach(chave -> seguidores.save(new Seguidor(pessoas.get(chave), livia)));
            seguidores.save(new Seguidor(pessoas.get("luis"), pessoas.get("caio")));
            seguidores.save(new Seguidor(pessoas.get("luis"), pessoas.get("marina")));
        }

        private void criarAvaliacoes(Map<String, Usuario> pessoas) {
            Usuario livia = pessoas.get("livia");
            avaliacoes.save(new Avaliacao(pessoas.get("helena"), livia, 5,
                    "Peca exatamente como descrita e atendimento muito gentil."));
            avaliacoes.save(new Avaliacao(pessoas.get("lucas"), livia, 5,
                    "Troca rapida e embalagem cuidadosa. Recomendo!"));
            avaliacoes.save(new Avaliacao(pessoas.get("joana"), livia, 4,
                    "Tudo certo com o produto e a entrega combinada."));
        }

        private void criarFavoritos(Map<String, Usuario> pessoas, Map<String, Anuncio> pecas) {
            // O front comecava com o anuncio 2, o tenis retro, ja favoritado.
            favoritos.save(new Favorito(pessoas.get("luis"), pecas.get("tenis")));
            favoritos.save(new Favorito(pessoas.get("luis"), pecas.get("jaquetaOliva")));
            favoritos.save(new Favorito(pessoas.get("caio"), pecas.get("moletom")));
        }

        private void criarConversas(Map<String, Usuario> pessoas, Map<String, Anuncio> pecas) {
            Usuario luis = pessoas.get("luis");

            conversa(luis, pessoas.get("marina"), pecas.get("jaquetaOliva"), List.of(
                    fala(pessoas.get("marina"), "A jaqueta ainda esta disponivel?", false),
                    fala(pessoas.get("marina"), "Consigo buscar no fim de semana.", false)));

            conversa(luis, pessoas.get("caio"), pecas.get("tenis"), List.of(
                    fala(luis, "Oi! O tenis continua disponivel?", true),
                    fala(pessoas.get("caio"), "Sim! Esta em otimo estado.", true),
                    fala(pessoas.get("caio"), "Gostei da sua proposta!", true)));

            conversa(luis, pessoas.get("bia"), pecas.get("jaquetaVintage"), List.of(
                    fala(pessoas.get("bia"), "Podemos combinar a entrega.", true)));
        }

        private void conversa(Usuario um, Usuario outro, Anuncio anuncio, List<Fala> falas) {
            Conversa conversa = conversas.save(Conversa.entre(um, outro, anuncio));
            falas.forEach(fala -> {
                Mensagem mensagem = new Mensagem(conversa, fala.autor(), fala.texto());
                if (fala.lida()) {
                    mensagem.marcarComoLida();
                }
                mensagens.save(mensagem);
            });
            conversa.tocar();
            conversas.save(conversa);
        }

        private void criarPropostas(Map<String, Usuario> pessoas, Map<String, Anuncio> pecas) {
            // Caio quer o tenis dele trocado pela jaqueta jeans do usuario de demonstracao.
            PropostaTroca recebida = new PropostaTroca(pessoas.get("caio"),
                    pecas.get("minhaJaqueta"), pecas.get("tenis"),
                    "Topa trocar o tenis retro pela sua jaqueta jeans?");
            propostas.save(recebida);

            PropostaTroca enviada = new PropostaTroca(pessoas.get("luis"),
                    pecas.get("jaquetaOliva"), pecas.get("minhaJaqueta"),
                    "Posso oferecer a jaqueta jeans pela workwear oliva.");
            propostas.save(enviada);

            PropostaTroca concluida = new PropostaTroca(pessoas.get("luis"),
                    pecas.get("camiseta"), pecas.get("minhaJaqueta"),
                    "Combinamos a entrega em mao.");
            concluida.setStatus(StatusProposta.CONCLUIDA);
            propostas.save(concluida);
        }

        private void criarDenuncias(Map<String, Usuario> pessoas, Map<String, Anuncio> pecas) {
            Denuncia descricao = new Denuncia(pessoas.get("luis"), "Descricao incorreta",
                    "As medidas informadas nao batem com a peca recebida.");
            descricao.setAnuncio(pecas.get("cargo"));
            descricao.setUsuarioDenunciado(pessoas.get("nina"));
            denuncias.save(descricao);

            Denuncia imagem = new Denuncia(pessoas.get("marina"), "Imagem inadequada",
                    "A foto do anuncio nao mostra a peca anunciada.");
            imagem.setAnuncio(pecas.get("camiseta"));
            imagem.setUsuarioDenunciado(pessoas.get("ravi"));
            imagem.resolver(StatusDenuncia.EM_ANALISE, null);
            denuncias.save(imagem);

            Denuncia conduta = new Denuncia(pessoas.get("bia"), "Conduta no chat",
                    "Mensagens fora do assunto da negociacao.");
            conduta.setAnuncio(pecas.get("tenis"));
            conduta.setUsuarioDenunciado(pessoas.get("caio"));
            conduta.resolver(StatusDenuncia.RESOLVIDA, "Orientacao enviada ao usuario.");
            denuncias.save(conduta);
        }

        private void criarNotificacoes(Map<String, Usuario> pessoas) {
            Usuario luis = pessoas.get("luis");
            notificacoes.saveAll(List.of(
                    new Notificacao(luis, "Caio enviou uma proposta de troca.",
                            TipoNotificacao.TROCA, "/trocas"),
                    new Notificacao(luis, "Marina comecou a seguir voce.",
                            TipoNotificacao.PERFIL, "/perfil/marinaluz"),
                    new Notificacao(luis, "Seu anuncio recebeu um favorito.",
                            TipoNotificacao.FAVORITO, "/perfil"),
                    new Notificacao(luis, "Voce recebeu uma nova mensagem.",
                            TipoNotificacao.MENSAGEM, "/mensagens"),
                    new Notificacao(luis, "Sua proposta foi aceita.",
                            TipoNotificacao.SUCESSO, "/trocas")));
        }

        /**
         * Copia as fotos de demonstracao para a pasta de uploads.
         *
         * <p>Sao as mesmas imagens que o front usava em src/assets, para que o catalogo
         * continue com a aparencia conhecida depois da troca dos dados mockados pela API.
         */
        private Map<String, String> copiarImagens(String pastaDeUploads) {
            Map<String, String> urls = new LinkedHashMap<>();
            Path destinoBase = Paths.get(pastaDeUploads).toAbsolutePath().normalize().resolve("seed");
            try {
                Files.createDirectories(destinoBase);
            } catch (IOException erro) {
                LOG.warn("Nao foi possivel criar a pasta das imagens de demonstracao.", erro);
                return urls;
            }
            for (String nome : IMAGENS_SEMENTE) {
                Path destino = destinoBase.resolve(nome);
                try (InputStream origem = new ClassPathResource("imagens-seed/" + nome).getInputStream()) {
                    Files.copy(origem, destino, StandardCopyOption.REPLACE_EXISTING);
                    urls.put(nome, "/api/imagens/seed/" + nome);
                } catch (IOException erro) {
                    LOG.warn("Imagem de demonstracao {} nao pode ser copiada.", nome, erro);
                }
            }
            return urls;
        }

        /** Ajusta a data de criacao gerada pelo Hibernate para a data historica do dado. */
        private void retroagir(String tabela, Long id, Instant data) {
            jdbc.update("UPDATE " + tabela + " SET criado_em = ? WHERE id = ?",
                    java.sql.Timestamp.from(data), id);
        }

        private static Fala fala(Usuario autor, String texto, boolean lida) {
            return new Fala(autor, texto, lida);
        }

        private record Fala(Usuario autor, String texto, boolean lida) {
        }
    }
}
