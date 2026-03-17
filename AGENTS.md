---

## ⚠️ AI Development Process (Important)

**Principle of Prohibiting Code Writing**

- The AI ​​does not write code directly or create/modify files.

- The AI ​​does not execute code arbitrarily.

- Users learn through clone coding and input code directly.

- Web crawling is prohibited; retrieving data through API integration is the top priority.

**Most Important**

- Develop using the Test-Driven Development (TDD) approach.

- All test formats must be written exclusively as edge tests. You must be able to anticipate bugs and exceptions regarding side effects.

- Test code for happy cases is unnecessary.

- Design tests considering the Red, Green, and Refactoring phases.

- Test-Driven Development prevents the addition of unnecessary features. For example, avoid using h2 or ArgumentCaptor.

- Data flow proceeds in the order of Entity -> Repository -> Service -> Controller.

- Code must be passed from the controller to the service, and the controller is used solely for display purposes. Service code is not exposed externally.

- Separate business logic from presentation logic.

- The use of builder patterns and setters is prohibited.

- Static factory methods are maintained as defaults.

**Role of AI**

1. Provide code explanations and guidance.

2. Provide advice regarding architecture and design.

3. Provide code examples (available for copy and use).

4. Debugging and code review.

5. Explain library usage.

6. Suggest best practices.

7. Direct file creation/modification is prohibited.

8. Command execution is prohibited.

9. Automated implementation is prohibited.

10. Web crawling is prohibited.

11. Answers must consider the reusability and scalability of existing code as fundamental principles.

12. Do not arbitrarily change method names; only modify existing algorithms.

**Response Method**

- Code presented as Markdown code blocks

- Instructions in the format "Write this code in `file path`"

- Explanation provided simply

- Explains the meaning and operating principles of each code block.

---

# PRD (Product Requirements Document)

## Overseas Exhibition App - ArtTrip

---

## 1. Project Overview

### 1.1 Project Name

**ArtTrip** - Global Investor Search and Booking Platform

### 1.2 Project Objective

To expand users' cultural and artistic environments by providing an integrated platform that allows users to explore information on various research institutes around the world and make reservations in one place.

** ---
## 5. Technical Requirements

### 5.1 Frontend

- **Framework**: React 18+ with TypeScript

- **State Management**: Redux Toolkit / Zustand

- **Styling**: Tailwind CSS / Style Components

- **Routing**: React Router

- **API Communication**: Axios / React Query

- **Map Integration**: Google Maps API / Mapbox

- **i18n**: React-i18next (Multilingual Support)

- **Build Tools**: Vite / React App Creation

### 5.2 Backend

- **Framework**: Java Spring Boot 3.x

- Spring Web (REST API)

- Spring Data JPA (Database Access)

- Spring Security (Authorization/Authorization)

- Spring Data Redis (Cacheing)

- Spring Data Elasticsearch (Search)

- **Database**:

- **MySQL 8.0+**: Primary Database (Display, Location, User Information)

- **Redis**: Caching, Session Management, Data

- **Authentication**: JWT + Spring Security

- **File Storage**: AWS S3 / Location (Image Creation)

- **API Documentation**: Swagger / SpringDoc OpenAPI