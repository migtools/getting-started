# JSR 286 Portlet Demo (JBoss Portal)

Legacy **JSR 286 (Portlet 2.0)** application packaged as a WAR for deployment to **JBoss Portal Server** / **GateIn**. This module represents the *current state* described in [portlet-2-springboot.md](../portlet-2-springboot.md) and is the starting point for portlet-to-Spring Boot migration exercises.

## Technology Stack

| Component | Technology |
|-----------|------------|
| Portlet API | JSR 286 (`javax.portlet.*`) |
| Portal | JBoss Portal Server / GateIn |
| Java | 8 (legacy) |
| Views | JSP + Portlet 2.0 tag library |
| Packaging | WAR |
| Build | Maven |

## Portlets

| Portlet | Class | Purpose |
|---------|-------|---------|
| **Task List** | `TaskListPortlet` | VIEW / EDIT / HELP modes, preferences, sessions, resource URLs, event publishing |
| **Task Detail** | `TaskDetailPortlet` | Consumes public render parameters and `taskSelected` events from Task List |

## JSR 286 Features Demonstrated

- **Portlet classes** extending `GenericPortlet`
- **Portlet modes**: VIEW, EDIT, HELP
- **Window states**: NORMAL, MAXIMIZED, MINIMIZED (portal-managed)
- **Render parameters**: public `selectedTaskId` for inter-portlet communication
- **Portlet preferences**: `maxResults`, `defaultAssignee` (EDIT mode)
- **Portlet sessions**: `PORTLET_SCOPE` (last action), `APPLICATION_SCOPE` (shared task catalog)
- **Resource serving**: `serveResource()` returns JSON for a task
- **Event-based IPC**: `task:taskSelected` portlet event

## Configuration Files

| File | Location | Role |
|------|----------|------|
| `portlet.xml` | `WEB-INF/` | JSR 286 portlet deployment descriptor |
| `jboss-portlet.xml` | `WEB-INF/` | JBoss/GateIn portlet extensions and instances |
| `jboss-app.xml` | `WEB-INF/` | Portal application name (`taskportal`) |
| `web.xml` | `WEB-INF/` | Servlet WAR descriptor |
| `portlet-instances.xml` | `WEB-INF/` | Portlet instance definitions |
| `taskportal-pages.xml` | `WEB-INF/` | Sample portal page layout |

## Project Structure

```
portlet-to-springboot/
├── pom.xml
├── README.md
└── src/main/
    ├── java/com/redhat/mta/examples/portlet/
    │   ├── TaskListPortlet.java
    │   ├── TaskDetailPortlet.java
    │   ├── event/TaskEvents.java
    │   ├── model/Task.java
    │   └── service/TaskCatalogService.java
    ├── resources/
    │   └── portlet-messages.properties
    └── webapp/
        ├── index.jsp
        ├── jsp/tasklist/     # view, edit, help
        ├── jsp/taskdetail/
        └── WEB-INF/
            ├── portlet.xml
            ├── jboss-portlet.xml
            ├── jboss-app.xml
            ├── portlet-instances.xml
            ├── taskportal-pages.xml
            └── web.xml
```

## Build

```bash
cd portlet-to-springboot
mvn clean package
```

Artifact: `target/taskportal.war`

## Deploy to JBoss Portal / GateIn

1. Copy `target/taskportal.war` to the portal deployment directory (varies by distribution; often `deploy/` or the management console).
2. Register the **task-dashboard** page if not auto-discovered (see `taskportal-pages.xml`).
3. Place **Task List** and **Task Detail** portlets on the same page to exercise IPC.

Instance reference format:

```
taskportal.TaskListPortlet.TaskListInstance
taskportal.TaskDetailPortlet.TaskDetailInstance
```

## Local Development Notes

Portlets require a portlet container; they cannot be exercised with `mvn tomcat7:run` alone. Use a JBoss Portal / GateIn distribution or an OpenShift/Kubernetes image that includes the portal runtime.

For migration analysis, run MTA/Konveyor against this module with target **Spring Boot** and rules that map portlet APIs, JSP views, and deployment descriptors to Spring MVC / Thymeleaf equivalents.

## Migration rules (Kantra)

Static-analysis rules for portlet → Spring Boot 4.x migration live in [`ruleset/`](ruleset/):

```bash
kantra analyze --input portlet-to-springboot --output /tmp/portlet-analysis --rules portlet-to-springboot/ruleset/
```

See [ruleset/README.md](ruleset/README.md) for the full rule catalog.

## Related Documentation

- [portlet-2-springboot.md](../portlet-2-springboot.md) — full migration specification
