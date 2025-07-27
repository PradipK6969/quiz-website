package com.example.quiz.dao;

import com.example.quiz.model.Question;
import com.example.quiz.model.Option;

import java.sql.*;
import java.util.List;

public class QuestionDAO {

    public int addQuestionToQuiz(Question question) {
        String sqlQuestion = "INSERT INTO questions (quiz_id, question_text, question_type) VALUES (?, ?, ?) RETURNING id";
        int questionId = 0;
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false); // Start transaction
            try (PreparedStatement pstmtQuestion = conn.prepareStatement(sqlQuestion)) {
                pstmtQuestion.setInt(1, question.getQuizId());
                pstmtQuestion.setString(2, question.getQuestionText());
                pstmtQuestion.setString(3, question.getQuestionType());
                ResultSet rs = pstmtQuestion.executeQuery();
                if (rs.next()) {
                    questionId = rs.getInt(1);
                    question.setId(questionId); // Set generated ID to question object

                    // Add options if it's an MCQ and options are provided
                    if ("MCQ".equals(question.getQuestionType()) && question.getOptions() != null) {
                        addOptionsForQuestion(question, conn);
                    }
                    conn.commit(); // Commit transaction
                } else {
                    conn.rollback();
                }
            } catch (SQLException e) {
                conn.rollback(); // Rollback on error
                e.printStackTrace();
                throw e; // Re-throw to indicate failure
            } finally {
                conn.setAutoCommit(true); // Reset auto-commit
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return questionId;
    }

    private void addOptionsForQuestion(Question question, Connection conn) throws SQLException {
        String sqlOption = "INSERT INTO options (question_id, option_text, is_correct) VALUES (?, ?, ?)";
        try (PreparedStatement pstmtOption = conn.prepareStatement(sqlOption)) {
            for (Option option : question.getOptions()) {
                pstmtOption.setInt(1, question.getId());
                pstmtOption.setString(2, option.getOptionText());
                pstmtOption.setBoolean(3, option.isCorrect());
                pstmtOption.addBatch();
            }
            pstmtOption.executeBatch();
        }
    }

    public Option getOptionById(int optionId) {
        Option option = null;
        String sql = "SELECT * FROM options WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, optionId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                option = new Option();
                option.setId(rs.getInt("id"));
                option.setQuestionId(rs.getInt("question_id"));
                option.setOptionText(rs.getString("option_text"));
                option.setCorrect(rs.getBoolean("is_correct"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return option;
    }

    public int saveQuizAttempt(int userId, int quizId, int score) {
        String sql = "INSERT INTO quiz_attempts (user_id, quiz_id, score, completed_at) VALUES (?, ?, ?, CURRENT_TIMESTAMP) RETURNING id";
        int attemptId = 0;
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, quizId);
            pstmt.setInt(3, score);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                attemptId = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return attemptId;
    }

    // ... (existing methods like addQuestionToQuiz, getOptionById, saveQuizAttempt, saveUserAnswer) ...

    public boolean updateQuestion(Question question) {
        // This simplified update first deletes old options, then adds new ones.
        // A more granular update would check which options changed, were added, or removed.
        String sqlUpdateQuestion = "UPDATE questions SET question_text = ?, question_type = ? WHERE id = ?";

        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false); // Start transaction

            // 1. Update question text and type
            try (PreparedStatement pstmtUpdate = conn.prepareStatement(sqlUpdateQuestion)) {
                pstmtUpdate.setString(1, question.getQuestionText());
                pstmtUpdate.setString(2, question.getQuestionType());
                pstmtUpdate.setInt(3, question.getId());
                pstmtUpdate.executeUpdate();
            }

            // 2. Delete existing options for this question
            deleteOptionsForQuestion(question.getId(), conn);

            // 3. Add new/updated options (if any)
            if ("MCQ".equals(question.getQuestionType()) && question.getOptions() != null && !question.getOptions().isEmpty()) {
                addOptionsForQuestion(question, conn); // Re-use the existing method
            }

            conn.commit(); // Commit transaction
            return true;
        } catch (SQLException e) {
            System.err.println("Error updating question with ID " + question.getId() + ": " + e.getMessage());
            try (Connection conn = DBUtil.getConnection()) { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace();}
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteQuestion(int questionId) {
        // ON DELETE CASCADE should handle options, but good to be explicit or have a separate method if not.
        String sql = "DELETE FROM questions WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, questionId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting question with ID " + questionId + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Helper method to delete options, used in updateQuestion
    private void deleteOptionsForQuestion(int questionId, Connection conn) throws SQLException {
        String sqlDeleteOptions = "DELETE FROM options WHERE question_id = ?";
        try (PreparedStatement pstmtDeleteOptions = conn.prepareStatement(sqlDeleteOptions)) {
            pstmtDeleteOptions.setInt(1, questionId);
            pstmtDeleteOptions.executeUpdate();
        }
    }

// Ensure addOptionsForQuestion (already existing) uses the provided connection for transactions
/*
    private void addOptionsForQuestion(Question question, Connection conn) throws SQLException {
        String sqlOption = "INSERT INTO options (question_id, option_text, is_correct) VALUES (?, ?, ?)";
        try (PreparedStatement pstmtOption = conn.prepareStatement(sqlOption)) {
            for (Option option : question.getOptions()) {
                pstmtOption.setInt(1, question.getId());
                pstmtOption.setString(2, option.getOptionText());
                pstmtOption.setBoolean(3, option.isCorrect());
                pstmtOption.addBatch();
            }
            pstmtOption.executeBatch();
        }
    }
*/
// The `addOptionsForQuestion` method from a previous step should already be suitable if it takes a Connection argument.
// If not, it needs to be modified to accept and use a passed-in Connection.
// The existing `addQuestionToQuiz` also handles options within a transaction context.

    public void saveUserAnswer(int attemptId, int questionId, int selectedOptionId, boolean isCorrect) {
        String sql = "INSERT INTO user_answers (attempt_id, question_id, selected_option_id, is_correct) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, attemptId);
            pstmt.setInt(2, questionId);
            pstmt.setInt(3, selectedOptionId);
            pstmt.setBoolean(4, isCorrect);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
