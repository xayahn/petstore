# Layug Backend Package Structure

## Overview

The Petstore backend uses a **layered architecture** with packages organized by technical responsibility. All code resides under the `layug` root package with clear separation of concerns.

## Package Organization

```
layug/
├── PetstoreApplication.java     # Main Spring Boot entry point
│
├── rest/                        # REST Controllers (API Layer)
│   ├── PetController.java
│   ├── OrderController.java
│   ├── SellerController.java
│   ├── AuthController.java
│   ├── CartController.java
│   └── PaymentController.java
│
├── service/                     # Business Logic Layer
│   ├── PetService.java
│   ├── OrderService.java
│   ├── SellerService.java
│   ├── AuthService.java
│   ├── CartService.java
│   ├── PaymentService.java
│   ├── EmailService.java
│   └── UserService.java
│
├── repository/                  # Data Access Layer (Spring Data JPA)
│   ├── PetRepository.java
│   ├── OrderRepository.java
│   ├── SellerRepository.java
│   ├── UserRepository.java
│   ├── CartRepository.java
│   ├── PaymentRepository.java
│   └── CartItemRepository.java
│
├── entity/                      # JPA Entity Models
│   ├── User.java
│   ├── Seller.java
│   ├── Pet.java
│   ├── Order.java
│   ├── OrderItem.java
│   ├── Cart.java
│   ├── CartItem.java
│   ├── Payment.java
│   ├── Review.java
│   ├── SellerEarning.java
│   ├── Wishlist.java
│   └── WishlistItem.java
│
├── dto/                         # Data Transfer Objects
│   ├── UserDto.java
│   ├── PetDto.java
│   ├── OrderDto.java
│   ├── SellerDto.java
│   ├── CartDto.java
│   ├── PaymentDto.java
│   ├── request/                 # Request DTOs
│   │   ├── RegisterRequest.java
│   │   ├── LoginRequest.java
│   │   ├── CreatePetRequest.java
│   │   └── CheckoutRequest.java
│   └── response/                # Response DTOs
│       ├── AuthResponse.java
│       ├── ApiResponse.java
│       └── ErrorResponse.java
│
├── config/                      # Spring Configuration
│   ├── WebConfig.java           # CORS, WebMvcConfig
│   ├── JpaConfig.java           # JPA/Hibernate config
│   ├── SecurityConfig.java      # (Will be in security/)
│   ├── OpenApiConfig.java       # Swagger/OpenAPI config
│   ├── FlywaySQLProvider.java   # Database migration config
│   └── ApplicationConfig.java   # Bean configurations
│
├── security/                    # Security & Authentication
│   ├── SecurityConfig.java      # Spring Security configuration
│   ├── JwtProvider.java         # JWT token generation/validation
│   ├── JwtAuthenticationFilter.java
│   ├── CustomUserDetailsService.java
│   ├── SecurityUtils.java       # Security utility methods
│   └── exception/
│       └── JwtAuthenticationException.java
│
├── exception/                   # Custom Exception Handling
│   ├── GlobalExceptionHandler.java
│   ├── NotFoundException.java
│   ├── UnauthorizedException.java
│   ├── ValidationException.java
│   ├── DuplicateResourceException.java
│   ├── PaymentException.java
│   └── InsufficientFundsException.java
│
└── util/                        # Utility Classes
    ├── EmailValidator.java
    ├── PasswordValidator.java
    ├── DateUtils.java
    ├── CurrencyUtils.java
    ├── PaginationUtils.java
    └── Constants.java
```

## Layer Responsibilities

### REST Layer (`layug.rest`)
- Handles HTTP requests and responses
- Maps routes to services
- Validates input parameters
- Returns appropriate HTTP status codes
- Implemented as @RestController beans

**Example**:
```java
@RestController
@RequestMapping("/api/pets")
public class PetController {
  @GetMapping
  public ResponseEntity<ApiResponse<List<PetDto>>> getAllPets() { }
  
  @PostMapping
  public ResponseEntity<ApiResponse<PetDto>> createPet(@RequestBody CreatePetRequest req) { }
}
```

### Service Layer (`layug.service`)
- Contains business logic
- Coordinates between repositories and controllers
- Handles transactions with @Transactional
- Implements domain rules (e.g., seller verification, commissions)
- Throws domain exceptions for business rule violations

**Example**:
```java
@Service
@Transactional
public class OrderService {
  public Order createOrder(CheckoutRequest request) { }
  public Order getOrder(Long orderId) { }
  public List<Order> getSellerOrders(Long sellerId) { }
}
```

### Repository Layer (`layug.repository`)
- Extends Spring Data JPA Repository interface
- Implements custom queries when needed
- No business logic - just data access
- Uses @Repository annotation

**Example**:
```java
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
  List<Order> findBySellerId(Long sellerId);
  Optional<Order> findByIdAndSellerId(Long orderId, Long sellerId);
  List<Order> findByBuyerIdOrderByCreatedAtDesc(Long buyerId);
}
```

### Entity Layer (`layug.entity`)
- JPA entity classes mapped to database tables
- Includes @Entity, @Table, @Column annotations
- Implements relationships with @ManyToOne, @OneToMany, etc.
- May include validation annotations

