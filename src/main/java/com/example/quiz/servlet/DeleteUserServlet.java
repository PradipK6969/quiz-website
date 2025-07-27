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

@WebServlet("/admin/deleteUser")
public class DeleteUserServlet extends HttpServlet {
    private UserDAO userDAO;

    @Override
    public void init() {
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Prefer POST for delete, but allow GET for simplicity with current link structure
        handleDelete(request, response);
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        handleDelete(request, response);
    }


    private void handleDelete(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        HttpSession session = request.getSession(false);
        User currentAdmin = (session != null) ? (User) session.getAttribute("user") : null;

        if (currentAdmin == null || !"admin".equals(currentAdmin.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login?error=Unauthorized");
            return;
        }

        String userIdStr = request.getParameter("userId");
        if (userIdStr == null || userIdStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/manageUsers?errorMessage=InvalidUserIdForDelete");
            return;
        }

        try {
            int userIdToDelete = Integer.parseInt(userIdStr);

            // Safety check: Prevent admin from deleting themselves
            if (currentAdmin.getId() == userIdToDelete) {
                response.sendRedirect(request.getContextPath() + "/admin/manageUsers?errorMessage=Admin+cannot+delete+their+own+account.");
                return;
            }

            // Optional: Add a check to prevent deleting the last admin user.
            // This would require fetching all admins and checking count.
            // List<User> admins = userDAO.getAllUsers().stream().filter(u -> "admin".equals(u.getRole())).collect(Collectors.toList());
            // User userToDelete = userDAO.getUserById(userIdToDelete); // Fetch user to check their role
            // if (userToDelete != null && "admin".equals(userToDelete.getRole()) && admins.size() <= 1) {
            //    response.sendRedirect(request.getContextPath() + "/admin/manageUsers?errorMessage=Cannot+delete+the+last+admin+account.");
            //    return;
            // }


            boolean success = userDAO.deleteUser(userIdToDelete);

            if (success) {
                response.sendRedirect(request.getContextPath() + "/admin/manageUsers?successMessage=User+deleted+successfully");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/manageUsers?errorMessage=Failed+to+delete+user.");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/manageUsers?errorMessage=InvalidUserIdFormatForDelete");
        }
    }
}