# API examples

Set `API=http://localhost:8080/api/v1`.

## Login

```bash
TOKEN="$(
  curl -fsS "$API/auth/login" \
    -H 'Content-Type: application/json' \
    -d '{"username":"admin","password":"your-password"}' |
  jq -r .token
)"
```

## Search patients

```bash
curl -fsS "$API/patient/search?query=sharma&page=0&size=20" \
  -H "Authorization: Bearer $TOKEN"
```

## Register a patient

```bash
curl -fsS "$API/patient" \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{
    "patientName":"Aditi Sharma",
    "age":32,
    "gender":"Female",
    "mobile":"9876543210",
    "address":"Noida"
  }'
```

## Create an idempotent pharmacy bill

```bash
curl -fsS "$API/billing" \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{
    "patientId":1,
    "appointmentId":1,
    "barcode":"MED-001",
    "quantity":2,
    "idempotencyKey":"invoice-2026-000001"
  }'
```

Repeating the same idempotency key returns the existing bill rather than
decrementing stock again.

