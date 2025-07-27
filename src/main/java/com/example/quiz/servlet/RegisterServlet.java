package com.example.quiz.servlet;

import com.example.quiz.dao.UserDAO;
import com.example.quiz.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/register") // This annotation maps the servlet to the "/register" URL pattern
public class RegisterServlet extends HttpServlet {
    private UserDAO userDAO;

    @Override
    public void init() {
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // This line forwards to the JSP page
        request.getRequestDispatcher("/jsp/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        // Basic Validations
        if (username == null || username.trim().isEmpty() ||
                email == null || email.trim().isEmpty() ||
                password == null || password.isEmpty() ||
                confirmPassword == null || confirmPassword.isEmpty()) { // Added check for confirmPassword
            request.setAttribute("errorMessage", "All fields are required.");
            request.getRequestDispatcher("/jsp/register.jsp").forward(request, response);
            return;
        }

        if (!password.equals(confirmPassword)) {
            request.setAttribute("errorMessage", "Passwords do not match.");
            request.getRequestDispatcher("/jsp/register.jsp").forward(request, response);
            return;
        }

        if (password.length() < 6) { // Example: minimum 6 characters
            request.setAttribute("errorMessage", "Password must be at least 6 characters long.");
            request.getRequestDispatcher("/jsp/register.jsp").forward(request, response);
            return;
        }

        User newUser = new User();
        newUser.setUsername(username.trim());
        newUser.setEmail(email.trim());
        newUser.setPassword(password); // DAO will store it plain

        if (userDAO.createUser(newUser)) {
            // Registration successful
            request.setAttribute("successMessage", "Registration successful! Please login.");
            request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
        } else {
            // Registration failed (e.g., username/email taken, DB error)
            request.setAttribute("errorMessage", "Registration failed. Username or email might already be in use, or another error occurred.");
            request.getRequestDispatcher("/jsp/register.jsp").forward(request, response);
        }
    }
}