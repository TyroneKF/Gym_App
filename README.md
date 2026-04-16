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

Gym Nutrition Planner is a Java-based desktop application for creating, managing and optimising personalised meal plans aligned with fitness goals. It combines a structured desktop UI with a version-controlled relational database model, allowing users to safely edit draft plans while preserving immutable historical versions for auditability and rollback.

The application is built around a **hierarchical nutrition data model** being; **Plan → Meal → Sub-meal → Ingredient** with SQL-driven macro calculations and analytics powering real-time nutritional insights. Its database architecture is designed to prioritise **data integrity, version control and maintainability** using: relational constraints triggers, views and migration based schema management.

From an engineering perspective, the project goes beyond a typical CRUD desktop app as it includes a **professionally structured CI/CD pipeline**, automated validation workflows, semantic versioning, release automation and release packaging for desktop distribution. The wider architecture also supports a clean separation between the desktop client and backend integrations, including a planned **AWS Lambda/API Gateway proxy layer** for securely consuming third-party nutrition APIs without exposing credentials in the client.

[//]: <> (#################################################################################.)
##
<!--- Features -->

<h2 align="left"> 🚀 Features / Capabilities </h2>

- Create and manage personalised meal plans
- Edit **mutable draft plans** during active work
- Save **immutable historical versions** for tracking and rollback
- Hierarchical data model: Plan → Meal → Sub-meal → Ingredient
- **Real-time macro analysis** via SQL aggregations and views
- Enforced data integrity using constraints, triggers, and indexes
- Schema evolution managed with **Flyway migrations**
- Clean service-layer architecture for extensibility
- Package the desktop application for distribution using **jpackage**
- Automated CI validation with GitHub Actions
- Validate builds automatically through **GitHub Actions CI**
- Enforced repository standards through **commitlint, PR checks, and release workflows**
- Automated versioning and releases using **Release Please**


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

- **Java** — primary desktop application language
- **SQL** — relational schema, views, triggers, and analytics
- **YAML** — CI/CD workflow configuration
- **JavaScript /Node.js tooling** — commitlint & release automation
- **Planned backend support**: Python or Go for AWS Lambda integration

[//]: <> (#################################################################################.)
##
<!---   -->

<h2 align="left"> Desktop Application </h2>

- **Java 21**
- **Swing + JavaFX**
- **Maven**
- **JFreeChart** for visualisation
- **JUnit** 5 for testing

[//]: <> (#################################################################################.)
##
<!--- Database  -->

<h2 align="left"> Database / Persistence </h2>

- **SQLite**  for embedded desktop persistence
- **HikariCP**  for connection management
- **Flyway**  for schema versioning and migrations

[//]: <> (#################################################################################.)
##
<!--- Cloud  -->

<h2 align="left"> Cloud / Backend </h2>

<br>

Everything below in this section is planned for future developement: 

<br>

- **AWS Lambda**
- **Amazon API Gateway**
- AWS Secrets Manager
- Optional **AWS Secrets Manager**
- Planned secure serverless proxy for third-party nutrition APIs

[//]: <> (#################################################################################.)
##
<!--- CI/CD  -->

<h2 align="left"> CI/CD & DevOps </h2>

- **GitHub Actions** - Automates CI/CD pipelines to build, test, and package the Gym App, ensuring consistent and reliable releases.
  
- **Commitlint** - Enforces structured commit message conventions to maintain clean versioning and enable automated release workflows.
  
- **Release Please** - Automatically manages semantic versioning, changelog generation, and GitHub releases based on commit history.
  
- **Qodana Static Analysis** - Performs automated code quality and security analysis to detect bugs, code smells, and enforce maintainability standards before merging changes.

  
- **jpackage** for native installer generation

[//]: <> (#################################################################################.)
##
<!--- DB Design -->

<h2 align="left"> 🗄️ Database Design </h2>


The database is one of the core strengths of the project. It is designed around a dual-layer versioning model:

<br>
<h3 align="left"> 1.)  📝 Draft Layer </h3>

This layer stores the user’s current working data and is fully editable.
 
- Draft plans can be created and modified freely
- Draft meals, sub-meals, and ingredients can be added, changed, or removed
- Used for active meal planning and iteration

<br>
<h3 align="left"> 2.) 📦 Versioned Layer </h3>

This layer stores immutable historical snapshots.

- Saving a plan creates a versioned snapshot
- Immutable historical snapshots
- Historical records cannot be edited or deleted directly
- Prevents updates/deletes via triggers
- Ensures auditability, reproducibility, and rollback safety

[//]: <> (#################################################################################.)
##
<!--- DB Design -->

<h2 align="left"> 📊 Data Hierarchy </h2>

<br>

The schema follows this domain structure:

<img width="273" height="118" alt="Capture" src="https://github.com/user-attachments/assets/eca58c2f-48c1-4415-9996-bd46565acf6f" />

This structure supports both usability in the UI and accurate macro rollups in SQL.

Macro rollups refer to the aggregation of nutritional values from ingredients up through sub-meals, meals, and entire plans using SQL. This allows accurate, real-time calculation of totals without duplicating data, ensuring consistency and scalability.

[//]: <> (#################################################################################.)
##
<!--- DB Integrity -->

<h2 align="left"> 🔒 Integrity & Constraints </h2>

The schema uses several mechanisms to enforce correctness:
- **Foreign key constraints**
- **Composite keys & partial indexes**
- **SQL views for nutritional analytics and UI queries**

<br>

Triggers for:
  - **Immutability enforcement**
  - **Timestamp propagation and parent updates**
  - **Parent-child consistency**

<br>

The production schema consists of ~24 tables and ~23 views, supporting a robust relational architecture reflecting a more serious relational architecture than a simple local desktop database.

[//]: <> (#################################################################################.)
##
<!--- CI/CD Pipeline -->

<h2 align="left"> ⚙️ CI/CD Pipeline </h2>

The project includes a structured CI/CD setup to improve reliability, consistency, and release quality.

<h4 align="left"> Continuous Integration </h4>

GitHub Actions validates the project on pushes and pull requests by:

- Enforcing branch protection requirements
- Preventing invalid changes from reaching master
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

Additional automation supports repository quality:

- **Commitlint** enforces Conventional Commit formatting
- **Protected branch rules** enforce PR-based changes
- **Qodana** provides static analysis and quality gates
- Linear history and up-to-date branch requirements improve repo hygiene

[//]: <> (#################################################################################.)
##
<!--- Release Automation -->

<h2 align="left"> 📦 Release Automation </h2>

The release pipeline uses **Release Please** for semantic versioning and automated release management.
- Version bumps are driven by conventional commits
- Release metadata is generated automatically
- Release tags stay aligned with project versions
- Native desktop packages are built for release distribution
  
Managed via Release Please
- Features:
  - Semantic versioning
  - Automated changelogs
  - Version sync with pom.xml
  - GitHub release generation

[//]: <> (#################################################################################.)
##
<!---  Packaging -->

<h2 align="left"> 🖥️ Packaging </h2>

The desktop application is packaged using **jpackage**, allowing the project to move toward professional installer-based distribution rather than manual jar execution.

- Built using jpackage
- Produces native desktop installers (e.g. .exe)
- Eliminates need for manual JAR execution

[//]: <> (#################################################################################.)
##
<!---  Archetecture Overview -->

<h2 align="left"> 🧩 Architecture Overview </h2>

The project is designed with maintainability in mind:

- Desktop UI layer for user interaction
- Service layer abstraction for business logic and external integrations
- Database layer for version-controlled persistence and analytics
- Planned serverless integration layer for secure API access

<br>

This separation helps the application scale from a local desktop planner into a more production-oriented system with cleaner boundaries between client, persistence, and cloud services.

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
