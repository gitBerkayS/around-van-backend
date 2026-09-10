-- Supabase PostgREST exposes public by default. This app only uses JDBC as postgres.
-- Block the publishable/anon key from reading application tables (including users).
-- No-ops on a regular Postgres that does not have Supabase roles.

DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'anon') THEN
        EXECUTE 'REVOKE ALL ON SCHEMA public FROM anon';
        EXECUTE 'REVOKE ALL ON ALL TABLES IN SCHEMA public FROM anon';
        EXECUTE 'REVOKE ALL ON ALL SEQUENCES IN SCHEMA public FROM anon';
        EXECUTE 'REVOKE ALL ON ALL FUNCTIONS IN SCHEMA public FROM anon';
        EXECUTE 'ALTER DEFAULT PRIVILEGES IN SCHEMA public REVOKE ALL ON TABLES FROM anon';
        EXECUTE 'ALTER DEFAULT PRIVILEGES IN SCHEMA public REVOKE ALL ON SEQUENCES FROM anon';
        EXECUTE 'ALTER DEFAULT PRIVILEGES IN SCHEMA public REVOKE ALL ON FUNCTIONS FROM anon';
    END IF;

    IF EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'authenticated') THEN
        EXECUTE 'REVOKE ALL ON SCHEMA public FROM authenticated';
        EXECUTE 'REVOKE ALL ON ALL TABLES IN SCHEMA public FROM authenticated';
        EXECUTE 'REVOKE ALL ON ALL SEQUENCES IN SCHEMA public FROM authenticated';
        EXECUTE 'REVOKE ALL ON ALL FUNCTIONS IN SCHEMA public FROM authenticated';
        EXECUTE 'ALTER DEFAULT PRIVILEGES IN SCHEMA public REVOKE ALL ON TABLES FROM authenticated';
        EXECUTE 'ALTER DEFAULT PRIVILEGES IN SCHEMA public REVOKE ALL ON SEQUENCES FROM authenticated';
        EXECUTE 'ALTER DEFAULT PRIVILEGES IN SCHEMA public REVOKE ALL ON FUNCTIONS FROM authenticated';
    END IF;
END $$;

DO $$
DECLARE
    r record;
BEGIN
    FOR r IN
        SELECT tablename
        FROM pg_tables
        WHERE schemaname = 'public'
          AND tablename NOT IN (
              'flyway_schema_history',
              'spatial_ref_sys'
          )
    LOOP
        EXECUTE format('ALTER TABLE public.%I ENABLE ROW LEVEL SECURITY', r.tablename);
    END LOOP;
END $$;
