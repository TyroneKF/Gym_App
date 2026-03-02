# Gym_App – CI / CD Documentation

---

# 1. Overview

Gym_App uses a **PR-driven CI/CD architecture** with:

- Protected `master` branch (PR-only)
- Mandatory CI validation (`mvn clean verify`)
- Static analysis (Qodana)
- Conventional Commits enforcement (commitlint)
- Automated semantic versioning + release PRs (Release Please)
- Automated GitHub Releases + installer attachment (Windows/macOS/Linux)

This system ensures:

- No direct commits to `master`
- Deterministic versioning (SemVer)
- Clean audit history
- Reproducible builds
- No manual tagging / no manual version edits

---

# 2. Branch Protection Strategy

## 2.1 Protected Branch: `master`

`master` is protected via GitHub Rulesets.

Enforced policies (typical):

- Require pull request before merging
- Require status checks to pass
- Require branches to be up to date before merging
- Block force pushes (recommended)
- Require linear history (recommended)
- (Optional) Admin bypass disabled (recommended)

---

# 3. Conventional Commits (Required)

All commits merged into `master` must follow:

```
type(scope optional): subject
```

Examples:

- `feat: add macro calculator`
- `fix: correct jpackage jar name`
- `ci: stabilise release build trigger`
- `docs: update CI/CD documentation`

---

## 3.1 Supported Commit Types

### feat:
Purpose: New feature  
Version impact: **Minor** bump

### fix:
Purpose: Bug fix  
Version impact: **Patch** bump

### perf:
Purpose: Performance improvement  
Version impact: **Patch** bump

### refactor:
Purpose: Internal restructuring (no behaviour change)  
Version impact: **No bump**

### chore:
Purpose: Maintenance / dependency bumps / tooling  
Version impact: **No bump**

### ci:
Purpose: CI/CD workflow changes  
Version impact: **No bump**

### docs:
Purpose: Documentation changes  
Version impact: **No bump**

### test:
Purpose: Test additions/updates  
Version impact: **No bump**

---

## 3.2 Breaking Changes (Major)

Two valid patterns:

### Option A
`feat!: change plan schema structure`

### Option B
```
feat: change plan schema structure

BREAKING CHANGE: draft table renamed
```

Version impact: **Major** bump

---

# 4. PR Titles (Required if using Squash Merge)

## 4.1 Why PR titles matter now

When you use **Squash & Merge**, GitHub creates **one new commit** on `master` using the **PR title** (as the squash commit message).  
Therefore:

> **PR title must be a valid Conventional Commit header.**

### ✅ Good PR titles
- `fix: attach installers to GitHub release`
- `ci: trigger release build from release-please workflow`
- `docs: update CI/CD guide`

### ❌ Bad PR titles
- `Working branch`
- `Updates`
- `Fixes`

If the PR title is invalid, commitlint will fail after merge (because the squash commit lands on `master`).

---

# 5. Workflows Overview

Workflows live in: `.github/workflows/`

## 5.1 CI Validation (`ci.yml`)

Purpose:
- Build + tests on PRs into `master`
- Build + tests on pushes to `master`

Trigger:
- `pull_request` → `master`
- `push` → `master`

Core command:
- `mvn -B clean verify`

---

## 5.2 Commit Message Lint (`commitlint.yml`)

Purpose:
- Enforce Conventional Commits consistently
- Prevent “bad commits” reaching `master`
- Avoid release automation confusion

Trigger:
- `push` → all branches (`'**'`)
- `pull_request` → all PRs (recommended) or only into `master`

Implementation:
- `npx commitlint --verbose --last`

Note:
- `--last` keeps the job stable and avoids “invalid revision range” problems on PR checkouts.

---

## 5.3 Qodana (`qodana_code_quality.yml`)

Purpose:
- Static analysis / code quality feedback

Trigger:
- `pull_request` → `master`
- `push` → `master`

Token:
- Requires `QODANA_TOKEN` secret

---

## 5.4 Release Please (`release-please.yml`)

Purpose:
- Create a **Release PR** automatically after commits land on `master`
- Manage versions and changelog/release notes

