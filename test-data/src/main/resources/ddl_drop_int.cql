DROP INDEX IF EXISTS "test_keyspace"."test_entity_flag_idx";


DROP TABLE IF EXISTS "test_keyspace"."test_entity";
DROP TABLE IF EXISTS "test_keyspace"."test_entity_by_date";
DROP TYPE IF EXISTS "test_keyspace"."test_udt";
DROP TYPE IF EXISTS "test_keyspace"."test_extra_udt";
DROP TYPE IF EXISTS "test_keyspace"."test_nested_udt";
DROP KEYSPACE IF EXISTS "test_keyspace";
