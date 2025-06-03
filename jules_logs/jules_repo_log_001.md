# Jules Repository Log - Entry 001

## Timestamp (Pacific Time)

[YYYY-MM-DD HH:MM:SS PT - Placeholder]

## Initial Raw Input Prompt

<issue>
This is a new repo which will collect miscellaneous other "Jules" repos as "subprojects," or folders inside the "repo root". Subprojects will be listed in the repo root readme.md, and each one will have a readme.md in the root of the subproject folder.

The top level will contain a folder for ./jules_templates, which will contain various .md files to help initialize Jules "sessions" or document sub repo folders and branches.

Initial settings:

Create ./jules_logs in each "top" level folder -- the main repo "root" and each "sub project root folder / tree". Log files and most other files will be "additive" and backed by "git". The idea is to allow fast prototyping (low friction) with a kind of "idempotent" file system, basically "backed" by github.

Including this prompt, always capture the "raw input prompt" and preserve it in a "log file". Inside a given logfile, sections may be defined, and "occasionally" a worker may be requested to get an "environment" timestamp (at least once per logfile_nnn.md), which will be in Pacific (standard or daylight savings) time zone.

Log files are actually a series of .md log entries; for simplicity, entries will always be appended, and when the log reaches a certain size (approximately 256KB), then the next log will be created. The names will be like <log_basename>_nnn.md where nnn starts at 001 and increments. If nnn gets to 999, then a new <log_basename> will be defined in the last log entry in the for the "current" <log_basename>.

The  "repo root" will contain "global" files and each project subroot (which may have tree structures) will contain files that apply to the subproject tree structure - perhaps inheriting some "defaults" from the "repo root", but without getting excessively complicated. :)

Let's start with this idea to start the plan.
</issue>
