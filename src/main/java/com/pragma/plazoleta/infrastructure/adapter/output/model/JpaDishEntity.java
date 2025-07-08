package com.pragma.plazoleta.infrastructure.adapter.output.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "platos")
@Getter
@Setter
public class JpaDishEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria", nullable = false)
    private JpaCategoryEntity category;

    @Column(name = "descripcion", nullable = false)
    private String description;

    @Column(name = "precio", nullable = false)
    private int price;

    @Column(name = "id_restaurante", nullable = false)
    private Long restaurantId;

    @Column(name = "url_imagen", nullable = false)
    private String imageUrl;

    @Column(name = "activo", nullable = false)
    private boolean active;
}
