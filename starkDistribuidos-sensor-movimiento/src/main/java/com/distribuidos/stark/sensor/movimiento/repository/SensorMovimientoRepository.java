package com.distribuidos.stark.sensor.movimiento.repository;

import com.distribuidos.stark.sensor.movimiento.entity.SensorMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SensorMovimientoRepository extends JpaRepository<SensorMovimiento, Long> {

    List<SensorMovimiento> findTop50ByOrderByTimestampDesc();

    List<SensorMovimiento> findTop10ByOrderByTimestampDesc();

    List<SensorMovimiento> findByZona(String zona);

    List<SensorMovimiento> findByMovimientoDetectadoTrue();
}
