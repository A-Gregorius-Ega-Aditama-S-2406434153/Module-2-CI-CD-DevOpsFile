# Submission-Tutorial-Modul-1

### Gregorius Ega Aditama Sudjali
### NPM: 2406434153

## Reflection 1:

```text
You already implemented two new features using Spring Boot. Check again your source code
and evaluate the coding standards that you have learned in this module. Write clean code
principles and secure coding practices that have been applied to your code. If you find any
mistake in your source code, please explain how to improve your code. Please write your
reflection inside the repository's README.md file.
```

## Answer reflection 1:
```text
I reviewed my source code by following the instruction and evaluated it based on the coding standards from this module, and overall
I think my structure is fairly clean because I separate responsibilities across layers: my `ProductController` focuses
on handling HTTP requests and preparing data for the view, my `ProductService` (interface + implementation) acts as the 
business layer, and my `ProductRepository` manages the data storage. I also apply the clean code principle of 
programming to an interface by having the controller depend on `ProductService` instead of the implementation 
class, which reduces coupling and makes the code easier to test or change later. In addition, 
I use clear, consistent method names such as `create()` and `findAll()` so the intent of each method is easy 
to understand, and I follow Spring conventions by using annotations like `@Controller`,
`@Service`, and `@Repository` to make each component’s role explicit. For secure coding practices, I use `POST` for 
creating products rather than `GET`, which aligns with safe HTTP semantics for state-changing operations, and I use 
the redirect-after-post pattern (`redirect:list`) to prevent duplicate submissions when users refresh the page.
I also avoid risky patterns such as building SQL queries or running commands using user input in these classes, which 
reduces the likelihood of injection-style vulnerabilities at this stage. However, I did find 
a mistake in my code: I named some files incorrectly, which breaks Java conventions and can reduce readability and 
maintainability (for example, in Java the public class name must match the filename exactly). To improve this, I should
rename files so every public class matches its filename (e.g., `ProductServiceImpl.java` must contain `public class
ProductServiceImpl`, and `ProductController.java` must contain `public class ProductController`) and keep 
naming consistent across packages. Beyond naming, I can further improve my code by switching from field
injection (`@Autowired` on fields) to constructor injection for better immutability and testing, adding
input validation (for example using `@Valid` and validation annotations or using a DTO instead of 
binding directly to the entity), and improving the repository API so it returns a `List<Product>` directly 
instead of an `Iterator<Product>` to avoid extra conversion logic in the service layer.
```
## Reflection 2:
```text
After writing the unit test, how do you feel? How many unit tests should be made in a
class? How to make sure that our unit tests are enough to verify our program? It would be
good if you learned about code coverage. Code coverage is a metric that can help you
understand how much of your source is tested. If you have 100% code coverage, does
that mean your code has no bugs or errors?
2. Suppose that after writing the CreateProductFunctionalTest.java along with the
corresponding test case, you were asked to create another functional test suite that
verifies the number of items in the product list. You decided to create a new Java class
similar to the prior functional test suites with the same setup procedures and instance
variables.
What do you think about the cleanliness of the code of the new functional test suite? Will
the new code reduce the code quality? Identify the potential clean code issues, explain
the reasons, and suggest possible improvements to make the code cleaner! Please write
your reflection inside the repository's README.md file.
```

## Answer reflection 2:
```text
1. Unit testing confidence, coverage, and limitations

After writing unit tests, I feel more confident about the correctness of the core logic because tests help
verify expected behavior under controlled conditions. However, I still remain cautious, since unit tests can
never prove the absence of bugs they only reduce uncertainty. There is no fixed or “correct” number of unit
tests per class; instead, the goal is to have sufficient tests that cover important behaviors, decision branches,
and edge cases without becoming redundant or overly coupled to internal implementation details. To judge
whether tests are sufficient, I focus on critical execution paths, boundary conditions  and error handling
scenarios. I also use code coverage tools as a guideline to identify which lines and branches have not been exercised.
That said, coverage metrics must be interpreted carefully: achieving 100% coverage does not guarantee the
program is bug free. Coverage only indicates that code was executed during tests, not that the assertions
were meaningful or correct. Bugs may still exist due to missing or weak assertions, incorrect assumptions
in test expectations, race conditions in concurrent code, or issues that only appear when
components interact in real environments. Because of this, unit testing should be complemented
with other testing levels such as integration tests, system tests, and exploratory testing, especially
for complex logic or state-dependent behavior.

2. Test duplication, maintainability, and test design improvements Creating another functional test suite with the
same setup logic and instance variables can easily introduce code duplication and reduce clarity. Repeating driver
initialization, base URL construction, timeouts, and helper utilities often leads to copy-paste code, which makes changes
error prone and increases the risk of inconsistent behavior across test classes. This negatively impacts code cleanliness by
violating the DRY (Don’t Repeat Yourself) principle and makes long-term maintenance harder.A better approach is to extract
shared setup and teardown logic into a base test class or a reusable JUnit 5 extension, allowing common configuration to be
defined in one place. Shared helper methods for example, for waiting strategies, navigation, or URL construction can
further improve consistency and readability. This also makes updates easier, since changes only need to be applied
once instead of across multiple test files. Another important improvement is applying the Page Object Model (POM).
By centralizing UI element locators and user interactions into dedicated page classes, test casesbecome shorter,
more expressive, and easier to understand. This separation of concerns makes tests more resilient to UI changes,
ince updates to element selectors or interaction logic are confined to page objects rather than scattered throughout test code.
Overall, these practices lead to cleaner, more maintainable, and more scalable test suites.
```

## Reflection 3:
```text
1. List the code quality issue(s) that you fixed during the exercise and explain your strategy
on fixing them.
2. Look at your CI/CD workflows (GitHub)/pipelines (GitLab). Do you think the current
implementation has met the definition of Continuous Integration and Continuous
Deployment? Explain the reasons (minimum 3 sentences)!
```

## Answer reflection 3:
```text
1. Code quality issues fixed and strategy

During this exercise, I fixed several code quality and security-related issues identified by static analysis and workflow review. 
First, I resolved an unused field issue in HomePageFunctionalTest by using WebDriverWait with ExpectedConditions, so the test 
became more stable and the existing wait dependency was used correctly. Second, I refactored ProductServiceImpl from field injection 
to constructor injection, made the dependency final, and added a null check to improve immutability, explicit dependencies, and testability. 
Third, in ci.yml I replaced gradle/actions/setup-gradle@v3 with a full commit SHA to reduce supply-chain risk from mutable tags. 
Fourth, in build.gradle.kts I removed hardcoded dependency versions where Spring dependency management already provides managed versions. 
Finally, I enabled Gradle dependency verification by adding gradle/verification-metadata.xml and gradle/verification-keyring.keys. 

My strategy was to prioritize issues by risk and maintainability: fix security-sensitive configuration first, then structural code quality issues, 
then consistency/build hygiene issues. I kept each change minimal and localized, aligned with existing project patterns, and verified changes by 
running Gradle tests after each significant update.

2. CI/CD evaluation

The current GitHub Actions setup has met the core definition of Continuous Integration because every push and pull request triggers automated 
build and test execution, and it also runs SonarCloud analysis to continuously check code quality. This gives fast feedback and enforces integration 
checks on shared code, which is a key characteristic of CI. However, it has not yet met the definition of Continuous Deployment because there is no 
automated deployment stage that releases successful builds to a production environment. There is also no release promotion pipeline (for example 
dev -> staging -> production) or post-deploy verification in the current workflow. So, the repository currently implements CI well, but CD still 
needs to be added.
```
