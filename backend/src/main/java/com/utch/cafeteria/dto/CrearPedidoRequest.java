package com.utch.cafeteria.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * Peticion para registrar un pedido.
 * Regla del caso: debe tener al menos un producto.
 */
public record CrearPedidoRequest(
        @NotBlank(message = "El cliente es obligatorio")
        String cliente,

        @NotEmpty(message = "El pedido debe tener al menos un producto")
        @Valid
        List<ItemPedidoRequest> items
) {
}
