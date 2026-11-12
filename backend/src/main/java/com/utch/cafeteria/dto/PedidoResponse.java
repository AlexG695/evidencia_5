package com.utch.cafeteria.dto;

import com.utch.cafeteria.model.Pedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Representacion de salida de un pedido (incluye total calculado y estado).
 */
public record PedidoResponse(
        Long id,
        String cliente,
        LocalDateTime fechaHora,
        String estado,
        BigDecimal total,
        List<DetalleResponse> detalles
) {
    public static PedidoResponse desde(Pedido p) {
        List<DetalleResponse> det = p.getDetalles().stream()
                .map(DetalleResponse::desde)
                .toList();
        return new PedidoResponse(
                p.getId(),
                p.getCliente(),
                p.getFechaHora(),
                p.getEstado().name(),
                p.getTotal(),
                det
        );
    }
}
