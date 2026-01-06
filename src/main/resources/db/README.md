    !! Notice !!


This directory contains database migrations.

  
  
    Plan Editing & Versioning Model Overview

This application uses a two-layer persistence model:

- Draft layer – mutable, temporary working data used while editing
- Version layer – immutable, historical snapshots created only on explicit save

These layers must never be mixed.

    Draft Layer (Editing / Calculations)

Purpose:

- Supports live editing of plans, meals, sub-meals, and ingredients
- Enables macro / nutrient calculations using SQL queries
- Prevents accidental corruption of historical data

Characteristics

- One active draft per user + plan
- Fully mutable (INSERT, UPDATE, DELETE allowed)
- Automatically created when editing starts
- Automatically deleted on cancel or after commit

Tables
- draft_plans
- raft_meals
- draft_meal_sections
- draft_ingredients

(Draft tables mirror the structure of version tables but contain no history.)

    Version Layer (History / Revert)

Purpose:

- Stores immutable snapshots of a plan
- Enables rollback, audit, and version history
- Represents user-intentional commits only

Characteristics:

- Append-only (INSERT only)
- Never updated or deleted
- Created only on explicit Save / Commit Plan
- Old versions are preserved indefinitely (or pruned by policy)

Tables:
- plans
- plan_versions
- meals_in_plan_versions
- meal_section_versions
- ingredient_versions


    SQL Action Rules

Editing (Meal / Sub-meal / Ingredient)

Action

- “Save meal”
- “Save sub-meal”
- “Update ingredient quantity”

Database behavior

-   UPDATE draft_*

    INSERT INTO draft_*

    DELETE FROM draft_*


Important

- No plan version is created
- No history is modified

    
    Save / Commit Plan

Action

- “Save plan”
- “Commit changes”

Database Behavior

-   INSERT INTO plan_versions

    INSERT INTO meals_in_plan_versions

    INSERT INTO meal_section_versions

    INSERT INTO ingredient_versions

    DELETE FROM draft_*


Result

-   A new immutable plan version is created
-   Draft data is discarded
-   The new version becomes the active plan


     Cancel Editing

Action

- “Cancel”
- Exit without saving

Database behavior

- DELETE FROM draft_*


Result

- All uncommitted changes are discarded
- No version is created
- 


    Revert to Previous Version

Action

-   “Revert to version X”

Database Behavior

-   UPDATE active_plans

    SET plan_version_id = X


Result

-   No data is copied
-   No new version is created
-   History remains unchanged
- 


    Invariants (Must Never Be Violated)
-   Draft tables are mutable
-   Version tables are immutable
-   Draft rows are never referenced by version rows
-   Version rows are never updated or deleted
-   Only one draft exists per user per plan
-   Only explicit user save creates a new plan version


    Design Rationale

This model:

-   Prevents history corruption
-   Supports SQL-based calculations during editing
-   Keeps version history meaningful
-   Avoids temporary version pollution
-   Scales cleanly with future features


    Rules:

- Migrations are append-only
- Never edit or delete applied migrations
- Each Vx__ file represents one database version
- No DROP DATABASE statements are allowed
- Schema, views, and seed data evolve via new migrations


    App Data Logic:

- Meal and sub-meal saves persist working data for calculations and UI refresh via draft tables,
  while saving the plan is a separate SQL-level commit that creates a new immutable version.
