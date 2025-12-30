-- #########################################################################
-- DDL SCRIPT | App Setup
-- #########################################################################

/**
	.) A user could clone someone else’s plan version.
	.) The user doesn’t own the whole plan.”
**/

-- ####################################################
-- Main Document
-- ####################################################
    CREATE TABLE plans
    (
        plan_id INT PRIMARY KEY AUTO_INCREMENT,

        date_time_of_creation DATETIME(6) NOT NULL,

        user_id INT NOT NULL,
            FOREIGN KEY (user_id) REFERENCES users(user_id)
                ON DELETE CASCADE,

        vegan BOOLEAN NOT NULL DEFAULT FALSE,

        plan_name VARCHAR(100) NOT NULL,

        UNIQUE KEY unique_plan_name_by_user(user_id, plan_name),

        INDEX idx_plans_user (user_id)
    );

-- ####################################################
-- Document Versions
-- ####################################################
    CREATE TABLE plan_versions
    (
        plan_version_id INT PRIMARY KEY AUTO_INCREMENT,

        plan_id INT NOT NULL,
            FOREIGN KEY (plan_id) REFERENCES plans(plan_id)
                ON DELETE CASCADE,

        user_id INT NOT NULL,
            FOREIGN KEY (user_id) REFERENCES users(user_id)
                ON DELETE CASCADE,

        version_number INT NOT NULL,
        date_time_last_edited DATETIME(6) NOT NULL,

        UNIQUE KEY no_repeated_vs_numbers_per_plan(plan_id, version_number),

        INDEX idx_plan_versions_plan (plan_id),
        INDEX idx_plan_versions_user (user_id)
    );

-- ####################################################
-- Active Document
-- ####################################################
    CREATE TABLE active_plans
    (
        row_id INT PRIMARY KEY AUTO_INCREMENT,

        plan_version_id INT NOT NULL,
            FOREIGN KEY (plan_version_id) REFERENCES plan_versions(plan_version_id)
                ON DELETE CASCADE,

        user_id INT NOT NULL,
                FOREIGN KEY (user_id) REFERENCES users(user_id)
                    ON DELETE CASCADE,

        UNIQUE KEY only_one_active_plan_per_user(user_id),
        UNIQUE KEY unique_ownership_plan_active(plan_version_id)
    );
