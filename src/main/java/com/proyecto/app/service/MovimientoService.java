package com.proyecto.app.service;

import com.proyecto.app.model.Movimiento;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface MovimientoService {
    Movimiento create(String numeroCuenta, String tipo, BigDecimal valor, String referencia);
    List<Movimiento> listByCuenta(String numeroCuenta, LocalDateTime from, LocalDateTime to);
    BigDecimal saldoActual(String numeroCuenta);
}
