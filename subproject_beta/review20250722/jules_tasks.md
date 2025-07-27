# Jules Tasks Operating on All Branches and Folders

This document lists the Jules tasks that are known to operate on all branches and folders in this repository.

## Logging

**Description:**

The initial prompt in `jules_logs/jules_repo_log_001.md` states that `jules_logs` directories should be created in each "top level folder". This implies a logging task that applies to the entire repository. All actions taken by Jules should be logged in the appropriate log file.

**Relevant Files:**

- `jules_logs/`
- `subproject_alpha/jules_logs/`
- `subproject_beta/jules_logs/`

## Templating

**Description:**

The `jules_templates` directory contains templates for subproject READMEs, branch documentation, and session initialization. This suggests a templating task that can be used across the entire repository to ensure consistency.

**Relevant Files:**

- `jules_templates/`

## Subproject Creation

**Description:**

The repository is designed to collect "Jules" repos as "subprojects". This is a recurring task that can be performed in any branch to add new subprojects to the repository.

**Relevant Files:**

- `subproject_alpha/`
- `subproject_beta/`
