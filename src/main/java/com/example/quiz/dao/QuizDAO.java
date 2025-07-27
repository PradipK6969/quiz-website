package com.example.quiz.dao;

import com.example.quiz.model.Quiz;
import com.example.quiz.model.Question;
import com.example.quiz.model.Option;
import com.example.quiz.model.LeaderboardEntry;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuizDAO {

    public int createQuiz(Quiz quiz) {
        String sql = "INSERT INTO quizzes (title, description, category, created_by_admin_id, duration_minutes) VALUES (?, ?, ?, ?, ?) RETURNING id";
        int quizId = 0;
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, quiz.getTitle());
            pstmt.setString(2, quiz.getDescription());
            pstmt.setString(3, quiz.getCategory());
            pstmt.setInt(4, quiz.getCreatedByAdminId());
            if (quiz.getDurationMinutes() > 0) {
                pstmt.setInt(5, quiz.getDurationMinutes());
            } else {
                pstmt.setNull(5, Types.INTEGER);
            }
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                quizId = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return quizId;
    }

    public List<Quiz> getAllQuizzes() {
        List<Quiz> quizzes = new ArrayList<>();
        String sql = "SELECT q.*, u.username as admin_username FROM quizzes q LEFT JOIN users u ON q.created_by_admin_id = u.id ORDER BY q.created_at DESC";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Quiz quiz = new Quiz();
                quiz.setId(rs.getInt("id"));
                quiz.setTitle(rs.getString("title"));
                quiz.setDescription(rs.getString("description"));
                quiz.setCategory(rs.getString("category"));
                quiz.setCreatedByAdminId(rs.getInt("created_by_admin_id"));
                quiz.setCreatedAt(rs.getTimestamp("created_at"));
                quiz.setDurationMinutes(rs.getInt("duration_minutes")); // Ensure this column exists
                // You might want to display admin_username if fetched
                quizzes.add(quiz);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return quizzes;
    }

    // ... (other existing methods) ...

    public List<Quiz> getRecommendedQuizzes(int currentQuizId, int currentUserId, int limit) {
        List<Quiz> recommendedQuizzes = new ArrayList<>();
        // This SQL query is a bit complex. It aims to:
        // 1. Find users (other_users) who took the currentQuizId.
        // 2. Find other quizzes (recommended_quiz_id) that these other_users also took.
        // 3. Exclude the currentQuizId itself from recommendations.
        // 4. Exclude quizzes the currentUserId has already attempted.
        // 5. Count occurrences of these recommended_quiz_id and order by most frequent.
        // 6. Limit the results.
        String sql = "SELECT DISTINCT q.id, q.title, q.description, q.category, q.duration_minutes, q.created_at, COUNT(other_qa.quiz_id) as recommendation_strength " +
                "FROM quiz_attempts cur_qa " +
                "JOIN users cur_user ON cur_qa.user_id = cur_user.id " +
                "JOIN quiz_attempts other_qa ON cur_qa.user_id = other_qa.user_id " + // Users who took same quizzes
                "JOIN quizzes q ON other_qa.quiz_id = q.id " +
                "WHERE cur_qa.quiz_id = ? " + // Focus on users who took the CURRENT quiz
                "  AND other_qa.quiz_id != ? " + // Don't recommend the current quiz itself
                "  AND other_qa.quiz_id NOT IN (SELECT qa_user.quiz_id FROM quiz_attempts qa_user WHERE qa_user.user_id = ?) " + // Don't recommend quizzes current user already took
                "GROUP BY q.id, q.title, q.description, q.category, q.duration_minutes, q.created_at " +
                "ORDER BY recommendation_strength DESC, q.created_at DESC " +
                "LIMIT ?";

        // A simpler alternative if the above is too complex or slow on large datasets initially:
        // Find users who took currentQuizId.
        // For each of those users, get other quizzes they took.
        // Aggregate and count in Java. This might be less efficient for DB but easier to write.
        // For now, let's try the more direct SQL.

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, currentQuizId);
            pstmt.setInt(2, currentQuizId);
            pstmt.setInt(3, currentUserId);
            pstmt.setInt(4, limit);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Quiz quiz = new Quiz();
                quiz.setId(rs.getInt("id"));
                quiz.setTitle(rs.getString("title"));
                quiz.setDescription(rs.getString("description"));
                quiz.setCategory(rs.getString("category"));
                quiz.setDurationMinutes(rs.getInt("duration_minutes"));
                quiz.setCreatedAt(rs.getTimestamp("created_at"));
                // We don't need createdByAdminId for this display usually
                recommendedQuizzes.add(quiz);
            }
        } catch (SQLException e) {
            System.err.println("Error getting recommended quizzes: " + e.getMessage());
            e.printStackTrace();
        }
        return recommendedQuizzes;
    }

    public Quiz getQuizById(int quizId) {
        Quiz quiz = null;
        String sql = "SELECT * FROM quizzes WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, quizId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                quiz = new Quiz();
                quiz.setId(rs.getInt("id"));
                quiz.setTitle(rs.getString("title"));
                quiz.setDescription(rs.getString("description"));
                quiz.setCategory(rs.getString("category"));
                quiz.setCreatedAt(rs.getTimestamp("created_at"));
                quiz.setCreatedByAdminId(rs.getInt("created_by_admin_id"));
                quiz.setDurationMinutes(rs.getInt("duration_minutes"));
                quiz.setQuestions(getQuestionsForQuiz(quizId, conn));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return quiz;
    }

    // ... (other existing methods) ...

    public boolean updateQuiz(Quiz quiz) {
        String sql = "UPDATE quizzes SET title = ?, description = ?, category = ?, duration_minutes = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, quiz.getTitle());
            pstmt.setString(2, quiz.getDescription());
            pstmt.setString(3, quiz.getCategory());
            if (quiz.getDurationMinutes() > 0) {
                pstmt.setInt(4, quiz.getDurationMinutes());
            } else {
                pstmt.setNull(4, Types.INTEGER);
            }
            pstmt.setInt(5, quiz.getId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating quiz with ID " + quiz.getId() + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private List<Question> getQuestionsForQuiz(int quizId, Connection conn) throws SQLException {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT * FROM questions WHERE quiz_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, quizId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Question question = new Question();
                question.setId(rs.getInt("id"));
                question.setQuizId(rs.getInt("quiz_id"));
                question.setQuestionText(rs.getString("question_text"));
                question.setQuestionType(rs.getString("question_type"));
                question.setOptions(getOptionsForQuestion(question.getId(), conn));
                questions.add(question);
            }
        }
        return questions;
    }

    // ... (other existing methods in QuizDAO) ...

    public boolean deleteQuiz(int quizId) {
        String sql = "DELETE FROM quizzes WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, quizId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting quiz with ID " + quizId + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private List<Option> getOptionsForQuestion(int questionId, Connection conn) throws SQLException {
        List<Option> options = new ArrayList<>();
        String sql = "SELECT * FROM options WHERE question_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, questionId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Option option = new Option();
                option.setId(rs.getInt("id"));
                option.setQuestionId(rs.getInt("question_id"));
                option.setOptionText(rs.getString("option_text"));
                option.setCorrect(rs.getBoolean("is_correct"));
                options.add(option);
            }
        }
        return options;
    }

    public List<LeaderboardEntry> getTopQuizAttempts(int limit) {
        List<LeaderboardEntry> leaderboard = new ArrayList<>();
        // This query gets the number of questions per quiz.
        // A more complex query or multiple queries might be needed if total questions aren't easily derived.
        // For simplicity, we count questions for each quiz being displayed.
        // Or better, store total questions at the time of attempt if quiz structure can change.
        // For this example, we fetch quiz and then count its questions.
        // This can be inefficient. A better approach is to store total questions in quiz_attempts.
        // For now:
        String sql = "SELECT u.username, q.title AS quiz_title, qa.score, qa.completed_at, " +
                "(SELECT COUNT(*) FROM questions WHERE quiz_id = q.id) AS total_questions " +
                "FROM quiz_attempts qa " +
                "JOIN users u ON qa.user_id = u.id " +
                "JOIN quizzes q ON qa.quiz_id = q.id " +
                "WHERE qa.completed_at IS NOT NULL " +
                "ORDER BY qa.score DESC, qa.completed_at ASC " + // Higher score first, then earlier completion
                "LIMIT ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                LeaderboardEntry entry = new LeaderboardEntry();
                entry.setUsername(rs.getString("username"));
                entry.setQuizTitle(rs.getString("quiz_title"));
                entry.setScore(rs.getInt("score"));
                entry.setTotalQuestions(rs.getInt("total_questions"));
                entry.setCompletedAt(rs.getTimestamp("completed_at"));
                leaderboard.add(entry);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return leaderboard;
    }
}