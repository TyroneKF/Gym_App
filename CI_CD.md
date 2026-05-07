# Gym_App – CI/CD Documentation

> Merged from `CI.md` + `CI_v2.md`. Source files can be deleted.

---

# 1. Overview

Gym_App uses a **PR-driven CI/CD architecture** with:

- Protected `master` branch (PR-only)
- Mandatory CI validation (`mvn clean verify`)
- Static analysis (Qodana)
- Conventional Commits enforcement (commitlint)
- Automated semantic versioning + release PRs (Release Please)
- Automated GitHub Releases + installer attachment (Windows / macOS / Linux)

This system ensures:

- No direct commits to `master`
- Deterministic versioning (SemVer)
- Clean audit history
- Reproducible builds
- No manual tagging / no manual version edits
- Enterprise-grade workflow discipline

---

# 2. Branch Protection Strategy

## 2.1 Protected Branch: `master`

`master` is protected via GitHub Rulesets.

Enforced policies:

- Require pull request before merging
- Require status checks to pass
- Require branches to be up to date before merging
- Block force pushes
- Require linear history
- (Optional) Admin bypass disabled

Direct pushes to `master` are prohibited unless admin override is used.

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

| Type       | Purpose                                      | Version impact |
|------------|----------------------------------------------|----------------|
| `feat`     | New feature                                  | Minor bump     |
| `fix`      | Bug fix                                      | Patch bump     |
| `perf`     | Performance improvement                      | Patch bump     |
| `refactor` | Internal restructuring (no behaviour change) | No bump        |
| `chore`    | Maintenance / dependency bumps / tooling     | No bump        |
| `ci`       | CI/CD workflow changes                       | No bump        |
| `docs`     | Documentation changes                        | No bump        |
| `test`     | Test additions / updates                     | No bump        |
| `build`    | Dependency or build system changes           | No bump        |

---

## 3.2 Breaking Changes (Major)

Two valid patterns:

### Option A – Exclamation mark
```
feat!: change plan schema structure
```

### Option B – Footer
```
feat: change plan schema structure

BREAKING CHANGE: draft table renamed
```

Both trigger a **major version bump**.

---

# 4. PR Titles (Required When Using Squash Merge)

When you use **Squash & Merge**, GitHub creates one new commit on `master` using the **PR title** as the squash commit message. Therefore:

> **PR title must be a valid Conventional Commit header.**

### Good PR titles
- `fix: attach installers to GitHub release`
- `ci: trigger release build from release-please workflow`
- `docs: update CI/CD guide`

### Bad PR titles
- `Working branch`
- `Updates`
- `Fixes`

If the PR title is invalid, commitlint will fail after merge because the squash commit lands on `master`.

---

# 5. Workflows Overview

All workflows live in: `.github/workflows/`

## 5.1 CI Validation (`ci.yml`)

Purpose: build + tests on PRs into `master` and on direct pushes to `master`.

Trigger:
- `pull_request` → `master`
- `push` → `master`

Core command: `mvn -B clean verify`

---

## 5.2 Commit Message Lint (`commitlint.yml`)

Purpose: enforce Conventional Commits across all branches and PRs.

Trigger:
- `push` → all branches (`'**'`)
- `pull_request` → `master`

Implementation: `npx commitlint --verbose`

Handles edge cases:
- Initial push (zero-SHA `before`) → falls back to `--last`
- `before` SHA not in local history → falls back to `--last`
- PR event → lints range `base..head`

---

## 5.3 Qodana (`qodana_code_quality.yml`)

Purpose: static analysis / code quality feedback.

Trigger:
- `pull_request` → `master`
- `push` → `master`

Requires secret: `QODANA_TOKEN`

---

## 5.4 Release Please (`release-please.yml`)

Purpose: create a release PR automatically after commits land on `master`.

Trigger: `push` → `master`

Output:
- Opens PR: `chore(master): release X.Y.Z`
- On merge: creates tag `vX.Y.Z` + GitHub Release

