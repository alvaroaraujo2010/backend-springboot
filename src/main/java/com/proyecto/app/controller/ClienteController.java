package com.proyecto.app.controller;

import com.proyecto.app.model.Cliente;
import com.proyecto.app.repository.ClienteRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteRepository clienteRepository;

    @PostMapping
    public ResponseEntity<Cliente> create(@Valid @RequestBody Cliente cliente) {
        Cliente saved = clienteRepository.save(cliente);
        return ResponseEntity.status(201).body(saved);
    }

    @GetMapping
    public ResponseEntity<List<Cliente>> list() {
        return ResponseEntity.ok(clienteRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> getById(@PathVariable Long id) {
        return clienteRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cliente> update(@PathVariable Long id,
                                          @Valid @RequestBody Cliente datos) {
        return clienteRepository.findById(id)
                .map(cliente -> {
                    cliente.setNombre(datos.getNombre());
                    cliente.setEmail(datos.getEmail());
                    cliente.setTelefono(datos.getTelefono());
                    cliente.setIdentificacion(datos.getIdentificacion());
                    Cliente actualizado = clienteRepository.save(cliente);
                    return ResponseEntity.ok(actualizado);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        return clienteRepository.findById(id)
                .map(c -> {
                    clienteRepository.delete(c);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
