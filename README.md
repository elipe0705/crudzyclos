# ZYCLOS API

Backend REST da plataforma de moda circular ZYCLOS, escrito em Java 21 com Spring Boot 3.5.

O front-end em TanStack Start continua na raiz do repositório e não foi alterado. Esta pasta é
independente: tem o próprio `pom.xml`, o próprio `.gitignore` e o próprio ciclo de build.

## Stack

| Camada | Tecnologia |
| --- | --- |
| Linguagem | Java 21 |
| Framework | Spring Boot 3.5.16 (Web, Data JPA, Security, Validation, Actuator) |
| Banco | H2 em arquivo no desenvolvimento, PostgreSQL 16 em produção |
| Migrações | Flyway |
| Autenticação | JWT (jjwt 0.12) com senhas em BCrypt |
| Documentação | springdoc-openapi (Swagger UI) |
| Testes | JUnit 5, Spring Boot Test, MockMvc, AssertJ |

## Como rodar

Só é necessário ter o JDK 21. O Maven vem embutido pelo wrapper.

```sh
cd backend
./mvnw spring-boot:run
```

A API sobe em `http://localhost:8080`. Na primeira execução o Flyway cria o esquema e a carga
inicial popula o banco com os dados de demonstração.

| Recurso | Endereço |
| --- | --- |
| Documentação interativa | http://localhost:8080/swagger-ui.html |
| Contrato OpenAPI | http://localhost:8080/v3/api-docs |
| Console do banco H2 | http://localhost:8080/h2-console |
| Verificação de saúde | http://localhost:8080/actuator/health |

No console do H2 use a URL `jdbc:h2:file:./dados/zyclos`, usuário `zyclos`, senha `zyclos`.

### Testes

```sh
./mvnw test
```

Os testes usam o perfil `test`, com H2 em memória e a carga de demonstração desligada.

### PostgreSQL com Docker

```sh
docker compose up --build
```

O arquivo `compose.yaml` sobe o PostgreSQL e a API no perfil `prod`. Troque o valor de
`ZYCLOS_JWT_SEGREDO` antes de publicar em qualquer lugar.

## Contas de demonstração

Todas usam a senha `zyclos123`.

| E-mail | Papel | Observação |
| --- | --- | --- |
| `luis@exemplo.com` | Usuário | Conta principal da demonstração, com favoritos, conversas e trocas |
| `admin@zyclos.com` | Administrador | Único acesso ao painel em `/api/admin` |
| `livia@exemplo.com` | Usuário | Perfil com avaliações e seguidores |
| `caio@exemplo.com` | Usuário | Enviou uma proposta de troca pendente |
| `nina@exemplo.com` | Usuário | Conta suspensa, para testar o bloqueio no login |

## De onde vieram os dados

A carga inicial reproduz no banco exatamente o conteúdo que o front-end mantinha em
`src/data/mock.js` e nas telas administrativas: as oito categorias, os seis anúncios com seus
vendedores e localidades, as contas da tabela de usuários, as três avaliações do perfil da Lívia,
as três conversas, as cinco notificações, as três denúncias e as propostas de troca das abas de
trocas. Nenhum dado foi inventado além de um sétimo anúncio da conta de demonstração, necessário
para que ela tenha uma peça própria a oferecer em uma troca.

As fotos dos produtos são as mesmas de `src/assets`, copiadas para
`src/main/resources/imagens-seed` e publicadas em `/api/imagens/seed/...`.

A carga só roda quando o banco está vazio. Para desligá-la use `zyclos.seed.habilitado=false`.

## Modelo de dados

```
usuario ──1:1── preferencias_usuario
   │
   ├──1:N── anuncio ──N:1── categoria
   │           ├──1:N── anuncio_imagem
   │           └──1:N── anuncio_tag
   │
   ├──1:N── favorito ──N:1── anuncio
   ├──N:N── seguidor (seguidor_id, seguido_id)
   ├──1:N── avaliacao (autor_id, avaliado_id)
   ├──1:N── notificacao
   ├──N:N── conversa ──1:N── mensagem
   ├──1:N── proposta_troca (anuncio_desejado_id, anuncio_oferecido_id)
   ├──1:N── denuncia
   └──1:N── token_recuperacao
```

O esquema completo está em `src/main/resources/db/migration/V1__esquema_inicial.sql`, escrito em
SQL portável para rodar tanto no H2 quanto no PostgreSQL.

### Decisões que valem registro

- **Remoção lógica de anúncios.** Excluir um anúncio muda o status para `REMOVIDO` em vez de apagar
  a linha, porque propostas de troca já concluídas continuam apontando para ele.
