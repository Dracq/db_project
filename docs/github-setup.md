# GitHub Branch Protection Setup

Since branch protection rules are not stored as code in Git, this document outlines the mandatory settings required in GitHub to enforce GitFlow for the ReconX project.

## `main` Branch Settings

Navigate to your repository's **Settings** → **Branches** → **Add rule**. Apply this to `main`.

- **Require a pull request before merging**
  - **Require approvals**: `2`
  - **Dismiss stale pull request approvals when new commits are pushed**: `Enabled`
  - **Require review from Code Owners**: `Enabled`
- **Require status checks to pass before merging**
  - **Require branches to be up to date before merging**: `Enabled`
  - **Status checks required**: `build`, `test`, `lint`
- **Require conversation resolution before merging**: `Enabled`
- **Require linear history**: `Enabled`
- **Include administrators**: `Enabled`
- **Allow force pushes**: `Disabled`
- **Allow deletions**: `Disabled`

## `develop` Branch Settings

Apply this rule to `develop` to ensure it acts as a stable integration branch.

- **Require a pull request before merging**
  - **Require approvals**: `1`
- **Require status checks to pass before merging**
  - **Status checks required**: `build`, `test`
- **Include administrators**: `Disabled`
