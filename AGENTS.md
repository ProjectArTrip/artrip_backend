---
## ⚠️ AI 개발 지침 (중요)

**No Code Writing Principle**
- AI never directly writes code or creates/modifies files.
- AI does not execute code arbitrarily.
- Users learn through clone coding and directly input code.
- Web crawling is prohibited, and data is retrieved through API integration as a top priority.

  **Most Important**
- Development is conducted using the Test-Driven Development (TDD) method.
- Major tests are written in an edge case format.
- Design tests with the red, green, and refactor phases in mind.
- Test-driven development avoids adding excessive functionality. For example, avoid using h2 or ArgumentCaptor.
- Data flow proceeds in the following order: entity -> repository -> service -> controller.
- Code should be passed from the controller to the service, and the controller is solely for display purposes; service code is not exposed.
- Business logic and presentation logic are separated.
- Builder pattern and setter usage are prohibited.
- Static factory methods are maintained as the default.

  **The Role of AI**
  1. Provide code explanations and guidance
  2. Architectural and design advice
  3. Provide code examples (for copying and use)
  4. Debugging and code review
  5. Explain library usage
  6. Suggest best practices
  7. Direct file creation/modification is prohibited
  8. Command execution is prohibited
  9. Automated implementation is prohibited
  10. Web crawling is prohibited
  11. Answers should consider reuse and scalability of existing code as a basic rule.
  12. Answers should be answered by referring to the .cursorrules file.
  13. Do not arbitrarily change method names; only modify existing algorithms.
  14. Encourage users to use free plans as much as possible. For example, AWS offers free plans first, preventing the use of Lambda@Edge.
  
**응답 방식**
- 코드는 마크다운 코드 블록으로 제시
- "이 코드를 `파일경로`에 작성하세요" 형식으로 안내
- 단계별로 명확하게 설명
- 각 코드의 의미와 동작 원리를 상세히 설명

---

# PRD (Product Requirements Document)
## 해외전시 앱 - ArtTrip

---

## 1. 프로젝트 개요

### 1.1 프로젝트 명
**ArtTrip** - 글로벌 전시회 탐색 및 예약 플랫폼

### 1.2 프로젝트 목적
전 세계의 다양한 전시회 정보를 한 곳에서 탐색하고, 예약까지 연결할 수 있는 통합 플랫폼을 제공하여 사용자의 문화 예술 경험을 확대한다.

### 1.3 비전
"전 세계 어디서나, 모든 전시를 한 눈에" - 글로벌 전시 문화를 누구나 쉽게 접근할 수 있도록 만드는 것

---

## 2. 타겟 사용자

### 2.1 Primary Users
- **문화 예술 애호가**: 전시회를 즐기고 여행과 결합하고 싶은 사람들
- **해외 여행자**: 여행지에서 특별한 문화 경험을 원하는 사람들
- **미술/예술 전공자**: 학습 및 연구 목적으로 해외 전시를 찾는 학생 및 연구자

### 2.2 Secondary Users
- **큐레이터 및 갤러리 관계자**: 트렌드 파악 및 벤치마킹
- **여행사**: 문화 관광 상품 개발을 위한 정보 수집

### 2.3 User Persona

**Persona 1: 김예진 (29세, 디자이너)**
- 연 2-3회 해외여행을 가며, 여행지의 미술관과 전시회를 반드시 방문
- Instagram에 전시 후기를 공유하는 것을 즐김
- 사전 계획을 철저히 세우며, 예약이 필요한 전시는 미리 확보

**Persona 2: 이준호 (35세, 미술 큐레이터)**
- 업무상 해외 전시 트렌드를 지속적으로 모니터링
- 특정 작가나 테마의 전시를 추적
- 전시 정보의 정확성과 상세함을 중요시

---

## 3. 핵심 가치 제안 (Value Proposition)

1. **통합 검색**: 전 세계 전시 정보를 한 곳에서 검색
2. **지역별 탐색**: 유럽, 아메리카, 아시아 등 지역별 필터링
3. **직접 예약 연결**: 공식 예약 링크 제공으로 원활한 예약 경험
4. **큐레이션**: 추천 전시 및 인기 전시 정보 제공
5. **여행 계획 지원**: 위치 기반 전시 탐색으로 여행 일정에 통합 가능

---

## 4. 주요 기능 (Features)

### 4.1 Core Features (MVP)

