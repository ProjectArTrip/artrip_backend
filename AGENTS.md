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
  12. Do not arbitrarily change method names; only modify existing algorithms.
  
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
- **Authentication**: JWT + Spring Security
- **File Storage**: AWS S3 / 로컬 스토리지 (이미지 저장)
- **API Documentation**: Swagger / SpringDoc OpenAPI

