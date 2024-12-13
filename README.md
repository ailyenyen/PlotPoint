
![PLotPoint Logo](https://github.com/user-attachments/assets/8cb6932e-145a-4bbd-a4de-3cab7127c2a2)



## I. Project Overview

- **PlotPoint** is a comprehensive book tracking application tailored for book lovers to enhance their reading experience. Users can securely sign up and log in to access personalized features such as book search, shelving, and reading analytics. The platform allows users to search for books by title or author and view detailed information, including average ratings and community reviews, offering valuable insights into a book's quality and appeal.

- PlotPoint's shelving system helps users organize books into default categories like “Want to Read,” “Reading,” and “Read”. Users can rate books, write detailed reviews, and explore personalized recommendations based on genres or moods. Additionally, the application provides insightful reading statistics, such as books read per month and total pages read, empowering users to set goals and track their progress. By combining these features, PlotPoint fosters a seamless and engaging reading experience for all.

---

## II. Object-Oriented Programming (OOP) Principles Applied
PlotPoint is built with a strong foundation in OOP principles, ensuring maintainability and scalability:
- **Encapsulation**:  
  - All core functionalities, such as managing books, users, and reviews, are encapsulated within dedicated classes like `BookManager`, `UserManager`, and `ReviewManager`, ensuring modular code organization.

- **Inheritance**:  
  - PlotPoint leverages inheritance to streamline shared functionality and attributes across related components. For example:
    - The `Menu` class serves as a base class, encapsulating common menu behaviors such as displaying options and handling user input.
    - Specialized menus like `UserMenu`, `BookSearchMenu`, and `RecommendationsMenu` inherit from `Menu`, reusing core functionalities while extending or overriding them to provide specific features.
      
- **Polymorphism**:  
  - PlotPoint demonstrates polymorphism by enabling flexible and dynamic behavior across different classes and methods:
      - Methods like `searchBooks` are designed to accept various input types, such as title, author, or genre, while providing consistent functionality regardless of the search parameter. This ensures users can interact with the system intuitively without requiring separate methods for each case.
      - The `Menu` class and its subclasses (`UserMenu`, `BookSearchMenu`, etc.) exhibit polymorphism through the `displayMenu()` method. Each subclass provides its unique implementation of `displayMenu()`, allowing diverse menu behaviors while adhering to a unified interface. For example, `displayMenu()` in `BookSearchMenu` handles search operations, whereas `displayMenu()` in `UserMenu` presents user-specific options.

- **Abstraction**:
    - Abstraction is key to managing the complexity of PlotPoint by focusing on essential details while hiding implementation specifics:
      - The `Menu` class is defined as abstract, serving as a blueprint for other menu types. It provides common methods like `displayTitle()` and `getUserChoice()` while leaving `displayMenu()` as an abstract method. This forces subclasses to implement their specific behavior, ensuring clarity and separation of concerns.  
  
---

## III. Chosen SDG: Quality Education (Goal 4)

- PlotPoint aligns with **Sustainable Development Goal 4: Quality Education** by promoting lifelong learning and fostering a culture of reading. By providing features such as personalized recommendations and reading statistics, the platform encourages users to explore diverse genres, set reading goals, and engage with literature as a means of personal growth. These tools inspire users to continuously expand their knowledge and improve their skills through the transformative power of books.

- Additionally, PlotPoint makes discovering and organizing educational content easy and accessible. Features like the shelving system enable users to categorize books into "Want to Read," "Read," or custom shelves, encouraging structured reading habits. By connecting readers to diverse perspectives and ideas, PlotPoint contributes to a more informed and empathetic community, embodying the essence of lifelong learning promoted by SDG 4.

---

## IV. Instructions for Running the Program