Requires:
- Secret: `RELEASE_PLEASE_TOKEN` (not the default `GITHUB_TOKEN` — needed to trigger downstream workflow runs)
- Config files: `release-please-config.json`, `.release-please-manifest.json`

---

## 5.5 Release Build (`release.yml`)

Purpose: build cross-platform installers and attach them to the GitHub Release.

Trigger: `workflow_run` — fires after "Release Please" completes successfully.

Jobs run in parallel: `build-windows`, `build-macos`, `build-linux`

Each job:
1. Checks out the release-please commit (`head_sha`)
2. Fetches tags and detects whether a version tag points at HEAD (skips if none)
3. Sets up Java 21 (Temurin)
4. Builds with Maven
5. Runs `jpackage` to produce the installer
6. Uploads the installer to the GitHub Release via `softprops/action-gh-release@v2`

Artifacts produced:
- `GymApp-<version>-Setup.exe` (Windows)
- `GymApp-<version>.dmg` (macOS)
- `GymApp-<version>.deb` (Linux)

---

# 6. Release Process (End-to-End)

## 6.1 Normal development PRs

1. Create branch from `master`
2. Commit using Conventional Commits
3. Open PR (PR title must follow Conventional Commits)
4. CI runs: validate + Qodana + commitlint
5. Merge PR

## 6.2 Release automation

1. After merges land on `master`, Release Please opens:
   - `chore(master): release X.Y.Z`
2. Merge the release PR
3. Release Please automatically:
   - Creates tag `vX.Y.Z`
   - Creates GitHub Release
4. Release Build triggers (`workflow_run`) and:
   - Builds installers on all three platforms
   - Attaches installers to that release

---

# 7. Versioning Rules

Gym_App follows Semantic Versioning: `MAJOR.MINOR.PATCH`

Development versions carry the suffix: `X.Y.Z-SNAPSHOT`

Example lifecycle:

```
1.0.4-SNAPSHOT   ← active development
     ↓ merge release PR
   1.0.4          ← release tag vX.Y.Z
     ↓ automatic bump
1.0.5-SNAPSHOT   ← next development cycle
```

**Manual editing of `pom.xml` versions is prohibited.**

---

# 8. Developer Workflow

## 8.1 Create Feature Branch

```bash
git checkout -b feature/macro-calculator
```

## 8.2 Commit Properly

```bash
git commit -m "feat: add macro calculator"
```

## 8.3 Fixing Invalid Commit Messages (Recovery Guide)

### When This Is Needed

When a PR fails commitlint due to errors like:

- `✖ header must not be longer than 100 characters`
- `✖ type must be one of [feat, fix, docs, chore, refactor, test, ci]`
- `✖ subject may not be empty`
- `✖ invalid format (e.g. docs : instead of docs:)`

---

### Case 1 — Fix the Most Recent Commit

```bash
git commit --amend
# edit message to: type: short description
git push --force-with-lease
```

---

### Case 2 — Fix an Older Commit (Interactive Rebase)

**Step 1** — start interactive rebase (adjust `N` to include the bad commit):

```bash
git rebase -i HEAD~3
```

**Step 2** — mark the bad commit as `reword`:

```
pick 0561bb27 feat: test automated release pipeline
reword 1c2f2ab2 docs : ci docs update       ← fix this one
pick c9a3c0be ci: automating release (#21)
```

> Commands are case-sensitive. Save and exit (`:wq`).

**Step 3** — correct the commit message when prompted, then save.

**Step 4** — continue the rebase:

```bash
git rebase --continue
```

**Step 5** — force-push (with lease, not bare `--force`):

```bash
git push --force-with-lease
```

If the rebase breaks, abort cleanly and restart:

```bash
git rebase --abort
```

---

### Quick Reference

| Action              | Command                        |
|---------------------|--------------------------------|
| Fix latest commit   | `git commit --amend`           |
| Fix older commit    | `git rebase -i HEAD~N`         |
| Continue rebase     | `git rebase --continue`        |
| Abort rebase        | `git rebase --abort`           |
| Update remote       | `git push --force-with-lease`  |

