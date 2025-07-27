<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %> <%-- Add this line --%>

<c:set var="pageTitle" value="Login" scope="request"/>
<%@ include file="header.jsp" %>

<div class="container login-container">
    <h2>Login</h2>
    <c:if test="${not empty errorMessage}">
        <p class="error-message">${errorMessage}</p>
    </c:if>
    <form action="${pageContext.request.contextPath}/login" method="post">
        <div>
            <label for="username">Username:</label>
            <input type="text" id="username" name="username" required>
        </div>
        <div>
            <label for="password">Password:</label>
            <input type="password" id="password" name="password" required>
        </div>
        <div>
            <button type="submit">Login</button>
        </div>
    </form>
    <p>Don't have an account? <a href="${pageContext.request.contextPath}/register">Register here</a></p>
</div>

<%@ include file="footer.jsp" %>