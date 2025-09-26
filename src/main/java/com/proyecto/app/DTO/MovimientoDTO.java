package com.proyecto.app.DTO;

import com.proyecto.app.model.Movimiento;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MovimientoDTO {
    private LocalDateTime fecha;
    private String cliente;
    private String numeroCuenta;
    private String tipoCuenta;
    private BigDecimal saldoInicial;
    private Boolean estado;
    private String tipoMovimiento;
    private BigDecimal valorMovimiento;
    private BigDecimal saldoDisponible;

    public MovimientoDTO(Movimiento movimiento) {
        this.fecha = movimiento.getFecha();
        this.cliente = movimiento.getCuenta().getCliente().getNombre(); // 👈 ajusta si el campo del cliente es distinto
        this.numeroCuenta = movimiento.getCuenta().getNumero();
        this.tipoCuenta = movimiento.getCuenta().getTipo();
        this.saldoInicial = movimiento.getCuenta().getSaldoInicial();
        this.estado = movimiento.getCuenta().getEstado();
        this.tipoMovimiento = movimiento.getTipo();
        this.valorMovimiento = movimiento.getValor();
        this.saldoDisponible = movimiento.getSaldo();
    }
}
