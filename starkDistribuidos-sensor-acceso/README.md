# Stark Sensor Acceso

Microservicio del sistema de seguridad distribuido **Stark Industries** encargado de registrar y auditar todos los intentos de acceso a zonas controladas mediante tarjetas de identificación. Genera eventos automáticos cada 5 segundos simulando lectores RFID instalados en puertas de acceso restringido. Los accesos con tarjetas desconocidas son siempre denegados; las tarjetas registradas tienen un 85 % de probabilidad de aprobación.

---

## Puerto

```
8085
```

---

## Endpoints REST

Base URL: `http://localhost:8085/api/sensores/acceso`

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/` | Devuelve todos los eventos de acceso |
| `GET` | `/ultimos` | Devuelve los 10 eventos más recientes |
| `GET` | `/puerta/{puerta}` | Filtra eventos por puerta |
| `GET` | `/tarjeta/{tarjetaId}` | Historial de accesos de una tarjeta |
| `GET` | `/denegados` | Devuelve solo accesos denegados |
| `POST` | `/` | Registra un evento de acceso manual |

### Ejemplos de uso

```bash
# Todos los eventos
curl http://localhost:8085/api/sensores/acceso

# Últimos 10 eventos
curl http://localhost:8085/api/sensores/acceso/ultimos

# Por puerta
curl "http://localhost:8085/api/sensores/acceso/puerta/Sala%20de%20Servidores"

# Historial de una tarjeta
curl http://localhost:8085/api/sensores/acceso/tarjeta/STARK-001

# Accesos denegados
curl http://localhost:8085/api/sensores/acceso/denegados

# Registrar evento manual
curl -X POST http://localhost:8085/api/sensores/acceso \
  -H "Content-Type: application/json" \
  -d '{
    "puerta": "Área Restringida A",
    "tarjetaId": "STARK-003",
    "autorizado": true
  }'
```

---

## Ejemplo de respuesta JSON

### `GET /api/sensores/acceso/ultimos`

```json
[
  {
    "id": 24,
    "puerta": "Sala de Servidores",
    "tarjetaId": "STARK-002",
    "autorizado": true,
    "timestamp": "2024-06-10T14:32:05.001"
  },
  {
    "id": 23,
    "puerta": "Área Restringida A",
    "tarjetaId": "UNKNOWN-999",
    "autorizado": false,
    "timestamp": "2024-06-10T14:32:00.312"
  }
]
```

### `GET /api/sensores/acceso/denegados`

```json
[
  {
    "id": 23,
    "puerta": "Área Restringida A",
    "tarjetaId": "UNKNOWN-999",
    "autorizado": false,
    "timestamp": "2024-06-10T14:32:00.312"
  },
  {
    "id": 18,
    "puerta": "Acceso Laboratorio",
    "tarjetaId": "GUEST-002",
    "autorizado": false,
    "timestamp": "2024-06-10T14:31:30.089"
  }
]
```

### Campos de la entidad

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id` | `Long` | Identificador autoincremental |
| `puerta` | `String` | Nombre del punto de acceso controlado |
| `tarjetaId` | `String` | Identificador de la tarjeta presentada |
| `autorizado` | `boolean` | `true` si el acceso fue concedido |
| `timestamp` | `LocalDateTime` | Fecha y hora del evento |

### Tarjetas registradas

| Tarjeta | Tipo | Comportamiento |
|---------|------|----------------|
| `STARK-001` a `STARK-005` | Personal Stark | 85 % aprobación |
| `GUEST-001`, `GUEST-002` | Visitantes | 85 % aprobación |
| `UNKNOWN-999` | Desconocida | Siempre denegada |

### Puertas monitoreadas

- Puerta Principal
- Acceso Laboratorio
- Sala de Servidores
- Área Restringida A
- Área Restringida B
- Salida de Emergencia

---

## Estructura del módulo

```
starkDistribuidos-sensor-acceso/
├── pom.xml
├── README.md
└── src/
    └── main/
        ├── java/com/distribuidos/stark/sensor/acceso/
        │   ├── SensorAccesoApplication.java   # Clase principal
        │   ├── entity/
        │   │   └── SensorAcceso.java           # Entidad JPA
        │   ├── repository/
        │   │   └── SensorAccesoRepository.java # JpaRepository
        │   ├── service/
        │   │   └── SensorAccesoService.java    # Lógica + @Scheduled
        │   └── controller/
        │       └── SensorAccesoController.java # Endpoints REST
        └── resources/
            └── application.yaml               # Configuración
```

---

## Configuración de Eureka

```yaml
eureka:
  client:
    service-url:
      defaultZone: http://admin:admin123@localhost:8761/eureka/
    register-with-eureka: true
    fetch-registry: true
  instance:
    prefer-ip-address: true
```

El servicio se registra en Eureka con el nombre `stark-sensor-acceso`. El servidor Eureka (`starkDistribuidos-config`, puerto 8761) debe estar activo antes de arrancar este microservicio.

---

## Dependencias principales

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
```

Hereda versiones de `stark-parent` (Spring Boot 3.2.5 / Spring Cloud 2023.0.1 / Java 17).

---

## Cómo ejecutar localmente

### Prerrequisitos

- Java 17+
- Maven 3.8+ (o usar el wrapper `mvnw` incluido en la raíz del proyecto)
- Eureka Server corriendo en `localhost:8761`

### Desde la raíz del proyecto (recomendado)

```bash
# Compilar solo este módulo
./mvnw install -pl starkDistribuidos-sensor-acceso -am

# Ejecutar
./mvnw spring-boot:run -pl starkDistribuidos-sensor-acceso
```

### Desde el directorio del módulo

```bash
cd starkDistribuidos-sensor-acceso
mvn spring-boot:run
```

### Con JAR

```bash
./mvnw package -pl starkDistribuidos-sensor-acceso -am -DskipTests
java -jar starkDistribuidos-sensor-acceso/target/stark-sensor-acceso-0.0.1-SNAPSHOT.jar
```

---

## Comportamiento del scheduler

El servicio genera un evento de acceso aleatorio cada **5 segundos**:

```
[14:32:00] AUTORIZADO  — STARK-002 en "Sala de Servidores"
[14:32:05] DENEGADO    — UNKNOWN-999 en "Área Restringida A"
[14:32:10] AUTORIZADO  — GUEST-001 en "Puerta Principal"
[14:32:15] DENEGADO    — STARK-004 en "Área Restringida B"  (15% de rechazo aleatorio)
```

Las tarjetas `UNKNOWN-*` son bloqueadas siempre. El resto tienen un 15 % de rechazo aleatorio para simular fallos de lectura o permisos revocados.

---

## Consola H2

Disponible en desarrollo en:

```
http://localhost:8085/h2-console
JDBC URL: jdbc:h2:mem:accesodb
Usuario:  sa
Password: (vacío)
```
