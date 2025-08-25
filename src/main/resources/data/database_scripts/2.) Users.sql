CREATE TABLE IF NOT EXISTS users
(
  user_id INT PRIMARY KEY AUTO_INCREMENT,
  user_name VARCHAR(100) NOT NULL,
  
  is_user_selected BOOLEAN NOT NULL DEFAULT FALSE,
  selected_user_flag BOOLEAN GENERATED ALWAYS AS (IF(is_user_selected, TRUE, NULL)) STORED,
  
  UNIQUE KEY no_repeat_user_names(user_name)
);

INSERT IGNORE INTO users (user_id, user_name, is_user_selected) VALUES
(NULL, "@USERNAME@", TRUE);