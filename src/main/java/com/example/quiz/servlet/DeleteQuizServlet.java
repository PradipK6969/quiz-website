package com.example.quiz.servlet;

import com.example.quiz.dao.QuizDAO;
import com.example.quiz.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/deleteQuiz")
public class DeleteQuizServlet extends HttpServlet {
    private QuizDAO quizDAO;

    @Override
    public void init() {
        quizDAO = new QuizDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        handleDelete(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // It's generally better to use POST for delete operations to prevent accidental deletion
        // via direct URL access or by search engine crawlers.
        // For simplicity with the current link structure, we'll allow GET, but POST is preferred.
        handleDelete(request, response);
    }

    private void handleDelete(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        HttpSession session = request.getSession(false);
        User adminUser = (session != null) ? (User) session.getAttribute("user") : null;

        if (adminUser == null || !"admin".equals(adminUser.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login?error=Unauthorized");
            return;
        }

        String quizIdStr = request.getParameter("quizId");
        if (quizIdStr == null || quizIdStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/viewQuizzes?errorMessage=InvalidQuizIdForDelete");
            return;
        }

        try {
            int quizId = Integer.parseInt(quizIdStr);
            boolean success = quizDAO.deleteQuiz(quizId);

            if (success) {
                response.sendRedirect(request.getContextPath() + "/viewQuizzes?successMessage=Quiz+deleted+successfully");
            } else {
                response.sendRedirect(request.getContextPath() + "/viewQuizzes?errorMessage=Failed+to+delete+quiz.+It+might+not+exist+or+an+error+occurred.");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/viewQuizzes?errorMessage=InvalidQuizIdFormatForDelete");
        }
    }
}