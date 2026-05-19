<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<portlet:defineObjects />

<!DOCTYPE html>
<html>
<head>
    <title>Task Detail</title>
    <style>
        body { font-family: sans-serif; font-size: 14px; }
        .meta { color: #555; font-size: 12px; }
        dl dt { font-weight: bold; }
    </style>
</head>
<body>

<h2>Task Detail</h2>
<p class="meta">Window state: <strong>${windowState}</strong></p>

<c:choose>
    <c:when test="${empty selectedTaskId}">
        <p>Select a task in the <em>Task List</em> portlet to populate this detail view via IPC.</p>
    </c:when>
    <c:when test="${empty task}">
        <p>Task <code>${selectedTaskId}</code> was not found in the application-scoped catalog.</p>
    </c:when>
    <c:otherwise>
        <p class="meta">
            Driven by public render parameter / event:
            <code>selectedTaskId=${selectedTaskId}</code>
        </p>
        <dl>
            <dt>ID</dt><dd>${task.id}</dd>
            <dt>Title</dt><dd>${task.title}</dd>
            <dt>Description</dt><dd>${task.description}</dd>
            <dt>Assignee</dt><dd>${task.assignee}</dd>
            <dt>Status</dt><dd>${task.status}</dd>
            <dt>Created</dt><dd>${task.createdAt}</dd>
        </dl>
    </c:otherwise>
</c:choose>

</body>
</html>
