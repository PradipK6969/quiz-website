<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:if test="${empty sessionScope.user}">
    <c:redirect url="/login"/>
</c:if>

<c:set var="pageTitle" value="Leaderboard" scope="request"/>
<%@ include file="header.jsp" %>

<div class="container leaderboard-container">
    <h2>Quiz Champions - Top Scores!</h2>

    <c:choose>
        <c:when test="${not empty leaderboardEntries}">
            <table class="leaderboard-table">
                <thead>
                    <tr>
                        <th>Rank</th>
                        <th>Player</th>
                        <th>Quiz</th>
                        <th>Score</th>
                        <th>Completed On</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="entry" items="${leaderboardEntries}" varStatus="loop">
                        <tr>
                            <td>${loop.count}</td>
                            <td><c:out value="${entry.username}"/></td>
                            <td><c:out value="${entry.quizTitle}"/></td>
                            <td>${entry.score} / ${entry.totalQuestions}</td>
                            <td>
                                <fmt:formatDate value="${entry.completedAt}" pattern="MMM dd, yyyy 'at' HH:mm"/>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:when>
        <c:otherwise>
            <div class="text-center" style="padding: 30px; background-color: #fff8f0; border-radius: 8px;">
                 <%-- Image for empty state - ensure it exists or remove --%>
                 <img src="${pageContext.request.contextPath}/images/no-leaderboard.svg" alt="Leaderboard empty" style="width: 150px; margin-bottom: 20px; opacity: 0.7;">
                <h3>The Leaderboard is Eager for Champions!</h3>
                <p>No high scores recorded yet. Take a quiz and claim your spot!</p>
            </div>
        </c:otherwise>
    </c:choose>
    <p class="text-center mt-3"><a href="${pageContext.request.contextPath}/viewQuizzes" class="btn btn-secondary">Browse Quizzes</a></p>
</div>

<%@ include file="footer.jsp" %>