#### 4.1.1 전시 검색 및 필터링
- **지역별 필터**: 유럽, 북미, 남미, 아시아, 오세아니아, 중동/아프리카
- **국가별 필터**: 각 지역 내 국가 세분화
- **도시별 필터**: 주요 도시 선택
- **날짜 필터**: 진행 중, 예정, 종료된 전시
- **카테고리 필터**:
    - 현대미술, 클래식 아트, 사진, 조각
    - 디자인, 건축, 패션
    - 특별 전시, 비엔날레, 아트페어
- **키워드 검색**: 작가명, 전시명, 갤러리명 검색

#### 4.1.2 전시 상세 정보
- 전시명 (한국어/영어)
- 전시 기간 (시작일/종료일)
- 장소 (갤러리/미술관명, 주소)
- 전시 설명 (개요, 주요 작품)
- 이미지 갤러리 (포스터, 전시 작품 이미지)
- 운영 시간
- 입장료 정보
- 공식 웹사이트 링크
- **예약 링크** (티켓 예약 페이지로 직접 연결)
- 위치 지도 (Google Maps 연동)

#### 4.1.3 홈 화면
- 추천 전시 (Featured Exhibitions)
- 지역별 인기 전시 (Top by Region)
- 곧 종료되는 전시 (Ending Soon)
- 새로운 전시 (Newly Added)

#### 4.1.4 예약 링크 제공
- 공식 티켓 판매처 링크
- 외부 예약 플랫폼 연동 (예: Tiqets, GetYourGuide, 공식 사이트)
- 예약 가능 여부 표시

### 4.2 Secondary Features (Post-MVP)

#### 4.2.1 사용자 계정
- 회원가입 / 로그인 (이메일, 소셜 로그인)
- 프로필 관리

#### 4.2.2 위시리스트
- 관심 전시 저장
- 저장한 전시 목록 관리
- 알림 설정 (전시 시작 전 알림)

#### 4.2.3 일정 관리
- 캘린더 뷰로 계획한 전시 보기
- 여행 일정에 전시 추가
- 일정 공유 기능

#### 4.2.4 리뷰 및 평가
- 사용자 리뷰 작성
- 별점 평가
- 사진 첨부

#### 4.2.5 개인화 추천
- 사용자 취향 기반 전시 추천
- 위치 기반 추천
- 저장한 전시 기반 유사 전시 추천

#### 4.2.6 소셜 기능
- 전시 공유 (SNS)
- 친구와 일정 공유
- 커뮤니티 게시판

---

## 5. 기술 요구사항

### 5.1 Frontend
- **Framework**: React 18+ with TypeScript
- **State Management**: Redux Toolkit / Zustand
- **Styling**: Tailwind CSS / Styled-components
- **Routing**: React Router
- **API Communication**: Axios / React Query
- **Map Integration**: Google Maps API / Mapbox
- **i18n**: react-i18next (다국어 지원)
- **Build Tool**: Vite / Create React App

### 5.2 Backend
- **Framework**: Java Spring Boot 3.x
    - Spring Web (REST API)
    - Spring Data JPA (데이터베이스 액세스)
    - Spring Security (인증/인가)
    - Spring Data Redis (캐싱)
    - Spring Data Elasticsearch (검색)
- **Database**:
    - **MySQL 8.0+**: 주 데이터베이스 (전시, 장소, 사용자 정보)
    - **Redis**: 캐싱, 세션 관리, 실시간 데이터
    - **Elasticsearch**: 전시 검색, 전문 검색, 자동완성
- **Authentication**: JWT + Spring Security
- **File Storage**: AWS S3 / 로컬 스토리지 (이미지 저장)
- **API Documentation**: Swagger / SpringDoc OpenAPI

### 5.3 검색 엔진 (Elasticsearch)
- **용도**:
    - 전시명, 작가명, 갤러리명 전문 검색
    - 다국어 검색 지원 (한국어, 영어 형태소 분석)
    - 자동완성 (autocomplete)
    - 필터링 및 정렬 최적화
    - 검색 결과 하이라이팅
- **인덱스 구조**:
    - `exhibitions` 인덱스: 전시 정보
    - `venues` 인덱스: 장소 정보
- **분석기**:
    - 한국어: nori analyzer
    - 영어: standard analyzer

### 5.4 캐싱 전략 (Redis)
- **사용 목적**:
    - API 응답 캐싱 (인기 전시, 추천 전시)
    - 세션 저장
    - Rate Limiting
    - 실시간 통계 (조회수, 인기도)
- **캐시 키 설계**:
    - `exhibition:{id}`: 전시 상세 정보
    - `popular:exhibitions:{region}`: 지역별 인기 전시
    - `user:session:{userId}`: 사용자 세션

