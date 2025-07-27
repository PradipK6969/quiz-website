package com.example.quiz.dao;

import com.example.quiz.model.User;
// import com.example.quiz.util.PasswordUtil; // Not strictly needed if not hashing

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        User user = null;
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password")); // Retrieve stored plain text password
                user.setEmail(rs.getString("email"));
                user.setRole(rs.getString("role"));
                user.setCreatedAt(rs.getTimestamp("created_at"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    // In src/main/java/com/example/quiz/dao/UserDAO.java
// ... (other methods) ...

    public boolean updateUserCoreDetails(User user) {
        // This method updates email and role. Username and password are not updated here.
        // Check if the new email is already taken by another user
        User userByNewEmail = getUserByEmail(user.getEmail());
        if (userByNewEmail != null && userByNewEmail.getId() != user.getId()) {
            System.err.println("Update failed: Email '" + user.getEmail() + "' is already in use by another user.");
            return false; // Email taken by someone else
        }

        String sql = "UPDATE users SET email = ?, role = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getEmail());
            pstmt.setString(2, user.getRole());
            pstmt.setInt(3, user.getId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating user core details for ID " + user.getId() + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ... (existing methods: getUserByUsername, createUser, getUserByEmail, getUserById) ...

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, username, email, role, created_at FROM users ORDER BY id ASC"; // Don't fetch password
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setRole(rs.getString("role"));
                user.setCreatedAt(rs.getTimestamp("created_at"));
                // user.setPassword(null); // Explicitly set password to null or don't set it
                users.add(user);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all users: " + e.getMessage());
            e.printStackTrace();
        }
        return users;
    }

    public boolean deleteUser(int userId) {
        // Consider implications: what happens to quizzes created by an admin if admin is deleted?
        // What about quiz attempts by a user if user is deleted?
        // Our current schema uses ON DELETE SET NULL for quizzes.created_by_admin_id
        // and ON DELETE CASCADE for quiz_attempts.user_id. This is important.
        // You might want to prevent deletion of an admin if they are the only admin.

        // Prevent deleting the currently logged-in admin themselves (optional safety)
        // This check would be better in the servlet, but as a DAO guard:
        // User currentUser = //... get current user (not straightforward in DAO)
        // if (currentUser.getId() == userId && currentUser.getRole().equals("admin")) {
        //     System.err.println("Admin cannot delete themselves.");
        //     return false;
        // }


        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting user with ID " + userId + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean createUser(User user) {
        if (getUserByUsername(user.getUsername()) != null) {
            System.err.println("Username already exists: " + user.getUsername());
            return false;
        }
        if (getUserByEmail(user.getEmail()) != null) {
            System.err.println("Email already exists: " + user.getEmail());
            return false;
        }

        String sql = "INSERT INTO users (username, password, email, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            // STORE PLAIN TEXT PASSWORD - SECURITY RISK
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, "user");

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ... (existing methods: getUserByUsername, createUser, getUserByEmail, getUserById) ...


    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        User user = null;
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setEmail(rs.getString("email"));
                user.setRole(rs.getString("role"));
                user.setCreatedAt(rs.getTimestamp("created_at"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public User getUserById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        User user = null;
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setRole(rs.getString("role"));
                user.setCreatedAt(rs.getTimestamp("created_at"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }
}