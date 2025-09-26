package com.proyecto.app.repository;

import com.proyecto.app.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    // Ejemplo de búsqueda personalizada ya existente
    Optional<Cliente> findByIdentificacion(String identificacion);

    @Override
    List<Cliente> findAll();

    @Override
    Optional<Cliente> findById(Long id);

    @Override
    <S extends Cliente> S save(S entity);

    @Override
    void delete(Cliente entity);

    @Override
    void deleteById(Long id);
}
