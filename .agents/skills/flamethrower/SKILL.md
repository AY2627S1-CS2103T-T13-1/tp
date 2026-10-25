---
name: flamethrower
description: Review CS2103T team-project PRs and prepare practical-exam testing, bug reports, or responses using course standards and grading rules. Use for CS2103T review work, not general Java development.
---

# Flamethrower

Find defensible issues and explain their consequences. Be thorough in investigation and concise in reporting; do not invent findings to fill a quota.

For installation, prerequisites, and invocation examples, see [the setup guide](README.md).

## Establish scope

Identify the requested mode, repository or released artifact, revision, and relevant UG/DG. Use the user's specified scope; ask only for missing inputs that prevent useful work. Review requests authorize investigation and local drafts, not code changes or posting reviews/issues.

Read only the relevant resources:

* [PE rules](references/pe.md) for exam testing, reports, and responses.
* [Standards](references/standards.md) for Java, Git, Markdown, or documentation review.
* [Review issue catalogue](references/review-issues.md) for code/PR issue patterns, severity ratings, and fixes.
* [Course review lenses and notes index](references/course-notes.md) for design, tests, debugging, and UML. Use the bundled synthesis directly; no external notes folder is required.

The references summarize all eight supplied sources for offline or quick use. When online, recheck administrative rules for an actual PE or grading decision, especially phases, limits, runtime, and submission instructions. Offline, continue with the summaries; flag decisions requiring uncached details or current rules. Use current course requirements over conflicting notes; project conventions fill gaps without silently overriding course requirements. Do not invent rules or mark deductions.

## Temporary workspace

Keep all rough work and temporary files inside `<base>/tmp/`, where `<base>` is the session's starting project/workspace directory. The shorthand `/tmp` in this skill means that base-relative folder, not the system-root or OS temporary directory. Resolve this location before changing working directories. Use it for PR and PE code-review downloads, temporary clones or source archives, diffs, scratch scripts, logs, and disposable test data.

Before ending the session, retain requested deliverables in their intended destination, then clear all temporary files and subdirectories from `<base>/tmp/`, leaving the folder itself in place. Before recursive cleanup, verify the resolved absolute targets remain inside that exact folder.

## PR review

Establish the actual base/head and inspect the merge-base-to-head diff, surrounding code, callers, tests, and relevant documentation. Keep existing defects and local uncommitted work distinct from changes introduced by the PR.

Optional context helper (Python 3.9+ and Git; paths may contain spaces):

```text
python scripts/review_context.py "/path/to/repository" --base origin/main --head HEAD
```

Run from the skill folder, or use the script's absolute path. Use `python3` on macOS or `py -3` on Windows when appropriate. Supply the actual PR refs; the helper reads local Git data and does not fetch, switch branches, or run tests. Its status describes the current checkout, which may differ from the requested head.

Trace changed behavior through parsing, commands, model, storage, and UI where relevant. Check applicable standards manually as well as using the project's existing checks. Inspect the build/CI configuration before choosing commands. In Codex, skip Gradle execution because of the known sandbox permission issue; do not request permission or escalation to run Gradle. Continue with source inspection and available CI evidence, and report Gradle checks as unrun. In other harnesses, such as Claude Code and OpenCode, Gradle may be run: use `gradlew.bat` on Windows and `./gradlew` on macOS/Linux. Run focused tests at the reviewed revision when the harness permits them; report commands, results, and anything untested. Apply this harness rule to PE code reviews and other Gradle tasks in this skill as well. Passing Checkstyle or coverage alone does not establish correctness.

Return an impact-ordered list or table of issues and violations. For each entry, include the exact file/line, applicable standard or contract, evidence/trigger, consequence, justified severity, and a concrete fix. Use the [catalogue's rating policy](references/review-issues.md#rating-policy): PE labels for eligible bugs, and explicitly advisory PR ratings for code-only concerns. Separate demonstrated defects, verified standard violations, and optional suggestions; keep plausible but unverified candidates separate with the check needed to confirm them. Tie design criticism to an actual maintenance or correctness cost. Use PR priorities only if requested or required by the review format; do not equate them with PE severity. If there are no actionable findings, say so and state material verification gaps.

## PE investigation and output

Test the supplied release against its UG/DG and the target user's workflows. Source inspection suggests tests; behavior in a rebuilt checkout does not prove a defect in the supplied release. Label unexecuted candidates as hypotheses. Do not claim GUI behavior was observed when only source or logs were inspected.

Reproduce candidates from a stated starting state, reduce the steps, and separate independent defects. Keep disposable test data inside `<base>/tmp/`. For every proposed report, provide:

* A title naming the observable failure.
* Artifact/version, OS/runtime, initial data, and exact steps/inputs.
* Expected behavior with its basis, actual behavior, and captured evidence.
* Impact on the intended user or reader; one justified type and severity.

Draft reports or phase-specific responses in the format the user requests. Distinguish evidence from inference, and explain any uncertain scope or classification. Finish with what was tested and the remaining coverage gaps.
