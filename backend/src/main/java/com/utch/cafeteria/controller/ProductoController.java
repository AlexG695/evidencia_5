package com.utch.cafeteria.controller;

import com.utch.cafeteria.dto.ProductoRequest;
import com.utch.cafeteria.model.Producto;
import com.utch.cafeteria.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints del catalogo de productos (menu).
 *  GET  /api/productos           -> consultar menu (cualquier rol autenticado)
 *  GET  /api/productos/catalogo  -> catalogo completo (ADMIN)
 *  POST /api/productos           -> crear producto (ADMIN)
 *  PUT  /api/productos/{id}      -> actualizar producto / disponibilidad (ADMIN)
 */
@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public List<Producto> menu() {
        return productoService.listarMenu();
    }

    @GetMapping("/catalogo")
    public List<Producto> catalogo() {
        return productoService.listarTodos();
    }

    @PostMapping
    public ResponseEntity<Producto> crear(@Valid @RequestBody ProductoRequest req) {
        Producto creado = productoService.crear(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    public Producto actualizar(@PathVariable Long id, @Valid @RequestBody ProductoRequest req) {
        return productoService.actualizar(id, req);
    }
}
