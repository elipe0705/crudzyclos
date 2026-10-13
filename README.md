# ZYCLOS API

Backend REST da plataforma de moda circular ZYCLOS (TCC), feito em Spring Boot seguindo a apostila
"Projeto Pizzaria Passo a Passo" (Prof. Rogério Caetano).

## Estrutura (MVC)

```
br.com.zyclos
├── config            CorsConfiguration (libera as portas 5173, 3000 e 8686)
├── controller        XxxController  -> endpoints /api/v1/...
└── model
    ├── entity        XxxEntity      -> uma classe por tabela do banco
    ├── repository    XxxRepository  -> extends JpaRepository<Entidade, Long>
    └── services      XxxService     -> regras de negócio (findAll, save, findById, update, delete)
```

Fluxo: Controller -> Service -> Repository -> Banco de Dados, com injeção de dependência via `@Autowired`.

## Como rodar

1. Abra o SQL Server Management Studio e execute `docs/sql/zyclos_db.sql` (F5). Ele cria o banco
   `zyclos_db`, as tabelas e algumas categorias iniciais.
2. Confira usuário e senha do SQL Server em `src/main/resources/application.properties`.
3. Execute a classe `ZyclosApplication` (ou `./mvnw spring-boot:run`). A API sobe em `http://localhost:8080`.

`spring.jpa.hibernate.ddl-auto=none`: o Hibernate não cria tabelas, por isso o script precisa rodar antes.

## Endpoints

Todas as entidades têm o mesmo CRUD:

| Método | Caminho | Ação |
| --- | --- | --- |
| GET | `/api/v1/{recurso}` | Listar todos |
| GET | `/api/v1/{recurso}/{id}` | Listar por ID |
| POST | `/api/v1/{recurso}` | Salvar |
| PUT | `/api/v1/{recurso}/{id}` | Atualizar |
| DELETE | `/api/v1/{recurso}/{id}` | Excluir (exclusão física) |

Recursos: `usuarios`, `categorias`, `anuncios`, `anuncio-imagens`, `favoritos`, `seguidores`,
`conversas`, `mensagens`, `notificacoes`, `avaliacoes`, `propostas-troca`, `denuncias`.

Os relacionamentos são enviados pelo id (ex.: `"categoriaId": 3, "vendedorId": 1`).

Respostas de erro:

| Status | Quando |
| --- | --- |
| 400 | Id não numérico, campo obrigatório faltando ou id relacionado inexistente |
| 404 | Registro não encontrado |
| 409 | Exclusão bloqueada porque outros registros dependem deste |

Exemplos prontos para o REST Client (VS Code) ou para copiar no Insomnia: `docs/requests/zyclos.http`.
