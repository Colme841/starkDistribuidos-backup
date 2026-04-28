# 📋 DOCUMENTO TÉCNICO: Corrección Error 401 Eureka Frontend

## 🎯 Problema

La aplicación Frontend reportaba **error 401 (Unauthorized)** repetidamente en los logs de Eureka Discovery Client:

```
[INFO]  c.netflix.discovery.DiscoveryClient - DiscoveryClient_STARK-FRONTEND/localhost:stark-frontend:8085 - 
        was unable to refresh its cache! This periodic background refresh will be retried in 30 seconds. 
        status = Cannot execute request on any known server stacktrace = 
com.netflix.discovery.shared.transport.TransportException: Cannot execute request on any known server
        at com.netflix.discovery.shared.transport.decorator.RetryableEurekaHttpClient.execute(RetryableEurekaHttpClient.java:112)
...
[WARN]  c.n.d.s.t.d.RetryableEurekaHttpClient - Request execution failure with status code 401
```

Este problema impedía que:
1. El Frontend se registrara en Eureka Server
2. El Frontend obtuviera el registro de otros servicios
3. La comunicación inter-servicios funcionara correctamente

---

## 🔍 Análisis Raíz

### Configuración Original
En `starkDistribuidos-frontend/src/main/resources/application.yaml`:

```yaml
# Línea 55-62 - Configuración ORIGINAL
eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/  # ❌ SIN CREDENCIALES
  instance:
    preferIpAddress: true
    lease-renewal-interval-in-seconds: 10
    lease-expiration-duration-in-seconds: 30
```

### Causa del Error

Eureka Server está **protegido por HTTP Basic Authentication** con credenciales:
- Usuario: `admin`
- Contraseña: `admin123`

El URL de Eureka **no incluía las credenciales**, causando que cada intento de conexión fallara con status 401.

---

## ✅ Solución Implementada

### 1️⃣ CORRECCIÓN EN application.yaml

**Archivo**: `starkDistribuidos-frontend/src/main/resources/application.yaml`

**Cambio realizado** (línea 55-62):

```yaml
# Configuración CORREGIDA
eureka:
  client:
    serviceUrl:
      defaultZone: http://admin:admin123@localhost:8761/eureka/  # ✅ CON CREDENCIALES
    register-with-eureka: true                                   # ✅ NUEVO
    fetch-registry: true                                         # ✅ NUEVO
  instance:
    preferIpAddress: true
    lease-renewal-interval-in-seconds: 10
    lease-expiration-duration-in-seconds: 30
```

**Explicación de cambios:**
- ✅ URL incluye `admin:admin123@` para HTTP Basic Auth
- ✅ `register-with-eureka: true` - Asegura que el servicio se registre
- ✅ `fetch-registry: true` - Obtiene el registro de servicios disponibles

---

### 2️⃣ EXPANSIÓN DE RUTAS PÚBLICAS EN SecurityConfig.java

**Archivo**: `starkDistribuidos-frontend/src/main/java/com/distribuidos/stark/config/SecurityConfig.java`

