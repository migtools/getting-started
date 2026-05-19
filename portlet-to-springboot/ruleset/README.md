# Portlet → Spring Boot Kantra Ruleset

Kantra static-analysis rules for migrating **JSR 286 (Portlet 2.0)** applications on **JBoss Portal / GateIn** to **Spring Boot 4.x**.

Rules were generated with the **Konveyor Scribe MCP** (`executeKantraOperation`) and aligned with [portlet-2-springboot.md](../../portlet-2-springboot.md).

## Ruleset structure

| File | Coverage |
|------|----------|
| `ruleset.yaml` | Ruleset metadata and labels |
| `00-portlet-dependencies-to-springboot.yaml` | portlet-api removal, Spring Boot parent, WAR→JAR, logging |
| `01-portlet-lifecycle-to-springmvc.yaml` | GenericPortlet, doView/doEdit/doHelp, processAction, serveResource, processEvent |
| `02-portlet-ipc-state-preferences.yaml` | Render params, events, preferences, sessions, modes, window states |
| `03-portlet-config-descriptors.yaml` | portlet.xml, jboss-portlet.xml, liferay, instances, pages, jboss-app |
| `04-portlet-jsp-to-thymeleaf.yaml` | Portlet 2.0 taglibs, JSTL → Thymeleaf |
| `05-portlet-javax-to-jakarta.yaml` | javax.servlet, javax.persistence, javax.annotation |
| `06-portlet-security-session.yaml` | Portal principal, SecurityFilterChain, Spring Session |
| `07-portlet-service-data-testing.yaml` | @Service extraction, JPA, tests, actuator, Docker |

**45 rules** across 8 rule files (35 generated via Scribe MCP + 10 supplemental rules for JSTL, javax.annotation, security, testing, and deployment).

## Run analysis

From the repository root:

```bash
kantra analyze \
  --input portlet-to-springboot \
  --output /tmp/portlet-analysis \
  --rules portlet-to-springboot/ruleset/
```

Or point at a single rule file:

```bash
kantra analyze \
  --input portlet-to-springboot \
  --output /tmp/portlet-analysis \
  --rules portlet-to-springboot/ruleset/01-portlet-lifecycle-to-springmvc.yaml
```

## Migration mapping (spec summary)

| Portlet concept | Spring Boot target |
|-----------------|-------------------|
| GenericPortlet / Portlet | `@Controller` / `@RestController` |
| VIEW / EDIT / HELP | `@GetMapping` routes |
| processAction | `@PostMapping` + redirect |
| serveResource | REST endpoints |
| processEvent / setEvent | `ApplicationEvent` / messaging |
| Public render parameters | `@RequestParam` / `@PathVariable` |
| PortletPreferences | JPA `UserPreference` + service |
| PortletSession | HTTP session / Spring Session / `@Service` |
| portlet.xml init-param | `application.yml` + `@ConfigurationProperties` |
| JSP portlet tags | Thymeleaf + Spring MVC URLs |
| Portal security | Spring Security |
| WAR on JBoss Portal | Executable JAR / container image |

## References

- [portlet-2-springboot.md](../../portlet-2-springboot.md) — migration specification
- [portlet-to-springboot/README.md](../README.md) — sample legacy portlet application
