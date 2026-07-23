# Security and privacy launch checklist

## Implemented controls

- Deny-by-default role authorization
- Signed, expiring JWT access tokens
- User active-state check on every authenticated request
- BCrypt password hashes with cost factor 12
- Exact-origin CORS allowlist
- Request validation and maximum field lengths
- Generic login failures to reduce username enumeration
- Parameterized persistence and no raw user SQL
- Environment-only secrets and disabled production admin bootstrap
- Request IDs, audit history, rate limiting, and safe error responses
- Non-root containers with dropped Linux capabilities and read-only filesystems
- Separate management port not routed by the public ingress
- CodeQL and dependency-update automation

## Required before real patient data

- Put secrets in a cloud secret manager; do not apply the example Kubernetes
  Secret unchanged.
- Terminate modern TLS at a trusted ingress and encrypt database backups.
- Use a managed identity provider with MFA and short session policies.
- Add refresh-token rotation or an enterprise session gateway if long sessions
  are required.
- Define least-privilege role assignments and a joiner/mover/leaver process.
- Redact or tokenize patient identifiers in logs, traces, analytics, and support
  exports.
- Set retention/deletion policies and record consent/legal-basis requirements.
- Test restore procedures, regional failover, RPO, and RTO.
- Commission SAST, DAST, dependency, container, IaC, and penetration testing.
- Have legal/compliance owners map controls to applicable HIPAA, DPDP, GDPR, or
  local health-data requirements.

## Known design boundaries

The included in-memory rate limiter is a per-replica abuse guard, not a global
quota service. For internet-scale enforcement, place a distributed gateway/WAF
in front of the API. The frontend access token intentionally disappears on
refresh; production SSO can replace this with an HttpOnly secure-cookie session
or an enterprise identity proxy.

