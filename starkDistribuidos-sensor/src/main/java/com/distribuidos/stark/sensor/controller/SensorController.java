package com.distribuidos.stark.sensor.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/sensors")
public class SensorController {
    @GetMapping
    public ResponseEntity<List<?>> getAllSensors() {
        return ResponseEntity.ok(Arrays.asList());
    }
    @PostMapping
    public ResponseEntity<?> createSensor(@RequestBody Object sensor) {
        return ResponseEntity.ok(sensor);
    }
}