### 5.5 Data Sources
- 미술관/갤러리 공식 API 연동
- 웹 스크래핑 (합법적 범위 내)
- 수동 큐레이션
- 파트너십을 통한 데이터 제공

### 5.6 Infrastructure & DevOps
- **Containerization**: Docker & Docker Compose
    - Frontend Container (Nginx + React build)
    - Backend Container (Spring Boot JAR)
    - MySQL Container
    - Redis Container
    - Elasticsearch Container
- **Orchestration** (Optional): Kubernetes (프로덕션 환경)
- **Hosting**:
    - AWS EC2 / ECS (컨테이너 호스팅)
    - AWS RDS (MySQL 관리형 서비스 옵션)
    - AWS ElastiCache (Redis 관리형 서비스 옵션)
    - AWS Elasticsearch Service (Elasticsearch 관리형 서비스 옵션)
- **CI/CD**:
    - GitHub Actions
    - Docker Hub / AWS ECR (컨테이너 레지스트리)
- **Monitoring**:
    - Spring Boot Actuator (헬스체크, 메트릭)
    - Prometheus + Grafana (모니터링)
    - ELK Stack (로그 수집 및 분석)
    - Sentry (에러 트래킹)
    - Google Analytics (사용자 분석)


### 5.7 환경 구성 및 배포 전략
#### 5.7.1 Docker Compose 환경별 구성프로젝트는 3가지 환경(로컬, 스테이징, 프로덕션)으로 구분하여 관리합니다.
**파일 구조**:
- `docker-compose.yml`: 기본 서비스 정의 (공통)
- `docker-compose.override.yml`: 로컬 개발 환경 (Git 추적, 개발자 편의 설정)
- `docker-compose.stage.yml`: 스테이징 환경 (테스트 및 QA)
- `docker-compose.prod.yml`: 프로덕션 환경 (운영 환경)
  **환경별 실행 명령어**:
```bash
# 로컬 개발 환경 (override 자동 적용)docker-compose up -d
# 스테이징 환경docker-compose -f docker-compose.yml -f docker-compose.stage.yml up -d
# 프로덕션 환경docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d
```**환경별 주요 차이점**:
| 항목 | 로컬 (override) | 스테이징 (stage) | 프로덕션 (prod) |
|------|----------------|-----------------|----------------|
| DB 데이터 | 로컬 볼륨 마운트 | 명명된 볼륨 | 외부 RDS 연결 |
| Redis | 단일 인스턴스 | 단일 인스턴스 | Redis Cluster/ElastiCache |
| Elasticsearch | 단일 노드 | 단일 노드 | 다중 노드 클러스터 |
| 로그 레벨 | DEBUG | INFO | WARN/ERROR |
| Spring Profile | default | stage | prod |
| 포트 노출 | 모두 노출 | 제한적 노출 | 최소 노출 |
| 리소스 제한 | 없음 | 중간 | 엄격 |

#### 5.7.2 Spring Boot Profile 구성

**설정 파일 구조**:
src/main/resources/
├── application.yml # 기본 설정 (공통)
├── application-stage.yml # 스테이징 환경 설정
└── application-prod.yml # 프로덕션 환경 설정

**Profile 활성화 방법**:
```yaml
# docker-compose.stage.yml
services:
  backend:
    environment:
      - SPRING_PROFILES_ACTIVE=stage

# docker-compose.prod.yml
services:
  backend:
    environment:
      - SPRING_PROFILES_ACTIVE=prod
```

**환경별 설정 관리**:
- **application.yml**: 공통 설정 (JPA, 로깅 패턴 등)
- **application-stage.yml**:
    - 스테이징 DB 연결 정보
    - Redis/Elasticsearch 스테이징 호스트
    - 테스트용 외부 API 키
    - 로그 레벨: INFO
- **application-prod.yml**:
    - 프로덕션 DB 연결 정보 (환경변수 사용)
    - Redis/Elasticsearch 프로덕션 호스트
    - 운영용 외부 API 키 (환경변수 사용)
    - 로그 레벨: WARN/ERROR
    - 보안 설정 강화 (CORS, HTTPS 등)

**민감 정보 관리**:
- 로컬: `application.yml`에 직접 입력 (Git 제외)
- 스테이징/프로덕션: 환경변수로 주입 (GitHub Secrets 활용)

```yaml
# application-prod.yml 예시
spring:
  datasource:
    url: ${DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
```
**CI 파이프라인 (ci.yml)**:
- 트리거: Pull Request 생성/업데이트
- 단계:
    1. Java 17 설정
    2. Gradle 빌드
    3. 단위 테스트 실행
    4. 통합 테스트 실행 (Docker Compose 활용)
    5. 코드 품질 검사 (SonarQube 옵션)
    6. 빌드 아티팩트 업로드

