#!/usr/bin/env python3
"""Emit local PR comparison metadata as JSON; never modify the checkout."""

import argparse
import json
import os
from pathlib import Path
import subprocess
import sys


def git(repo, *args, executable="git"):
    """Use argument arrays so paths and refs work without shell escaping."""
    env = dict(os.environ, GIT_OPTIONAL_LOCKS="0", GIT_TERMINAL_PROMPT="0")
    result = subprocess.run(
        [executable, "--no-pager", "-c", "core.fsmonitor=false", "-C", str(repo), *args],
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
        env=env,
        timeout=30,
        check=False,
    )
    if result.returncode:
        message = result.stderr.decode("utf-8", errors="replace").strip()
        raise ValueError(message or f"Git exited with status {result.returncode}")
    return result.stdout.decode("utf-8", errors="surrogateescape")


def changed_paths(output):
    """Decode NUL-separated names, including renamed paths and embedded newlines."""
    fields = iter(output.rstrip("\0").split("\0") if output else [])
    changes = []
    for status in fields:
        old_path = next(fields)
        if status.startswith(("R", "C")):
            changes.append({"status": status, "old_path": old_path, "path": next(fields)})
        else:
            changes.append({"status": status, "path": old_path})
    return changes


def collect(repo, base, head="HEAD", executable="git"):
    repo = Path(repo).expanduser().resolve()

    def read(*args):
        return git(repo, *args, executable=executable)

    root = read("rev-parse", "--show-toplevel").strip()
    # Resolve once: every comparison below uses immutable commit IDs.
    base_sha = read("rev-parse", "--verify", "--end-of-options", f"{base}^{{commit}}").strip()
    head_sha = read("rev-parse", "--verify", "--end-of-options", f"{head}^{{commit}}").strip()
    ancestors = read("merge-base", "--all", base_sha, head_sha).splitlines()
    if len(ancestors) != 1:
        raise ValueError("Multiple merge bases; inspect the history before choosing a comparison")
    merge_base = ancestors[0]
    changes = read(
        "diff", "--no-ext-diff", "--no-textconv", "--name-status", "-z", "-M",
        merge_base, head_sha, "--",
    )
    status = read("status", "--porcelain=v1", "--untracked-files=normal")
    return {
        "repository": root,
        "base": {"ref": base, "sha": base_sha},
        "head": {"ref": head, "sha": head_sha},
        "merge_base": merge_base,
        "changed_files": changed_paths(changes),
        "commits": read("log", "--oneline", f"{merge_base}..{head_sha}", "--").splitlines(),
        "checkout": {
            "head_sha": read("rev-parse", "--verify", "HEAD").strip(),
            "dirty": bool(status),
            "status_porcelain": status.splitlines(),
        },
    }


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("repo", nargs="?", default=".", help="Local repository or subdirectory")
    parser.add_argument("--base", required=True, help="Actual PR base ref, already available locally")
    parser.add_argument("--head", default="HEAD", help="PR head ref (default: HEAD)")
    parser.add_argument("--git", default="git", help="Git executable name or path")
    args = parser.parse_args()
    try:
        context = collect(args.repo, args.base, args.head, args.git)
    except (OSError, ValueError, subprocess.TimeoutExpired) as error:
        print(f"review_context: {error}", file=sys.stderr)
        return 1
    # ASCII escapes keep JSON portable across Windows terminal encodings.
    json.dump(context, sys.stdout, indent=2, ensure_ascii=True)
    print()
    return 0


if __name__ == "__main__":
    sys.exit(main())
