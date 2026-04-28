package com.distribuidos.stark.sensor.temperatura.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "sensor_temperatura")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SensorTemperatura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String ubicacion;

    /** Temperatura en grados Celsius (-10.0 – 50.0) */
    @Column(nullable = false)
    private double temperatura;

    /** Humedad relativa en porcentaje (0.0 – 100.0) */
    @Column(nullable = false)
    private double humedad;

    @Column(nullable = false)
    private LocalDateTime timestamp;
}
