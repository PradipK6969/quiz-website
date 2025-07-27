<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:if test="${empty sessionScope.user || sessionScope.user.role != 'admin'}">
    <c:redirect url="${pageContext.request.contextPath}/login"/>
</c:if>
<c:if test="${empty userToEdit}">
    <c:redirect url="${pageContext.request.contextPath}/admin/manageUsers?errorMessage=UserDataNotAvailable"/>
</c:if>

<c:set var="pageTitle" value="Edit User: ${userToEdit.username}" scope="request"/>
<%@ include file="/jsp/header.jsp" %>

<div class="container form-container">
    <h2>Edit User: <c:out value="${userToEdit.username}"/></h2>

    <c:if test="${not empty errorMessage}">
        <p class="message error-message"><c:out value="${errorMessage}"/></p>
    </c:if>

    <form action="${pageContext.request.contextPath}/admin/editUser" method="post">
        <input type="hidden" name="userId" value="${userToEdit.id}">

        <div class="form-group">
            <label for="username">Username (cannot change):</label>
            <input type="text" id="username" name="username" value="<c:out value="${userToEdit.username}"/>" readonly class="form-control-plaintext">
        </div>

        <div class="form-group">
            <label for="email">Email:</label>
            <input type="email" id="email" name="email" value="<c:out value="${userToEdit.email}"/>" required>
        </div>

        <div class="form-group">
            <label for="role">Role:</label>
            <select id="role" name="role" required <c:if test="${sessionScope.user.id == userToEdit.id}">disabled title="Cannot change your own role"</c:if> >
                <option value="user" ${userToEdit.role == 'user' ? 'selected' : ''}>User</option>
                <option value="admin" ${userToEdit.role == 'admin' ? 'selected' : ''}>Admin</option>
            </select>
            <c:if test="${sessionScope.user.id == userToEdit.id}">
                <input type="hidden" name="role" value="${userToEdit.role}"> <%-- Submit original role if disabled --%>
                <small class="form-text text-muted">You cannot change your own role.</small>
            </c:if>
        </div>

        <div class="form-group">
            <button type="submit" class="btn btn-primary btn-block">Save Changes</button>
        </div>
    </form>
    <p class="text-center"><a href="${pageContext.request.contextPath}/admin/manageUsers">Cancel and Back to User List</a></p>
</div>
<style>
    .form-control-plaintext {
        display: block;
        width: 100%;
        padding: 0.8em 0; /* Adjust to match input padding if needed */
        margin-bottom: 0;
        line-height: 1.6;
        color: #495057;
        background-color: transparent;
        border: solid transparent;
        border-width: 1px 0;
    }
</style>
<%@ include file="/jsp/footer.jsp" %>