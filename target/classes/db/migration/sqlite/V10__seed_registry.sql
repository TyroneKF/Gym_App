CREATE TABLE seed_registry
(
    seed_key           VARCHAR(100) PRIMARY KEY,
    entity_table_name  VARCHAR(100) NOT NULL,
    entity_id_value    INT NOT NULL,

    UNIQUE unique_id_per_table(entity_table_name, entity_id_value )
);
