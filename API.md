# Around Van API

REST API for the Around Van / Vancouver Hub backend.

**Base URL:** `http://localhost:8080` (or deployed host)  
**Content-Type:** `application/json`  
**Auth:** JWT Bearer token (`Authorization: Bearer <token>`)

---

## Table of contents

1. [Overview](#overview)
2. [Authentication](#authentication)
3. [Errors](#errors)
4. [Enums](#enums)
5. [Auth](#auth)
6. [Users](#users)
7. [Gas](#gas)
8. [Events](#events)
9. [Wildfires](#wildfires)
10. [AQHI](#aqhi)
11. [Weather](#weather)
12. [Service requests (311)](#service-requests-311)
13. [Health](#health)
14. [Endpoint summary](#endpoint-summary)

---

## Overview

### Auth model

- Stateless JWT (OAuth2 resource server), RS256, issuer `aroundvan`, subject = username, **1 hour** expiry.
- RSA keypair is generated **in memory at startup** — tokens become invalid after a server restart.
- No roles; access is **public** or **authenticated**.
- Send the token on protected routes:

```http
Authorization: Bearer eyJ...
```

### Home location

Many “near” endpoints require the user to have set a home location via `PUT /api/users/me/location`. Without it they return `400` with a message like `"Set your home location before requesting…"`.

### CORS & email links

Allowed origins come from `APP_CORS_ALLOWED_ORIGINS` (comma-separated, no trailing slash). CORS applies to `/api/**`.

Email confirm / password-reset links use the **first `https://` origin** from that list:

- Confirm: `{base}/confirm-email?token=...`
- Reset: `{base}/reset-password?token=...`

Requires Resend: `RESEND_API_KEY`. Optional `RESEND_FROM` (defaults to `Around Van <onboarding@resend.dev>`).

### Typical onboarding flow

1. `POST /api/auth/register` → check email
2. Open confirm link → frontend calls `POST /api/auth/confirm-email` → JWT
3. `PUT /api/users/me/location` (and optionally fuel / 311 prefs)
4. Call location-aware endpoints (`/near`, weather without coords, AQHI, etc.)

---

## Authentication

| Access | Meaning |
|--------|---------|
| **Public** | No token required |
| **Auth** | Valid JWT required (`401` if missing/invalid) |
| **Auth + home** | JWT + home location set (`400` if location missing) |

**Public paths:** `/api/auth/**`, `GET /actuator/health`, `GET /api/events/upcoming`, `GET /api/events/past`, `GET /api/wildfires/active`, and `GET /api/weather/current` **only when both** `latitude` and `longitude` query params are present.

Everything else under documented `/api/**` routes requires authentication. Unmatched routes are denied (`403`).

---

## Errors

| Case | Status | Body |
|------|--------|------|
| Username / email taken | `400` | `{ "message": string, "field": "username" \| "email" }` |
| Bean validation (`@Valid`) | `400` | Spring validation error payload |
| Missing home location / bad params | `400` | Status reason message |
| Bad credentials | `401` | Auth failure |
| Missing / invalid JWT on protected route | `401` | OAuth2 resource server |
| Email not confirmed on login | `403` | `"Confirm your email before logging in"` |
| Email service not configured | `503` | When Resend is required but missing |
| Upstream weather failure | `502` / `503` | Meteosource issues |
| Not found (e.g. mark seen) | `404` | — |

Field-taken conflicts use a dedicated shape — show `message` under the input named by `field`.

---

## Enums

| Name | Values |
|------|--------|
| `FuelType` | `REGULAR`, `MIDGRADE`, `PREMIUM` |
| `ServiceRequestCategory` | `ROAD`, `GARBAGE`, `WATER`, `GRAFFITI`, `NOISE`, `SAFETY` |
| `ServiceRequestImportance` | `IMPORTANT`, `LOW` (filter rejects `HIDDEN`) |
| `ServiceRequestStatus` | `OPEN`, `CLOSED` |
| `EventProvider` | `TICKETMASTER` |
| `AqhiRiskLevel` | `LOW`, `MODERATE`, `HIGH`, `VERY_HIGH` |
| `FireDangerClass` | `VERY_LOW`, `LOW`, `MODERATE`, `HIGH`, `EXTREME` (ratings 1–5) |
| `ComparisonBasis` | `PREVIOUS_MONTH`, `CURRENT_MONTH_TO_DATE` |
| `TrendDirection` | `UP`, `DOWN`, `FLAT`, `INSUFFICIENT_DATA` |

---

## Auth

Base path: `/api/auth` — all **public**.

### `POST /api/auth/register`

Creates an unverified account and emails a confirm link. Does **not** return a JWT.

**Request**

| Field | Type | Rules |
|-------|------|-------|
| `username` | string | required, 3–30 chars |
| `email` | string | required, valid email |
| `password` | string | required, 8–100 chars |

```json
{
  "username": "jane",
  "email": "jane@example.com",
  "password": "password123"
}
```

**Response `201`**

```json
{
  "message": "Account created. Check your email to confirm your address before logging in"
}
```

**Response `400`** (username or email taken)

```json
{
  "message": "Username is already taken",
  "field": "username"
}
```

```json
{
  "message": "Email is already taken",
  "field": "email"
}
```

---

### `POST /api/auth/login`

Blocked with `403` until email is confirmed.

**Request**

```json
{
  "username": "jane",
  "password": "password123"
}
```

**Response `200`**

```json
{
  "token": "eyJ..."
}
```

| Status | When |
|--------|------|
| `401` | Bad credentials |
| `403` | Email not verified |

---

### `POST /api/auth/confirm-email`

Consumes the confirm token (24h TTL), marks email verified, returns a JWT.

**Request**

```json
{
  "token": "..."
}
```

**Response `200`** — same as login (`{ "token": "..." }`).

**Errors:** `400` invalid/expired token.

---

### `POST /api/auth/resend-confirmation`

Always `200` (no email enumeration). Re-sends confirm email if the account exists and is unverified.

**Request**

```json
{
  "email": "jane@example.com"
}
```

**Response `200`**

```json
{
  "message": "If an account exists for that email, we sent instructions"
}
```

**Errors:** `503` if Resend is not configured. Rate-limit cooldown may still return the generic `200`.

---

### `POST /api/auth/forgot-password`

Always `200`. Emails a reset link (1h TTL) if the account exists.

**Request**

```json
{
  "email": "jane@example.com"
}
```

**Response `200`** — same generic message as resend-confirmation.

**Errors:** `503` if email is not configured.

---

### `POST /api/auth/reset-password`

**Request**

| Field | Type | Rules |
|-------|------|-------|
| `token` | string | required |
| `password` | string | required, 8–100 chars |

```json
{
  "token": "...",
  "password": "newpassword123"
}
```

**Response `200`**

```json
{
  "message": "Password updated. You can log in with your new password"
}
```

**Errors:** `400` invalid/expired token.

---

## Users

Base path: `/api/users` — all **Auth**.

### `UserDTO`

```json
{
  "id": 1,
  "username": "jane",
  "email": "jane@example.com",
  "emailVerified": true,
  "preferredFuelType": "REGULAR",
  "location": {
    "id": 10,
    "latitude": 49.2827,
    "longitude": -123.1207,
    "postalCodePrefix": "V6B"
  }
}
```

`location` may be `null` until set.

---

### `GET /api/users/me`

Current user profile.

**Response `200`:** `UserDTO`

---

### `PUT /api/users/me/location`

Sets / updates home location (used by near-* endpoints).

**Request**

| Field | Type | Rules |
|-------|------|-------|
| `latitude` | number | required, −90…90 |
| `longitude` | number | required, −180…180 |
| `postalCodePrefix` | string | optional, 3–10 chars |

```json
{
  "latitude": 49.2827,
  "longitude": -123.1207,
  "postalCodePrefix": "V6B"
}
```

**Response `200`:** `UserDTO`

---

### `PUT /api/users/me/fuel-preference`

Sets preferred gas fuel type (defaults gas queries when `fuelType` is omitted).

**Request**

```json
{
  "fuelType": "REGULAR"
}
```

**Response `200`:** `UserDTO`

---

### `GET /api/users/me/service-request-preferences`

Enabled 311 categories. If never set, **all** categories are returned.

**Response `200`**

```json
{
  "categories": ["ROAD", "GARBAGE", "WATER", "GRAFFITI", "NOISE", "SAFETY"]
}
```

---

### `PUT /api/users/me/service-request-preferences`

Replaces 311 category preferences. Empty `categories` = show nothing from 311.

**Request**

```json
{
  "categories": ["ROAD", "GARBAGE", "WATER", "SAFETY"]
}
```

**Response `200`:** same shape as GET.

---

## Gas

Base path: `/api/gas` — all **Auth**. Near / cheapest require **home location**.

### `GasStationResponse`

```json
{
  "id": 42,
  "name": "Shell",
  "address": "1000 Burrard St",
  "postalCodePrefix": "V6Z",
  "latitude": 49.28,
  "longitude": -123.13,
  "distanceKm": 1.2,
  "fuelType": "REGULAR",
  "price": 1.799,
  "observedAt": "2026-08-05T18:00:00Z"
}
```

---

### `GET /api/gas/near`

Nearest priced stations within ~25 km, sorted by distance.

**Auth + home**

| Query | Type | Default |
|-------|------|---------|
| `fuelType` | `FuelType` | user’s preference / `REGULAR` |
| `limit` | integer | `5` (max `25`) |

**Response `200`:** `GasStationResponse[]`

---

### `GET /api/gas/cheapest`

Cheapest stations near the user (within ~25 km).

**Auth + home** — same query params as `/near`.

**Response `200`:** `GasStationResponse[]`

---

### `GET /api/gas/trend`
### `GET /api/gas/trend?fuelType=REGULAR`

City-wide gas price trend for Vancouver-proper stations.

**Auth required. Home location not required.**

- **Today average**: average of each station’s price for today (`America/Vancouver`). If the importer runs more than once, the latest price that day wins per station.
- **Comparison**: last calendar month’s average of daily city averages, when that month has data.
- **Fallback**: if there is no previous month, compare to this month’s average of daily city averages for past days only (today excluded).
- **Insufficient data**: returned when today has no prices, or there is nothing to compare against.

| Query | Type | Default |
|-------|------|---------|
| `fuelType` | `FuelType` | user’s preferred fuel type |

**Response `200`**

```json
{
  "fuelType": "REGULAR",
  "todayDate": "2026-08-05",
  "todayAverage": 1.789,
  "comparisonAverage": 1.750,
  "comparisonBasis": "PREVIOUS_MONTH",
  "comparisonPeriodStart": "2026-07-01",
  "comparisonPeriodEnd": "2026-07-31",
  "direction": "UP",
  "delta": 0.039
}
```

| Field | Notes |
|-------|-------|
| `comparisonBasis` | `PREVIOUS_MONTH` \| `CURRENT_MONTH_TO_DATE` |
| `direction` | `UP` \| `DOWN` \| `FLAT` \| `INSUFFICIENT_DATA` |

When `direction` is `INSUFFICIENT_DATA`, averages / period fields are `null`.

---

### `POST /api/gas/import`

Bulk-import station prices for a postal prefix / fuel type. Any authenticated user (no admin role).

**Request**

| Field | Type | Rules |
|-------|------|-------|
| `postalCodePrefix` | string | required, 3–10 |
| `fuelType` | `FuelType` | required |
| `stations` | array | required, non-empty |
| `stations[].name` | string | required |
| `stations[].address` | string | required |
| `stations[].price` | number | required |
| `stations[].latitude` | number | optional |
| `stations[].longitude` | number | optional |

```json
{
  "postalCodePrefix": "V6B",
  "fuelType": "REGULAR",
  "stations": [
    {
      "name": "Shell",
      "address": "1000 Burrard St",
      "price": 1.799,
      "latitude": 49.28,
      "longitude": -123.13
    }
  ]
}
```

**Response `200`**

```json
{
  "imported": 1,
  "skipped": 0
}
```

---

## Events

Base path: `/api/events`

### `EventResponse`

```json
{
  "id": 1,
  "title": "Concert",
  "description": "...",
  "publishedDate": "2026-07-01T12:00:00Z",
  "dateStart": "2026-08-10T19:00:00Z",
  "dateEnd": "2026-08-10T22:00:00Z",
  "externalUrl": "https://...",
  "imageUrl": "https://...",
  "provider": "TICKETMASTER"
}
```

---

### `GET /api/events/upcoming`

**Public.** All upcoming events.

**Response `200`:** `EventResponse[]`

---

### `GET /api/events/upcoming/near`

**Auth + home.** Upcoming events near the user’s home.

**Response `200`:** `EventResponse[]`

**Errors:** `400` if no home location.

---

### `GET /api/events/past`

**Public.** Past events.

**Response `200`:** `EventResponse[]`

---

## Wildfires

Base path: `/api/wildfires`

### `WildfireResponse`

```json
{
  "id": 1,
  "fireNumber": "K51234",
  "incidentName": "Example Fire",
  "geographicDescription": "Near Highway 99",
  "neighbourhood": null,
  "latitude": 49.5,
  "longitude": -123.0,
  "distanceKm": 42.1,
  "sizeHectares": 120.5,
  "status": "Under Control",
  "cause": "Lightning",
  "responseType": "Full Response",
  "fireType": "Wildfire",
  "ignitionDate": "2026-07-20T08:00:00Z",
  "fireOfNote": false,
  "fireUrl": "https://...",
  "lastSyncedAt": "2026-08-05T18:00:00Z"
}
```

`distanceKm` is populated on nearby queries; may be unused on the global active list.

---

### `GET /api/wildfires/active`

**Public.** All active BC wildfires.

**Response `200`:** `WildfireResponse[]`

---

### `GET /api/wildfires/active/near`

**Auth + home.** Active wildfires nearest to home.

| Query | Type | Default |
|-------|------|---------|
| `limit` | integer | omit / `<= 0` = no limit |

**Response `200`:** `WildfireResponse[]`

---

### `GET /api/wildfires/fire-weather/near`

**Auth + home.** Nearby BCWS fire-weather station readings.

| Query | Type | Default |
|-------|------|---------|
| `radiusKm` | integer | `25` (max `100`) |
| `limit` | integer | optional |

**Response `200`:** `FireWeatherResponse[]`

```json
{
  "stationCode": "123",
  "stationName": "Example Station",
  "fireCentre": "Coastal",
  "latitude": 49.3,
  "longitude": -123.1,
  "distanceKm": 8.2,
  "dangerRating": 3,
  "dangerClass": "MODERATE",
  "dangerLabel": "Moderate",
  "temperature": 22.1,
  "relativeHumidity": 45.0,
  "windSpeedKmh": 12.0,
  "windDirection": 180.0,
  "precipitationMm": 0.0,
  "fineFuelMoistureCode": 85.0,
  "duffMoistureCode": 40.0,
  "droughtCode": 200.0,
  "initialSpreadIndex": 5.0,
  "buildUpIndex": 50.0,
  "fireWeatherIndex": 10.0,
  "observedAt": "2026-08-05T18:00:00Z"
}
```

---

## AQHI

### `GET /api/aqhi/current`

**Auth + home.** Current Air Quality Health Index for the region nearest the user’s home.

**Response `200`**

```json
{
  "regionId": "…",
  "regionName": "Metro Vancouver",
  "neighbourhood": "Kitsilano",
  "value": 2.0,
  "riskLevel": "LOW",
  "riskLabel": "Low",
  "healthMessage": "Ideal air quality for outdoor activities.",
  "observedAt": "2026-08-05T18:00:00Z"
}
```

`riskLevel`: `LOW` | `MODERATE` | `HIGH` | `VERY_HIGH`

**Errors:** `400` if no home location.

---

## Weather

### `GET /api/weather/current`

Current weather for explicit coordinates **or** the user’s home.

| Access | When |
|--------|------|
| **Public** | Both `latitude` and `longitude` provided |
| **Auth + home** | Neither coordinate provided |
| **`400`** | Only one of lat/lng provided |

| Query | Type | Rules |
|-------|------|-------|
| `latitude` | number | optional, −90…90 |
| `longitude` | number | optional, −180…180 |

Examples:

- `GET /api/weather/current?latitude=49.28&longitude=-123.12` — public
- `GET /api/weather/current` — authenticated, uses home

**Response `200`**

```json
{
  "latitude": 49.28,
  "longitude": -123.12,
  "neighbourhood": "Downtown",
  "summary": "Partly cloudy",
  "icon": "partly_cloudy",
  "iconNumber": 4,
  "temperature": 18.5,
  "feelsLike": 17.0,
  "humidity": 70.0,
  "pressure": 1013.0,
  "uvIndex": 3.0,
  "windSpeed": 12.0,
  "windAngle": 180,
  "windDirection": "S",
  "precipitation": 0.0,
  "precipitationType": "none",
  "cloudCoverPercent": 40,
  "units": "metric",
  "fetchedAt": "2026-08-05T18:00:00Z"
}
```

**Errors:** `400` missing home / partial coords; `502`/`503` upstream provider issues.

---

## Service requests (311)

Base path: `/api/service-requests` — all **Auth**. Near endpoints require **home**.

Results respect the user’s category preferences (`/api/users/me/service-request-preferences`).

### `ServiceRequestResponse`

```json
{
  "id": 1,
  "requestType": "Pothole Case",
  "category": "ROAD",
  "importance": "IMPORTANT",
  "status": "OPEN",
  "address": "123 MAIN ST",
  "neighbourhood": "Kitsilano",
  "localArea": "Kitsilano",
  "latitude": 49.26,
  "longitude": -123.16,
  "distanceKm": 0.8,
  "openedAt": "2026-07-28T04:28:00Z",
  "seen": false
}
```

`category`: `ROAD` | `GARBAGE` | `WATER` | `GRAFFITI` | `NOISE` | `SAFETY`  
`importance`: `IMPORTANT` | `LOW`

---

### `GET /api/service-requests/important/near`

**Auth + home.** Unseen `IMPORTANT` requests in the user’s neighbourhood (prefs applied).

**Response `200`:** `ServiceRequestResponse[]`

---

### `GET /api/service-requests/near`
### `GET /api/service-requests/near?importance=IMPORTANT&limit=50`
### `GET /api/service-requests/near?importance=LOW&limit=25`

**Auth + home.** Nearby open requests matching prefs, sorted by distance.

| Query | Type | Default |
|-------|------|---------|
| `importance` | `IMPORTANT` \| `LOW` | all visible |
| `limit` | integer | `50` (max `100`) |

**Response `200`:** same shape as `/important/near` (`seen` may be `true`).

**Errors:** `400` if `importance` is invalid or `HIDDEN`.

---

### `POST /api/service-requests/{id}/seen`

**Auth.** Marks the request as seen for the current user. Drops it from `/important/near`.

**Response:** `204 No Content`

**Errors:** `404` if the request is not found.

---

## Health

### `GET /actuator/health`

**Public.** Process health check (used by deployment probes). Other actuator paths are not exposed.

---

## Endpoint summary

| Method | Path | Auth | Home? |
|--------|------|------|-------|
| `POST` | `/api/auth/register` | Public | — |
| `POST` | `/api/auth/login` | Public | — |
| `POST` | `/api/auth/confirm-email` | Public | — |
| `POST` | `/api/auth/resend-confirmation` | Public | — |
| `POST` | `/api/auth/forgot-password` | Public | — |
| `POST` | `/api/auth/reset-password` | Public | — |
| `GET` | `/api/users/me` | Auth | — |
| `PUT` | `/api/users/me/location` | Auth | — |
| `PUT` | `/api/users/me/fuel-preference` | Auth | — |
| `GET` | `/api/users/me/service-request-preferences` | Auth | — |
| `PUT` | `/api/users/me/service-request-preferences` | Auth | — |
| `GET` | `/api/gas/near` | Auth | Yes |
| `GET` | `/api/gas/cheapest` | Auth | Yes |
| `GET` | `/api/gas/trend` | Auth | No |
| `POST` | `/api/gas/import` | Auth | No |
| `GET` | `/api/events/upcoming` | Public | — |
| `GET` | `/api/events/upcoming/near` | Auth | Yes |
| `GET` | `/api/events/past` | Public | — |
| `GET` | `/api/wildfires/active` | Public | — |
| `GET` | `/api/wildfires/active/near` | Auth | Yes |
| `GET` | `/api/wildfires/fire-weather/near` | Auth | Yes |
| `GET` | `/api/aqhi/current` | Auth | Yes |
| `GET` | `/api/weather/current` | Public *with lat+lng*; Auth otherwise | Yes if no coords |
| `GET` | `/api/service-requests/important/near` | Auth | Yes |
| `GET` | `/api/service-requests/near` | Auth | Yes |
| `POST` | `/api/service-requests/{id}/seen` | Auth | No |
| `GET` | `/actuator/health` | Public | — |
