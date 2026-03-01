# Gym_App – CI / CD Documentation

---

# 1. Overview

Gym_App uses a **PR-driven CI/CD architecture** with:

- Protected `master` branch
- Mandatory CI validation
- Conventional Commits
- Automated semantic versioning (Release Please)
- Automated tagging
- Automated multi-platform installer builds
- Automatic GitHub Releases
- Automatic SNAPSHOT version bump

This system ensures:

- No direct commits to production
- Deterministic versioning
- Clean audit history
- Reproducible builds
- No manual tagging errors
- Enterprise-grade workflow discipline

---

# 2. Branch Protection Strategy

## Protected Branch: `master`

The `master` branch is protected via GitHub Rulesets.

Enforced policies:

- Require pull request before merging
- Require status checks to pass
- Require branches to be up to date before merging
- Require conversation resolution before merging
- Block force pushes
- Require linear history
- Optional approval requirement (enabled when multi-account review is active)

Direct pushes to `master` are prohibited unless admin override is used.

---

# 3. Commit Convention (Conventional Commits)

All commits merged into `master` must follow:

---

## 3.1 Supported Commit Types

### feat:
Purpose: New feature  
Version impact: Minor bump

### fix:
Purpose: Bug fix  
Version impact: Patch bump

### perf:
Purpose: Performance improvement  
Version impact: Patch bump

### refactor:
Purpose: Internal restructuring (no behavior change)  
Version impact: No bump

### test:
Purpose: Add or update tests  
Version impact: No bump

### chore:
Purpose: Maintenance / tooling  
Version impact: No bump

### ci:
Purpose: CI/CD workflow changes  
Version impact: No bump

### build:
Purpose: Dependency or build system changes  
Version impact: No bump

### docs:
Purpose: Documentation updates  
Version impact: No bump

---

## 3.2 Examples
- feat: add macro calculator
- fix: correct macro rounding logic
- ci: configure release-please workflow
- refactor: extract plan version service
- docs: update CI documentation


---

## 3.3 Breaking Changes

### Option 1 – Exclamation
feat!: change plan schema structure


### Option 2 – Footer
feat: change plan schema structure

BREAKING CHANGE: draft table renamed


Breaking changes trigger a **major version bump**.

---

# 4. CI Validation Workflow

## Trigger

on:
pull_request:
branches: [ master ]


---

## Validations Performed

- Maven compilation
- Unit tests
- Integration tests
- `mvn clean verify`
- Static analysis (Qodana)
- Code quality checks

---

## Required Status Checks

The following checks must pass before merging:

- `CI Validation / validate`
- `Qodana / qodana`

If status checks are misaligned with workflow job names, merging will be blocked.

---

# 5. Release Process (Automated – Release Please)

Releases are fully automated.

Manual tagging is not permitted.

---

## 5.1 Step 1 – Merge Feature PRs

Feature branches are merged into `master`.

Example:

- feat: add macro calculator
- fix: correct rounding issue


---

## 5.2 Step 2 – Release PR Creation

Release Please analyzes commits since last tag.

It creates a PR:

chore(master): release X.Y.Z


This PR:

- Removes `-SNAPSHOT` from `pom.xml`
- Bumps version
- Generates changelog
- Prepares release notes

---

## 5.3 Step 3 – Merge Release PR

Merging the release PR automatically:

- Creates tag `vX.Y.Z`
- Triggers release build pipeline
- Creates GitHub Release
- Uploads platform installers
- Bumps version to next `X.Y.(Z+1)-SNAPSHOT`

---

# 6. Release Build Pipeline

## Trigger

on:
push:
tags:
- 'v*..'


---

## Pipeline Jobs

### Windows

- Validate tag on master
- Validate version match
- Ensure non-SNAPSHOT
- Run `mvn clean verify`
- Build `.exe` via `jpackage`
- Create GitHub Release
- Upload artifact

### macOS

- Build `.dmg`
- Upload artifact

### Linux

- Build `.deb`
- Upload artifact

---

## Artifacts

- GymApp-Windows.exe
- GymApp-macOS.dmg
- GymApp-Linux.deb

Release notes auto-generated from commit history.

---

# 7. Versioning Rules

Gym_App follows Semantic Versioning:

MAJOR.MINOR.PATCH

MAJOR.MINOR.PATCH

Development versions use:

X.Y.Z-SNAPSHOT



---

## Example Lifecycle

1.0.4-SNAPSHOT ← Development

Release PR → 1.0.4

Tag → v1.0.4

Next development → 1.0.5-SNAPSHOT


Manual editing of `pom.xml` versions is prohibited.

---

# 8. Developer Workflow

---

## 8.1 Create Feature Branch

git checkout -b feature/macro-calculator


---

## 8.2 Commit Properly

git commit -m "feat: add macro calculator"

---

## 🔒 Commit Message Enforcement (Portable Setup)

### 🎯 Objective

Ensure all commits follow the **Conventional Commits** standard without requiring any local developer setup.

This prevents:

- Broken semantic versioning
- Release Please failures
- History rewrites and rebases
- Invalid PR merges into `master`

---

## 🏗 Architecture Decision

Commit message validation is enforced **entirely in GitHub Actions (CI)**.

We intentionally:

- ❌ Do NOT require Husky
- ❌ Do NOT require local Node installation
- ❌ Do NOT depend on Git hooks
- ✅ Enforce validation server-side
- ✅ Keep the repository fully portable

This ensures:

> A fresh clone on any OS behaves identically.