**스테이징 배포 파이프라인 (deploy-stage.yml)**:
- 트리거: `stage` 브랜치에 푸시
- 단계:
    1. CI 파이프라인 재실행
    2. Docker 이미지 빌드 (태그: `stage-{commit-sha}`)
    3. Docker Hub/ECR에 푸시
    4. 스테이징 서버 SSH 접속
    5. `docker-compose -f docker-compose.yml -f docker-compose.stage.yml pull`
    6. `docker-compose -f docker-compose.yml -f docker-compose.stage.yml up -d`
    7. 헬스체크 확인 (`/api/health`)
    8. Slack/Discord 알림

**프로덕션 배포 파이프라인 (deploy-prod.yml)**:
- 트리거: `main` 브랜치에 태그 생성 (예: `v1.0.0`)
- 단계:
    1. CI 파이프라인 재실행
    2. Docker 이미지 빌드 (태그: `latest`, `{version}`)
    3. Docker Hub/ECR에 푸시
    4. 프로덕션 서버 SSH 접속
    5. Blue-Green 배포 또는 Rolling Update
    6. `docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d`
    7. 헬스체크 및 스모크 테스트
    8. 롤백 준비 (이전 이미지 보관)
    9. Slack/Discord 알림

**GitHub Secrets 설정**:
- `DOCKER_USERNAME`, `DOCKER_PASSWORD`: Docker Hub 인증
- `STAGE_SERVER_HOST`, `STAGE_SERVER_USER`, `STAGE_SERVER_SSH_KEY`: 스테이징 서버 SSH
- `PROD_SERVER_HOST`, `PROD_SERVER_USER`, `PROD_SERVER_SSH_KEY`: 프로덕션 서버 SSH
- `DB_URL_STAGE`, `DB_PASSWORD_STAGE`: 스테이징 DB 정보
- `DB_URL_PROD`, `DB_PASSWORD_PROD`: 프로덕션 DB 정보
- `SLACK_WEBHOOK_URL`: 배포 알림용

#### 5.7.3 CI/CD 파이프라인 (GitHub Actions)

**워크플로우 구조**:
.github/workflows/
├── ci.yml # PR 시 빌드 및 테스트
├── deploy-stage.yml # stage 브랜치 푸시 시 스테이징 배포
└── deploy-prod.yml # main 브랜치 태그 시 프로덕션 배포

**배포 흐름**:
1. `feature/*` → `develop`: PR 병합 (CI만 실행)
2. `develop` → `stage`: PR 병합 (스테이징 자동 배포)
3. `stage` → `main`: PR 병합 + 태그 생성 (프로덕션 수동 배포)

#### 5.7.5 환경별 접근 URL (예시)

- **로컬**: `http://localhost:8080`
- **스테이징**: `https://stage-api.artrip.com`
- **프로덕션**: `https://api.artrip.com`

#### 5.7.6 모니터링 및 로깅

**환경별 모니터링 도구**:
- **로컬**: Spring Boot Actuator (`/actuator`)
- **스테이징**:
    - Prometheus + Grafana (메트릭)
    - ELK Stack (로그 수집)
- **프로덕션**:
    - AWS CloudWatch / Datadog (통합 모니터링)
    - Sentry (에러 트래킹)
    - Uptime Robot (가용성 모니터링)

**로그 관리**:
- 로컬: 콘솔 출력
- 스테이징: 파일 로그 + ELK
- 프로덕션: CloudWatch Logs + S3 아카이빙

---

```
┌─────────────────────────────────────────────────────────────┐
│                         Client Layer                         │
├─────────────────────────────────────────────────────────────┤
│              React App (TypeScript)                          │
│  - React Router  - Redux/Zustand  - Axios/React Query      │
└────────────────────────┬────────────────────────────────────┘
                         │ HTTPS/REST API
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                      API Gateway Layer                       │
├─────────────────────────────────────────────────────────────┤
│                  Spring Boot Backend                         │
│  - Spring Web (REST Controllers)                            │
│  - Spring Security (JWT Authentication)                     │
│  - Swagger/OpenAPI Documentation                            │
└──────┬──────────────┬──────────────┬──────────────┬─────────┘
       │              │              │              │
       ▼              ▼              ▼              ▼
┌──────────┐   ┌──────────┐   ┌──────────┐   ┌──────────┐
│  MySQL   │   │  Redis   │   │Elasticsearch│ │  AWS S3  │
│          │   │          │   │          │   │          │
│ 전시정보  │   │  캐싱     │   │  검색엔진  │   │ 이미지    │
│ 사용자    │   │  세션     │   │  자동완성  │   │ 저장소    │
│ 예약정보  │   │  통계     │   │          │   │          │
└──────────┘   └──────────┘   └──────────┘   └──────────┘
```

