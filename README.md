<h1>
  <img src="https://github.com/user-attachments/assets/d4706a8f-49b0-4c96-82c3-4a82616957cb" alt="arttrip logo" width="36">
  &nbsp;아트트립 - ArtTrip
</h1>

**아트트립과 함께 국내·해외 전시·공연을 한 번에 모아보고, 일상과 여행 속 문화 경험을 더 가볍게 시작해 보세요.**  
> 여행 중 “지금 이 도시에서 볼 만한 전시”부터, 관심사에 맞는 전시까지 한 흐름으로 이어집니다.

<img width="1200" alt="10" src="https://github.com/user-attachments/assets/4ed844d1-7682-46e3-b3ab-91645fed0261" />
<img width="1920" height="1200" alt="arttrip_page2 (1)" src="https://github.com/user-attachments/assets/39438569-c276-49eb-aa8e-e8db6f94271b" />

## Key Features
* **일정에 맞는 전시 찾기**: 원하는 날짜/기간을 선택하면, 그때 볼 수 있는 전시만 모아 보여줘요.
* **취향에 맞는 추천**: 관심 장르/스타일을 바탕으로 당신에게 어울리는 전시를 추천해요.
* **전시 상세 정보 한눈에**: 장소·기간·가격·소개는 물론, **진행 중/오픈 예정/마감 임박** 상태까지 바로 확인할 수 있어요.
* **공식 사이트로 바로 이동**: 더 자세한 정보나 예매가 필요하면, 공식 예매처/전시 사이트로 즉시 이동할 수 있어요.
* **보관함 & 최근 본 전시**: 저장해 둔 전시와 최근에 본 전시를 다시 쉽게 확인할 수 있어요.
<br><br><br>


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


