<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<portlet:defineObjects />

<!DOCTYPE html>
<html>
<head>
    <title>${catalogTitle}</title>
    <style>
        body { font-family: sans-serif; font-size: 14px; }
        table { border-collapse: collapse; width: 100%; }
        th, td { border: 1px solid #ccc; padding: 0.4rem 0.6rem; text-align: left; }
        th { background: #eee; }
        .meta { color: #555; font-size: 12px; margin-bottom: 1rem; }
        .actions form { display: inline; }
    </style>
</head>
<body>

<h2>${catalogTitle}</h2>

<p class="meta">
    Mode: <strong>${portletMode}</strong> |
    Window: <strong>${windowState}</strong> |
    Showing ${tasks.size()} of ${totalTaskCount} tasks (max ${maxResults}) |
    <c:if test="${not empty lastAction}">Last action: ${lastAction}</c:if>
</p>

<c:if test="${not empty selectedTaskId}">
    <p class="meta">Public render parameter <code>selectedTaskId</code> = <strong>${selectedTaskId}</strong></p>
</c:if>

<portlet:actionURL var="addTaskUrl">
    <portlet:param name="action" value="addTask"/>
</portlet:actionURL>

<form action="${addTaskUrl}" method="post">
    <fieldset>
        <legend>Add task</legend>
        <label>Title <input type="text" name="title" required="required"/></label>
        <label>Description <input type="text" name="description"/></label>
        <label>Assignee <input type="text" name="assignee" placeholder="optional"/></label>
        <button type="submit">Add</button>
    </fieldset>
</form>

<table>
    <thead>
    <tr>
        <th>ID</th>
        <th>Title</th>
        <th>Assignee</th>
        <th>Status</th>
        <th>Actions</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${tasks}" var="task">
        <tr>
            <td>${task.id}</td>
            <td>${task.title}</td>
            <td>${task.assignee}</td>
            <td>${task.status}</td>
            <td class="actions">
                <portlet:actionURL var="selectUrl">
                    <portlet:param name="action" value="selectTask"/>
                    <portlet:param name="taskId" value="${task.id}"/>
                </portlet:actionURL>
                <form action="${selectUrl}" method="post" style="display:inline">
                    <button type="submit">Select (IPC)</button>
                </form>

                <portlet:actionURL var="completeUrl">
                    <portlet:param name="action" value="completeTask"/>
                    <portlet:param name="taskId" value="${task.id}"/>
                </portlet:actionURL>
                <form action="${completeUrl}" method="post" style="display:inline">
                    <button type="submit">Complete</button>
                </form>

                <portlet:resourceURL var="resourceUrl">
                    <portlet:param name="taskId" value="${task.id}"/>
                </portlet:resourceURL>
                <a href="${resourceUrl}" target="_blank" rel="noopener">JSON</a>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>

<p class="meta">
    Switch to <portlet:renderURL portletMode="edit">EDIT</portlet:renderURL> mode for portlet preferences, or
    <portlet:renderURL portletMode="help">HELP</portlet:renderURL> for documentation.
</p>

</body>
</html>
