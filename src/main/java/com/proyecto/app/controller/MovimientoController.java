package com.proyecto.app.controller;

import com.proyecto.app.model.Movimiento;
import com.proyecto.app.service.MovimientoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/movimientos")
@RequiredArgsConstructor
public class MovimientoController {
    private final MovimientoService movimientoService;

    @PostMapping
    public ResponseEntity<Movimiento> create(@Valid @RequestBody MovimientoRequest req){
        Movimiento m = movimientoService.create(req.numeroCuenta, req.tipo, req.valor, req.referencia);
        return ResponseEntity.status(201).body(m);
    }

    @GetMapping("/cuenta/{numero}")
    public ResponseEntity<List<Movimiento>> list(@PathVariable String numero){
        return ResponseEntity.ok(movimientoService.listByCuenta(numero, LocalDateTime.of(1970,1,1,0,0), LocalDateTime.now()));
    }

    public static class MovimientoRequest { public String numeroCuenta; public String tipo; public java.math.BigDecimal valor; public String referencia; }
}
