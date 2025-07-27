package com.example.quiz.servlet;

import com.example.quiz.dao.QuizDAO;
import com.example.quiz.model.Quiz;
import com.example.quiz.model.Question;
import com.example.quiz.model.Option;
import com.example.quiz.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Collections;

@WebServlet("/takeQuiz")
public class TakeQuizServlet extends HttpServlet {
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

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String quizIdStr = request.getParameter("quizId");
        if (quizIdStr == null || quizIdStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/viewQuizzes?error=NoQuizIdProvided");
            return;
        }

        try {
            int quizId = Integer.parseInt(quizIdStr);
            Quiz quiz = quizDAO.getQuizById(quizId);

            if (quiz != null && quiz.getQuestions() != null && !quiz.getQuestions().isEmpty()) {
                // Shuffle questions
                Collections.shuffle(quiz.getQuestions());

                // Shuffle options for each MCQ question
                for (Question question : quiz.getQuestions()) {
                    if ("MCQ".equals(question.getQuestionType()) && question.getOptions() != null) {
                        Collections.shuffle(question.getOptions());
                    }
                }

                request.setAttribute("quiz", quiz);
                request.getRequestDispatcher("/jsp/takeQuiz.jsp").forward(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/viewQuizzes?error=QuizNotFoundOrEmpty");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/viewQuizzes?error=InvalidQuizId");
        }
    }
}