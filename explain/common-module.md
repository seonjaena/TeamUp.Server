# Common 모듈 분석 문서

## 1. 개요

`common` 모듈은 프로젝트의 여러 모듈에서 공통적으로 사용되는 기능들을 모아놓은 핵심 지원 모듈입니다. 특정 비즈니스 도메인에 종속되지 않는 횡단 관심사(Cross-cutting Concerns)를 처리합니다. 주요 기능은 다음과 같습니다.

*   **보안 (Security)**: Spring Security 설정, JWT 처리, 암호화 등 인증/인가의 기반을 담당합니다.
*   **설정 (Config)**: AWS, Redis 등 외부 서비스 연동에 필요한 설정을 관리합니다.
*   **예외 처리 (Exception Handling)**: 애플리케이션 전역의 예외를 일관된 방식으로 처리합니다.
*   **유효성 검사 (Validation)**: API 요청 데이터의 유효성을 검증하기 위한 커스텀 어노테이션과 로직을 제공합니다.
*   **인프라 (Infrastructure)**: 이메일/SMS 발송, 시간/UUID 생성 등 외부 시스템 또는 인프라와 연동하는 구현체를 제공합니다.
*   **유틸리티 (Utils)**: 날짜, 문자열 처리 등 범용적으로 사용되는 유틸리티 클래스를 포함합니다.

## 2. 주요 기능 및 파일 상세 설명

### 2.1. `security` - 보안

애플리케이션 보안의 핵심을 담당합니다.

#### **`SecurityConfig.java`**

Spring Security의 메인 설정 파일입니다. `HttpSecurity`를 설정하여 다음을 구성합니다.
*   **URL 접근 제어**: 특정 URL 패턴별로 HTTP 메서드와 필요한 권한(ex: `permitAll`, `hasAuthority('ADMIN')`)을 설정합니다.
*   **필터 체인**: `JwtAuthenticationFilter`를 `UsernamePasswordAuthenticationFilter` 앞에 추가하여 JWT 인증이 먼저 수행되도록 합니다.
*   **세션 관리**: 세션을 사용하지 않는 상태 없는(Stateless) 방식으로 설정합니다.
*   **예외 핸들러 등록**: 인증/인가 실패 시 처리할 `CustomAuthenticationEntryPoint`와 `CustomAccessDeniedHandler`를 등록합니다.
*   **CORS 설정**: Cross-Origin Resource Sharing 정책을 설정합니다.
*   **권한 계층**: `RoleHierarchy`를 설정하여 `ADMIN` > `USER`와 같은 권한 계층을 정의합니다.

#### **`JwtProvider.java`**

JWT의 생성, 파싱, 검증을 담당하는 핵심 클래스입니다. Access Token과 Refresh Token을 생성하고, HTTP 요청 헤더에서 토큰을 추출하며, 토큰의 유효성을 검사합니다.

#### **`JwtAuthenticationFilter.java`**

HTTP 요청이 들어올 때마다 실행되는 필터입니다. `shouldNotFilter`를 통해 공개된 URL은 통과시키고, 보호된 URL에 대해서는 `Authorization` 헤더에서 JWT를 파싱하여 사용자를 인증하고 `SecurityContextHolder`에 인증 정보를 저장합니다.

#### **`CustomAuthenticationEntryPoint.java` & `CustomAccessDeniedHandler.java`**

각각 인증(401 Unauthorized) 실패와 인가(403 Forbidden) 실패 시 호출되는 핸들러입니다. 표준 HTML 에러 페이지 대신 일관된 JSON 형식의 에러 응답을 클라이언트에게 반환합니다.

#### **`EncryptionProvider.java`**

RSA 알고리즘을 사용한 암호화/복호화 기능을 제공합니다. 주로 비밀번호 찾기 기능에서 URL 파라미터를 안전하게 전달하는 데 사용됩니다.

### 2.2. `controller` - 전역 컨트롤러 및 유효성 검사

#### **`ExceptionRestController.java` (`@RestControllerAdvice`)

애플리케이션 전역에서 발생하는 예외를 처리하는 중앙 집중식 핸들러입니다. 특정 예외(`UserIdNotFoundException`, `MethodArgumentNotValidException` 등)가 발생했을 때, 각각에 맞는 HTTP 상태 코드와 일관된 `ExceptionResponse` 형식의 JSON 응답을 생성하여 클라이언트에 반환합니다.

#### **`constraint` & `validator` 패키지

사용자 정의 유효성 검사 어노테이션과 그 구현체를 포함합니다.
*   **Constraints**: `@UserPwConstraint`, `@PhoneConstraint`, `@ListSizeConstraint` 등과 같이 특정 데이터의 형식을 검증하기 위한 커스텀 어노테이션을 정의합니다.
*   **Validators**: 각 Constraint 어노테이션에 대한 실제 검증 로직을 구현합니다. 예를 들어, `UserPwValidator`는 `VALID_REGEX.USER_PW`에 정의된 정규식을 사용하여 비밀번호 형식을 검증합니다.

### 2.3. `infrastructure` & `service.port` - 인프라 연동 (Ports and Adapters)

Hexagonal Architecture의 Ports and Adapters 패턴을 잘 보여주는 부분입니다. 애플리케이션의 비즈니스 로직은 `port`에 정의된 인터페이스에만 의존하고, `infrastructure`의 구현체는 런타임에 주입됩니다.

*   **Ports (in `service.port`)**: `MailSender`, `SmsSender`, `ClockHolder`, `UuidHolder` 등 추상화된 인터페이스를 정의합니다.
*   **Adapters (in `infrastructure`)**: 각 Port에 대한 실제 구현체를 제공합니다.
    *   `SmtpMailSender`, `SesMailSender`: `MailSender`의 구현체로, 각각 SMTP와 AWS SES를 이용해 이메일을 발송합니다. `@Primary`를 통해 `SmtpMailSender`가 기본 구현체로 사용됩니다.
    *   `CoolSmsSender`: `SmsSender`의 구현체로, CoolSMS 서비스를 이용해 SMS를 발송합니다.
    *   `SystemClockHolder`, `SystemUuidHolder`: `ClockHolder`, `UuidHolder`의 구현체로, 시스템의 실제 시간과 UUID를 제공합니다. 이를 통해 테스트 시 시간을 고정하거나 예측 가능한 UUID를 주입할 수 있습니다.

### 2.4. `domain/exception` - 사용자 정의 예외

비즈니스 상황에 맞는 의미 있는 예외들을 정의합니다. `AlreadyUserEmailExistsException`, `BadVerificationCodeException` 등과 같이 예외 이름만으로도 어떤 문제가 발생했는지 명확하게 알 수 있어 코드 가독성과 유지보수성을 높입니다. 이 예외들은 `ExceptionRestController`에서 처리됩니다.
