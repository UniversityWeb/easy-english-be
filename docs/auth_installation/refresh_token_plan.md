# Kế Hoạch Triển Khai Refresh Token & Đăng Nhập Đa Thiết Bị

Tài liệu này trình bày kế hoạch chi tiết để triển khai tính năng Refresh Token, quản lý phiên đăng nhập trên nhiều thiết bị (multi-device session management) kèm lưu trữ thông tin thiết bị (Device Info), vị trí đăng nhập (IP/Location), và loại bỏ hoàn toàn khóa ngoại (foreign key) dưới Database.

---

## Giải Pháp Thiết Kế Chi Tiết

### 1. Đăng nhập đa thiết bị & Lưu thông tin thiết bị, vị trí
Để lưu thông tin thiết bị và vị trí của phiên đăng nhập:
- **Tên thiết bị (Device Info)**: Được trích xuất từ header `User-Agent` của request đăng nhập. Có thể lưu chuỗi raw User-Agent hoặc phân tích cú pháp (ví dụ: "Chrome on Windows", "Safari on iOS").
- **Địa chỉ IP & Vị trí (IP Address & Location)**: 
  - Lấy địa chỉ IP từ request client qua `request.getRemoteAddr()` hoặc header `X-Forwarded-For`.
  - Từ IP, có thể xác định sơ bộ vị trí (ví dụ: Thành phố, Quốc gia) thông qua GeoIP API/Library (như MaxMind GeoIP2) hoặc tạm thời lưu thông tin IP trực tiếp.
- **Cấu trúc lưu trữ**: Bảng `tokens` sẽ được thêm các trường `device_info`, `ip_address`, và `login_location`.

### 2. Loại bỏ hoàn toàn khóa ngoại (No Foreign Keys in DB)
Để đảm bảo cơ sở dữ liệu không tự sinh khóa ngoại vật lý (Physical Foreign Key Constraint) nhưng ứng dụng vẫn hiểu được mối liên kết thực thể:
- Định cấu hình `@JoinColumn` trong JPA Entity với `foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)`.
- Điều này giúp Hibernate bỏ qua việc tạo `CONSTRAINT FK_... FOREIGN KEY` khi auto-ddl tạo bảng. Cột liên kết (ví dụ: `username`) sẽ chỉ hoạt động như một cột dữ liệu thông thường.

---

## Chi Tiết Các Thay Đổi Mã Nguồn (Proposed Changes)

### 1. Cấu Trúc Thực Thể (Domain Entities)

#### [Token.java](file:///media/antv/3CE4477DE4473882/BaiTapFresherDev/Projects/easy-english-migration/easy-english-be/src/main/java/com/universityweb/common/auth/entity/Token.java)
- Thay đổi mối quan hệ từ `@OneToOne` thành `@ManyToOne`.
- Thêm các trường lưu trữ thiết bị, IP, vị trí và Refresh Token:
```java
@Entity
@Table(name = "tokens")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Token implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String tokenStr;

    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    @Column(columnDefinition = "TEXT")
    private String refreshTokenStr;

    @Column(name = "refresh_expiry_date")
    private LocalDateTime refreshExpiryDate;

    // --- Thông tin thiết bị & vị trí ---
    @Column(name = "device_info", columnDefinition = "TEXT")
    private String deviceInfo;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "login_location")
    private String loginLocation;

    // Không sinh khóa ngoại vật lý dưới DB
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username", referencedColumnName = "username", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;
}
```

### 2. Lớp DTO & Request/Response

#### [LoginResponse.java](file:///media/antv/3CE4477DE4473882/BaiTapFresherDev/Projects/easy-english-migration/easy-english-be/src/main/java/com/universityweb/common/auth/response/LoginResponse.java)
- Thêm thuộc tính `refreshTokenStr`:
```java
private String refreshTokenStr;
```

#### [RefreshTokenRequest.java](file:///media/antv/3CE4477DE4473882/BaiTapFresherDev/Projects/easy-english-migration/easy-english-be/src/main/java/com/universityweb/common/auth/request/RefreshTokenRequest.java)
- Tạo mới record DTO:
```java
package com.universityweb.common.auth.request;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
    @NotBlank(message = "Refresh token must not be blank")
    String refreshTokenStr
) {}
```

### 3. Tầng Repository

#### [TokenRepos.java](file:///media/antv/3CE4477DE4473882/BaiTapFresherDev/Projects/easy-english-migration/easy-english-be/src/main/java/com/universityweb/common/auth/repos/TokenRepos.java)
- Thêm các phương thức truy vấn:
```java
Optional<Token> findByRefreshTokenStr(String refreshTokenStr);
void deleteByTokenStr(String tokenStr);
void deleteByUser_Username(String username);
```

### 4. Tầng JWT & Token Generation

