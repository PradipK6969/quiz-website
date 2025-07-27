<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="pageTitle" value="Register" scope="request"/>
<%@ include file="header.jsp" %>

<div class="container register-container">
    <h2>Register New Account</h2>
    <c:if test="${not empty errorMessage}">
        <p class="error-message">${errorMessage}</p>
    </c:if>
     <c:if test="${not empty successMessage}">
        <p class="success-message">${successMessage}</p>
    </c:if>

    <form action="${pageContext.request.contextPath}/register" method="post">
        <div>
            <label for="username">Username:</label>
            <input type="text" id="username" name="username" value="${param.username}" required>
        </div>
        <div>
            <label for="email">Email:</label>
            <input type="email" id="email" name="email" value="${param.email}" required>
        </div>
        <div>
            <label for="password">Password:</label>
            <input type="password" id="password" name="password" required>
        </div>
        <div>
            <label for="confirmPassword">Confirm Password:</label>
            <input type="password" id="confirmPassword" name="confirmPassword" required>
        </div>
        <div>
            <button type="submit">Register</button>
        </div>
    </form>
    <p>Already have an account? <a href="${pageContext.request.contextPath}/login">Login here</a></p>
</div>

<%@ include file="footer.jsp" %>