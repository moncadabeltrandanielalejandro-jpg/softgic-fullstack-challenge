CREATE TABLE categorias (
    id UNIQUEIDENTIFIER NOT NULL PRIMARY KEY,
    nombre NVARCHAR(120) NOT NULL,
    activa BIT NOT NULL DEFAULT 1
);

CREATE TABLE solicitudes (
    id UNIQUEIDENTIFIER NOT NULL PRIMARY KEY,
    codigo NVARCHAR(40) NOT NULL UNIQUE,
    asunto NVARCHAR(200) NOT NULL,
    descripcion NVARCHAR(2000) NOT NULL,
    categoria_id UNIQUEIDENTIFIER NOT NULL REFERENCES categorias(id),
    prioridad NVARCHAR(10) NOT NULL,
    estado NVARCHAR(20) NOT NULL,
    solicitante_id UNIQUEIDENTIFIER NOT NULL,
    analista_id UNIQUEIDENTIFIER NULL,
    creado_en DATETIME2 NOT NULL,
    actualizado_en DATETIME2 NOT NULL,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX ix_solicitudes_estado ON solicitudes(estado);
CREATE INDEX ix_solicitudes_categoria ON solicitudes(categoria_id);
CREATE INDEX ix_solicitudes_solicitante ON solicitudes(solicitante_id);

CREATE TABLE historial_estados (
    id UNIQUEIDENTIFIER NOT NULL PRIMARY KEY,
    solicitud_id UNIQUEIDENTIFIER NOT NULL REFERENCES solicitudes(id),
    estado_origen NVARCHAR(20) NULL,
    estado_destino NVARCHAR(20) NOT NULL,
    actor_id UNIQUEIDENTIFIER NOT NULL,
    rol_actor NVARCHAR(20) NOT NULL,
    motivo NVARCHAR(500) NULL,
    ocurrido_en DATETIME2 NOT NULL
);

CREATE INDEX ix_historial_solicitud ON historial_estados(solicitud_id);

CREATE TABLE observaciones (
    id UNIQUEIDENTIFIER NOT NULL PRIMARY KEY,
    solicitud_id UNIQUEIDENTIFIER NOT NULL REFERENCES solicitudes(id),
    autor_id UNIQUEIDENTIFIER NOT NULL,
    texto NVARCHAR(2000) NOT NULL,
    creado_en DATETIME2 NOT NULL
);

CREATE INDEX ix_observaciones_solicitud ON observaciones(solicitud_id);

-- Outbox transaccional (ver ADR-0002): garantiza publicación confiable de eventos.
CREATE TABLE outbox_events (
    event_id UNIQUEIDENTIFIER NOT NULL PRIMARY KEY,
    aggregate_id NVARCHAR(64) NOT NULL,
    type NVARCHAR(60) NOT NULL,
    version INT NOT NULL,
    correlation_id NVARCHAR(64) NOT NULL,
    payload_json NVARCHAR(4000) NOT NULL,
    occurred_at DATETIME2 NOT NULL,
    published_at DATETIME2 NULL
);

CREATE INDEX ix_outbox_pending ON outbox_events(published_at);

-- Semilla de categorías no sensibles.
INSERT INTO categorias (id, nombre, activa) VALUES
    (NEWID(), 'Soporte técnico', 1),
    (NEWID(), 'Coordinación logística', 1),
    (NEWID(), 'Solicitud administrativa', 1),
    (NEWID(), 'Infraestructura', 1);
