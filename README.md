# programmers-todo

할 일(ToDo)을 저장하고 돌려주는 REST API 서버입니다.
Java 21 · Spring Boot 4.1.1 · Spring Data JPA · PostgreSQL 17

> 과제 요구사항은 Spring Boot 3.x이지만 이 저장소는 4.1.1로 작성했습니다.

## 실행 방법

- 필요: JDK 21, Docker
- DB: `docker compose up`이 PostgreSQL 17 컨테이너를 띄우고, 처음 실행할 때 `todo-db`(앱)와 `todo_test_db`(테스트)를 만듭니다.

```bash
docker compose up -d && ./gradlew bootRun
```

서버는 `http://localhost:8889`, Swagger UI는 `http://localhost:8889/swagger-ui/index.html`입니다.

> 예전에 만든 DB 볼륨이 있으면 초기화 스크립트가 실행되지 않아 `todo_test_db`가 없습니다. 이때는 한 번만 직접 만듭니다.
> `docker exec programmers-todo-db psql -U sa -d todo-db -c "CREATE DATABASE todo_test_db;"`

## API 명세

| 기능 | 메서드 · 주소 | 요청 본문 | 성공 |
|---|---|---|---|
| 생성 | `POST /api/todos` | `{"title"}` | `201` + `Location` |
| 목록 | `GET /api/todos` | 없음 | `200` |
| 단건 조회 | `GET /api/todos/{todoId}` | 없음 | `200` |
| 수정·완료 전환 | `PUT /api/todos/{todoId}` | `{"title", "completed"}` 둘 다 필수 | `200` |
| 삭제 | `DELETE /api/todos/{todoId}` | 없음 | `204` (본문 없음) |

- `title`: 비었거나 공백뿐이면 안 되며 200자 이하
- `completed`: `"Y"` 또는 `"N"`. 생성 시 `"N"`
- 목록 파라미터: `page`(0부터), `size`(기본 20), `sort`(예: `id,desc`), `completed`(`Y`/`N` 필터)

### 응답 모양

성공과 오류 모두 같은 모양입니다(삭제 `204`만 본문 없음).

```json
{ "resultCode": "200 OK", "msg": "상세 조회 완료", "data": { "id": 1, "title": "...", "completed": "N", "createdAt": "...", "modifiedAt": "..." } }
```

`resultCode`는 HTTP 상태와 같은 값입니다. 오류일 때 `data`는 `null`이고, 입력 검증 실패일 때만 필드별 오류 목록 `[{"field", "message"}]`이 들어갑니다.

| 상황 | 상태 |
|---|---|
| 제목이 비었거나 공백뿐·200자 초과, `completed` 누락·허용되지 않은 값, 깨진 JSON, 숫자가 아닌 id, 잘못된 필터 값, 없는 필드로 정렬 | `400` |
| 없는 할 일 조회·수정·삭제, 없는 경로 | `404` |
| 예상하지 못한 서버 오류 | `500` (일반 메시지만 반환) |

## 설계 설명

- **구조:** Controller(`in`) · Service(`app`) · Repository(`out`)로 역할을 나누고, 요청·응답에는 엔티티 대신 DTO(`TodoCreateRequest`, `TodoUpdateRequest`, `TodoResponse`)를 씁니다.
- **주소:** 자원을 복수 명사 `/api/todos`, 개별 할 일을 `/api/todos/{todoId}`로 두고 동작은 HTTP 메서드로 구분했습니다.
- **완료 전환을 `PUT`으로:** 바꿀 수 있는 값이 제목과 완료 여부뿐이라 수정 API 하나로 처리합니다. `PUT`은 전체 교체이므로 두 필드를 모두 받습니다. 빠진 값을 유지할지 지울지 모호해지지 않습니다.
- **상태 코드:** 생성은 새 자원의 주소를 알리려고 `201`과 `Location`, 삭제는 돌려줄 자원이 없어 `204`를 씁니다. 요청 자체가 잘못되면 `400`, 대상이 없으면 `404`입니다.
- **오류 모양 통일:** 검증·JSON 파싱처럼 컨트롤러 전에 나는 예외도 있어서, `GlobalExceptionHandler`가 Spring의 `ResponseEntityExceptionHandler`를 상속해 모든 오류를 같은 모양으로 바꿉니다.
- **입력 검증:** 제목은 `@NotBlank`·`@Size(max = 200)`, 완료 여부는 `Y`/`N` enum으로 받아 허용되지 않은 값을 400으로 거부합니다.
- **DB로 PostgreSQL:** 널리 쓰는 RDBMS라 운영과 같은 SQL·제약 조건에서 동작을 확인할 수 있고, Docker Compose로 버전과 설정을 고정해 누구나 같은 환경을 재현할 수 있습니다.

