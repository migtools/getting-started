package com.redhat.mta.examples.portlet;

import com.redhat.mta.examples.portlet.event.TaskEvents;
import com.redhat.mta.examples.portlet.model.Task;
import com.redhat.mta.examples.portlet.service.TaskCatalogService;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.Event;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.WindowState;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Locale;

/**
 * Primary JSR 286 portlet demonstrating VIEW/EDIT/HELP modes, preferences,
 * portlet-scoped sessions, render parameters, resource URLs, and event-based IPC.
 */
public class TaskListPortlet extends GenericPortlet {

    public static final String PREF_MAX_RESULTS = "maxResults";
    public static final String PREF_DEFAULT_ASSIGNEE = "defaultAssignee";
    public static final String SESSION_LAST_ACTION = "lastAction";
    public static final String INIT_PARAM_CATALOG_TITLE = "catalogTitle";

    @Override
    protected void doView(RenderRequest request, RenderResponse response)
            throws PortletException, IOException {

        TaskCatalogService catalog = TaskCatalogService.getOrCreate(getPortletContext());
        int maxResults = getMaxResults(request);
        List<Task> tasks = catalog.listTasks(maxResults);

        request.setAttribute("tasks", tasks);
        request.setAttribute("catalogTitle", getCatalogTitle());
        request.setAttribute("maxResults", Integer.valueOf(maxResults));
        request.setAttribute("totalTaskCount", Integer.valueOf(catalog.countTasks()));
        request.setAttribute("windowState", request.getWindowState().toString());
        request.setAttribute("portletMode", request.getPortletMode().toString());
        request.setAttribute("selectedTaskId", getPublicTaskId(request));
        request.setAttribute("lastAction", getPortletSessionAttribute(request, SESSION_LAST_ACTION));

        includeJsp("/jsp/tasklist/view.jsp", request, response);
    }

    @Override
    protected void doEdit(RenderRequest request, RenderResponse response)
            throws PortletException, IOException {

        PortletPreferences prefs = request.getPreferences();
        request.setAttribute("maxResults", prefs.getValue(PREF_MAX_RESULTS, "10"));
        request.setAttribute("defaultAssignee", prefs.getValue(PREF_DEFAULT_ASSIGNEE, "unassigned"));
        includeJsp("/jsp/tasklist/edit.jsp", request, response);
    }

    @Override
    protected void doHelp(RenderRequest request, RenderResponse response)
            throws PortletException, IOException {
        includeJsp("/jsp/tasklist/help.jsp", request, response);
    }

    @Override
    public void processAction(ActionRequest request, ActionResponse response)
            throws PortletException, IOException {

        String action = request.getParameter("action");
        TaskCatalogService catalog = TaskCatalogService.getOrCreate(getPortletContext());
        PortletSession session = request.getPortletSession();

        if ("savePreferences".equals(action)) {
            PortletPreferences prefs = request.getPreferences();
            prefs.setValue(PREF_MAX_RESULTS, request.getParameter(PREF_MAX_RESULTS));
            prefs.setValue(PREF_DEFAULT_ASSIGNEE, request.getParameter(PREF_DEFAULT_ASSIGNEE));
            prefs.store();
            session.setAttribute(SESSION_LAST_ACTION, "Saved portlet preferences", PortletSession.PORTLET_SCOPE);
            response.setPortletMode(PortletMode.VIEW);
            return;
        }

        if ("addTask".equals(action)) {
            String title = request.getParameter("title");
            String description = request.getParameter("description");
            String assignee = request.getParameter("assignee");
            if (assignee == null || assignee.trim().isEmpty()) {
                assignee = request.getPreferences().getValue(PREF_DEFAULT_ASSIGNEE, "unassigned");
            }
            Task created = catalog.addTask(title, description, assignee);
            session.setAttribute(SESSION_LAST_ACTION, "Created task " + created.getId(), PortletSession.PORTLET_SCOPE);
            publishTaskSelection(request, response, created.getId());
            return;
        }

        if ("selectTask".equals(action)) {
            String taskId = request.getParameter("taskId");
            session.setAttribute(SESSION_LAST_ACTION, "Selected task " + taskId, PortletSession.PORTLET_SCOPE);
            publishTaskSelection(request, response, taskId);
            return;
        }

        if ("completeTask".equals(action)) {
            String taskId = request.getParameter("taskId");
            Task task = catalog.getTask(taskId);
            if (task != null) {
                task.setStatus(Task.Status.DONE);
                catalog.updateTask(task);
            }
            session.setAttribute(SESSION_LAST_ACTION, "Completed task " + taskId, PortletSession.PORTLET_SCOPE);
            publishTaskSelection(request, response, taskId);
        }
    }

    @Override
    public void processEvent(EventRequest request, EventResponse response)
            throws PortletException, IOException {

        Event event = request.getEvent();
        if (TaskEvents.TASK_SELECTED.equals(event.getQName())) {
            String taskId = (String) event.getValue();
            request.getPortletSession().setAttribute(
                    SESSION_LAST_ACTION,
                    "Received taskSelected event for " + taskId,
                    PortletSession.PORTLET_SCOPE);
        }
    }

    @Override
    public void serveResource(ResourceRequest request, ResourceResponse response)
            throws PortletException, IOException {

        response.setContentType("application/json");

        TaskCatalogService catalog = TaskCatalogService.getOrCreate(getPortletContext());
        String taskId = request.getParameter("taskId");

        PrintWriter writer = response.getWriter();
        if (taskId != null && !taskId.isEmpty()) {
            Task task = catalog.getTask(taskId);
            if (task == null) {
                writer.write("{\"error\":\"not_found\"}");
            } else {
                writer.write(toJson(task));
            }
        } else {
            writer.write("{\"count\":" + catalog.countTasks() + "}");
        }
        writer.flush();
    }

    private void publishTaskSelection(ActionRequest request, ActionResponse response, String taskId)
            throws IOException {

        response.setRenderParameter(TaskEvents.PUBLIC_RENDER_PARAM_TASK_ID, taskId);

        response.setEvent(TaskEvents.TASK_SELECTED, taskId);
    }

    private int getMaxResults(PortletRequest request) {
        try {
            return Integer.parseInt(request.getPreferences().getValue(PREF_MAX_RESULTS, "10"));
        } catch (NumberFormatException ex) {
            return 10;
        }
    }

    private String getCatalogTitle() {
        return getInitParameter(INIT_PARAM_CATALOG_TITLE);
    }

    private String getPublicTaskId(RenderRequest request) {
        return request.getParameter(TaskEvents.PUBLIC_RENDER_PARAM_TASK_ID);
    }

    private String getPortletSessionAttribute(RenderRequest request, String name) {
        PortletSession session = request.getPortletSession();
        Object value = session.getAttribute(name, PortletSession.PORTLET_SCOPE);
        return value != null ? value.toString() : null;
    }

    private void includeJsp(String path, RenderRequest request, RenderResponse response)
            throws PortletException, IOException {
        response.setContentType("text/html");
        getPortletContext().getRequestDispatcher(path).include(request, response);
    }

    private static String toJson(Task task) {
        return "{"
                + "\"id\":\"" + escape(task.getId()) + "\","
                + "\"title\":\"" + escape(task.getTitle()) + "\","
                + "\"status\":\"" + task.getStatus().name() + "\","
                + "\"assignee\":\"" + escape(task.getAssignee()) + "\""
                + "}";
    }

    private static String escape(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    @Override
    public void init() throws PortletException {
        super.init();
        getPortletContext().log("TaskListPortlet initialized for locale " + Locale.getDefault());
    }
}
