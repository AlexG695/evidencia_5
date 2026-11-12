package com.utch.cafeteria.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Linea solicitada por el cliente: id de producto + cantidad.
 * La cantidad debe ser valida (>= 1); el precio y subtotal los pone el servidor.
 */
public record ItemPedidoRequest(
        @NotNull(message = "productoId es obligatorio")
        Long productoId,

        @Min(value = 1, message = "La cantidad debe ser al menos 1")
        int cantidad
) {
}
