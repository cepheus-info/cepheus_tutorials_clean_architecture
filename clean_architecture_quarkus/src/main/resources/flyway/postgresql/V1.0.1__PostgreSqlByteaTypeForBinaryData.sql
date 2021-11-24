-- This script uses PostgreSql database specific column type "BYTEA" for support of binary data
ALTER TABLE "base"."token_entry" ADD COLUMN TOKEN bytea;
