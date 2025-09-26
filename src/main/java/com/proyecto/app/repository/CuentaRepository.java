package com.proyecto.app.repository;

import com.proyecto.app.model.Cliente;
import com.proyecto.app.model.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CuentaRepository extends JpaRepository<Cuenta, Long> {
    Optional<Cuenta> findByNumero(String numero);

    List<Cuenta> findByCliente(Cliente cliente);

    Optional<Cuenta> findByNumeroIgnoreCase(String numero);

    List<Cuenta> findByTipo(String tipo);
}
