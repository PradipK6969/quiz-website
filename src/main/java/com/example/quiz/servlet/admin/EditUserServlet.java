package com.example.quiz.servlet.admin; // New package

import com.example.quiz.dao.UserDAO;
import com.example.quiz.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/admin/editUser")
public class EditUserServlet extends HttpServlet {
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

        String userIdStr = request.getParameter("userId");
        if (userIdStr == null) {
            response.sendRedirect(request.getContextPath() + "/admin/manageUsers?errorMessage=NoUserIdForEdit");
            return;
        }

        try {
            int userId = Integer.parseInt(userIdStr);
            User userToEdit = userDAO.getUserById(userId); // Fetches without password

            if (userToEdit == null) {
                response.sendRedirect(request.getContextPath() + "/admin/manageUsers?errorMessage=UserNotFoundForEdit");
                return;
            }
            request.setAttribute("userToEdit", userToEdit);
            request.getRequestDispatcher("/jsp/admin/editUser.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/manageUsers?errorMessage=InvalidUserIdFormatForEdit");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User adminUser = (session != null) ? (User) session.getAttribute("user") : null;

        if (adminUser == null || !"admin".equals(adminUser.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login?error=UnauthorizedUpdate");
            return;
        }

        int userId = Integer.parseInt(request.getParameter("userId"));
        String username = request.getParameter("username"); // Username is displayed but not changed here
        String email = request.getParameter("email");
        String role = request.getParameter("role");

        // Basic validation
        if (email == null || email.trim().isEmpty() || role == null || role.trim().isEmpty()) {
            User userToEdit = userDAO.getUserById(userId);
            request.setAttribute("userToEdit", userToEdit); // Re-populate for form display
            request.setAttribute("errorMessage", "Email and Role cannot be empty.");
            request.getRequestDispatcher("/jsp/admin/editUser.jsp").forward(request, response);
            return;
        }
        if (!role.equals("user") && !role.equals("admin")) {
            User userToEdit = userDAO.getUserById(userId);
            request.setAttribute("userToEdit", userToEdit);
            request.setAttribute("errorMessage", "Invalid role specified.");
            request.getRequestDispatcher("/jsp/admin/editUser.jsp").forward(request, response);
            return;
        }

        // Prevent changing the role of the currently logged-in admin if it's themselves
        // Or prevent changing the last admin to a non-admin (more complex check needed for last admin)
        if (adminUser.getId() == userId && !role.equals("admin")) {
            User userToEdit = userDAO.getUserById(userId);
            request.setAttribute("userToEdit", userToEdit);
            request.setAttribute("errorMessage", "You cannot change your own role from admin.");
            request.getRequestDispatcher("/jsp/admin/editUser.jsp").forward(request, response);
            return;
        }


        User userToUpdate = new User();
        userToUpdate.setId(userId);
        userToUpdate.setUsername(username); // Keep original username
        userToUpdate.setEmail(email.trim());
        userToUpdate.setRole(role);

        if (userDAO.updateUserCoreDetails(userToUpdate)) {
            response.sendRedirect(request.getContextPath() + "/admin/manageUsers?successMessage=User+updated+successfully");
        } else {
            // Error message might be due to email uniqueness constraint or DB error
            User userToEdit = userDAO.getUserById(userId); // Re-fetch for form re-population
            request.setAttribute("userToEdit", userToEdit);
            request.setAttribute("errorMessage", "Failed to update user. Email might be in use by another account or a database error occurred.");
            request.getRequestDispatcher("/jsp/admin/editUser.jsp").forward(request, response);
        }
    }
}