package com.example.quiz.servlet;

import com.example.quiz.dao.QuizDAO;
import com.example.quiz.dao.QuestionDAO;
import com.example.quiz.model.Quiz;
import com.example.quiz.model.Question;
import com.example.quiz.model.Option;
import com.example.quiz.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@WebServlet("/createQuiz")
public class CreateQuizServlet extends HttpServlet {
    private QuizDAO quizDAO;
    private QuestionDAO questionDAO;

    @Override
    public void init() {
        quizDAO = new QuizDAO();
        questionDAO = new QuestionDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user != null && "admin".equals(user.getRole())) {
            request.getRequestDispatcher("/jsp/createQuiz.jsp").forward(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/login");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User adminUser = (session != null) ? (User) session.getAttribute("user") : null;

        if (adminUser == null || !"admin".equals(adminUser.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String quizTitle = request.getParameter("quizTitle");
        String quizDescription = request.getParameter("quizDescription");
        String quizCategory = request.getParameter("quizCategory");
        String durationMinutesStr = request.getParameter("durationMinutes");

        int durationMinutes = 0;
        if (durationMinutesStr != null && !durationMinutesStr.trim().isEmpty()) {
            try {
                durationMinutes = Integer.parseInt(durationMinutesStr);
            } catch (NumberFormatException e) {
                durationMinutes = 0;
            }
        }

        Quiz newQuiz = new Quiz();
        newQuiz.setTitle(quizTitle);
        newQuiz.setDescription(quizDescription);
        newQuiz.setCategory(quizCategory);
        newQuiz.setCreatedByAdminId(adminUser.getId());
        newQuiz.setDurationMinutes(durationMinutes);

        int quizId = quizDAO.createQuiz(newQuiz);

        if (quizId > 0) {
            // Handle multiple questions
            int questionIndex = 0;
            while (true) {
                String questionText = request.getParameter("questions[" + questionIndex + "].questionText");
                if (questionText == null || questionText.trim().isEmpty()) {
                    // No more questions submitted with this index
                    break;
                }

                String questionType = request.getParameter("questions[" + questionIndex + "].questionType"); // Currently always MCQ
                if (questionType == null) questionType = "MCQ"; // Default

                Question q = new Question();
                q.setQuizId(quizId);
                q.setQuestionText(questionText);
                q.setQuestionType(questionType);

                List<Option> optionsList = new ArrayList<>();
                String correctOptionIndexStr = request.getParameter("questions[" + questionIndex + "].correctOptionIndex");
                int correctOptionIdx = -1;
                if(correctOptionIndexStr != null && !correctOptionIndexStr.isEmpty()){
                    try {
                        correctOptionIdx = Integer.parseInt(correctOptionIndexStr);
                    } catch (NumberFormatException e) { /* ignore, stays -1 */ }
                }


                for (int i = 0; i < 4; i++) { // Assuming max 4 options per MCQ
                    String optionText = request.getParameter("questions[" + questionIndex + "].options[" + i + "].optionText");
                    if (optionText != null && !optionText.trim().isEmpty()) {
                        Option opt = new Option();
                        opt.setOptionText(optionText);
                        opt.setCorrect(i == correctOptionIdx); // Check if current option index matches the selected correct one
                        optionsList.add(opt);
                    } else if (i < 2) { // Assuming first 2 options are required if question is present
                        // Handle error: required option missing, maybe break or set error message
                        System.err.println("Warning: Missing required option text for question " + questionIndex + ", option " + i);
                    }
                }
                q.setOptions(optionsList);

                if (!optionsList.isEmpty() && correctOptionIdx != -1) { // Ensure there are options and one is marked correct
                    questionDAO.addQuestionToQuiz(q);
                } else {
                    System.err.println("Skipping question " + questionIndex + " due to no options or no correct option selected.");
                }
                questionIndex++;
            }

            if (questionIndex == 0) { // No questions were added
                request.setAttribute("warningMessage", "Quiz created, but no questions were added. Please edit the quiz to add questions.");
                // Consider if a quiz without questions should be allowed or if it should be an error.
                // For now, redirecting to admin dashboard with a warning.
                response.sendRedirect(request.getContextPath() + "/adminDashboard?warningMessage=Quiz+created+but+no+questions+added.");
            } else {
                response.sendRedirect(request.getContextPath() + "/adminDashboard?message=Quiz+created+successfully+with+" + questionIndex + "+question(s).");
            }

        } else {
            request.setAttribute("errorMessage", "Failed to create quiz.");
            request.getRequestDispatcher("/jsp/createQuiz.jsp").forward(request, response);
        }
    }
}