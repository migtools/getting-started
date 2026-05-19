package com.redhat.mta.examples.portlet.service;

import com.redhat.mta.examples.portlet.model.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * In-memory task catalog shared across portlet instances (APPLICATION_SCOPE).
 * Legacy portlet apps often keep business data in portlet/session scope rather than a service tier.
 */
public final class TaskCatalogService {

    public static final String APPLICATION_SCOPE_KEY = "com.redhat.mta.examples.portlet.TASK_CATALOG";

    private final Map<String, Task> tasks = new LinkedHashMap<String, Task>();
    private final AtomicInteger idSequence = new AtomicInteger(100);

    public TaskCatalogService() {
        addTask("Review quarterly reports", "Compile Q1 metrics for leadership review.", "alice");
        addTask("Update portal theme", "Apply corporate branding to GateIn theme.", "bob");
        addTask("Migrate order service", "Extract OrderServiceMDB to Spring Boot.", "carol");
    }

    public static TaskCatalogService getOrCreate(javax.portlet.PortletContext portletContext) {
        TaskCatalogService catalog = (TaskCatalogService) portletContext.getAttribute(APPLICATION_SCOPE_KEY);
        if (catalog == null) {
            catalog = new TaskCatalogService();
            portletContext.setAttribute(APPLICATION_SCOPE_KEY, catalog);
        }
        return catalog;
    }

    public Task addTask(String title, String description, String assignee) {
        String id = "TASK-" + idSequence.incrementAndGet();
        Task task = new Task(id, title, description, assignee);
        tasks.put(id, task);
        return task;
    }

    public Task getTask(String id) {
        return tasks.get(id);
    }

    public List<Task> listTasks(int maxResults) {
        List<Task> all = new ArrayList<Task>(tasks.values());
        Collections.reverse(all);
        if (maxResults > 0 && all.size() > maxResults) {
            return all.subList(0, maxResults);
        }
        return all;
    }

    public void updateTask(Task task) {
        if (task != null && tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    public int countTasks() {
        return tasks.size();
    }
}
