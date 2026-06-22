# Secrets Evidence

Variables locales requeridas (`.env`, no versionar):
```text
POSTGRES_USER
POSTGRES_PASSWORD
RABBITMQ_DEFAULT_USER
RABBITMQ_DEFAULT_PASS
JWT_SECRET
JWT_EXPIRATION_MS
```

Kubernetes:
```powershell
kubectl apply -f k8s/secrets.yaml
kubectl describe secret logiflow-secrets
```

GitHub Actions Secrets requeridos:
```text
SONAR_TOKEN
SONAR_ORGANIZATION
TELEGRAM_CHAT_ID
TELEGRAM_BOT_TOKEN
```

Comprobacion:
```powershell
rg -n --hidden --glob '!**/target/**' --glob '!.git/**' --pcre2 '(sqp_[A-Za-z0-9]{20,}|ghp_[A-Za-z0-9_]{20,}|github_pat_[A-Za-z0-9_]{20,}|-----BEGIN (RSA |OPENSSH |EC |DSA )?PRIVATE KEY-----)'
git check-ignore -v .env .env.local
```
