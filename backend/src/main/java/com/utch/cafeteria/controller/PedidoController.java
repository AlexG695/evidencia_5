package com.utch.cafeteria.controller;

import com.utch.cafeteria.dto.CambiarEstadoRequest;
import com.utch.cafeteria.dto.CrearPedidoRequest;
import com.utch.cafeteria.dto.PedidoResponse;
import com.utch.cafeteria.model.Pedido;
import com.utch.cafeteria.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints del flujo de pedidos (nucleo del Caso 4).
 *  POST  /api/pedidos               -> registrar pedido y calcular total (CLIENTE)
 *  GET   /api/pedidos               -> listar pedidos (rol autenticado)
 *  GET   /api/pedidos/{id}          -> consultar un pedido (rol autenticado)
 *  PATCH /api/pedidos/{id}/estado   -> avanzar estado del pedido (PERSONAL)
 */
@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    public ResponseEntity<PedidoResponse> crear(@Valid @RequestBody CrearPedidoRequest req) {
        Pedido pedido = pedidoService.crearPedido(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(PedidoResponse.desde(pedido));
    }

    @GetMapping
    public List<PedidoResponse> listar() {
        return pedidoService.listar().stream().map(PedidoResponse::desde).toList();
    }

    @GetMapping("/{id}")
    public PedidoResponse consultar(@PathVariable Long id) {
        return PedidoResponse.desde(pedidoService.obtener(id));
    }

    @PatchMapping("/{id}/estado")
    public PedidoResponse cambiarEstado(@PathVariable Long id,
                                        @Valid @RequestBody CambiarEstadoRequest req) {
        return PedidoResponse.desde(pedidoService.cambiarEstado(id, req.estado()));
    }
}
