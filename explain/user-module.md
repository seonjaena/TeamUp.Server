# User 모듈 분석 문서

## 1. 개요

`user` 모듈은 TeamUp 애플리케이션의 사용자 관련 모든 기능을 담당하는 핵심 모듈입니다. 사용자 정보 조회, 회원가입, 정보 수정(비밀번호, 닉네임, 생년월일, 프로필 이미지), 회원 탈퇴 등의 기능을 포함합니다.

## 2. 아키텍처

이 모듈은 **Hexagonal Architecture (Ports and Adapters)** 의 원칙을 따릅니다. 이를 통해 비즈니스 로직(Domain)이 외부 기술(Infrastructure, UI)로부터 분리되어 유연하고 테스트하기 쉬운 구조를 가집니다.

*   **Domain**: 순수한 비즈니스 로직과 데이터 모델(`User`, `USER_STATUS`)을 포함합니다. 외부 세계에 대한 의존성이 없습니다.
*   **Application (Service)**: 도메인 객체를 사용하여 실제 비즈니스 흐름을 구현합니다.
    *   `UserService` (Port): 애플리케이션 서비스의 인터페이스(계약)를 정의합니다.
    *   `UserServiceImpl` (Adapter): `UserService`의 실제 구현체입니다.
*   **Adapter**: 외부 세계와 상호작용하는 구현체들을 포함합니다.
    *   **Web Adapter (Controller)**: `UserController`는 REST API 엔드포인트를 제공하여 외부 요청을 받습니다.
    *   **Persistence Adapter (Repository)**: `UserRepository`(Port)와 그 구현체인 `UserRepositoryImpl`은 데이터베이스와의 상호작용을 담당합니다.

## 3. 주요 파일 및 클래스 설명

### 3.1. `controller` - Web Adapter

외부 HTTP 요청을 받아 애플리케이션 서비스로 전달하는 역할을 합니다.

#### **`UserController.java`**

사용자 관련 API 엔드포인트를 정의하는 REST 컨트롤러입니다. `UserService`를 통해 비즈니스 로직을 호출합니다.

*   **주요 API 엔드포인트:**
    *   `GET /user/available/userId/{userId}`: 사용자 ID 중복 여부를 확인합니다.
    *   `GET /user/available/userNickname/{userNickname}`: 닉네임 중복 여부를 확인합니다.
    *   `POST /user`: 신규 회원을 가입시킵니다. (`signUp`)
    *   `GET /user/link/password/{userId}`: 비밀번호 변경 이메일을 발송합니다.
    *   `PATCH /user/password`: 이메일 링크를 통해 비밀번호를 재설정합니다.
    *   `PATCH /user/authenticated/password`: 로그인 상태에서 비밀번호를 변경합니다.
    *   `PATCH /user/nickname/{userNickname}`: 닉네임을 변경합니다.
    *   `PATCH /user/birth/{userBirth}`: 생년월일을 변경합니다.
    *   `PATCH /user/profile-image`: 프로필 이미지를 변경합니다.
    *   `GET /user/profile-image-url`: 프로필 이미지 URL을 조회합니다.
    *   `GET /user/profile-info`: 사용자 프로필 정보 전체를 조회합니다.
    *   `DELETE /user`: 회원 탈퇴를 처리합니다.

#### **`port/UserService.java`**

`UserController`가 의존하는 서비스 인터페이스(Port)입니다. `UserServiceImpl`에 의해 구현되며, 사용자 관련 비즈니스 로직의 명세를 정의합니다.

#### **`request/` & `response/`**

API 요청(Request) 및 응답(Response)에 사용되는 DTO(Data Transfer Object)를 정의합니다.

*   `SignUpRequest`: 회원가입 요청 DTO
*   `ChangePasswordRequest`: 비밀번호 찾기(재설정) 요청 DTO
*   `LoginChangePasswordRequest`: 로그인 후 비밀번호 변경 요청 DTO
*   `ProfileImageUrlResponse`: 프로필 이미지 URL 응답 DTO
*   `UserProfileInfoResponse`: 사용자 프로필 정보 응답 DTO

### 3.2. `service` - Application Layer

실제 비즈니스 로직을 수행하는 서비스 계층입니다.

#### **`UserServiceImpl.java`**

`UserService` 인터페이스의 구현체입니다. 사용자 관련 핵심 비즈니스 로직을 모두 포함하고 있습니다.

*   **주요 의존성:**
    *   `UserRepository`: 사용자 데이터 영속성 처리
    *   `PasswordEncoder`: 비밀번호 암호화
    *   `AmazonS3Client`: 프로필 이미지 저장을 위한 S3 클라이언트
    *   `RedisTemplate`: 비밀번호 변경 시 인증 값 임시 저장
    *   `MailSender`: 비밀번호 변경 링크 이메일 발송
*   **주요 메서드 로직:**
    *   `signUp()`: ID/닉네임 중복 검사, 비밀번호 일치 여부 확인 후 `User` 객체를 생성하여 `UserRepository`를 통해 저장합니다.
    *   `sendChangePasswordUrl()`: 암호화된 사용자 ID와 랜덤 값을 포함한 URL을 생성하고, Redis에 랜덤 값을 저장한 뒤 사용자에게 이메일로 발송합니다.
    *   `findPassword()`: 이메일로 받은 랜덤 값들을 검증하여 유효한 경우 비밀번호를 변경합니다.
    *   `changeProfileImage()`: 전송된 이미지 파일의 유효성(크기, 확장자)을 검사하고, 임시 파일로 저장한 뒤 S3에 업로드하고, 최종적으로 DB에 S3 경로를 업데이트합니다.
    *   `getProfileImageUrl()` / `getProfileInfo()`: DB에 저장된 이미지 경로를 바탕으로 S3로부터 Pre-signed URL을 생성하여 반환합니다.

#### **`port/UserRepository.java`**

데이터베이스와 상호작용하는 리포지토리의 인터페이스(Port)입니다. `UserServiceImpl`은 이 인터페이스에 의존하여 DB 작업을 수행합니다.

### 3.3. `domain` - Core Business Logic

애플리케이션의 핵심 도메인 모델입니다.

#### **`User.java`**

사용자 정보를 나타내는 핵심 도메인 객체입니다. `Builder` 패턴으로 생성되며, 사용자의 상태를 변경하는 비즈니스 메서드(`changeNickname`, `delete` 등)를 포함합니다. 이 객체는 특정 기술(JPA 등)에 의존하지 않습니다.

#### **`USER_STATUS.java`**

사용자의 상태(`ACTIVE`, `DELETED` 등)를 나타내는 `enum` 타입입니다.

### 3.4. `infrastructure` - Persistence Adapter

데이터베이스 연동을 담당하는 영속성 계층입니다.

#### **`UserRepositoryImpl.java`**

`UserRepository` 인터페이스의 구현체입니다. Spring Data JPA를 사용하여 실제 DB 작업을 위임합니다.

#### **`UserJpaRepository.java`**

Spring Data JPA의 `JpaRepository`를 상속받는 인터페이스입니다. `findByAccountId`, `existsByNickname` 등 쿼리 메서드를 정의합니다.

#### **`UserEntity.java`**

JPA를 위한 데이터베이스 `USERS` 테이블과 매핑되는 엔티티 클래스입니다. 도메인 객체(`User`)와 DB 엔티티(`UserEntity`) 간의 변환을 위한 정적 팩토리 메서드(`fromDomain`)와 변환 메서드(`toDomain`)를 가지고 있습니다.
