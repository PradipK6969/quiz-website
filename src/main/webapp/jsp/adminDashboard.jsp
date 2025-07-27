<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:if test="${empty sessionScope.user || sessionScope.user.role != 'admin'}">
    <c:redirect url="/login"/>
</c:if>

<c:set var="pageTitle" value="Admin Dashboard" scope="request"/>
<%@ include file="header.jsp" %>

<div class="container">
    <h2>Admin Dashboard</h2>
    <p>Welcome, ${sessionScope.user.username}!</p>

    <c:if test="${not empty param.message}">
        <p class="success-message">${param.message}</p>
    </c:if>
     <c:if test="${not empty requestScope.message}">
        <p class="success-message">${requestScope.message}</p>
    </c:if>


    <h3>Quiz Management</h3>
    <ul>
        <li><a href="${pageContext.request.contextPath}/createQuiz">Create New Quiz</a></li>
        <li><a href="${pageContext.request.contextPath}/viewQuizzes">View/Manage Quizzes</a> (View All Quizzes for now)</li>
        <li><a href="${pageContext.request.contextPath}/admin/manageUsers">Manage Users</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/viewStatistics">View Statistics</a></li>
    </ul>

    <p><em>Future features: Edit/Delete Quizzes, Question Bank Management, User Management, etc.</em></p>
</div>

<%@ include file="footer.jsp" %>
