# Stark Sensor Temperatura

Microservicio del sistema de seguridad distribuido **Stark Industries** responsable del monitoreo continuo de temperatura y humedad en las distintas áreas de las instalaciones. Genera lecturas automáticas cada 5 segundos simulando sensores ambientales (DHT22 / PT100) distribuidos por el edificio. Permite detectar condiciones fuera de rango que puedan indicar fallos de refrigeración, incendios o alteraciones ambientales.

---

## Puerto

```
8084
```

---

## Endpoints REST

Base URL: `http://localhost:8084/api/sensores/temperatura`

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/` | Devuelve todas las lecturas almacenadas |
| `GET` | `/ultimas` | Devuelve las 10 lecturas más recientes |
| `GET` | `/ubicacion/{ubicacion}` | Filtra lecturas por nombre de ubicación |
| `GET` | `/promedio` | Devuelve la temperatura promedio global |
| `POST` | `/` | Registra una lectura manual |

### Ejemplos de uso

```bash
# Todas las lecturas
curl http://localhost:8084/api/sensores/temperatura

# Últimas 10
curl http://localhost:8084/api/sensores/temperatura/ultimas

# Por ubicación
curl "http://localhost:8084/api/sensores/temperatura/ubicacion/Centro%20de%20Datos"

# Temperatura promedio global
curl http://localhost:8084/api/sensores/temperatura/promedio

# Registrar lectura manual
curl -X POST http://localhost:8084/api/sensores/temperatura \
  -H "Content-Type: application/json" \
  -d '{
    "ubicacion": "Centro de Datos",
    "temperatura": 22.5,
    "humedad": 45.0
  }'
```

---

## Ejemplo de respuesta JSON

### `GET /api/sensores/temperatura/ultimas`

```json
[
  {
    "id": 12,
    "ubicacion": "Centro de Datos",
    "temperatura": 19.3,
    "humedad": 52.7,
    "timestamp": "2024-06-10T14:32:00.001"
  },
  {
    "id": 11,
    "ubicacion": "Laboratorio Criogénico",
    "temperatura": -7.8,
    "humedad": 30.2,
    "timestamp": "2024-06-10T14:31:55.123"
  }
]
```

### `GET /api/sensores/temperatura/promedio`

```json
{
  "temperaturaPromedio": 21.46
}
```

### Campos de la entidad

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id` | `Long` | Identificador autoincremental |
| `ubicacion` | `String` | Nombre del área monitoreada |
| `temperatura` | `double` | Temperatura en grados Celsius (-10.0 – 50.0) |
| `humedad` | `double` | Humedad relativa en porcentaje (0.0 – 100.0) |
| `timestamp` | `LocalDateTime` | Fecha y hora de la lectura |

### Ubicaciones monitoreadas

- Sala de Control
- Centro de Datos
- Laboratorio Criogénico
- Almacén Norte
- Almacén Sur
- Taller de Ensamblaje
- Oficina Ejecutiva

---

## Estructura del módulo

```
starkDistribuidos-sensor-temperatura/
├── pom.xml
├── README.md
└── src/
    └── main/
        ├── java/com/distribuidos/stark/sensor/temperatura/
        │   ├── SensorTemperaturaApplication.java   # Clase principal
        │   ├── entity/
        │   │   └── SensorTemperatura.java           # Entidad JPA
        │   ├── repository/
        │   │   └── SensorTemperaturaRepository.java # JpaRepository + @Query
        │   ├── service/
        │   │   └── SensorTemperaturaService.java    # Lógica + @Scheduled
        │   └── controller/
        │       └── SensorTemperaturaController.java # Endpoints REST
        └── resources/
            └── application.yaml                     # Configuración
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

El servicio se registra en Eureka con el nombre `stark-sensor-temperatura`. El servidor Eureka (`starkDistribuidos-config`, puerto 8761) debe estar activo antes de arrancar este microservicio.

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
./mvnw install -pl starkDistribuidos-sensor-temperatura -am

# Ejecutar
./mvnw spring-boot:run -pl starkDistribuidos-sensor-temperatura
```

### Desde el directorio del módulo

```bash
cd starkDistribuidos-sensor-temperatura
mvn spring-boot:run
```

### Con JAR

```bash
./mvnw package -pl starkDistribuidos-sensor-temperatura -am -DskipTests
java -jar starkDistribuidos-sensor-temperatura/target/stark-sensor-temperatura-0.0.1-SNAPSHOT.jar
```

---

## Comportamiento del scheduler

El servicio genera una lectura ambiental aleatoria cada **5 segundos**:

```
[14:32:00] Centro de Datos        — Temp: 21.3°C  Humedad: 48.5%
[14:32:05] Laboratorio Criogénico — Temp: -4.7°C  Humedad: 22.1%
[14:32:10] Almacén Norte          — Temp: 35.8°C  Humedad: 71.3%
```

Los rangos generados son:
- **Temperatura:** -10.0 °C a 50.0 °C (redondeado a 1 decimal)
- **Humedad:** 0.0 % a 100.0 % (redondeado a 1 decimal)

---

## Consola H2

Disponible en desarrollo en:

```
http://localhost:8084/h2-console
JDBC URL: jdbc:h2:mem:temperaturadb
Usuario:  sa
Password: (vacío)
```
