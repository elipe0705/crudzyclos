-- Banco de dados da ZYCLOS (SQL Server)
-- Rode no SQL Server Management Studio com F5 antes de subir a API (ddl-auto=none).

USE master IF EXISTS (SELECT * FROM SYS.databases WHERE name = 'zyclos_db')

DROP DATABASE zyclos_db
GO

CREATE DATABASE zyclos_db;
GO

USE zyclos_db
GO

CREATE TABLE Usuario (
  id BIGINT NOT NULL IDENTITY(1,1) PRIMARY KEY,
  nome VARCHAR(100) NOT NULL,
  username VARCHAR(30) NOT NULL,
  email VARCHAR(255) NOT NULL,
  senha VARCHAR(255) NOT NULL,
  avatar_url VARCHAR(500) NULL,
  bio VARCHAR(500) NULL,
  cidade VARCHAR(120) NULL,
  estado VARCHAR(2) NULL,
  tipo_usuario VARCHAR(20) NOT NULL,
  verificado BIT NOT NULL,
  data_cadastro DATETIME2 NOT NULL,
  cod_status BIT NOT NULL,
  CONSTRAINT uk_usuario_email UNIQUE (email),
  CONSTRAINT uk_usuario_username UNIQUE (username)
)

CREATE TABLE Categoria (
  id BIGINT NOT NULL IDENTITY(1,1) PRIMARY KEY,
  nome VARCHAR(60) NOT NULL,
  slug VARCHAR(60) NOT NULL,
  descricao VARCHAR(255) NULL,
  cod_status BIT NOT NULL,
  CONSTRAINT uk_categoria_nome UNIQUE (nome),
  CONSTRAINT uk_categoria_slug UNIQUE (slug)
)

CREATE TABLE Anuncio (
  id BIGINT NOT NULL IDENTITY(1,1) PRIMARY KEY,
  titulo VARCHAR(120) NOT NULL,
  descricao VARCHAR(2000) NOT NULL,
  preco DECIMAL(10,2) NULL,
  marca VARCHAR(60) NULL,
  tamanho VARCHAR(20) NOT NULL,
  condicao VARCHAR(30) NOT NULL,
  estilo VARCHAR(40) NULL,
  cidade VARCHAR(120) NULL,
  estado VARCHAR(2) NULL,
  tipo_negociacao VARCHAR(20) NOT NULL,
  status VARCHAR(20) NOT NULL,
  visualizacoes BIGINT NOT NULL,
  data_cadastro DATETIME2 NOT NULL,
  data_atualizacao DATETIME2 NULL,
  categoria_id BIGINT NOT NULL,
  vendedor_id BIGINT NOT NULL,
  cod_status BIT NOT NULL,
  CONSTRAINT fk_anuncio_categoria_id FOREIGN KEY (categoria_id) REFERENCES Categoria(id),
  CONSTRAINT fk_anuncio_vendedor_id FOREIGN KEY (vendedor_id) REFERENCES Usuario(id)
)

CREATE TABLE anuncio_imagem (
  id BIGINT NOT NULL IDENTITY(1,1) PRIMARY KEY,
  url VARCHAR(500) NOT NULL,
  principal BIT NOT NULL,
  ordem INT NOT NULL,
  anuncio_id BIGINT NOT NULL,
  cod_status BIT NOT NULL,
  CONSTRAINT fk_anuncioImagem_anuncio_id FOREIGN KEY (anuncio_id) REFERENCES Anuncio(id)
)

CREATE TABLE Favorito (
  id BIGINT NOT NULL IDENTITY(1,1) PRIMARY KEY,
  data_cadastro DATETIME2 NOT NULL,
  usuario_id BIGINT NOT NULL,
  anuncio_id BIGINT NOT NULL,
  cod_status BIT NOT NULL,
  CONSTRAINT uk_favorito_usuario_anuncio UNIQUE (usuario_id, anuncio_id),
  CONSTRAINT fk_favorito_usuario_id FOREIGN KEY (usuario_id) REFERENCES Usuario(id),
  CONSTRAINT fk_favorito_anuncio_id FOREIGN KEY (anuncio_id) REFERENCES Anuncio(id)
)

CREATE TABLE Seguidor (
  id BIGINT NOT NULL IDENTITY(1,1) PRIMARY KEY,
  data_cadastro DATETIME2 NOT NULL,
  seguidor_id BIGINT NOT NULL,
  seguido_id BIGINT NOT NULL,
  cod_status BIT NOT NULL,
  CONSTRAINT uk_seguidor_par UNIQUE (seguidor_id, seguido_id),
  CONSTRAINT fk_seguidor_seguidor_id FOREIGN KEY (seguidor_id) REFERENCES Usuario(id),
  CONSTRAINT fk_seguidor_seguido_id FOREIGN KEY (seguido_id) REFERENCES Usuario(id)
)

CREATE TABLE Conversa (
  id BIGINT NOT NULL IDENTITY(1,1) PRIMARY KEY,
  data_cadastro DATETIME2 NOT NULL,
  data_atualizacao DATETIME2 NULL,
  participante_a_id BIGINT NOT NULL,
  participante_b_id BIGINT NOT NULL,
  anuncio_id BIGINT NULL,
  cod_status BIT NOT NULL,
  CONSTRAINT fk_conversa_participante_a_id FOREIGN KEY (participante_a_id) REFERENCES Usuario(id),
  CONSTRAINT fk_conversa_participante_b_id FOREIGN KEY (participante_b_id) REFERENCES Usuario(id),
  CONSTRAINT fk_conversa_anuncio_id FOREIGN KEY (anuncio_id) REFERENCES Anuncio(id)
)

