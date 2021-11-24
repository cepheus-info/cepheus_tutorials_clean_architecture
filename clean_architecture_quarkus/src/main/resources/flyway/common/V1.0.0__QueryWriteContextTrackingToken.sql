CREATE SCHEMA IF NOT EXISTS "base.token_entry";

CREATE TABLE "base"."token_entry" (
	processor_name VARCHAR(255) NOT NULL,
	segment INTEGER NOT NULL,
	-- token BLOB NULL, -> needs to be added afterwards inside the database specific setups
	token_type VARCHAR(255) NULL,
	timestamp VARCHAR(255) NULL,
	owner VARCHAR(255) NULL,
	PRIMARY KEY (processor_name, segment)
);
