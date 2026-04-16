<!--- Main Title -->
<h1 align="center">  Nutrition Gym App  </h1>

[//]: <> (#################################################################################.)
<!--- Youtube Gif & Link -->
 
[![Watch the video](https://github.com/user-attachments/assets/f50c207b-09d6-4b52-ba93-446eaa90b0f2)](https://www.youtube.com/watch?v=cNcIUuPFwQ4)
### [Watch this video on YouTube](https://www.youtube.com/watch?v=cNcIUuPFwQ4)



[//]: <> (#################################################################################.)
##
<!--- Project Description -->

<h2 align="left">🏋️ Gym Nutrition Planner </h2>

Gym Nutrition Planner is a Java-based desktop application for creating, managing, and optimising personalised meal plans aligned with fitness goals.

It combines a structured desktop UI with a version-controlled relational database model, allowing users to safely edit draft plans while preserving immutable historical versions for auditability and rollback.

The system is built around a hierarchical nutrition model and leverages SQL-driven analytics to deliver real-time macro insights, while maintaining strong data integrity through constraints, triggers, and schema versioning.

[//]: <> (#################################################################################.)
##
<!--- Features -->

<h2 align="left"> 🚀 Features </h2>

- Create and manage personalised meal plans
- Edit mutable draft plans during active work
- Save immutable historical versions for tracking and rollback
- Hierarchical data model: Plan → Meal → Sub-meal → Ingredient
- Real-time macro analysis via SQL aggregations and views
- Enforced data integrity using constraints, triggers, and indexes
- Schema evolution managed with Flyway migrations
- Clean service-layer architecture for extensibility
- Desktop packaging using jpackage
- Automated CI validation with GitHub Actions
- Repository governance via commitlint and PR checks
- Automated versioning and releases using Release Please


[//]: <> (#################################################################################.)
##
<!---  Project Files -->

<h2 align="left"> Project Files </h2>

<!--- -  [🔗Installation Guide](https://github.com/TyroneKF/Gym_App/edit/ReadMe2/README.md) -->

-  [🔗 DB Shcema READ.MD](https://github.com/TyroneKF/Gym_App/blob/master/src/main/resources/db/README.md)
-  [🔗 CI/CD ReadME.MD](https://github.com/TyroneKF/Gym_App/blob/master/CI_v2.md)

<br>


[//]: <> (#################################################################################.)
##
<!--- Languages  -->

<h2 align="left"> 🧱 Tech Stack </h2>

<h3 align="left">  Languages  </h3>

- Java — core application logic
- SQL — schema design, queries, analytics
- YAML — CI/CD workflows
- JavaScript (Node tooling) — commitlint & release automation
- (Planned) Python / Go — AWS Lambda backend

[//]: <> (#################################################################################.)
##
<!---   -->

<h2 align="left"> Desktop Application </h2>

- Java 21
- Swing + JavaFX
- Maven
- JFreeChart for visualisation
- JUnit 5 for testing

[//]: <> (#################################################################################.)
##
<!--- Database  -->

<h2 align="left"> Database & Persistence </h2>

- SQLite for embedded desktop persistence
- HikariCP for connection management
- Flyway for schema versioning and migrations

[//]: <> (#################################################################################.)
##
<!--- Cloud  -->

<h2 align="left"> Cloud / Backend </h2>

- AWS Lambda
- Amazon API Gateway
- AWS Secrets Manager
- Optional AWS Secrets Manager
- Planned secure serverless proxy for third-party nutrition APIs

[//]: <> (#################################################################################.)
##
<!--- CI/CD  -->

<h2 align="left"> CI/CD & DevOps </h2>

- GitHub Actions
- Commitlint
- Release Please
- Qodana static analysis
- jpackage for native installer generation

[//]: <> (#################################################################################.)
##
<!--- DB Design -->

<h2 align="left"> 🗄️ Database Design </h2>

<br>

The application uses a dual-layer versioning model:

<br>

<h3 align="left"> 📝 Draft Layer </h3>
 
- Fully editable working data
- Used during active plan creation
- Supports insert, update, delete operations

<h3 align="left">  📦 Versioned Layer </h3>
 
- Immutable historical snapshots
- Created on save
- Prevents updates/deletes via triggers
- Enables rollback and auditability

[//]: <> (#################################################################################.)
##
<!--- DB Design -->

<h2 align="left"> 📊 Data Hierarchy </h2>

<br>

<img width="273" height="118" alt="Capture" src="https://github.com/user-attachments/assets/eca58c2f-48c1-4415-9996-bd46565acf6f" />

[//]: <> (#################################################################################.)
##
<!--- DB Design -->

<br>
<h2 align="left"> 🔒 Integrity & Constraints </h2>

- Foreign key constraints
- Composite keys & partial indexes
- SQL views for analytics and UI queries


- Triggers for:
  - Immutability enforcement
  - Timestamp propagation
  - Parent-child consistency

The production schema consists of ~24 tables and ~23 views, supporting a robust relational architecture.

[//]: <> (#################################################################################.)
##
<!--- CI/CD Pipeline -->

<h2 align="left"> ⚙️ CI/CD Pipeline </h2>

✅ Continuous Integration
- Triggered on push & pull requests
- Runs:
  - mvn clean verify

- Ensures:
  - Build stability
  - Test validation
  - Merge protection

[//]: <> (#################################################################################.)
##
<!--- Code Quality & Governance -->

<h2 align="left"> 🛡️ Code Quality & Governance </h2>

- Conventional commits enforced via commitlint
- Protected master branch
- PR-based workflow only
- Qodana static analysis checks
- Linear history enforced

[//]: <> (#################################################################################.)
##
<!--- Creating Packages -->

<h2 align="left"> 📦 Release Automation </h2>

- Managed via Release Please
- Features:
  - Semantic versioning
  - Automated changelogs
  - Version sync with pom.xml
  - GitHub release generation

[//]: <> (#################################################################################.)
##
<!---  Packaging -->

<h2 align="left"> 🖥️ Packaging </h2>

- Built using jpackage
- Produces native desktop installers (e.g. .exe)
- Eliminates need for manual JAR execution

[//]: <> (#################################################################################.)
##
<!---  Packaging -->

<h2 align="left"> 🧩 Architecture Overview </h2>

The system follows a layered design:

<br>

 <img width="390" height="180" alt="Capture" src="https://github.com/user-attachments/assets/9379236d-e24d-4380-ac3d-e726f21fd391" />

[//]: <> (#################################################################################.)
##
<!---  Future Features -->

<h2 align="left"> 🔮 Future Improvements </h2>

- AWS Lambda proxy for secure API consumption
- External nutrition API integration
- Improved analytics & visualisations
- Native installer distribution pipeline
- Enhanced modular service layer

[//]: <> (#################################################################################.)
##
<!---  Summary -->

<h2 align="left"> 📌 Summary </h2>

Gym Nutrition Planner is more than a typical desktop CRUD app — it is a data-driven system combining:

- Version-controlled relational design
- SQL-powered analytics
- Strong data integrity guarantees
- Professional CI/CD and release workflows

  
[//]: <> (################################################################################)
<!--- Bottom Right Icons -->

<br>
<br>
<br>
<hr></hr>

<br>
<a href="https://www.linkedin.com/in/tyrone-friday/">
  <img align="right" alt="LinkedIn" width="21px" src="readme/images/linkedin-big-logo.svg"/>
</a>

<a href="https://github.com/TyroneKF/Tyrone-Friday/blob/main/README.md">
  <img align="right" alt="LinkedIn" width="21px" src="readme/images/homepage.svg" />
</a>

<a href="https://github.com/TyroneKF/Gym_App/tree/master">
  <img align="right" alt="LinkedIn" width="21px" src="readme/images/repo.png" />
</a>
