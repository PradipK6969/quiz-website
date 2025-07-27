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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/editQuiz")
public class EditQuizServlet extends HttpServlet {
    private QuizDAO quizDAO;
    private QuestionDAO questionDAO;

    @Override
    public void init() {
        quizDAO = new QuizDAO();
        questionDAO = new QuestionDAO();
    }

    // Display the edit form
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User adminUser = (session != null) ? (User) session.getAttribute("user") : null;

        if (adminUser == null || !"admin".equals(adminUser.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login?error=Unauthorized");
            return;
        }

        String quizIdStr = request.getParameter("quizId");
        if (quizIdStr == null) {
            response.sendRedirect(request.getContextPath() + "/viewQuizzes?errorMessage=NoQuizIdForEdit");
            return;
        }

        try {
            int quizId = Integer.parseInt(quizIdStr);
            Quiz quiz = quizDAO.getQuizById(quizId); // This should fetch questions and options too

            if (quiz == null) {
                response.sendRedirect(request.getContextPath() + "/viewQuizzes?errorMessage=QuizNotFoundForEdit");
                return;
            }
            request.setAttribute("quizToEdit", quiz);
            request.getRequestDispatcher("/jsp/editQuiz.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/viewQuizzes?errorMessage=InvalidQuizIdFormatForEdit");
        }
    }

    // Process the submitted changes
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User adminUser = (session != null) ? (User) session.getAttribute("user") : null;

        if (adminUser == null || !"admin".equals(adminUser.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login?error=UnauthorizedUpdate");
            return;
        }

        // --- 1. Update Quiz Details ---
        int quizId = Integer.parseInt(request.getParameter("quizId"));
        String quizTitle = request.getParameter("quizTitle");
        String quizDescription = request.getParameter("quizDescription");
        String quizCategory = request.getParameter("quizCategory");
        String durationMinutesStr = request.getParameter("durationMinutes");
        int durationMinutes = 0;
        if (durationMinutesStr != null && !durationMinutesStr.isEmpty()) {
            try {
                durationMinutes = Integer.parseInt(durationMinutesStr);
            } catch (NumberFormatException e) { /* default to 0 */ }
        }

        Quiz quizToUpdate = quizDAO.getQuizById(quizId); // Get existing quiz to preserve other details
        if (quizToUpdate == null) {
            response.sendRedirect(request.getContextPath() + "/viewQuizzes?errorMessage=QuizToUpdateNotFound");
            return;
        }
        quizToUpdate.setTitle(quizTitle);
        quizToUpdate.setDescription(quizDescription);
        quizToUpdate.setCategory(quizCategory);
        quizToUpdate.setDurationMinutes(durationMinutes);
        quizDAO.updateQuiz(quizToUpdate);

        // --- 2. Handle Questions (Update existing, Add new, Delete removed) ---
        List<Integer> submittedQuestionIds = new ArrayList<>();
        int questionIndex = 0;
        while (true) {
            String questionIdStr = request.getParameter("questions[" + questionIndex + "].questionId");
            String questionText = request.getParameter("questions[" + questionIndex + "].questionText");

            if (questionText == null && questionIdStr == null) { // No more questions in form
                break;
            }
            // If questionText is null but questionIdStr is not, it means the block was there but text removed.
            // This might indicate deletion if the text field was emptied for an existing question.
            // However, our JS for "remove question" actually removes the block.
            // So, if questionText is null, we might assume the block was removed by JS.
            // A more robust way: have a hidden field "questions[X].isDeleted" or compare submitted IDs with existing.

            if (questionText == null || questionText.trim().isEmpty()) { // Skip if question text is empty (might be from a removed block)
                if (questionIdStr != null && !questionIdStr.isEmpty() && !questionIdStr.equals("NEW")) {
                    // This was an existing question whose text field was emptied. Treat as delete.
                    questionDAO.deleteQuestion(Integer.parseInt(questionIdStr));
                }
                questionIndex++;
                continue;
            }


            String questionType = request.getParameter("questions[" + questionIndex + "].questionType");
            if (questionType == null) questionType = "MCQ";

            Question q = new Question();
            q.setQuizId(quizId);
            q.setQuestionText(questionText);
            q.setQuestionType(questionType);

            List<Option> optionsList = new ArrayList<>();
            String correctOptionIndexStr = request.getParameter("questions[" + questionIndex + "].correctOptionIndex");
            int correctOptionIdx = -1;
            if (correctOptionIndexStr != null) {
                try {
                    correctOptionIdx = Integer.parseInt(correctOptionIndexStr);
                } catch (NumberFormatException e) {}
            }

            for (int i = 0; i < 4; i++) { // Max 4 options
                String optionText = request.getParameter("questions[" + questionIndex + "].options[" + i + "].optionText");
                // String optionIdStr = request.getParameter("questions[" + questionIndex + "].options[" + i + "].optionId"); // For granular option update

                if (optionText != null && !optionText.trim().isEmpty()) {
                    Option opt = new Option();
                    opt.setOptionText(optionText);
                    opt.setCorrect(i == correctOptionIdx);
                    // if (optionIdStr != null && !optionIdStr.isEmpty()) opt.setId(Integer.parseInt(optionIdStr));
                    optionsList.add(opt);
                }
            }
            q.setOptions(optionsList);

            if (questionIdStr != null && !questionIdStr.isEmpty() && !questionIdStr.equals("NEW")) {
                // Existing question: Update
                int existingQuestionId = Integer.parseInt(questionIdStr);
                q.setId(existingQuestionId);
                questionDAO.updateQuestion(q);
                submittedQuestionIds.add(existingQuestionId);
            } else if (questionIdStr != null && questionIdStr.equals("NEW") && !optionsList.isEmpty() && correctOptionIdx != -1){
                // New question: Add
                int newQuestionId = questionDAO.addQuestionToQuiz(q); // This method also adds options
                if(newQuestionId > 0) submittedQuestionIds.add(newQuestionId);
            } else if (questionIdStr == null && !optionsList.isEmpty() && correctOptionIdx != -1) {
                // This case can happen if the JS adds a question but doesn't set questionId to "NEW"
                // Or if form submission is weird. Treat as new.
                int newQuestionId = questionDAO.addQuestionToQuiz(q);
                if(newQuestionId > 0) submittedQuestionIds.add(newQuestionId);
            }
            questionIndex++;
        }

        // --- 3. Delete questions that were in the DB but not submitted (i.e., removed via JS) ---
        List<Question> existingQuestions = quizToUpdate.getQuestions(); // Fetched earlier by getQuizById
        if (existingQuestions != null) {
            for (Question dbQuestion : existingQuestions) {
                if (!submittedQuestionIds.contains(dbQuestion.getId())) {
                    questionDAO.deleteQuestion(dbQuestion.getId());
                }
            }
        }

        response.sendRedirect(request.getContextPath() + "/viewQuizzes?successMessage=Quiz+updated+successfully");
    }
}