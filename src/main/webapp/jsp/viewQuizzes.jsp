<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:if test="${empty sessionScope.user}">
    <c:redirect url="/login"/>
</c:if>

<c:set var="pageTitle" value="Available Quizzes" scope="request"/>
<%@ include file="header.jsp" %>

<div class="container quiz-list-container">
    <h2>Available Quizzes</h2>

    <%-- Display messages from URL parameters --%>
    <c:if test="${not empty param.errorMessage}">
        <p class="message error-message"><c:out value="${param.errorMessage}"/></p>
    </c:if>
    <c:if test="${not empty param.successMessage}">
        <p class="message success-message"><c:out value="${param.successMessage}"/></p>
    </c:if>
     <c:if test="${not empty param.warningMessage}">
        <p class="message warning-message"><c:out value="${param.warningMessage}"/></p> <%-- For create quiz with no questions --%>
    </c:if>


    <c:choose>
        <c:when test="${not empty quizzes}">
            <%-- ... rest of the quiz list rendering (no changes here) ... --%>
            <ul class="quiz-list">
                <c:forEach var="quiz" items="${quizzes}">
                    <li class="quiz-card">
                        <h3><c:out value="${quiz.title}"/></h3>
                        <p class="quiz-category"><c:out value="${quiz.category != null ? quiz.category : 'General'}"/></p>
                        <p class="quiz-description"><c:out value="${quiz.description != null ? quiz.description : 'No description available.'}"/></p>
                        <div class="quiz-meta">
                            Created:
                            <c:if test="${not empty quiz.createdAt}">
                                <jsp:useBean id="quizCreateDate" class="java.util.Date" scope="page"/>
                                <c:set target="${quizCreateDate}" property="time" value="${quiz.createdAt.time}"/>
                                <fmt:formatDate value="${quizCreateDate}" pattern="MMM dd, yyyy"/>
                            </c:if>
                            <br>
                            <c:if test="${quiz.durationMinutes > 0}">
                                Duration: <span><c:out value="${quiz.durationMinutes}"/> minutes</span>
                            </c:if>
                            <c:if test="${quiz.durationMinutes <= 0 || empty quiz.durationMinutes}">
                                Duration: <span>No time limit</span>
                            </c:if>
                        </div>
                        <div class="quiz-actions">
                            <a href="${pageContext.request.contextPath}/takeQuiz?quizId=${quiz.id}" class="btn btn-primary">Take Quiz</a>
                            <c:if test="${sessionScope.user.role == 'admin'}">
                                <a href="${pageContext.request.contextPath}/editQuiz?quizId=${quiz.id}" class="btn btn-secondary">Edit</a>
                                <a href="${pageContext.request.contextPath}/deleteQuiz?quizId=${quiz.id}" class="btn btn-danger" onclick="return confirm('Are you sure you want to delete this quiz?');">Delete</a>
                            </c:if>
                        </div>
                    </li>
                </c:forEach>
            </ul>
        </c:when>
        <c:otherwise>
            <div class="text-center" style="padding: 30px; background-color: #fff8f0; border-radius: 8px;">
                <img src="${pageContext.request.contextPath}/images/no-quiz.svg" alt="No quizzes found" style="width: 150px; margin-bottom: 20px; opacity: 0.7;">
                <h3>No Quizzes Yet!</h3>
                <p>It looks like there are no quizzes available at the moment.</p>
                <c:if test="${sessionScope.user.role == 'admin'}">
                    <p>Why not <a href="${pageContext.request.contextPath}/createQuiz">create the first one</a>?</p>
                </c:if>
            </div>
        </c:otherwise>
    </c:choose>
</div>
<%@ include file="footer.jsp" %>