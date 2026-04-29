package com.distribuidos.stark.sensor.acceso.service;

import com.distribuidos.stark.sensor.acceso.entity.SensorAcceso;
import com.distribuidos.stark.sensor.acceso.repository.SensorAccesoRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class SensorAccesoService {

    private static final String[] PUERTAS = {
        "Puerta Principal", "Acceso Laboratorio", "Sala de Servidores",
        "Área Restringida A", "Área Restringida B", "Salida de Emergencia"
    };

    private static final String[] TARJETAS = {
        "STARK-001", "STARK-002", "STARK-003", "STARK-004",
        "STARK-005", "GUEST-001", "GUEST-002", "UNKNOWN-999"
    };

    private final SensorAccesoRepository repository;

    public SensorAccesoService(SensorAccesoRepository repository) {
        this.repository = repository;
    }

    @Scheduled(fixedRate = 5000)
    @Transactional
    public void generarLecturaAleatoria() {
        String tarjeta = TARJETAS[ThreadLocalRandom.current().nextInt(TARJETAS.length)];
        SensorAcceso evento = new SensorAcceso();
        evento.setPuerta(PUERTAS[ThreadLocalRandom.current().nextInt(PUERTAS.length)]);
        evento.setTarjetaId(tarjeta);
        // Las tarjetas UNKNOWN son siempre denegadas; el resto tienen 85 % de aprobación
        evento.setAutorizado(!tarjeta.startsWith("UNKNOWN") && ThreadLocalRandom.current().nextInt(100) < 85);
        evento.setTimestamp(LocalDateTime.now());
        repository.save(evento);
    }

    @Transactional(readOnly = true)
    public List<SensorAcceso> obtenerTodos() {
        return repository.findTop50ByOrderByTimestampDesc();
    }

    @Transactional(readOnly = true)
    public List<SensorAcceso> obtenerUltimos() {
        return repository.findTop10ByOrderByTimestampDesc();
    }

    @Transactional(readOnly = true)
    public List<SensorAcceso> obtenerPorPuerta(String puerta) {
        return repository.findByPuerta(puerta);
    }

    @Transactional(readOnly = true)
    public List<SensorAcceso> obtenerPorTarjeta(String tarjetaId) {
        return repository.findByTarjetaId(tarjetaId);
    }

    @Transactional(readOnly = true)
    public List<SensorAcceso> obtenerDenegados() {
        return repository.findByAutorizadoFalse();
    }

    @Transactional
    public SensorAcceso guardar(SensorAcceso evento) {
        evento.setTimestamp(LocalDateTime.now());
        return repository.save(evento);
    }
}