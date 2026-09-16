IF EXISTS (
    SELECT 1 FROM sys.columns c
    JOIN sys.tables t ON t.object_id = c.object_id
    WHERE t.name = 'solicitudes' AND c.name = 'creado_en' AND TYPE_NAME(c.user_type_id) = 'datetime2'
)
    ALTER TABLE solicitudes ALTER COLUMN creado_en DATETIMEOFFSET(6) NOT NULL;

IF EXISTS (
    SELECT 1 FROM sys.columns c
    JOIN sys.tables t ON t.object_id = c.object_id
    WHERE t.name = 'solicitudes' AND c.name = 'actualizado_en' AND TYPE_NAME(c.user_type_id) = 'datetime2'
)
    ALTER TABLE solicitudes ALTER COLUMN actualizado_en DATETIMEOFFSET(6) NOT NULL;

IF EXISTS (
    SELECT 1 FROM sys.columns c
    JOIN sys.tables t ON t.object_id = c.object_id
    WHERE t.name = 'historial_estados' AND c.name = 'ocurrido_en' AND TYPE_NAME(c.user_type_id) = 'datetime2'
)
    ALTER TABLE historial_estados ALTER COLUMN ocurrido_en DATETIMEOFFSET(6) NOT NULL;

IF EXISTS (
    SELECT 1 FROM sys.columns c
    JOIN sys.tables t ON t.object_id = c.object_id
    WHERE t.name = 'observaciones' AND c.name = 'creado_en' AND TYPE_NAME(c.user_type_id) = 'datetime2'
)
    ALTER TABLE observaciones ALTER COLUMN creado_en DATETIMEOFFSET(6) NOT NULL;

IF EXISTS (
    SELECT 1 FROM sys.columns c
    JOIN sys.tables t ON t.object_id = c.object_id
    WHERE t.name = 'outbox_events' AND c.name = 'occurred_at' AND TYPE_NAME(c.user_type_id) = 'datetime2'
)
    ALTER TABLE outbox_events ALTER COLUMN occurred_at DATETIMEOFFSET(6) NOT NULL;

IF EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'ix_outbox_pending' AND object_id = OBJECT_ID('outbox_events'))
    DROP INDEX ix_outbox_pending ON outbox_events;

IF EXISTS (
    SELECT 1 FROM sys.columns c
    JOIN sys.tables t ON t.object_id = c.object_id
    WHERE t.name = 'outbox_events' AND c.name = 'published_at' AND TYPE_NAME(c.user_type_id) = 'datetime2'
)
    ALTER TABLE outbox_events ALTER COLUMN published_at DATETIMEOFFSET(6) NULL;

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'ix_outbox_pending' AND object_id = OBJECT_ID('outbox_events'))
    CREATE INDEX ix_outbox_pending ON outbox_events(published_at);