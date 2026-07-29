**Changes**

---

**GET /api/users/me**

Response now includes `preferredFuelType` (default `REGULAR`):

```json
{
  "id": 1,
  "username": "berkay",
  "email": "berkay@email.com",
  "location": null,
  "preferredFuelType": "REGULAR"
}
```

---

**PUT /api/users/me/location**

Request can include optional `postalCodePrefix`:

```json
{
  "latitude": 49.2827,
  "longitude": -123.1207,
  "postalCodePrefix": "V5K 0A1"
}
```

Response:

```json
{
  "id": 1,
  "username": "berkay",
  "email": "berkay@email.com",
  "location": {
    "id": 1,
    "latitude": 49.2827,
    "longitude": -123.1207,
    "postalCodePrefix": "V5K"
  },
  "preferredFuelType": "REGULAR"
}
```

---

**PUT /api/users/me/fuel-preference**

Request:

```json
{ "fuelType": "REGULAR" }
```

`fuelType`: `REGULAR` | `MIDGRADE` | `PREMIUM`

Response: same as `/api/users/me`

---

**GET /api/gas/near?limit=5**
**GET /api/gas/near?fuelType=PREMIUM&limit=5**

Response:

```json
[
  {
    "id": 1,
    "name": "Shell",
    "address": "123 Example St, Vancouver",
    "postalCodePrefix": "V5K",
    "latitude": 49.28,
    "longitude": -123.05,
    "distanceKm": 1.2,
    "fuelType": "REGULAR",
    "price": 1.899,
    "observedAt": "2026-07-27T20:00:00Z"
  }
]
```

Uses preferred fuel unless `fuelType` is set. Stations need coords; within 25 km.

---

**GET /api/gas/cheapest?limit=5**
**GET /api/gas/cheapest?fuelType=MIDGRADE&limit=5**

Response: same as `/api/gas/near`, sorted by price then distance.

---

**POST /api/gas/import**

Request:

```json
{
  "postalCodePrefix": "V5K",
  "fuelType": "REGULAR",
  "stations": [
    {
      "name": "Shell",
      "address": "123 Example St, Vancouver",
      "price": 1.899,
      "latitude": 49.28,
      "longitude": -123.05
    }
  ]
}
```

Response:

```json
{ "imported": 12, "skipped": 8 }
```

`skipped` = non-Vancouver stations (Burnaby, Richmond, North/West Vancouver, etc.). Addresses are normalized on import; duplicates upsert by name+address.