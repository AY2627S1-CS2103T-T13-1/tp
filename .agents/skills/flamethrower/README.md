# Flamethrower

A Codex skill for CS2103T PR reviews and practical exam testing, bug reports, and responses.

## Install locally

Download or clone this skill folder. Install it as a directory named `flamethrower` in one of these locations:

| Scope | Skill directory |
| --- | --- |
| Personal: available across repositories | `~/.agents/skills/flamethrower/` |
| Repository: available within a project | `.agents/skills/flamethrower/` under the target repository root |

`~` means your home directory. Keep `SKILL.md`, `README.md`, `agents/`, `references/`, and `scripts/` together inside `flamethrower/`; copying only `SKILL.md` leaves its supporting references unavailable. Exclude `.git/` when copying a checkout.

For a personal installation in PowerShell, run this from the downloaded skill folder:

```powershell
$skillSource = (Get-Location).Path
$skillDestination = Join-Path $HOME '.agents/skills/flamethrower'
if (Test-Path -LiteralPath $skillDestination) {
    throw 'Flamethrower already exists. Update the existing installation instead.'
}
New-Item -ItemType Directory -Path $skillDestination -Force | Out-Null
'SKILL.md', 'README.md', 'agents', 'references', 'scripts' | ForEach-Object {
    Copy-Item -LiteralPath (Join-Path $skillSource $_) -Destination $skillDestination -Recurse
}
```

On macOS or Linux, copy the same files and directories into the personal or repository location above. The resulting entrypoint must be `flamethrower/SKILL.md`, without an extra nested skill folder.

Codex discovers local skill changes automatically. If Flamethrower does not appear, restart Codex. See the [official skill documentation](https://learn.chatgpt.com/docs/build-skills) for discovery locations and installation behavior.

## Requirements

The review instructions and bundled references need no additional packages or external course-notes folder. The optional `scripts/review_context.py` helper requires Python 3.9+ and Git on `PATH`; it uses only the Python standard library. The reviewed project's build and tests may need their own dependencies.

## Use and verify

Select Flamethrower in the skill picker, or explicitly invoke it in a Codex prompt:

```text
Use $flamethrower to review this PR against its actual base branch.
```

For PE work, provide the released artifact, UG/DG, and the testing or reporting task. For a PR review, provide the repository and PR or base/head refs. Follow [SKILL.md](SKILL.md) for the review workflow.

In Codex CLI or the IDE extension, use `/skills` to confirm the skill is listed. In the desktop skill picker, look for Flamethrower. If it is missing, check the installation directory and confirm `SKILL.md` is directly inside `flamethrower/`.

## Update an installation

Copy the updated skill files into the existing installation, preserving the folder structure. Keep one installation for the intended scope so personal and repository copies do not drift. If an older setup already loads Flamethrower from a different skills directory, update that copy instead of adding a duplicate.
