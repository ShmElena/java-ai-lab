# Java AI Lab — Agent Instructions

This is a Java 21 Maven project used for learning AI-driven GitHub workflows.

## Project structure

- `src/main/java/lab/` — production code
- `src/test/java/lab/` — JUnit 5 tests
- All classes live in the `lab` package

## Build and test commands

- Compile: `mvn compile`
- Run tests: `mvn test`
- Full build: `mvn verify`

## Coding conventions

- Java 21 features are allowed (records, sealed classes, pattern matching)
- Use `BigDecimal` for all monetary values, never `double` or `float`
- Use `Optional` instead of returning `null`
- Every public method must have a corresponding test
- Use SLF4J for logging (`LoggerFactory.getLogger(ClassName.class)`)
- Throw `IllegalArgumentException` for invalid input, `IllegalStateException` for invalid state

## Agent workflow

When implementing a feature:
1. First post an implementation plan as a comment — list the methods, validations, and tests you will write
2. Wait for user approval before writing any code
3. After approval, implement the code and run `mvn test` to verify
4. Only create a PR if all tests pass

When reviewing code:
- Focus on correctness first, then readability
- Always check for missing null checks and edge cases
- Suggest using Java 21 features where appropriate
