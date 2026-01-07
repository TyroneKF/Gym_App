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