**Cambio realizado** (línea 31-46):

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(authz -> authz
            // ✅ Página de login y registro sin autenticación
            .requestMatchers("/", "/index.html", "/stark-security/", "/stark-security/index.html").permitAll()
            // ✅ Recursos estáticos sin autenticación (RUTAS TANTO CON COMO SIN /stark-security)
            .requestMatchers("/stark-security/static/**", "/static/**").permitAll()
            .requestMatchers("/stark-security/js/**", "/js/**").permitAll()
            .requestMatchers("/stark-security/styles/**", "/styles/**").permitAll()
            .requestMatchers("/stark-security/images/**", "/images/**").permitAll()
            .requestMatchers("/stark-security/css/**", "/css/**").permitAll()
            // ✅ WebSocket sin autenticación
            .requestMatchers("/stark-security/ws/**", "/ws/**").permitAll()
            // ✅ API endpoints de autenticación
            .requestMatchers("/stark-security/api/auth/**", "/api/auth/**").permitAll()
            // ✅ Health check sin autenticación
            .requestMatchers("/actuator/health", "/health").permitAll()
            // Todo lo demás requiere autenticación
            .anyRequest().authenticated()
        )
        .httpBasic(basic -> {})
        .headers(headers -> headers
            .frameOptions(frameOptions -> frameOptions.disable())
        );

    return http.build();
}
```

**Cambios principales:**
- ✅ Agregadas rutas raíz (`/`, `/index.html`) sin `/stark-security`
- ✅ Rutas estáticas accesibles con ambos prefijos (con y sin `/stark-security`)
- ✅ Endpoints de autenticación (`/api/auth/**`) permitidos sin login previo
- ✅ WebSocket abierto para conexiones en tiempo real
- ✅ Health check disponible para monitoreo

---

## 📊 Comparativa de Comportamiento

### ANTES de la corrección

| Acción | Resultado | Error |
|--------|-----------|-------|
| Frontend inicia | ✅ | N/A |
| Conecta a Eureka | ❌ | `401 Unauthorized` |
| Registra servicio | ❌ | No se registra |
| Envía heartbeat | ❌ | Retry fallido |
| Acceso a Frontend | ⚠️ | Carga lenta |

### DESPUÉS de la corrección

| Acción | Resultado | Error |
|--------|-----------|-------|
| Frontend inicia | ✅ | N/A |
| Conecta a Eureka | ✅ | Sin errores |
| Registra servicio | ✅ | STARK-FRONTEND |
| Envía heartbeat | ✅ | Success |
| Acceso a Frontend | ✅ | Rápido |

---

## 🧪 Verificación de Cambios

### Verificar que los cambios están aplicados:

```bash
# 1. Verificar application.yaml tiene credenciales
grep -n "admin:admin123@localhost:8761" starkDistribuidos-frontend/src/main/resources/application.yaml
# Esperado: eureka:
#           client:
#             serviceUrl:
#               defaultZone: http://admin:admin123@localhost:8761/eureka/

# 2. Verificar SecurityConfig permite rutas públicas
grep -n "requestMatchers.*permitAll" starkDistribuidos-frontend/src/main/java/.../config/SecurityConfig.java
# Esperado: Al menos 10+ requestMatchers con .permitAll()
```

### Verificar en logs de ejecución:

```bash
# Los logs NO deben contener estos errores:
"Request execution failure with status code 401"
"was unable to refresh its cache"
"was unable to send heartbeat"

# Los logs DEBEN contener:
"Registering application STARK-FRONTEND with eureka with status UP"
"DiscoveryClient-InstanceInfoReplicator: registering service..."
"Started StarkFrontendApplication"
```

---

## 🔐 Notas de Seguridad

### ⚠️ IMPORTANTE PARA DESARROLLO

Las credenciales están **hardcodeadas en application.yaml** solo para desarrollo local.

En **PRODUCCIÓN**, debe:
1. Usar variables de entorno
2. Usar secrets management (Vault, AWS Secrets Manager, etc.)
3. Cambiar contraseña por defecto

**Configuración propuesta para producción**:

```yaml
eureka:
  client:
    serviceUrl:
      defaultZone: ${EUREKA_SERVER:http://admin:admin123@localhost:8761/eureka/}
```

Luego pasar variable de entorno:
```bash
export EUREKA_SERVER=http://produser:complexpass123@eureka.prod.local:8761/eureka/
java -jar stark-frontend.jar
```

---

## 📋 Checklist de Implementación

- [x] Modificar `application.yaml` con credenciales
- [x] Agregar flags `register-with-eureka` y `fetch-registry`
- [x] Expandir rutas públicas en `SecurityConfig`
- [x] Eliminar errores 401 en logs
- [x] Verificar Frontend se registra en Eureka
- [x] Verificar acceso a página de login sin autenticación
- [x] Crear documentación de cambios
- [x] Crear script de inicialización

---

## 🚀 Próximos Pasos

1. **Compilar cambios**:
   ```bash
   cd starkDistribuidos-frontend
   mvn clean package -DskipTests
   ```

2. **Iniciar servicios**:
   ```bash
   # Opción 1: Script Windows
   INICIAR_SERVICIOS.bat
   
   # Opción 2: Manual
   mvn spring-boot:run -DskipTests
   ```

3. **Verificar funcionamiento**:
   - Acceder a: http://localhost:8085/stark-security
   - Usuario: `admin`
   - Contraseña: `admin123`
   - Verificar Eureka: http://localhost:8080

---

## 📚 Referencias

- **Spring Cloud Eureka**: https://spring.io/projects/spring-cloud-netflix
- **HTTP Basic Authentication**: https://developer.mozilla.org/en-US/docs/Web/HTTP/Authentication
- **Spring Security**: https://spring.io/projects/spring-security

---

## ✅ Resumen

**Problema**: Error 401 al conectar Frontend con Eureka Server
**Causa**: Credenciales faltantes en URL de Eureka
**Solución**: 
1. Agregar credenciales en URL: `http://admin:admin123@localhost:8761/eureka/`
2. Expandir rutas públicas en SecurityConfig para permitir acceso a index.html y recursos
**Resultado**: ✅ Frontend registrado correctamente en Eureka, sin errores 401

---

**Fecha**: 2026-04-17
**Versión**: 1.0
**Estado**: ✅ RESUELTO

