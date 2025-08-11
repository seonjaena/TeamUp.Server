# Auth 모듈 분석 문서

## 1. 개요

`auth` 모듈은 사용자 인증(Authentication) 및 인가(Authorization)와 관련된 모든 기능을 담당합니다. 주요 기능은 다음과 같습니다.

*   **로그인**: ID/PW를 이용한 사용자 인증 및 JWT(Access Token, Refresh Token) 발급
*   **토큰 재발급**: Refresh Token을 이용한 Access Token 재발급
*   **인증 코드**: 회원가입 또는 주요 정보 변경 시 이메일/휴대폰 인증 코드 발송 및 검증
*   **권한 관리**: 사용자 역할(Role) 및 권한 계층 관리

## 2. 핵심 플로우

### 2.1. 로그인 및 토큰 발급 플로우

1.  **`POST /auth`** (`AuthController`): 사용자가 ID/PW로 로그인을 요청합니다.
2.  **`AuthServiceImpl.login()`**: `UserService`를 통해 사용자 정보를 조회하고, `PasswordEncoder`로 비밀번호를 비교하여 인증을 수행합니다.
3.  **`JwtProvider.createToken()`**: 인증 성공 시, Access Token과 Refresh Token을 생성합니다.
4.  **`UserRefreshTokenRepository.save()`**: 생성된 Refresh Token은 DB에 저장됩니다. 이때, 클라이언트에게는 실제 토큰 값 대신 토큰을 식별하기 위한 해시 값(`idxHash`)을 생성하여 함께 저장합니다.
5.  **`LoginResponse`**: 클라이언트에게 Access Token과 Refresh Token의 식별자인 `idxHash`를 반환합니다.

### 2.2. Access Token 재발급 플로우

1.  **`GET /auth/renewal`** (`AuthController`): 클라이언트는 Access Token 만료 시, 보관하고 있던 `refreshTokenIdxHash`로 토큰 재발급을 요청합니다.
2.  **`UserTokenServiceImpl.refreshAccessToken()`**: 전달받은 `idxHash`를 사용해 DB에서 `UserRefreshToken`을 조회합니다.
3.  **`JwtProvider.validateToken()`**: 조회된 Refresh Token이 유효한지 검증합니다.
4.  **토큰 재발급 및 교체**: 유효하다면, 새로운 Access Token을 발급하고, 보안 강화를 위해 기존 Refresh Token의 `idxHash`를 새로운 해시 값으로 교체(Token Rotation)하여 DB에 업데이트합니다.
5.  **`RefreshAccessTokenResponse`**: 클라이언트에게 새로운 Access Token과 새로 발급된 `idxHash`를 반환합니다.

## 3. 주요 파일 및 클래스 설명

### 3.1. `controller` - Web Adapter

인증 관련 HTTP 요청을 처리하는 계층입니다.

#### **`AuthController.java`**

인증 관련 API 엔드포인트를 정의합니다.

*   `POST /auth`: 로그인 처리
*   `GET /auth/renewal`: Access Token 재발급
*   `POST /auth/email-verification-code`: 이메일 인증 코드 발송
*   `POST /auth/phone-verification-code`: 휴대폰 인증 코드 발송
*   `PATCH /auth/email-verification`: 이메일 인증 코드 검증
*   `PATCH /auth/phone-verification`: 휴대폰 인증 코드 검증

#### **`port/`**

컨트롤러가 의존하는 서비스 인터페이스(Port)를 정의합니다.

*   `AuthService.java`: 로그인, 인증 코드 발송/검증 로직 명세
*   `UserRoleService.java`: 사용자 권한 관련 로직 명세
*   `UserTokenService.java`: 토큰 관리(재발급, 삭제) 로직 명세

### 3.2. `service` - Application Layer

실제 인증/인가 비즈니스 로직을 수행합니다.

#### **`AuthServiceImpl.java`**

`AuthService`의 구현체로, 로그인 및 인증 코드 처리의 핵심 로직을 담당합니다. `RedisTemplate`을 사용하여 인증 코드를 지정된 유효 시간 동안 임시 저장하고, `MailSender`와 `SmsSender`를 통해 사용자에게 코드를 발송합니다.

#### **`UserTokenServiceImpl.java`**

`UserTokenService`의 구현체로, Refresh Token을 관리합니다. 토큰 재발급 요청 시, DB에 저장된 토큰과 비교하고 새로운 토큰을 발급하는 로직을 수행합니다.

#### **`UserRoleServiceImpl.java`**

`UserRoleService`의 구현체로, 사용자 권한 계층(Role Hierarchy)을 설정하고 관리합니다. `local` 프로필에서는 초기 권한 데이터를 자동으로 생성하는 로직을 포함합니다.

### 3.3. `domain` - Core Business Logic

인증/인가와 관련된 핵심 도메인 모델입니다.

*   **`UserRefreshToken.java`**: 사용자의 Refresh Token 정보를 나타내는 도메인 객체입니다. 실제 토큰 값(`value`)과 식별자(`idxHash`), 그리고 해당 `User`를 포함합니다.
*   **`UserRole.java`**: 사용자 권한(ex: `ADMIN`, `USER`)과 우선순위를 나타내는 도메인 객체입니다.

### 3.4. `infrastructure` - Persistence & System Adapters

DB 연동 및 외부 시스템(Redis, UUID 생성 등) 연동을 담당합니다.

#### **`UserRefreshTokenRepositoryImpl.java` & `UserRoleRepositoryImpl.java`**

각각 `UserRefreshTokenRepository`와 `UserRoleRepository`의 구현체로, Spring Data JPA를 사용하여 DB 작업을 처리합니다.

#### **`UserRefreshTokenJpaRepository.java` & `UserRoleJpaRepository.java`**

Spring Data JPA 인터페이스입니다.

#### **`UserRefreshTokenEntity.java` & `UserRoleEntity.java`**

DB 테이블과 매핑되는 JPA 엔티티 클래스입니다. 각각 `UserRefreshToken`, `UserRole` 도메인 객체와 상호 변환하는 로직을 포함합니다.

#### **`SystemVerificationCodeHolder.java`**

`VerificationCodeHolder` 인터페이스의 구현체로, 이메일/휴대폰 인증에 사용될 랜덤 문자열 또는 숫자 코드를 생성하는 역할을 합니다.
