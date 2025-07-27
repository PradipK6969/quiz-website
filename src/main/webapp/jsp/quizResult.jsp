<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:if test="${empty sessionScope.user}">
    <c:redirect url="/login"/>
</c:if>

<c:set var="pageTitle" value="Quiz Results" scope="request"/>
<%@ include file="header.jsp" %>

<div class="container quiz-result-container">
    <h2>Quiz Results: ${quiz.title}</h2>
    <h3>Your Score: ${score} / ${totalQuestions}</h3>

    <div class="score-summary">
        <p>Congratulations, ${sessionScope.user.username}! You have completed the quiz.</p>
        <%-- Add more detailed feedback or score breakdown here later --%>
    </div>

    <h4>Review Your Answers:</h4>
    <c:forEach var="question" items="${quiz.questions}" varStatus="loop">
        <div class="question-review-item <c:if test="${!answerCorrectness[question.id]}">incorrect-answer</c:if> <c:if test="${answerCorrectness[question.id]}">correct-answer</c:if>">
            <p><strong>Question ${loop.count}: ${question.questionText}</strong></p>
            <ul>
                <c:forEach var="option" items="${question.options}">
                    <li
                        <c:if test="${option.id == userAnswers[question.id]}">class="selected-option"</c:if>
                        <c:if test="${option.correct}">style="font-weight: bold; color: green;"</c:if>
                    >
                        ${option.optionText}
                        <c:if test="${option.id == userAnswers[question.id] && !option.correct}"> (Your answer - Incorrect)</c:if>
                        <c:if test="${option.id == userAnswers[question.id] && option.correct}"> (Your answer - Correct!)</c:if>
                        <c:if test="${option.id != userAnswers[question.id] && option.correct}"> (Correct Answer)</c:if>
                    </li>
                </c:forEach>
            </ul>
            <c:if test="${!answerCorrectness[question.id]}">
                <p class="explanation"><em>Explanation for incorrect answer would go here.</em></p>
            </c:if>
        </div>
    </c:forEach>
<%-- ... (existing score display and answer review) ... --%>

    <c:if test="${not empty recommendedQuizzes}">
        <div class="recommendations-section" style="margin-top: 40px; padding-top:30px; border-top: 1px solid #e0dcd1;">
            <h3>You Might Also Like...</h3>
            <ul class="quiz-list" style="grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 20px;">
                <c:forEach var="recQuiz" items="${recommendedQuizzes}">
                    <li class="quiz-card">
                        <h4><c:out value="${recQuiz.title}"/></h4>
                        <p class="quiz-category" style="font-size: 0.8em;"><c:out value="${recQuiz.category != null ? recQuiz.category : 'General'}"/></p>
                        <p class="quiz-description" style="font-size: 0.9em; margin-bottom:10px;"><c:out value="${recQuiz.description != null ? recQuiz.description.substring(0, Math.min(recQuiz.description.length(), 100)) : 'No description.'}"/>
                            <c:if test="${recQuiz.description != null && recQuiz.description.length() > 100}">...</c:if>
                        </p>
                        <div class="quiz-actions">
                             <a href="${pageContext.request.contextPath}/takeQuiz?quizId=${recQuiz.id}" class="btn btn-primary btn-sm">Try this Quiz</a>
                        </div>
                    </li>
                </c:forEach>
            </ul>
        </div>
    </c:if>

    <div class="result-actions" style="margin-top: 30px;">
        <a href="${pageContext.request.contextPath}/viewQuizzes" class="btn btn-secondary">Back to Quizzes</a>
        <a href="${pageContext.request.contextPath}/userDashboard" class="btn btn-secondary">My Dashboard</a>
    </div>

</div> <%-- End of .quiz-result-container --%>

<%@ include file="footer.jsp" %>
    <a href="${pageContext.request.contextPath}/viewQuizzes" class="button">Back to Quizzes</a>
    <a href="${pageContext.request.contextPath}/userDashboard" class="button">Back to Dashboard</a>

</div>

<%@ include file="footer.jsp" %>
