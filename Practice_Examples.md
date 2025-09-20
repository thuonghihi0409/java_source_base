# HƯỚNG DẪN THỰC HÀNH VÀ VÍ DỤ CODE SPRING BOOT

## 🚀 PROJECT 1: REST API CƠ BẢN - USER MANAGEMENT

### Mục tiêu: Tạo REST API quản lý người dùng với CRUD operations

### Bước 1: Tạo Project
```bash
# Sử dụng Spring Initializr hoặc tạo thủ công
# Dependencies: Web, JPA, H2, Validation, Actuator
```

### Bước 2: Cấu trúc Project
```
src/main/java/com/example/userapp/
├── UserAppApplication.java
├── controller/
│   └── UserController.java
├── service/
│   └── UserService.java
├── repository/
│   └── UserRepository.java
├── entity/
│   └── User.java
└── dto/
    ├── UserRequest.java
    └── UserResponse.java
```

### Bước 3: Entity Class
```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    @NotBlank(message = "Name is required")
    private String name;
    
    @Column(unique = true, nullable = false)
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    private String email;
    
    @Column(length = 20)
    private String phone;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    // Constructors
    public User() {}
    
    public User(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }
    
    // Getters and Setters
    // ... (generate automatically)
}
```

### Bước 4: Repository Interface
```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Custom query methods
    Optional<User> findByEmail(String email);
    
    List<User> findByNameContainingIgnoreCase(String name);
    
    @Query("SELECT u FROM User u WHERE u.createdAt >= :date")
    List<User> findUsersCreatedAfter(@Param("date") LocalDateTime date);
    
    @Query(value = "SELECT * FROM users WHERE phone IS NOT NULL", nativeQuery = true)
    List<User> findUsersWithPhone();
    
    boolean existsByEmail(String email);
}
```

### Bước 5: Service Layer
```java
@Service
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }
    
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists: " + user.getEmail());
        }
        return userRepository.save(user);
    }
    
    public User updateUser(Long id, User userDetails) {
        User user = getUserById(id);
        
        if (!user.getEmail().equals(userDetails.getEmail()) && 
            userRepository.existsByEmail(userDetails.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists: " + userDetails.getEmail());
        }
        
        user.setName(userDetails.getName());
        user.setEmail(userDetails.getEmail());
        user.setPhone(userDetails.getPhone());
        
        return userRepository.save(user);
    }
    
    public void deleteUser(Long id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }
    
    public List<User> searchUsersByName(String name) {
        return userRepository.findByNameContainingIgnoreCase(name);
    }
}
```

### Bước 6: Custom Exceptions
```java
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
```

### Bước 7: Controller
```java
@RestController
@RequestMapping("/api/users")
@Validated
public class UserController {
    
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }
    
    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        User createdUser = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, 
                                         @Valid @RequestBody User userDetails) {
        User updatedUser = userService.updateUser(id, userDetails);
        return ResponseEntity.ok(updatedUser);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String name) {
        List<User> users = userService.searchUsersByName(name);
        return ResponseEntity.ok(users);
    }
}
```

### Bước 8: Global Exception Handler
```java
@ControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        logger.error("User not found: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse("USER_NOT_FOUND", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailExists(EmailAlreadyExistsException ex) {
        logger.error("Email already exists: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse("EMAIL_EXISTS", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        logger.error("Validation error: {}", ex.getMessage());
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        ErrorResponse error = new ErrorResponse("VALIDATION_ERROR", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        logger.error("Unexpected error: {}", ex.getMessage(), ex);
        ErrorResponse error = new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
```

### Bước 9: Error Response DTO
```java
public class ErrorResponse {
    private String code;
    private String message;
    private LocalDateTime timestamp;
    
    public ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
    
    // Getters and Setters
}
```

### Bước 10: Application Configuration
```yaml
# application.yml
server:
  port: 8080
  servlet:
    context-path: /api

spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password: password
  
  h2:
    console:
      enabled: true
      path: /h2-console
  
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
    properties:
      hibernate:
        format_sql: true

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: always

logging:
  level:
    com.example.userapp: DEBUG
    org.springframework.web: DEBUG
```

## 🧪 PROJECT 2: TESTING COMPREHENSIVE

