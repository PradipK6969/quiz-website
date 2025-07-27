package com.example.quiz.servlet;

import com.example.quiz.dao.QuizDAO;
import com.example.quiz.model.LeaderboardEntry;
import com.example.quiz.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/leaderboard")
public class LeaderboardServlet extends HttpServlet {
    private QuizDAO quizDAO;

    @Override
    public void init() {
        quizDAO = new QuizDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) { // Leaderboard can be public or user-only. Here, require login.
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Fetch top N (e.g., top 10) quiz attempts for the leaderboard
        List<LeaderboardEntry> topAttempts = quizDAO.getTopQuizAttempts(10);

        request.setAttribute("leaderboardEntries", topAttempts);
        request.getRequestDispatcher("/jsp/leaderboard.jsp").forward(request, response);
    }
}