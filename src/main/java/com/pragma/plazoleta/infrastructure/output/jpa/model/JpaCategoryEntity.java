package com.pragma.plazoleta.infrastructure.output.jpa.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "categorias")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JpaCategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false)
    private String name;

    @Column(name = "descripcion", nullable = false)
    private String description;
}