- **Participantes de conversa ordenados por id.** A conversa guarda o par sempre do menor id para o
  maior, o que torna a chave única independente de quem iniciou o contato.
- **Enums com rótulo.** `TipoNegociacao`, `Condicao` e os status trafegam na API com o mesmo texto
  que as telas já exibiam (`"Venda ou troca"`, `"Muito bom"`), e aceitam tanto esse rótulo quanto a
  constante técnica na entrada.
- **Preço opcional.** Anúncios de troca pura não têm preço; a validação exige o valor apenas quando
  a negociação inclui venda.

## Endpoints

Tudo responde abaixo de `/api`. As rotas marcadas como públicas dispensam token.

### Autenticação

| Método | Rota | Acesso |
| --- | --- | --- |
| POST | `/auth/cadastro` | Pública |
| POST | `/auth/login` | Pública |
| POST | `/auth/recuperar-senha` | Pública |
| POST | `/auth/redefinir-senha` | Pública |
| GET | `/auth/eu` | Autenticado |
| POST | `/auth/alterar-senha` | Autenticado |

### Catálogo

| Método | Rota | Acesso |
| --- | --- | --- |
| GET | `/anuncios` | Pública |
| GET | `/anuncios/{id}` | Pública |
| GET | `/categorias` | Pública |
| GET | `/filtros` | Pública |
| GET | `/meus-anuncios` | Autenticado |
| POST | `/anuncios` | Autenticado |
| PUT | `/anuncios/{id}` | Dono do anúncio |
| PATCH | `/anuncios/{id}/status` | Dono do anúncio |
| DELETE | `/anuncios/{id}` | Dono do anúncio |

Parâmetros de `GET /anuncios`: `q`, `categoria`, `negociacao`, `marca`, `tamanho`, `condicao`,
`cidade`, `precoMinimo`, `precoMaximo`, `ordenar`, `pagina`, `tamanhoDaPagina`.
Valores de `ordenar`: `recentes` (padrão), `menor`, `maior`, `relevantes`.

### Favoritos, perfis e configurações

| Método | Rota | Acesso |
| --- | --- | --- |
| GET | `/favoritos` e `/favoritos/ids` | Autenticado |
| POST | `/favoritos/{anuncioId}` | Autenticado, alterna o estado |
| DELETE | `/favoritos/{anuncioId}` | Autenticado |
| GET | `/perfis/{username}` | Pública |
| GET | `/perfis/{username}/avaliacoes` | Pública |
| POST | `/perfis/{username}/avaliacoes` | Autenticado, uma por pessoa |
| POST | `/perfis/{username}/seguir` | Autenticado, alterna o estado |
| GET, PUT | `/perfil` | Autenticado |
| GET, PUT | `/configuracoes` | Autenticado |

### Mensagens e notificações

| Método | Rota | Acesso |
| --- | --- | --- |
| GET, POST | `/conversas` | Autenticado |
| GET | `/conversas/nao-lidas` | Autenticado |
| GET, POST | `/conversas/{id}/mensagens` | Participante da conversa |
| PATCH | `/conversas/{id}/lidas` | Participante da conversa |
| GET | `/notificacoes` | Autenticado |
| GET | `/notificacoes/nao-lidas` | Autenticado |
| PATCH | `/notificacoes/{id}/lida` e `/notificacoes/lidas` | Autenticado |

### Trocas e denúncias

| Método | Rota | Acesso |
| --- | --- | --- |
| GET | `/trocas/enviadas`, `/trocas/recebidas`, `/trocas/historico` | Autenticado |
| POST | `/trocas` | Autenticado |
| PATCH | `/trocas/{id}/aceitar` e `/trocas/{id}/recusar` | Quem recebeu a proposta |
| PATCH | `/trocas/{id}/cancelar` | Quem enviou a proposta |
| PATCH | `/trocas/{id}/concluir` | Qualquer uma das partes |
| POST | `/denuncias` | Autenticado |

### Uploads e painel administrativo

| Método | Rota | Acesso |
| --- | --- | --- |
| POST | `/uploads/imagem` e `/uploads/imagens` | Autenticado, multipart |
| GET | `/imagens/{arquivo}` | Pública |
| GET | `/admin/dashboard` e `/admin/relatorios` | Administrador |
| GET | `/admin/anuncios` e `/admin/usuarios` | Administrador |
| PATCH | `/admin/usuarios/{id}/status` | Administrador |
| GET, POST | `/admin/categorias` | Administrador |
| PUT, DELETE | `/admin/categorias/{id}` | Administrador |
| GET | `/admin/denuncias` | Administrador |
| PATCH | `/admin/denuncias/{id}` | Administrador |

