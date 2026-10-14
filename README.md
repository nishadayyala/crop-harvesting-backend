# Crop Harvesting System

A Java Spring Boot + React full-stack web application for managing crops and harvesting schedules in a farm environment.

## Overview
The Crop Harvesting System helps farmers and agricultural staff track crops, monitor field activities, and manage harvesting tasks. The app includes crop CRUD operations, harvesting records, and a dashboard that shows real counts from the database.

## Technologies Used
- Java 21
- Spring Boot 3.3.4
- Maven
- MySQL
- Spring Data JPA and Hibernate
- Spring Validation
- Spring Security with BCrypt passwords, session-cookie login, CSRF protection, and farmer/admin roles
- React.js with Vite
- JavaScript
- CSS3
- Postman

## Project Structure
- backend/ or current Spring Boot project root
- frontend/
- database/
- postman/
- README.md

## Prerequisites
- Java 21 JDK
- Maven
- MySQL Server
- Node.js and npm
- Vite
- VS Code
- Postman

## MySQL Configuration
The local profile connects to `crop_harvesting_db` on `localhost:3306` by default. Override `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` in the backend process environment if your MySQL setup differs. Do not place real credentials in tracked files. The production profile requires all three values and uses the TLS settings supplied in `DB_URL`.

## Java 21 Setup
Download and install Java 21, then confirm it works:
```bash
java -version
mvn -version
```

## Running the Backend
From the project root on Windows:
```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21.0.12.1"
$env:DB_URL = "jdbc:mysql://localhost:3306/crop_harvesting_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
$env:DB_USERNAME = "root"
$securePassword = Read-Host "MySQL password" -AsSecureString
$env:DB_PASSWORD = (New-Object System.Net.NetworkCredential("", $securePassword)).Password
.\mvnw.cmd spring-boot:run
```

Enter the password for the MySQL account in the hidden prompt; do not use an empty value unless that account has explicitly been configured with no password. These commands set `JAVA_HOME` and the database values only in the current PowerShell session. The Java 21 `JAVA_HOME` is also configured for your Windows user.

The backend runs on `http://localhost:8080`.
Local development uses Hibernate `update` to preserve the existing local schema. The production Flyway migration is not enabled locally. Run the automated tests with `.\mvnw.cmd clean test`; these use an isolated H2 database, not MySQL.

If an older backend is already running on port 8080, stop it in its original terminal with `Ctrl+C` before starting this source version. The old process was observed to return 200 for anonymous crop/harvest list requests and 404 for the auth endpoint; that is not the expected secure behavior. After starting this version, `GET /api/auth/csrf` should return 200 and anonymous `GET /api/crops` should return 401.

To create the first administrator account, set `BOOTSTRAP_ADMIN_EMAIL` and `BOOTSTRAP_ADMIN_PASSWORD` in the backend process environment before starting it. Keep those values private and configured on every startup. If both are omitted, public registration can still create farmer accounts, but no admin account is bootstrapped. The bootstrap password must be at least 12 characters. Public registration cannot choose an administrator role.

The browser login uses an HTTP-only session cookie and a CSRF token. Local development defaults to an HTTP cookie with `SameSite=Lax`; the production profile always enables `Secure` and defaults to `SameSite=Lax`. Set `CORS_ALLOWED_ORIGINS` to the exact comma-separated frontend origins, including scheme and host. Do not use a wildcard with credentialed cookies. For production, use frontend and API HTTPS subdomains under the same registered domain.

## Optional Sample Data
Sample data can be inserted manually using SQL only after the tables are created. Avoid inserting it automatically on every startup by not placing seed SQL in application code. The recommended approach is to keep sample inserts in `database/schema.sql` or run them once in MySQL Workbench.

## Frontend Setup

The maintained frontend is in the separate `crop-harvesting-frontend` repository. The old `frontend/` copy inside this Maven folder is ignored and is not used for deployment. In the maintained frontend folder:

```bash
npm install
npm run dev
```
Then open the Vite URL (typically `http://localhost:5173`). The Vite proxy forwards `/api` to this backend on port 8080.

## Production deployment preparation

### Render backend

The root `Dockerfile` builds with Java 21 and starts the Spring Boot JAR. In Render, create a **Web Service** from the backend repository, choose the Docker runtime, and use `/actuator/health` as its health-check path. Render supplies the listening `PORT`; Spring Boot reads it through `server.port=${PORT:8080}`.

Set the following values in the Render service's Environment settings (never commit the real values):

