package com.pragma.plazoleta.infrastructure.output.jpa.model;

import com.pragma.plazoleta.infrastructure.output.jpa.model.pk.JpaOrderDetailId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pedidos_platos")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JpaOrderDetailEntity {

    @EmbeddedId
    private JpaOrderDetailId id;

    @ManyToOne(optional = false)
    @MapsId("orderId")
    @JoinColumn(name = "id_pedido", nullable = false)
    private JpaOrderEntity order;

    @ManyToOne(optional = false)
    @MapsId("dishId")
    @JoinColumn(name = "id_plato", nullable = false)
    private JpaDishEntity dish;

    @Column(name = "cantidad", nullable = false)
    private int quantity;

}