Trigger:
- `push` → `master`

Output:
- Opens PR like: `chore(master): release X.Y.Z`
- On merge: creates tag `vX.Y.Z` + GitHub Release

Recommended:
- Keep **one** Release Please workflow only (avoid duplicates like `release-pr.yml`).

---

## 5.5 Release Build (`release.yml`)

Purpose:
- Build cross-platform installers (Windows `.exe`, macOS `.dmg`, Linux `.deb`)
- Attach installers to the GitHub Release for the version tag

### Trigger (Updated Architecture)
Release Build is triggered by:

- `workflow_run` (after “Release Please” completes)

Why:
- Tags created inside workflows do not reliably trigger separate “tag push” workflows in the way manual tags do.
- `workflow_run` provides deterministic chaining: Release Please → Release Build.

---

# 6. Release Process (End-to-End)

## 6.1 Normal development PRs
1. Create branch
2. Commit using Conventional Commits
3. Open PR into `master` (PR title must be Conventional)
4. CI runs (validate + qodana + commitlint)
5. Merge PR

## 6.2 Release automation
1. After merges to `master`, Release Please opens:
   - `chore(master): release X.Y.Z`
2. You merge the release PR
3. Release Please creates:
   - tag `vX.Y.Z`
   - GitHub Release page
4. Release Build workflow triggers (workflow_run) and:
   - builds installers
   - attaches installers to that release

---

# 7. Release Build: Key Technical Requirements

## 7.1 Checkout the correct commit
Because the workflow is triggered via `workflow_run`, checkout must pin to the release-please run commit:

- `ref: ${{ github.event.workflow_run.head_sha }}`

## 7.2 GitHub Release attachment needs an explicit tag
Because `workflow_run` is not a tag event, `softprops/action-gh-release` needs `tag_name`.

Solution:
- Fetch tags
- Resolve the latest tag at that commit:
  - `git describe --tags --abbrev=0`
- Pass it into `action-gh-release` using:
  - `tag_name: ${{ steps.get_tag.outputs.tag }}`

## 7.3 Installers must be uploaded as release assets
`generate_release_notes: true` alone does **not** attach files.

You must include:

- `files: dist/*.exe` (Windows)
- `files: dist/*.dmg` (macOS)
- `files: dist/*.deb` (Linux)

---

# 8. “Why did only commitlint run?”

This is expected when pushing to a feature branch:

- `commitlint.yml` runs on **all branch pushes**
- `ci.yml` and `qodana_code_quality.yml` run only on:
  - PRs into `master`
  - pushes to `master`

So:
- Feature branch push → commitlint runs
- PR into master → commitlint + CI + Qodana run

---

# 9. Troubleshooting Playbook

## 9.1 Commitlint fails on merge (type-empty / subject-empty)
Cause:
- Squash merge commit message (PR title) not Conventional.

Fix:
- Rename PR title to valid Conventional format before merging.

## 9.2 Release exists but only source zip/tar.gz (no installers)
Cause:
- Release Build did not attach assets, or did not run.

Fix checklist:
- Confirm Release Build workflow ran (Actions tab)
- Ensure `action-gh-release` has:
  - `tag_name: ...`
  - `files: ...`

## 9.3 “GitHub Releases requires a tag”
Cause:
- Running `action-gh-release` from `workflow_run` without tag context.

Fix:
- Add tag resolution step (`git describe --tags --abbrev=0`)
- Pass `tag_name:` explicitly

## 9.4 jpackage fails because `GymApp.jar` not found
Cause:
- Maven output jar name differs from `GymApp.jar`.

Fix options:
- Set `<finalName>GymApp</finalName>` in `pom.xml`, OR
- Update `--main-jar` to match the actual jar name.

---

# 10. Summary Rules (Print This)

- **PR title must be Conventional** (if using Squash merge)
- **No direct commits to master**
- **Release Please controls versioning**
- **Release Build attaches installers to GitHub Releases**
- **Do not duplicate Release Please workflows**
- **Commitlint prevents “bad history” early**
