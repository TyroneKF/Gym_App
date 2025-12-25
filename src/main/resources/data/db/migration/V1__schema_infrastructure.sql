-- #########################################################################
-- DDL SCRIPT	
-- #########################################################################

DROP DATABASE IF EXISTS gymapp00001;

CREATE DATABASE gymapp00001 CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE gymapp00001;

-- ######################################
-- Pure infrastructure
-- ######################################
CREATE TABLE schema_version 
(
    version INT PRIMARY KEY,
    description VARCHAR(200),
    checksum VARCHAR(64),
    applied_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    success BOOLEAN NOT NULL
);