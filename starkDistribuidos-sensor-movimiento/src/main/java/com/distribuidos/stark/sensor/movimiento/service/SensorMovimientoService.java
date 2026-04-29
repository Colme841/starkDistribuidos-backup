package com.distribuidos.stark.sensor.movimiento.service;

import com.distribuidos.stark.sensor.movimiento.entity.SensorMovimiento;
import com.distribuidos.stark.sensor.movimiento.repository.SensorMovimientoRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class SensorMovimientoService {

    private static final String[] ZONAS = {
        "Entrada Principal", "Sala de Servidores", "Laboratorio A",
        "Laboratorio B", "Pasillo Norte", "Pasillo Sur", "Acceso Roof"
    };

    private final SensorMovimientoRepository repository;

    public SensorMovimientoService(SensorMovimientoRepository repository) {
        this.repository = repository;
    }

    @Scheduled(fixedRate = 5000)
    @Transactional
    public void generarLecturaAleatoria() {
        SensorMovimiento lectura = new SensorMovimiento();
        lectura.setZona(ZONAS[ThreadLocalRandom.current().nextInt(ZONAS.length)]);
        lectura.setMovimientoDetectado(ThreadLocalRandom.current().nextBoolean());
        lectura.setIntensidad(Math.round(ThreadLocalRandom.current().nextDouble() * 100.0 * 10.0) / 10.0);
        lectura.setTimestamp(LocalDateTime.now());
        repository.save(lectura);
    }

    @Transactional(readOnly = true)
    public List<SensorMovimiento> obtenerTodas() {
        return repository.findTop50ByOrderByTimestampDesc();
    }

    @Transactional(readOnly = true)
    public List<SensorMovimiento> obtenerUltimas() {
        return repository.findTop10ByOrderByTimestampDesc();
    }

    @Transactional(readOnly = true)
    public List<SensorMovimiento> obtenerPorZona(String zona) {
        return repository.findByZona(zona);
    }

    @Transactional(readOnly = true)
    public List<SensorMovimiento> obtenerConMovimiento() {
        return repository.findByMovimientoDetectadoTrue();
    }

    @Transactional
    public SensorMovimiento guardar(SensorMovimiento lectura) {
        lectura.setTimestamp(LocalDateTime.now());
        return repository.save(lectura);
    }
}