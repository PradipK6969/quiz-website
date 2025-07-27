package com.example.quiz.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class StatisticsDAO {

    public long getTotalUsers() {
        String sql = "SELECT COUNT(*) FROM users";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public long getTotalQuizzes() {
        String sql = "SELECT COUNT(*) FROM quizzes";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public long getTotalQuizAttempts() {
        String sql = "SELECT COUNT(*) FROM quiz_attempts";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // More stats can be added here
    // Example: Get quiz attempt counts per quiz
    public Map<String, Long> getAttemptsPerQuiz() {
        Map<String, Long> attemptsMap = new HashMap<>();
        String sql = "SELECT q.title, COUNT(qa.id) as attempt_count " +
                "FROM quizzes q LEFT JOIN quiz_attempts qa ON q.id = qa.quiz_id " +
                "GROUP BY q.title ORDER BY attempt_count DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                attemptsMap.put(rs.getString("title"), rs.getLong("attempt_count"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return attemptsMap;
    }
}