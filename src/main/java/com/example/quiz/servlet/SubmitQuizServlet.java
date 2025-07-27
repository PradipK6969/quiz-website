package com.example.quiz.servlet;

import com.example.quiz.dao.QuestionDAO;
import com.example.quiz.dao.QuizDAO;
import com.example.quiz.model.Option;
import com.example.quiz.model.Question;
import com.example.quiz.model.Quiz;
import com.example.quiz.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/submitQuiz")
public class SubmitQuizServlet extends HttpServlet {
    private QuizDAO quizDAO;
    private QuestionDAO questionDAO;

    @Override
    public void init() {
        quizDAO = new QuizDAO();
        questionDAO = new QuestionDAO(); // For fetching correct options and saving results
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String quizIdStr = request.getParameter("quizId");
        if (quizIdStr == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Quiz ID is missing.");
            return;
        }
        int quizId = Integer.parseInt(quizIdStr);

        Quiz quiz = quizDAO.getQuizById(quizId); // Fetch quiz details including questions and options
        if (quiz == null || quiz.getQuestions() == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Quiz not found.");
            return;
        }

        int score = 0;
        int totalQuestions = quiz.getQuestions().size();
        Map<Integer, Integer> userAnswers = new HashMap<>(); // questionId -> selectedOptionId
        Map<Integer, Boolean> answerCorrectness = new HashMap<>(); // questionId -> isCorrect

        // Save attempt first (optional, could do after calculating score)
        // int attemptId = questionDAO.saveQuizAttempt(user.getId(), quizId, 0); // Placeholder score initially

        for (Question question : quiz.getQuestions()) {
            String paramName = "question_" + question.getId();
            String selectedOptionIdStr = request.getParameter(paramName);

            if (selectedOptionIdStr != null && !selectedOptionIdStr.isEmpty()) {
                int selectedOptionId = Integer.parseInt(selectedOptionIdStr);
                userAnswers.put(question.getId(), selectedOptionId);

                Option selectedOption = questionDAO.getOptionById(selectedOptionId); // Fetch option to check correctness
                if (selectedOption != null && selectedOption.isCorrect()) {
                    score++;
                    answerCorrectness.put(question.getId(), true);
                } else {
                    answerCorrectness.put(question.getId(), false);
                }
                // If saving individual answers:
                // questionDAO.saveUserAnswer(attemptId, question.getId(), selectedOptionId, selectedOption.isCorrect());
            } else {
                // Handle unanswered question if needed
                answerCorrectness.put(question.getId(), false);
                // if saving individual answers
                // questionDAO.saveUserAnswer(attemptId, question.getId(), null, false);
            }
        }

        // Update attempt with final score
        // questionDAO.updateQuizAttemptScore(attemptId, score);
        // For simplicity, we are just displaying score, not saving detailed attempt record yet.
        // But a full implementation would use `quiz_attempts` and `user_answers` tables.
        int attemptId = questionDAO.saveQuizAttempt(user.getId(), quizId, score); // Save attempt with score
        for (Map.Entry<Integer, Integer> entry : userAnswers.entrySet()) {
            questionDAO.saveUserAnswer(attemptId, entry.getKey(), entry.getValue(), answerCorrectness.get(entry.getKey()));
        }


        request.setAttribute("score", score);
        request.setAttribute("totalQuestions", totalQuestions);
        request.setAttribute("quiz", quiz); // For displaying questions and correct answers on result page
        request.setAttribute("userAnswers", userAnswers); // Map of questionId to selectedOptionId
        request.setAttribute("answerCorrectness", answerCorrectness);
        request.getRequestDispatcher("/jsp/quizResult.jsp").forward(request, response);
    }
}
