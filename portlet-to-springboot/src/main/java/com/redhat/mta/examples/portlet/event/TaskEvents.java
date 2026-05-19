package com.redhat.mta.examples.portlet.event;

import javax.xml.namespace.QName;

/**
 * QName constants for portlet event-based inter-portlet communication (IPC).
 */
public final class TaskEvents {

    public static final String NAMESPACE = "http://konveyor.example.com/portlet/events";

    public static final QName TASK_SELECTED = new QName(NAMESPACE, "taskSelected");

    public static final String PUBLIC_RENDER_PARAM_TASK_ID = "selectedTaskId";

    private TaskEvents() {
    }
}
