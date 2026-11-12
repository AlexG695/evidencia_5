package com.utch.cafeteria.model;

/**
 * Maquina de estados del pedido (regla del Caso 4).
 * Transicion valida y ordenada: RECIBIDO -> EN_PREPARACION -> LISTO -> ENTREGADO.
 */
public enum EstadoPedido {
    RECIBIDO,
    EN_PREPARACION,
    LISTO,
    ENTREGADO;

    /**
     * Devuelve el siguiente estado permitido, o null si ya es estado final (ENTREGADO).
     */
    public EstadoPedido siguiente() {
        return switch (this) {
            case RECIBIDO -> EN_PREPARACION;
            case EN_PREPARACION -> LISTO;
            case LISTO -> ENTREGADO;
            case ENTREGADO -> null;
        };
    }

    /**
     * Valida si el paso de este estado a 'destino' respeta el orden de la maquina de estados.
     */
    public boolean puedeTransicionarA(EstadoPedido destino) {
        return destino != null && destino == this.siguiente();
    }
}
