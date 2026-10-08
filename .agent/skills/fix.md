# Skill: Fix

Use this skill when:
- CI tests failed on an AI branch (comment contains "CI tests failed on branch `ai/issue-N`")
- User left a PR review comment on a specific file and line

## For CI failure

1. Read the test output in the comment carefully
2. Identify the root cause — compilation error, assertion failure, wrong logic
3. Fix the minimum necessary — do not rewrite unrelated code

Respond ONLY with raw JSON:
{"action":"fix","branch":"ai/issue-N","files":[{"path":"...","content":"...full fixed file..."}]}

Extract the branch name exactly from the failure comment text.

## For PR review comment

1. Read the reviewer's comment and the file/line context provided
2. Apply the requested change
3. If the change affects logic — update the test too

Respond ONLY with raw JSON:
{"action":"fix","branch":"BRANCH_NAME","files":[{"path":"...","content":"...full updated file..."}]}

The branch name is provided in the context message.

## Rules for both cases

- Always write the complete file — never partial content
- Only include files that actually changed
