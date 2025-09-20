# TỔNG QUAN VỀ SPRING BOOT

## 🚀 Spring Boot là gì?

Spring Boot là một framework Java được xây dựng trên nền tảng Spring Framework, giúp tạo ra các ứng dụng Java độc lập (standalone) một cách nhanh chóng và dễ dàng.

### Đặc điểm chính:
- **Auto-configuration**: Tự động cấu hình dựa trên classpath
- **Starter dependencies**: Quản lý dependencies đơn giản
- **Embedded servers**: Tích hợp sẵn Tomcat, Jetty, Undertow
- **Production-ready**: Có sẵn các tính năng monitoring, health checks
- **Opinionated**: Có những quyết định mặc định hợp lý

## 🏗️ Kiến trúc Spring Boot

```
┌─────────────────────────────────────┐
│           Spring Boot App           │
├─────────────────────────────────────┤
│  Spring Boot Starters              │
├─────────────────────────────────────┤
│  Spring Framework Core             │
├─────────────────────────────────────┤
│  Spring Boot Auto-configuration    │
├─────────────────────────────────────┤
│  Embedded Web Server               │
└─────────────────────────────────────┘
```

## 📦 Các thành phần chính

### 1. Spring Boot Starters
- **spring-boot-starter-web**: Web applications
- **spring-boot-starter-data-jpa**: JPA và Hibernate
- **spring-boot-starter-security**: Spring Security
- **spring-boot-starter-test**: Testing framework
- **spring-boot-starter-actuator**: Production monitoring

### 2. Auto-configuration
Spring Boot tự động cấu hình dựa trên:
- Classpath dependencies
- Existing beans
- Properties files
- Environment variables

### 3. Embedded Servers
- **Tomcat** (mặc định)
- **Jetty**
- **Undertow**

## 🎯 Lợi ích của Spring Boot

### So với Spring Framework truyền thống:
| Spring Framework | Spring Boot |
|------------------|-------------|
| Cấu hình XML phức tạp | Cấu hình Java annotations |
| Nhiều boilerplate code | Ít code hơn |
| Cấu hình thủ công | Auto-configuration |
| Deploy phức tạp | JAR file độc lập |
| Khởi động chậm | Khởi động nhanh |

### Lợi ích chính:
1. **Giảm thời gian phát triển**: Ít cấu hình, nhiều convention
2. **Dễ học**: Cú pháp đơn giản, tài liệu tốt
3. **Production-ready**: Có sẵn monitoring, security
4. **Microservices**: Hỗ trợ tốt cho kiến trúc microservices
5. **Ecosystem**: Hệ sinh thái phong phú

## 🔧 Các annotation quan trọng

### Core Annotations:
```java
@SpringBootApplication  // Main class
@Component              // Generic component
@Service               // Business logic layer
@Repository            // Data access layer
@Controller            // Web controller
@RestController        // REST API controller
```

### Configuration Annotations:
```java
@Configuration         // Configuration class
@Bean                 // Bean definition
@Autowired            // Dependency injection
@Qualifier            // Specify bean name
@Primary              // Primary bean
@Conditional          // Conditional bean creation
```

### Web Annotations:
```java
@RequestMapping       // Map HTTP requests
@GetMapping          // GET requests
@PostMapping         // POST requests
@PutMapping          // PUT requests
@DeleteMapping       // DELETE requests
@PathVariable        // Path variables
@RequestParam        // Query parameters
@RequestBody         // Request body
@ResponseBody        // Response body
```

## 📁 Cấu trúc project Spring Boot

```
src/
├── main/
│   ├── java/
│   │   └── com/example/demo/
│   │       ├── DemoApplication.java      # Main class
│   │       ├── controller/               # REST controllers
│   │       ├── service/                  # Business logic
│   │       ├── repository/               # Data access
│   │       ├── entity/                   # JPA entities
│   │       ├── dto/                      # Data transfer objects
│   │       └── config/                   # Configuration classes
│   └── resources/
│       ├── application.properties        # Configuration
│       ├── application.yml              # YAML configuration
│       └── static/                      # Static resources
└── test/
    └── java/
        └── com/example/demo/
            └── DemoApplicationTests.java # Test class
```

## ⚙️ Configuration Files

### application.properties:
```properties
# Server configuration
server.port=8080
server.servlet.context-path=/api

# Database configuration
spring.datasource.url=jdbc:mysql://localhost:3306/mydb
spring.datasource.username=root
spring.datasource.password=password

# JPA configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### application.yml:
```yaml
server:
  port: 8080
  servlet:
    context-path: /api

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mydb
    username: root
    password: password
  
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
```

## 🚀 Tạo ứng dụng Spring Boot đầu tiên

### 1. Sử dụng Spring Initializr:
- Truy cập: https://start.spring.io/
- Chọn: Maven, Java, Spring Boot version
- Dependencies: Web, JPA, H2, Actuator

### 2. Main Application Class:
```java
@SpringBootApplication
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
```

### 3. REST Controller:
```java
@RestController
@RequestMapping("/api")
public class HelloController {
    
    @GetMapping("/hello")
    public String hello() {
        return "Hello Spring Boot!";
    }
    
    @GetMapping("/hello/{name}")
    public String helloName(@PathVariable String name) {
        return "Hello " + name + "!";
    }
}
```

## 🔍 Spring Boot Actuator

Actuator cung cấp các endpoints để monitor và quản lý ứng dụng:

```properties
# Enable all endpoints
management.endpoints.web.exposure.include=*

# Health check
management.endpoint.health.show-details=always

# Info endpoint
info.app.name=My Spring Boot App
info.app.version=1.0.0
```

### Các endpoints hữu ích:
- `/actuator/health` - Health status
- `/actuator/info` - Application info
- `/actuator/metrics` - Application metrics
- `/actuator/env` - Environment properties
- `/actuator/beans` - Spring beans

## 🧪 Testing trong Spring Boot

### Unit Test:
```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private UserService userService;
    
    @Test
    void shouldReturnUserWhenValidId() {
        // Given
        User user = new User("John", "john@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        
        // When
        User result = userService.findById(1L);
        
        // Then
        assertThat(result.getName()).isEqualTo("John");
    }
}
```

### Integration Test:
```java
@SpringBootTest
@AutoConfigureTestDatabase
class UserControllerIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    void shouldReturnUserList() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/users", String.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
```

## 📊 Best Practices

### 1. Project Structure:
- Tách biệt rõ ràng các layer (Controller, Service, Repository)
- Sử dụng DTOs cho data transfer
- Đặt tên package theo domain

### 2. Configuration:
- Sử dụng profiles cho các môi trường khác nhau
- Externalize configuration
- Sử dụng @ConfigurationProperties

### 3. Error Handling:
- Sử dụng @ControllerAdvice cho global exception handling
- Tạo custom exceptions
- Logging đầy đủ

### 4. Security:
- Luôn enable Spring Security
- Sử dụng HTTPS trong production
- Validate input data

### 5. Performance:
- Sử dụng connection pooling
- Enable caching khi cần thiết
- Monitor với Actuator

## 🎯 Kết luận

Spring Boot giúp:
- **Tăng tốc độ phát triển** với auto-configuration
- **Giảm boilerplate code** với starters
- **Dễ dàng deploy** với embedded servers
- **Production-ready** với Actuator
- **Hỗ trợ microservices** architecture

Đây là foundation vững chắc để bắt đầu học Spring Boot và chuẩn bị cho kỳ thi lập trình!
