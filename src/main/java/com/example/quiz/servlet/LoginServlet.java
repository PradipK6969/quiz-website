package com.example.quiz.servlet;

import com.example.quiz.dao.UserDAO;
import com.example.quiz.model.User;
// import com.example.quiz.util.PasswordUtil; // REMOVE THIS IMPORT IF PasswordUtil DOES NOT EXIST

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private UserDAO userDAO;

    @Override
    public void init() {
        userDAO = new UserDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String passwordAttempt = request.getParameter("password"); // Password entered by user

        User user = userDAO.getUserByUsername(username);

        // Perform direct plain text password comparison
        if (user != null && user.getPassword() != null && user.getPassword().equals(passwordAttempt)) {
            HttpSession session = request.getSession();
            session.setAttribute("user", user);

            if ("admin".equals(user.getRole())) {
                response.sendRedirect(request.getContextPath() + "/adminDashboard");
            } else {
                response.sendRedirect(request.getContextPath() + "/userDashboard");
            }
        } else {
            request.setAttribute("errorMessage", "Invalid username or password.");
            request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            User loggedInUser = (User) session.getAttribute("user");
            if ("admin".equals(loggedInUser.getRole())) {
                response.sendRedirect(request.getContextPath() + "/adminDashboard");
            } else {
                response.sendRedirect(request.getContextPath() + "/userDashboard");
            }
            return;
        }
        request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
    }
}