### 6.2 Docker 컨테이너 구성

```yaml
# docker-compose.yml 구조
services:
  frontend:
    - Nginx + React Build
    - Port: 80, 443

  backend:
    - Spring Boot Application
    - Port: 8080
    - Depends on: mysql, redis, elasticsearch

  mysql:
    - MySQL 8.0
    - Port: 3306
    - Volume: persistent storage

  redis:
    - Redis 7.x
    - Port: 6379

  elasticsearch:
    - Elasticsearch 8.x
    - Port: 9200, 9300
    - Volume: persistent storage
```

### 6.3 데이터 플로우

#### 6.3.1 전시 검색 플로우
```
User Request (검색어: "피카소")
    ↓
React Frontend → Backend API (/api/exhibitions/search?q=피카소)
    ↓
Spring Boot Controller
    ↓
[Cache Check] Redis에서 캐시 확인
    ↓
    ├─ Cache Hit → Redis에서 반환
    │
    └─ Cache Miss
        ↓
    Elasticsearch Query (전문 검색)
        ↓
    MySQL Join (상세 정보 보강)
        ↓
    Redis 캐싱 (TTL: 1시간)
        ↓
    Response → Frontend
```

#### 6.3.2 전시 상세 조회 플로우
```
User Request (전시 ID: 123)
    ↓
GET /api/exhibitions/123
    ↓
[1] Redis 캐시 확인 (exhibition:123)
    ↓
    ├─ Cache Hit → 즉시 반환
    │
    └─ Cache Miss
        ↓
    [2] MySQL 조회
        ↓
    [3] Redis 캐싱 (TTL: 24시간)
        ↓
    [4] 조회수 증가 (Redis INCR)
        ↓
    Response
```

### 6.4 API 설계 (RESTful)

#### 전시 관련 API
```
GET    /api/exhibitions              # 전시 목록 (필터링, 페이징)
GET    /api/exhibitions/{id}         # 전시 상세
GET    /api/exhibitions/search       # 검색 (Elasticsearch)
GET    /api/exhibitions/featured     # 추천 전시
GET    /api/exhibitions/popular      # 인기 전시
GET    /api/exhibitions/ending-soon  # 종료 임박
POST   /api/exhibitions              # 전시 등록 (관리자)
PUT    /api/exhibitions/{id}         # 전시 수정 (관리자)
DELETE /api/exhibitions/{id}         # 전시 삭제 (관리자)
```

#### 장소 관련 API
```
GET    /api/venues                   # 장소 목록
GET    /api/venues/{id}              # 장소 상세
GET    /api/venues/{id}/exhibitions  # 특정 장소의 전시 목록
```

#### 사용자 관련 API (Post-MVP)
```
POST   /api/auth/signup              # 회원가입
POST   /api/auth/login               # 로그인
POST   /api/auth/logout              # 로그아웃
GET    /api/users/me                 # 내 정보
GET    /api/users/me/favorites        # 즐겨찾기
POST   /api/users/me/favorites/{id}   # 즐겨찾기 추가
DELETE /api/users/me/favorites/{id}   # 즐겨찾기 삭제
```

### 6.5 데이터베이스 스키마 (MySQL)

#### exhibitions 테이블
```sql
CREATE TABLE exhibit (
    exhibit_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    exhibit_hall_id BIGINT NULL,
    title VARCHAR(255) NULL,
    description VARCHAR(255) NULL,
    poster_url VARCHAR(255) NULL,
    ticket_url VARCHAR(255) NULL,
    start_date DATETIME(6) NULL,
    end_date DATETIME(6) NULL,
    created_at DATETIME(6) NULL,
    updated_at DATETIME(6) NULL,
    status ENUM('FINISHED', 'ONGOING', 'UPCOMING') NOT NULL,
    genre ENUM('PAINTING', 'PHOTOGRAPHY', 'SCULPTURE') NOT NULL,
    INDEX idx_exhibit_hall_id (exhibit_hall_id)
);

```
#### exhibit_keyword 테이블
```sql
CREATE TABLE exhibit_keyword (
    exhibit_keyword_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    exhibit_id BIGINT NOT NULL,
    keyword_id BIGINT NOT NULL,
    INDEX idx_exhibit_id (exhibit_id),
    INDEX idx_keyword_id (keyword_id),
    FOREIGN KEY (exhibit_id) REFERENCES exhibit(exhibit_id),
    FOREIGN KEY (keyword_id) REFERENCES keyword(keyword_id)
)
```
#### exhibit_hall 테이블
```sql
CREATE TABLE exhibit_hall (
    exhibit_hall_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(255) NULL,
    country VARCHAR(255) NULL,
    region VARCHAR(255) NULL,
    phone VARCHAR(255) NULL,
    homepage_url VARCHAR(255) NULL,
    opening_hours VARCHAR(255) NULL,
    closed_days DATETIME(6) NULL,
    is_domestic BIT(1) NULL
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NULL,
    latitude DECIMAL(10,7) NULL,
    longitude DECIMAL(10,7) NULL
);
```

