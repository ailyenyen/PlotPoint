![PLotPoint Logo](https://github.com/user-attachments/assets/5c569274-d169-4ba8-bfd4-3e2f2570da11)

# PlotPoint: Your Personal Book Tracker
<img src="https://github.com/user-attachments/assets/95bdd3c0-cd9a-407e-99cc-86de9b2606cb" alt="Logo Mini" width="200">

## I. Project Overview
**PlotPoint** is a book tracking application designed for book lovers to seamlessly manage their reading experience. With PlotPoint, users can:
- Search for books and view detailed information.
- Rate and review books they've read.
- Organize books into personal shelves.
- Analyze their reading habits with insightful statistics.
- Receive personalized book recommendations based on genres or moods.

This project emphasizes user-friendly design and robust functionality to make reading more engaging and organized.

---

## II. Object-Oriented Programming (OOP) Principles Applied
PlotPoint is built with a strong foundation in OOP principles, ensuring maintainability and scalability:
- **Encapsulation**:  
  All core functionalities, such as managing books, users, and reviews, are encapsulated within dedicated classes like `BookManager`, `UserManager`, and `ReviewManager`, ensuring modular code organization.
- **Inheritance**:  
  Shared behaviors and attributes of entities like `Book` and specialized categories (e.g., genres or moods) are structured using inheritance.
- **Polymorphism**:  
  Methods like `searchBooks` are designed to handle searching by title, author, or genre, providing flexible user inputs.
- **Abstraction**:  
  Complex database operations are abstracted within helper classes, like `DatabaseHelper`, to simplify interaction with MySQL.

---

## III. Chosen SDG: Quality Education (Goal 4)
PlotPoint aligns with **Sustainable Development Goal 4: Quality Education**, promoting lifelong learning through reading. By making it easier to track and discover books, PlotPoint encourages users to broaden their knowledge and engage with diverse literature. Features like personalized recommendations and reading statistics inspire users to set reading goals, fostering a culture of continuous learning and self-improvement.

---

## IV. Instructions for Running the Program
To run PlotPoint, follow these steps:

1. **Prerequisites**:
   - Install Java Development Kit (JDK) 11 or higher.
   - Set up MySQL and create the `book_tracker` database using the provided schema.

2. **Setup**:
   - Clone this repository:
     ```bash
     git clone https://github.com/yourusername/PlotPoint.git
     cd PlotPoint
     ```
   - Update the `DatabaseHelper` class with your MySQL credentials.

3. **Compile and Run**:
   - Compile the program:
     ```bash
     javac -d bin src/**/*.java
     ```
   - Run the program:
     ```bash
     java -cp bin Main
     ```

4. **Enjoy**:
   - Log in or create an account to start tracking your books.
   - Explore features like searching for books, adding to shelves, and viewing recommendations.

---

