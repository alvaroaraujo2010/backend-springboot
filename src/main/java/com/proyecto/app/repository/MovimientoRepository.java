package com.proyecto.app.repository;

import com.proyecto.app.model.Movimiento;
import com.proyecto.app.model.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {
    List<Movimiento> findByCuentaAndFechaBetween(Cuenta cuenta, LocalDateTime from, LocalDateTime to);
}