| Variable | Value |
| --- | --- |
| `SPRING_PROFILES_ACTIVE` | `production` |
| `DB_URL` | The MySQL provider's JDBC URL for a **new, empty** database; enable TLS as required by the provider |
| `DB_USERNAME` | The database user supplied by the provider |
| `DB_PASSWORD` | The database password supplied by the provider |
| `CORS_ALLOWED_ORIGINS` | The exact HTTPS Vercel frontend origin, with no trailing slash |
| `BOOTSTRAP_ADMIN_EMAIL` | The private email for the first administrator |
| `BOOTSTRAP_ADMIN_PASSWORD` | A private password of at least 12 characters |
| `SESSION_COOKIE_SAME_SITE` | `Lax` (the default) |

The production profile uses the MySQL JDBC URL and TLS options supplied by the provider, applies versioned Flyway migrations, validates the resulting schema instead of changing it with Hibernate, and requires an explicit CORS origin. The first migration creates the schema only in an empty database. Do not point it at a database containing existing tables or valuable data; arrange a verified backup and a reviewed migration/baseline plan first.

Render does not provide a native managed MySQL database as part of its standard database offering. Select an external managed MySQL provider and check its pricing, backup/restore, TLS and Render outbound-connectivity requirements before creating a database. The application accepts standard provider-supplied JDBC credentials through the variables above; it does not assume a Render-specific database URL.

### Vercel frontend and session cookies

In Vercel, import the existing frontend repository, use its repository root, set the build command to `npm run build`, and set the output directory to `dist`. Set `VITE_API_BASE_URL` to `/api` (or leave it unset). Add the server-side Vercel environment variable `BACKEND_API_URL` with the Render service's HTTPS origin only, without credentials or an `/api` path.

The Vercel serverless API proxy forwards requests to Render and relays cookies. The browser therefore uses a first-party `/api` URL while the backend still permits only the exact deployed Vercel origin. The production profile forces the session cookie `Secure` and defaults `SameSite=Lax`; a shared custom domain is not required.

The owner must connect GitHub to Render and Vercel, provision the external database, configure domain DNS and enter the private environment values in provider dashboards. Never send database or admin passwords through chat.

### Health endpoint

`GET /actuator/health` is public for platform health checks and exposes no component details. With the production profile it includes Spring Boot's database health indicator, so a healthy response also confirms the database connection.

## Postman Testing
Import `postman/Crop-Harvesting-System.postman_collection.json`.
Create a local Postman environment with `baseUrl` (default `http://localhost:8080`), `userName`, `userEmail`, and `userPassword`; mark `userPassword` as secret and do not commit the environment. Use Postman's cookie jar for the session and CSRF cookies. Run `Get CSRF token`, then choose either `Register farmer` or `Login`. The CSRF endpoint's test script stores the token for the collection. Authentication rotates that token, so fetch a fresh CSRF token before **each** POST, PUT, or DELETE request. The collection saves newly created crop and harvest IDs.

Run crop and harvest CRUD while authenticated. Delete the harvesting record before its crop because crop deletion is rejected while related records exist. To avoid accumulating sample rows, finish the tests by deleting both records and sign out.

## API Functional Notes
- Crop and harvesting records are stored in MySQL using JPA.
- `POST /api/auth/register` creates a farmer account; `POST /api/auth/login` starts a session, `GET /api/auth/me` returns the current safe user profile, and `POST /api/auth/logout` ends the session. `GET /api/auth/csrf` supplies the CSRF token required for state-changing requests.
- `GET /actuator/health` is the deployment health-check endpoint.
- Every crop, harvest, and dashboard endpoint requires authentication. Farmers can access only crops they created and their related harvests; administrators can access all records. Existing crops have no recorded owner and are visible only to administrators; review and run an owner-assignment migration before farmers need access to legacy rows.
- Passwords are BCrypt-hashed. Registration and login responses never include the password or password hash.
- Crop deletion is blocked when a crop still has harvesting records.
- Validation errors use HTTP 400, missing records use HTTP 404, and conflicting deletes use HTTP 409.
- CORS defaults to local Vite origins and can be configured with `CORS_ALLOWED_ORIGINS`.
- Hibernate `ddl-auto=update` is local-development-only; the production profile uses Flyway versioned migrations and `ddl-auto=validate`.

## Project Goal
This project is intended as a beginner-friendly crop harvesting management system for demonstrating a working Java full-stack application in a college project setting.