CREATE TABLE Mensagem (
  id BIGINT NOT NULL IDENTITY(1,1) PRIMARY KEY,
  conteudo VARCHAR(2000) NOT NULL,
  lida BIT NOT NULL,
  data_envio DATETIME2 NOT NULL,
  conversa_id BIGINT NOT NULL,
  remetente_id BIGINT NOT NULL,
  cod_status BIT NOT NULL,
  CONSTRAINT fk_mensagem_conversa_id FOREIGN KEY (conversa_id) REFERENCES Conversa(id),
  CONSTRAINT fk_mensagem_remetente_id FOREIGN KEY (remetente_id) REFERENCES Usuario(id)
)

CREATE TABLE Notificacao (
  id BIGINT NOT NULL IDENTITY(1,1) PRIMARY KEY,
  texto VARCHAR(300) NOT NULL,
  tipo VARCHAR(20) NOT NULL,
  destino VARCHAR(200) NULL,
  lida BIT NOT NULL,
  data_cadastro DATETIME2 NOT NULL,
  usuario_id BIGINT NOT NULL,
  cod_status BIT NOT NULL,
  CONSTRAINT fk_notificacao_usuario_id FOREIGN KEY (usuario_id) REFERENCES Usuario(id)
)

CREATE TABLE Avaliacao (
  id BIGINT NOT NULL IDENTITY(1,1) PRIMARY KEY,
  nota INT NOT NULL,
  comentario VARCHAR(1000) NULL,
  data_cadastro DATETIME2 NOT NULL,
  autor_id BIGINT NOT NULL,
  avaliado_id BIGINT NOT NULL,
  anuncio_id BIGINT NULL,
  cod_status BIT NOT NULL,
  CONSTRAINT ck_avaliacao_nota CHECK (nota BETWEEN 1 AND 5),
  CONSTRAINT fk_avaliacao_autor_id FOREIGN KEY (autor_id) REFERENCES Usuario(id),
  CONSTRAINT fk_avaliacao_avaliado_id FOREIGN KEY (avaliado_id) REFERENCES Usuario(id),
  CONSTRAINT fk_avaliacao_anuncio_id FOREIGN KEY (anuncio_id) REFERENCES Anuncio(id)
)

CREATE TABLE proposta_troca (
  id BIGINT NOT NULL IDENTITY(1,1) PRIMARY KEY,
  mensagem VARCHAR(500) NULL,
  status VARCHAR(20) NOT NULL,
  data_cadastro DATETIME2 NOT NULL,
  data_atualizacao DATETIME2 NULL,
  proponente_id BIGINT NOT NULL,
  destinatario_id BIGINT NOT NULL,
  anuncio_desejado_id BIGINT NOT NULL,
  anuncio_oferecido_id BIGINT NOT NULL,
  cod_status BIT NOT NULL,
  CONSTRAINT fk_propostaTroca_proponente_id FOREIGN KEY (proponente_id) REFERENCES Usuario(id),
  CONSTRAINT fk_propostaTroca_destinatario_id FOREIGN KEY (destinatario_id) REFERENCES Usuario(id),
  CONSTRAINT fk_propostaTroca_anuncio_desejado_id FOREIGN KEY (anuncio_desejado_id) REFERENCES Anuncio(id),
  CONSTRAINT fk_propostaTroca_anuncio_oferecido_id FOREIGN KEY (anuncio_oferecido_id) REFERENCES Anuncio(id)
)

CREATE TABLE Denuncia (
  id BIGINT NOT NULL IDENTITY(1,1) PRIMARY KEY,
  motivo VARCHAR(120) NOT NULL,
  descricao VARCHAR(1000) NULL,
  status VARCHAR(20) NOT NULL,
  resposta_moderacao VARCHAR(1000) NULL,
  data_cadastro DATETIME2 NOT NULL,
  data_resolucao DATETIME2 NULL,
  denunciante_id BIGINT NOT NULL,
  anuncio_id BIGINT NULL,
  usuario_denunciado_id BIGINT NULL,
  cod_status BIT NOT NULL,
  CONSTRAINT fk_denuncia_denunciante_id FOREIGN KEY (denunciante_id) REFERENCES Usuario(id),
  CONSTRAINT fk_denuncia_anuncio_id FOREIGN KEY (anuncio_id) REFERENCES Anuncio(id),
  CONSTRAINT fk_denuncia_usuario_denunciado_id FOREIGN KEY (usuario_denunciado_id) REFERENCES Usuario(id)
)
GO

-- Categorias iniciais

INSERT INTO Categoria (nome, slug, descricao, cod_status) VALUES
  ('Camisetas', 'camisetas', 'Camisetas e regatas', 1),
  ('Calças', 'calcas', 'Calças, jeans e bermudas', 1),
  ('Casacos', 'casacos', 'Jaquetas, moletons e casacos', 1),
  ('Calçados', 'calcados', 'Tênis, botas e sandálias', 1),
  ('Acessórios', 'acessorios', 'Bolsas, bonés e bijuterias', 1)
GO
