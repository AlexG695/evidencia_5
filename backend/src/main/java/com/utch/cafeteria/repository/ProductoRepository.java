package com.utch.cafeteria.repository;

import com.utch.cafeteria.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Capa de persistencia de Producto.
 */
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    /** Menu: solo productos marcados como disponibles. */
    List<Producto> findByDisponibleTrue();
}
