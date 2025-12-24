DELIMITER $$

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
END$$

DELIMITER ;