## Formato das respostas

Listagens paginadas devolvem um envelope:

```json
{
  "itens": [],
  "pagina": 0,
  "tamanho": 12,
  "total": 7,
  "totalPaginas": 1,
  "primeira": true,
  "ultima": true
}
```

Erros seguem sempre o mesmo corpo, com `campos` presente apenas em falhas de validação:

```json
{
  "momento": "2026-10-05T20:58:29.790Z",
  "status": 400,
  "erro": "Dados invalidos",
  "mensagem": "Informe o titulo do anuncio.",
  "caminho": "/api/anuncios",
  "campos": { "titulo": "Informe o titulo do anuncio." }
}
```

| Status | Quando acontece |
| --- | --- |
| 400 | Corpo ou parâmetro inválido |
| 401 | Sem token, token expirado ou senha incorreta |
| 403 | Conta suspensa, recurso de outra pessoa ou rota administrativa |
| 404 | Recurso inexistente |
| 409 | E-mail, nome de usuário, categoria ou proposta em duplicidade |
| 413 | Imagem acima do tamanho máximo |
| 422 | Regra de negócio violada |

## Ligando o front-end

O front hoje importa os dados de `src/data/mock.js`. Para passar a consumir a API, troque esses
imports por chamadas a `http://localhost:8080/api` e guarde o token devolvido pelo login.

Pontos de atenção na integração:

- **Identificadores são números.** O mock usava `"1"` como texto; a API devolve `1`. Comparações com
  parâmetros de rota precisam de `String(anuncio.id) === params.id`.
- **O card usa `titulo`, não `name`.** Os demais campos mantêm os nomes conhecidos: `preco`,
  `imagem`, `categoria`, `marca`, `tamanho`, `condicao`, `localizacao`, `tipoNegociacao`.
- **O token vai no cabeçalho** `Authorization: Bearer <token>` e vale 12 horas por padrão.
- **CORS já libera** `localhost:3000` e `localhost:5173`. Outras origens entram em
  `zyclos.cors.origens-permitidas`.
- **Favoritos deixam de ser locais.** `POST /api/favoritos/{id}` alterna o estado e devolve
  `{ "favorito": true }`, substituindo o `toggleFavorite` do contexto React.

## Configuração

Tudo é ajustável por variável de ambiente, sem recompilar.

| Variável | Para que serve | Padrão |
| --- | --- | --- |
| `SPRING_PROFILES_ACTIVE` | `dev`, `test` ou `prod` | `dev` |
| `ZYCLOS_JWT_SEGREDO` | Chave HMAC do token, 32 bytes ou mais | Valor fixo de desenvolvimento |
| `ZYCLOS_DB_URL` | JDBC do PostgreSQL no perfil `prod` | `jdbc:postgresql://localhost:5432/zyclos` |
| `ZYCLOS_DB_USUARIO` | Usuário do banco | `zyclos` |
| `ZYCLOS_DB_SENHA` | Senha do banco | vazio |
| `ZYCLOS_UPLOAD_DIR` | Pasta das fotos enviadas | `dados/uploads` |
| `ZYCLOS_CORS_ORIGENS` | Origens liberadas no perfil `prod` | `https://zyclos.app` |

No perfil `prod` o segredo do JWT precisa ser definido. Sem ele a aplicação gera uma chave
temporária, registra um aviso no log e invalida todos os tokens a cada reinício.

## Estrutura do código

```
src/main/java/br/com/zyclos/
├── ZyclosApplication.java
├── bootstrap/    carga de demonstração
├── config/       segurança, CORS, OpenAPI, conversores e propriedades
├── domain/       entidades JPA e enums
├── dto/          request e response
├── exception/    exceções de negócio e tratador global
├── mapper/       conversão de entidade para DTO
├── repository/   Spring Data e especificações de busca
├── security/     JWT, filtro e detalhes do usuário
├── service/      regras de negócio
└── web/          controladores REST
```

## Limites conhecidos

- **A recuperação de senha não envia e-mail.** O código é gravado no banco e escrito no log; fora do
  perfil `prod` ele também volta no corpo da resposta, para permitir a demonstração do fluxo.
- **As fotos ficam em disco local.** Em um ambiente com mais de uma instância isso precisa virar um
  serviço de objetos, como S3 ou similar.
- **Não há atualização em tempo real.** As telas de mensagens e notificações precisam consultar a
  API periodicamente; WebSocket ficou fora do escopo.
