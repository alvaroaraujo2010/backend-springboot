package com.proyecto.app.model;

import com.proyecto.app.model.Movimiento;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class EstadoCuentaReporte {
    private String numeroCuenta;
    private String tipoCuenta;
    private String cliente;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private List<Movimiento> movimientos;
    private BigDecimal totalCreditos;
    private BigDecimal totalDebitos;
    private BigDecimal saldoFinal;
}