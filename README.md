# QuizMaster - A Java Web Quiz Platform

QuizMaster is a full-featured quiz website built using Java Servlets, JSP, Maven, and PostgreSQL. It supports distinct roles for users and administrators, dynamic quiz creation, progress tracking, and includes algorithms for recommendations and question difficulty prediction.

## Features Implemented

- **User and Admin Roles:** Secure login for both users and administrators, leading to separate, feature-rich dashboards.
- **User Registration:** Self-service user registration with validation.
- **Full Quiz Management (Admin):**
    - Create, Edit, and Delete quizzes.
    - Dynamically add/remove multiple-choice questions within a quiz.
    - Set optional time limits for quizzes.
- **Interactive Quiz Taking:**
    - Paginated, one-question-at-a-time interface.
    - Progress bar and question counter.
    - Real-time countdown timer for timed quizzes.
- **Dynamic Content:**
    - Question and option shuffling for each attempt.
- **User Analytics & Gamification:**
    - **Leaderboard:** View top scores across all quizzes.
    - **Personal Progress Tracking:** A dedicated "My Progress" page with summary stats, a score history chart (using Chart.js), and a detailed attempt table.
- **Admin Analytics:**
    - "View Statistics" dashboard showing total users, quizzes, and attempts.
    - A list of quizzes ranked by popularity (number of attempts).

## Algorithms Implemented

1.  **Recommendation System (Collaborative Filtering):**
    - **"Recommended for You"** on the main quiz list, suggesting quizzes based on the user's entire history and the activities of similar users.
    - **"You Might Also Like..."** on the quiz results page, suggesting quizzes taken by other users who also took the one just completed.
2.  **Question Difficulty Prediction:**
    - Automatically assigns a difficulty rating (Easy, Medium, Hard, Not Rated) to each question based on the aggregate success rate of all user answers.
    - Admins can view this rating on the "Edit Quiz" page to get feedback on their content.

## Tech Stack

- **Backend:** Java 8+, Java Servlets, JSP, JSTL
- **Database:** PostgreSQL
- **Build Tool:** Apache Maven
- **Frontend:** HTML5, CSS3, JavaScript (ES6)
- **Libraries:**
    - jBCrypt (for future password hashing)
    - Google Gson (for JSON processing)
    - Chart.js (for data visualization)
- **Server:** Apache Tomcat 9

## Setup and Installation

1.  **Database Setup:**
    - Install PostgreSQL.
    - Create a database (e.g., `quizdb`) and a user with privileges.
    - Update the database credentials in `src/main/java/com/example/quiz/dao/DBUtil.java`.
    - Run the `schema.sql` script (located in the project root or `src/main/resources`) to create all necessary tables and the initial admin user.

2.  **Build the Project:**
    - Clone the repository: `git clone [your-repo-url]`
    - Navigate to the project root directory.
    - Run the Maven build command: `mvn clean package`
    - This will generate a `quiz-website.war` file in the `target/` directory.

3.  **Deployment:**
    - Deploy the `quiz-website.war` file to your Apache Tomcat server (or any other Java Servlet container).
    - Access the application at `http://localhost:8080/quiz-website/` (the context path may vary).

## Default Credentials

- **Admin:** `username: admin`, `password: adminpass`
