package com.distribuidos.stark.sensor.temperatura.service;

import com.distribuidos.stark.sensor.temperatura.entity.SensorTemperatura;
import com.distribuidos.stark.sensor.temperatura.repository.SensorTemperaturaRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class SensorTemperaturaService {

    private static final String[] UBICACIONES = {
        "Sala de Control", "Centro de Datos", "Laboratorio Criogénico",
        "Almacén Norte", "Almacén Sur", "Taller de Ensamblaje", "Oficina Ejecutiva"
    };

    private final SensorTemperaturaRepository repository;
    private final Random random = new Random();

    public SensorTemperaturaService(SensorTemperaturaRepository repository) {
        this.repository = repository;
    }

    @Scheduled(fixedRate = 5000)
    @Transactional
    public void generarLecturaAleatoria() {
        SensorTemperatura lectura = new SensorTemperatura();
        lectura.setUbicacion(UBICACIONES[random.nextInt(UBICACIONES.length)]);
        // Temperatura entre -10.0 y 50.0 °C
        double temp = -10.0 + (random.nextDouble() * 60.0);
        lectura.setTemperatura(Math.round(temp * 10.0) / 10.0);
        // Humedad entre 0.0 y 100.0 %
        lectura.setHumedad(Math.round(random.nextDouble() * 100.0 * 10.0) / 10.0);
        lectura.setTimestamp(LocalDateTime.now());
        repository.save(lectura);
    }

    @Transactional(readOnly = true)
    public List<SensorTemperatura> obtenerTodas() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public List<SensorTemperatura> obtenerUltimas() {
        return repository.findTop10ByOrderByTimestampDesc();
    }

    @Transactional(readOnly = true)
    public List<SensorTemperatura> obtenerPorUbicacion(String ubicacion) {
        return repository.findByUbicacion(ubicacion);
    }

    @Transactional(readOnly = true)
    public Double obtenerTemperaturaPromedio() {
        return repository.findTemperaturaPromedio().orElse(0.0);
    }

    @Transactional
    public SensorTemperatura guardar(SensorTemperatura lectura) {
        lectura.setTimestamp(LocalDateTime.now());
        return repository.save(lectura);
    }
}
