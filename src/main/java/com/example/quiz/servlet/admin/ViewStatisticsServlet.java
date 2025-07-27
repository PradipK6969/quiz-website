package com.example.quiz.servlet.admin; // In admin sub-package

import com.example.quiz.dao.StatisticsDAO;
import com.example.quiz.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

@WebServlet("/admin/viewStatistics")
public class ViewStatisticsServlet extends HttpServlet {
    private StatisticsDAO statisticsDAO;

    @Override
    public void init() {
        statisticsDAO = new StatisticsDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User adminUser = (session != null) ? (User) session.getAttribute("user") : null;

        if (adminUser == null || !"admin".equals(adminUser.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login?error=Unauthorized");
            return;
        }

        long totalUsers = statisticsDAO.getTotalUsers();
        long totalQuizzes = statisticsDAO.getTotalQuizzes();
        long totalAttempts = statisticsDAO.getTotalQuizAttempts();
        Map<String, Long> attemptsPerQuiz = statisticsDAO.getAttemptsPerQuiz();

        request.setAttribute("totalUsers", totalUsers);
        request.setAttribute("totalQuizzes", totalQuizzes);
        request.setAttribute("totalAttempts", totalAttempts);
        request.setAttribute("attemptsPerQuiz", attemptsPerQuiz);


        request.getRequestDispatcher("/jsp/admin/viewStatistics.jsp").forward(request, response);
    }
}