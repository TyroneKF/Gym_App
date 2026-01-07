-- #########################################################################
--
-- #########################################################################
    CREATE TABLE active_user
    (
        singleton_id INT PRIMARY KEY CHECK (singleton_id = 1), -- at max one row at a time

        user_id INT NOT NULL,
            FOREIGN KEY (user_id) REFERENCES users(user_id)
                ON DELETE CASCADE
    );
