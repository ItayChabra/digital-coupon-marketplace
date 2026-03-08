# Nexus – Digital Coupon Marketplace

A full-stack digital marketplace for coupon-based products, built as a backend exercise.  
Supports two selling channels (direct customers and external resellers), admin product management, strict server-side pricing, and atomic purchases.

---

## Tech Stack

| Layer     | Technology                        |
|-----------|-----------------------------------|
| Backend   | Java 25 · Spring Boot 4           |
| Database  | PostgreSQL 16                     |
| Frontend  | React 18 · Vite · Nginx           |
| Packaging | Docker · Docker Compose           |

---

## Quick Start

### Prerequisites

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) (or Docker + Docker Compose)
- Nothing else — the JDK, Node, and Postgres all run inside containers

### Run the project

```bash
git clone https://github.com/ItayChabra/digital-coupon-marketplace.git
cd digital-coupon-marketplace
docker-compose up --build
```

| Service  | URL                   |
|----------|-----------------------|
| Frontend | http://localhost:3000 |
| API      | http://localhost:8080 |

To stop:
```bash
docker-compose down
```

To stop and wipe the database:
```bash
docker-compose down -v
```

---

## Configuration & Secrets

All sensitive values are passed via environment variables. Default development values are baked into `docker-compose.yml` **for local use only** — never commit real tokens or passwords to git.

| Variable         | Default (dev)                  | Description                   |
|------------------|--------------------------------|-------------------------------|
| `ADMIN_TOKEN`    | `admin-dev-token-change-me`    | Bearer token for admin API    |
| `RESELLER_TOKEN` | `reseller-dev-token-change-me` | Bearer token for reseller API |
| `DB_PASSWORD`    | `password`                     | PostgreSQL password           |

To override for production, set these as real environment variables before running `docker-compose up`, or use a `.env` file (already in `.gitignore`).

---

## Architecture

```
┌──────────────────────┐      /api/*       ┌──────────────────────┐
│   React Frontend     │ ────────────────► │   Spring Boot API    │
│   (Nginx :80)        │   reverse proxy   │   (:8080)            │
└──────────────────────┘                   └──────────┬───────────┘
                                                      │
                                               JPA / Hibernate
                                                      │
                                           ┌──────────▼───────────┐
                                           │   PostgreSQL 16       │
                                           └──────────────────────┘
```

### Domain Model

```
Product (abstract, JOINED inheritance)
└── Coupon
    ├── cost_price          — set by admin
    ├── margin_percentage   — set by admin
    ├── minimum_sell_price  — DERIVED server-side, never accepted from clients
    ├── is_sold             — default false, set atomically on purchase
    ├── value_type          — STRING | IMAGE
    └── coupon_value        — only revealed after successful purchase
```

**Pricing formula (enforced server-side via `@PrePersist` / `@PreUpdate`):**
```
minimum_sell_price = cost_price × (1 + margin_percentage / 100)
```

### Security

Bearer token authentication via a custom `OncePerRequestFilter`:
- `Authorization: Bearer <admin-token>` → `ROLE_ADMIN` → `/api/admin/**`
- `Authorization: Bearer <reseller-token>` → `ROLE_RESELLER` → `/api/v1/**`
- No token required → `/api/customer/**` (public)

---

## API Reference

### Customer API (public)

| Method | Endpoint                               | Description              |
|--------|----------------------------------------|--------------------------|
| GET    | `/api/customer/products`               | List all unsold products |
| GET    | `/api/customer/products/{id}`          | Get single product       |
| POST   | `/api/customer/products/{id}/purchase` | Purchase at fixed price  |

Purchase response:
```json
{
  "product_id": "uuid",
  "final_price": 100.00,
  "value_type": "STRING",
  "value": "ABCD-1234"
}
```

---

### Reseller API (Bearer token required)

All requests must include:
```
Authorization: Bearer <reseller-token>
```

| Method | Endpoint                              | Description              |
|--------|---------------------------------------|--------------------------|
| GET    | `/api/v1/products`                    | List all unsold products |
| GET    | `/api/v1/products/{id}`               | Get single product       |
| POST   | `/api/v1/products/{id}/purchase`      | Purchase at custom price |

Purchase request:
```json
{ "reseller_price": 120.00 }
```

Rules:
- `reseller_price` must be `≥ minimum_sell_price` — otherwise `400 RESELLER_PRICE_TOO_LOW`
- Purchase is atomic (pessimistic lock prevents double-sell)

---

### Admin API (Bearer token required)

All requests must include:
```
Authorization: Bearer <admin-token>
```

| Method | Endpoint                   | Description            |
|--------|----------------------------|------------------------|
| GET    | `/api/admin/products`      | List all coupons       |
| GET    | `/api/admin/products/{id}` | Get coupon (full view) |
| POST   | `/api/admin/products`      | Create coupon          |
| PUT    | `/api/admin/products/{id}` | Update coupon          |
| DELETE | `/api/admin/products/{id}` | Delete coupon          |

Create request body:
```json
{
  "name": "Amazon $100 Gift Card",
  "description": "Redeemable on Amazon.com",
  "image_url": "https://example.com/amazon.png",
  "cost_price": 80.00,
  "margin_percentage": 25,
  "value_type": "STRING",
  "coupon_value": "ABCD-1234-EFGH"
}
```

> `minimum_sell_price` is always calculated server-side and is never accepted from input.

---

### Error Format

All errors follow a consistent structure:

```json
{
  "error_code": "ERROR_NAME",
  "message": "Human readable description"
}
```

