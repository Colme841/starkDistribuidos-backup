package com.distribuidos.stark.sensor.temperatura.repository;

import com.distribuidos.stark.sensor.temperatura.entity.SensorTemperatura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SensorTemperaturaRepository extends JpaRepository<SensorTemperatura, Long> {

    List<SensorTemperatura> findTop10ByOrderByTimestampDesc();

    List<SensorTemperatura> findByUbicacion(String ubicacion);

    @Query("SELECT AVG(s.temperatura) FROM SensorTemperatura s")
    Optional<Double> findTemperaturaPromedio();
}
