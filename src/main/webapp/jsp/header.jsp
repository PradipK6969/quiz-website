<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${not empty pageTitle ? pageTitle : 'QuizMaster'}</title> <%-- Changed default title --%>

    <%-- Google Fonts --%>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&family=Pacifico&display=swap" rel="stylesheet">

    <%-- Your Stylesheet --%>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <header>
        <div class="header-content">
            <h1 class="site-title site-title-heading">
                <a href="${pageContext.request.contextPath}/">QuizMaster</a>
            </h1>
            <nav>
                <a href="${pageContext.request.contextPath}/" <c:if test="${pageContext.request.servletPath == '/index.jsp' || pageContext.request.servletPath == '/login'}">class="active"</c:if>>Home</a>

                <c:if test="${not empty sessionScope.user}">
                    <%-- Display welcome message for logged-in users --%>
                    <span class="nav-welcome-msg">Hi, ${sessionScope.user.username}!</span>

                    <c:choose>
                        <c:when test="${sessionScope.user.role == 'admin'}">
                            <a href="${pageContext.request.contextPath}/adminDashboard" <c:if test="${pageContext.request.servletPath == '/adminDashboard'}">class="active"</c:if>>Admin Panel</a>
                        </c:when>
                        <c:otherwise>
                            <a href="${pageContext.request.contextPath}/userDashboard" <c:if test="${pageContext.request.servletPath == '/userDashboard'}">class="active"</c:if>>My Dashboard</a>
                        </c:otherwise>
                    </c:choose>

                    <a href="${pageContext.request.contextPath}/viewQuizzes" <c:if test="${pageContext.request.servletPath == '/viewQuizzes' || pageContext.request.servletPath == '/takeQuiz'}">class="active"</c:if>>Quizzes</a>
                    <a href="${pageContext.request.contextPath}/leaderboard" <c:if test="${pageContext.request.servletPath == '/leaderboard'}">class="active"</c:if>>Leaderboard</a>
                    <a href="${pageContext.request.contextPath}/logout" class="logout-button">Logout</a>
                </c:if>

                <c:if test="${empty sessionScope.user}">
                    <%-- Display login/register for guests --%>
                    <a href="${pageContext.request.contextPath}/login" <c:if test="${pageContext.request.servletPath == '/login'}">class="active"</c:if>>Login</a>
                    <a href="${pageContext.request.contextPath}/register" class="btn-primary-nav" <c:if test="${pageContext.request.servletPath == '/register'}">class="active"</c:if>>Sign Up</a>
                </c:if>
            </nav>
        </div>
    </header>
    <main>
        <%-- The content of individual JSPs will be injected here --%>