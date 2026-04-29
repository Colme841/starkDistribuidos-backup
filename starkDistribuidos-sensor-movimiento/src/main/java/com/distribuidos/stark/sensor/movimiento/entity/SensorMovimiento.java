package com.distribuidos.stark.sensor.movimiento.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "sensor_movimiento")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SensorMovimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String zona;

    @Column(nullable = false)
    private boolean movimientoDetectado;

    /** Intensidad de movimiento en porcentaje (0.0 – 100.0) */
    @Column(nullable = false)
    private double intensidad;

    @Column(nullable = false)
    private LocalDateTime timestamp;
}
