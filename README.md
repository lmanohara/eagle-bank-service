# Eagle Bank Service

A Spring Boot REST API for a fictional bank. Users can create an account, manage personal bank accounts, and record deposit/withdrawal transactions. Authentication is JWT-based: a Basic-auth token-exchange endpoint issues short-lived JWTs, and new users complete sign-up via a one-time password setup token.

## Tech stack

- Java 23, Spring Boot 4
- Spring Data JPA + H2 (in-memory)
- Spring Security OAuth2 Resource Server (JWT)
- Asymmetric JWT signing (RS256, key pair under `src/main/resources/keys/`)
- OpenAPI 3.1 spec at `src/main/resources/open-api-spec/openapi.yaml`
- Gradle, Spotless (Google Java Format)

## Run

```bash
./gradlew bootRun
```

The service starts on `http://localhost:8080`. The H2 console is available at `/h2-console`.

## Test

```bash
./gradlew test
```

## API overview

| Area | Endpoint |
| --- | --- |
| Auth | `POST /v1/auth/token`, `POST /v1/auth/set-password` |
| Users | `POST /v1/users`, `GET /v1/users/{userId}`, `PATCH /v1/users/{userId}` |
| Accounts | `POST /v1/accounts`, `GET /v1/accounts`, `GET /v1/accounts/{accountNumber}`, `PATCH /v1/accounts/{accountNumber}` |
| Transactions | `POST /v1/accounts/{accountNumber}/transactions`, `GET /v1/accounts/{accountNumber}/transactions`, `GET /v1/accounts/{accountNumber}/transactions/{transactionId}` |

All endpoints except user creation and the auth endpoints require a `Bearer` JWT obtained from `POST /v1/auth/token`. Users may only access their own resources.

See `src/main/resources/open-api-spec/openapi.yaml` for the full contract.

## Auth flow

1. `POST /v1/users` — create a user. The response includes a one-time `passwordSetupToken`.
2. `POST /v1/auth/set-password` — submit the token together with a chosen username and password.
3. `POST /v1/auth/token` — exchange `Authorization: Basic <base64(username:password)>` for a JWT.
4. Call protected endpoints with `Authorization: Bearer <token>`.

## Configuration

Defaults live in `src/main/resources/application.yml`. Notable settings:

- `jwt.private-key` / `jwt.public-key` — classpath locations of the signing key pair
- `jwt.expiration` — token lifetime in milliseconds (default 1 hour)
