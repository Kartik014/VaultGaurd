# VaultGuard

VaultGuard is a secure, extensible platform for managing database connections, user authentication, backup policies, and real-time data access. Built with Kotlin using Spring Boot, it supports role-based access control, JWT authentication, and integrates with Supabase for cloud storage.

---

## Table of Contents

- [Tech Stack](#tech-stack)
- [Features](#features)
- [Project Structure](#project-structure)
- [Environment Variables](#environment-variables)
- [Setup & Running](#setup--running)
- [Authentication & Authorization](#authentication--authorization)
- [API Endpoints](#api-endpoints)
  - [Auth](#auth)
  - [Database Connection](#database-connection)
  - [Backup](#backup)
  - [SuperAdmin](#superadmin)
  - [WebSocket](#websocket)
- [Response Format](#response-format)
- [Security](#security)

---

## Tech Stack

- **Languages:** Kotlin
- **Framework:** Spring Boot
- **Database:** PostgreSQL (via JPA/Hibernate)
- **Security:** Spring Security, JWT
- **WebSocket:** Spring WebSocket
- **Storage:** Supabase Storage, Local Storage
- **Build Tool:** Gradle
- **HTTP Client:** Spring WebClient
- **Containerization:** Docker

---

## Features

- 🔐 **JWT Authentication** - Secure token-based authentication
- 👥 **Role-Based Access Control** - Support for superadmin, admin, and user roles
- 🗄️ **Multi-Database Support** - Connect and manage multiple databases
- 📊 **Real-time Data Access** - WebSocket support for live table data
- 💾 **Automated Backups** - Configurable backup policies with cron scheduling
- ☁️ **Cloud Storage Integration** - Supabase storage support
- 🔒 **Data Encryption** - Sensitive data encryption
- 📡 **RESTful API** - Comprehensive REST API endpoints
- 🐳 **Docker Ready** - Containerization support

---

## Project Structure

```
VaultGuard/
├── controllers/        # REST and WebSocket controllers
├── services/           # Business logic and service layer
├── repository/         # JPA repositories and custom DB logic
├── factory/            # Factory classes for object and DB handler creation
├── middlewares/        # Security filters (JWT)
├── utils/              # Utility classes (JWT, enums, etc.)
├── DTO/                # Data Transfer Objects for API requests/responses
├── docker/             # Docker configuration files
├── docs/               # Documentation
└── resources/          # Application properties and static resources
```

---

## Environment Variables

Create a `.env` file in the root directory with the following variables:

| Variable                   | Description                        | Required |
|----------------------------|------------------------------------|----------|
| `DB_url`                   | JDBC URL for PostgreSQL            | Yes      |
| `DB_username`              | Database username                  | Yes      |
| `DB_password`              | Database password                  | Yes      |
| `SUPABASE_SERVICE_ROLE_KEY`| Supabase storage access key        | Yes      |
| `encryption_secret`        | Encryption key for sensitive data  | Yes      |

### Example `.env` file:

```env
DB_url=jdbc:postgresql://localhost:5432/vaultguard
DB_username=your_db_user
DB_password=your_db_password
SUPABASE_SERVICE_ROLE_KEY=your_supabase_key
encryption_secret=your_encryption_secret_key
```

---

## Setup & Running

### Prerequisites

- Java 17 or higher
- PostgreSQL database
- Gradle 7.0 or higher
- Docker (optional)

### Local Development

1. **Clone the repository**
   ```bash
   git clone <repo-url>
   cd VaultGuard
   ```

2. **Configure environment**
   ```bash
   cp .env.example .env
   # Edit .env file with your configuration
   ```

3. **Build the project**
   ```bash
   ./gradlew build
   ```

4. **Run the application**
   ```bash
   ./gradlew bootRun
   ```

The application will start on `http://localhost:8080`

### Docker Deployment

1. **Build Docker image**
   ```bash
   docker build -t vaultguard .
   ```

2. **Run with Docker Compose**
   ```bash
   docker-compose up -d
   ```

---

## Authentication & Authorization

VaultGuard uses JWT-based authentication for all protected endpoints.

### Roles

- **superadmin**: Full access (user management, database management, backup policies)
- **admin**: Backup management and limited database access
- **user**: Basic access (future extension)

### Authentication Flow

1. Register or login to obtain a JWT token
2. Include the token in the `Authorization` header for all protected requests:
   ```
   Authorization: Bearer <your_jwt_token>
   ```

---

## API Endpoints

### Auth

#### Register User
```http
POST /auth/signup
Content-Type: application/json

{
  "username": "john",
  "email": "john@example.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "status": "success",
  "message": "User registered successfully",
  "data": {
    "id": "user_123",
    "username": "john",
    "email": "john@example.com",
    "role": "user"
  }
}
```

#### User Login
```http
POST /auth/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "status": "success",
  "message": "Login Successful",
  "data": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

---

### Database Connection

#### Add Database Connection
```http
POST /dbConn/addDb
Authorization: Bearer <token>
Content-Type: application/json

{
  "dbtype": "postgres",
  "host": "db.example.com",
  "port": 5432,
  "dbname": "mydb",
  "username": "dbuser",
  "password": "dbpass",
  "ssl": true
}
```

**Response:**
```json
{
  "status": "success",
  "message": "Database connection added successfully",
  "data": {
    "id": "db_123",
    "dbtype": "postgres",
    "host": "db.example.com",
    "dbname": "mydb",
    "createdAt": "2024-06-01T12:00:00Z"
  }
}
```

#### List All Databases
```http
GET /dbConn/allDb
Authorization: Bearer <token>
```

#### Connect to Database
```http
GET /dbConn/connect/{dbid}
Authorization: Bearer <token>
```

**Response:**
```json
{
  "status": "success",
  "message": "Database connected successfully",
  "data": ["users", "orders", "products", "categories"]
}
```

---

### Backup

#### Create Backup Policy
```http
POST /backup/v1/create-backup-policy
Authorization: Bearer <token>
Content-Type: application/json

{
  "dbid": "db_123",
  "policyname": "Daily Backup",
  "selectedtables": ["users", "orders"],
  "frequencycron": "0 0 * * *",
  "storagetype": "supabase",
  "isactive": true
}
```

#### Get Backup Policies
```http
GET /backup/v1/{dbid}/get-policy
Authorization: Bearer <token>
```

#### Create Backup
```http
POST /backup/v1/{dbid}/create-backup
Authorization: Bearer <token>
Content-Type: application/json

{
  "policyid": "backup_1_db_123"
}
```

#### List Backup Files
```http
GET /backup/v1/{dbid}/list-backup-files
Authorization: Bearer <token>
```

**Response:**
```json
{
  "status": "success",
  "message": "Backup files listed successfully",
  "data": [
    {
      "name": "backup_users_20240601.sql",
      "id": "file_456",
      "created_at": "2024-06-01T12:00:00Z",
      "size": "1.2 MB",
      "mimetype": "text/plain"
    }
  ]
}
```

---

### SuperAdmin

#### Add User
```http
POST /superadmin/addUser
Authorization: Bearer <token>
Content-Type: application/json

{
  "username": "alice",
  "email": "alice@example.com",
  "password": "alicepass",
  "role": "admin"
}
```

#### List All Users
```http
GET /superadmin/allUsers
Authorization: Bearer <token>
```

#### Update User Role
```http
PATCH /superadmin/updateRole
Authorization: Bearer <token>
Content-Type: application/json

{
  "userId": "user_123",
  "role": "admin"
}
```

#### Remove User
```http
DELETE /superadmin/removeUser
Authorization: Bearer <token>
Content-Type: application/json

{
  "userId": "user_123"
}
```

---

### WebSocket

Connect to the WebSocket endpoint for real-time database table data:

```
ws://localhost:8080/ws
```

#### Authentication
JWT authentication is enforced during the WebSocket handshake using a custom interceptor. Include the JWT token in the `Authorization` header during the handshake.

#### Usage Example
```javascript
// Connect to WebSocket
const ws = new WebSocket('ws://localhost:8080/ws', [], {
  headers: {
    'Authorization': 'Bearer your_jwt_token'
  }
});

// Send request for table data
ws.send(JSON.stringify({
  "dbid": "db_123",
  "tablename": "users"
}));

// Receive table data
ws.onmessage = function(event) {
  const tableData = JSON.parse(event.data);
  console.log('Table data:', tableData);
};
```

---

## Response Format

All API endpoints return a standardized response format:

```json
{
  "status": "success" | "error",
  "message": "Description of the result",
  "data": null | object | array
}
```

### Error Response Example
```json
{
  "status": "error",
  "message": "Invalid credentials provided",
  "data": null
}
```

---

## Security

- **JWT Authentication**: All protected endpoints require valid JWT tokens
- **Role-Based Access Control**: Different access levels for different user roles
- **Data Encryption**: Sensitive data is encrypted using configurable encryption keys
- **SQL Injection Protection**: Parameterized queries and JPA prevent SQL injection
- **CORS Configuration**: Configurable CORS settings for web client integration
