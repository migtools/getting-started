package com.redhat.mta.examples.portlet.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Simple task entity used by the legacy portlet demo.
 */
public class Task implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum Status {
        OPEN, IN_PROGRESS, DONE
    }

    private final String id;
    private String title;
    private String description;
    private String assignee;
    private Status status;
    private final Date createdAt;

    public Task(String id, String title, String description, String assignee) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.assignee = assignee;
        this.status = Status.OPEN;
        this.createdAt = new Date();
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }
}
