# ✅ CHECKLIST: Validar Corrección de Error 401

## 📋 Pre-Ejecución

- [ ] Backend y Frontend compilados (`mvn clean package`)
- [ ] No hay procesos Java antiguos corriendo (`taskkill /F /IM java.exe`)
- [ ] Puertos 8080 y 8085 están libres
- [ ] Cambios guardados y aplicados

---

## 🔧 Verificar Cambios en Código

### application.yaml
```bash
# Comando: Verificar que tiene credenciales
grep "admin:admin123@localhost:8761" \
  starkDistribuidos-frontend/src/main/resources/application.yaml
```

- [ ] URL contiene `http://admin:admin123@localhost:8761/eureka/`
- [ ] `register-with-eureka: true` está presente
- [ ] `fetch-registry: true` está presente

### SecurityConfig.java
```bash
# Comando: Verificar rutas públicas
grep -c "permitAll()" \
  starkDistribuidos-frontend/src/main/java/.../config/SecurityConfig.java
```

- [ ] Mínimo 10 líneas con `.permitAll()`
- [ ] Rutas incluyen `/`, `/index.html`
- [ ] Rutas incluyen `/stark-security/static/**`
- [ ] Rutas incluyen `/stark-security/api/auth/**`

---

## 🚀 Durante la Ejecución

### Eureka Server (Gateway) - Terminal 1

```bash
cd starkDistribuidos-gateway
mvn spring-boot:run -DskipTests
```

✅ Checklist Eureka:
- [ ] Compila sin errores
- [ ] Inicia en puerto 8080
- [ ] No hay errores en logs
- [ ] Se ve: "Tomcat started on port 8080"

### Frontend - Terminal 2

```bash
cd starkDistribuidos-frontend
mvn spring-boot:run -DskipTests
```

✅ Checklist Frontend:
- [ ] Compila sin errores
- [ ] Inicia en puerto 8085
- [ ] Se ve: "Tomcat started on port 8085 (http) with context path '/stark-security'"
- [ ] Se ve: "Registering application STARK-FRONTEND with eureka with status UP"

---

## 🔍 Verificar Logs (NO deben aparecer)

En los logs del Frontend, **BUSCAR** estas cadenas:
- [ ] ❌ "Request execution failure with status code 401"
- [ ] ❌ "was unable to send heartbeat"
- [ ] ❌ "was unable to refresh its cache"
- [ ] ❌ "registration failed Cannot execute request"

Si aparecen, significa que los cambios NO se aplicaron correctamente.

---

## 🌐 Pruebas en Navegador

### Test 1: Acceder a Frontend sin autenticación

```
URL: http://localhost:8085/stark-security/
```

✅ Esperado:
- [ ] Se carga la página de login
- [ ] NO aparece un diálogo de autenticación HTTP
- [ ] Status Code: 200 OK

❌ No esperado:
- [ ] Diálogo de autenticación HTTP Basic
- [ ] Status Code: 401 Unauthorized

### Test 2: Acceder a index.html

```
URL: http://localhost:8085/stark-security/index.html
```

✅ Esperado:
- [ ] Se carga el HTML
- [ ] Status Code: 200 OK

### Test 3: Acceder a archivo JS

```
URL: http://localhost:8085/stark-security/static/js/app.js
```

✅ Esperado:
- [ ] Se descarga el archivo
- [ ] Status Code: 200 OK

### Test 4: Acceder a Health Check

```
URL: http://localhost:8085/stark-security/actuator/health
```

✅ Esperado:
- [ ] JSON: `{"status":"UP"}`
- [ ] Status Code: 200 OK
- [ ] NO requiere autenticación

---

## 🔐 Prueba de Credenciales

### Test 5: Login con credenciales correctas

1. Acceder a: http://localhost:8085/stark-security
2. Ingresar credenciales:
   - Usuario: `admin`
   - Contraseña: `admin123`

✅ Esperado:
- [ ] Dashboard se carga
- [ ] Se ve el menú de navegación
- [ ] Status: Logged in

