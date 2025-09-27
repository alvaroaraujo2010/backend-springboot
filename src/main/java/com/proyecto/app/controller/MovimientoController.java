package com.proyecto.app.controller;

import com.proyecto.app.DTO.MovimientoDTO;
import com.proyecto.app.model.EstadoCuentaReporte;
import com.proyecto.app.model.Movimiento;
import com.proyecto.app.service.MovimientoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/movimientos")
@RequiredArgsConstructor
public class MovimientoController {
    private final MovimientoService movimientoService;

    @PostMapping
    public ResponseEntity<Movimiento> create(@Valid @RequestBody MovimientoRequest req) {
        Movimiento m = movimientoService.create(req.numeroCuenta, req.tipo, req.valor, req.referencia);
        return ResponseEntity.status(201).body(m);
    }

    @GetMapping("/cuenta/{numero}")
    public ResponseEntity<List<Movimiento>> list(@PathVariable String numero) {
        return ResponseEntity.ok(
                movimientoService.listByCuenta(
                        numero,
                        LocalDateTime.of(1970, 1, 1, 0, 0),
                        LocalDateTime.now()
                )
        );
    }

    @GetMapping("/all")
    public ResponseEntity<List<Movimiento>> listAllWithSaldo() {
        return ResponseEntity.ok(
                movimientoService.listAllWithSaldo()
                        .stream()
                        .toList()
        );
    }

    @GetMapping("/cuenta/{numero}/saldo")
    public ResponseEntity<List<Movimiento>> listWithSaldo(@PathVariable String numero,
                                                          @RequestParam(required = false) String from,
                                                          @RequestParam(required = false) String to) {
        LocalDateTime fromDate = (from == null) ? LocalDateTime.of(1970, 1, 1, 0, 0)
                : LocalDate.parse(from).atStartOfDay();
        LocalDateTime toDate   = (to == null)   ? LocalDateTime.now()
                : LocalDate.parse(to).atTime(LocalTime.MAX);

        return ResponseEntity.ok(movimientoService.listWithSaldo(numero, fromDate, toDate));
    }

    @GetMapping("/reporte")
    public ResponseEntity<EstadoCuentaReporte> generarReporte(@RequestParam String numeroCuenta) {
        LocalDateTime fromDate = LocalDateTime.of(1970, 1, 1, 0, 0);
        LocalDateTime toDate   = LocalDateTime.now();

        EstadoCuentaReporte reporte = movimientoService.generarReporte(numeroCuenta, fromDate, toDate);
        return ResponseEntity.ok(reporte);
    }

    public static class MovimientoRequest {
        public String numeroCuenta;
        public String tipo;
        public java.math.BigDecimal valor;
        public String referencia;
    }
}
