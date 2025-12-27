
-- ################################################################
--
-- ################################################################
CREATE PROCEDURE assert_id_not_null
(
    IN p_id INT,
    IN p_message VARCHAR(255)
)
BEGIN
    IF p_id IS NULL THEN
        SIGNAL SQLSTATE '45000'
            SET MYSQL_ERRNO = 10001,
                MESSAGE_TEXT = p_message;
    END IF;
END;

-- ################################################################
-- Insert ID
-- ################################################################
CREATE PROCEDURE insert_into_seed_registry
(
    IN  seed_key  VARCHAR(100),
    IN  entity_table_name  VARCHAR(100),
    IN  entity_id_value  INT
)
BEGIN

    INSERT INTO seed_registry (seed_key, entity_table_name, entity_id_value)
    VALUES
        (seed_key, entity_table_name , entity_id_value)
    AS new_vals
    ON DUPLICATE KEY UPDATE -- In case of duplicate, ensures fields match correctly to new insert
        entity_id_value = new_vals.entity_id_value;

END;

-- #####################################
--
-- #####################################
CREATE PROCEDURE validate_and_insert_into_seed_registry
(
    IN  seed_key  VARCHAR(100),
    IN  entity_table_name  VARCHAR(100),
    IN  entity_id_value  INT,
    IN p_message VARCHAR(255)
)
BEGIN

    CALL assert_id_not_null(entity_id_value, p_message);

    CALL insert_into_seed_registry(seed_key, entity_table_name, entity_id_value);

END;


-- ################################################################
-- Get ID
-- ################################################################

CREATE FUNCTION get_seed_id_by_key
(
    seed_key VARCHAR(100),
    p_message VARCHAR(255)
)
RETURNS INT
DETERMINISTIC
BEGIN

    DECLARE seed_id int;

    SELECT
        entity_id_value
    INTO seed_id
    FROM seed_registry
    WHERE seed_key = seed_key
    LIMIT 1;

    CALL assert_id_not_null(seed_id, p_message);

    RETURN seed_id;

END;

-- Validate & Insert Into Seed Registry Table