#### favorite 테이블
```sql
CREATE TABLE favorite (
    favorite_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    exhibit_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    INDEX idx_exhibit_id (exhibit_id),
    INDEX idx_user_id (user_id),
    FOREIGN KEY (exhibit_id) REFERENCES exhibit(exhibit_id),
    FOREIGN KEY (user_id) REFERENCES user(user_id)
);
```
```sql
CREATE TABLE keyword (
    keyword_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type ENUM('GENRE', 'STYLE') NOT NULL,
    `group` VARCHAR(255) NOT NULL
);
```
```sql
CREATE TABLE user (
    user_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NULL,
    role ENUM('ADMIN', 'USER') NOT NULL,
    push_token VARCHAR(255) NULL,
    stamp_num TINYINT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NULL
);
```
```sql
CREATE TABLE user_keyword (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    keyword_id BIGINT NULL,
    created_at DATETIME(6) NOT NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_keyword_id (keyword_id),
    FOREIGN KEY (user_id) REFERENCES `user`(user_id),
    FOREIGN KEY (keyword_id) REFERENCES keyword(keyword_id)
)
```
```sql
CREATE TABLE search_history (
    search_history_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    content VARCHAR(255) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    INDEX idx_user_id (user_id),
    FOREIGN KEY (user_id) REFERENCES `user`(user_id)
)
```

### 6.6 Elasticsearch 매핑

```json
{
  "exhibitions": {
    "mappings": {
      "properties": {
        "id": { "type": "long" },
        "title_ko": {
          "type": "text",
          "analyzer": "nori"
        },
        "title_en": {
          "type": "text",
          "analyzer": "standard"
        },
        "description_ko": {
          "type": "text",
          "analyzer": "nori"
        },
        "category": { "type": "keyword" },
        "start_date": { "type": "date" },
        "end_date": { "type": "date" },
        "venue": {
          "properties": {
            "name_ko": { "type": "text", "analyzer": "nori" },
            "city": { "type": "keyword" },
            "country": { "type": "keyword" },
            "region": { "type": "keyword" }
          }
        },
        "status": { "type": "keyword" }
      }
    }
  }
}
```

---

## 7. 화면 구성 (Screen Flow)

### 7.1 Main Screens

1. **홈 화면**
    - 상단: 검색바
    - 추천 전시 캐러셀
    - 지역별 인기 전시 섹션
    - 곧 종료되는 전시 섹션

2. **검색/필터 화면**
    - 필터 사이드바 (지역, 날짜, 카테고리)
    - 전시 리스트 (그리드/리스트 뷰 전환)
    - 정렬 옵션 (최신순, 인기순, 종료임박순)

3. **전시 상세 화면**
    - 메인 이미지/갤러리
    - 전시 정보 (제목, 기간, 장소)
    - 상세 설명
    - 운영 시간 및 입장료
    - **예약하기 버튼** (외부 링크)
    - 위치 지도
    - 위시리스트 버튼
    - 공유 버튼

4. **지역별 탐색 화면**
    - 세계 지도 인터랙션
    - 지역 선택 시 해당 지역 전시 표시

5. **마이페이지** (Post-MVP)
    - 위시리스트
    - 예약한 전시
    - 작성한 리뷰
    - 설정

### 7.2 User Flow Example

```
홈 화면 진입
   ↓
검색 또는 추천 전시 클릭
   ↓
필터 적용 (지역: 유럽, 국가: 프랑스, 도시: 파리)
   ↓
전시 리스트 확인
   ↓
관심 전시 선택
   ↓
상세 정보 확인
   ↓
"예약하기" 버튼 클릭 → 외부 예약 사이트로 이동
   ↓
예약 완료 후 앱으로 복귀 (위시리스트에 저장)
```

