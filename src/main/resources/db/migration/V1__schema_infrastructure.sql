-- #########################################################################
-- DDL SCRIPT	
-- #########################################################################

CREATE TABLE schema_version
(
    version INT PRIMARY KEY,
    description VARCHAR(200),
    checksum VARCHAR(64),
    applied_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    success BOOLEAN NOT NULL
);