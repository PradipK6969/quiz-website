<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:if test="${empty sessionScope.user || sessionScope.user.role != 'admin'}">
    <c:redirect url="${pageContext.request.contextPath}/login"/>
</c:if>

<c:set var="pageTitle" value="Manage Users" scope="request"/>
<%@ include file="/jsp/header.jsp" %> <%-- CORRECTED PATH --%>

<div class="container user-management-container">
    <h2>User Management</h2>

    <c:if test="${not empty successMessage}">
        <p class="message success-message"><c:out value="${successMessage}"/></p>
    </c:if>
    <c:if test="${not empty errorMessage}">
        <p class="message error-message"><c:out value="${errorMessage}"/></p>
    </c:if>

    <c:choose>
        <c:when test="${not empty users}">
            <table class="table-modern leaderboard-table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Username</th>
                        <th>Email</th>
                        <th>Role</th>
                        <th>Registered On</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="user" items="${users}">
                        <tr>
                            <td>${user.id}</td>
                            <td><c:out value="${user.username}"/></td>
                            <td><c:out value="${user.email}"/></td>
                            <td><span class="role-badge role-${user.role}"><c:out value="${user.role}"/></span></td>
                            <td>
                                <fmt:formatDate value="${user.createdAt}" pattern="MMM dd, yyyy HH:mm"/>
                            </td>
                            <td>
<%-- ... existing delete link ... --%>
<a href="${pageContext.request.contextPath}/admin/editUser?userId=${user.id}" class="btn btn-sm btn-secondary" style="margin-right: 5px;">Edit</a>
<c:if test="${user.id != loggedInAdminId}">
    <a href="${pageContext.request.contextPath}/admin/deleteUser?userId=${user.id}"
       class="btn btn-sm btn-danger"
       onclick="return confirm('Are you sure you want to delete user \'${user.username}\'? This action cannot be undone and will delete their quiz attempts.');">
        Delete
    </a>
</c:if>
<c:if test="${user.id == loggedInAdminId}">
    <button class="btn btn-sm btn-danger" disabled title="Cannot delete your own account">Delete</button>
</c:if>

                                <c:if test="${user.id != loggedInAdminId}">
                                    <a href="${pageContext.request.contextPath}/admin/deleteUser?userId=${user.id}"
                                       class="btn btn-sm btn-danger"
                                       onclick="return confirm('Are you sure you want to delete user \'${user.username}\'? This action cannot be undone and will delete their quiz attempts.');">
                                        Delete
                                    </a>
                                </c:if>
                                <c:if test="${user.id == loggedInAdminId}">
                                    <button class="btn btn-sm btn-danger" disabled title="Cannot delete your own account">Delete</button>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:when>
        <c:otherwise>
            <p>No users found (except possibly yourself if you are the only admin).</p>
        </c:otherwise>
    </c:choose>
     <p class="mt-3"><a href="${pageContext.request.contextPath}/adminDashboard" class="btn btn-secondary">Back to Admin Dashboard</a></p>
</div>

<%@ include file="/jsp/footer.jsp" %> <%-- CORRECTED PATH --%>