# 🔧 FIX: Error 401 Eureka Frontend - Guía Rápida

## ⚡ TL;DR (Too Long; Didn't Read)

**Problema**: Frontend no se conecta a Eureka (error 401)
**Solución**: Se agregaron credenciales a la configuración de Eureka

---

## 📂 Cambios Realizados

### ✅ 1. Archivo: `starkDistribuidos-frontend/src/main/resources/application.yaml`

```yaml
# Línea 59 - ANTES:
defaultZone: http://localhost:8761/eureka/

# Línea 59 - DESPUÉS:
defaultZone: http://admin:admin123@localhost:8761/eureka/

# Líneas 60-61 - NUEVO:
register-with-eureka: true
fetch-registry: true
```

### ✅ 2. Archivo: `starkDistribuidos-frontend/.../SecurityConfig.java`

Agregadas rutas públicas sin autenticación:
- `GET /` y `/index.html`
- `GET /stark-security/static/**`
- `GET /stark-security/js/**`
- `POST /stark-security/api/auth/**`
- Y más...

---

## 🚀 Cómo Usar

### Opción 1: Script Automático (Recomendado)
```bash
# Ejecutar en Windows (PowerShell o CMD)
cd C:\ruta\a\StarkDistribuidos
.\INICIAR_SERVICIOS.bat
```

### Opción 2: Manual

#### Terminal 1 - Eureka Server:
```bash
cd starkDistribuidos-gateway
mvn spring-boot:run -DskipTests
```

#### Terminal 2 - Frontend:
```bash
cd starkDistribuidos-frontend
mvn spring-boot:run -DskipTests
```

---

## 🌐 Acceso

Una vez que todo esté corriendo:

| Servicio | URL | Usuario | Contraseña |
|----------|-----|---------|-----------|
| **Frontend** | http://localhost:8085/stark-security | admin | admin123 |
| **Eureka** | http://localhost:8080 | admin | admin123 |
| **Health** | http://localhost:8085/stark-security/actuator/health | N/A | N/A |

---

## ✅ Verificación

### Logs esperados (NO deben aparecer errores 401):
```
✅ Registering application STARK-FRONTEND with eureka with status UP
✅ DiscoveryClient-InstanceInfoReplicator: registering service...
✅ Started StarkFrontendApplication in X.XXX seconds
```

### Logs incorrectos (DEBEN desaparecer):
```
❌ Request execution failure with status code 401
❌ was unable to refresh its cache
❌ was unable to send heartbeat
```

---

## 🔐 Credenciales de Prueba

```
🧑 Usuario: admin
🔑 Contraseña: admin123
```

También disponibles:
- Usuario: `security` / Contraseña: `security123`
- Usuario: `user` / Contraseña: `user123`

---

## 📊 Antes vs Después

| Aspecto | ANTES ❌ | DESPUÉS ✅ |
|--------|---------|----------|
| Errores 401 | Cada 10 segundos | Ninguno |
| Frontend registrado | ❌ No | ✅ Sí |
| Acceso a login | ❌ 401 | ✅ Funciona |
| Status de Eureka | ❌ No accesible | ✅ Accesible |

---

## 🆘 Troubleshooting

### Problema: "Connection refused"
```
Significa que Eureka Server no está corriendo
Solución: Ejecutar primero INICIAR_SERVICIOS.bat o terminal Eureka
```

### Problema: Puerto 8085 en uso
```
taskkill /F /IM java.exe
```

### Problema: Cambios no se aplicaron
```
mvn clean package -DskipTests
mvn spring-boot:run -DskipTests
```

---

## 📚 Documentación Detallada

Para más información, consultar:
- `docs/SOLUCION_EUREKA_401.md` - Solución completa
- `docs/DOCUMENTO_TECNICO_FIX_401.md` - Análisis técnico
- `docs/FIX_401_RESUMEN.md` - Resumen ejecutivo

---

## 🎯 Próximos Pasos

1. ✅ Iniciar servicios
2. ✅ Acceder a http://localhost:8085/stark-security
3. ✅ Ingresar credenciales (admin/admin123)
4. ✅ Verificar que funciona el dashboard
5. ✅ Comprobar estado en Eureka (http://localhost:8080)

---

**Estado**: ✅ RESUELTO
**Última actualización**: 2026-04-17