**Never use `git push --force`. Never rewrite history on `master`.**

---

## 8.4 Architecture Decision: Server-Side Enforcement

Commit message validation is enforced **entirely in GitHub Actions**. We intentionally:

- Do NOT require Husky
- Do NOT require local Node installation
- Do NOT depend on Git hooks
- Enforce validation server-side

This ensures a fresh clone on any OS behaves identically.

---

## 8.5 Push and Open PR

```bash
git push origin feature/macro-calculator
```

Open PR → CI runs → Merge when green.

---

# 9. Security & Permissions

Repository settings:

- Workflow permissions: Read & Write
- Allow GitHub Actions to create PRs: Enabled
- Required checks enforced via Rulesets
- Linear history enforced
- Force pushes blocked on `master`

Required secrets:

| Secret                | Used by            | Purpose                                         |
|-----------------------|--------------------|-------------------------------------------------|
| `RELEASE_PLEASE_TOKEN`| release-please.yml | Create PRs + tags (must trigger workflow chains)|
| `QODANA_TOKEN`        | qodana_code_quality.yml | Authenticate with Qodana cloud            |
| `GITHUB_TOKEN`        | release.yml        | Upload release assets (auto-provided)           |

---

# 10. Troubleshooting Playbook

| Symptom | Cause | Fix |
|---------|-------|-----|
| Commitlint fails after merge | PR title not Conventional | Rename PR title before merging |
| Release exists but no installer files | Release Build didn't attach assets | Check that `action-gh-release` has `tag_name:` and `files:` |
| "GitHub Releases requires a tag" | Running from `workflow_run` without tag context | Add tag resolution step (`git tag --points-at HEAD`) and pass `tag_name:` explicitly |
| `jpackage` fails — `GymApp.jar` not found | Maven output jar name differs | Set `<finalName>GymApp</finalName>` in `pom.xml` or update `--main-jar` |
| Merge blocked — "Expected — Waiting for status" | Ruleset required check names don't match job names | Align ruleset check names with actual workflow job names |
| Release pipeline fails — version mismatch | Tag version doesn't match `pom.xml` | Release Please controls this; do not manually edit `pom.xml` versions |
| SNAPSHOT tag pushed | Tag created during development | Only Release Please creates tags; never tag manually from a SNAPSHOT |

---

# 11. Emergency Release (Admin Override)

Only for critical hotfixes. Should be rare.

```bash
git tag -a v1.0.X -m "Release 1.0.X"
git push origin v1.0.X
```

This triggers the release pipeline directly.

---

# 12. Summary Rules

- PR title must be Conventional (if using Squash merge)
- No direct commits to `master`
- Release Please controls all versioning
- Release Build attaches installers to GitHub Releases
- Do not duplicate Release Please workflows
- Commitlint prevents bad history early
- No manual `pom.xml` version edits
- No manual tagging

---

# 13. Workflow Analysis & Issues Found

Analysis of `.github/workflows/` as of 2026-05-07.

---

## 13.1 Non-Existent Action Versions (High — Will Break Workflows)

Several workflows reference action versions that do not exist:

| Workflow | Action reference | Latest stable |
|----------|-----------------|---------------|
| `ci.yml` | `actions/checkout@v6` | `v4` |
| `ci.yml` | `actions/setup-java@v5` | `v4` |
| `qodana_code_quality.yml` | `actions/checkout@v6` | `v4` |
| `commitlint.yml` | `actions/checkout@v6` | `v4` |
| `commitlint.yml` | `actions/setup-node@v6` | `v4` |
| `release.yml` | `actions/checkout@v6` | `v4` |
| `release.yml` | `actions/setup-java@v5` | `v4` |

**Fix:** Replace all `@v5`/`@v6` references with `@v4` (the current major release for these actions). GitHub Actions will fail at runtime if the version tag doesn't exist.

---

## 13.2 Inconsistent Maven Command in Release Build (Medium)

