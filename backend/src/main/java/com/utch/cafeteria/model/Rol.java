package com.utch.cafeteria.model;

/**
 * Roles del sistema, derivados de los actores del Caso 4:
 *  - CLIENTE:  consulta menu, genera pedido y consulta estado.
 *  - PERSONAL: recibe pedidos y actualiza preparacion/entrega.
 *  - ADMIN:    gestiona productos (administrador).
 * Spring Security usa el prefijo "ROLE_" internamente; aqui guardamos el nombre base.
 */
public enum Rol {
    CLIENTE,
    PERSONAL,
    ADMIN
}
