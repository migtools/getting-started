package com.redhat.mta.examples.portlet;

import com.redhat.mta.examples.portlet.event.TaskEvents;
import com.redhat.mta.examples.portlet.model.Task;
import com.redhat.mta.examples.portlet.service.TaskCatalogService;

import javax.portlet.Event;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import java.io.IOException;

/**
 * Companion portlet that consumes inter-portlet communication via
 * public render parameters and portlet events from {@link TaskListPortlet}.
 */
public class TaskDetailPortlet extends GenericPortlet {

    public static final String SESSION_SELECTED_TASK_ID = "selectedTaskId";

    @Override
    protected void doView(RenderRequest request, RenderResponse response)
            throws PortletException, IOException {

        String taskId = resolveSelectedTaskId(request);
        Task task = null;
        if (taskId != null) {
            TaskCatalogService catalog = TaskCatalogService.getOrCreate(getPortletContext());
            task = catalog.getTask(taskId);
        }

        request.setAttribute("selectedTaskId", taskId);
        request.setAttribute("task", task);
        request.setAttribute("windowState", request.getWindowState().toString());

        response.setContentType("text/html");
        getPortletContext().getRequestDispatcher("/jsp/taskdetail/view.jsp").include(request, response);
    }

    @Override
    public void processEvent(EventRequest request, EventResponse response)
            throws PortletException, IOException {

        Event event = request.getEvent();
        if (TaskEvents.TASK_SELECTED.equals(event.getQName())) {
            String taskId = (String) event.getValue();
            request.getPortletSession().setAttribute(
                    SESSION_SELECTED_TASK_ID,
                    taskId,
                    PortletSession.PORTLET_SCOPE);
            response.setRenderParameter(TaskEvents.PUBLIC_RENDER_PARAM_TASK_ID, taskId);
        }
    }

    private String resolveSelectedTaskId(RenderRequest request) {
        String fromRenderParam = request.getParameter(TaskEvents.PUBLIC_RENDER_PARAM_TASK_ID);
        if (fromRenderParam != null && !fromRenderParam.isEmpty()) {
            return fromRenderParam;
        }
        PortletSession session = request.getPortletSession();
        Object fromSession = session.getAttribute(SESSION_SELECTED_TASK_ID, PortletSession.PORTLET_SCOPE);
        return fromSession != null ? fromSession.toString() : null;
    }
}
