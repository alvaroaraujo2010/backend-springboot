package com.proyecto.app.controller;

import com.proyecto.app.model.Cliente;
import com.proyecto.app.model.Cuenta;
import com.proyecto.app.repository.ClienteRepository;
import com.proyecto.app.repository.CuentaRepository;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/cuentas")
@RequiredArgsConstructor
public class CuentaController {

    private final CuentaRepository cuentaRepository;
    private final ClienteRepository clienteRepository;

    @PostMapping
    public ResponseEntity<Cuenta> create(@Valid @RequestBody CuentaRequest req) {
        Cliente cliente = clienteRepository.findById(req.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        Cuenta c = new Cuenta();
        c.setNumero(req.getNumero());
        c.setTipo(req.getTipo());
        c.setSaldoInicial(req.getSaldoInicial());
        c.setEstado(true);
        c.setCliente(cliente);
        return ResponseEntity.status(201).body(cuentaRepository.save(c));
    }

    @GetMapping
    public ResponseEntity<List<Cuenta>> list() {
        return ResponseEntity.ok(cuentaRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cuenta> getById(@PathVariable Long id) {
        return cuentaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cuenta> update(@PathVariable Long id,
                                         @Valid @RequestBody CuentaRequest req) {
        return cuentaRepository.findById(id)
                .map(cuenta -> {
                    Cliente cliente = clienteRepository.findById(req.getClienteId())
                            .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
                    cuenta.setNumero(req.getNumero());
                    cuenta.setTipo(req.getTipo());
                    cuenta.setSaldoInicial(req.getSaldoInicial());
                    cuenta.setCliente(cliente);
                    Cuenta updated = cuentaRepository.save(cuenta);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        return cuentaRepository.findById(id)
                .map(c -> {
                    cuentaRepository.delete(c);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ DTO para peticiones
    @Data
    public static class CuentaRequest {
        private String numero;
        private String tipo;
        private BigDecimal saldoInicial;
        private Long clienteId;
    }
}
