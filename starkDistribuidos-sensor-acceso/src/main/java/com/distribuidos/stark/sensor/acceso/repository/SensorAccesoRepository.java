package com.distribuidos.stark.sensor.acceso.repository;

import com.distribuidos.stark.sensor.acceso.entity.SensorAcceso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SensorAccesoRepository extends JpaRepository<SensorAcceso, Long> {

    List<SensorAcceso> findTop50ByOrderByTimestampDesc();

    List<SensorAcceso> findTop10ByOrderByTimestampDesc();

    List<SensorAcceso> findByPuerta(String puerta);

    List<SensorAcceso> findByTarjetaId(String tarjetaId);

    List<SensorAcceso> findByAutorizadoFalse();
}