---

## 8. 기능 요구사항 (Functional Requirements)

### 8.1 검색 및 필터링
- FR-001: 사용자는 지역(대륙)별로 전시를 필터링할 수 있어야 한다
- FR-002: 사용자는 국가별로 전시를 필터링할 수 있어야 한다
- FR-003: 사용자는 도시별로 전시를 필터링할 수 있어야 한다
- FR-004: 사용자는 전시 기간(진행 중/예정/종료)으로 필터링할 수 있어야 한다
- FR-005: 사용자는 전시 카테고리로 필터링할 수 있어야 한다
- FR-006: 사용자는 키워드로 전시를 검색할 수 있어야 한다
- FR-007: 사용자는 검색 결과를 정렬(최신순, 인기순, 종료임박순)할 수 있어야 한다

### 8.2 전시 정보 제공
- FR-008: 각 전시의 상세 정보를 확인할 수 있어야 한다
- FR-009: 전시 이미지를 갤러리 형태로 볼 수 있어야 한다
- FR-010: 전시 장소의 위치를 지도에서 확인할 수 있어야 한다
- FR-011: 공식 웹사이트 링크가 제공되어야 한다

### 8.3 예약 링크
- FR-012: 예약 가능한 전시는 "예약하기" 버튼이 표시되어야 한다
- FR-013: 예약하기 버튼 클릭 시 공식 예약 페이지로 이동해야 한다
- FR-014: 예약 불가 시 "예약 불가" 또는 "현장 구매" 표시가 있어야 한다

### 8.4 데이터 관리
- FR-015: 전시 정보는 실시간 또는 일정 주기로 업데이트되어야 한다
- FR-016: 종료된 전시는 별도 아카이브로 분류되어야 한다

---

## 9. 비기능 요구사항 (Non-Functional Requirements)

### 9.1 성능
- NFR-001: 페이지 로딩 시간은 3초 이내여야 한다
- NFR-002: 검색 결과는 2초 이내에 표시되어야 한다
- NFR-003: 이미지는 lazy loading으로 최적화되어야 한다

### 9.2 사용성
- NFR-004: 모바일, 태블릿, 데스크톱에서 반응형으로 동작해야 한다
- NFR-005: 직관적인 UI/UX로 첫 방문자도 쉽게 사용 가능해야 한다
- NFR-006: 한국어와 영어를 기본 지원해야 한다

### 9.3 보안
- NFR-007: 사용자 정보는 암호화되어 저장되어야 한다
- NFR-008: HTTPS 프로토콜을 사용해야 한다
- NFR-009: 외부 링크는 새 탭에서 열리며 보안 경고를 표시해야 한다

### 9.4 확장성
- NFR-010: 월 10만 명의 사용자를 지원할 수 있어야 한다
- NFR-011: 데이터베이스는 수평 확장이 가능해야 한다

### 9.5 유지보수
- NFR-012: 코드는 TypeScript로 타입 안정성을 확보해야 한다
- NFR-013: 컴포넌트는 재사용 가능하게 설계되어야 한다
- NFR-014: API는 버전 관리되어야 한다

---

## 10. 데이터 모델 (Data Model)

### 10.1 Exhibition (전시)
```
{
  id: string
  title_ko: string
  title_en: string
  description_ko: string
  description_en: string
  category: string[]
  start_date: Date
  end_date: Date
  venue_id: string
  images: string[]
  poster_image: string
  admission_fee: {
    adult: number
    student: number
    child: number
    currency: string
  }
  opening_hours: {
    monday: string
    tuesday: string
    ...
  }
  official_website: string
  booking_url: string
  booking_available: boolean
  status: 'upcoming' | 'ongoing' | 'ended'
  created_at: Date
  updated_at: Date
}
```

### 10.2 Venue (장소)
```
{
  id: string
  name_ko: string
  name_en: string
  type: 'museum' | 'gallery' | 'art_center' | 'other'
  address: {
    country: string
    city: string
    street: string
    postal_code: string
  }
  location: {
    latitude: number
    longitude: number
  }
  region: 'europe' | 'north_america' | 'south_america' | 'asia' | 'oceania' | 'middle_east_africa'
  contact: {
    phone: string
    email: string
    website: string
  }
}
```

### 10.3 User (사용자) - Post-MVP
```
{
  id: string
  email: string
  username: string
  profile_image: string
  preferences: {
    favorite_categories: string[]
    favorite_regions: string[]
  }
  wishlist: string[] // exhibition IDs
  created_at: Date
}
```

