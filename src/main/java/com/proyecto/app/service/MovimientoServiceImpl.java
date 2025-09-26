package com.proyecto.app.service;

import com.proyecto.app.model.Cuenta;
import com.proyecto.app.model.Movimiento;
import com.proyecto.app.repository.CuentaRepository;
import com.proyecto.app.repository.MovimientoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class MovimientoServiceImpl implements MovimientoService {

    private final MovimientoRepository movimientoRepository;
    private final CuentaRepository cuentaRepository;

    private final Predicate<Movimiento> esDebito = m -> "DEBITO".equalsIgnoreCase(m.getTipo());

    @Override
    @Transactional
    public Movimiento create(String numeroCuenta, String tipo, BigDecimal valor, String referencia) {
        Cuenta cuenta = cuentaRepository.findByNumero(numeroCuenta)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

        BigDecimal current = saldoActual(numeroCuenta);

        if ("DEBITO".equalsIgnoreCase(tipo) && current.compareTo(valor) < 0) {
            throw new RuntimeException("Saldo insuficiente");
        }

        Movimiento m = new Movimiento();
        m.setCuenta(cuenta);
        m.setTipo(tipo.toUpperCase());
        m.setValor(valor);
        m.setReferencia(referencia == null ? java.util.UUID.randomUUID().toString() : referencia);
        BigDecimal nuevoSaldo = "DEBITO".equalsIgnoreCase(tipo) ? current.subtract(valor) : current.add(valor);
        m.setSaldo(nuevoSaldo);
        m.setFecha(LocalDateTime.now());
        movimientoRepository.save(m);
        return m;
    }

    @Override
    public List<Movimiento> listByCuenta(String numeroCuenta, LocalDateTime from, LocalDateTime to) {
        Cuenta cuenta = cuentaRepository.findByNumero(numeroCuenta)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));
        return movimientoRepository.findByCuentaAndFechaBetween(cuenta, from, to);
    }

    @Override
    public BigDecimal saldoActual(String numeroCuenta) {
        Cuenta cuenta = cuentaRepository.findByNumero(numeroCuenta)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));
        BigDecimal inicio = cuenta.getSaldoInicial() == null ? BigDecimal.ZERO : cuenta.getSaldoInicial();

        java.util.List<Movimiento> movimientos = movimientoRepository.findByCuentaAndFechaBetween(cuenta, LocalDateTime.of(1970,1,1,0,0), LocalDateTime.now());
        BigDecimal movimientosSum = movimientos.stream()
                .map(m -> "DEBITO".equalsIgnoreCase(m.getTipo()) ? m.getValor().negate() : m.getValor())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return inicio.add(movimientosSum);
    }
}