### Test 6: Login con credenciales incorrectas

1. Acceder a: http://localhost:8085/stark-security
2. Ingresar credenciales falsas

✅ Esperado:
- [ ] Muestra error de autenticación
- [ ] NO permite acceso al dashboard

---

## 📊 Verificar Eureka Dashboard

### Test 7: Acceder a Eureka Dashboard

```
URL: http://localhost:8080
```

✅ Esperado:
- [ ] Dashboard carga sin errores
- [ ] Aparece "STARK-FRONTEND" en "Instances currently registered with Eureka"
- [ ] Status: "UP"
- [ ] Instances: Mínimo 1

Buscar sección "Instances currently registered with Eureka":
```
STARK-FRONTEND instances - 1
├── STARK-FRONTEND (localhost:stark-frontend:8085)
    Status: UP
    Leases: 1
```

---

## 🧪 Test de Conectividad

### Test 8: Verificar heartbeat Eureka

En los logs del Frontend, deberían aparece mensajes cada 10 segundos:
```
[INFO] DiscoveryClient sending heartbeat...
```

✅ Esperado:
- [ ] Heartbeats cada 10 segundos
- [ ] Status: SUCCESS (no FAILED)

---

## 📈 Verificaciones Adicionales

### Test 9: WebSocket (en tiempo real)

Si accedes al dashboard después de login:
- [ ] La sección "Feed en Tiempo Real" muestra eventos
- [ ] No hay errores en consola del navegador

### Test 10: API Endpoints

```bash
# Probar endpoint sin autenticación
curl http://localhost:8085/stark-security/actuator/health

# Esperado: {"status":"UP"}
```

---

## 🎯 Resumen Final

### Si TODO está ✅:
```
✅ Cambios aplicados correctamente
✅ Eureka sin errores 401
✅ Frontend registrado en Eureka
✅ Dashboard accesible
✅ Credenciales funcionan
✅ Recursos estáticos cargables
✅ Health check funciona
✅ WebSocket conecta
```

**RESULTADO: ✅ ÉXITO - Problema resuelto**

---

### Si algo falla ❌:

1. Verificar logs en ambas terminales
2. Buscar error 401 en logs
3. Verificar que los cambios se guardaron:
   ```bash
   grep "admin:admin123" application.yaml
   ```
4. Hacer rebuild:
   ```bash
   mvn clean package -DskipTests
   ```
5. Reiniciar servicios

---

## 📞 Troubleshooting

| Problema | Solución |
|----------|----------|
| Error 401 en logs | Verificar credenciales en application.yaml |
| Connection refused | Eureka no está corriendo en puerto 8080 |
| Port 8085 already in use | `taskkill /F /IM java.exe` |
| Login bloqueado | Verificar SecurityConfig permite `/stark-security/api/auth/**` |
| 404 en recursos estáticos | Verificar rutas en WebConfig y SecurityConfig |

---

## ✅ CHECKLIST FINAL

Completar esta sección cuando TODO esté funcionando:

```
┌────────────────────────────────────────────┐
│ VALIDACIÓN FINAL - PROBLEMA 401 RESUELTO   │
├────────────────────────────────────────────┤
│ ☐ application.yaml modificado correctamente│
│ ☐ SecurityConfig modificado correctamente  │
│ ☐ Frontend compila sin errores             │
│ ☐ Eureka compila sin errores               │
│ ☐ No hay errores 401 en logs               │
│ ☐ Frontend registrado en Eureka            │
│ ☐ Dashboard accesible en navegador         │
│ ☐ Login funciona con admin/admin123        │
│ ☐ Recursos estáticos cargan correctamente  │
│ ☐ Health check funciona (200 OK)           │
│ ☐ Eureka Dashboard muestra STARK-FRONTEND  │
│ ☐ Websocket conecta sin errores            │
└────────────────────────────────────────────┘

Fecha de validación: ___/___/______
Validado por: _________________
```

---

**Fin del checklist. Si todos los items están ✅, el problema está RESUELTO.**

