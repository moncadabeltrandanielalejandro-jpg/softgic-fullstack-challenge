IF EXISTS (
    SELECT 1 FROM sys.columns c
    JOIN sys.tables t ON t.object_id = c.object_id
    WHERE t.name = 'processed_events' AND c.name = 'processed_at' AND TYPE_NAME(c.user_type_id) = 'datetime2'
)
    ALTER TABLE processed_events ALTER COLUMN processed_at DATETIMEOFFSET(6) NOT NULL;