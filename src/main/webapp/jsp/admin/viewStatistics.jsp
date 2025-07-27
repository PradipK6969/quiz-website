<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:if test="${empty sessionScope.user || sessionScope.user.role != 'admin'}">
    <c:redirect url="${pageContext.request.contextPath}/login"/>
</c:if>

<c:set var="pageTitle" value="View Statistics" scope="request"/>
<%@ include file="/jsp/header.jsp" %>

<div class="container statistics-container">
    <h2>Quiz Platform Statistics</h2>

    <div class="stats-grid">
        <div class="stat-card">
            <h3>Total Users</h3>
            <p class="stat-value">${totalUsers}</p>
        </div>
        <div class="stat-card">
            <h3>Total Quizzes</h3>
            <p class="stat-value">${totalQuizzes}</p>
        </div>
        <div class="stat-card">
            <h3>Total Quiz Attempts</h3>
            <p class="stat-value">${totalAttempts}</p>
        </div>
    </div>

    <c:if test="${not empty attemptsPerQuiz}">
        <h3 style="margin-top: 30px;">Attempts Per Quiz:</h3>
        <table class="leaderboard-table" style="margin-top: 10px;">
             <thead>
                <tr>
                    <th>Quiz Title</th>
                    <th>Number of Attempts</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="entry" items="${attemptsPerQuiz}">
                    <tr>
                        <td><c:out value="${entry.key}"/></td>
                        <td>${entry.value}</td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:if>
     <p class="mt-3"><a href="${pageContext.request.contextPath}/adminDashboard" class="btn btn-secondary">Back to Admin Dashboard</a></p>
</div>

<style>
    .stats-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
        gap: 20px;
        margin-bottom: 30px;
    }
    .stat-card {
        background-color: #fff;
        padding: 20px;
        border-radius: 10px;
        text-align: center;
        box-shadow: 0 5px 15px rgba(0,0,0,0.07);
        border-top: 4px solid #e67e22; /* Accent color */
    }
    .stat-card h3 {
        font-size: 1.2em;
        color: #5d5b53;
        margin-bottom: 10px;
    }
    .stat-card .stat-value {
        font-size: 2.5em;
        font-weight: 700;
        color: #e67e22;
        margin-bottom: 0;
    }
</style>

<%@ include file="/jsp/footer.jsp" %>