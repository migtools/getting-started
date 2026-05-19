<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects />

<!DOCTYPE html>
<html>
<head>
    <title>Portlet Preferences</title>
    <style>
        body { font-family: sans-serif; font-size: 14px; }
        label { display: block; margin: 0.5rem 0; }
        input { margin-left: 0.5rem; }
    </style>
</head>
<body>

<h2>Portlet Preferences (EDIT mode)</h2>
<p>User-specific configuration stored via <code>PortletPreferences</code>.</p>

<portlet:actionURL var="savePrefsUrl">
    <portlet:param name="action" value="savePreferences"/>
</portlet:actionURL>

<form action="${savePrefsUrl}" method="post">
    <label>
        Max results
        <input type="number" name="maxResults" value="${maxResults}" min="1" max="100"/>
    </label>
    <label>
        Default assignee
        <input type="text" name="defaultAssignee" value="${defaultAssignee}"/>
    </label>
    <button type="submit">Save preferences</button>
</form>

<p>
    <portlet:renderURL portletMode="view">Return to VIEW mode</portlet:renderURL>
</p>

</body>
</html>
