# ✅ Solución: Error 401 de Eureka en Frontend

## 🔴 Problema Identificado

El Frontend recibía errores repetidos **401 (Unauthorized)** del Eureka Server:

```
WARN  c.n.d.s.t.d.RetryableEurekaHttpClient - Request execution failure with status code 401
ERROR c.netflix.discovery.DiscoveryClient - DiscoveryClient_STARK-FRONTEND/.../stark-frontend:8085 - was unable to send heartbeat!
```

**Causa raíz**: La configuración de desarrollo del Frontend no incluía las **credenciales de autenticación** para conectarse a Eureka Server.

---

## ✅ Soluciones Aplicadas

### 1. **Agregar Credenciales a Eureka (application.yaml)**

**Archivo**: `starkDistribuidos-frontend/src/main/resources/application.yaml`

**Cambio**:
```yaml
# ANTES (línea 59):
eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/

# DESPUÉS:
eureka:
  client:
    serviceUrl:
      defaultZone: http://admin:admin123@localhost:8761/eureka/
    register-with-eureka: true
    fetch-registry: true
```

**¿Por qué?** 
- Eureka Server requiere autenticación básica (HTTP Basic Auth)
- Las credenciales: `admin:admin123` coinciden con la configuración de Eureka
- Agregar `register-with-eureka: true` asegura que el frontend se registre
- Agregar `fetch-registry: true` permite obtener el registro de servicios

### 2. **Expandir Rutas Permitidas sin Autenticación (SecurityConfig.java)**

**Archivo**: `starkDistribuidos-frontend/src/main/java/.../config/SecurityConfig.java`

**Cambio**:
```java
.authorizeHttpRequests(authz -> authz
    // Página de login y registro sin autenticación
    .requestMatchers("/", "/index.html", "/stark-security/", "/stark-security/index.html").permitAll()
    // Recursos estáticos sin autenticación
    .requestMatchers("/stark-security/static/**", "/static/**").permitAll()
    .requestMatchers("/stark-security/js/**", "/js/**").permitAll()
    .requestMatchers("/stark-security/styles/**", "/styles/**").permitAll()
    // API endpoints de autenticación
    .requestMatchers("/stark-security/api/auth/**", "/api/auth/**").permitAll()
    // ...
)
```

**¿Por qué?**
- Permite que usuarios accedan a `index.html` sin autenticación
- Permite acceso a JS, CSS y otros archivos estáticos
- Permite acceso a endpoints de autenticación (login/registro)

---

## 📊 Estado Actual

✅ **Frontend**: Ejecutándose en `http://localhost:8085/stark-security`
✅ **Eureka**: Registración completada sin errores 401
✅ **Archivos estáticos**: Accesibles sin autenticación
✅ **Credenciales de prueba**: 
- Usuario: `admin`
- Contraseña: `admin123`

---

## 🚀 Cómo Acceder

1. **Abrir navegador**: [http://localhost:8085/stark-security](http://localhost:8085/stark-security)
2. **Ingresar credenciales**:
   - **Usuario**: `admin`
   - **Contraseña**: `admin123`
3. **Acceso al Dashboard**: Sistema de Seguridad Stark Industries

---

## 📝 Cambios Realizados

### Archivo 1: `application.yaml`
- ✅ Agregadas credenciales a URL de Eureka
- ✅ Habilitado registro y obtención de registro

### Archivo 2: `SecurityConfig.java`
- ✅ Expandidas rutas permitidas sin autenticación
- ✅ Rutas tanto con `/stark-security` como sin prefijo
- ✅ Endpoints de autenticación abiertos

---

## 🔍 Verificación

Para verificar que todo está funcionando:

```powershell
# 1. Verificar que Frontend responde
curl http://localhost:8085/stark-security/

# 2. Verificar que Eureka no reporta errores 401
# (Ver logs del terminal sin mensajes "Request execution failure")

# 3. Verificar que index.html es accesible
curl http://localhost:8085/stark-security/index.html
```

---

## 📌 Notas Importantes

1. **Credenciales compartidas**: Los usuarios `admin:admin123`, `security:security123`, `user:user123` están definidos en memoria (solo desarrollo)
2. **HTTP Basic Auth**: Eureka usa autenticación básica HTTP, no JWT
3. **Context path**: Todas las rutas están bajo `/stark-security` (configurable en `application.yaml`)
4. **Registro en Eureka**: El Frontend se auto-registra cada 10 segundos

---

## 🔐 Seguridad en Producción

Para ambiente **production**, cambiar en `application.yaml` (sección `production`):
```yaml
eureka:
  client:
    serviceUrl:
      defaultZone: ${EUREKA_SERVER:http://admin:admin123@localhost:8761/eureka/}
```

Usar variables de entorno para credenciales sensibles.

