DROP INDEX IF EXISTS "test_keyspace"."test_entity_flag_idx";


DROP MATERIALIZED VIEW IF EXISTS "test_keyspace"."test_entity_by_value";
DROP TABLE IF EXISTS "test_keyspace"."test_entity";
DROP TABLE IF EXISTS "test_keyspace"."test_entity_by_date";
DROP TABLE IF EXISTS "test_keyspace"."test_entity_counter";
DROP TYPE IF EXISTS "test_keyspace"."test_udt";
DROP TYPE IF EXISTS "test_keyspace"."test_extra_udt";
DROP TYPE IF EXISTS "test_keyspace"."test_nested_udt";
DROP KEYSPACE IF EXISTS "test_keyspace";
