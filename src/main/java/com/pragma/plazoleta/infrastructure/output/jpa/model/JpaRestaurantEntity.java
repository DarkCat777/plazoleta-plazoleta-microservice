package com.pragma.plazoleta.infrastructure.output.jpa.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "restaurantes")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
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
