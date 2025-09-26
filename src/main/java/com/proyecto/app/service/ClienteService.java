package com.proyecto.app.service;

import com.proyecto.app.model.Cliente;
import com.proyecto.app.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public List<Cliente> listar() {
        return clienteRepository.findAll();
    }

    public Optional<Cliente> obtenerPorId(Long id) {
        return clienteRepository.findById(id);
    }

    public Optional<Cliente> obtenerPorIdentificacion(String identificacion) {
        return clienteRepository.findByIdentificacion(identificacion);
    }

    public Cliente crear(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    public Cliente actualizar(Long id, Cliente datos) {
        return clienteRepository.findById(id)
                .map(c -> {
                    c.setNombre(datos.getNombre());
                    c.setEmail(datos.getEmail());
                    c.setTelefono(datos.getTelefono());
                    c.setIdentificacion(datos.getIdentificacion());
                    return clienteRepository.save(c);
                })
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
    }

    public void eliminar(Long id) {
        clienteRepository.deleteById(id);
    }
}
