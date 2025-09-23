# Artrip

> Java Monologue 버전

## Tech Stack
- Java, SpringBoot
- Docker, MySQL, MongoDB, Elasticsearch

## Getting Started

### 1. `application.yml` 파일 설정
**resources 하위에 application.yml을 설정 후 실행**

```bash
# 빌드 시 로그 기록 확인 시 하위 명령어
docker-compose up --build

# -d 옵션을 추가할 시 백그라운드에서 동작
docker-compose up -d --build
```

## Git 브랜치 전략 (Git Branch Strategy)

1. 기본 원칙
>모든 작업은 이슈 기반으로 브랜치를 생성하여 진행합니다.<br>
> main 브랜치에 직접적인 commit이나 push는 금지합니다.<br>
> 모든 merge는 Pull Request (PR)를 통해서만 진행합니다.<br>

2. 브랜치 명명 규칙
> 브랜치 이름은 [이슈번호] 형식으로 생성합니다.<Br>
> 예시: ART-001

3. 작업 흐름
> main 브랜치에서 git pull을 통해 최신 상태를 유지합니다.<br>
> main 브랜치로부터 아래 명령어로 새로운 작업 브랜치를 생성합니다.<br>
> git checkout -b ART-001<br><br>
> 기능 개발을 완료한 후 commit과 push를 진행합니다.<br>
> commit 내용은 {feat: {작업 내용}} 양식에 맞춰 push 진행<br>
> GitHub에서 develop 브랜치로 향하는 Pull Request를 생성하고 코드 리뷰를 요청합니다.

4. 의존성이 있는 브랜치 작업
>만약 새로운 이슈(ART-002)가 기존 브랜치(ART-001)의 작업물을 필요로 한다면,<br> main이 아닌 ART-001 브랜치에서 ART-002 브랜치를 생성합니다.

예시:

### 먼저 의존성이 있는 브랜치로 이동하여 최신 상태로 업데이트합니다.
```bash
git checkout ART-001
git pull
```

### 해당 브랜치에서 새로운 작업 브랜치를 생성합니다.
```
git checkout -b ART-002
```


