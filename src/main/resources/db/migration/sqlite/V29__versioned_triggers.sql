-- ###########################################################################
-- Plans Trigger | Prevents CERTAIN Rows from being updated
-- ###########################################################################
    CREATE TRIGGER trg_plans_allow_only_safe_updates
    BEFORE UPDATE OF date_time_of_creation, user_id, vegan
    ON plans
    FOR EACH ROW
        BEGIN
                SELECT RAISE(ABORT, 'UPDATE Trigger - plans only mutable columns can change!');
        END;

    -- Immutable Columns (Not Allowed)
        -- date_time_of_creation
        -- user_id
        -- vegan

    -- Mutable Columns (Implicitly Allowed):
        -- plan_name

-- ###########################################################################
-- Plans_versions | Prevents table from being updated
-- ###########################################################################
    CREATE TRIGGER trg_draft_plans_versions_prevent_any_update
    BEFORE UPDATE ON plan_versions
    FOR EACH ROW
    BEGIN
      SELECT RAISE(ABORT, 'UPDATE Trigger - plans_versions table is immutable');
    END;

-- ###########################################################################
-- macros_per_pound_and_limits Trigger | Prevents table from being updated
-- ###########################################################################
    CREATE TRIGGER trg_macros_per_pound_and_limits_prevent_any_update
    BEFORE UPDATE ON macros_per_pound_and_limits
    FOR EACH ROW
    BEGIN
            SELECT RAISE(ABORT, 'UPDATE Trigger - macros_per_pound_and_limits table is immutable');
    END;


-- ###########################################################################
-- meals_in_plan  Trigger | Prevents table from being updated
-- ###########################################################################
    CREATE TRIGGER trg_meals_in_plan_prevent_any_update
    BEFORE UPDATE ON meals_in_plan
    FOR EACH ROW
        BEGIN
                SELECT RAISE(ABORT, 'UPDATE Trigger -  meals_in_plan table is immutable');
        END;

-- ###########################################################################
-- meals_in_plan_versions Trigger | Prevent Table From Being Updated
-- ###########################################################################
    CREATE TRIGGER trg_meals_in_plan_versions_prevent_any_update
    BEFORE UPDATE ON meals_in_plan_versions
    FOR EACH ROW
        BEGIN
            SELECT RAISE(ABORT, 'UPDATE Trigger -  meals_in_plan_versions table is immutable');
        END;

-- ###########################################################################
-- divided_meal_sections Trigger | Prevents table from being updated
-- ###########################################################################
    CREATE TRIGGER trg_divided_meal_sections_prevent_any_update
    BEFORE UPDATE ON divided_meal_sections
    FOR EACH ROW
        BEGIN
            SELECT RAISE(ABORT, 'UPDATE Trigger -  divided_meal_sections table is immutable');
        END;

-- ###########################################################################
-- divided_meal_sections_versions Trigger | Allows only certain rows to be updated
-- ###########################################################################
    CREATE TRIGGER trg_divided_meal_sections_versions_prevent_any_update
    BEFORE UPDATE ON divided_meal_sections_versions
    FOR EACH ROW
        BEGIN
            SELECT RAISE(ABORT, 'UPDATE Trigger -  divided_meal_sections_versions table is immutable');
        END;

-- ###########################################################################
-- ingredients_in_sections_of_meal Trigger | Prevents table from being updated
-- ###########################################################################
    CREATE TRIGGER trg_ingredients_in_sections_of_meal_prevent_any_update
    BEFORE UPDATE ON ingredients_in_sections_of_meal
    FOR EACH ROW
        BEGIN
            SELECT RAISE(ABORT, 'UPDATE Trigger -  ingredients_in_sections_of_meal table is immutable');
        END;





