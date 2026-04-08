# Profile API README (for FE)

Base URL: `http://localhost:8080`  
Prefix: `/api/profile`

All responses use `ApiResponse<T>`:

- `success`: boolean
- `message`: string
- `data`: payload
- `timestamp`: server time

## Auth and Roles

- Bearer token required for most profile APIs.
- `ADMIN`/`HR` only:
  - `GET /api/profile/users`
  - `GET /api/profile/users/{userId}`

---

## 1) Get my profile

`GET /api/profile/me`  
Auth: required

`data` (`ProfileResponse`) fields:

- `id`, `email`, `fullName`, `phone`, `avatarUrl`, `address`, `headline`, `bio`
- `role`, `emailVerified`, `enabled`
- `createdAt`, `updatedAt`

---

## 2) Update my profile

`PUT /api/profile/me`  
Auth: required

Body:

```json
{
  "fullName": "Nguyen Van A",
  "phone": "0901234567",
  "address": "Ho Chi Minh City",
  "headline": "Java Backend Developer",
  "bio": "4+ years in Spring Boot and distributed systems."
}
```

Returns updated `ProfileResponse`.

---

## 3) Update avatar

`PATCH /api/profile/me/avatar`  
Auth: required

Body:

```json
{
  "avatarUrl": "https://cdn.example.com/avatar/user-1.png"
}
```

Returns updated `ProfileResponse`.

---

## 4) Admin/HR - get user profile by ID

`GET /api/profile/users/{userId}`  
Auth: required (`ROLE_ADMIN` or `ROLE_HR`)

Returns `ProfileResponse`.

---

## 5) Admin/HR - search/list users (paged)

`GET /api/profile/users?role=ROLE_CUSTOMER&keyword=nguyen&page=0&size=20`  
Auth: required (`ROLE_ADMIN` or `ROLE_HR`)

Query params:

- `role` (optional): `ROLE_ADMIN` | `ROLE_HR` | `ROLE_CUSTOMER`
- `keyword` (optional): search by email or fullName
- `page` (optional, default `0`)
- `size` (optional, default `20`, max `100`)

Returns Spring `Page<ProfileResponse>` in `data`.

---

## FE integration notes

- Always include:
  - `Authorization: Bearer <accessToken>`
  - `Content-Type: application/json`
- Handle `401` by calling refresh flow (`/api/auth/refresh`) then retry request.
