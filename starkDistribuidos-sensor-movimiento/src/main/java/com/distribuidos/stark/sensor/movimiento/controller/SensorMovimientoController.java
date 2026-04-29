package com.distribuidos.stark.sensor.movimiento.controller;

import com.distribuidos.stark.sensor.movimiento.entity.SensorMovimiento;
import com.distribuidos.stark.sensor.movimiento.service.SensorMovimientoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sensores/movimiento")
@CrossOrigin(origins = "*")
public class SensorMovimientoController {

    private final SensorMovimientoService service;

    public SensorMovimientoController(SensorMovimientoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<SensorMovimiento>> obtenerTodas() {
        return ResponseEntity.ok(service.obtenerTodas());
    }

    @GetMapping("/ultimas")
    public ResponseEntity<List<SensorMovimiento>> obtenerUltimas() {
        return ResponseEntity.ok(service.obtenerUltimas());
    }

    @GetMapping("/zona/{zona}")
    public ResponseEntity<List<SensorMovimiento>> obtenerPorZona(@PathVariable String zona) {
        return ResponseEntity.ok(service.obtenerPorZona(zona));
    }

    @GetMapping("/activos")
    public ResponseEntity<List<SensorMovimiento>> obtenerConMovimiento() {
        return ResponseEntity.ok(service.obtenerConMovimiento());
    }

    @PostMapping
    public ResponseEntity<SensorMovimiento> crear(@Valid @RequestBody SensorMovimiento lectura) {
        return ResponseEntity.ok(service.guardar(lectura));
    }
}
