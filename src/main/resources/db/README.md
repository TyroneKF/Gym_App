
> ###       Plan Editing & Versioning Model Overview 

This application uses a two-layer persistence model:

- Draft layer – mutable, temporary working data used while editing
- Version layer – immutable, historical snapshots created only on explicit save

These layers must never be mixed.

> ###### In Other Words

This application uses a draft + versioned snapshot architecture to support safe editing, 
accurate macro calculations, and full plan history without data corruption.

The schema is intentionally split into three conceptual layers:

- Reference / Identity data
- Draft (working copy) data
- Versioned (immutable history) data

> ##### Core Concepts
-   Drafts represent in-progress, editable data used while a user is actively modifying a plan.
-   Versions represent committed, immutable snapshots of a plan that can be reverted to later.
-   Drafts are mutable and temporary.
-   Versions are append-only and permanent.
-   Drafts are for work. Versions are for history. These two concepts are never mixed.



> ###    Draft Layer (Editing / Calculations)


 ***Purpose:***

- Supports live editing of plans, meals, sub-meals, and ingredients
- Enables macro / nutrient calculations using SQL queries
- Prevents accidental corruption of historical data

***Characteristics***

- One active draft per user + plan
- Fully mutable (INSERT, UPDATE, DELETE allowed)
- Automatically created when editing starts
- Automatically deleted on cancel or after commit

***Tables***
- draft_plans
- draft_meals
- draft_meal_sections
- draft_ingredients_in_meals

***(Draft tables mirror the structure of version tables but contain no history.)***

***(If a column exists only to support history or auditing, it does not belong in draft tables.)***

Draft tables mirror version tables structurally but represent mutable working state, not historical versions; 
therefore, drafts reference plans and users directly and enforce single ownership per user per plan.

> ### Tables
sdjsdksdksksksss

> #### Table Roles
***Users and Active State***
-   users (Application users).

- active_user (Tracks which user/session is currently active).

***Plans (Logical Ownership)***

***plans***
-   The logical plan entity (name, user ownership, metadata).
-   A plan does not change over time.
-   All edits occur via versions.

> #### Versioned Plan History (Single-Ownership Snapshots)

***These tables implement single-parent (owned) versioning.
Each version belongs to exactly one parent plan version and is never reused.***

-   plan_versions (Immutable snapshots of a plan).
-   meals_in_plan_versions
-   divided_meal_sections_versions
-   ingredients_in_sections_of_meal_versions
-   macros_per_pound_and_limits_versions

***Key properties:***

-  Versions are created only on explicit “Save Plan” actions
-  Old versions are never updated or deleted
-  Reverting a plan means switching which plan_version is active

> #### Draft / Working Copy Tables (Mutable)

These tables store editable working data while a user is modifying a plan.

-  meals_in_plan
-  divided_meal_sections
-  ingredients_in_sections_of_meal
-  macros_per_pound_and_limits

***Key properties:***

- Fully mutable (INSERT, UPDATE, DELETE allowed)
- There is only one active draft per user per plan

***Used For:***
- live macro calculations
- UI refreshes
- incremental saves of meals and sub-meals

***Draft data is never treated as history.***

> ### . Versioning Model Used

***This schema uses:***

> ###### Single-Ownership (Owned Snapshot) Versioning

- Each version row belongs to exactly one parent version
- No snapshot reuse across versions
- All version trees are cloned on commit
- Predictable, simple, and safe

***This model was chosen because:***

- Plans are small to medium in size
- Edits are human-driven
- Storage is inexpensive
- Correctness and clarity matter more than deduplication



> ###     Version Layer (History / Revert)

***Purpose:***

- Stores immutable snapshots of a plan
- Enables rollback, audit, and version history
- Represents user-intentional commits only

***Characteristics:***

- Append-only (INSERT only)
- Never updated or deleted
- Created only on explicit Save / Commit Plan
- Old versions are preserved indefinitely (or pruned by policy)

***Tables:***
- plans
- plan_versions
- meals_in_plan_versions
- meal_section_versions
- ingredient_versions


> ###     SQL Action Rules

Editing (Meal / Sub-meal / Ingredient)

***Action***

- “Save meal”
- “Save sub-meal”
- “Update ingredient quantity”

***Database Behavior***

-   UPDATE draft_*

    INSERT INTO draft_*

    DELETE FROM draft_*


***Important***

- No plan version is created
- No history is modified


> ###     Save / Commit Plan

***Action***

- “Save plan”
- “Commit changes”

***Database Behavior***

-   INSERT INTO plan_versions

    INSERT INTO meals_in_plan_versions

    INSERT INTO meal_section_versions

    INSERT INTO ingredient_versions

    DELETE FROM draft_*


***Result***

-   A new immutable plan version is created
-   Draft data is discarded
-   The new version becomes the active plan


> ###     Cancel Editing

***Action***

- “Cancel”
- Exit without saving

***Database Behavior***

- DELETE FROM draft_*


***Result***

- All uncommitted changes are discarded
- No version is created
- 


> ###     Revert to Previous Version

***Action***

-   “Revert to version X”

***Database Behavior***

-   UPDATE active_plans

    SET plan_version_id = X


***Result***

-   No data is copied
-   No new version is created
-   History remains unchanged
- 

> ### Save, Delete, and Commit Semantics
<br>

> ###### ***For Meal / Sub-Meal Save***


***Triggered by application actions such as:***

- Saving an ingredient
- Saving a sub-meal
- Saving a meal

***SQL Behavior:***
- Writes only to draft tables
- Updates macro calculations
- No version is created
- Existing history is untouched

> ######  For Save Plan (Commit)

Triggered only by an explicit user action.

***SQL Behavior:***

-   Read current draft tables
-   Create a new plan_versions row
-   Clone draft data into: *_versions tables
-   Mark the new plan version as active
-   Delete draft rows

***This is the only operation that creates history.***

> ###### For Cancel / Discard Changes

-   Draft rows are deleted
-   No version is created
-   Last committed version remains active

> ######  For Delete Plan

-   Deletes the logical plan
-   Cascades to all plan versions
-   Drafts (if any) are also removed


> ###    Invariants (Must Never Be Violated)
-   Draft tables are mutable
-   Version tables are immutable
-   Draft rows are never referenced by version rows
-   Version rows are never updated or deleted
-   Only one draft exists per user per plan
-   Only explicit user save creates a new plan version

> ###### In Other Words

-   The following rules must always hold:
-   Draft tables may be updated; version tables may not.
-   Version tables are append-only.
-   Meal / sub-meal saves never create plan versions.
-   Only “Save Plan” creates a plan_version.
-   Draft data is disposable and never referenced by history.

***If these rules are respected, the system remains consistent and safe.***


> ###     Design Rationale

***This model:***

-   Prevents history corruption
-   Supports SQL-based calculations during editing
-   Keeps version history meaningful
-   Avoids temporary version pollution
-   Scales cleanly with future features


> ###     Rules:

- Migrations are append-only
- Never edit or delete applied migrations
- Each Vx__ file represents one database version
- No DROP DATABASE statements are allowed
- Schema, views, and seed data evolve via new migrations


> ###     App Data Logic:

Meal and sub-meal saves persist working data for calculations and UI refresh via draft tables,
  while saving the plan is a separate SQL-level commit that creates a new immutable version.


> #    !! Notice !!


**This directory contains database migrations.**