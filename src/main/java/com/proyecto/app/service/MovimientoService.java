package com.proyecto.app.service;

import com.proyecto.app.model.Movimiento;
import com.proyecto.app.model.EstadoCuentaReporte;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface MovimientoService {
    Movimiento create(String numeroCuenta, String tipo, BigDecimal valor, String referencia);
    List<Movimiento> listByCuenta(String numeroCuenta, LocalDateTime from, LocalDateTime to);
    BigDecimal saldoActual(String numeroCuenta);
    List<Movimiento> listWithSaldo(String numeroCuenta, LocalDateTime from, LocalDateTime to);
    EstadoCuentaReporte generarReporte(String numeroCuenta, LocalDateTime from, LocalDateTime to);
    String exportarReportePDFBase64(EstadoCuentaReporte reporte);
    List<Movimiento> listAllWithSaldo();
    List<Movimiento> listarMovimientosConSaldo(String numeroCuenta, LocalDateTime desde, LocalDateTime hasta);
}
