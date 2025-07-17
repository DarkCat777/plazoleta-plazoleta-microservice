package com.pragma.plazoleta.infrastructure.output.jpa.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JpaOrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_cliente", nullable = false)
    private Long customerId;

    @Column(name = "id_chef")
    private Long chefId;

    @Column(name = "estado", nullable = false)
    private String status;

    @Column(name = "codigo_seguridad")
    private String securityPin;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "id_restaurante", nullable = false)
    private JpaRestaurantEntity restaurant;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JpaOrderDetailEntity> dishes = new ArrayList<>();

}
