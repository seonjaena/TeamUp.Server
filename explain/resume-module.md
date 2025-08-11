# Resume 모듈 분석 문서

## 1. 개요

`resume` 모듈은 사용자의 이력서 정보를 관리하는 기능을 담당합니다. 사용자는 자신의 기술, 경력, 학력, 자격증, 사용 가능 언어 등 다양한 정보를 등록하고 조회할 수 있습니다.

## 2. 아키텍처 및 데이터 모델

`user` 모듈과 마찬가지로 Hexagonal Architecture를 따르며, `Controller` -> `Service` -> `Repository` 계층으로 구성됩니다.

이 모듈의 핵심 데이터 모델은 다음과 같습니다.

*   **`Resume`**: 이력서의 전체 정보를 담는 메인 도메인 객체입니다. 자기소개, 기술 스택, 프로젝트 경험 등의 필드를 가집니다. `User`와 1:1 관계를 가집니다.
*   **`ResumeLanguage`**: 이력서에 포함되는 '사용 가능 언어' 정보를 나타내는 도메인 객체입니다. 언어 종류(`type`)와 숙련도(`grade`)를 가지며, `Resume`과 N:1 관계를 가집니다.

## 3. 주요 파일 및 클래스 설명

### 3.1. `controller` - Web Adapter

이력서 관련 HTTP 요청을 처리합니다.

#### **`ResumeController.java`**

이력서의 생성(등록) 및 조회를 위한 API 엔드포인트를 정의합니다.

*   `POST /resume`: 새로운 이력서를 등록하거나 기존 이력서를 갱신합니다.
*   `GET /resume`: 현재 로그인된 사용자의 이력서 정보를 조회합니다.

#### **`port/ResumeService.java`**

`ResumeController`가 의존하는 서비스 인터페이스(Port)로, 이력서 관련 비즈니스 로직의 명세를 정의합니다.

#### **`request/AddResumeRequest.java`**

이력서 등록/수정 시 클라이언트로부터 받는 데이터를 담는 DTO입니다. 자기소개, 프로젝트 URL 목록, 기술, 경험, 자격증, 사용 언어 등 이력서의 모든 항목을 포함합니다.

#### **`response/ResumeResponse.java`**

이력서 조회 시 클라이언트에게 반환하는 데이터를 담는 DTO입니다. `Resume`과 `ResumeLanguage` 도메인 객체를 조합하여 API 응답 형식에 맞게 데이터를 구성합니다.

### 3.2. `service` - Application Layer

실제 이력서 관련 비즈니스 로직을 수행합니다.

#### **`ResumeServiceImpl.java`**

`ResumeService`의 구현체입니다.

*   **주요 의존성:**
    *   `UserService`: 이력서를 등록/조회할 사용자를 특정하기 위해 의존합니다.
    *   `ResumeRepository`: `Resume` 데이터의 영속성을 처리합니다.
    *   `ResumeLanguageRepository`: `ResumeLanguage` 데이터의 영속성을 처리합니다.
*   **주요 메서드 로직:**
    *   `addResume()`: `AddResumeRequest` DTO를 받아 `Resume` 도메인 객체를 생성합니다. 여러 개의 프로젝트 URL이나 자격증은 세미콜론(`;`)으로 구분된 단일 문자열로 변환하여 저장합니다. `Resume` 저장 후, 연관된 `ResumeLanguage` 정보들도 함께 저장합니다.
    *   `getResume()`: 사용자 ID를 기반으로 `ResumeRepository`에서 이력서 정보를 조회하고, `ResumeLanguageRepository`에서 관련 언어 정보를 조회한 뒤, 이 둘을 조합하여 `ResumeResponse` DTO를 생성하여 반환합니다.

### 3.3. `domain` - Core Business Logic

이력서 관련 핵심 도메인 모델입니다.

*   **`Resume.java`**: 이력서의 핵심 정보를 담는 도메인 객체입니다. `User`와 연관 관계를 맺습니다.
*   **`ResumeLanguage.java`**: 사용 가능 언어 정보를 표현하는 도메인 객체입니다. `Resume`과 연관 관계를 맺습니다.

### 3.4. `infrastructure` - Persistence Adapter

데이터베이스 연동을 담당합니다.

#### **`ResumeRepositoryImpl.java` & `ResumeLanguageRepositoryImpl.java`**

각각의 Repository Port에 대한 구현체로, Spring Data JPA를 사용하여 DB 작업을 처리합니다.

#### **`ResumeJpaRepository.java` & `ResumeLanguageJpaRepository.java`**

Spring Data JPA 인터페이스입니다. `findByUser`, `findAllByResume` 등 필요한 쿼리 메서드를 정의합니다.

#### **`ResumeEntity.java` & `ResumeLanguageEntity.java`**

DB의 `RESUME`, `RESUME_LANGUAGE` 테이블과 매핑되는 JPA 엔티티 클래스입니다. 각 도메인 객체와 상호 변환하는 로직을 포함합니다.