| Job | Maven command |
|-----|---------------|
| `build-windows` | `mvn -B clean verify` (full tests) |
| `build-macos` | `mvn -B clean package -DskipTests` |
| `build-linux` | `mvn -B clean package -DskipTests` |

Windows runs the full test suite before packaging; macOS and Linux skip tests. A platform-specific bug could ship in the macOS or Linux installer undetected.

**Fix:** Use `mvn -B clean verify` on all three jobs, or add a dedicated pre-build test job and use `mvn -B clean package -DskipTests` only for the packaging step after tests have already passed.

---

## 13.3 Duplicated Tag-Extraction Step Across Three Jobs (Low — Maintainability)

The "Extract tag + numeric version" shell block is copy-pasted identically into `build-windows`, `build-macos`, and `build-linux`. Any change must be applied in three places.

**Fix options:**

- Extract into a reusable composite action (`.github/actions/get-release-tag/action.yml`).
- Add a dedicated `get-tag` job that outputs `has_tag`, `tag`, and `version`, then use `needs: get-tag` in the three build jobs.

---

## 13.4 `cancel-in-progress: true` on Release Build (Medium — Risk of Partial Releases)

```yaml
concurrency:
  group: release
  cancel-in-progress: true
```

If a second Release Please run fires while a release build is in progress, the in-progress build is canceled. This can leave a GitHub Release with only some platform installers attached (e.g., Windows uploaded, macOS/Linux canceled).

**Fix:** Set `cancel-in-progress: false` for the release group, or remove the concurrency block entirely on `release.yml`. It's safe for release builds to queue rather than cancel.

---

## 13.5 No Concurrency Group on Qodana (Low)

`qodana_code_quality.yml` has no `concurrency:` setting. Multiple Qodana scans can pile up if several PRs land quickly. This is low risk but wastes Qodana quota.

**Fix:** Add:

```yaml
concurrency:
  group: qodana-${{ github.ref }}
  cancel-in-progress: true
```

---

## 13.6 CI Runs Only on Windows (Low — Platform Coverage Gap)

`ci.yml` uses `runs-on: windows-latest`. This means platform-specific compilation or runtime issues on macOS/Linux are not caught until a release build. For a JavaFX desktop app, this is a real risk (module path, font rendering, file system separators).

**Fix (optional):** Add a matrix strategy to run CI on all three platforms:

```yaml
strategy:
  matrix:
    os: [windows-latest, ubuntu-latest, macos-latest]
runs-on: ${{ matrix.os }}
```

---

## 13.7 No Version Cross-Check Between Tag and `pom.xml` (Low)

The documentation mentions validating that the tag version matches `pom.xml`, but no workflow step implements this check. A manually created tag with a wrong version number could produce a mislabeled release.

**Fix (optional):** Add a step after checkout in `release.yml`:

```bash
POM_VERSION=$(mvn -q -Dexec.executable=echo -Dexec.args='${project.version}' --non-recursive exec:exec 2>/dev/null)
TAG_VERSION="${{ steps.get_tag.outputs.version }}"
if [ "$POM_VERSION" != "$TAG_VERSION" ]; then
  echo "Version mismatch: pom=$POM_VERSION tag=$TAG_VERSION"
  exit 1
fi
```

---

## 13.8 Summary of Issues by Priority

| # | Issue | Priority | Effort |
|---|-------|----------|--------|
| 13.1 | Non-existent action versions (`@v5`/`@v6`) | **High** | Low |
| 13.2 | Tests skipped on macOS/Linux release builds | **Medium** | Low |
| 13.4 | `cancel-in-progress: true` on release group risks partial releases | **Medium** | Low |
| 13.3 | Tag extraction duplicated across 3 jobs | Low | Medium |
| 13.5 | No concurrency on Qodana | Low | Low |
| 13.6 | CI only runs on Windows | Low | Low |
| 13.7 | No tag vs pom.xml version cross-check | Low | Low |

---

End of Document.