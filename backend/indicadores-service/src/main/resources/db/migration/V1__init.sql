CREATE TABLE processed_events (
    event_id UNIQUEIDENTIFIER NOT NULL PRIMARY KEY,
    processed_at DATETIME2 NOT NULL
);

CREATE TABLE fact_transiciones (
    id UNIQUEIDENTIFIER NOT NULL PRIMARY KEY,
    solicitud_id UNIQUEIDENTIFIER NOT NULL,
    categoria_id UNIQUEIDENTIFIER NULL,
    estado NVARCHAR(20) NOT NULL,
    fecha DATE NOT NULL
);

CREATE INDEX ix_fact_estado ON fact_transiciones(estado);
CREATE INDEX ix_fact_categoria ON fact_transiciones(categoria_id);
CREATE INDEX ix_fact_fecha ON fact_transiciones(fecha);
