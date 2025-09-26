package com.proyecto.app.service;

import com.proyecto.app.model.Cuenta;
import com.proyecto.app.repository.CuentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CuentaService {

    private final CuentaRepository cuentaRepository;

    public Cuenta crear(Cuenta cuenta) {
        return cuentaRepository.save(cuenta);
    }

    public List<Cuenta> listarTodas() {
        return cuentaRepository.findAll();
    }

    public Optional<Cuenta> obtenerPorId(Long id) {
        return cuentaRepository.findById(id);
    }

    public Optional<Cuenta> actualizar(Long id, Cuenta datos) {
        return cuentaRepository.findById(id).map(cuenta -> {
            cuenta.setNumero(datos.getNumero());
            cuenta.setTipo(datos.getTipo());
            cuenta.setSaldoInicial(datos.getSaldoInicial());
            cuenta.setCliente(datos.getCliente());
            return cuentaRepository.save(cuenta);
        });
    }

    public boolean eliminar(Long id) {
        return cuentaRepository.findById(id).map(cuenta -> {
            cuentaRepository.delete(cuenta);
            return true;
        }).orElse(false);
    }
}
