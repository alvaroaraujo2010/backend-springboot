package com.proyecto.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "cuenta")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cuenta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El número de cuenta es obligatorio")
    @Size(max = 50, message = "El número no debe superar los 50 caracteres")
    @Column(unique = true, nullable = false, length = 50)
    private String numero;

    @NotBlank(message = "El tipo de cuenta es obligatorio")
    @Size(max = 30, message = "El tipo no debe superar los 30 caracteres")
    @Column(nullable = false, length = 30)
    private String tipo;

    @NotNull(message = "El saldo inicial es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El saldo inicial debe ser mayor o igual a 0")
    @Column(name = "saldo_inicial", nullable = false, precision = 15, scale = 2)
    private BigDecimal saldoInicial = BigDecimal.ZERO;

    @NotNull(message = "El estado no puede ser nulo")
    @Column(nullable = false)
    private Boolean estado = true;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;
}
