package com.distribuidos.stark.sensor.acceso.controller;

import com.distribuidos.stark.sensor.acceso.entity.SensorAcceso;
import com.distribuidos.stark.sensor.acceso.service.SensorAccesoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sensores/acceso")
@CrossOrigin(origins = "*")
public class SensorAccesoController {

    private final SensorAccesoService service;

    public SensorAccesoController(SensorAccesoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<SensorAcceso>> obtenerTodos() {
        return ResponseEntity.ok(service.obtenerTodos());
    }

    @GetMapping("/ultimos")
    public ResponseEntity<List<SensorAcceso>> obtenerUltimos() {
        return ResponseEntity.ok(service.obtenerUltimos());
    }

    @GetMapping("/puerta/{puerta}")
    public ResponseEntity<List<SensorAcceso>> obtenerPorPuerta(@PathVariable String puerta) {
        return ResponseEntity.ok(service.obtenerPorPuerta(puerta));
    }

    @GetMapping("/tarjeta/{tarjetaId}")
    public ResponseEntity<List<SensorAcceso>> obtenerPorTarjeta(@PathVariable String tarjetaId) {
        return ResponseEntity.ok(service.obtenerPorTarjeta(tarjetaId));
    }

    @GetMapping("/denegados")
    public ResponseEntity<List<SensorAcceso>> obtenerDenegados() {
        return ResponseEntity.ok(service.obtenerDenegados());
    }

    @PostMapping
    public ResponseEntity<SensorAcceso> crear(@RequestBody SensorAcceso evento) {
        return ResponseEntity.ok(service.guardar(evento));
    }
}
