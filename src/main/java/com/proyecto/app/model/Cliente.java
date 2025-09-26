package com.proyecto.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "cliente")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(max = 100, message = "El nombre no debe superar los 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nombre;

    @Email(message = "El correo debe ser válido")
    @Size(max = 150, message = "El correo no debe superar los 150 caracteres")
    @Column(nullable = false, length = 150, unique = true)
    private String email;

    @NotBlank(message = "El teléfono es obligatorio")
    @Size(min = 7, max = 20, message = "El teléfono debe tener entre 7 y 20 caracteres")
    @Column(nullable = false, length = 20)
    private String telefono;

    @NotBlank(message = "La identificación es obligatoria")
    @Size(max = 50, message = "La identificación no debe superar los 50 caracteres")
    @Column(nullable = false, unique = true, length = 50)
    private String identificacion;
}
