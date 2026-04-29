package com.distribuidos.stark.sensor.temperatura.controller;

import com.distribuidos.stark.sensor.temperatura.entity.SensorTemperatura;
import com.distribuidos.stark.sensor.temperatura.service.SensorTemperaturaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sensores/temperatura")
@CrossOrigin(origins = "*")
public class SensorTemperaturaController {

    private final SensorTemperaturaService service;

    public SensorTemperaturaController(SensorTemperaturaService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<SensorTemperatura>> obtenerTodas() {
        return ResponseEntity.ok(service.obtenerTodas());
    }

    @GetMapping("/ultimas")
    public ResponseEntity<List<SensorTemperatura>> obtenerUltimas() {
        return ResponseEntity.ok(service.obtenerUltimas());
    }

    @GetMapping("/ubicacion/{ubicacion}")
    public ResponseEntity<List<SensorTemperatura>> obtenerPorUbicacion(@PathVariable String ubicacion) {
        return ResponseEntity.ok(service.obtenerPorUbicacion(ubicacion));
    }

    @GetMapping("/promedio")
    public ResponseEntity<Map<String, Double>> obtenerPromedio() {
        return ResponseEntity.ok(Map.of("temperaturaPromedio", service.obtenerTemperaturaPromedio()));
    }

    @PostMapping
    public ResponseEntity<SensorTemperatura> crear(@Valid @RequestBody SensorTemperatura lectura) {
        return ResponseEntity.ok(service.guardar(lectura));
    }
}
