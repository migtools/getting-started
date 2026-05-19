# Migration Specification: JSR 286 Portlet to Spring Boot 4.x

## Executive Summary

This document outlines the migration strategy for moving a Java Portlet application (JSR 286) running on JBoss Portal Server to a modern Spring Boot 4.x application.

---

## 1. Current State Analysis

### Technology Stack
- **Portlet Specification**: JSR 286 (Portlet 2.0)
- **Portal Server**: JBoss Portal Server (GateIn/JBoss Enterprise Portal Platform)
- **Java Version**: Java 6/7/8 (legacy)
- **Presentation Layer**: JSP (JavaServer Pages)
- **Portlet API**: javax.portlet.* (Portlet 2.0 API)
- **Deployment**: WAR files deployed to portal container

### Key Portlet Components
- **Portlet Classes**: Classes extending `GenericPortlet` or implementing `Portlet` interface
- **Portlet Modes**: VIEW, EDIT, HELP modes
- **Window States**: NORMAL, MAXIMIZED, MINIMIZED
- **Render Parameters**: Inter-portlet communication (IPC)
- **Portlet Preferences**: User-specific configuration storage
- **Portlet Sessions**: Scoped sessions (APPLICATION_SCOPE, PORTLET_SCOPE)
- **Resource Serving**: AJAX/resource URLs via `serveResource()`
- **Event-based IPC**: Portlet events for communication

### Configuration Files
- **portlet.xml**: Portlet deployment descriptor
- **liferay-portlet.xml** or **jboss-portlet.xml**: Vendor-specific configuration
- **web.xml**: Web application deployment descriptor
- **portlet-instances.xml**: Portlet instance definitions

---

## 2. Target State

### Technology Stack
- **Framework**: Spring Boot 4.x
- **Java Version**: Java 21+ (LTS)
- **Spring Version**: Spring Framework 6.2+
- **Presentation Layer**: 
  - **Option 1**: Thymeleaf templates
  - **Option 2**: React/Vue/Angular SPA
  - **Option 3**: JSP (if legacy preservation required)
- **Build Tool**: Maven or Gradle
- **Deployment**: Standalone JAR with embedded Tomcat/Jetty

### Modern Architecture Patterns
- RESTful APIs with Spring MVC or Spring WebFlux
- Microservices architecture (if applicable)
- Stateless session management
- JWT/OAuth2 for authentication
- Modern frontend framework integration

---

## 3. Migration Strategy

### Approach: **Big Bang vs. Incremental**

**Recommended: Incremental Migration**
1. Extract business logic from portlet classes
2. Create REST APIs in Spring Boot
3. Migrate UI layer incrementally
4. Run portal and Spring Boot side-by-side during transition
5. Deprecate portal once all functionality migrated

---

## 4. Key Migration Challenges

### 4.1 Portlet-Specific Concepts

| Portlet Concept | Spring Boot Equivalent | Migration Notes |
|----------------|------------------------|-----------------|
| **Portlet Modes** (VIEW/EDIT/HELP) | Different endpoints or query parameters | Map to separate URLs or modal dialogs |
| **Window States** | Frontend state management | Handle in UI layer (maximize/minimize) |
| **Render Parameters** | Query parameters or path variables | Convert to standard HTTP parameters |
| **Portlet Preferences** | Database, Redis, or user preferences table | Migrate to persistent storage with user service |
| **Portlet Sessions** | Spring Sessions with Redis/JDBC | Use distributed session management |
| **Inter-Portlet Communication** | REST APIs or WebSockets | Replace with API calls or real-time messaging |
| **Portlet Events** | Spring Events or Message Queues | Use Spring ApplicationEvent or Kafka/RabbitMQ |
| **Resource Serving** | Static resources or REST endpoints | Use Spring MVC resource handlers |

### 4.2 JSP Migration

**Options:**
1. **Thymeleaf**: Modern server-side templating (recommended)
2. **Keep JSP**: Spring Boot supports JSP with additional configuration
3. **SPA Framework**: Complete UI rewrite with React/Vue/Angular

---

## 5. Technical Implementation Plan

### Phase 1: Analysis & Setup (Weeks 1-2)

