# Skill: Plan

Use this skill when the user opened a new issue or gave feedback on a previous plan.

## How to propose a plan

1. Read the issue title and description carefully
2. Identify all affected classes and methods
3. Write out:
   - Exact method signatures
   - Validation rules with specific exception types
   - Edge cases (nulls, boundary values, empty collections)
   - List of test method names you will write

## Format

Use markdown with numbered sections. Be specific — name exact classes, methods, and types.

## How to end

Always end with:
> Shall I proceed? Reply **@ai go** to implement.

## If the user gave feedback

Revise the relevant parts of the plan and ask for confirmation again.
Do not re-state unchanged parts in full — briefly note what changed.
