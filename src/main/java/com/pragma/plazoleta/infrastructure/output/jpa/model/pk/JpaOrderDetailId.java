package com.pragma.plazoleta.infrastructure.output.jpa.model.pk;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JpaOrderDetailId implements Serializable {

    @Column(name = "id_pedido")
    private Long orderId;

    @Column(name = "id_plato")
    private Long dishId;

}
