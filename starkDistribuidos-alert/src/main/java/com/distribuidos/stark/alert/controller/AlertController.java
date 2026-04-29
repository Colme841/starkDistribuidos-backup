package com.distribuidos.stark.alert.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {
    @GetMapping
    public ResponseEntity<List<?>> getAllAlerts() {
        return ResponseEntity.ok(Arrays.asList());
    }
    @PostMapping
    public ResponseEntity<?> createAlert(@RequestBody Object alert) {
        return ResponseEntity.ok(alert);
    }
}