### Unit Tests cho Service
```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private UserService userService;
    
    @Test
    void shouldReturnAllUsers() {
        // Given
        List<User> users = Arrays.asList(
            new User("John", "john@example.com", "123456789"),
            new User("Jane", "jane@example.com", "987654321")
        );
        when(userRepository.findAll()).thenReturn(users);
        
        // When
        List<User> result = userService.getAllUsers();
        
        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("John");
    }
    
    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> userService.getUserById(userId))
            .isInstanceOf(UserNotFoundException.class)
            .hasMessage("User not found with id: 1");
    }
    
    @Test
    void shouldCreateUserSuccessfully() {
        // Given
        User user = new User("John", "john@example.com", "123456789");
        User savedUser = new User("John", "john@example.com", "123456789");
        savedUser.setId(1L);
        
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepository.save(user)).thenReturn(savedUser);
        
        // When
        User result = userService.createUser(user);
        
        // Then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("John");
        verify(userRepository).save(user);
    }
    
    @Test
    void shouldThrowExceptionWhenEmailExists() {
        // Given
        User user = new User("John", "john@example.com", "123456789");
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);
        
        // When & Then
        assertThatThrownBy(() -> userService.createUser(user))
            .isInstanceOf(EmailAlreadyExistsException.class)
            .hasMessage("Email already exists: john@example.com");
    }
}
```

### Integration Tests
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class UserControllerIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private UserRepository userRepository;
    
    @Test
    void shouldCreateAndRetrieveUser() {
        // Given
        User user = new User("John", "john@example.com", "123456789");
        
        // When - Create user
        ResponseEntity<User> createResponse = restTemplate.postForEntity(
            "/api/users", user, User.class);
        
        // Then
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createResponse.getBody().getName()).isEqualTo("John");
        
        // When - Retrieve user
        Long userId = createResponse.getBody().getId();
        ResponseEntity<User> getResponse = restTemplate.getForEntity(
            "/api/users/" + userId, User.class);
        
        // Then
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody().getName()).isEqualTo("John");
    }
    
    @Test
    void shouldReturn404WhenUserNotFound() {
        // When
        ResponseEntity<ErrorResponse> response = restTemplate.getForEntity(
            "/api/users/999", ErrorResponse.class);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getCode()).isEqualTo("USER_NOT_FOUND");
    }
}
```

## 🔐 PROJECT 3: SPRING SECURITY VỚI JWT

### JWT Configuration
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }
}
```

### JWT Service
```java
@Service
public class JwtService {
    
    private static final String SECRET_KEY = "mySecretKey";
    private static final int EXPIRATION_TIME = 86400000; // 24 hours
    
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }
    
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }
    
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
```

## 📊 BÀI TẬP THỰC HÀNH

### Bài tập 1: Product Management API
Tạo REST API quản lý sản phẩm với:
- CRUD operations
- Tìm kiếm theo tên, giá
- Phân trang
- Validation
- Exception handling

### Bài tập 2: Order Management System
Tạo hệ thống quản lý đơn hàng với:
- User authentication
- Order creation
- Order status tracking
- Email notification (mock)
- Database relationships

### Bài tập 3: Blog API
Tạo API cho blog với:
- Posts và Comments
- User roles (Admin, User)
- File upload (images)
- Search functionality
- Caching

## 🎯 CHECKLIST THỰC HÀNH

### Cơ bản ✅
- [ ] Tạo project Spring Boot từ đầu
- [ ] Cấu hình database
- [ ] Tạo Entity với JPA annotations
- [ ] Implement Repository interface
- [ ] Tạo Service layer với business logic
- [ ] Tạo REST Controller với CRUD operations
- [ ] Xử lý exceptions
- [ ] Validation input data

### Nâng cao ✅
- [ ] Unit testing cho Service
- [ ] Integration testing cho Controller
- [ ] JWT authentication
- [ ] Role-based authorization
- [ ] Custom queries
- [ ] Pagination và Sorting
- [ ] File upload
- [ ] Email service
- [ ] Caching
- [ ] Logging

### Production Ready ✅
- [ ] Application properties cho các environments
- [ ] Health checks
- [ ] Metrics monitoring
- [ ] Error handling
- [ ] Security configuration
- [ ] Database migration
- [ ] Docker configuration
- [ ] API documentation (Swagger)

---
**Lưu ý:** Thực hành từng project một cách hoàn chỉnh. Đừng bỏ qua bất kỳ bước nào. Mỗi project sẽ giúp bạn hiểu sâu hơn về Spring Boot.
