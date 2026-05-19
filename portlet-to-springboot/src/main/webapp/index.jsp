<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Task Portal WAR</title>
    <style>
        body { font-family: sans-serif; margin: 2rem; }
        code { background: #f4f4f4; padding: 0.1rem 0.3rem; }
    </style>
</head>
<body>
<h1>Task Portal (JSR 286)</h1>
<p>
    This WAR is intended for deployment to <strong>JBoss Portal Server</strong> or
    <strong>GateIn</strong>. Portlets are not rendered when accessing the WAR directly;
    add the application to the portal and open the <em>task-dashboard</em> page.
</p>
<ul>
    <li><code>TaskListPortlet</code> &mdash; VIEW / EDIT / HELP modes, preferences, resource URLs</li>
    <li><code>TaskDetailPortlet</code> &mdash; inter-portlet communication via render parameters and events</li>
</ul>
</body>
</html>
