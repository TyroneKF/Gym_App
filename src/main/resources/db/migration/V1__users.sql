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
  
  -- Forces App to only have 1 user active at a time
  is_user_selected BOOLEAN NOT NULL DEFAULT FALSE,
  selected_user_flag BOOLEAN GENERATED ALWAYS AS (IF(is_user_selected, TRUE, NULL)) STORED,
  
  UNIQUE KEY no_multiple_active_user(selected_user_flag),
  UNIQUE KEY no_repeat_user_names(user_name)
);

-- ########################################
--
-- ########################################
CREATE TABLE active_user
(
  id INT PRIMARY KEY CHECK (id = 1),

  user_id INT NULL,
  		FOREIGN KEY (user_id) REFERENCES users(user_id)
  			ON DELETE RESTRICT -- Prevents the whole row from being deleted when an active user is deleted
  			                   -- This must be set to null first, then the user can be deleted
);