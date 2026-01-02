-- #########################################################################
-- DDL SCRIPT | App Setup
-- #########################################################################
    /*


    */


-- #########################################################################
--
-- #########################################################################
    CREATE TABLE users
    (
      user_id INTEGER PRIMARY KEY AUTOINCREMENT,

      user_name TEXT NOT NULL,
      CHECK (length(user_name) <= 100) -- restricts variable length
    );

    -- ####################################################
    -- Unique Indexes
    -- ####################################################
        CREATE UNIQUE INDEX no_repeat_user_names
            ON users (user_name);

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
