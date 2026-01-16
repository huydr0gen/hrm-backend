DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.tables
        WHERE table_name = 'approval_configs'
          AND table_schema = 'public'
    ) THEN

        -- =====================================================
        -- Step 1: Deactivate duplicate approval configs
        -- Keep the latest record (by created_at)
        -- =====================================================

        WITH ranked AS (
            SELECT
                id,
                target_type,
                target_id,
                created_at,
                ROW_NUMBER() OVER (
                    PARTITION BY target_type, target_id
                    ORDER BY created_at DESC
                ) AS rn
            FROM approval_configs
            WHERE active = true
        )
        UPDATE approval_configs
        SET active = false
        WHERE id IN (
            SELECT id
            FROM ranked
            WHERE rn > 1
        );

        -- =====================================================
        -- Step 2: Add unique constraint if not exists
        -- =====================================================

        IF NOT EXISTS (
            SELECT 1
            FROM pg_constraint
            WHERE conname = 'uq_approval_configs_target'
        ) THEN
            ALTER TABLE approval_configs
            ADD CONSTRAINT uq_approval_configs_target
            UNIQUE (target_type, target_id, active);
        END IF;

        -- =====================================================
        -- Step 3: Add index for faster lookup
        -- =====================================================

        IF NOT EXISTS (
            SELECT 1
            FROM pg_indexes
            WHERE indexname = 'idx_approval_configs_target_lookup'
        ) THEN
            CREATE INDEX idx_approval_configs_target_lookup
            ON approval_configs (target_type, target_id, active);
        END IF;

    END IF;
END $$;
