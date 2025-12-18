INSERT INTO plans (plan_id, date_time_of_creation, user_id, vegan, plan_name) VALUES
(1, now(), 1, FALSE, 'Temp_Plan'),
(2, now(), 1, FALSE, 'Dummy Plan 1');


INSERT INTO plan_versions(plan_version_id, plan_id, user_id, date_time_of_last_edited, version_number, is_selected_plan) VALUES
(1, 2, 1, now(), 1, TRUE);