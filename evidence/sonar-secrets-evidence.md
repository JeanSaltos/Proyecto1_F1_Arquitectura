# Sonar Secrets Evidence

Estado esperado:
- No debe existir token Sonar en archivos versionados.
- `target/` esta ignorado por `.gitignore`.
- GitHub Actions usa solo `secrets.SONAR_TOKEN` y `secrets.SONAR_ORGANIZATION`.

Comandos:
```powershell
rg -n --hidden --glob '!.git/**' --pcre2 '(sqp_[A-Za-z0-9]{20,}|sonar\.login=|sonar\.token=)'
git check-ignore -v ms-auth/target/sonar/report-task.txt
git check-ignore -v ms-auth/target/ms-auth-0.0.1-SNAPSHOT.jar
```

Resultado esperado:
```text
Sin tokens reales encontrados.
.gitignore: target/ ...
```

Accion externa obligatoria si alguna vez se expuso un token:
1. Revocar el token en SonarCloud.
2. Crear un token nuevo.
3. Guardarlo solo como GitHub Secret `SONAR_TOKEN`.
