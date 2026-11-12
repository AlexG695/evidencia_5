package com.utch.cafeteria.dto;

import com.utch.cafeteria.model.DetallePedido;

import java.math.BigDecimal;

/**
 * Representacion de salida de una linea de pedido.
 */
public record DetalleResponse(
        Long productoId,
        String nombreProducto,
        BigDecimal precioUnitario,
        int cantidad,
        BigDecimal subtotal
) {
    public static DetalleResponse desde(DetallePedido d) {
        return new DetalleResponse(
                d.getProducto().getId(),
                d.getProducto().getNombre(),
                d.getProducto().getPrecio(),
                d.getCantidad(),
                d.getSubtotal()
        );
    }
}
