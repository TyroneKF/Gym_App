> ###                CI / CD Documentation – Gym_App




> ###### Overview

This repository uses a PR-driven release workflow with automated semantic versioning, tagging, and multi-platform installer builds.

The system consists of:

- CI Validation Workflow (runs on PRs)

- Release PR Automation (Release Please)

- Tag-triggered Release Build Pipeline

- Protected master branch with required checks

This setup ensures:

- No direct commits to master (PR-only)

- Automatic version management

- Automatic tag creation

- Automatic Windows/macOS/Linux installers

- Automatic GitHub Releases

- Automatic next snapshot version bump

> ### Branch Strategy

> ###### Protected Branch

master is protected with:

✅ Require pull request before merging

✅ Require status checks to pass

✅ Require branches to be up to date before merging

✅ Block force pushes

✅ Require linear history

✅ Repository admin bypass (optional emergency override)

No direct pushes allowed unless admin bypass is used.

> ###  Commit Convention (Required)

This project follows Conventional Commits.

All commits merged into master should follow:

 Supported Types:

> ###### feat:
- Purpose : New Feature
- Version Impact: Minor bump

> ###### fix:
- Purpose : Bug fix
- Version Impact: Patch bump

> ###### refactor:
- Purpose : Internal restructuring
- Version Impact:  No bump

> ###### chore:
- Purpose : Maintenance / tooling / CI updates
- Version Impact: No bump

> ###### test:
- Purpose : Test additions or updates
- Version Impact: No bump


> ####  Examples

feat: add macro calculator

fix: correct macro rounding logic

chore: update GitHub workflow permissions

refactor: extract plan version service


> #####   Breaking Changes
feat!: change plan schema structure

> #####   or

feat: change plan schema structure

BREAKING CHANGE: draft table renamed

This triggers a major version bump.


> ### CI Validation Workflow

Triggered on:

> #### pull_request

> #### Validates:

- Maven build

- Tests (mvn clean verify)

- Static analysis (Qodana)

- Code quality checks

> #### Required status checks:

- validate

- qodana

PR cannot merge unless these pass.

> ### Release Process (Automated – Pattern 1)

Releases are PR-driven using release-please.

There is NO manual tagging.

> #### Step 1 – Merge Feature PRs

Example:

- feat: add macro calculator
- fix: correct macro rounding

Merged into master.

> ####  Step 2 – Release PR Opens Automatically

Release Please analyzes commits since last tag.

It opens a PR:

- chore: release 1.0.X

This PR:

- Removes -SNAPSHOT from pom.xml

- Updates version

- Generates release notes

You review and merge this PR.

> #### Step 3 – Tag Is Created Automatically

After merging release PR:

- Tag vX.Y.Z is created automatically

- release.yml pipeline is triggered

> ###  Release Build Pipeline

Triggered on:

push:

tags:

-- 'v*.*.*'

> #### Jobs:

> #####  1️⃣ Windows Build

- Verifies tag is on master

- Ensures version is not SNAPSHOT

- Ensures tag matches pom.xml version

- Runs mvn clean verify

- Builds .exe installer via jpackage

- Creates GitHub Release

- Uploads Windows artifact

> ##### 2️⃣ macOS Build

- Builds .dmg

- Uploads artifact

> ##### 3️⃣ Linux Build

- Builds .deb

- Uploads artifact

> ##### Artifacts:

- GymApp-Windows

- GymApp-macOS

- GymApp-Linux

GitHub Release includes generated release notes.

> ### Versioning Rules



> ###### Project follows Semantic Versioning:

MAJOR.MINOR.PATCH

> ###### Between releases:

X.Y.Z-SNAPSHOT

> ###### Example lifecycle:

- 1.0.4-SNAPSHOT   ← development
- Release PR → 1.0.4
- Tag created → v1.0.4
- Next PR → 1.0.5-SNAPSHOT

You never manually edit pom.xml for versioning anymore.

> ### Developer Workflow

> ###### Create Feature Branch
git checkout -b feature/macro-calculator

> ###### Commit Properly
git commit -m "feat: add macro calculator"

> ###### Push & Create PR
- git push origin feature/macro-calculator

Open PR → CI runs → Merge into master.

Release process continues automatically.

> ### Emergency Release (Admin Override)

If required:

- Admin may push directly to master (bypass role).

- Tag manually:

git tag -a v1.0.X -m "Release 1.0.X"

git push origin v1.0.X

This triggers release pipeline.

Use only for critical hotfixes.

> ### Security & Permissions

Repository settings:

- Workflow permissions: Read and Write

- Allow GitHub Actions to create PRs: Enabled

- Required checks enforced

- Linear history enforced

- Force pushes blocked

> ### Why This Architecture

- This setup provides:

- Deterministic versioning

- Audit-friendly history

- No manual tag mistakes

- No accidental SNAPSHOT releases

- Multi-platform build reproducibility

- Professional CI/CD discipline

This mirrors production-grade SaaS and enterprise Java pipelines.

> ### Summary

- Release = Merge Release PR
- Build = Automatic
- Tagging = Automatic
- Version bump = Automatic
- Installers = Automatic



- No manual pom edits.
- No manual tagging.
- No direct master commits.