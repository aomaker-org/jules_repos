# aom0627 Project

This project is a full development environment for rapid prototyping,
designed to be workable from an Android phone.

## Project Structure

- `.gitignore`: Specifies intentionally untracked files that Git should ignore.
- `android/`: Contains Android-specific development files.
- `python/`: Contains Python-specific development files.
- `docker/`: Contains Docker-related files for containerization.
- `Jules/`: Contains log files of interactions with the AI assistant (Jules),
  runtime information, and test runs.
- `README.md`: This file, providing an overview of the project.

## Logging

All significant actions, interactions with the AI assistant, runtime outputs,
and test results are logged in Markdown files within the `Jules/` directory.
Log files follow the naming convention `aom0627.NNN.md`, where `NNN` is an
incrementing number. A new log file is started when the current one exceeds
256KB.

## Development Principles

- **Rapid Prototyping:** Focus on quick iteration and additive changes.
- **Mobile Development:** Ensure the environment is conducive to development
  on an Android device.
- **Comprehensive Logging:** Maintain detailed logs for traceability and debugging.
- **Standalone Branch:** This project resides in its own branch and folder,
  independent of the main repository's other branches/folders, though it may
  leverage methods and patterns explored elsewhere in the repo.
