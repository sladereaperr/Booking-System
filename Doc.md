# Backend API Documentation & Setup

## ⚙️ Configuration

- **API URL:** `http://localhost:8080`
- **Database:** Port `5433` (External access)
- **Redis:** Port `6379`

### Option 2: Local Dev Setup

1. Ensure **Postgres** (`5432`) and **Redis** (`6379`) are running locally.
2. Update `application.properties` to point to localhost.
3. Run command:

```bash
mvn spring-boot:run

```

---

## 🔌 API Documentation (Postman Guide)

### 1. Authentication

| Method   | Endpoint             | Description          | Body                                                                   |
| -------- | -------------------- | -------------------- | ---------------------------------------------------------------------- |
| **POST** | `/api/auth/register` | Register new user    | `{ "name": "...", "email": "...", "password": "...", "phone": "..." }` |
| **POST** | `/api/auth/login`    | Login & Get Token    | `{ "email": "...", "password": "..." }`                                |
| **POST** | `/api/auth/refresh`  | Refresh Access Token | `{ "refreshToken": "..." }`                                            |

### 2. Events & Browsing (Public)

| Method  | Endpoint                | Description               | Headers |
| ------- | ----------------------- | ------------------------- | ------- |
| **GET** | `/api/events`           | List all events (Cached)  | -       |
| **GET** | `/api/events?city=NYC`  | Search/Filter Events      | -       |
| **GET** | `/api/shows/event/{id}` | Get Shows for an Event    | -       |
| **GET** | `/api/shows/{id}`       | Get Real-time Seating Map | -       |

### 3. Booking (User Only)

**Header Required:** `Authorization: Bearer <ACCESS_TOKEN>`

| Method   | Endpoint        | Description    | Body                                              |
| -------- | --------------- | -------------- | ------------------------------------------------- |
| **POST** | `/api/bookings` | Book Tickets   | `{ "userId": 1, "showId": 1, "seatIds": [1, 2] }` |
| **GET**  | `/api/users/me` | Get My Profile | -                                                 |

### 4. Administration (Admin Only)

**Header Required:** `Authorization: Bearer <ADMIN_TOKEN>`

| Method     | Endpoint                 | Description                       |
| ---------- | ------------------------ | --------------------------------- |
| **POST**   | `/api/admin/events`      | Create Event                      |
| **PUT**    | `/api/admin/events/{id}` | Update Event Metadata             |
| **POST**   | `/api/admin/shows`       | Schedule a Show                   |
| **DELETE** | `/api/admin/shows/{id}`  | Delete Show (Protected if booked) |

---

## 🧪 Testing Scenarios (How to Verify)

### ✅ Scenario 1: Concurrency (Double Booking)

1. Log in as **User A**.
2. Log in as **User B**.
3. User A sends a request to book **Seat #1** for **Show #1**.
4. User B sends a request to book **Seat #1** for **Show #1** (simultaneously).

- **Result:** Only one succeeds (`200 OK`). The other fails with `400 Bad Request: Seat already booked`.

### ✅ Scenario 2: Redis Caching

1. Call `GET /api/events`. Check Docker logs.

- **Log:** `⚠️ Cache Miss: Fetching from Database...`

2. Call `GET /api/events` again.

- **Log:** _(Empty)_ -> Data served instantly from Redis.

### ✅ Scenario 3: Data Integrity

1. Admin tries to delete a Show that has bookings.

- **Result:** `400 Bad Request: Cannot delete Show: Tickets have already been booked`.

---

## 📂 Project Structure

```text
src/main/java/com/booking/backend
├── config/          # Security & Redis Config
├── controller/      # REST API Layer
├── dto/             # Data Transfer Objects (Validation)
├── model/           # JPA Entities
├── repository/      # Database Access (Interfaces)
├── service/         # Business Logic (Transactions)
└── util/            # JWT Utilities

```
