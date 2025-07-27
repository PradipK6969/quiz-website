package com.example.quiz.servlet;

import com.example.quiz.dao.UserDAO;
import com.example.quiz.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/manageUsers") // Placed under /admin/ path for clarity
public class ManageUsersServlet extends HttpServlet {
    private UserDAO userDAO;

    @Override
    public void init() {
        userDAO = new UserDAO();
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

        List<User> allUsers = userDAO.getAllUsers();
        request.setAttribute("users", allUsers);
        request.setAttribute("loggedInAdminId", adminUser.getId()); // To prevent deleting self

        // For messages from delete action
        String successMessage = request.getParameter("successMessage");
        String errorMessage = request.getParameter("errorMessage");
        if (successMessage != null) {
            request.setAttribute("successMessage", successMessage);
        }
        if (errorMessage != null) {
            request.setAttribute("errorMessage", errorMessage);
        }

        request.getRequestDispatcher("/jsp/manageUsers.jsp").forward(request, response);
    }
}