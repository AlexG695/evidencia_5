package com.utch.cafeteria.service;

import com.utch.cafeteria.dto.ProductoRequest;
import com.utch.cafeteria.exception.RecursoNoEncontradoException;
import com.utch.cafeteria.model.Producto;
import com.utch.cafeteria.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Logica de negocio de productos (menu).
 */
@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    /** Menu publico: solo productos disponibles. */
    public List<Producto> listarMenu() {
        return productoRepository.findByDisponibleTrue();
    }

    /** Catalogo completo (incluye no disponibles) para el administrador. */
    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    public Producto obtener(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado: " + id));
    }

    public Producto crear(ProductoRequest req) {
        Producto p = new Producto(req.nombre(), req.precio(), req.disponible());
        return productoRepository.save(p);
    }

    public Producto actualizar(Long id, ProductoRequest req) {
        Producto p = obtener(id);
        p.setNombre(req.nombre());
        p.setPrecio(req.precio());
        p.setDisponible(req.disponible());
        return productoRepository.save(p);
    }
}
