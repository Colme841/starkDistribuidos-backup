# Stark Sensor Movimiento

Microservicio del sistema de seguridad distribuido **Stark Industries** encargado de detectar y registrar actividad de movimiento en zonas controladas de las instalaciones. Genera lecturas automáticas cada 5 segundos simulando sensores PIR (Passive Infrared) distribuidos por el edificio.

---

## Puerto

```
8083
```

---

## Endpoints REST

Base URL: `http://localhost:8083/api/sensores/movimiento`

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/` | Devuelve todas las lecturas almacenadas |
| `GET` | `/ultimas` | Devuelve las 10 lecturas más recientes |
| `GET` | `/zona/{zona}` | Filtra lecturas por nombre de zona |
| `GET` | `/activos` | Devuelve solo lecturas con movimiento detectado |
| `POST` | `/` | Registra una lectura manual |

### Ejemplos de uso

```bash
# Todas las lecturas
curl http://localhost:8083/api/sensores/movimiento

# Últimas 10
curl http://localhost:8083/api/sensores/movimiento/ultimas

# Por zona
curl "http://localhost:8083/api/sensores/movimiento/zona/Entrada%20Principal"

# Solo con movimiento detectado
curl http://localhost:8083/api/sensores/movimiento/activos

# Registrar lectura manual
curl -X POST http://localhost:8083/api/sensores/movimiento \
  -H "Content-Type: application/json" \
  -d '{
    "zona": "Laboratorio A",
    "movimientoDetectado": true,
    "intensidad": 73.5
  }'
```

---

## Ejemplo de respuesta JSON

```json
[
  {
    "id": 1,
    "zona": "Entrada Principal",
    "movimientoDetectado": true,
    "intensidad": 87.4,
    "timestamp": "2024-06-10T14:32:05.123"
  },
  {
    "id": 2,
    "zona": "Pasillo Norte",
    "movimientoDetectado": false,
    "intensidad": 12.1,
    "timestamp": "2024-06-10T14:32:10.456"
  }
]
```

### Campos de la entidad

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id` | `Long` | Identificador autoincremental |
| `zona` | `String` | Nombre de la zona monitoreada |
| `movimientoDetectado` | `boolean` | `true` si hay actividad de movimiento |
| `intensidad` | `double` | Nivel de intensidad del movimiento (0.0 – 100.0) |
| `timestamp` | `LocalDateTime` | Fecha y hora de la lectura |

### Zonas monitoreadas

- Entrada Principal
- Sala de Servidores
- Laboratorio A
- Laboratorio B
- Pasillo Norte
- Pasillo Sur
- Acceso Roof

---

## Estructura del módulo

```
starkDistribuidos-sensor-movimiento/
├── pom.xml
├── README.md
└── src/
    └── main/
        ├── java/com/distribuidos/stark/sensor/movimiento/
        │   ├── SensorMovimientoApplication.java   # Clase principal
        │   ├── entity/
        │   │   └── SensorMovimiento.java           # Entidad JPA
        │   ├── repository/
        │   │   └── SensorMovimientoRepository.java # JpaRepository
        │   ├── service/
        │   │   └── SensorMovimientoService.java    # Lógica + @Scheduled
        │   └── controller/
        │       └── SensorMovimientoController.java # Endpoints REST
        └── resources/
            └── application.yaml                    # Configuración
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

El servicio se registra en Eureka con el nombre `stark-sensor-movimiento`. Asegúrate de que el servidor Eureka (`starkDistribuidos-config`, puerto 8761) esté corriendo antes de iniciar este microservicio.

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
./mvnw install -pl starkDistribuidos-sensor-movimiento -am

# Ejecutar
./mvnw spring-boot:run -pl starkDistribuidos-sensor-movimiento
```

### Desde el directorio del módulo

```bash
cd starkDistribuidos-sensor-movimiento
mvn spring-boot:run
```

### Con JAR

```bash
./mvnw package -pl starkDistribuidos-sensor-movimiento -am -DskipTests
java -jar starkDistribuidos-sensor-movimiento/target/stark-sensor-movimiento-0.0.1-SNAPSHOT.jar
```

---

## Comportamiento del scheduler

El servicio genera una lectura aleatoria cada **5 segundos** de forma automática:

```
[14:32:05] MOVIMIENTO detectado en "Sala de Servidores" — intensidad: 63.2
[14:32:10] Sin movimiento en "Pasillo Sur"              — intensidad: 8.7
[14:32:15] MOVIMIENTO detectado en "Laboratorio A"      — intensidad: 91.0
```

Los datos quedan persistidos en la base H2 en memoria (`movimientodb`) y son accesibles inmediatamente a través de los endpoints REST.

---

## Consola H2

Disponible en desarrollo en:

```
http://localhost:8083/h2-console
JDBC URL: jdbc:h2:mem:movimientodb
Usuario:  sa
Password: (vacío)
```