## 실행 결과

실제 서버에 호출한 요청과 응답입니다(JSON은 줄바꿈만 정리).

### 1. 만들기

```bash
curl -i -X POST http://localhost:8889/api/todos -H 'Content-Type: application/json' -d '{"title":"장보기"}'
```

```http
HTTP/1.1 201
Location: /api/todos/1

{
  "data": { "id": 1, "title": "장보기", "createdAt": "2026-09-21T23:44:52.20233", "modifiedAt": "2026-09-21T23:44:52.20233", "completed": "N" },
  "msg": "할 일이 생성됐습니다.",
  "resultCode": "201 CREATED"
}
```

같은 방식으로 `README 작성`(id 2)을 하나 더 만들었습니다.

### 2. 목록

```bash
curl -i 'http://localhost:8889/api/todos?page=0&size=10'
```

```http
HTTP/1.1 200

{
  "data": {
    "content": [
      { "id": 1, "title": "장보기", "createdAt": "2026-09-21T23:44:52.20233", "modifiedAt": "2026-09-21T23:44:52.20233", "completed": "N" },
      { "id": 2, "title": "README 작성", "createdAt": "2026-09-21T23:44:52.260771", "modifiedAt": "2026-09-21T23:44:52.260771", "completed": "N" }
    ],
    "number": 0,
    "size": 10,
    "totalElements": 2,
    "totalPages": 1,
    ...
  },
  "msg": "목록 조회 완료",
  "resultCode": "200 OK"
}
```

`...`은 Spring Data `Page`의 나머지 페이지 정보(`first`, `last`, `pageable` 등)를 생략한 것입니다.

### 3. 완료 처리

```bash
curl -i -X PUT http://localhost:8889/api/todos/1 -H 'Content-Type: application/json' -d '{"title":"장보기","completed":"Y"}'
```

```http
HTTP/1.1 200

{
  "data": { "id": 1, "title": "장보기", "createdAt": "2026-09-21T23:44:52.20233", "modifiedAt": "2026-09-21T23:44:52.37886", "completed": "Y" },
  "msg": "할 일이 변경됐습니다.",
  "resultCode": "200 OK"
}
```

### 4. 삭제

```bash
curl -i -X DELETE http://localhost:8889/api/todos/1
```

```http
HTTP/1.1 204
```

### 400: 공백뿐인 제목

```bash
curl -i -X POST http://localhost:8889/api/todos -H 'Content-Type: application/json' -d '{"title":"   "}'
```

```http
HTTP/1.1 400

{
  "data": [ { "field": "title", "message": "제목은 필수입니다." } ],
  "msg": "입력값이 올바르지 않습니다.",
  "resultCode": "400 BAD_REQUEST"
}
```

### 404: 없는 할 일 (삭제한 id 1)

```bash
curl -i http://localhost:8889/api/todos/1
```

```http
HTTP/1.1 404

{
  "data": null,
  "msg": "존재하지 않는 할 일 입니다.",
  "resultCode": "404 NOT_FOUND"
}
```

## 테스트

DB 컨테이너를 띄운 상태에서 `./gradlew test`로 실행합니다. `@SpringBootTest`와 `RestTestClient`로 생성·완료 처리·필터·삭제의 정상 흐름과 400·404 응답을 실제 HTTP 요청으로 검증하며, 테스트는 `todo_test_db`만 사용합니다.
