# Java Coding Rules

- Java 21 features are allowed: records, sealed classes, pattern matching, text blocks
- Use `BigDecimal` for all monetary values — never `double` or `float`
- Use `Optional` instead of returning `null`
- Throw `IllegalArgumentException` for invalid input
- Throw `IllegalStateException` for invalid state
- Use SLF4J for logging: `LoggerFactory.getLogger(ClassName.class)`
- Every public method must have a corresponding test
- Tests use JUnit 5 — use `@Test`, `assertThrows`, `assertEquals`
- Build tool: Maven — run `mvn test` to verify
- All classes in package `lab`