---

## ⚙️ GitHub Action: Commitlint

File: `.github/workflows/commitlint.yml`

```yaml
name: Commit Message Lint

on:
  push:
    branches:
      - '**'
  pull_request:
    branches:
      - master

jobs:
  commitlint:
    runs-on: ubuntu-latest

    steps:
      - uses: actions/checkout@v4
        with:
          fetch-depth: 0

      - uses: actions/setup-node@v4
        with:
          node-version: 20

      - run: npm install --no-save @commitlint/cli @commitlint/config-conventional

      - run: |
          npx commitlint \
            --from ${{ github.event.before }} \
            --to ${{ github.sha }} \
            --verbose






---

## 8.3 Fixing Commit Messages (After Push)

If a commit message is incorrect:

git commit --amend

git push --force-with-lease

Never use `--force` alone.

Never rewrite history on `master`.

###  🔧 Fixing Invalid Commit Messages (Commitlint Recovery Guide)

When This Is Needed

If a pull request fails due to commitlint errors such as:

- ✖ header must not be longer than 100 characters
- ✖ type must be one of [feat, fix, docs, chore, refactor, test, ci]
- ✖ subject may not be empty
- ✖ invalid format (e.g. docs : instead of docs:)


#### ✅ Case 1 — Fix the Most Recent Commit
If the invalid commit is the latest one:

- git commit --amend

Update the message to follow Conventional Commits:

 Correct format:

-  type: short description

Example:
- docs: update CI documentation

Then push:
- git push --force-with-lease

✅ Case 2 — Fix an Older Commit (Interactive Rebase)

If the invalid commit is not the latest one:

Step 1 — Start Interactive Rebase

If the commit is within the last 3 commits:

- git rebase -i HEAD~3

Step 2 — Mark the Commit for Reword

You will see something like:

- pick 0561bb27 feat: test automated release pipeline

- pick 1c2f2ab2 docs : ci docs update

- pick c9a3c0be ci: automating release with release-please (#21)

Change only the incorrect commit:

- pick 0561bb27 feat: test automated release pipeline

- reword 1c2f2ab2 docs : ci docs update

- pick c9a3c0be ci: automating release with release-please (#21)

⚠ Commands are case-sensitive (must be lowercase).

Save and exit (:wq).

Step 3 — Edit the Commit Message

When prompted, correct the message:

❌ Incorrect:
- docs : ci docs update

✅ Correct:
- docs: ci docs update

Save and exit.

Step 4 — Continue Rebase

If Git pauses:
- git rebase --continue

Repeat until rebase completes.

Step 5 — Force Push

Since history was rewritten:

- git push --force-with-lease

This safely updates the remote branch.

🚨 If Rebase Breaks (Invalid PICK / Syntax Errors)

If you see:
- error: invalid line 1: PICK ...

Abort cleanly:
- git rebase --abort

Then restart the rebase process.

🛑 Important Rules
- Never use git push --force
- Always use git push --force-with-lease
- Rebase commands must be lowercase
- Do not edit unrelated commits
- Do not rewrite history on master

🔎 How to Verify Fix
After finishing:
- git log --oneline -n 3

Ensure the commit now follows:
- type: description

🎯 Why We Enforce This

Commitlint ensures:

- Automated semantic versioning works correctly
- Release Please detects version bumps
- Changelogs are generated automatically
- CI/CD remains deterministic

Invalid commit messages can break automated releases.

🧠 Recommended Editor Configuration (Optional)

To avoid Vim during rebase:
- git config --global core.editor "code --wait"

Or IntelliJ:
- git config --global core.editor "idea --wait"

🔥 Summary

- Fix latest commit	= git commit --amend
- Fix older commit	= git rebase -i HEAD~N
- Continue rebase = git rebase --continue
- Abort rebase    = git rebase --abort
- Update remote	= git push --force-with-lease

This keeps your CI pipeline stable and production-grade.



---

## 8.4 Push Branch

git push origin feature/macro-calculator


Open PR → CI runs → Merge when green.

---

# 9. Status Check Alignment

If merge is blocked due to:

Expected — Waiting for status to be reported


Ensure:

- Ruleset required checks match actual workflow job names
- Workflow permissions include:
    - contents: write
    - pull-requests: write

---

# 10. Emergency Release (Admin Override)

Only for critical hotfixes.

git tag -a v1.0.X -m "Release 1.0.X"

git push origin v1.0.X


Triggers release pipeline.

Should be rare.

---

# 11. Security & Permissions

Repository Settings:

- Workflow permissions: Read & Write
- Allow GitHub Actions to create PRs: Enabled
- Required checks enforced
- Linear history enforced
- Force pushes blocked on `master`

---

# 12. Common Failure Scenarios

### Commit message invalid
Release not triggered.

### Required check mismatch
Merge blocked.

### Version mismatch between tag and pom.xml
Release pipeline fails.

### SNAPSHOT tag pushed
Release rejected.

---

# 13. Architectural Rationale

This CI/CD architecture provides:

- Deterministic version control
- Automated semantic releases
- Full audit traceability
- Zero manual tag risk
- Cross-platform reproducibility
- Enterprise-level development discipline

This mirrors production SaaS pipelines.

---

# 14. System Summary

Release = Merge Release PR  
Build = Automatic  
Tagging = Automatic  
Version bump = Automatic  
Installers = Automatic

No manual `pom.xml` edits.  
No manual tagging.  
No direct `master` commits.

---

End of Document.

