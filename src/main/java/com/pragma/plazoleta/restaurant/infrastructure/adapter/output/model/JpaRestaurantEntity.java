package com.pragma.plazoleta.restaurant.infrastructure.adapter.output.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "restaurantes")
public class JpaRestaurantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "nombre")
    private String name;
    @Column(name = "direccion")
    private String address;
    @Column(name = "telefono")
    private String phone;
    @Column(name = "url_logo")
    private String logoUrl;
    @Column(name = "nit")
    private String nit;

    @Column(name = "id_propietario")
    private Long ownerId;
}
