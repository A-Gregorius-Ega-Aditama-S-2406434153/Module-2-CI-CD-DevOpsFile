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
1. After writing the unit tests, I feel more confident about the correctness of the core logic, but I still
stay cautious because tests can miss edge cases. There is no fixed number of unit tests per class; the right
amount is enough to cover important behaviors, branches, and edge cases without becoming redundant or too
tightly coupled to implementation details. To make sure tests are enough, I focus on critical paths, boundary
conditions, and error handling, and I use code coverage as a guide to see which lines and branches are not
exercised. However, 100% coverage does not mean the program has no bugs. Coverage only shows that code ran,
not that assertions were correct or that all scenarios were tested. Bugs can still exist due to missing
assertions, wrong expectations, concurrency issues, or integration problems.

2. Creating another functional test suite with the same setup and instance variables can introduce code
duplication and reduce clarity. Repeating driver setup, base URL construction, and helper methods can lead
to copy-paste code, scattered updates, and inconsistent behavior between tests. This hurts cleanliness by
violating DRY (Dont Repeat Yourself) and makes maintenance harder. To improve this, I can extract shared
setup and utilities into a base class or a JUnit 5 extension, and use common helper methods for waits and
URL creation. Another improvement is the Page Object Model to centralize element locators and actions so
tests are shorter, more readable, and more resilient to UI changes.
```
