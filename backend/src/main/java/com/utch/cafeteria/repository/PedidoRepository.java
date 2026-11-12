package com.utch.cafeteria.repository;

import com.utch.cafeteria.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Capa de persistencia de Pedido (conserva el historial de pedidos).
 */
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
}
