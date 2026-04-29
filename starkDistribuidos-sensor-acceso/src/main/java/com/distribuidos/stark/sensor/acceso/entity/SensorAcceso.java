package com.distribuidos.stark.sensor.acceso.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "sensor_acceso")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SensorAcceso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String puerta;

    @NotBlank
    @Column(nullable = false)
    private String tarjetaId;

    @Column(nullable = false)
    private boolean autorizado;

    @Column(nullable = false)
    private LocalDateTime timestamp;
}
