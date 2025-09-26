package com.proyecto.app.service;

import com.proyecto.app.model.Cuenta;
import com.proyecto.app.model.EstadoCuentaReporte;
import com.proyecto.app.model.Movimiento;
import com.proyecto.app.repository.CuentaRepository;
import com.proyecto.app.repository.MovimientoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class MovimientoServiceImpl implements MovimientoService {

    private final MovimientoRepository movimientoRepository;
    private final CuentaRepository cuentaRepository;
    private final BigDecimal LIMITE_DIARIO = BigDecimal.valueOf(1000);

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

    @Override
    public List<Movimiento> listWithSaldo(String numeroCuenta, LocalDateTime from, LocalDateTime to) {
        Cuenta cuenta = cuentaRepository.findByNumero(numeroCuenta)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

        BigDecimal saldo = cuenta.getSaldoInicial() == null ? BigDecimal.ZERO : cuenta.getSaldoInicial();

        List<Movimiento> movimientos = movimientoRepository.findByCuentaAndFechaBetween(cuenta, from, to);

        for (Movimiento m : movimientos) {
            if ("DEBITO".equalsIgnoreCase(m.getTipo())) {
                saldo = saldo.subtract(m.getValor());
            } else {
                saldo = saldo.add(m.getValor());
            }
            m.setSaldo(saldo); // actualizar saldo después del movimiento
        }

        return movimientos;
    }

    @Override
    public List<Movimiento> listAllWithSaldo() {
        List<Cuenta> cuentas = cuentaRepository.findAll();
        List<Movimiento> result = new ArrayList<>();

        for (Cuenta cuenta : cuentas) {
            BigDecimal saldo = cuenta.getSaldoInicial() != null ? cuenta.getSaldoInicial() : BigDecimal.ZERO;

            List<Movimiento> movimientos = movimientoRepository.findByCuentaAndFechaBetween(
                    cuenta,
                    LocalDateTime.of(1970, 1, 1, 0, 0),
                    LocalDateTime.now()
            );

            movimientos.sort((m1, m2) -> {
                if (m1.getFecha() == null && m2.getFecha() == null) return 0;
                if (m1.getFecha() == null) return -1;
                if (m2.getFecha() == null) return 1;
                return m1.getFecha().compareTo(m2.getFecha());
            });

            for (Movimiento m : movimientos) {
                if ("DEBITO".equalsIgnoreCase(m.getTipo())) {
                    saldo = saldo.subtract(m.getValor());
                } else {
                    saldo = saldo.add(m.getValor());
                }
                m.setSaldo(saldo);
                result.add(m);
            }
        }

        result.sort((m1, m2) -> {
            if (m1.getFecha() == null && m2.getFecha() == null) return 0;
            if (m1.getFecha() == null) return -1;
            if (m2.getFecha() == null) return 1;
            return m1.getFecha().compareTo(m2.getFecha());
        });

        return result;
    }


    @Override
    public EstadoCuentaReporte generarReporte(String numeroCuenta, LocalDateTime from, LocalDateTime to) {
        Cuenta cuenta = cuentaRepository.findByNumero(numeroCuenta)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

        List<Movimiento> movimientos = listWithSaldo(numeroCuenta, from, to);

        BigDecimal totalCreditos = movimientos.stream()
                .filter(m -> "CREDITO".equalsIgnoreCase(m.getTipo()))
                .map(Movimiento::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDebitos = movimientos.stream()
                .filter(m -> "DEBITO".equalsIgnoreCase(m.getTipo()))
                .map(Movimiento::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new EstadoCuentaReporte(
                cuenta.getNumero(),
                cuenta.getTipo(),
                cuenta.getCliente().getNombre(),
                from,
                to,
                movimientos,
                totalCreditos,
                totalDebitos,
                saldoActual(numeroCuenta)
        );
    }

    @Override
    public String exportarReportePDFBase64(EstadoCuentaReporte reporte) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // Aquí usas iText o PDFBox para armar el PDF con reporte.getMovimientos() etc.
        byte[] pdfBytes = out.toByteArray();
        return Base64.getEncoder().encodeToString(pdfBytes);
    }

    @Override
    public List<Movimiento> listarMovimientosConSaldo(String numeroCuenta, LocalDateTime desde, LocalDateTime hasta) {
        Cuenta cuenta = cuentaRepository.findByNumero(numeroCuenta)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

        // Obtener movimientos en el rango
        List<Movimiento> movimientos = movimientoRepository.findByCuentaAndFechaBetween(cuenta, desde, hasta);

        BigDecimal saldoActual = cuenta.getSaldoInicial() == null ? BigDecimal.ZERO : cuenta.getSaldoInicial();
        BigDecimal limiteDiario = new BigDecimal("1000"); // tope diario
        BigDecimal totalDebitosHoy = BigDecimal.ZERO;

        LocalDateTime fechaActual = null;

        for (Movimiento m : movimientos) {
            // Reiniciar el total diario si el día cambia
            if (fechaActual == null || !m.getFecha().toLocalDate().equals(fechaActual.toLocalDate())) {
                totalDebitosHoy = BigDecimal.ZERO;
                fechaActual = m.getFecha();
            }

            if ("DEBITO".equalsIgnoreCase(m.getTipo())) {
                // Verificar saldo disponible
                if (saldoActual.compareTo(BigDecimal.ZERO) <= 0) {
                    m.setReferencia("Saldo no disponible");
                    m.setSaldo(saldoActual);
                    continue;
                }
                // Verificar límite diario
                if (totalDebitosHoy.add(m.getValor()).compareTo(limiteDiario) > 0) {
                    m.setReferencia("Cupo diario excedido");
                    m.setSaldo(saldoActual);
                    continue;
                }

                saldoActual = saldoActual.subtract(m.getValor());
                totalDebitosHoy = totalDebitosHoy.add(m.getValor());
            } else { // CREDITO
                saldoActual = saldoActual.add(m.getValor());
            }

            m.setSaldo(saldoActual);
        }

        return movimientos;
    }

}