#### Tasks:
1. **Code Inventory**
   - Catalog all portlet classes and their methods
   - Identify business logic vs. presentation logic
   - Map portlet modes and window states to use cases
   - Document inter-portlet communication patterns
   - List all portlet preferences and session attributes

2. **Dependency Analysis**
   - Identify third-party libraries
   - Check Spring Boot compatibility
   - Plan library upgrades (e.g., Hibernate, Jackson)

3. **Create Spring Boot Project**
   ```xml
   <parent>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-parent</artifactId>
       <version>4.0.0</version>
   </parent>
   
   <dependencies>
       <dependency>
           <groupId>org.springframework.boot</groupId>
           <artifactId>spring-boot-starter-web</artifactId>
       </dependency>
       <dependency>
           <groupId>org.springframework.boot</groupId>
           <artifactId>spring-boot-starter-thymeleaf</artifactId>
       </dependency>
       <dependency>
           <groupId>org.springframework.boot</groupId>
           <artifactId>spring-boot-starter-data-jpa</artifactId>
       </dependency>
       <dependency>
           <groupId>org.springframework.boot</groupId>
           <artifactId>spring-boot-starter-security</artifactId>
       </dependency>
   </dependencies>
   ```

### Phase 2: Business Logic Extraction (Weeks 3-5)

#### Tasks:
1. **Extract Service Layer**
   - Move business logic from portlet classes to `@Service` beans
   - Create clean interfaces
   - Implement dependency injection

   **Before (Portlet):**
   ```java
   public class MyPortlet extends GenericPortlet {
       @Override
       protected void doView(RenderRequest request, RenderResponse response) {
           // Business logic mixed with portlet code
           List<User> users = getUsersFromDatabase();
           request.setAttribute("users", users);
           getPortletContext().getRequestDispatcher("/view.jsp").include(request, response);
       }
   }
   ```

   **After (Spring Boot):**
   ```java
   @Service
   public class UserService {
       @Autowired
       private UserRepository userRepository;
       
       public List<User> getAllUsers() {
           return userRepository.findAll();
       }
   }
   
   @Controller
   @RequestMapping("/users")
   public class UserController {
       @Autowired
       private UserService userService;
       
       @GetMapping
       public String listUsers(Model model) {
           model.addAttribute("users", userService.getAllUsers());
           return "users/list";
       }
   }
   ```

2. **Data Access Layer**
   - Migrate to Spring Data JPA repositories
   - Update entity mappings for JPA 3.x (jakarta.persistence.*)
   - Configure data sources in application.properties

### Phase 3: API Development (Weeks 6-8)

#### Tasks:
1. **Create REST Controllers**
   ```java
   @RestController
   @RequestMapping("/api/v1")
   public class UserApiController {
       @Autowired
       private UserService userService;
       
       @GetMapping("/users")
       public ResponseEntity<List<UserDTO>> getUsers() {
           return ResponseEntity.ok(userService.getAllUsers());
       }
       
       @PostMapping("/users")
       public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO user) {
           return ResponseEntity.status(HttpStatus.CREATED)
               .body(userService.createUser(user));
       }
   }
   ```

2. **Replace Portlet Preferences**
   ```java
   @Entity
   @Table(name = "user_preferences")
   public class UserPreference {
       @Id
       @GeneratedValue(strategy = GenerationType.IDENTITY)
       private Long id;
       
       private String userId;
       private String preferenceKey;
       private String preferenceValue;
   }
   
   @Repository
   public interface UserPreferenceRepository extends JpaRepository<UserPreference, Long> {
       List<UserPreference> findByUserId(String userId);
       Optional<UserPreference> findByUserIdAndPreferenceKey(String userId, String key);
   }
   ```

### Phase 4: UI Migration (Weeks 9-12)

#### Option A: Thymeleaf Migration

