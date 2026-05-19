<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects />

<!DOCTYPE html>
<html>
<head>
    <title>Task List Help</title>
    <style>
        body { font-family: sans-serif; font-size: 14px; line-height: 1.5; }
        code { background: #f4f4f4; padding: 0.1rem 0.25rem; }
    </style>
</head>
<body>

<h2>HELP mode</h2>
<p>This portlet demonstrates legacy JSR 286 concepts targeted for Spring Boot migration:</p>
<ul>
    <li><strong>VIEW / EDIT / HELP</strong> portlet modes</li>
    <li><strong>NORMAL / MAXIMIZED / MINIMIZED</strong> window states (portal chrome)</li>
    <li><strong>Portlet preferences</strong> in EDIT mode</li>
    <li><strong>Portlet sessions</strong> (<code>PORTLET_SCOPE</code>, <code>APPLICATION_SCOPE</code> catalog)</li>
    <li><strong>Public render parameters</strong> for inter-portlet communication</li>
    <li><strong>Portlet events</strong> (<code>task:taskSelected</code>)</li>
    <li><strong>Resource URLs</strong> via <code>serveResource()</code> for AJAX/JSON</li>
</ul>

<p>
    <portlet:renderURL portletMode="view">Back to task list</portlet:renderURL>
</p>

</body>
</html>
