package com.utch.cafeteria.dto;

import com.utch.cafeteria.model.EstadoPedido;
import jakarta.validation.constraints.NotNull;

/**
 * Peticion para avanzar el estado de un pedido (usada por el PERSONAL).
 */
public record CambiarEstadoRequest(
        @NotNull(message = "El estado destino es obligatorio")
        EstadoPedido estado
) {
}
