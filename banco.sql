-- =====================================================================
--  Consumo Digital — Hotel e Day Use (Projeto Integrador Senac)
--  Schema inicial · PostgreSQL 15+ (Supabase)
--  Uso: Flyway (V1__schema_inicial.sql) ou SQL Editor do Supabase
--
--  Decisões:
--  - Tudo no schema "hotel", que NÃO é exposto pela API REST do Supabase.
--    Só o backend Java acessa o banco (via JDBC / Session Pooler).
--  - Status como TEXT + CHECK (mais simples de mapear no JPA que ENUM nativo).
--  - Dinheiro em NUMERIC(10,2). Saldo NÃO é coluna: é calculado (view no fim).
--  - Vínculos entre tabelas diferentes (ex.: participante da proposta pertence
--    à permanência da conta) são validados no servidor, como pede o documento.
-- =====================================================================

CREATE SCHEMA IF NOT EXISTS hotel;
SET search_path TO hotel;

-- ---------------------------------------------------------------------
-- 1. CADASTROS BÁSICOS
-- ---------------------------------------------------------------------

CREATE TABLE funcionario (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nome          VARCHAR(120) NOT NULL,
    login         VARCHAR(60)  NOT NULL UNIQUE,
    senha_hash    VARCHAR(255) NOT NULL,
    perfil        VARCHAR(20)  NOT NULL
                  CHECK (perfil IN ('ATENDENTE', 'CAIXA', 'ADMIN')),
    ativo         BOOLEAN      NOT NULL DEFAULT TRUE,
    criado_em     TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE TABLE cliente (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nome          VARCHAR(120) NOT NULL,
    telefone      VARCHAR(20),
    email         VARCHAR(120),
    ativo         BOOLEAN      NOT NULL DEFAULT TRUE,
    criado_em     TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE TABLE quarto (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    numero        VARCHAR(10)  NOT NULL UNIQUE,
    ativo         BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE setor (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nome          VARCHAR(60)  NOT NULL UNIQUE,      -- Bar, Restaurante, Piscina...
    ativo         BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE terminal (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nome          VARCHAR(60)  NOT NULL,             -- ex.: "Tablet Bar"
    setor_id      BIGINT       NOT NULL REFERENCES setor(id),
    token_hash    VARCHAR(255) NOT NULL UNIQUE,      -- autentica o dispositivo
    ativo         BOOLEAN      NOT NULL DEFAULT TRUE,
    criado_em     TIMESTAMPTZ  NOT NULL DEFAULT now()
);

-- ---------------------------------------------------------------------
-- 2. PERMANÊNCIA, CONTA E PARTICIPANTES
-- ---------------------------------------------------------------------

CREATE TABLE permanencia (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tipo            VARCHAR(12)  NOT NULL
                    CHECK (tipo IN ('HOSPEDAGEM', 'DAY_USE')),
    quarto_id       BIGINT       REFERENCES quarto(id),
    responsavel_id  BIGINT       NOT NULL REFERENCES cliente(id),
    inicio          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    fim_previsto    TIMESTAMPTZ  NOT NULL,
    encerrada_em    TIMESTAMPTZ,
    aberta_por      BIGINT       NOT NULL REFERENCES funcionario(id),
    encerrada_por   BIGINT       REFERENCES funcionario(id),

    -- RN02: hospedagem exige quarto; day use pode não ter
    CONSTRAINT ck_permanencia_quarto
        CHECK (tipo = 'DAY_USE' OR quarto_id IS NOT NULL),
    CONSTRAINT ck_permanencia_datas
        CHECK (fim_previsto > inicio),
    CONSTRAINT ck_permanencia_encerramento
        CHECK ((encerrada_em IS NULL) = (encerrada_por IS NULL))
);

-- Um quarto não pode ter duas hospedagens abertas ao mesmo tempo
-- (novo ocupante = nova permanência, só depois de encerrar a anterior)
CREATE UNIQUE INDEX ux_quarto_hospedagem_aberta
    ON permanencia (quarto_id)
    WHERE tipo = 'HOSPEDAGEM' AND encerrada_em IS NULL;

CREATE TABLE conta (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    permanencia_id  BIGINT       NOT NULL UNIQUE REFERENCES permanencia(id), -- MVP: 1 conta principal
    status          VARCHAR(15)  NOT NULL DEFAULT 'ABERTA'
                    CHECK (status IN ('ABERTA', 'EM_FECHAMENTO', 'ENCERRADA')),
    aberta_em       TIMESTAMPTZ  NOT NULL DEFAULT now(),
    fechada_em      TIMESTAMPTZ,
    fechada_por     BIGINT       REFERENCES funcionario(id),

    CONSTRAINT ck_conta_fechamento
        CHECK ((status = 'ENCERRADA') = (fechada_em IS NOT NULL))
);

CREATE TABLE participante (
    id                  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    permanencia_id      BIGINT       NOT NULL REFERENCES permanencia(id),
    cliente_id          BIGINT       NOT NULL REFERENCES cliente(id),
    pode_consumir       BOOLEAN      NOT NULL DEFAULT TRUE,
    pin_hash            VARCHAR(255),                  -- nulo até o cliente definir
    pin_falhas          SMALLINT     NOT NULL DEFAULT 0 CHECK (pin_falhas >= 0),
    pin_bloqueado_ate   TIMESTAMPTZ,                   -- RNF09: 5 falhas -> 15 min
    criado_em           TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT ux_participante UNIQUE (permanencia_id, cliente_id)
);

-- ---------------------------------------------------------------------
-- 3. CREDENCIAIS E ACESSO DO CLIENTE
-- ---------------------------------------------------------------------

CREATE TABLE credencial (
    id                  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    participante_id     BIGINT       NOT NULL REFERENCES participante(id),
    -- String que vira QR Code e Code 128. Aleatória, sem dados pessoais (RN03).
    -- Só A-Z e 0-9 para o leitor USB não trocar caracteres por layout de teclado.
    identificador       VARCHAR(16)  NOT NULL UNIQUE
                        CHECK (identificador ~ '^[0-9A-Z]{12,16}$'),
    status              VARCHAR(10)  NOT NULL DEFAULT 'ATIVA'
                        CHECK (status IN ('ATIVA', 'BLOQUEADA', 'EXPIRADA', 'REVOGADA')),
    emitida_em          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    emitida_por         BIGINT       NOT NULL REFERENCES funcionario(id),
    valida_ate          TIMESTAMPTZ  NOT NULL,
    bloqueada_em        TIMESTAMPTZ,
    bloqueada_por       BIGINT       REFERENCES funcionario(id),
    motivo_bloqueio     VARCHAR(200),
    substituida_por_id  BIGINT       REFERENCES credencial(id),   -- RF08: histórico de reemissão

    CONSTRAINT ck_credencial_bloqueio
        CHECK (status <> 'BLOQUEADA' OR (bloqueada_em IS NOT NULL AND bloqueada_por IS NOT NULL))
);

-- No máximo uma credencial ATIVA por participante
CREATE UNIQUE INDEX ux_credencial_ativa
    ON credencial (participante_id)
    WHERE status = 'ATIVA';

-- RF07 / RF29: código de uso único para ativar ou recuperar o acesso web
CREATE TABLE ativacao_acesso (
    id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    participante_id   BIGINT       NOT NULL REFERENCES participante(id),
    codigo_hash       VARCHAR(255) NOT NULL UNIQUE,
    criado_em         TIMESTAMPTZ  NOT NULL DEFAULT now(),
    criado_por        BIGINT       NOT NULL REFERENCES funcionario(id),
    expira_em         TIMESTAMPTZ  NOT NULL,
    usado_em          TIMESTAMPTZ                      -- preenchido = não vale mais
);

-- Sessões do cliente no navegador (permite revogar: RF23, RF26, RF29)
CREATE TABLE sessao_cliente (
    id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    participante_id   BIGINT       NOT NULL REFERENCES participante(id),
    token_hash        VARCHAR(255) NOT NULL UNIQUE,
    criada_em         TIMESTAMPTZ  NOT NULL DEFAULT now(),
    expira_em         TIMESTAMPTZ  NOT NULL,
    revogada_em       TIMESTAMPTZ
);

CREATE INDEX ix_sessao_participante ON sessao_cliente (participante_id) WHERE revogada_em IS NULL;

-- ---------------------------------------------------------------------
-- 4. CATÁLOGO
-- ---------------------------------------------------------------------

CREATE TABLE categoria (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nome          VARCHAR(60)  NOT NULL UNIQUE,
    ativo         BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE produto (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    categoria_id  BIGINT        NOT NULL REFERENCES categoria(id),
    nome          VARCHAR(120)  NOT NULL,
    descricao     VARCHAR(500),
    preco         NUMERIC(10,2) NOT NULL CHECK (preco >= 0),
    foto_url      VARCHAR(500),
    disponivel    BOOLEAN       NOT NULL DEFAULT TRUE,  -- falso = aparece, mas não entra no carrinho
    ativo         BOOLEAN       NOT NULL DEFAULT TRUE,  -- falso = saiu do catálogo
    criado_em     TIMESTAMPTZ   NOT NULL DEFAULT now()
);

CREATE INDEX ix_produto_categoria ON produto (categoria_id) WHERE ativo;

-- ---------------------------------------------------------------------
-- 5. PROPOSTA DE CONSUMO E DECISÃO DO CLIENTE
-- ---------------------------------------------------------------------

CREATE TABLE proposta (
    id                    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    conta_id              BIGINT        NOT NULL REFERENCES conta(id),
    participante_id       BIGINT        NOT NULL REFERENCES participante(id), -- consumidor
    credencial_id         BIGINT        NOT NULL REFERENCES credencial(id),   -- credencial lida
    atendente_id          BIGINT        NOT NULL REFERENCES funcionario(id),
    setor_id              BIGINT        NOT NULL REFERENCES setor(id),
    versao                INTEGER       NOT NULL DEFAULT 1 CHECK (versao > 0),
    status                VARCHAR(12)   NOT NULL DEFAULT 'RASCUNHO'
                          CHECK (status IN ('RASCUNHO', 'AGUARDANDO', 'CONFIRMADA',
                                            'RECUSADA', 'EXPIRADA', 'CANCELADA')),
    total                 NUMERIC(10,2) NOT NULL DEFAULT 0 CHECK (total >= 0), -- calculado pelo servidor
    criada_em             TIMESTAMPTZ   NOT NULL DEFAULT now(),
    enviada_em            TIMESTAMPTZ,
    expira_em             TIMESTAMPTZ,                -- enviada_em + 5 min (RN07)
    finalizada_em         TIMESTAMPTZ,
    cancelada_por         BIGINT        REFERENCES funcionario(id),
    motivo_cancelamento   VARCHAR(200),

    CONSTRAINT ck_proposta_envio
        CHECK (status = 'RASCUNHO' OR (enviada_em IS NOT NULL AND expira_em IS NOT NULL)),
    CONSTRAINT ck_proposta_finalizada
        CHECK ((status IN ('RASCUNHO', 'AGUARDANDO')) = (finalizada_em IS NULL))
);

CREATE INDEX ix_proposta_pendente_participante
    ON proposta (participante_id)
    WHERE status = 'AGUARDANDO';                          -- polling do cliente
CREATE INDEX ix_proposta_conta ON proposta (conta_id);

CREATE TABLE item_proposta (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    proposta_id     BIGINT        NOT NULL REFERENCES proposta(id) ON DELETE CASCADE, -- só rascunho é apagável
    produto_id      BIGINT        NOT NULL REFERENCES produto(id),
    nome_produto    VARCHAR(120)  NOT NULL,              -- cópia no envio (RN06)
    preco_unitario  NUMERIC(10,2) NOT NULL CHECK (preco_unitario >= 0), -- congelado (RN06)
    quantidade      INTEGER       NOT NULL CHECK (quantidade > 0),       -- RN05
    observacao      VARCHAR(200)
);

CREATE INDEX ix_item_proposta ON item_proposta (proposta_id);

-- Confirmação OU recusa do cliente. Uma decisão por proposta.
CREATE TABLE decisao_proposta (
    id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    proposta_id      BIGINT       NOT NULL UNIQUE REFERENCES proposta(id),
    participante_id  BIGINT       NOT NULL REFERENCES participante(id),
    resultado        VARCHAR(10)  NOT NULL CHECK (resultado IN ('CONFIRMADA', 'RECUSADA')),
    canal            VARCHAR(10)  NOT NULL CHECK (canal IN ('WEB', 'TERMINAL')),
    terminal_id      BIGINT       REFERENCES terminal(id),
    id_operacao      UUID         NOT NULL UNIQUE,     -- gerado no front; evita duplicidade no reenvio
    decidida_em      TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT ck_decisao_terminal
        CHECK ((canal = 'TERMINAL') = (terminal_id IS NOT NULL))
);

-- ---------------------------------------------------------------------
-- 6. FINANCEIRO
-- ---------------------------------------------------------------------

CREATE SEQUENCE seq_comprovante START 1000;

CREATE TABLE lancamento (
    id                  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    conta_id            BIGINT        NOT NULL REFERENCES conta(id),
    tipo                VARCHAR(8)    NOT NULL CHECK (tipo IN ('DEBITO', 'ESTORNO')),
    valor               NUMERIC(10,2) NOT NULL CHECK (valor > 0),
    proposta_id         BIGINT        REFERENCES proposta(id),     -- só no débito
    estorna_id          BIGINT        REFERENCES lancamento(id),   -- só no estorno
    motivo              VARCHAR(200),
    autor_funcionario   BIGINT        REFERENCES funcionario(id),  -- admin no estorno
    autor_participante  BIGINT        REFERENCES participante(id), -- cliente que confirmou o débito
    numero_comprovante  BIGINT        NOT NULL UNIQUE DEFAULT nextval('seq_comprovante'), -- RF21
    criado_em           TIMESTAMPTZ   NOT NULL DEFAULT now(),

    CONSTRAINT ck_lancamento_debito
        CHECK (tipo <> 'DEBITO'
               OR (proposta_id IS NOT NULL AND estorna_id IS NULL AND autor_participante IS NOT NULL)),
    CONSTRAINT ck_lancamento_estorno
        CHECK (tipo <> 'ESTORNO'
               OR (estorna_id IS NOT NULL AND proposta_id IS NULL
                   AND motivo IS NOT NULL AND autor_funcionario IS NOT NULL))
);

-- RN10 / RNF11: no máximo UM débito por proposta
CREATE UNIQUE INDEX ux_lancamento_debito_proposta
    ON lancamento (proposta_id)
    WHERE tipo = 'DEBITO';

-- RN14: estorno total, uma vez só por débito
CREATE UNIQUE INDEX ux_lancamento_estorno_unico
    ON lancamento (estorna_id)
    WHERE tipo = 'ESTORNO';

CREATE INDEX ix_lancamento_conta ON lancamento (conta_id);

CREATE TABLE pagamento (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    conta_id        BIGINT        NOT NULL REFERENCES conta(id),
    valor           NUMERIC(10,2) NOT NULL CHECK (valor > 0),
    forma           VARCHAR(10)   NOT NULL CHECK (forma IN ('DINHEIRO', 'PIX', 'DEBITO', 'CREDITO')),
    funcionario_id  BIGINT        NOT NULL REFERENCES funcionario(id),
    registrado_em   TIMESTAMPTZ   NOT NULL DEFAULT now()
);

CREATE INDEX ix_pagamento_conta ON pagamento (conta_id);

-- ---------------------------------------------------------------------
-- 7. AUDITORIA (RF28 / RNF12) — nunca gravar senha, PIN ou token aqui
-- ---------------------------------------------------------------------

CREATE TABLE auditoria (
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    ator_tipo    VARCHAR(12)  NOT NULL CHECK (ator_tipo IN ('FUNCIONARIO', 'PARTICIPANTE', 'TERMINAL', 'SISTEMA')),
    ator_id      BIGINT,
    acao         VARCHAR(60)  NOT NULL,     -- ex.: PROPOSTA_ENVIADA, PIN_FALHOU, CREDENCIAL_BLOQUEADA
    entidade     VARCHAR(40),
    entidade_id  BIGINT,
    detalhes     JSONB,
    criado_em    TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX ix_auditoria_entidade ON auditoria (entidade, entidade_id);
CREATE INDEX ix_auditoria_data     ON auditoria (criado_em);

-- ---------------------------------------------------------------------
-- 8. SALDO (RN13): consumos confirmados - estornos - pagamentos
-- ---------------------------------------------------------------------

CREATE VIEW v_saldo_conta AS
SELECT c.id AS conta_id,
       COALESCE(l.debitos, 0)    AS total_debitos,
       COALESCE(l.estornos, 0)   AS total_estornos,
       COALESCE(p.pagamentos, 0) AS total_pagamentos,
       COALESCE(l.debitos, 0) - COALESCE(l.estornos, 0) - COALESCE(p.pagamentos, 0) AS saldo
  FROM conta c
  LEFT JOIN (SELECT conta_id,
                    SUM(valor) FILTER (WHERE tipo = 'DEBITO')  AS debitos,
                    SUM(valor) FILTER (WHERE tipo = 'ESTORNO') AS estornos
               FROM lancamento GROUP BY conta_id) l ON l.conta_id = c.id
  LEFT JOIN (SELECT conta_id, SUM(valor) AS pagamentos
               FROM pagamento GROUP BY conta_id) p ON p.conta_id = c.id;

-- ---------------------------------------------------------------------
-- 9. SEGURANÇA NO SUPABASE
--    RLS ligado sem policies + sem permissão para anon/authenticated:
--    a API REST do Supabase não consegue ler nem escrever nada.
--    O backend conecta como dono das tabelas (postgres), que não é afetado.
-- ---------------------------------------------------------------------

DO $$
DECLARE t TEXT;
BEGIN
    FOR t IN SELECT tablename FROM pg_tables WHERE schemaname = 'hotel' LOOP
        EXECUTE format('ALTER TABLE hotel.%I ENABLE ROW LEVEL SECURITY', t);
    END LOOP;
    IF EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'anon') THEN
        EXECUTE 'REVOKE ALL ON SCHEMA hotel FROM anon, authenticated';
        EXECUTE 'REVOKE ALL ON ALL TABLES IN SCHEMA hotel FROM anon, authenticated';
        EXECUTE 'REVOKE ALL ON ALL SEQUENCES IN SCHEMA hotel FROM anon, authenticated';
    END IF;
END $$;

-- ---------------------------------------------------------------------
-- 10. DADOS INICIAIS PARA A DEMONSTRAÇÃO (sem usuários: a senha precisa
--     ser gerada com BCrypt pelo backend)
-- ---------------------------------------------------------------------

INSERT INTO setor (nome) VALUES ('Bar'), ('Restaurante'), ('Piscina');

INSERT INTO quarto (numero) VALUES ('101'), ('102'), ('203'), ('208'), ('210');

INSERT INTO categoria (nome) VALUES ('Bebidas'), ('Porções'), ('Pratos'), ('Sobremesas');

INSERT INTO produto (categoria_id, nome, descricao, preco) VALUES
  ((SELECT id FROM categoria WHERE nome = 'Bebidas'),    'Cerveja long neck', '355 ml',                  12.00),
  ((SELECT id FROM categoria WHERE nome = 'Bebidas'),    'Refrigerante lata', '350 ml',                   7.00),
  ((SELECT id FROM categoria WHERE nome = 'Bebidas'),    'Água mineral',      '500 ml',                   5.00),
  ((SELECT id FROM categoria WHERE nome = 'Bebidas'),    'Suco natural',      'Laranja ou limão, 400 ml', 11.00),
  ((SELECT id FROM categoria WHERE nome = 'Porções'),    'Batata frita',      'Serve 2 pessoas',          35.00),
  ((SELECT id FROM categoria WHERE nome = 'Porções'),    'Isca de peixe',     'Serve 2 pessoas',          48.00),
  ((SELECT id FROM categoria WHERE nome = 'Pratos'),     'Prato executivo',   'Arroz, feijão, salada e carne', 39.00),
  ((SELECT id FROM categoria WHERE nome = 'Sobremesas'), 'Picolé',            'Sabores variados',          6.00);
