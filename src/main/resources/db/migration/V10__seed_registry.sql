CREATE TABLE seed_registry
(
    seed_key     VARCHAR(64) PRIMARY KEY,
    entity_type  VARCHAR(32) NOT NULL,
    entity_id    INT NOT NULL
);
