<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:if test="${empty sessionScope.user || sessionScope.user.role != 'user'}">
    <c:redirect url="/login"/>
</c:if>

<c:set var="pageTitle" value="User Dashboard" scope="request"/>
<%@ include file="header.jsp" %>

<div class="container">
    <h2>User Dashboard</h2>
    <p>Welcome, ${sessionScope.user.username}!</p>

    <h3>Available Actions</h3>
    <ul>
        <li><a href="${pageContext.request.contextPath}/viewQuizzes">Browse and Take Quizzes</a></li>
        <li><a href="#">View My Progress</a> (Not Implemented)</li>
        <li><a href="#">View Leaderboard</a> </li>
    </ul>

    <p><em>Future features: Progress Tracking, Achievements, Recommended Quizzes, etc.</em></p>
</div>

<%@ include file="footer.jsp" %>