#### [JwtGenerator.java](file:///media/antv/3CE4477DE4473882/BaiTapFresherDev/Projects/easy-english-migration/easy-english-be/src/main/java/com/universityweb/common/security/JwtGenerator.java)
- Cập nhật phương thức `generateAndSaveToken` để nhận thêm thông tin thiết bị và IP từ `HttpServletRequest`:
```java
public Token generateAndSaveToken(User user, String deviceInfo, String ipAddress, String loginLocation) {
    LocalDateTime curTime = LocalDateTime.now();
    LocalDateTime accessTokenExpiry = curTime.plus(SecurityUtils.EXPIRATION_DURATION_MILLIS, ChronoUnit.MILLIS);
    LocalDateTime refreshTokenExpiry = curTime.plus(7, ChronoUnit.DAYS); // Refresh token 7 ngày

    String generatedAccessToken = generateToken(user.getUsername(), curTime, accessTokenExpiry);
    String generatedRefreshToken = UUID.randomUUID().toString(); // Hoặc JWT tùy ý

    Token newToken = Token.builder()
            .tokenStr(generatedAccessToken)
            .expiryDate(accessTokenExpiry)
            .refreshTokenStr(generatedRefreshToken)
            .refreshExpiryDate(refreshTokenExpiry)
            .deviceInfo(deviceInfo)
            .ipAddress(ipAddress)
            .loginLocation(loginLocation)
            .user(user)
            .build();

    return tokenRepos.save(newToken);
}
```

### 5. Tầng Service & Controller

#### [AuthServiceImpl.java](file:///media/antv/3CE4477DE4473882/BaiTapFresherDev/Projects/easy-english-migration/easy-english-be/src/main/java/com/universityweb/common/auth/service/auth/AuthServiceImpl.java)
- Cập nhật logic Login để lấy `User-Agent` và `IP Address` từ `HttpServletRequest`:
```java
// Trong phương thức login:
String userAgent = request.getHeader("User-Agent");
String ipAddress = request.getRemoteAddr();
String location = resolveLocationFromIp(ipAddress); // Tạm thời trả về "Unknown" hoặc gọi API phụ trợ

Token savedToken = jwtGenerator.generateAndSaveToken(user, userAgent, ipAddress, location);
```
- Triển khai phương thức refresh token:
```java
@Override
@Transactional
public LoginResponse refreshToken(RefreshTokenRequest request) {
    Token token = tokenRepos.findByRefreshTokenStr(request.refreshTokenStr())
            .orElseThrow(() -> new TokenNotFoundException("Invalid Refresh Token"));

    if (token.getRefreshExpiryDate().isBefore(LocalDateTime.now())) {
        tokenRepos.delete(token);
        throw new TokenExpiredException("Refresh Token has expired. Please login again.");
    }

    // Xoay vòng refresh token (Token Rotation)
    LocalDateTime curTime = LocalDateTime.now();
    LocalDateTime nextAccessExpiry = curTime.plus(SecurityUtils.EXPIRATION_DURATION_MILLIS, ChronoUnit.MILLIS);
    LocalDateTime nextRefreshExpiry = curTime.plus(7, ChronoUnit.DAYS);

    String newAccessToken = jwtGenerator.generateToken(token.getUser().getUsername(), curTime, nextAccessExpiry);
    String newRefreshToken = UUID.randomUUID().toString();

    token.setTokenStr(newAccessToken);
    token.setExpiryDate(nextAccessExpiry);
    token.setRefreshTokenStr(newRefreshToken);
    token.setRefreshExpiryDate(nextRefreshExpiry);
    tokenRepos.save(token);

    return LoginResponse.builder()
            .tokenStr(newAccessToken)
            .refreshTokenStr(newRefreshToken)
            .tokenType("Bearer")
            .user(uMapper.toDTO(token.getUser()))
            .accountStatus(User.EStatus.ACTIVE)
            .build();
}
```

#### [AuthController.java](file:///media/antv/3CE4477DE4473882/BaiTapFresherDev/Projects/easy-english-migration/easy-english-be/src/main/java/com/universityweb/common/auth/controller/AuthController.java)
- Thêm tham số `HttpServletRequest` vào phương thức `/login` để lấy thông tin kết nối từ Client.
- Định nghĩa API refresh mới:
```java
@PostMapping("/refresh")
public ResponseEntity<LoginResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
    LoginResponse response = authService.refreshToken(request);
    return ResponseEntity.ok(response);
}
```
- Định nghĩa API hủy kích hoạt toàn bộ phiên:
```java
@PostMapping("/revoke-all")
public ResponseEntity<String> revokeAll() {
    authService.revokeAllDevices();
    return ResponseEntity.ok("All sessions revoked successfully");
}
```

---

## Kịch Bản Kiểm Thử & Xác Minh (Verification Plan)

1. **Kiểm tra schema DB**: 
   - Khởi động app để Hibernate tự động sinh schema.
   - Xác minh bảng `tokens` được tạo thành công với các trường `device_info`, `ip_address`, `login_location`.
   - Xác nhận cơ sở dữ liệu (PostgreSQL) **không chứa bất kỳ khóa ngoại vật lý (FOREIGN KEY CONSTRAINT)** nào liên kết giữa `tokens` và `users`.
2. **Kiểm thử đa thiết bị**:
   - Sử dụng Postman gửi request đăng nhập 1 từ Browser Chrome (User-Agent: Chrome) -> lưu token Chrome.
   - Gửi tiếp request đăng nhập 2 từ Postman (User-Agent: Postman) -> lưu token Postman.
   - Đảm bảo trong bảng `tokens` có 2 bản ghi song song cùng sở hữu bởi `username` của bạn.
   - Gọi một API cần phân quyền bằng cả 2 token Chrome và Postman -> cả hai đều thành công.
3. **Đăng xuất đơn thiết bị**:
   - Thực hiện logout kèm token Chrome -> kiểm tra DB thấy bản ghi Chrome bị xóa, bản ghi Postman vẫn còn.
4. **Revoke all**:
   - Gọi `/revoke-all` từ Postman -> kiểm tra DB thấy toàn bộ các phiên của user đó bị xóa sạch.