**Example**:
```java
@Entity
@Table(name = "pets")
public class Pet {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @ManyToOne
  @JoinColumn(name = "seller_id")
  private Seller seller;
  
  private String name;
  private BigDecimal price;
}
```

### DTO Layer (`layug.dto`)
- Request DTOs: Receive input from clients
- Response DTOs: Send data to clients
- Separate from entities to control API contracts
- Includes validation annotations (@NotNull, @Email, etc.)

**Example**:
```java
public class CreatePetRequest {
  @NotBlank
  private String name;
  
  @NotNull
  @Positive
  private BigDecimal price;
}

public class PetDto {
  private Long id;
  private String name;
  private BigDecimal price;
  private String sellerName;
}
```

### Configuration Layer (`layug.config`)
- Spring beans and component configurations
- Web configuration (CORS, content negotiation)
- Database configuration
- Cache configuration
- OpenAPI/Swagger documentation

**Example**:
```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/api/**")
      .allowedOrigins("http://localhost:5173")
      .allowedMethods("GET", "POST", "PUT", "DELETE");
  }
}
```

### Security Layer (`layug.security`)
- Spring Security configuration
- JWT token provider
- Authentication filters
- User details service
- Authorization logic

**Example**:
```java
@Component
public class JwtProvider {
  public String generateAccessToken(User user) { }
  public String generateRefreshToken(User user) { }
  public Claims extractClaims(String token) { }
  public boolean validateToken(String token) { }
}
```

### Exception Layer (`layug.exception`)
- Custom exception classes
- Global exception handler with @ControllerAdvice
- Maps exceptions to HTTP responses

**Example**:
```java
@ControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException e) {
    return ResponseEntity.status(404).body(new ErrorResponse(e.getMessage()));
  }
}
```

### Utility Layer (`layug.util`)
- Stateless utility classes
- Constants
- Helper methods
- Formatters and validators

**Example**:
```java
public class Constants {
  public static final String JWT_SECRET = "...";
  public static final int JWT_ACCESS_EXPIRATION = 900000; // 15 min
  public static final String ADMIN_ROLE = "ROLE_ADMIN";
}

public class PaginationUtils {
  public static PageRequest getPageRequest(int page, int limit) { }
}
```

## Key Design Patterns

### Request Flow
```
HTTP Request
    ↓
REST Controller (@RestController)
    ↓ (calls)
Service (@Service) 
    ↓ (calls)
Repository (Spring Data JPA)
    ↓ (queries)
Database
    ↓ (returns Entity)
Repository
    ↓ (returns to)
Service
    ↓ (converts to DTO)
REST Controller
    ↓
HTTP Response (JSON)
```

### Exception Handling
```
Business Logic throws checked/unchecked exception
    ↓
Service catches or propagates
    ↓
Controller optionally catches
    ↓
GlobalExceptionHandler catches
    ↓
Returns ErrorResponse as JSON
```

### Authorization
```
@RestController
  ↓
@PreAuthorize("hasRole('SELLER')")  // Check role
  ↓
@PreAuthorize("@authService.isOwner(#sellerId, principal)")  // Check ownership
  ↓
Invoke service
```

## Naming Conventions

- **Controllers**: {Domain}Controller (e.g., PetController, OrderController)
- **Services**: {Domain}Service (e.g., PetService, OrderService)
- **Repositories**: {Domain}Repository (e.g., PetRepository, OrderRepository)
- **Entities**: {Domain} (e.g., Pet, Order, Seller)
- **DTOs**: {Domain}Dto (e.g., PetDto, OrderDto)
- **Request DTOs**: {Action}{Domain}Request (e.g., CreatePetRequest, UpdateOrderRequest)
- **Response DTOs**: {Domain}{Type}Response (e.g., LoginResponse, ErrorResponse)
- **Exceptions**: {Type}Exception (e.g., NotFoundException, ValidationException)
- **Utilities**: {Concept}Utils or {Concept}Helper (e.g., DateUtils, PaginationHelper)

## Dependency Injection

All classes use constructor injection for dependencies:

```java
@Service
public class OrderService {
  private final OrderRepository orderRepository;
  private final SellerRepository sellerRepository;
  private final PaymentService paymentService;
  
  // Constructor injection (required dependencies)
  public OrderService(
    OrderRepository orderRepository,
    SellerRepository sellerRepository,
    PaymentService paymentService
  ) {
    this.orderRepository = orderRepository;
    this.sellerRepository = sellerRepository;
    this.paymentService = paymentService;
  }
}
```

## Testing Structure

```
src/test/java/layug/
├── service/
│   └── OrderServiceTest.java
├── repository/
│   └── OrderRepositoryTest.java
├── rest/
│   └── OrderControllerTest.java
└── integration/
    └── OrderControllerIntegrationTest.java
```

## Migration Notes

If previously at `com.petstore.*`, migration to `layug.*`:

1. Update package declarations in all files
2. Update imports across files
3. Update Spring component scan: `@SpringBootApplication(scanBasePackages = "layug")`
4. Update IDE project structure
5. Update test package structure
6. Update any external configuration referencing package names

---

**Last Updated**: 2026-05-05  
**Architecture**: Layered (REST → Service → Repository → Entity)  
**Root Package**: `layug`
