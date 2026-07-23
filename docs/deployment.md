# Production deployment runbook

## Release gates

1. Frontend lint, render tests, and production build pass.
2. Backend unit and Testcontainers integration tests pass on Java 21.
3. CodeQL, dependency, container, and IaC scans have no unaccepted critical or
   high findings.
4. Flyway migration is reviewed against a production-sized clone.
5. Backup restoration is demonstrated.
6. k6 thresholds pass in a production-like environment.
7. Security, privacy, and clinical operations owners approve the release.

## Rollout

1. Build immutable frontend/backend images and tag them with the commit SHA.
2. Apply configuration and secret-manager references.
3. Run Flyway as a controlled pre-deployment job.
4. Deploy the backend and wait for readiness.
5. Deploy the frontend with its API URL baked in.
6. Run login, patient search, registration, billing idempotency, and health
   smoke tests.
7. Watch error rate, latency, saturation, database connections, and lock waits.

## Rollback

Application images can roll back to the previous immutable tag. Database
migrations are forward-only: prepare a compatible corrective migration instead
of assuming destructive rollback. Use expand/migrate/contract changes whenever
old and new application versions overlap.

## Kubernetes example

Replace all placeholders under `deploy/k8s`, then:

```bash
kubectl apply -f deploy/k8s/namespace.yaml
kubectl apply -f deploy/k8s/config.yaml
kubectl apply -f deploy/k8s/backend.yaml
kubectl apply -f deploy/k8s/frontend.yaml
kubectl apply -f deploy/k8s/scaling.yaml
kubectl apply -f deploy/k8s/ingress.yaml
```

The checked-in Secret is only a schema example. Prefer External Secrets, CSI
Secret Store, Sealed Secrets, or the cloud provider's native secret integration.