1. **Convert JSP to Thymeleaf**
   
   **Before (JSP):**
   ```jsp
   <%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
   <portlet:defineObjects />
   
   <h1>${title}</h1>
   <ul>
       <c:forEach items="${users}" var="user">
           <li>${user.name}</li>
       </c:forEach>
   </ul>
   ```

   **After (Thymeleaf):**
   ```html
   <!DOCTYPE html>
   <html xmlns:th="http://www.thymeleaf.org">
   <head>
       <title th:text="${title}">User List</title>
   </head>
   <body>
       <h1 th:text="${title}">Title</h1>
       <ul>
           <li th:each="user : ${users}" th:text="${user.name}">User</li>
       </ul>
   </body>
   </html>
   ```

2. **Handle Portlet URLs**
   - Replace `<portlet:actionURL>` with Spring MVC URLs
   - Replace `<portlet:resourceURL>` with REST API endpoints
   - Update form submissions to use standard HTTP POST

#### Option B: SPA Migration

1. **Create React/Vue/Angular Frontend**
2. **Expose Backend APIs**
3. **Implement Client-Side Routing**
4. **Handle State Management**

### Phase 5: Security Migration (Weeks 13-14)

#### Tasks:
1. **Replace Portal Security**
   ```java
   @Configuration
   @EnableWebSecurity
   public class SecurityConfig {
       @Bean
       public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
           http
               .authorizeHttpRequests(auth -> auth
                   .requestMatchers("/api/public/**").permitAll()
                   .requestMatchers("/api/**").authenticated()
                   .anyRequest().authenticated()
               )
               .oauth2Login()
               .formLogin();
           return http.build();
       }
   }
   ```

2. **User Authentication**
   - Migrate from portal authentication to Spring Security
   - Implement JWT tokens for API access
   - Configure OAuth2/OIDC if required

### Phase 6: Session Management (Week 15)

#### Tasks:
1. **Replace Portlet Sessions**
   ```yaml
   # application.yml
   spring:
     session:
       store-type: redis
       redis:
         namespace: spring:session
   ```

2. **Distributed Sessions**
   - Use Spring Session with Redis/JDBC
   - Migrate session attributes to stateless tokens where possible

### Phase 7: Testing (Weeks 16-18)

#### Tasks:
1. **Unit Tests**
   ```java
   @SpringBootTest
   class UserServiceTest {
       @Autowired
       private UserService userService;
       
       @Test
       void testGetAllUsers() {
           List<User> users = userService.getAllUsers();
           assertNotNull(users);
       }
   }
   ```

2. **Integration Tests**
   ```java
   @SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
   @AutoConfigureMockMvc
   class UserControllerTest {
       @Autowired
       private MockMvc mockMvc;
       
       @Test
       void testListUsers() throws Exception {
           mockMvc.perform(get("/users"))
               .andExpect(status().isOk())
               .andExpect(view().name("users/list"));
       }
   }
   ```

3. **End-to-End Testing**
   - Selenium/Playwright for UI testing
   - API testing with RestAssured

### Phase 8: Deployment (Weeks 19-20)

#### Tasks:
1. **Containerization**
   ```dockerfile
   FROM eclipse-temurin:21-jre
   WORKDIR /app
   COPY target/*.jar app.jar
   EXPOSE 8080
   ENTRYPOINT ["java", "-jar", "app.jar"]
   ```

2. **CI/CD Pipeline**
   - GitHub Actions/Jenkins pipeline
   - Automated testing
   - Container registry push
   - Kubernetes/OpenShift deployment

---

## 6. Data Migration

### User Preferences
```sql
-- Extract from JBoss Portal tables
SELECT 
    user_id,
    pref_key,
    pref_value
FROM 
    jbp_preferences
WHERE 
    portlet_id = 'your-portlet-id';

-- Insert into Spring Boot schema
INSERT INTO user_preferences (user_id, preference_key, preference_value)
VALUES (?, ?, ?);
```

### Session Data
- Export critical session data before cutover
- Provide migration scripts for persistent session stores

---

## 7. Configuration Migration

### Portlet Descriptor → Application Properties

**Before (portlet.xml):**
```xml
<portlet>
    <portlet-name>UserPortlet</portlet-name>
    <portlet-class>com.example.UserPortlet</portlet-class>
    <init-param>
        <name>maxResults</name>
        <value>100</value>
    </init-param>
</portlet>
```