| `error_code`             | HTTP |
|--------------------------|------|
| `PRODUCT_NOT_FOUND`      | 404  |
| `PRODUCT_ALREADY_SOLD`   | 409  |
| `RESELLER_PRICE_TOO_LOW` | 400  |
| `UNAUTHORIZED`           | 401  |
| `VALIDATION_ERROR`       | 400  |

---

## Frontend

The UI is served at `http://localhost:3000` and has three tabs:

- **Customer** — browse and purchase available coupons at the listed price
- **Reseller** — authenticate with a reseller token, enter a custom price ≥ minimum, and purchase
- **Admin** — authenticate with an admin token to create, view, and delete coupons

All API calls are proxied through Nginx (`/api/*` → Spring container), so there are no CORS issues.

---

## Project Structure

```
marketplace/
├── src/main/java/com/nexus/marketplace/
│   ├── config/           # Spring Security configuration
│   ├── controller/       # REST controllers (Admin, Reseller, Customer)
│   ├── dto/              # Request and response DTOs
│   ├── entity/           # JPA entities (Product, Coupon)
│   ├── enums/            # ProductType, ValueType
│   ├── exception/        # Custom exceptions + global handler
│   ├── repository/       # Spring Data JPA repositories
│   ├── security/         # BearerTokenFilter
│   └── service/          # Business logic (CouponService)
├── src/main/resources/
│   └── application.properties
├── frontend/
│   ├── src/
│   │   ├── components/   # AdminView, CustomerView, ResellerView, ProductCard, Toast
│   │   ├── hooks/        # useToast
│   │   ├── api.js        # All fetch calls (snake_case contract)
│   │   └── App.jsx
│   ├── Dockerfile
│   └── nginx.conf
├── docker-compose.yml
├── Dockerfile
└── build.gradle
```

---

## Design Notes

- **Extensibility**: `Product` uses `JOINED` table inheritance with a discriminator column. Adding a new product type (e.g. `VOUCHER`) only requires a new entity class with `@DiscriminatorValue("VOUCHER")` — no schema changes to the `products` table.
- **Atomicity**: Purchases use `@Lock(PESSIMISTIC_WRITE)` on the coupon row. The sold check happens in Java after acquiring the lock, which correctly returns 409 (already sold) vs 404 (not found) rather than conflating the two.
- **Pricing integrity**: `minimum_sell_price` is recalculated via `@PrePersist` / `@PreUpdate` and is never accepted as client input — not even from the admin API.
- **Coupon secrecy**: `couponValue` is never included in `ProductResponse` (used for listings). It only appears in `PurchaseResponse` after a completed transaction.

---

## REST API Examples

All examples use the default dev tokens. Replace with your actual tokens in production.

### Admin — Create a coupon
```bash
curl -X POST http://localhost:8080/api/admin/products \
  -H "Authorization: Bearer admin-dev-token-change-me" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Amazon $100 Gift Card",
    "description": "Redeemable on Amazon.com",
    "image_url": "https://upload.wikimedia.org/wikipedia/commons/a/a9/Amazon_logo.svg",
    "cost_price": 80.00,
    "margin_percentage": 25,
    "value_type": "STRING",
    "coupon_value": "AMZN-ABCD-1234-EFGH"
  }'
```

### Admin — List all coupons (includes sold, cost, margin, coupon value)
```bash
curl http://localhost:8080/api/admin/products \
  -H "Authorization: Bearer admin-dev-token-change-me"
```

### Admin — Get a single coupon
```bash
curl http://localhost:8080/api/admin/products/{id} \
  -H "Authorization: Bearer admin-dev-token-change-me"
```

### Admin — Update a coupon
```bash
curl -X PUT http://localhost:8080/api/admin/products/{id} \
  -H "Authorization: Bearer admin-dev-token-change-me" \
  -H "Content-Type: application/json" \
  -d '{
    "margin_percentage": 30
  }'
```

### Admin — Delete a coupon
```bash
curl -X DELETE http://localhost:8080/api/admin/products/{id} \
  -H "Authorization: Bearer admin-dev-token-change-me"
```

---

### Reseller — List available products (no cost/margin exposed)
```bash
curl http://localhost:8080/api/v1/products \
  -H "Authorization: Bearer reseller-dev-token-change-me"
```

### Reseller — Get a single product
```bash
curl http://localhost:8080/api/v1/products/{id} \
  -H "Authorization: Bearer reseller-dev-token-change-me"
```

### Reseller — Purchase a product
```bash
curl -X POST http://localhost:8080/api/v1/products/{id}/purchase \
  -H "Authorization: Bearer reseller-dev-token-change-me" \
  -H "Content-Type: application/json" \
  -d '{ "reseller_price": 120.00 }'
```

**Success (200):**
```json
{
  "product_id": "3f5a1b2c-...",
  "final_price": 120.00,
  "value_type": "STRING",
  "value": "AMZN-ABCD-1234-EFGH"
}
```

**Price too low (400):**
```json
{
  "error_code": "RESELLER_PRICE_TOO_LOW",
  "message": "reseller_price must be >= 100.00"
}
```

**Already sold (409):**
```json
{
  "error_code": "PRODUCT_ALREADY_SOLD",
  "message": "Product is already sold: 3f5a1b2c-..."
}
```

---

### Customer — List available products
```bash
curl http://localhost:8080/api/customer/products
```

### Customer — Get a single product
```bash
curl http://localhost:8080/api/customer/products/{id}
```

### Customer — Purchase a product (price is fixed server-side)
```bash
curl -X POST http://localhost:8080/api/customer/products/{id}/purchase
```

---

### Auth errors

Missing token → `401`:
```bash
curl http://localhost:8080/api/v1/products
# {"error_code":"UNAUTHORIZED","message":"Missing or invalid token"}
```
