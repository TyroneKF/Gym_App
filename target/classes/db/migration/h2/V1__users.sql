-- #########################################################################
-- DDL SCRIPT | App Setup
-- #########################################################################
/*


*/

-- ########################################
--
-- ########################################
    CREATE TABLE users
    (
      user_id INT PRIMARY KEY AUTO_INCREMENT,
      user_name VARCHAR(100) NOT NULL,

      UNIQUE KEY no_repeat_user_names(user_name)
    );

-- ########################################
--
-- ########################################
    CREATE TABLE active_user
    (
        singleton_id INT PRIMARY KEY CHECK (singleton_id = 1), -- at max one row at a time

        user_id INT NOT NULL,
            FOREIGN KEY (user_id) REFERENCES users(user_id)
                ON DELETE CASCADE
    );