**After (application.yml):**
```yaml
app:
  user:
    max-results: 100
    
spring:
  application:
    name: user-service
  datasource:
    url: jdbc:postgresql://localhost:5432/userdb
    username: ${DB_USER}
    password: ${DB_PASSWORD}
```

---

## 8. Dependency Updates

### Key Library Migrations

| Old Dependency | New Dependency | Notes |
|---------------|----------------|-------|
| javax.portlet:portlet-api | (remove) | No longer needed |
| javax.servlet:servlet-api | jakarta.servlet:jakarta.servlet-api:6.0+ | Namespace change |
| javax.persistence:* | jakarta.persistence:jakarta.persistence-api:3.1+ | Jakarta EE 10+ |
| org.hibernate:hibernate-core:5.x | org.hibernate:hibernate-core:6.x | Major version upgrade |
| commons-logging | org.slf4j:slf4j-api | Modern logging |

---

## 9. Rollback Strategy

1. **Keep Portal Running**: Maintain portal server during parallel run
2. **Feature Flags**: Use feature toggles to switch between old/new
3. **Database Backups**: Regular backups before data migration
4. **Canary Deployment**: Gradual rollout to user segments
5. **Monitoring**: Real-time monitoring with Prometheus/Grafana

---

## 10. Success Criteria

### Functional Requirements
- [ ] All portlet functionality replicated in Spring Boot
- [ ] User preferences migrated successfully
- [ ] Authentication/authorization working
- [ ] All business logic working as expected
- [ ] UI rendering correctly across browsers

### Non-Functional Requirements
- [ ] Performance: Response time < 200ms (95th percentile)
- [ ] Availability: 99.9% uptime
- [ ] Security: Pass security audit
- [ ] Scalability: Handle 2x current load
- [ ] Test Coverage: > 80% code coverage

---

## 11. Timeline Summary

| Phase | Duration | Deliverable |
|-------|----------|-------------|
| Analysis & Setup | 2 weeks | Project structure, inventory |
| Business Logic Extraction | 3 weeks | Service layer complete |
| API Development | 3 weeks | REST APIs functional |
| UI Migration | 4 weeks | New UI implemented |
| Security Migration | 2 weeks | Security working |
| Session Management | 1 week | Sessions migrated |
| Testing | 3 weeks | Test suite complete |
| Deployment | 2 weeks | Production ready |
| **Total** | **20 weeks** | **Full migration** |

---

## 12. Risk Assessment

| Risk | Impact | Mitigation |
|------|--------|-----------|
| Data loss during migration | High | Multiple backups, migration rehearsals |
| Performance degradation | Medium | Load testing, performance optimization |
| Security vulnerabilities | High | Security audit, penetration testing |
| User adoption issues | Medium | Training, documentation, gradual rollout |
| Integration failures | Medium | Comprehensive integration testing |
| Timeline overrun | Medium | Buffer time, agile approach |

---

## 13. Resources Required

### Team
- 2 Senior Java Developers
- 1 Frontend Developer
- 1 DevOps Engineer
- 1 QA Engineer
- 1 Technical Lead/Architect

### Infrastructure
- Development environments
- Staging environment (mirrors production)
- CI/CD pipeline
- Monitoring and logging tools

---

## 14. Post-Migration

### Monitoring
- Application metrics (Micrometer + Prometheus)
- Log aggregation (ELK stack)
- Error tracking (Sentry/Rollbar)
- User analytics

### Documentation
- API documentation (Swagger/OpenAPI)
- User guides
- Developer documentation
- Runbooks for operations

### Support
- Knowledge transfer sessions
- 24/7 support during first month
- Regular retrospectives

---

## 15. References

- [Spring Boot 4.x Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [JSR 286 Portlet Specification](https://jcp.org/en/jsr/detail?id=286)
- [Spring Migration Guide](https://github.com/spring-projects/spring-framework/wiki/Upgrading-to-Spring-Framework-6.x)
- [Jakarta EE 10 Migration](https://jakarta.ee/specifications/platform/10/)

---

**Document Version**: 1.0  
**Last Updated**: 2026-05-19  
**Owner**: Migration Team  
**Status**: Draft