---

## 11. 개발 로드맵

### Phase 1: MVP (3개월)
**Goal**: 핵심 기능 구현 및 런칭

- Week 1-2: 프로젝트 세팅, 데이터 모델 확정
- Week 3-4: 홈 화면, 검색/필터 UI 개발
- Week 5-6: 전시 상세 페이지 개발
- Week 7-8: 지역별 탐색 기능 개발
- Week 9-10: 예약 링크 연동 및 테스트
- Week 11: QA 및 버그 수정
- Week 12: 베타 런칭

**MVP Deliverables**:
- ✅ 전시 검색 및 필터링
- ✅ 전시 상세 정보 제공
- ✅ 예약 링크 제공
- ✅ 지역별 탐색
- ✅ 반응형 웹 디자인
- ✅ 최소 500개 이상의 전시 데이터

### Phase 2: 사용자 기능 추가 (2개월)
**Goal**: 사용자 경험 개선 및 개인화

- 회원가입/로그인
- 위시리스트
- 알림 기능
- 일정 관리

### Phase 3: 소셜 및 커뮤니티 (2개월)
**Goal**: 커뮤니티 활성화

- 리뷰 시스템
- 사용자 평가
- 소셜 공유 기능
- 커뮤니티 게시판

### Phase 4: 고도화 (ongoing)
**Goal**: AI 기반 추천 및 파트너십 확대

- AI 기반 개인화 추천
- 모바일 앱 개발
- 파트너 갤러리/미술관 대시보드
- 다국어 확장 (일본어, 중국어, 프랑스어 등)

---

## 12. 성공 지표 (KPI)

### 12.1 사용자 지표
- MAU (Monthly Active Users): 첫 6개월 내 10,000명 달성
- 재방문율: 40% 이상
- 평균 세션 시간: 5분 이상

### 12.2 참여 지표
- 전시 상세 페이지 조회 수
- 예약 링크 클릭률: 15% 이상
- 위시리스트 저장률: 20% 이상

### 12.3 비즈니스 지표
- 파트너십 체결 갤러리/미술관 수: 50개 이상
- 예약 전환율 (클릭 → 실제 예약): 추적

---

## 13. 리스크 및 대응 방안

### 13.1 데이터 수집의 어려움
**리스크**: 전시 정보 수집이 어렵거나 저작권 문제 발생
**대응**:
- 공개 API 우선 활용
- 파트너십을 통한 공식 데이터 제공
- 사용자 제보 시스템 구축

### 13.2 예약 링크 유효성
**리스크**: 외부 예약 링크가 변경되거나 만료됨
**대응**:
- 주기적인 링크 유효성 검증 스크립트
- 사용자 신고 시스템
- 여러 예약 옵션 제공

### 13.3 경쟁 서비스
**리스크**: 유사 서비스와의 경쟁
**대응**:
- 특화된 큐레이션으로 차별화
- 한국어 사용자 맞춤 서비스
- 커뮤니티 기반 콘텐츠 강화

### 13.4 수익 모델 부재
**리스크**: 초기 수익 창출 어려움
**대응** (향후 고려사항):
- 제휴 수수료 모델 (예약 시 커미션)
- 프리미엄 구독 (광고 제거, 우선 알림 등)
- 갤러리 프로모션 광고

---

## 14. 부록

### 14.1 참고 서비스
- **Artsy**: 온라인 아트 마켓플레이스 및 전시 정보
- **Timeout**: 지역별 문화 이벤트 정보
- **Google Arts & Culture**: 온라인 전시 및 미술관 정보
- **Eventbrite**: 이벤트 예약 플랫폼

### 14.2 API 후보
- Google Maps API (지도)
- Museum API (미술관 컬렉션 정보)
- Ticketmaster API (티켓 정보)
- 각 미술관/갤러리 공식 API

### 14.3 디자인 레퍼런스
- Minimalist & Clean UI
- 이미지 중심 레이아웃
- 모던한 타이포그래피
- 컬러: 흰색/검정 베이스 + 포인트 컬러(아트 이미지에서 추출)

---

## 변경 이력
| 날짜 | 버전 | 내용 | 작성자 |
|------|------|------|--------|
| 2025-10-16 | 1.0 | 초안 작성 | - |
| 2025-10-16 | 1.1 | 기술 스택 확정 (Docker, Spring Boot, MySQL, Redis, Elasticsearch) 및 시스템 아키텍처 추가 | - |

---

**문서 승인자**: -
**최종 수정일**: 2025-10-16

