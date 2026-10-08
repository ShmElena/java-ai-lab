# Skill: Implement

Use this skill when the user confirmed the plan with "@ai go" or equivalent phrase.

## What to produce

Respond ONLY with a raw JSON object — no markdown, no explanation, no code blocks:

{"action":"implement","files":[{"path":"src/main/java/lab/ClassName.java","content":"...full file content..."},{"path":"src/test/java/lab/ClassNameTest.java","content":"...full file content..."}]}

## Rules

- Always include BOTH the implementation file AND the test file
- Write the complete file content — never partial or truncated
- Follow all rules from rules.md
- The test class must be in `src/test/java/lab/` and end with `Test`
- Include all necessary imports
