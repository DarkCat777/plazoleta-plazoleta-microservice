package com.pragma.plazoleta.dish.infrastructure.adapter.output.model;

import com.pragma.plazoleta.restaurant.infrastructure.adapter.output.model.JpaRestaurantEntity;
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

    @ManyToOne
    @JoinColumn(name = "id_categoria", nullable = false)
    private JpaCategoryEntity category;

    @Column(name = "descripcion", nullable = false)
    private String description;

    @Column(name = "precio", nullable = false)
    private int price;

    @ManyToOne
    @JoinColumn(name = "id_restaurante", nullable = false)
    private JpaRestaurantEntity restaurant;

    @Column(name = "url_imagen", nullable = false)
    private String imageUrl;

    @Column(name = "activo", nullable = false)
    private boolean active;
}
